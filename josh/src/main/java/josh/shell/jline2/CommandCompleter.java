package josh.shell.jline2;

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
import josh.command.CommandDescriptor;
import josh.command.CommandProvider;
import josh.shell.LineParser;
import josh.shell.Range;

public class CommandCompleter implements Completer {
    private static final Logger LOG = LoggerFactory.getLogger(CommandCompleter.class);

    protected LineParser parser;
    protected CommandProvider commandProvider;

    public CommandCompleter(LineParser parser, CommandProvider commandProvider) {
        this.parser = parser;
        this.commandProvider = commandProvider;
    }

    public int complete(String buffer, int cursor, List candidates) {
        LOG.debug("cursor = {} buffer = [{}]", cursor, buffer);

        StringsCompleter commandNameCompleter = new StringsCompleter(commandProvider.getCommands().keySet());

        while (buffer.startsWith(" ")) {
            buffer = buffer.replaceFirst("^ ", "");
            cursor--;
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
                CommandDescriptor descriptor = commandProvider.getCommands().get(commandName);

                if (descriptor != null) {
                    Map<String, Class> options = descriptor.getOptions();
                    if (options != null && !options.isEmpty()) {

                        Range rangeUnderCursor = null;
                        int rangeIndexUnderCursor;

                        for (rangeIndexUnderCursor = 0; rangeIndexUnderCursor < ranges.size(); rangeIndexUnderCursor++) {
                            Range r = ranges.get(rangeIndexUnderCursor);
                            if (r.start < cursor && cursor <= r.end) {
                                rangeUnderCursor = r;
                                break;
                            }
                        }
                        if (rangeUnderCursor == null) {
                            rangeUnderCursor = new Range(cursor, cursor);
                        }
                        LOG.debug("rangeUnderCursor = {} index = {}", rangeUnderCursor, rangeIndexUnderCursor);

                        Class classUnderCursor = null;
                        if (rangeIndexUnderCursor > 1) {
                            Range prevRange = ranges.get(rangeIndexUnderCursor - 1);
                            String prevToken = buffer.substring(prevRange.start, prevRange.end);
                            LOG.debug("prevToken = {}", prevToken);
                            classUnderCursor = options.get(prevToken);
                            LOG.debug("classUnderCursor = {}", classUnderCursor);
                        }

                        String tokenUnderCursor = buffer.substring(rangeUnderCursor.start, Math.min(cursor, rangeUnderCursor.end));
                        LOG.debug("tokenUnderCursor = [{}]", tokenUnderCursor);

                        Completer completer = getCompleter(options, classUnderCursor);
                        int complete = completer.complete(tokenUnderCursor, 0, candidates);
                        LOG.debug("candidates = {}", candidates);
                        LOG.debug("complete = {}", complete);
                        return rangeUnderCursor.start + complete;
                    }
                }
                return -1;
            }
        }
        catch (Exception e) {
            LOG.debug("Ignoring exception...", e);
        }
        return cursor;
    }

    protected Completer getCompleter(Map<String, Class> options, Class type) {
        Completer completer;
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
        }
        return completer;
    }

}
