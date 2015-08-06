package josh.shell;

/**
 * Cleanup shell context, show goodbye message, etc. Is is called once before the shell terminates
 * (even when pressing Ctrl-C).
 */
public interface ShellFinalizer {

    void destroy(Shell shell);

}
