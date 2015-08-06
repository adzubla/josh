package josh.command.jcommander;

import com.beust.jcommander.Parameters;

import josh.command.CommandOutcome;

/**
 * Interface to mark a JCommander command <br /> The class implementing this interface must also use the JCommander
 * {@link Parameters} annotation and supply the {@link Parameters#commandNames()} option
 *
 * @see Parameters
 */
public interface Executable {

    /**
     * Execute this command
     *
     * @return The outcome of this execution
     */
    CommandOutcome execute();

}
