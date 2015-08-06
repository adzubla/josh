package josh.command.builtin;

import java.util.List;
import java.util.Properties;

public class PropertiesCommand {

    public int run(List<String> arguments) {

        Properties properties = System.getProperties();
        for (String key : properties.stringPropertyNames()) {
            String value = properties.getProperty(key);
            System.out.println(key + "=" + value);
        }

        return 0;
    }

}
