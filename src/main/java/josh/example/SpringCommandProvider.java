package josh.example;

import java.util.Collection;

import org.springframework.context.ApplicationContext;

import josh.command.jcommander.AbstractJCommanderProvider;
import josh.command.jcommander.Executable;

public class SpringCommandProvider extends AbstractJCommanderProvider {
    protected ApplicationContext ctx;

    public SpringCommandProvider(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    @Override
    protected Collection<Executable> findCommands() {
        return ctx.getBeansOfType(Executable.class).values();
    }

}
