package murach.data;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import murach.business.User;

public class UserIO {

    // Ghi một dòng "email|firstName|lastName" vào cuối file
    public static synchronized void add(User user, String filepath) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(filepath, true))) {
            out.println(user.getEmail() + "|"
                      + user.getFirstName() + "|"
                      + user.getLastName());
        }
    }
}
