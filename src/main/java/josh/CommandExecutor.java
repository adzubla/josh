package josh;

/**
 * Executa comando
 */
interface CommandExecutor {
    CommandOutcome execute(CommandDescriptor cd, String line);

    CommandOutcome execute(CommandDescriptor cd, String[] args);
}
