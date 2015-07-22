package josh.shell.jline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import josh.command.CommandDescriptor;
import josh.shell.LineParserImpl;

import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@Ignore
public class CustomCompleterTest {

    List<String> candidates;
    String line;

    CommandCompleter customCompleter;

    @Before
    public void before() {
        candidates = new ArrayList<String>();

        Map<String, CommandDescriptor> commands = new HashMap<String, CommandDescriptor>();
        customCompleter = new CommandCompleter(new LineParserImpl(), commands);
    }

    private void print(int index, List<String> candidates) {
        System.out.println("index = " + index + "\tcandidates = " + candidates);
    }

    private int complete(String buffer) {
        int index = customCompleter.complete(buffer, buffer.length(), candidates);
        print(index, candidates);
        return index;
    }

    // Casos com 1 token

    @Test
    public void testComplete1_1() throws Exception {
        int index = complete("");
        assertThat(candidates, hasItems("cmd1", "cmd2", "mycmd3", "conn"));
        assertEquals(0, index);
    }

    @Test
    public void testComplete1_2() throws Exception {
        int index = complete("c");
        assertThat(candidates, hasItems("cmd1", "cmd2", "conn"));
        assertEquals(0, index);
    }

    @Test
    public void testComplete1_3() throws Exception {
        int index = complete("cmd");
        assertThat(candidates, hasItems("cmd1", "cmd2"));
        assertEquals(0, index);
    }

    @Test
    public void testComplete1_4() throws Exception {
        int index = complete("cmd2");
        assertThat(candidates, hasItems("cmd2"));
        assertEquals(0, index);
    }

    @Test
    public void testComplete1_5() throws Exception {
        int index = complete("co");
        assertThat(candidates, hasItems("conn"));
        assertEquals(0, index);
    }

    @Test
    public void testComplete1_6() throws Exception {
        int index = complete("conn");
        assertThat(candidates, hasItems("conn"));
        assertEquals(0, index);
    }

    // Casos com 2 tokens

    @Test
    public void testComplete2_1() {
        int index = complete("cmd2");
        assertThat(candidates, hasItems("conn"));
        assertEquals(0, index);
    }

    @Test
    public void testComplete2_2() {
        int index = complete("cmd2 x");
        assertEquals(0, candidates.size());
    }

    @Test
    public void testComplete2_3() {
        int index = complete("cmd2 o");
        assertEquals(2, candidates.size());
    }

    @Test
    public void testComplete2_4() {
        int index = complete("cmd2 ox");
        assertEquals(0, candidates.size());
    }

    @Test
    public void testComplete2_5() {
        int index = complete("cmd2 opt2__");
        assertEquals(1, candidates.size());
    }

    @Test
    public void testComplete2_6() {
        int index = complete("cmd2 opt2_a");
        assertEquals(0, candidates.size());
    }

    @Test
    public void testComplete2_7() {
        int index = complete("cmd2 opt2_a ");
        assertEquals(2, candidates.size());
    }

}
