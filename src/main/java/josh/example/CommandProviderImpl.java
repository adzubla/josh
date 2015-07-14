package josh.example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import josh.api.CommandDescriptor;
import josh.api.CommandNotFound;
import josh.api.CommandOutcome;
import josh.api.CommandProvider;
import josh.api.HelpFormatter;
import josh.impl.CommandParser;

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

        List<String> tokens = commandParser.parseLine(line);

        String name = tokens.get(0);
        List<String> arguments = tokens.subList(1, tokens.size());

        CommandDescriptor commandDescriptor = commands.get(name);
        if (commandDescriptor == null) {
            throw new CommandNotFound(name);
        }

        if ("date".equals(commandDescriptor.getCommandName())) {
            DateCommand dateCommand = new DateCommand();
            commandOutcome.setExitCode(dateCommand.run(arguments));
        }
        else if ("echo".equals(commandDescriptor.getCommandName())) {
            EchoCommand echoCommand = new EchoCommand();
            commandOutcome.setExitCode(echoCommand.run(arguments));
        }
        else if ("help".equals(commandDescriptor.getCommandName())) {
            HelpCommand helpCommand = new HelpCommand(commands);
            commandOutcome.setExitCode(helpCommand.run(arguments));
        }

        return commandOutcome;
    }
    /*
    public CommandOutcome execute(String[] args) throws CommandNotFound {

    }
    */
}