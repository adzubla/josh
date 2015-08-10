package josh.shell;

/**
 * Exception thrown when the program is interrupted by an external signal like Ctrl-C
 */
public class InterruptedException extends RuntimeException {

    public InterruptedException() {
    }

    public InterruptedException(String message) {
        super(message);
    }

    public InterruptedException(String message, Throwable cause) {
        super(message, cause);
    }

    public InterruptedException(Throwable cause) {
        super(cause);
    }

}
