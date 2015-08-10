package josh.example.jcommander;

import java.util.ArrayList;
import java.util.Collection;

import josh.command.CommandNotFound;
import josh.command.jcommander.AbstractJCommanderProvider;
import josh.command.jcommander.Executable;
import josh.shell.Shell;
import josh.shell.ShellAwareCommandProvider;

public class JCommanderProvider extends AbstractJCommanderProvider implements ShellAwareCommandProvider {

    protected Shell shell;

    @Override
    protected Collection<Executable> findCommands() {
        Collection<Executable> list = new ArrayList<Executable>();
        list.add(new EchoCommand());
        list.add(new PromptCommand(shell.getConsoleProvider()));
        return list;
    }

    @Override
    protected Executable getNewCommand(Class<? extends Executable> bean) throws CommandNotFound {
        if (EchoCommand.class.isAssignableFrom(bean)) {
            return new EchoCommand();
        } else if (PromptCommand.class.isAssignableFrom(bean)) {
            return new PromptCommand(shell.getConsoleProvider());
        }
        throw new CommandNotFound(bean.getName());
    }

    @Override
    public void setShell(Shell shell) {
        this.shell = shell;
    }
}
