package josh.impl;

import java.io.File;
import java.io.IOException;

import jline.console.ConsoleReader;
import jline.console.history.FileHistory;
import josh.api.ConsoleProvider;

public class JLineProvider implements ConsoleProvider {

    ConsoleReader console;
    FileHistory history;

    public JLineProvider() {
        try {
            console = new ConsoleReader();
            //console.setExpandEvents(false);
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
        console.setPrompt(prompt);
    }

    @Override
    public void displayPrompt() {
    }

    @Override
    public void displayError(String message) {
        try {
            console.println(message);
            console.flush();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void displayWarning(String message) {
        try {
            console.println(message);
            console.flush();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void displayInfo(String message) {
        try {
            console.println(message);
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
            File file = new File(path, fileName);
            {
                System.out.println("*** " + file + "\t" + file.canWrite());
            }
            history = new FileHistory(file);
            console.setHistoryEnabled(true);
            console.setHistory(history);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setDebugMode() {
        jline.internal.Log.setOutput(System.out);
        System.setProperty("jline.internal.Log.debug", "true");
    }

}
