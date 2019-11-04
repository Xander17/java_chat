package server.service;

import resources.LoginRegError;

import java.sql.*;

class AuthService {
    private static Statement statement = DatabaseSQL.getStatement();

    static String getNickByLoginPass(String login, String pass) {
        String query = String.format("select nickname from clients where lower(login) = lower('%s') and password='%s'", login, pass);
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
        String query = String.format("select * from clients where lower(%s) = lower('%s')", column, entry);
        ResultSet result = statement.executeQuery(query);
        return result.next();
    }

    static Integer getIdByNick(String nick) {
        try {
            ResultSet set = statement.executeQuery(String.format("select id from clients where lower(nickname) = lower('%s')", nick));
            if (set.next())
                return set.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
