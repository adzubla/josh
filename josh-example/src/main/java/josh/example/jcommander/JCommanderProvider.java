package josh.example.jcommander;

import java.util.ArrayList;
import java.util.Collection;

import josh.command.CommandNotFound;
import josh.command.jcommander.AbstractJCommanderProvider;
import josh.command.jcommander.Executable;

public class JCommanderProvider extends AbstractJCommanderProvider {
    @Override
    protected Collection<Executable> findCommands() {
        Collection<Executable> list = new ArrayList<Executable>();
        list.add(new EchoCommand());
        return list;
    }

    @Override
    protected Executable getNewCommand(Class<? extends Executable> bean) throws CommandNotFound {
        if (EchoCommand.class.isAssignableFrom(bean)) {
            return new EchoCommand();
        }
        throw new CommandNotFound(bean.getName());
    }
}
