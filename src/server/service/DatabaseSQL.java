package server.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSQL {
    private static Connection connection;
    private static Statement statement;

    private final static String DB_NAME = "DB/java_chat.db";

    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + DB_NAME);
            statement = connection.createStatement();
            System.out.println("Clients DB connected.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Clients DB connection failed.");
        }
    }

    public static void shutdown() {
        try {
            connection.close();
            System.out.println("Clients DB stopped.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Statement getStatement() {
        return statement;
    }
}
