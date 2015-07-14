package josh.api;

/**
 * Faz a formatação do help de um comando
 */
public interface HelpFormatter {

    String formatHelpMessage(CommandDescriptor cd);
}
