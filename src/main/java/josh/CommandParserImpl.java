package josh;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class CommandParserImpl implements CommandParser {

    @Override
    public ParseResult parseLine(String line) {

        ParseResult result = new ParseResult();
        StringTokenizer tokenizer = new StringTokenizer(line);

        if (tokenizer.hasMoreTokens()) {
            result.setCommandName(tokenizer.nextToken());

            List<String> arguments = new ArrayList<String>();
            result.setArguments(arguments);

            while (tokenizer.hasMoreTokens()) {
                arguments.add(tokenizer.nextToken());
            }
        }

        return result;
    }

}