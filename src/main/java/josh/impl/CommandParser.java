package josh.impl;

import java.util.List;

/**
 * Faz o parse de uma linha de comando
 */
public interface CommandParser {
    List<String> parseLine(String line);
}
