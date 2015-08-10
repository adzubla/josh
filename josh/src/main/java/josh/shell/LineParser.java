package josh.shell;

import java.util.List;

/**
 * Parse a command line
 */
public interface LineParser {

    List<String> getTokens(CharSequence line);

    List<Range> getRanges(CharSequence line);

}
