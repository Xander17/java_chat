package server.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ChatHistory {
    private static Statement statement = DatabaseSQL.getStatement();
    private static final int LAST_MESSAGES_TO_SHOW = 50;

    public static String get(String nickname) {
        StringBuilder str = new StringBuilder();
        String query = String.format("SELECT h.time, c.nickname, h.message FROM history AS h, clients AS c ON c.id = h.user_id " +
                "WHERE h.user_id NOT IN (SELECT b.id_user FROM blacklist AS b INNER JOIN clients AS c ON b.id_blacklisted=c.id WHERE c.nickname = '%s' " +
                "UNION SELECT b.id_blacklisted FROM blacklist AS b INNER JOIN clients AS c ON b.id_user=c.id WHERE c.nickname = '%s') " +
                "ORDER BY time DESC LIMIT %d", nickname, nickname, LAST_MESSAGES_TO_SHOW);
        try {
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                String msgNick = rs.getString("nickname");
                long msgTime = rs.getLong("time");
                String msg = rs.getString("message");
                str.insert(0, MessageFormating.broadcast(msgNick, msgTime, msg) + "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        } finally {
            DatabaseSQL.closePreparedStatement(preparedStatement);
        }

    }
}
