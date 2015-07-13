package josh;

/**
 * Faz o parse de uma linha de comando
 */
public interface CommandParser {
    ParseResult parseLine(String line);
}
