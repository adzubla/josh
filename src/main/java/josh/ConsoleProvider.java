package josh;

import java.util.List;

/**
 * Realiza as operações de input/output (classes separadas?)
 * - edição da linha
 * - print de cores ANSI
 * - entrada de password
 * - processa TAB
 */
// TextDevice
public interface ConsoleProvider {
    void configCompletion(List<CommandDescriptor> commands);

    void displayPrompt();

    void displayError(String message);

    void displayWarning(String message);

    void displayMessage(String message);

    String readLine();
}
