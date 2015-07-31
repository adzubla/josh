package josh.example.simple;

import java.util.Arrays;

import org.fusesource.jansi.Ansi;

import josh.command.builtin.BuiltInCommandProvider;
import josh.command.CommandNotFound;
import josh.command.CommandProvider;
import josh.command.CompoundCommandProvider;
import josh.example.MyShellFinalizer;
import josh.example.MyShellInitializer;
import josh.shell.BasicConsoleProvider;
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

        Shell shell = new Shell();
        shell.setDisplayStackTraceOnError(true);
        shell.setExitOnError(false);
        shell.setLineParser(new LineParserImpl());
        shell.setInitializer(new MyShellInitializer());
        shell.setFinalizer(new MyShellFinalizer());
        shell.setCommandProvider(commandProvider);

        int exitCode;
        if (args.length == 0) {
            JLineProvider provider = getJLineProvider(shell, commandProvider);
            shell.setConsoleProvider(provider);
            exitCode = shell.run().getExitCode();
        }
        else {
            shell.setConsoleProvider(new BasicConsoleProvider());
            exitCode = shell.executeCommand(Arrays.asList(args)).getExitCode();
        }
        System.exit(exitCode);
    }

    private static JLineProvider getJLineProvider(Shell shell, CommandProvider commandProvider) {
        JLineProvider provider = new JLineProvider();
        provider.setHistory(System.getProperty("user.home") + "/.josh/", "josh_history", 800);
        provider.setPromptColor(Ansi.Color.CYAN);
        provider.setInfoColor(Ansi.Color.GREEN);
        provider.setWarnColor(Ansi.Color.YELLOW);
        provider.setErrorColor(Ansi.Color.RED);
        provider.addCompleter(new CommandCompleter(shell.getLineParser(), commandProvider));
        provider.setPrompt("> ");
        return provider;
    }

}
