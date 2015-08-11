
# josh - Java Oriented Shell

This is a little framework to help the creation of a command line shell console in Java.

What should be configurable:

* Where to find commands, and how to run them
    * from beans in Spring application context
    * from jars in plugins directory
    * from groovy or jython files
    * embedded in my source code
* How to parse commands and options
    * JCommander (http://jcommander.org/)
    * Commons CLI (https://commons.apache.org/proper/commons-cli/)
    * custom made options parser
* How to display command help
    * custom formatting
* Initialization and destruction
    * Hooks for initialization and destruction in the shell life cycle
* Customizable
    * startup message
    * prompt
    * error messages
    * tab completion

What josh must provide:

* A framework to glue all the components together
* Standard implementation of common components
* Command line editing and history

Don't depend on a lot of external libraries

* The only required dependency is slf4j-api
* jline and jcommander are _optional_ dependencies


# How to use

A simple example of a basic shell with the built in commands, and no customization.

```
package josh.example;

import josh.command.builtin.BuiltInCommandProvider;
import josh.shell.BasicConsoleProvider;
import josh.shell.LineParserImpl;
import josh.shell.Shell;

public class MinimalShell {

    public static void main(String[] args) {
        Shell shell = new Shell();
        shell.setLineParser(new LineParserImpl());
        shell.setCommandProvider(new BuiltInCommandProvider());
        shell.setConsoleProvider(new BasicConsoleProvider());
        System.exit(shell.runInteractive().getExitCode());
    }

}
```

To add you own commands you must implement the interface CommandProvider.
The josh-example module has some examples that could be used as a starting point.

To use advanced line editing capabilities, instead of BasicConsoleProvider you should use JLineConsoleProvider
that is already included in josh.

```
        JLineConsoleProvider provider = new JLineConsoleProvider();
        provider.setHistory(System.getProperty("user.home") + "/.josh/", "josh_history", 800);
        provider.setPromptColor(Ansi.Color.CYAN);
        provider.setErrorColor(Ansi.Color.RED);
        provider.addCompleter(new CommandCompleter(shell.getLineParser(), shell.getCommandProvider()));
        provider.setPrompt("> ");

        shell.setConsoleProvider(provider);
```
