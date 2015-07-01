package josh;

/**
 * Faz o parse de uma linha de comando
 */
interface CommandParser {
    CommandDescriptor parseLine(String line);

    CommandDescriptor parseLine(String[] args);
}
