# josh - Java Oriented Shell

This is a little framework to help the creation of a command line shell console in Java.

What should be configurable:

* Where to find commands, and how to run them
    * from jars in plugins directory
    * from groovy or jython files
    * from beans in Spring application context
    * embedded in my source code
* How to parse commands and options
    * JCommander
    * Commons CLI
    * custom made options parser
* How to display command help
    * custom formatting
* Hooks for initialization and destruction, for the shell (and for each command?)
* Customizable
    * startup message
    * prompt
    * error messages
    * tab completion

What josh must provide:

* A framework to glue all the components together
* Standard implementation of common components
* Command line editing and history

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
        System.exit(shell.run().getExitCode());
    }

}
```
