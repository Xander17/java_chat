package server.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

public class ChatHistory {
    private static Statement statement = DatabaseSQL.getStatement();
    private static final int LAST_MESSAGES_TO_SHOW = 50;

    public static String get(String nickname) {
        StringBuilder str = new StringBuilder();
        String query = String.format("SELECT h.time, c.nickname, h.message FROM history AS h " +
                    "JOIN clients AS c ON c.id = h.user_id JOIN clients AS c2 ON c2.nickname = '%s' " +
                    "LEFT JOIN blacklist AS b ON (b.id_blacklisted = h.user_id AND b.id_user = c2.id) " +
                    "OR (b.id_blacklisted = c2.id AND b.id_user = h.user_id) " +
                    "WHERE b.id_user IS NULL AND b.id_blacklisted IS NULL " +
                    "ORDER BY h.time DESC LIMIT %d", nickname, LAST_MESSAGES_TO_SHOW);
        try {
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                String msgNick = rs.getString("nickname");
                long msgTime = rs.getLong("time");
                String msg = rs.getString("message");
                str.insert(0, MessageFormating.broadcast(msgNick, msgTime, msg) + "\n");
            }
        } catch (SQLException e) {
            LogService.SERVER.info("ChatHistory",nickname, Arrays.toString(e.getStackTrace()));
        }
        return str.toString();
    }

    public static void addMsg(String nickname, long time, String msg) {
        PreparedStatement preparedStatement = null;
        Integer user_id = AuthService.getIdByNick(nickname);
        if (user_id == null) return;
        try {
            String query = "insert into history values(?, ?, ?)";
            preparedStatement = DatabaseSQL.getPreparedStatement(query);
            preparedStatement.setLong(1, time);
            preparedStatement.setInt(2, user_id);
            preparedStatement.setString(3, msg);
            preparedStatement.execute();
        } catch (SQLException | NullPointerException e) {
            LogService.SERVER.info("ChatHistory",nickname, Arrays.toString(e.getStackTrace()));
        } finally {
            DatabaseSQL.closePreparedStatement(preparedStatement);
        }

    }
}
