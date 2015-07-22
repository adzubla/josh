package josh.example;

import java.util.Collection;

import org.springframework.context.ApplicationContext;

import josh.command.jcommander.AbstractJCommanderProvider;
import josh.command.jcommander.Command;

public class SpringCommandProvider extends AbstractJCommanderProvider {
    protected ApplicationContext ctx;

    public SpringCommandProvider(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    @Override
    protected Collection<Command> findCommands() {
        return ctx.getBeansOfType(Command.class).values();
    }

}
