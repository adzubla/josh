package josh.example.simple;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import josh.command.CommandDescriptor;
import josh.command.CommandNotFound;
import josh.command.CommandOutcome;
import josh.command.CommandProvider;
import josh.command.HelpFormatter;
import josh.example.YesNo;

public class ExampleCommandProvider implements CommandProvider {
    private static final Logger LOG = LoggerFactory.getLogger(ExampleCommandProvider.class);

    protected Map<String, CommandDescriptor> commands;

    public ExampleCommandProvider() {
        CommandDescriptor echoDescriptor = new CommandDescriptor();
        echoDescriptor.setCommandName("echo");
        echoDescriptor.setCommandDescription("Echo parameters.");
        Map<String, Class> options = new HashMap<String, Class>();
        options.put("--quiet", null);
        options.put("--opt1", null);
        options.put("--opt2", null);
        options.put("--opt3", null);
        options.put("--debug", YesNo.class);
        options.put("--file", File.class);
        options.put("--level", String.class);
        echoDescriptor.setOptions(options);

        commands = new HashMap<String, CommandDescriptor>();
        commands.put(echoDescriptor.getCommandName(), echoDescriptor);

        LOG.debug("commands = {}", commands);
    }

    public Map<String, CommandDescriptor> getCommands() {
        return commands;
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

    private CommandOutcome invokeCommand(CommandDescriptor commandDescriptor, List<String> arguments) {
        LOG.debug("invokeCommand {}, {}", commandDescriptor.getCommandName(), arguments);

        CommandOutcome commandOutcome = new CommandOutcome();
        if ("echo".equals(commandDescriptor.getCommandName())) {
            EchoCommand echoCommand = new EchoCommand();
            commandOutcome.setExitCode(echoCommand.run(arguments));
        }
        return commandOutcome;
    }

}