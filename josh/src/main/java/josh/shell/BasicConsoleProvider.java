package josh.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class BasicConsoleProvider implements ConsoleProvider {

    protected String prompt = "> ";
    protected BufferedReader reader;

    public BasicConsoleProvider() {
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public void destroy() {
    }

    @Override
    public void initialize() {
    }

    @Override
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    @Override
    public void displayPrompt() {
        System.out.print(prompt);
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
        try {
            return reader.readLine();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
