package server.service;

import resources.LoginRegError;

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

    static String getNickByLoginPass(String login, String pass) {
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

    static LoginRegError registerAndEchoMsg(String login, String pass, String nickname) {
        try {
            if (recordExist("login", login)) return LoginRegError.LOGIN_EXISTS;
            if (recordExist("nickname", nickname)) return LoginRegError.NICKNAME_EXISTS;
            String query = String.format("insert into clients(login,password,nickname) values ('%s','%s','%s')", login, pass, nickname);
            if (statement.executeUpdate(query) > 0) return null;
            else return LoginRegError.REG_ERROR;
        } catch (SQLException e) {
            e.printStackTrace();
            return LoginRegError.DB_ERROR;
        }
    }

    private static boolean recordExist(String column, String entry) throws SQLException {
        String query = String.format("select * from clients where %s='%s'", column, entry);
        ResultSet result = statement.executeQuery(query);
        return result.next();
    }

    public static void shutdown() {
        try {
            connection.close();
            System.out.println("Clients DB stopped.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
