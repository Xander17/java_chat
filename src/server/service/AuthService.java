package server.service;

import java.sql.*;

public class AuthService {
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

    public static String getNickByLoginPass(String login, String pass) {
        String query = String.format("select nickname from clients where login='%s' and password='%s'", login, pass);
        ResultSet result;
        String nickname = null;
        try {
            result = statement.executeQuery(query);
            if (result.next()) {
                nickname = result.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nickname;
    }
}
