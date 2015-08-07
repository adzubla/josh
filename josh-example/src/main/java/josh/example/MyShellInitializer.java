package josh.example;

import josh.shell.ConsoleProvider;
import josh.shell.Shell;
import josh.shell.ShellInitializer;

public class MyShellInitializer implements ShellInitializer {

    @Override
    public void initialize(Shell shell) {
        if (shell.isInteractive()) {
            ConsoleProvider console = shell.getConsoleProvider();
            String banner = "   _           _     \n" +
                    "  (_) ___  ___| |__  \n" +
                    "  | |/ _ \\/ __| '_ \\ \n" +
                    "  | | (_) \\__ \\ | | |\n" +
                    " _/ |\\___/|___/_| |_|\n" +
                    "|__/                 \n";
            console.displayInfo(banner);
            console.displayInfo("Press Ctrl-D to exit shell.");
            console.displayInfo("");
        }
    }
}
