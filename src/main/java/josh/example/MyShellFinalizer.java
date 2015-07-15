package josh.example;

import josh.api.ConsoleProvider;
import josh.api.Shell;
import josh.api.ShellFinalizer;

public class MyShellFinalizer implements ShellFinalizer {

    @Override
    public void destroy(Shell shell) {
        ConsoleProvider console = shell.getConsoleProvider();
        console.displayInfo("Goodbye!");
    }

}
