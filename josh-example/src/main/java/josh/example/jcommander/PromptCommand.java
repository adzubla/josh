package josh.example.jcommander;

import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import josh.command.CommandOutcome;
import josh.command.jcommander.Executable;
import josh.shell.ConsoleProvider;

@Parameters(commandNames = "prompt", commandDescription = "Changes the Prompt Message")
public class PromptCommand implements Executable {

    private ConsoleProvider consoleProvider;

    public PromptCommand(ConsoleProvider consoleProvider) {
        this.consoleProvider = consoleProvider;
    }

    @Parameter
    private List<String> arguments = new ArrayList<String>();

    @Override
    public CommandOutcome execute() {
        if (arguments != null && arguments.size() == 1 && arguments.get(0) != null && !"".equals(arguments.get(0))) {
            consoleProvider.setPrompt(arguments.get(0));
        }
        else {
            consoleProvider.displayWarning("Please provide a message to the new prompt");
        }
        return new CommandOutcome(0);
    }

}
