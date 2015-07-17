package josh.api;

import java.util.List;

import josh.impl.Range;

/**
 * Faz o parse de uma linha de comando
 */
public interface CommandParser {

    List<String> getTokens(CharSequence line);

    List<Range> getRanges(CharSequence line);

}
