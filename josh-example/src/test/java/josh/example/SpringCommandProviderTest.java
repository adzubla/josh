package josh.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.stereotype.Component;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import josh.command.CommandDescriptor;
import josh.command.CommandNotFound;
import josh.command.CommandOutcome;
import josh.command.jcommander.Executable;
import josh.example.spring.SpringCommandProvider;

public class SpringCommandProviderTest {

    private ApplicationContext ctx;

    @Before
    public void before() {
        StaticApplicationContext ctx = new StaticApplicationContext();
        ctx.registerSingleton("CommandTest", CommandTest.class);

        this.ctx = ctx;
    }

    @Test
    public void testRegisterCommandDescriptor() {
        SpringCommandProvider commandProvider = new SpringCommandProvider(ctx);
        commandProvider.initialize();

        Map<String, CommandDescriptor> commands = commandProvider.getCommands();

        assertEquals("Should register atl least 1 command", 1, commands.size());
        assertNotNull("Should register the command with the correct name", commands.get("command-test"));

        CommandDescriptor descriptor = commands.get("command-test");
        assertEquals("Wrong Command Description name", "command-test", descriptor.getCommandName());
        assertEquals("Wrong Command Description description", "Description Test 123",
                descriptor.getCommandDescription());
        assertEquals("Wrong Command Options size", 4, descriptor.getOptions().size());

        assertEquals("String option configured with the wrong class", String.class,
                descriptor.getOptions().get("--string-opt"));
        assertEquals("Integer option configured with the wrong class", Integer.class,
                descriptor.getOptions().get("--integer-opt"));
        assertEquals("Long option configured with the wrong class", Long.class,
                descriptor.getOptions().get("--long-opt"));
        assertNull("Option with arity=0 should have a null class", descriptor.getOptions().get("--arity-opt"));
    }

    @Test
    public void testExecuteCommand() throws Exception {
        SpringCommandProvider commandProvider = new SpringCommandProvider(ctx);
        commandProvider.initialize();

        CommandTest commandTest = (CommandTest)ctx.getBean("CommandTest");

        try {
            assertFalse("Command should not be executed before call execute method", commandTest.isCommandExecuted());
            commandProvider.execute(Arrays.asList("command-test", "--integer-opt", "5"));
            assertTrue("Command should be executed after calling the execute method", commandTest.isCommandExecuted());
        }
        catch (CommandNotFound commandNotFound) {
            Assert.fail();
        }
    }

    @Component
    @Parameters(commandNames = "command-test", commandDescription = "Description Test 123")
    public static class CommandTest implements Executable {

        private boolean commandExecuted = false;

        @Parameter(names = "--string-opt")
        private String string;

        @Parameter(names = "--integer-opt")
        private Integer integer;

        @Parameter(names = "--long-opt", description = "Long Opt")
        private Long aLong;

        @Parameter(names = "--arity-opt", arity = 0, description = "Long Opt")
        private String testArity0;

        @Override
        public CommandOutcome execute() {
            commandExecuted = true;
            return new CommandOutcome(1);
        }

        public boolean isCommandExecuted() {
            return commandExecuted;
        }
    }

}