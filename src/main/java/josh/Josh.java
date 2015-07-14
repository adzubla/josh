package josh;

/**
 * Integra todos os componentes com implementação default
 * - le opções da linha de comando
 * - configura shell
 * - invoca shell
 * <p/>
 * josh -c 'cmd arg1 arg2'
 * josh -f cmdlist.txt
 * josh -v key=value
 * josh
 */
public class Josh {

    public static void main(String[] args) {
        Shell shell = new Shell();

        CommandProviderImpl commandProvider = new CommandProviderImpl();
        commandProvider.setCommandParser(new CommandParserImpl());

        shell.setCommandProvider(commandProvider);

        shell.setConsoleProvider(new BasicConsoleProvider());

        CommandOutcome co = shell.run();

        System.exit(co.getExitCode());
    }

}
