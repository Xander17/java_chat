package server.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

public class ChatHistory {
    private static Statement statement = DatabaseSQL.getStatement();
    private static final int LAST_MESSAGES_TO_SHOW = 50;

    public static String get(String nickname) {
        StringBuilder str = new StringBuilder();
        String query = String.format(
                "select h.time, c.nickname, h.message from history as h, clients as c on c.id = h.user_id order by time limit (select count(time) from history) - %d, %d",
                LAST_MESSAGES_TO_SHOW, LAST_MESSAGES_TO_SHOW);
        // TODO: 21.10.2019 запрос с учетом блеклиста
        try {
            Set<String> blockedNicknames = Blacklist.getBlacklistSet(nickname);
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                String msgNick = rs.getString("nickname");
                if (blockedNicknames.contains(msgNick)) continue;
                long msgTime = rs.getLong("time");
                String msg = rs.getString("message");
                str.append(MessageFormating.broadcast(msgNick, msgTime, msg));
                str.append("\n");
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
