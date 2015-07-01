package josh;

import java.util.List;

/**
 * Realiza as operações de input/output (classes separadas?)
 * - edição da linha
 * - print de cores ANSI
 * - entrada de password
 * - processa TAB
 */
interface ConsoleProvider {
    void configCompletion(List<CommandDescriptor> commands);
}
