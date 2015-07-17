package josh;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import josh.impl.CommandParserImpl;

import static org.junit.Assert.assertEquals;

public class CommandParserImplTest {

    CommandParserImpl commandParser = new CommandParserImpl();
    String line;
    List<String> result;

    @Before
    public void before() {
        commandParser = new CommandParserImpl();
    }

    @Test
    public void testParseLine0() throws Exception {
        line = "cmd";
        result = commandParser.getTokens(line);
        assertEquals("cmd", result.get(0));

        line = " cmd";
        result = commandParser.getTokens(line);
        assertEquals("cmd", result.get(0));

        line = "cmd ";
        result = commandParser.getTokens(line);
        assertEquals("cmd", result.get(0));

        line = " cmd ";
        result = commandParser.getTokens(line);
        assertEquals("cmd", result.get(0));
    }

    @Test
    public void testParseLine1() throws Exception {
        line = "my-command --opt1 arg1 123  \t   4\t56789  --opt2 arg2";
        result = commandParser.getTokens(line);
        assertEquals("my-command", result.get(0));
        assertEquals("--opt1", result.get(1));
        assertEquals("arg1", result.get(2));
        assertEquals("123", result.get(3));
        assertEquals("4", result.get(4));
        assertEquals("56789", result.get(5));
        assertEquals("--opt2", result.get(6));
        assertEquals("arg2", result.get(7));
        assertEquals(8, result.size());
    }

    @Test(expected = RuntimeException.class)
    public void testParseLineDoubleQuoteError() throws Exception {
        line = "xxx\"xxx";
        result = commandParser.getTokens(line);
    }

    @Test
    public void testParseLineDoubleQuote1() throws Exception {
        line = "\"x y\"";
        result = commandParser.getTokens(line);
        assertEquals("x y", result.get(0));

        line = " \"x y\"";
        result = commandParser.getTokens(line);
        assertEquals("x y", result.get(0));

        line = "\"x y\" ";
        result = commandParser.getTokens(line);
        assertEquals("x y", result.get(0));

        line = " \"x y\" ";
        result = commandParser.getTokens(line);
        assertEquals("x y", result.get(0));
    }

    @Test
    public void testParseLineDoubleQuote2() throws Exception {
        line = "000 \"xxx yyy\" \" a b \"  zzz";
        result = commandParser.getTokens(line);
        assertEquals("000", result.get(0));
        assertEquals("xxx yyy", result.get(1));
        assertEquals(" a b ", result.get(2));
        assertEquals("zzz", result.get(3));
    }

    @Test
    public void testParseLineEscapedDoubleQuoted() throws Exception {
        line = "\"xxx \\\"! @\\\" yyy\"";
        result = commandParser.getTokens(line);
        assertEquals("xxx \"! @\" yyy", result.get(0));

        line = "xy\\\"zw";
        result = commandParser.getTokens(line);
        assertEquals("xy\"zw", result.get(0));

        line = "    cmd --opt \"abc 123\" \"hello \\\"world\\\"!\"    ";
        result = commandParser.getTokens(line);
        assertEquals("cmd", result.get(0));
        assertEquals("--opt", result.get(1));
        assertEquals("abc 123", result.get(2));
        assertEquals("hello \"world\"!", result.get(3));
        assertEquals(4, result.size());
    }

}
