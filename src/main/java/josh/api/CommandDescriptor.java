package josh.api;

public class CommandDescriptor {

    protected String commandName;
    protected String commandDescription;

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandDescription() {
        return commandDescription;
    }

    public void setCommandDescription(String commandDescription) {
        this.commandDescription = commandDescription;
    }

    @Override
    public String toString() {
        return "CommandDescriptor{" +
                "commandName='" + commandName + '\'' +
                ", commandDescription='" + commandDescription + '\'' +
                '}';
    }
}