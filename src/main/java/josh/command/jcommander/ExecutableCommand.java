package josh.command.jcommander;

/**
 * Extension interface allowing command provider to initialize and finalize a command
 *
 * @author Jeferson Estevo
 */
public interface ExecutableCommand extends Executable {

    /**
     * Method called before the {@link Executable#execute()} method
     */
    void initialize();

    /**
     * Method called after the {@link Executable#execute()} method <br />
     * If a shutdownHook occurs, this method will be called too
     *
     * @see Runtime#addShutdownHook(Thread)
     */
    void finalizeCommand();
}
