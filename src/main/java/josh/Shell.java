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
    private CommandExecutor commandExecutor;
    private CommandParser commandParser;

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
        System.out.println(banner);
        System.out.println("Press Ctrl-D to exit shell.");
        System.out.println();
    }

    protected CommandOutcome repl() {

        CommandOutcome outcome = new CommandOutcome();
        while (true) {
            consoleProvider.displayPrompt();
            String line = consoleProvider.readLine();
            if (line == null) {
                System.out.println();
                break;
            }
            if (!line.trim().equals("")) {
                try {
                    outcome = execute(line);
                    if (outcome.getExitCode() != 0) {
                        System.out.println("Error executing command.");
                    }
                } catch (CommandNotFound e) {
                    System.out.println("Command " + e.getName() + " not found.");
                    outcome.setExitCode(127);
                } catch (RuntimeException e) {
                    System.out.println("Error executing command: " + e.getMessage());
                    outcome.setExitCode(126);
                }
            }
        }

        return outcome;
    }

    CommandOutcome execute(String line) throws CommandNotFound {
        CommandOutcome outcome = commandExecutor.execute(line);
        return outcome;
    }

    public CommandProvider getCommandProvider() {
        return commandProvider;
    }

    public void setCommandProvider(CommandProvider commandProvider) {
        this.commandProvider = commandProvider;
    }

    public CommandExecutor getCommandExecutor() {
        return commandExecutor;
    }

    public void setCommandExecutor(CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }

    public CommandParser getCommandParser() {
        return commandParser;
    }

    public void setCommandParser(CommandParser commandParser) {
        this.commandParser = commandParser;
    }
}
