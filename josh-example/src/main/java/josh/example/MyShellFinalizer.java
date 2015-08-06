package josh.example;

import josh.shell.ConsoleProvider;
import josh.shell.Shell;
import josh.shell.ShellFinalizer;

public class MyShellFinalizer implements ShellFinalizer {

    @Override
    public void destroy(Shell shell) {
        ConsoleProvider console = shell.getConsoleProvider();
        console.displayInfo("Goodbye!");
    }

}
