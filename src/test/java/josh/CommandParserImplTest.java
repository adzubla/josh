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
    public void testParseLine1() throws Exception {
        line = "   my-command --opt1 arg1 123  \t   4 56789  --opt2 arg2   ";
        result = commandParser.parseLine(line);
        assertEquals("my-command", result.getCommandName());
        assertEquals("--opt1", result.getArguments().get(0));
        assertEquals("arg1", result.getArguments().get(1));
        assertEquals("123", result.getArguments().get(2));
        assertEquals("4", result.getArguments().get(3));
        assertEquals("56789", result.getArguments().get(4));
        assertEquals("--opt2", result.getArguments().get(5));
        assertEquals("arg2", result.getArguments().get(6));
    }

    @Test
    public void testParseLine2() throws Exception {
        line = "\"my command\" --opt 'Alo \"Mundo\"!'";
        result = commandParser.parseLine(line);
        assertEquals("my command", result.getCommandName());
        assertEquals("--opt", result.getArguments().get(0));
        assertEquals("Alo \"Mundo\"!", result.getArguments().get(1));
    }

    @Test
    public void testParseLine3() throws Exception {
        line = "'my command' --opt \"Alo 'Mundo'!\"";
        result = commandParser.parseLine(line);
        assertEquals("my command", result.getCommandName());
        assertEquals("--opt", result.getArguments().get(0));
        assertEquals("Alo 'Mundo'!", result.getArguments().get(1));
    }

    private void print(ParseResult parseResult) {
        System.out.println("Command: " + parseResult.getCommandName());
        System.out.println("Arguments:");
        for (String arg : parseResult.getArguments()) {
            System.out.println(arg);
        }
    }
}