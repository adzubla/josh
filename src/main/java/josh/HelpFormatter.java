package josh;

/**
 * Faz a formatação do help de um comando
 */
interface HelpFormatter {
    String formatHelpMessage(CommandDescriptor cd);
}
