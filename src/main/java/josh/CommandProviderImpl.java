package josh;

import java.util.ArrayList;
import java.util.List;

public class CommandProviderImpl implements CommandProvider {

    protected CommandParser commandParser;
    List<CommandDescriptor> commands;

    @Override
    public List<CommandDescriptor> findCommands() {
        CommandDescriptor dateDescriptor = new CommandDescriptor();
        dateDescriptor.setCommandName("date");
        dateDescriptor.setCommandDescription("Display current date.");

        commands = new ArrayList<CommandDescriptor>();
        commands.add(dateDescriptor);
        return commands;
    }

    @Override
    public HelpFormatter getHelpFormatter() {
        return null;
    }

    public CommandParser getCommandParser() {
        return commandParser;
    }

    public void setCommandParser(CommandParser commandParser) {
        this.commandParser = commandParser;
    }

    // Mover para CommandExecutor???
    @Override
    public CommandOutcome execute(String line) throws CommandNotFound {

        CommandOutcome commandOutcome = new CommandOutcome();

        ParseResult result = commandParser.parseLine(line);

        if ("date".equals(result.getCommandName())) {
            DateCommand dateCommand = new DateCommand();
            dateCommand.run(result.getArguments());
            commandOutcome.setExitCode(0);
        } else {
            throw new CommandNotFound(result.getCommandName());
        }
        return commandOutcome;
    }

}
