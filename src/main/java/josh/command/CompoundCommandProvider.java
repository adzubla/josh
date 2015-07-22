package josh.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Permite juntar comandos de várias fontes
 */
public class CompoundCommandProvider implements CommandProvider {

    private List<CommandProvider> providers = new ArrayList<CommandProvider>();

    public CompoundCommandProvider(CommandProvider... providers) {
        this.providers = Arrays.asList(providers);
    }

    public void addCommandProvider(CommandProvider provider) {
        this.providers.add(provider);
    }

    protected List<CommandProvider> getProviders() {
        // Always use the last implementation of any command
        Collections.reverse(this.providers);
        return this.providers;
    }

    @Override
    public void initialize() {
        for (CommandProvider provider : getProviders()) {
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
        for (CommandProvider provider : getProviders()) {
            if (provider.isValidCommand(commandName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public HelpFormatter getHelpFormatter(String commandName) {
        for (CommandProvider provider : getProviders()) {
            if (provider.isValidCommand(commandName)) {
                return provider.getHelpFormatter(commandName);
            }
        }
        return null;
    }

    @Override
    public CommandOutcome execute(List<String> args) throws CommandNotFound {
        String commandName = args.get(0);
        for (CommandProvider provider : getProviders()) {
            if (provider.isValidCommand(commandName)) {
                return provider.execute(args);
            }
        }
        throw new CommandNotFound(commandName);
    }

    @Override
    public Map<String, CommandDescriptor> getCommands() {
        Map<String, CommandDescriptor> commands = new HashMap<String, CommandDescriptor>();
        for (CommandProvider provider : getProviders()) {
            commands.putAll(provider.getCommands());
        }
        return commands;
    }
}
