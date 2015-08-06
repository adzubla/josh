package josh.shell;

/**
 * Initialize shell context, show welcome message, etc. It is called just once, before the shell starts running.
 */
public interface ShellInitializer {

    void initialize(Shell shell);

}
