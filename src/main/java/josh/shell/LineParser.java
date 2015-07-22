package josh.shell;

import java.util.List;

/**
 * Faz o parse de uma linha de comando
 */
public interface LineParser {

    List<String> getTokens(CharSequence line);

    List<Range> getRanges(CharSequence line);

}
