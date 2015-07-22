package josh.command;

public class CommandNotFound extends Throwable {

    protected String name;

    public CommandNotFound(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
