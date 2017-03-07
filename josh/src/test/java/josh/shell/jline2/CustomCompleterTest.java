package josh.shell.jline2;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import josh.command.CommandDescriptor;
import josh.command.CommandNotFound;
import josh.command.CommandOutcome;
import josh.command.CommandProvider;
import josh.command.HelpFormatter;
import josh.shell.LineParserImpl;

public class CustomCompleterTest {

    List<String> candidates;

    CommandCompleter customCompleter;

    @Before
    public void before() {
        candidates = new ArrayList<String>();

        CommandProvider commandProvider = new CommandProvider() {
            Map<String, CommandDescriptor> commands = new HashMap<String, CommandDescriptor>();

            @Override
            public void initialize() {
                addCommand("cmd1", null);

                Map<String, Class> options = new HashMap<String, Class>();
                options.put("opt1", null);
                options.put("opt2_a", String.class);
                options.put("opt2__a", String.class);
                addCommand("cmd2", options);

                addCommand("mycmd3", null);

                addCommand("conn", null);
            }

            @Override
            public void destroy() {
            }

            @Override
            public boolean isValidCommand(String commandName) {
                return false;
            }

            @Override
            public HelpFormatter getHelpFormatter(String commandName) {
                return null;
            }

            @Override
            public CommandOutcome execute(List<String> args) throws CommandNotFound {
                return null;
            }

            @Override
            public Map<String, CommandDescriptor> getCommands() {
                return commands;
            }

            private void addCommand(String name, Map<String, Class> options) {
                CommandDescriptor descriptor = new CommandDescriptor();
                descriptor.setCommandName(name);
                descriptor.setOptions(options);
                commands.put(descriptor.getCommandName(), descriptor);
            }
        };
        commandProvider.initialize();

        customCompleter = new CommandCompleter(new LineParserImpl(), commandProvider);
    }

    private void print(int index, List<String> candidates) {
        System.out.println("index = " + index + "\tcandidates = " + candidates);
    }

    private void complete(String buffer) {
        int index = customCompleter.complete(buffer, buffer.length(), candidates);
        print(index, candidates);
    }

    // Casos com 1 token

    @Test
    public void testComplete1_1() throws Exception {
        complete("");
        assertThat(candidates, hasItems("cmd1", "cmd2", "mycmd3", "conn"));
    }

    @Test
    public void testComplete1_2() throws Exception {
        complete("c");
        assertThat(candidates, hasItems("cmd1", "cmd2", "conn"));
    }

    @Test
    public void testComplete1_3() throws Exception {
        complete("cmd");
        assertThat(candidates, hasItems("cmd1", "cmd2"));
    }

    @Test
    public void testComplete1_4() throws Exception {
        complete("cmd2");
        assertThat(candidates, hasItems("cmd2"));
    }

    @Test
    public void testComplete1_5() throws Exception {
        complete("co");
        assertThat(candidates, hasItems("conn"));
    }

    @Test
    public void testComplete1_6() throws Exception {
        complete("conn");
        assertThat(candidates, hasItems("conn"));
    }

    // Casos com 2 tokens

    @Test
    public void testComplete2_1() {
        complete("cmd2");
        assertThat(candidates, hasItems("cmd2"));
    }

    @Test
    public void testComplete2_2() {
        complete("cmd2 x");
        assertTrue(candidates.isEmpty());
    }

    @Test
    public void testComplete2_3() {
        complete("cmd2 o");
        assertThat(candidates, hasItems("opt1", "opt2_a", "opt2__a"));
    }

    @Test
    public void testComplete2_4() {
        complete("cmd2 ox");
        assertTrue(candidates.isEmpty());
    }

    @Test
    public void testComplete2_5() {
        complete("cmd2 opt2_");
        assertThat(candidates, hasItems("opt2_a", "opt2__a"));
    }

    @Test
    public void testComplete2_6() {
        complete("cmd2 opt2_a");
        assertThat(candidates, hasItems("opt2_a"));
    }

    // Option values

    @Test
    public void testComplete3_1() {
        complete("cmd2 opt1 ");
        assertThat(candidates, hasItems("opt1", "opt2_a", "opt2__a"));
    }

    @Test
    public void testComplete3_2() {
        complete("cmd2 opt1 opt2_");
        assertThat(candidates, hasItems("opt2_a", "opt2__a"));
    }

    @Test
    public void testComplete3_3() {
        complete("cmd2 opt1 opt2_a ");
        assertTrue(candidates.isEmpty());
    }

    @Test
    public void testComplete3_4() {
        complete("cmd2 opt1 opt2_a xx");
        assertTrue(candidates.isEmpty());
    }

    @Test
    public void testComplete3_5() {
        complete("cmd2 opt1 opt2_a xx ");
        assertThat(candidates, hasItems("opt1", "opt2_a", "opt2__a"));
    }

}
