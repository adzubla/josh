package josh.api;

import java.util.List;
import java.util.Map;

/**
 * Carrega os comandos disponíveis
 * - embedded
 * - contexto Spring
 * - diretório com .jar
 * - diretório com .py
 * - diretório com .groovy
 */
public interface CommandProvider {

    boolean isValidCommand(String commandName);

    HelpFormatter getHelpFormatter(String commandName);

    CommandOutcome execute(List<String> args) throws CommandNotFound;

    Map<String, CommandDescriptor> getCommands();

}
