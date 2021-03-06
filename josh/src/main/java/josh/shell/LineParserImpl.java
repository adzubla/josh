package josh.shell;

import java.util.ArrayList;
import java.util.List;

/**
 * Parse a command line into tokens separated by white spaces, and using double quotes to build tokens that can have
 * spaces inside.
 */
public class LineParserImpl implements LineParser {

    protected static final char SPACE_CHAR = ' ';
    protected static final char TAB_CHAR = '\t';
    protected static final char DOUBLE_QUOTE_CHAR = '"';
    protected static final char BACKSLASH_CHAR = '\\';

    @Override
    public List<String> getTokens(CharSequence line) {
        List<Range> ranges = getRanges(line);
        List<String> tokens = new ArrayList<String>(ranges.size());

        for (Range range : ranges) {
            String token = line.subSequence(range.start, range.end).toString();
            tokens.add(token.replace("\\\"", "\""));
        }

        return tokens;
    }

    @Override
    public List<Range> getRanges(CharSequence line) {
        List<Range> ranges = new ArrayList<Range>();

        int tokenStart = 0;
        int tokenEnd = 0;
        boolean insideDoubleQuote = false;

        for (int i = 0; i < line.length(); i++) {
            int c = line.charAt(i);
            if (c == DOUBLE_QUOTE_CHAR) {
                if (insideDoubleQuote) {
                    tokenEnd++;
                    insideDoubleQuote = false;
                }
                else {
                    tokenStart++;
                    insideDoubleQuote = true;
                }
            }
            else if (!insideDoubleQuote && c == SPACE_CHAR || c == TAB_CHAR) {
                if (tokenEnd > tokenStart) {
                    ranges.add(new Range(tokenStart, tokenEnd));
                }
                tokenStart = i + 1;
                tokenEnd = i + 1;
            }
            else {
                if (c == BACKSLASH_CHAR && line.charAt(i + 1) == DOUBLE_QUOTE_CHAR) {
                    i++;
                    tokenEnd++;
                }
                tokenEnd++;
            }
        }

        if (insideDoubleQuote) {
            throw new RuntimeException("Unclosed quote.");
        }

        if (tokenEnd > tokenStart) {
            ranges.add(new Range(tokenStart, tokenEnd));
        }

        return ranges;
    }

}
