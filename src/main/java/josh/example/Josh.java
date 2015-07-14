package josh.example;

import josh.api.CommandOutcome;
import josh.api.ConsoleProvider;
import josh.api.Shell;
import josh.impl.BasicConsoleProvider;
import josh.impl.CommandParserImpl;
import josh.impl.JLineProvider;

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
        boolean interactive = true;

        Shell shell = new Shell();

        shell.setDisplayStackTraceOnError(true);

        CommandProviderImpl commandProvider = new CommandProviderImpl();
        commandProvider.setCommandParser(new CommandParserImpl());
        shell.setCommandProvider(commandProvider);

        ConsoleProvider provider;
        if (interactive) {
            JLineProvider jline = new JLineProvider();
            jline.setHistory(System.getProperty("user.home") + "/.josh/", "josh_history");
            provider = jline;
        }
        else {
            provider = new BasicConsoleProvider();
        }

        provider.setPrompt("> ");
        shell.setConsoleProvider(provider);

        CommandOutcome co = shell.run();

        System.exit(co.getExitCode());
    }

}
