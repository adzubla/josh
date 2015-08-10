package josh.example.jcommander;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import josh.command.CommandOutcome;
import josh.command.jcommander.Executable;
import josh.example.YesNo;

@Parameters(commandNames = "echo", commandDescription = "Echoes a message")
public class EchoCommand implements Executable {

    @Parameter
    private List<String> arguments = new ArrayList<String>();

    @Parameter(names = "--quiet", arity = 0)
    private boolean quiet;

    @Parameter(names = "--opt1", arity = 0)
    private String opt1;

    @Parameter(names = "--opt2", arity = 0)
    private String opt2;

    @Parameter(names = "--opt3", arity = 0)
    private String opt3;

    @Parameter(names = "--debug")
    private YesNo debug;

    @Parameter(names = "--file")
    private File file;

    @Parameter(names = "--level")
    private String level;

    @Override
    public CommandOutcome execute() {
        for (int i = 0; i < arguments.size(); i++) {
            if (i > 0) {
                System.out.print(" ");
            }
            System.out.print(arguments.get(i));
        }
        System.out.println();
        return new CommandOutcome(0);
    }

}
