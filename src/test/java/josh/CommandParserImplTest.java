package josh;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CommandParserImplTest {

    CommandParserImpl commandParser = new CommandParserImpl();
    String line;
    ParseResult result;

    @Before
    public void before() {
        commandParser = new CommandParserImpl();
    }

    @Test
    public void testParseLine0() throws Exception {
        line = "cmd";
        result = commandParser.parseLine(line);
        assertEquals("cmd", result.getCommandName());
    }

    @Test
    public void testParseLine1() throws Exception {
        line = "my-command --opt1 arg1 123  \t   4\t56789  --opt2 arg2";
        result = commandParser.parseLine(line);
        assertEquals("my-command", result.getCommandName());
        assertEquals("--opt1", result.getArguments().get(0));
        assertEquals("arg1", result.getArguments().get(1));
        assertEquals("123", result.getArguments().get(2));
        assertEquals("4", result.getArguments().get(3));
        assertEquals("56789", result.getArguments().get(4));
        assertEquals("--opt2", result.getArguments().get(5));
        assertEquals("arg2", result.getArguments().get(6));
        assertEquals(7, result.getArguments().size());
    }

    @Test(expected = RuntimeException.class)
    public void testParseLineDoubleQuoteError() throws Exception {
        line = "xxx\"xxx";
        result = commandParser.parseLine(line);
    }

    @Test
    public void testParseLineDoubleQuote1() throws Exception {
        line = "\"xxx yyy\"";
        result = commandParser.parseLine(line);
        assertEquals("xxx yyy", result.getCommandName());

        line = "\"xxx \\\"! @\\\" yyy\"";
        result = commandParser.parseLine(line);
        assertEquals("xxx \"! @\" yyy", result.getCommandName());

        line = "xy\\\"zw";
        result = commandParser.parseLine(line);
        assertEquals("xy\"zw", result.getCommandName());
    }

    @Test
    public void testParseLineDoubleQuote2() throws Exception {
        line = "    cmd --opt \"abc 123\" \"hello \\\"world\\\"!\"    ";
        result = commandParser.parseLine(line);
        assertEquals("cmd", result.getCommandName());
        assertEquals("--opt", result.getArguments().get(0));
        assertEquals("abc 123", result.getArguments().get(1));
        assertEquals("hello \"world\"!", result.getArguments().get(2));
        assertEquals(3, result.getArguments().size());
    }

    private void print(ParseResult parseResult) {
        System.out.println("Command: " + parseResult.getCommandName());
        System.out.println("Arguments:");
        for (String arg : parseResult.getArguments()) {
            System.out.println(arg);
        }
    }

}
