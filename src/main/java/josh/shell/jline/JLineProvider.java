package josh.shell.jline;

import java.io.File;
import java.io.IOException;

import org.fusesource.jansi.Ansi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jline.console.ConsoleReader;
import jline.console.completer.Completer;
import jline.console.history.FileHistory;
import josh.shell.ConsoleProvider;

import static org.fusesource.jansi.Ansi.Color.DEFAULT;
import static org.fusesource.jansi.Ansi.ansi;

public class JLineProvider implements ConsoleProvider {
    private static final Logger LOG = LoggerFactory.getLogger(JLineProvider.class);

    ConsoleReader console;
    FileHistory history;

    Ansi.Color promptColor = DEFAULT;
    Ansi.Color errorColor = DEFAULT;
    Ansi.Color warnColor = DEFAULT;
    Ansi.Color infoColor = DEFAULT;

    public JLineProvider() {
        try {
            System.setProperty("jline.shutdownhook", "true");
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
        console.shutdown();
        try {
            if (history != null) {
                history.flush();
            }
        }
        catch (IOException e) {
            LOG.error("history flush error", e);
        }
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
        catch (UnsupportedOperationException e) {
            LOG.error("JLineProvider.readLine exception", e);
            return null;
        }
    }

    public void setHistory(String path, String fileName, int maxSize) {
        File directory = new File(path);
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                String msg = "Could not create directory " + directory.getAbsolutePath();
                LOG.warn(msg);
                println(msg);
                return;
            }
        }
        else if (!directory.isDirectory()) {
            String msg = "File " + directory.getAbsolutePath() + " already exists";
            LOG.warn(msg);
            println(msg);
            return;
        }

        File file = new File(path, fileName);
        try {
            history = new FileHistory(file);
            history.setMaxSize(maxSize);
            console.setHistory(history);
        }
        catch (IOException e) {
            String msg = "Error acessing file " + file.getAbsolutePath();
            LOG.error(msg);
            println(msg);
        }
    }

    public boolean addCompleter(Completer completer) {
        return console.addCompleter(completer);
    }

    public ConsoleReader getConsole() {
        return console;
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
