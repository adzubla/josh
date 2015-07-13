package josh;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class CommandParserImpl implements CommandParser {

    public static final char SPACE_CHAR = ' ';
    public static final char TAB_CHAR = '\t';

    @Override
    public ParseResult parseLine(String line) {
        List<String> tokens = new ArrayList<String>();

        try {
            StringBuilder token = new StringBuilder();
            StringReader reader = new StringReader(line);
            int c;
            while ((c = reader.read()) != -1) {
                if (c == SPACE_CHAR || c == TAB_CHAR) {
                    if (token.length() > 0) {
                        tokens.add(token.toString());
                        token = new StringBuilder();
                    }
                }
                else {
                    token.append((char)c);
                }
            }
            if (token.length() > 0) {
                tokens.add(token.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        ParseResult parseResult = new ParseResult();
        parseResult.setCommandName(tokens.get(0));
        parseResult.setArguments(tokens.subList(1, tokens.size()));

        return parseResult;
    }

}