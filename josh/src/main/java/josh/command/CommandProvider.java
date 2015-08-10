package josh.command;

import java.util.List;
import java.util.Map;

/**
 * Interface used to find and load commands that will be used in the Shell.
 */
public interface CommandProvider {

    void initialize();

    void destroy();

    boolean isValidCommand(String commandName);

    HelpFormatter getHelpFormatter(String commandName);

    CommandOutcome execute(List<String> args) throws CommandNotFound;

    Map<String, CommandDescriptor> getCommands();

}
