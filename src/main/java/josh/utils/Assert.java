package josh.utils;

public class Assert {

    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalStateException(message);
        }
    }
}
