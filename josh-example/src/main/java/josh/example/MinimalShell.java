package josh.example;

import josh.command.builtin.BuiltInCommandProvider;
import josh.shell.BasicConsoleProvider;
import josh.shell.LineParserImpl;
import josh.shell.Shell;

public class MinimalShell {

    public static void main(String[] args) {
        Shell shell = new Shell();
        shell.setLineParser(new LineParserImpl());
        shell.setCommandProvider(new BuiltInCommandProvider());
        shell.setConsoleProvider(new BasicConsoleProvider());
        System.exit(shell.runInteractive().getExitCode());
    }

}
