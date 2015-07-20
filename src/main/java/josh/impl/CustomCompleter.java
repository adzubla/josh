package josh.impl;

import java.util.Collection;
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
                return commandNameCompleter.complete(buffer, cursor, candidates);
            }
            else {
                Range firstRange = ranges.get(0);
                String commandName = buffer.substring(firstRange.start, firstRange.end);
                CommandDescriptor descriptor = commands.get(commandName);

                if (descriptor != null) {
                    Collection<String> options = descriptor.getOptions();
                    if (options != null) {
                        Range cursorRange = findRange(cursor, ranges);
                        String sub = buffer.substring(cursorRange.start, Math.min(cursor, cursorRange.end));
                        LOG.debug("sub = [{}]", sub);
                        Completer completer = new ArgumentCompleter(new StringsCompleter(options));
                        int complete = completer.complete(sub, 0, candidates);
                        LOG.debug("candidates = {}", candidates);
                        LOG.debug("complete = {}", complete);
                        LOG.debug("complete+start = {}", cursorRange.start + complete);
                        return cursorRange.start + complete;
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

    protected Range findRange(int cursor, List<Range> ranges) {
        for (Range range : ranges) {
            if (range.start < cursor && cursor <= range.end) {
                return range;
            }
        }
        return new Range(cursor, cursor);
    }

}
