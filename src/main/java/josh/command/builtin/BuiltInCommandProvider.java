package josh.command.builtin;

import java.io.IOException;
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
import josh.shell.Shell;
import josh.shell.ShellAware;
import josh.shell.jline.JLineProvider;

public class BuiltInCommandProvider implements CommandProvider, ShellAware {
    private static final Logger LOG = LoggerFactory.getLogger(BuiltInCommandProvider.class);

    protected Map<String, CommandDescriptor> commands;

    protected Shell shell;

    protected HelpFormatter helpFormatter;

    public BuiltInCommandProvider withCustomHelpFormatter(HelpFormatter helpFormatter) {
        this.helpFormatter = helpFormatter;
        return this;
    }

    @Override
    public void initialize() {
        commands = new HashMap<String, CommandDescriptor>();

        addDescriptor("date", "Display current date.");
        addDescriptor("props", "Display Java system properties.");
        addDescriptor("env", "Display environment variables.");
        addDescriptor("help", "Display commands.");
        addDescriptor("clear", "Clear screen.");
        addDescriptor("exit", "Exit shell.");

        LOG.debug("commands = {}", commands);

        if (this.helpFormatter == null) {
            helpFormatter = new BuiltInHelpFormatter();
        }
    }

    private void addDescriptor(String name, String description) {
        CommandDescriptor descriptor = new CommandDescriptor();
        descriptor.setCommandName(name);
        descriptor.setCommandDescription(description);
        commands.put(descriptor.getCommandName(), descriptor);
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
        return helpFormatter;
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
        else if ("env".equals(commandDescriptor.getCommandName())) {
            EnvironmentCommand environmentCommand = new EnvironmentCommand();
            commandOutcome.setExitCode(environmentCommand.run(arguments));
        }
        else if ("props".equals(commandDescriptor.getCommandName())) {
            PropertiesCommand propertiesCommand = new PropertiesCommand();
            commandOutcome.setExitCode(propertiesCommand.run(arguments));
        }
        else if ("help".equals(commandDescriptor.getCommandName())) {
            HelpCommand helpCommand = new HelpCommand(shell);
            commandOutcome.setExitCode(helpCommand.run(arguments));
        }
        else if ("clear".equals(commandDescriptor.getCommandName())) {
            try {
                JLineProvider jLineProvider = (JLineProvider)shell.getConsoleProvider();
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

    private class BuiltInHelpFormatter implements HelpFormatter {

        private String NEW_LINE = "\n";

        @Override
        public String formatHelpMessage(CommandDescriptor cd) {
            StringBuilder sb = new StringBuilder();
            sb.append(cd.getCommandDescription()).append(NEW_LINE);
            sb.append("Usage: ").append(cd.getCommandName());
            if ("date".equals(cd.getCommandName())) {
                sb.append(" [options]").append(NEW_LINE);
                sb.append("  Options: ").append(NEW_LINE);
                sb.append("      Date Format [optional]").append(NEW_LINE);
            }
            else if ("help".equals(cd.getCommandName())) {
                sb.append(" [options]").append(NEW_LINE);
                sb.append("  Options: ").append(NEW_LINE);
                sb.append("      Command Name [optional]").append(NEW_LINE);
            }
            sb.append(NEW_LINE);
            return sb.toString();
        }
    }

}
