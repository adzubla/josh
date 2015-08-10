package josh.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import josh.shell.Shell;
import josh.shell.ShellAwareCommandProvider;

/**
 * Aggregate commands from different sources in one CommandProvider
 */
public class CompoundCommandProvider implements CommandProvider, ShellAwareCommandProvider {

    protected List<CommandProvider> providers = new ArrayList<CommandProvider>();

    public CompoundCommandProvider(CommandProvider... providers) {
        this.providers = new LinkedList<CommandProvider>(Arrays.asList(providers));
    }

    public void addCommandProvider(CommandProvider provider) {
        this.providers.add(provider);
    }

    @Override
    public void initialize() {
        for (int i = providers.size() - 1; i >= 0; i--) {
            CommandProvider provider = providers.get(i);
            provider.initialize();
        }
    }

    @Override
    public void destroy() {
        for (CommandProvider provider : providers) {
            provider.destroy();
        }
    }

    @Override
    public boolean isValidCommand(String commandName) {
        for (int i = providers.size() - 1; i >= 0; i--) {
            CommandProvider provider = providers.get(i);
            if (provider.isValidCommand(commandName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public HelpFormatter getHelpFormatter(String commandName) {
        for (int i = providers.size() - 1; i >= 0; i--) {
            CommandProvider provider = providers.get(i);
            if (provider.isValidCommand(commandName)) {
                return provider.getHelpFormatter(commandName);
            }
        }
        return null;
    }

    @Override
    public CommandOutcome execute(List<String> args) throws CommandNotFound {
        String commandName = args.get(0);
        for (int i = providers.size() - 1; i >= 0; i--) {
            CommandProvider provider = providers.get(i);
            if (provider.isValidCommand(commandName)) {
                return provider.execute(args);
            }
        }
        throw new CommandNotFound(commandName);
    }

    @Override
    public Map<String, CommandDescriptor> getCommands() {
        Map<String, CommandDescriptor> commands = new TreeMap<String, CommandDescriptor>();
        for (CommandProvider provider : providers) {
            commands.putAll(provider.getCommands());
        }
        return commands;
    }

    @Override
    public void setShell(Shell shell) {
        for (CommandProvider provider : providers) {
            if (provider instanceof ShellAwareCommandProvider) {
                ((ShellAwareCommandProvider)provider).setShell(shell);
            }
        }
    }
}
