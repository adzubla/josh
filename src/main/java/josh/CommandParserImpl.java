package josh;

import java.util.ArrayList;
import java.util.List;

public class CommandParserImpl implements CommandParser {

    public static final char SPACE_CHAR = ' ';
    public static final char TAB_CHAR = '\t';
    public static final char DOUBLE_QUOTE_CHAR = '"';
    public static final char BACKSLASH_CHAR = '\\';

    @Override
    public ParseResult parseLine(String line) {
        List<String> tokens = new ArrayList<String>();

        StringBuilder token = new StringBuilder();
        boolean insideDoubleQuote = false;
        for (int i = 0; i < line.length(); i++) {
            int c = line.charAt(i);
            if (c == DOUBLE_QUOTE_CHAR) {
                insideDoubleQuote = !insideDoubleQuote;
            } else if (!insideDoubleQuote && c == SPACE_CHAR || c == TAB_CHAR) {
                if (token.length() > 0) {
                    tokens.add(token.toString());
                    token = new StringBuilder();
                }
            } else {
                if (c == BACKSLASH_CHAR && line.charAt(i + 1) == DOUBLE_QUOTE_CHAR) {
                    c = DOUBLE_QUOTE_CHAR;
                    i++;
                }
                token.append((char) c);
            }
        }
        if (insideDoubleQuote) {
            throw new RuntimeException("Unclosed quote.");
        }

        if (token.length() > 0) {
            tokens.add(token.toString());
        }

        ParseResult parseResult = new ParseResult();
        parseResult.setCommandName(tokens.get(0));
        parseResult.setArguments(tokens.subList(1, tokens.size()));

        return parseResult;
    }

}