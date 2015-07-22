package josh.command;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import josh.example.DateCommand;
import josh.example.HelpCommand;
import josh.shell.Shell;
import josh.shell.ShellAware;
import josh.shell.jline.JLineProvider;

public class BuiltInCommandProvider implements CommandProvider, ShellAware {
    private static final Logger LOG = LoggerFactory.getLogger(BuiltInCommandProvider.class);

    protected Map<String, CommandDescriptor> commands;

    protected Shell shell;

    public BuiltInCommandProvider() {
        CommandDescriptor dateDescriptor = new CommandDescriptor();
        dateDescriptor.setCommandName("date");
        dateDescriptor.setCommandDescription("Display current date.");

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
        commands.put(helpDescriptor.getCommandName(), helpDescriptor);
        commands.put(clearDescriptor.getCommandName(), clearDescriptor);
        commands.put(exitDescriptor.getCommandName(), exitDescriptor);

        LOG.debug("commands = {}", commands);    }

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
        if ("date".equals(commandDescriptor.getCommandName())) {
            DateCommand dateCommand = new DateCommand();
            commandOutcome.setExitCode(dateCommand.run(arguments));
        }
        else if ("help".equals(commandDescriptor.getCommandName())) {
            HelpCommand helpCommand = new HelpCommand(shell.getCommandProvider().getCommands());
            commandOutcome.setExitCode(helpCommand.run(arguments));
        }
        else if ("clear".equals(commandDescriptor.getCommandName())) {
            try {
                JLineProvider jLineProvider = (JLineProvider) shell.getConsoleProvider();
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

    @Override
    public void setShell(Shell shell) {
        this.shell = shell;
    }
}
