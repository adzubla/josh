package josh;

public class CommandNotFound extends Throwable {

    String name;

    public CommandNotFound(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
