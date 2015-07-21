package josh.example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import josh.api.CommandDescriptor;
import josh.api.CommandNotFound;
import josh.api.CommandOutcome;
import josh.api.CommandProvider;
import josh.api.HelpFormatter;

public class BuiltInCommandProvider implements CommandProvider {
    private static final Logger LOG = LoggerFactory.getLogger(BuiltInCommandProvider.class);

    protected Map<String, CommandDescriptor> commands;

    public BuiltInCommandProvider() {
        CommandDescriptor dateDescriptor = new CommandDescriptor();
        dateDescriptor.setCommandName("date2");
        dateDescriptor.setCommandDescription("Display current date.");

        CommandDescriptor echoDescriptor = new CommandDescriptor();
        echoDescriptor.setCommandName("echo2");
        echoDescriptor.setCommandDescription("Echo parameters.");
        Map<String, Class> options = new HashMap<String, Class>();
        options.put("--quiet", null);
        options.put("--opt1", null);
        options.put("--opt2", null);
        options.put("--opt3", null);
        options.put("--level", String.class);
        echoDescriptor.setOptions(options);

        CommandDescriptor helpDescriptor = new CommandDescriptor();
        helpDescriptor.setCommandName("help2");
        helpDescriptor.setCommandDescription("Display commands.");

        CommandDescriptor exitDescriptor = new CommandDescriptor();
        exitDescriptor.setCommandName("exit2");
        exitDescriptor.setCommandDescription("Exit shell.");

        commands = new HashMap<String, CommandDescriptor>();
        commands.put(dateDescriptor.getCommandName(), dateDescriptor);
        commands.put(echoDescriptor.getCommandName(), echoDescriptor);
        commands.put(helpDescriptor.getCommandName(), helpDescriptor);
        commands.put(exitDescriptor.getCommandName(), exitDescriptor);

        LOG.debug("commands = {}", commands);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public boolean isValidCommand(String commandName) {
        return commands.containsKey(commandName);
    }

    @Override
    public HelpFormatter getHelpFormatter(String commandName) {
        return null;
    }

    @Override
    public CommandOutcome execute(List<String> tokens) throws CommandNotFound {
        String name = tokens.get(0);
        List<String> arguments = tokens.subList(1, tokens.size());

        CommandDescriptor commandDescriptor = commands.get(name);
        if (commandDescriptor == null) {
            throw new CommandNotFound(name);
        }

        return invokeCommand(commandDescriptor, arguments);
    }

    @Override
    public Map<String, CommandDescriptor> getCommands() {
        return new HashMap<String, CommandDescriptor>(commands);
    }

    private CommandOutcome invokeCommand(CommandDescriptor commandDescriptor, List<String> arguments) {
        LOG.debug("invokeCommand {}, {}", commandDescriptor.getCommandName(), arguments);

        CommandOutcome commandOutcome = new CommandOutcome();
        if ("date2".equals(commandDescriptor.getCommandName())) {
            DateCommand dateCommand = new DateCommand();
            commandOutcome.setExitCode(dateCommand.run(arguments));
        }
        else if ("echo2".equals(commandDescriptor.getCommandName())) {
            EchoCommand echoCommand = new EchoCommand();
            commandOutcome.setExitCode(echoCommand.run(arguments));
        }
        else if ("help2".equals(commandDescriptor.getCommandName())) {
            HelpCommand helpCommand = new HelpCommand(commands);
            commandOutcome.setExitCode(helpCommand.run(arguments));
        }
        else if ("exit2".equals(commandDescriptor.getCommandName())) {
            commandOutcome.setExitRequest();
        }
        return commandOutcome;
    }

}
