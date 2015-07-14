package josh.api;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Controla os componentes configurados
 * - inicializa comandos
 * - obtem linha do console
 * - substitui variaveis (environment variables, system properties,
 * local properties)
 * - invoca comando
 * - chama shutdownHook
 * <p/>
 * Exit codes
 * http://www.tldp.org/LDP/abs/html/exitcodes.html
 */
public class Shell {

    protected boolean quiet;
    protected boolean displayStackTraceOnError;

    protected CommandProvider commandProvider;
    protected CommandParser commandParser;
    protected ConsoleProvider consoleProvider;

    public CommandOutcome run() {
        consoleProvider.initialize();
        displayWelcome();

        CommandOutcome commandOutcome = repl();

        displayGoodbye();
        consoleProvider.destroy();

        return commandOutcome;
    }

    protected void displayWelcome() {
        String banner = "   _           _     \n" +
                "  (_) ___  ___| |__  \n" +
                "  | |/ _ \\/ __| '_ \\ \n" +
                "  | | (_) \\__ \\ | | |\n" +
                " _/ |\\___/|___/_| |_|\n" +
                "|__/                 \n";
        consoleProvider.displayInfo(banner);
        consoleProvider.displayInfo("Press Ctrl-D to exit shell.");
        consoleProvider.displayInfo("");
    }

    protected void displayGoodbye() {
        consoleProvider.displayInfo("Goodbye!");
    }

    protected CommandOutcome repl() {

        CommandOutcome outcome = new CommandOutcome();
        while (true) {
            consoleProvider.displayPrompt();
            String line = consoleProvider.readLine();
            if (line == null) {
                consoleProvider.displayInfo("");
                break;
            }
            if (!line.trim().equals("")) {
                try {
                    outcome = commandProvider.execute(commandParser.parseLine(line));
                    if (outcome.getExitCode() != 0) {
                        consoleProvider.displayError("Error executing command. Exit code " + outcome.getExitCode());
                    }
                }
                catch (CommandNotFound e) {
                    consoleProvider.displayWarning("Command " + e.getName() + " not found.");
                    outcome.setExitCode(127);
                }
                catch (RuntimeException e) {
                    if (displayStackTraceOnError) {
                        //e.printStackTrace();
                        StringWriter sw = new StringWriter();
                        e.printStackTrace(new PrintWriter(sw));
                        consoleProvider.displayError(sw.toString());
                    }
                    consoleProvider.displayError("Error: " + e.getMessage());
                    outcome.setExitCode(126);
                }
            }
        }

        return outcome;
    }

    public boolean isDisplayStackTraceOnError() {
        return displayStackTraceOnError;
    }

    public void setDisplayStackTraceOnError(boolean displayStackTraceOnError) {
        this.displayStackTraceOnError = displayStackTraceOnError;
    }

    public CommandProvider getCommandProvider() {
        return commandProvider;
    }

    public void setCommandProvider(CommandProvider commandProvider) {
        this.commandProvider = commandProvider;
    }

    public CommandParser getCommandParser() {
        return commandParser;
    }

    public void setCommandParser(CommandParser commandParser) {
        this.commandParser = commandParser;
    }

    public ConsoleProvider getConsoleProvider() {
        return consoleProvider;
    }

    public void setConsoleProvider(ConsoleProvider consoleProvider) {
        this.consoleProvider = consoleProvider;
    }

}
