package server.service;


import java.sql.*;

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

    public static PreparedStatement getPreparedStatement(String query) throws SQLException {
        return connection.prepareStatement(query);
    }

    public static void closePreparedStatement(PreparedStatement preparedStatement) {
        try {
            if (preparedStatement != null) preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
