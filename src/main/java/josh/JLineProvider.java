package josh;

import java.util.List;

public class JLineProvider implements ConsoleProvider {
    @Override
    public void configCompletion(List<CommandDescriptor> commands) {

    }

    @Override
    public void displayPrompt() {

    }

    @Override
    public void displayError(String message) {

    }

    @Override
    public void displayWarning(String message) {

    }

    @Override
    public void displayMessage(String message) {

    }

    @Override
    public String readLine() {
        return null;
    }
}
