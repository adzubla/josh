package josh.shell;

/**
 * Realiza as operações de input/output (classes separadas?)
 * - edição da linha
 * - print de cores ANSI
 * - entrada de password
 * - processa TAB
 */
public interface ConsoleProvider {

    void initialize();

    void destroy();

    void setPrompt(String prompt);

    void displayPrompt();

    void displayError(String message);

    void displayWarning(String message);

    void displayInfo(String message);

    void println(CharSequence charSequence);

    void print(CharSequence charSequence);

    String readLine();

}
