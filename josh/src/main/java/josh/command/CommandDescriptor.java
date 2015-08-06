package josh.command;

import java.util.Collections;
import java.util.Map;

public class CommandDescriptor {

    protected String commandName;
    protected String commandDescription;
    protected Map<String, Class> options = Collections.emptyMap();

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

    public Map<String, Class> getOptions() {
        return options;
    }

    public void setOptions(Map<String, Class> options) {
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