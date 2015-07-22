package josh.example;

import java.util.Arrays;

import org.fusesource.jansi.Ansi;

import josh.command.BuiltInCommandProvider;
import josh.command.CommandNotFound;
import josh.command.CommandProvider;
import josh.command.CompoundCommandProvider;
import josh.shell.BasicConsoleProvider;
import josh.shell.ConsoleProvider;
import josh.shell.LineParserImpl;
import josh.shell.Shell;
import josh.shell.jline.CustomCompleter;
import josh.shell.jline.JLineProvider;

/**
 * Integra todos os componentes com implementação default - le opções da linha de comando - configura shell - invoca
 * shell
 * <p/>
 * josh -c 'cmd arg1 arg2' josh -f cmdlist.txt josh -v key=value josh
 */
public class MyShell {

    static boolean useJline = true;

    public static void main(String[] args) {

        CommandProvider commandProvider =
                new CompoundCommandProvider(new BuiltInCommandProvider(), new ExampleCommandProvider());

        int exitCode;
        if (args.length == 0) {
            exitCode = runInteractiveShell(commandProvider);
        }
        else {
            exitCode = runSingleCommand(commandProvider, args);
        }
        System.exit(exitCode);
    }

    private static int runSingleCommand(CommandProvider commandProvider, String[] args) {
        try {
            return commandProvider.execute(Arrays.asList(args)).getExitCode();
        }
        catch (CommandNotFound commandNotFound) {
            return 127;
        }
    }

    private static int runInteractiveShell(CommandProvider commandProvider) {
        Shell shell = new Shell();
        shell.setDisplayStackTraceOnError(true);
        shell.setExitOnError(false);
        shell.setLineParser(new LineParserImpl());
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
            jline.getConsole()
                    .addCompleter(new CustomCompleter(shell.getLineParser(), commandProvider.getCommands()));
            provider = jline;
            if (commandProvider instanceof ExampleCommandProvider) {
                ((ExampleCommandProvider)commandProvider).setjLineProvider(jline);
            }
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
