package josh;

import java.util.List;
import java.util.Map;

public class HelpCommand {

    protected Map<String, CommandDescriptor> commands;

    public HelpCommand(Map<String, CommandDescriptor> commands) {
        this.commands = commands;
    }

    public int run(List<String> arguments) {

        for (CommandDescriptor commandDescriptor : commands.values()) {
            System.out.println(commandDescriptor.getCommandName() + " - " + commandDescriptor.getCommandDescription());
        }

        return 0;
    }

}
