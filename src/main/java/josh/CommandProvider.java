package josh;

import java.util.List;

/**
 * Carrega os comandos disponíveis
 * - embedded
 * - contexto Spring
 * - diretório com .jar
 * - diretório com .py
 * - diretório com .groovy
 */
public interface CommandProvider {
    List<CommandDescriptor> findCommands();

    HelpFormatter getHelpFormatter();

    CommandOutcome execute(String line) throws CommandNotFound;

}
