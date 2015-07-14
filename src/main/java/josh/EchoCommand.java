package josh;

import java.util.List;

public class EchoCommand {

    public int run(List<String> arguments) {
        for (int i = 0; i < arguments.size(); i++) {
            if (i > 0) {
                System.out.print(" ");
            }
            System.out.print(arguments.get(i));
        }
        System.out.println();
        return 0;
    }

}
