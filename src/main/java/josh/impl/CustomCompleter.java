package josh.impl;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jline.console.completer.ArgumentCompleter;
import jline.console.completer.Completer;
import jline.console.completer.EnumCompleter;
import jline.console.completer.FileNameCompleter;
import jline.console.completer.NullCompleter;
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
                    Map<String, Class> options = descriptor.getOptions();
                    LOG.debug("options = {}", options);
                    if (options != null && !options.isEmpty()) {

                        Range cursorToken = null;
                        int cursorIndex;

                        for (cursorIndex = 0; cursorIndex < ranges.size(); cursorIndex++) {
                            Range r = ranges.get(cursorIndex);
                            if (r.start < cursor && cursor <= r.end) {
                                cursorToken = r;
                                break;
                            }
                        }
                        if (cursorToken == null) {
                            cursorToken = new Range(cursor, cursor);
                        }
                        LOG.debug("cursorToken = {}", cursorToken);

                        Class type = null;
                        if (cursorIndex > 1) {
                            Range prevToken = ranges.get(cursorIndex - 1);
                            String tokenValue = buffer.substring(prevToken.start, prevToken.end);
                            LOG.debug("tokenValue = {}", tokenValue);
                            type = options.get(tokenValue);
                            LOG.debug("type = {}", type);
                        }

                        String sub = buffer.substring(cursorToken.start, Math.min(cursor, cursorToken.end));
                        LOG.debug("sub = [{}]", sub);

                        Completer completer = getCompleter(options, type);
                        int complete = completer.complete(sub, 0, candidates);
                        LOG.debug("candidates = {}", candidates);
                        LOG.debug("complete = {}", complete);
                        LOG.debug("complete+start = {}", cursorToken.start + complete);
                        return cursorToken.start + complete;
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

    private Completer getCompleter(Map<String, Class> options, Class type) {
        Completer completer = null;

        if (type == null) {
            completer = new ArgumentCompleter(new StringsCompleter(options.keySet()));
        }
        else if (File.class.isAssignableFrom(type)) {
            completer = new FileNameCompleter();
        }
        else if (type.isEnum()) {
            completer = new EnumCompleter(type);
        }
        else {
            completer = new NullCompleter();
            LOG.debug("No completer found for type {}", type);
        }
        return completer;
    }

    protected Range findRange(int cursor, List<Range> ranges) {
        for (int i = 0; i < ranges.size(); i++) {
            Range range = ranges.get(i);
            if (range.start < cursor && cursor <= range.end) {
                return range;
            }
        }
        return new Range(cursor, cursor);
    }

}
