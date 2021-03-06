package josh.command.jcommander;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterDescription;
import com.beust.jcommander.Parameterized;

import josh.command.CommandDescriptor;
import josh.command.CommandNotFound;
import josh.command.CommandOutcome;
import josh.command.CommandProvider;
import josh.command.HelpFormatter;

/**
 * Abstract CommandProvider that grab commands options using JCommander (http://jcommander.org/)
 */
public abstract class AbstractJCommanderProvider implements CommandProvider {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractJCommanderProvider.class);

    protected JCommander commands;

    protected Map<String, CommandDescriptor> commandDescriptors;

    protected abstract Collection<Executable> findCommands();

    protected abstract Executable getNewCommand(Class<? extends Executable> bean) throws CommandNotFound;

    protected Executable actualCommand;

    @Override
    public void initialize() {
        commands = new JCommander();

        for (Executable executable : findCommands()) {
            commands.addCommand(executable);
        }

        commandDescriptors = new HashMap<String, CommandDescriptor>();

        for (Map.Entry<String, JCommander> entry : commands.getCommands().entrySet()) {
            String commandName = entry.getKey();
            JCommander actualJCommander = entry.getValue();

            CommandDescriptor commandDescriptor = new CommandDescriptor();
            commandDescriptor.setCommandName(commandName);
            commandDescriptor.setCommandDescription(commands.getCommandDescription(commandName));

            Map<String, Class> options = new HashMap<String, Class>();
            for (ParameterDescription parameterDescription : actualJCommander.getParameters()) {
                Class type = getTypeForParameter(parameterDescription.getParameterized(), parameterDescription);
                for (String name : parameterDescription.getNames().split(",")) {
                    options.put(name.trim(), type);
                }
            }

            commandDescriptor.setOptions(options);

            commandDescriptors.put(commandName, commandDescriptor);
        }
        LOG.debug("commandDescriptors = {}", commandDescriptors);
    }

    protected Class getTypeForParameter(Parameterized parameterized, ParameterDescription parameterDescription) {
        Class type = parameterized.getType();
        if (parameterDescription.getParameter().arity() == 0) {
            // If the parameter has a 0 arity, there is no class to convert
            type = null;
        }
        return type;
    }

    @Override
    public void destroy() {
        finalizeActualCommand();
    }

    @Override
    public boolean isValidCommand(String commandName) {
        return commands.getCommands().containsKey(commandName);
    }

    @Override
    public HelpFormatter getHelpFormatter(final String commandName) {
        return new HelpFormatter() {
            @Override
            public String formatHelpMessage(CommandDescriptor cd) {
                StringBuilder out = new StringBuilder();
                commands.usage(cd.getCommandName(), out);
                return out.toString();
            }
        };
    }

    @Override
    public CommandOutcome execute(List<String> tokens) throws CommandNotFound {
        String name = tokens.get(0);
        List<String> arguments = tokens.subList(1, tokens.size());

        JCommander jCommander = this.findCommand(name);
        if (jCommander == null) {
            throw new CommandNotFound(name);
        }

        try {
            Executable executableBean = (Executable)jCommander.getObjects().get(0);
            Executable executable = getNewCommand(executableBean.getClass());
            this.actualCommand = executable;
            new JCommander(executable).parse(arguments.toArray(new String[arguments.size()]));

            if (executable instanceof ExecutableWithLifecycle) {
                ExecutableWithLifecycle executableWithLifecycle = (ExecutableWithLifecycle)executable;
                executableWithLifecycle.initialize();
                CommandOutcome outcome = executableWithLifecycle.execute();
                finalizeActualCommand();
                return outcome;
            }
            else {
                return executable.execute();
            }
        }
        finally {
            this.actualCommand = null;
        }
    }

    @Override
    public Map<String, CommandDescriptor> getCommands() {
        if (commandDescriptors == null) {
            return null;
        }
        return new HashMap<String, CommandDescriptor>(commandDescriptors);
    }

    protected JCommander findCommand(String commandName) {
        return commands.getCommands().get(commandName);
    }

    protected void finalizeActualCommand() {
        if (this.actualCommand != null && this.actualCommand instanceof ExecutableWithLifecycle) {
            ((ExecutableWithLifecycle)this.actualCommand).finalizeCommand();
        }
    }

}
