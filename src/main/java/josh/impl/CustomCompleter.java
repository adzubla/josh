package josh.impl;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jline.console.completer.ArgumentCompleter;
import jline.console.completer.Completer;
import jline.console.completer.StringsCompleter;
import josh.api.CommandDescriptor;
import josh.api.CommandParser;

public class CustomCompleter implements Completer {
    private static final Logger LOG = LoggerFactory.getLogger(CustomCompleter.class);

    protected CommandParser parser;
    protected Map<String, CommandDescriptor> commands;
    protected StringsCompleter commandNameCompleter;

    public CustomCompleter(CommandParser parser, Map<String, CommandDescriptor> commands) {
        this.parser = parser;
        this.commands = commands;
        commandNameCompleter = new StringsCompleter(commands.keySet());
    }

    public int complete(final String buffer, final int cursor, final List candidates) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("---------------------------------------------------------------");
            LOG.debug("buffer = [{}]", buffer);
            String padding = "                                                                                           ".substring(0, cursor + 1);
            LOG.debug("cursor = {}^ {}", padding, cursor);
        }
        try {
            List<Range> ranges = parser.getRanges(buffer);
            LOG.debug("ranges = {}", ranges);
            if (ranges.isEmpty() || cursor <= ranges.get(0).end) {
                int complete = commandNameCompleter.complete(buffer, cursor, candidates);
                LOG.debug("candidates = {}", candidates);
                LOG.debug("complete = {}", complete);
                return complete;
            }
            else {
                Range range = ranges.get(0);
                String commandName = buffer.substring(range.start, range.end);
                CommandDescriptor descriptor = commands.get(commandName);
                if (descriptor != null) {
                    Collection<String> options = descriptor.getOptions();
                    if (options != null) {
                        LinkedList<String> args = new LinkedList<String>();
                        args.add(commandName);
                        args.addAll(options);
                        Completer completer = new ArgumentCompleter(new StringsCompleter(args));
                        int complete = completer.complete(buffer, cursor, candidates);
                        LOG.debug("candidates = {}", candidates);
                        LOG.debug("complete = {}", complete);
                        return complete;
                    }
                }
                return -1;
            }
        }
        catch (Exception e) {
            LOG.debug("Ignoring exception: " + e);
            e.printStackTrace();
        }
        return cursor;
    }

}
