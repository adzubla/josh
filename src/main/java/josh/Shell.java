package josh;

/**
 * Controla os componentes configurados
 * - inicializa comandos
 * - obtem linha do console
 * - substitui variaveis (environment variables, system properties,
 * local properties)
 * - invoca comando
 * - chama shutdownHook
 */
class Shell {

    private ConsoleProvider consoleProvider;

    public ConsoleProvider getConsoleProvider() {
        return consoleProvider;
    }

    public void setConsoleProvider(ConsoleProvider consoleProvider) {
        this.consoleProvider = consoleProvider;
    }

    CommandOutcome run() {
        return null;
    }

    CommandOutcome execute(String line) {
        return null;
    }

    CommandOutcome execute(String[] args) {
        return null;
    }
}
