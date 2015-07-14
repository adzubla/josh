package josh;

import java.util.HashMap;
import java.util.Map;

public class CommandProviderImpl implements CommandProvider {

    protected CommandParser commandParser;
    protected Map<String, CommandDescriptor> commands;

    @Override
    public void init() {

        CommandDescriptor dateDescriptor = new CommandDescriptor();
        dateDescriptor.setCommandName("date");
        dateDescriptor.setCommandDescription("Display current date.");

        CommandDescriptor echoDescriptor = new CommandDescriptor();
        echoDescriptor.setCommandName("echo");
        echoDescriptor.setCommandDescription("Echo parameters.");

        CommandDescriptor helpDescriptor = new CommandDescriptor();
        helpDescriptor.setCommandName("help");
        helpDescriptor.setCommandDescription("Display commands.");

        commands = new HashMap<String, CommandDescriptor>();
        commands.put(dateDescriptor.getCommandName(), dateDescriptor);
        commands.put(echoDescriptor.getCommandName(), echoDescriptor);
        commands.put(helpDescriptor.getCommandName(), helpDescriptor);
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

        CommandDescriptor commandDescriptor = commands.get(result.getCommandName());
        if (commandDescriptor == null) {
            throw new CommandNotFound(result.getCommandName());
        }

        if ("date".equals(commandDescriptor.getCommandName())) {
            DateCommand dateCommand = new DateCommand();
            commandOutcome.setExitCode(dateCommand.run(result.getArguments()));
        } else if ("echo".equals(commandDescriptor.getCommandName())) {
            EchoCommand echoCommand = new EchoCommand();
            commandOutcome.setExitCode(echoCommand.run(result.getArguments()));
        } else if ("help".equals(commandDescriptor.getCommandName())) {
            HelpCommand helpCommand = new HelpCommand(commands);
            commandOutcome.setExitCode(helpCommand.run(result.getArguments()));
        }

        return commandOutcome;
    }

}
