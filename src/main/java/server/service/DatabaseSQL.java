package server.service;


import java.sql.*;
import java.util.Arrays;

public class DatabaseSQL {
    private static Connection connection;
    private static Statement statement;

    private final static String DB_NAME = "DB/java_chat.db";

    public static void connect() {
        try {
            //Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + DB_NAME);
            statement = connection.createStatement();
            LogService.SERVER.info("Clients DB connected.");
        } catch (Exception e) {
            LogService.SERVER.error("Clients DB connection failed", Arrays.toString(e.getStackTrace()));
        }
    }

    public static void shutdown() {
        try {
            connection.close();
            LogService.SERVER.info("Clients DB stopped.");
        } catch (SQLException e) {
            LogService.SERVER.error("DB", Arrays.toString(e.getStackTrace()));
        }
    }

    public static Statement getStatement() {
        return statement;
    }

    public static PreparedStatement getPreparedStatement(String query) throws SQLException {
        return connection.prepareStatement(query);
    }

    public static void closePreparedStatement(PreparedStatement preparedStatement) {
        try {
            if (preparedStatement != null) preparedStatement.close();
        } catch (SQLException e) {
            LogService.SERVER.error("DB", Arrays.toString(e.getStackTrace()));
        }
    }
}
