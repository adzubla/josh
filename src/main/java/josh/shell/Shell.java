package josh.shell;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import josh.command.CommandNotFound;
import josh.command.CommandOutcome;
import josh.command.CommandProvider;
import josh.utils.Assert;

/**
 * Controla os componentes configurados - inicializa comandos - obtem linha do console - substitui variaveis
 * (environment variables, system properties, local properties) - invoca comando - chama shutdownHook
 * <p/>
 * Exit codes http://www.tldp.org/LDP/abs/html/exitcodes.html
 */
public class Shell {
    private static final Logger LOG = LoggerFactory.getLogger(Shell.class);

    protected boolean displayStackTraceOnError;
    protected boolean exitOnError;

    protected CommandProvider commandProvider;
    protected LineParser lineParser;
    protected ConsoleProvider consoleProvider;

    protected ShellInitializer initializer;
    protected ShellFinalizer finalizer;

    protected ShellState state = ShellState.PREPARING;

    private final Object MUTEX = new Object();

    public CommandOutcome run() {
        LOG.info("Starting shell");
        Assert.notNull(lineParser, "Could not run the shell without a lineParser");
        initialize();
        CommandOutcome commandOutcome = repl();
        destroy();
        return commandOutcome;
    }

    public CommandOutcome executeCommand(List<String> args) {
        LOG.info("Executing command in shell");
        initialize();
        CommandOutcome commandOutcome = runCommand(args);
        destroy();
        return commandOutcome;
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                LOG.info("Ending shell");
                Shell.this.destroy();
            }
        });
    }

    protected void initialize() {
        synchronized (MUTEX) {
            if (ShellState.PREPARING.equals(state)) {
                LOG.info("Initializing shell");
                Assert.notNull(consoleProvider, "ConsoleProvider should never be null");
                Assert.notNull(commandProvider, "CommandProvider should never be null");

                registerShutdownHook();
                consoleProvider.initialize();
                commandProvider.initialize();

                if (initializer != null) {
                    initializer.initialize(this);
                }
                state = ShellState.STARTED;
            }
        }
    }

    protected void destroy() {
        synchronized (MUTEX) {
            if (ShellState.STARTED.equals(state)) {
                LOG.info("Destroying shell");
                if (finalizer != null) {
                    finalizer.destroy(this);
                }

                commandProvider.destroy();
                consoleProvider.destroy();
                state = ShellState.FINALIZED;
            }
        }
    }

    protected CommandOutcome repl() {
        LOG.info("Starting repl");
        CommandOutcome outcome = new CommandOutcome();
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
        LOG.info("Ending repl");
        LOG.info("last outcome = {}", outcome);
        return outcome;
    }

    private CommandOutcome runCommand(List<String> args) {
        CommandOutcome outcome = new CommandOutcome();
        try {
            outcome = commandProvider.execute(args);
            LOG.debug("outcome = {}", outcome);
            if (outcome.getExitCode() != 0) {
                consoleProvider.displayError("Error executing command. Exit code " + outcome.getExitCode());
            }
        }
        catch (CommandNotFound e) {
            LOG.error("Command {} not found.", e.getName());
            consoleProvider.displayWarning("Command " + e.getName() + " not found.");
            outcome.setExitCode(127);
        }
        catch (RuntimeException e) {
            LOG.error("Error executing command", e);
            if (displayStackTraceOnError) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                consoleProvider.displayError(sw.toString());
            }
            consoleProvider.displayError("Error executing command: " + e.getMessage());
            outcome.setExitCode(126);
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

        if (this.commandProvider instanceof ShellAware) {
            ((ShellAware)this.commandProvider).setShell(this);
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

    protected enum ShellState {
        PREPARING,
        STARTED,
        FINALIZED
    }

}
