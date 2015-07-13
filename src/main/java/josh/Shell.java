package josh;

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

    private boolean quiet = false;

    private ConsoleProvider consoleProvider;
    private CommandProvider commandProvider;

    public ConsoleProvider getConsoleProvider() {
        return consoleProvider;
    }

    public void setConsoleProvider(ConsoleProvider consoleProvider) {
        this.consoleProvider = consoleProvider;
    }

    public CommandOutcome run() {
        displayBanner();
        return repl();
    }

    protected void displayBanner() {
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
                    outcome = execute(line);
                    if (outcome.getExitCode() != 0) {
                        consoleProvider.displayError("Error executing command. Exit code " + outcome.getExitCode());
                    }
                } catch (CommandNotFound e) {
                    consoleProvider.displayError("Command " + e.getName() + " not found.");
                    outcome.setExitCode(127);
                } catch (RuntimeException e) {
                    consoleProvider.displayError("Error: " + e.getMessage());
                    outcome.setExitCode(126);
                }
            }
        }

        return outcome;
    }

    CommandOutcome execute(String line) throws CommandNotFound {
        CommandOutcome outcome = commandProvider.execute(line);
        return outcome;
    }

    public CommandProvider getCommandProvider() {
        return commandProvider;
    }

    public void setCommandProvider(CommandProvider commandProvider) {
        this.commandProvider = commandProvider;
    }

}
