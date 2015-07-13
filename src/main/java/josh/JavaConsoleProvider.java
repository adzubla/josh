package josh;

import java.util.List;

public class JavaConsoleProvider implements ConsoleProvider {

    @Override
    public void configCompletion(List<CommandDescriptor> commands) {

    }

    @Override
    public void displayPrompt() {
        System.out.print("> ");
    }

    @Override
    public void displayError(String message) {
        System.out.println(message);
    }

    @Override
    public void displayWarning(String message) {
        System.out.println(message);
    }

    @Override
    public void displayMessage(String message) {
        System.out.println(message);
    }

    @Override
    public String readLine() {
        String line = System.console().readLine();
        return line;
    }
}
