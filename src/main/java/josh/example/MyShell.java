package josh.example;

import java.util.Arrays;

import org.fusesource.jansi.Ansi;

import josh.api.CommandNotFound;
import josh.api.ConsoleProvider;
import josh.api.Shell;
import josh.impl.BasicConsoleProvider;
import josh.impl.CommandParserImpl;
import josh.impl.CustomCompleter;
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
public class MyShell {

    static boolean useJline = true;

    public static void main(String[] args) {

        CommandProviderImpl commandProvider = new CommandProviderImpl();

        int exitCode;
        if (args.length == 0) {
            exitCode = runInteractiveShell(commandProvider);
        }
        else {
            exitCode = runSingleCommand(commandProvider, args);
        }
        System.exit(exitCode);
    }

    private static int runSingleCommand(CommandProviderImpl commandProvider, String[] args) {
        try {
            return commandProvider.execute(Arrays.asList(args)).getExitCode();
        }
        catch (CommandNotFound commandNotFound) {
            return 127;
        }
    }

    private static int runInteractiveShell(CommandProviderImpl commandProvider) {
        Shell shell = new Shell();
        shell.setDisplayStackTraceOnError(true);
        shell.setExitOnError(false);
        shell.setCommandParser(new CommandParserImpl());
        shell.setInitializer(new MyShellInitializer());
        shell.setFinalizer(new MyShellFinalizer());

        ConsoleProvider provider;
        if (useJline) {
            JLineProvider jline = new JLineProvider();
            jline.setHistory(System.getProperty("user.home") + "/.josh/", "josh_history", 800);
            jline.setPromptColor(Ansi.Color.CYAN);
            jline.setInfoColor(Ansi.Color.GREEN);
            jline.setWarnColor(Ansi.Color.YELLOW);
            jline.setErrorColor(Ansi.Color.RED);
            jline.getConsole().addCompleter(new CustomCompleter(shell.getCommandParser(), commandProvider.getCommands()));
            provider = jline;
            commandProvider.setjLineProvider(jline);
        }
        else {
            provider = new BasicConsoleProvider();
        }
        provider.setPrompt("> ");
        shell.setConsoleProvider(provider);

        shell.setCommandProvider(commandProvider);

        return shell.run().getExitCode();
    }

}
