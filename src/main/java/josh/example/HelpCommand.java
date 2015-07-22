package josh.example;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import josh.command.CommandDescriptor;

public class HelpCommand {

    protected Map<String, CommandDescriptor> commands;

    public HelpCommand(Map<String, CommandDescriptor> commands) {
        this.commands = new TreeMap<String, CommandDescriptor>(commands);
    }

    public int run(List<String> arguments) {

        for (CommandDescriptor commandDescriptor : commands.values()) {
            System.out.println(commandDescriptor.getCommandName() + " - " + commandDescriptor.getCommandDescription());
        }

        return 0;
    }

}
