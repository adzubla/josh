package josh.example.spring;

import java.util.Collection;

import org.springframework.context.ApplicationContext;

import josh.command.CommandNotFound;
import josh.command.jcommander.AbstractJCommanderProvider;
import josh.command.jcommander.Executable;

/**
 * Example of a CommandProvider that has its commands implemented as Spring beans (and JCommander)
 */
public class SpringCommandProvider extends AbstractJCommanderProvider {
    protected ApplicationContext ctx;

    public SpringCommandProvider(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    @Override
    protected Collection<Executable> findCommands() {
        return ctx.getBeansOfType(Executable.class).values();
    }

    @Override
    protected Executable getNewCommand(Class<? extends Executable> bean) throws CommandNotFound {
        return ctx.getBean(bean);
    }

}
