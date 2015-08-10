package josh.command.builtin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import josh.command.CommandDescriptor;
import josh.command.CommandProvider;
import josh.command.HelpFormatter;
import josh.shell.ConsoleProvider;
import josh.shell.Shell;

/**
 * Help command the lists a description of all available commands.
 */
public class HelpCommand {

    protected Shell shell;

    public HelpCommand(Shell shell) {
        this.shell = shell;
    }

    public int run(List<String> arguments) {
        ConsoleProvider consoleProvider = shell.getConsoleProvider();
        if (arguments.size() > 1) {
            consoleProvider.displayError("Should use only 1 option on help");
            return 26;
        }

        CommandProvider commandProvider = shell.getCommandProvider();

        Map<String, CommandDescriptor> commands = new TreeMap<String, CommandDescriptor>(commandProvider.getCommands());

        List<CommandDescriptor> commandsToShow = getCommandsToShow(arguments, commands);

        if (commandsToShow.size() == 0) {
            consoleProvider.displayError("No commands found");
        }
        else if (commandsToShow.size() == 1) {
            CommandDescriptor cd = commandsToShow.get(0);
            HelpFormatter helpFormatter = commandProvider.getHelpFormatter(cd.getCommandName());
            if (helpFormatter != null) {
                consoleProvider.displayInfo(helpFormatter.formatHelpMessage(cd));
            }
            else {
                showSimpleHelpMessage(consoleProvider, cd);
            }
        }
        else {
            for (CommandDescriptor commandDescriptor : commandsToShow) {
                showSimpleHelpMessage(consoleProvider, commandDescriptor);
            }
        }

        return 0;
    }

    protected List<CommandDescriptor> getCommandsToShow(List<String> arguments, Map<String, CommandDescriptor> commands) {
        List<CommandDescriptor> commandsToShow = new ArrayList<CommandDescriptor>();
        if (arguments.size() == 0) {
            commandsToShow = new ArrayList<CommandDescriptor>(commands.values());
        }
        else {
            String commandName = arguments.get(0);
            for (CommandDescriptor commandDescriptor : commands.values()) {
                if (commandDescriptor.getCommandName().startsWith(commandName)) {
                    commandsToShow.add(commandDescriptor);
                }
            }
        }
        return commandsToShow;
    }

    protected void showSimpleHelpMessage(ConsoleProvider consoleProvider, CommandDescriptor commandDescriptor) {
        consoleProvider.displayInfo(
                commandDescriptor.getCommandName() + " - " + commandDescriptor.getCommandDescription());
    }

}
