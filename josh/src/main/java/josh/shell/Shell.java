package josh.shell;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import josh.command.CommandNotFound;
import josh.command.CommandOutcome;
import josh.command.CommandProvider;

/**
 * The Shell controls the interaction between ConsoleProvider and CommandProvider, and the life cycle of the
 * evaluation loop.
 * <p/>
 * Exit codes are compatible with http://www.tldp.org/LDP/abs/html/exitcodes.html
 */
public class Shell {
    private static final Logger LOG = LoggerFactory.getLogger(Shell.class);

    public static final int EXIT_CODE_OK = 0; // No error
    public static final int EXIT_CODE_GENERAL_ERROR = 1; // Catchall for general errors
    public static final int EXIT_CODE_SHELL_ERROR = 2; // Misuse of shell builtins
    public static final int EXIT_CODE_CAN_NOT_EXECUTE = 126; // Command invoked cannot execute
    public static final int EXIT_CODE_COMMAND_NOT_FOUND = 127; // Command not found
    public static final int EXIT_CODE_TERMINATED = 130; // Script terminated by Control-C

    protected boolean displayStackTraceOnError;
    protected boolean exitOnError;

    protected CommandProvider commandProvider;
    protected LineParser lineParser;
    protected ConsoleProvider consoleProvider;

    protected ShellInitializer initializer;
    protected ShellFinalizer finalizer;

    protected ShellState state = ShellState.PREPARING;

    protected final Object MUTEX = new Object();

    protected boolean interactive;

    public boolean isInteractive() {
        return interactive;
    }

    public CommandOutcome runInteractive() {
        LOG.info("Starting shell");
        interactive = true;
        initialize();
        CommandOutcome commandOutcome = repl();
        destroy(false);
        return commandOutcome;
    }

    public CommandOutcome executeCommand(List<String> args) {
        LOG.info("Executing command in shell");
        initialize();
        CommandOutcome commandOutcome = runCommand(args);
        destroy(false);
        return commandOutcome;
    }

