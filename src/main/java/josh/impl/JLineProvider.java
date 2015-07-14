package josh.impl;

import java.io.File;
import java.io.IOException;

import org.fusesource.jansi.Ansi;

import jline.console.ConsoleReader;
import jline.console.history.FileHistory;
import josh.api.ConsoleProvider;

import static org.fusesource.jansi.Ansi.Color.DEFAULT;
import static org.fusesource.jansi.Ansi.ansi;

public class JLineProvider implements ConsoleProvider {

    ConsoleReader console;
    FileHistory history;

    Ansi.Color promptColor = DEFAULT;
    Ansi.Color errorColor = DEFAULT;
    Ansi.Color warnColor = DEFAULT;
    Ansi.Color infoColor = DEFAULT;

    public JLineProvider() {
        try {
            console = new ConsoleReader();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize() {
    }

    @Override
    public void destroy() {
        try {
            history.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        console.shutdown();
    }

    @Override
    public void setPrompt(String prompt) {
        console.setPrompt(ansi().fgBright(promptColor).a(prompt).reset().toString());
    }

    @Override
    public void displayPrompt() {
    }

    @Override
    public void displayError(String message) {
        println(ansi().fg(errorColor).a(message).reset().toString());
    }

    @Override
    public void displayWarning(String message) {
        println(ansi().fg(warnColor).a(message).reset().toString());
    }

    @Override
    public void displayInfo(String message) {
        println(ansi().fg(infoColor).a(message).reset().toString());
    }

    public void println(CharSequence charSequence) {
        try {
            console.println(charSequence);
            console.flush();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String readLine() {
        try {
            return console.readLine();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Not working ????
    public void setHistory(String path, String fileName) {
        File directory = new File(path);
        if (!directory.mkdirs()) {
            //log.warn("could not create directory " + directory.getAbsolutePath());
        }
        try {
            history = new FileHistory(new File(path, fileName));
            console.setHistoryEnabled(true);
            console.setHistory(history);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Ansi.Color getPromptColor() {
        return promptColor;
    }

    public void setPromptColor(Ansi.Color promptColor) {
        this.promptColor = promptColor;
    }

    public Ansi.Color getErrorColor() {
        return errorColor;
    }

    public void setErrorColor(Ansi.Color errorColor) {
        this.errorColor = errorColor;
    }

    public Ansi.Color getWarnColor() {
        return warnColor;
    }

    public void setWarnColor(Ansi.Color warnColor) {
        this.warnColor = warnColor;
    }

    public Ansi.Color getInfoColor() {
        return infoColor;
    }

    public void setInfoColor(Ansi.Color infoColor) {
        this.infoColor = infoColor;
    }

    public void setDebugMode() {
        jline.internal.Log.setOutput(System.out);
        System.setProperty("jline.internal.Log.debug", "true");
    }

}
