package services;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZonedDateTime;

/**
 * Logger is a helper class for writing data to files
 */
public class Logger {
    /**
     * Log an attempted log-in attempt to the login_activity.txt file
     * @param userId the attempted User ID
     * @param successful whether the attempt was successful
     */
    public static void logAttemptedLogin(String userId, boolean successful) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("login_activity.txt", true));
            PrintWriter pw = new PrintWriter(writer);
            pw.format("%s - User ID: %s , Login Successful: %s \n", ZonedDateTime.now(), userId, successful);
            pw.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
