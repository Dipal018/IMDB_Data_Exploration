package sample;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Dipal on 3/2/2017.
 */
public class DatabaseHelper {

    private static String HOST_URL = "localhost";
    private static int HOST_PORT = 1521;
    private static String USERNAME = "coen280";
    private static String PASSWORD = "coen280";
    private static String DB_NAME = "movie";
    private static  String CONNECTION_URL = "jdbc:oracle:thin:@" + HOST_URL + ":" + HOST_PORT + ":" + DB_NAME;

    public static boolean loadDriver() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.out.println("Error loading driver: " + e);
            return false;
        }
        return true;
    }

    public static Connection connectToDatabase() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(CONNECTION_URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Failed to connect to database: " + e);
            return null;
        }
        return  connection;
    }
}