    protected void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                LOG.info("Ending shell");
                Shell.this.destroy(true);
            }
        });
    }

    protected void initialize() {
        synchronized (MUTEX) {
            if (ShellState.PREPARING.equals(state)) {
                LOG.info("Initializing shell");

                if (lineParser == null) {
                    throw new IllegalStateException("Can not run the shell without a LineParser");
                }
                if (consoleProvider == null) {
                    throw new IllegalStateException("Can not run the shell without a ConsoleProvider");
                }
                if (commandProvider == null) {
                    throw new IllegalStateException("Can not run the shell without a CommandProvider");
                }

                registerShutdownHook();
                consoleProvider.initialize();
                commandProvider.initialize();

                if (initializer != null) {
                    initializer.initialize(this);
                }
                setState(ShellState.STARTED);
            }
        }
    }

    protected void destroy(boolean shutdownHook) {
        synchronized (MUTEX) {
            if (ShellState.STARTED.equals(state) || ShellState.INTERRUPTED.equals(state)) {
                LOG.info("Destroying shell");

                if (shutdownHook) {
                    try {
                        consoleProvider.displayWarning("[Interrupted]");
                    }
                    catch (Exception e) {
                        LOG.warn("Error", e);
                    }
                }

                if (finalizer != null) {
                    finalizer.destroy(this);
                }

                commandProvider.destroy();
                consoleProvider.destroy();
                setState(ShellState.FINALIZED);
            }
        }
    }

    protected CommandOutcome repl() {
        LOG.info("Starting repl");
        CommandOutcome outcome = new CommandOutcome();
        try {
            while (true) {
                consoleProvider.displayPrompt();
                String line = consoleProvider.readLine();
                LOG.debug("line = {}", line);
                if (line == null) {
                    LOG.info("No more input");
                    consoleProvider.displayInfo("");
                    break;
                }
                if (!line.trim().equals("")) {
                    List<String> args = lineParser.getTokens(line);
                    outcome = runCommand(args);
                    if (exitOnError && outcome.isErrorState()) {
                        LOG.info("Exiting on error");
                        break;
                    }
                    if (outcome.isExitRequest()) {
                        LOG.info("Exit requested");
                        break;
                    }
                }
            }
        }
        catch (InterruptedException e) {
            LOG.debug(e.getMessage(), e);
            setState(ShellState.INTERRUPTED);
            outcome.setExitCode(Shell.EXIT_CODE_TERMINATED);
        }
        LOG.info("Ending repl");
        LOG.info("last outcome = {}", outcome);
        return outcome;
    }

    protected CommandOutcome runCommand(List<String> args) {
        CommandOutcome outcome = new CommandOutcome();
        try {
            outcome = commandProvider.execute(args);
            LOG.debug("outcome = {}", outcome);
            if (outcome.getExitCode() != EXIT_CODE_OK) {
                consoleProvider.displayError("Error executing command. Exit code " + outcome.getExitCode());
            }
        }
        catch (CommandNotFound e) {
            LOG.error("Command {} not found.", e.getName());
            consoleProvider.displayWarning("Command " + e.getName() + " not found.");
            outcome.setExitCode(EXIT_CODE_COMMAND_NOT_FOUND);
        }
        catch (RuntimeException e) {
            LOG.error("Error executing command", e);
            if (displayStackTraceOnError) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                consoleProvider.displayError(sw.toString());
            }
            consoleProvider.displayError("Error executing command: " + e.getMessage());
            outcome.setExitCode(EXIT_CODE_CAN_NOT_EXECUTE);
        }
        return outcome;
    }

    public boolean isDisplayStackTraceOnError() {
        return displayStackTraceOnError;
    }

    public void setDisplayStackTraceOnError(boolean displayStackTraceOnError) {
        this.displayStackTraceOnError = displayStackTraceOnError;
    }

    public boolean isExitOnError() {
        return exitOnError;
    }

    public void setExitOnError(boolean exitOnError) {
        this.exitOnError = exitOnError;
    }

    public CommandProvider getCommandProvider() {
        return commandProvider;
    }

    public void setCommandProvider(CommandProvider commandProvider) {
        this.commandProvider = commandProvider;

        if (this.commandProvider instanceof ShellAwareCommandProvider) {
            ((ShellAwareCommandProvider)this.commandProvider).setShell(this);
        }
    }

    public LineParser getLineParser() {
        return lineParser;
    }

    public void setLineParser(LineParser lineParser) {
        this.lineParser = lineParser;
    }

    public ConsoleProvider getConsoleProvider() {
        return consoleProvider;
    }

    public void setConsoleProvider(ConsoleProvider consoleProvider) {
        this.consoleProvider = consoleProvider;
    }

    public ShellInitializer getInitializer() {
        return initializer;
    }

    public void setInitializer(ShellInitializer initializer) {
        this.assertShellNotStarted("Can't set a new initializer");
        this.assertShellNotFinalized("Can't set a new initializer");
        this.initializer = initializer;
    }

    public ShellFinalizer getFinalizer() {
        return finalizer;
    }

    public void setFinalizer(ShellFinalizer finalizer) {
        this.assertShellNotStarted("Can't set a new finalizer");
        this.assertShellNotFinalized("Can't set a new finalizer");
        this.finalizer = finalizer;
    }

    protected void assertShellNotStarted(String message) {
        if (ShellState.STARTED.equals(state)) {
            throw new IllegalStateException(message + "\nShell is already started");
        }
    }

    protected void assertShellNotFinalized(String message) {
        if (ShellState.FINALIZED.equals(state)) {
            throw new IllegalStateException(message + "\nShell is already finalized");
        }
    }

    public void setState(ShellState state) {
        synchronized (MUTEX) {
            this.state = state;
        }
    }

    protected enum ShellState {
        PREPARING,
        STARTED,
        INTERRUPTED,
        FINALIZED
    }

}
