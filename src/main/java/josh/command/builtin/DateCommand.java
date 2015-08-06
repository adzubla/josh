package josh.command.builtin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DateCommand {

    private static final String defaultFormat = "yyyy-MM-dd'T'HH:mm:ss";

    public int run(List<String> arguments) {

        Date currentDate = new Date();

        String format = defaultFormat;
        if (arguments.size() == 1) {
            format = arguments.get(0);
        }
        else if (!arguments.isEmpty()) {
            System.err.println("Expected date format");
            return 1;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        System.out.println(sdf.format(currentDate));

        return 0;
    }

}
