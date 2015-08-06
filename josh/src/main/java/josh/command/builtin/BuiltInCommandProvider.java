package josh.command.builtin;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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

    public void setCustomHelpFormatter(HelpFormatter helpFormatter) {
        this.helpFormatter = helpFormatter;
    }

    @Override
    public void initialize() {
        commands = new HashMap<String, CommandDescriptor>();

        addDescriptor("date", "Display current date.");
        addDescriptor("props", "Display Java system properties.");
        addDescriptor("env", "Display environment variables.");
        addDescriptor("pwd", "Display working directory.");
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
    public void setShell(Shell shell) {
        this.shell = shell;
    }

    @Override
    public HelpFormatter getHelpFormatter(String commandName) {
        return helpFormatter;
    }

    @Override
    public Map<String, CommandDescriptor> getCommands() {
        return new HashMap<String, CommandDescriptor>(commands);
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
            commandOutcome.setExitCode(dateCommand(arguments));
        }
        else if ("env".equals(commandDescriptor.getCommandName())) {
            commandOutcome.setExitCode(envCommand());
        }
        else if ("props".equals(commandDescriptor.getCommandName())) {
            commandOutcome.setExitCode(propertiesCommand());
        }
        else if ("pwd".equals(commandDescriptor.getCommandName())) {
            commandOutcome.setExitCode(pwdCommand());
        }
        else if ("clear".equals(commandDescriptor.getCommandName())) {
            commandOutcome.setExitCode(clearCommand());
        }
        else if ("help".equals(commandDescriptor.getCommandName())) {
            HelpCommand helpCommand = new HelpCommand(shell);
            commandOutcome.setExitCode(helpCommand.run(arguments));
        }
        else if ("exit".equals(commandDescriptor.getCommandName())) {
            commandOutcome.setExitRequest();
        }
        return commandOutcome;
    }

    protected int pwdCommand() {
        System.out.println(System.getProperty("user.dir"));
        return 0;
    }

    protected int propertiesCommand() {

        Properties properties = System.getProperties();
        for (String key : properties.stringPropertyNames()) {
            String value = properties.getProperty(key);
            System.out.println(key + "=" + value);
        }

        return 0;
    }

    protected int envCommand() {

        Map<String, String> env = System.getenv();
        for (String key : env.keySet()) {
            String value = env.get(key);
            System.out.println(key + "=" + value);
        }

        return 0;
    }

    public int dateCommand(List<String> arguments) {
        String format = "yyyy-MM-dd'T'HH:mm:ss";

        Date currentDate = new Date();

        if (arguments.size() == 1) {
            format = arguments.get(0);
        }
        else if (!arguments.isEmpty()) {
            System.err.println("Expected date format");
            return 1;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        System.out.println(sdf.format(currentDate));

        return 0;
    }

    protected int clearCommand() {
        try {
            JLineProvider jLineProvider = (JLineProvider)shell.getConsoleProvider();
            jLineProvider.getConsole().clearScreen();
            return 0;
        }
        catch (IOException e) {
            LOG.error("clear errror", e);
            return 1;
        }
    }

    private static class BuiltInHelpFormatter implements HelpFormatter {

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