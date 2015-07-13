package josh;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DateCommand {

    //format yyyy-MM-dd'T'HH:mm:ss

    public void run(List<String> arguments) {

        Date currentDate = new Date();

        if (arguments == null || arguments.isEmpty()) {
            System.out.println(currentDate);
        } else if (arguments.size() == 1) {
            SimpleDateFormat sdf = new SimpleDateFormat(arguments.get(0));
            System.out.println(sdf.format(currentDate));
        } else {
            throw new RuntimeException("Invalid arguments.");
        }
    }

}
