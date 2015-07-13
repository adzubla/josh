package josh;

/**
 * Executa comando
 */
public interface CommandExecutor {
    CommandOutcome execute(String line) throws CommandNotFound;
}
