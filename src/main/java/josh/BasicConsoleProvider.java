package josh;

import java.util.List;

public class BasicConsoleProvider implements ConsoleProvider {

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
    public void displayInfo(String message) {
        System.out.println(message);
    }

    @Override
    public String readLine() {
        return System.console().readLine();
    }
}