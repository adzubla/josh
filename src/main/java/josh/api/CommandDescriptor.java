package josh.api;

import java.util.Collection;

public class CommandDescriptor {

    protected String commandName;
    protected String commandDescription;
    protected Collection<String> options;

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

    public Collection<String> getOptions() {
        return options;
    }

    public void setOptions(Collection<String> options) {
        this.options = options;
    }

    @Override
    public String toString() {
        return "CommandDescriptor{" +
                "commandName='" + commandName + '\'' +
                ", commandDescription='" + commandDescription + '\'' +
                '}';
    }
}