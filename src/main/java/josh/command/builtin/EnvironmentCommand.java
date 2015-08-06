package josh.command.builtin;

import java.util.List;
import java.util.Map;

public class EnvironmentCommand {

    public int run(List<String> arguments) {

        Map<String, String> env = System.getenv();
        for (String key : env.keySet()) {
            String value = env.get(key);
            System.out.println(key + "=" + value);
        }

        return 0;
    }

}
