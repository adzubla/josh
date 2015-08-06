package josh.shell;

/**
 * To be implemented by command providers that need access to the shell object
 * (to use the associated ConsoleProvider for example).
 */
public interface ShellAwareCommandProvider {

    void setShell(Shell shell);

}
