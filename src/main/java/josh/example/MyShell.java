package josh.example;

import java.util.Arrays;

import org.fusesource.jansi.Ansi;

import josh.command.builtin.BuiltInCommandProvider;
import josh.command.CommandNotFound;
import josh.command.CommandProvider;
import josh.command.CompoundCommandProvider;
import josh.shell.LineParserImpl;
import josh.shell.Shell;
import josh.shell.jline.CommandCompleter;
import josh.shell.jline.JLineProvider;

/**
 * Integra todos os componentes com implementação default - le opções da linha de comando - configura shell - invoca
 * shell
 * <p/>
 * josh -c 'cmd arg1 arg2' josh -f cmdlist.txt josh -v key=value josh
 */
public class MyShell {

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

        JLineProvider provider = new JLineProvider();
        provider.setHistory(System.getProperty("user.home") + "/.josh/", "josh_history", 800);
        provider.setPromptColor(Ansi.Color.CYAN);
        provider.setInfoColor(Ansi.Color.GREEN);
        provider.setWarnColor(Ansi.Color.YELLOW);
        provider.setErrorColor(Ansi.Color.RED);
        provider.addCompleter(new CommandCompleter(shell.getLineParser(), commandProvider.getCommands()));

        provider.setPrompt("> ");
        shell.setConsoleProvider(provider);

        shell.setCommandProvider(commandProvider);

        return shell.run().getExitCode();
    }

}
