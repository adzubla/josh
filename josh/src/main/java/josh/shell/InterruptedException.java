package josh.shell;

/**
 * Exception lancada quando ocorrer uma interrupção externa no programa
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
