package josh;

/**
 * Carrega os comandos disponíveis
 * - embedded
 * - contexto Spring
 * - diretório com .jar
 * - diretório com .py
 * - diretório com .groovy
 */
public interface CommandProvider {
    void init();

    HelpFormatter getHelpFormatter();

    CommandOutcome execute(String line) throws CommandNotFound;

}
