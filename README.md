# josh - Java Oriented Shell

This is a little framework to help the creation of a command line shell console in Java.

What should be configurable:

* Where to find commands, and how to run them?
    * from jars in plugins directory
    * from groovy or jython files
    * from beans in Spring application context
    * embedded in my source code
* How to parse commands and options
    * JCommander
    * Commons CLI
    * custom made options parser
* How to display command help?
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
