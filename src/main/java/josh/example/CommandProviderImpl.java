package josh.example;

import java.io.File;
import java.io.IOException;
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
import josh.impl.JLineProvider;

public class CommandProviderImpl implements CommandProvider {
    private static final Logger LOG = LoggerFactory.getLogger(CommandProviderImpl.class);

    protected Map<String, CommandDescriptor> commands;

    protected JLineProvider jLineProvider;

    public CommandProviderImpl() {
        CommandDescriptor dateDescriptor = new CommandDescriptor();
        dateDescriptor.setCommandName("date");
        dateDescriptor.setCommandDescription("Display current date.");

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

        CommandDescriptor helpDescriptor = new CommandDescriptor();
        helpDescriptor.setCommandName("help");
        helpDescriptor.setCommandDescription("Display commands.");

        CommandDescriptor clearDescriptor = new CommandDescriptor();
        clearDescriptor.setCommandName("clear");
        clearDescriptor.setCommandDescription("Clear screen.");

        CommandDescriptor exitDescriptor = new CommandDescriptor();
        exitDescriptor.setCommandName("exit");
        exitDescriptor.setCommandDescription("Exit shell.");

        commands = new HashMap<String, CommandDescriptor>();
        commands.put(dateDescriptor.getCommandName(), dateDescriptor);
        commands.put(echoDescriptor.getCommandName(), echoDescriptor);
        commands.put(helpDescriptor.getCommandName(), helpDescriptor);
        commands.put(clearDescriptor.getCommandName(), clearDescriptor);
        commands.put(exitDescriptor.getCommandName(), exitDescriptor);

        LOG.debug("commands = {}", commands);
    }

    public Map<String, CommandDescriptor> getCommands() {
        return commands;
    }

    @Override
    public HelpFormatter getHelpFormatter() {
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
        // "built-in" commands
        else if ("clear".equals(commandDescriptor.getCommandName())) {
            try {
                jLineProvider.getConsole().clearScreen();
            }
            catch (IOException e) {
                LOG.error("clear errror", e);
            }
        }
        else if ("exit".equals(commandDescriptor.getCommandName())) {
            commandOutcome.setExitRequest();
        }
        return commandOutcome;
    }

    public JLineProvider getjLineProvider() {
        return jLineProvider;
    }

    public void setjLineProvider(JLineProvider jLineProvider) {
        this.jLineProvider = jLineProvider;
    }

}