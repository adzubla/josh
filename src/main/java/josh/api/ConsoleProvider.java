package josh.api;

/**
 * Realiza as operações de input/output (classes separadas?)
 * - edição da linha
 * - print de cores ANSI
 * - entrada de password
 * - processa TAB
 */
// TextDevice
public interface ConsoleProvider {

    void initialize();

    void destroy();

    void setPrompt(String prompt);

    void displayPrompt();

    void displayError(String message);

    void displayWarning(String message);

    void displayInfo(String message);

    String readLine();
}
