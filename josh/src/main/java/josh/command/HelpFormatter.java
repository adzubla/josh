package josh.command;

/**
 * Format help message of a command.
 */
public interface HelpFormatter {

    String formatHelpMessage(CommandDescriptor cd);
}
