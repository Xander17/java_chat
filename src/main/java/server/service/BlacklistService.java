package server.service;

import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

@Service
public class BlacklistService {

    private Statement statement;
    private AuthService authService;

    public BlacklistService(Database database, AuthService authService) {
        this.statement = database.getStatement();
        this.authService = authService;
    }

    ArrayList<String> getBlacklist(String nick) {
        ArrayList<String> list = new ArrayList<>();
        try {
            ResultSet resultSet = statement.executeQuery(String.format("" +
                            "Select m1.nickname from blacklist as b " +
                            "inner join clients as m1 on m1.id=b.id_blacklisted " +
                            "inner join clients as m2 on m2.id=b.id_user " +
                            "where m2.nickname='%s'",
                    nick));
            while (resultSet.next()) {
                list.add(resultSet.getString(1));
            }
        } catch (SQLException e) {
            LogService.SERVER.error("Blacklist", nick, Arrays.toString(e.getStackTrace()));
        }
        return list;
    }

    public boolean isBlacklistRelations(ClientHandler client1, ClientHandler client2) {
        return client1.checkBlackList(client2.getNickname()) || client2.checkBlackList(client1.getNickname());
    }

    private boolean addToDB(Integer userID, Integer blacklistID) {
        try {
            statement.executeUpdate(String.format("insert into blacklist(id_user,id_blacklisted) values(%d,%d)", userID, blacklistID));
            return true;
        } catch (SQLException e) {
            LogService.SERVER.error("Blacklist", userID.toString(), blacklistID.toString(), Arrays.toString(e.getStackTrace()));
        }
        return false;
    }

    private boolean removeFromDB(Integer userID, Integer blacklistID) {
        try {
            statement.executeUpdate(String.format("delete from blacklist where id_user='%s' and id_blacklisted='%s'", userID, blacklistID));
            return true;
        } catch (SQLException e) {
            LogService.SERVER.error("Blacklist", userID.toString(), blacklistID.toString(), Arrays.toString(e.getStackTrace()));
        }
        return false;
    }

    String addAndEcho(ClientBlacklist userBlacklist, String nickToBlacklist) {
        String userNick = userBlacklist.getNickname();
        if (userNick.equalsIgnoreCase(nickToBlacklist)) return "Невозможно добавить себя же в черный список";
        if (userBlacklist.containsNick(nickToBlacklist))
            return "Пользователь " + nickToBlacklist + " уже есть в черном списке";
        Integer blacklistID = authService.getIdByNick(nickToBlacklist);
        if (blacklistID == null) return "Пользователя " + nickToBlacklist + " не существует";
        Integer userID = authService.getIdByNick(userNick);
        if (addToDB(userID, blacklistID)) {
            userBlacklist.setUpdated(true);
            userBlacklist.add(nickToBlacklist);
            LogService.USERS.info(userNick + " добавил " + nickToBlacklist + " в черный список");
            return "Вы добавили пользователя " + nickToBlacklist + " в черный список";
        } else return "Ошибка при добавлении пользователя в черный список";
    }

    String removeAndEcho(ClientBlacklist userBlacklist, String nickToBlacklist) {
        String userNick = userBlacklist.getNickname();
        if (!userBlacklist.containsNick(nickToBlacklist))
            return "Пользователя " + nickToBlacklist + " нет в черном списке";
        Integer blacklistID = authService.getIdByNick(nickToBlacklist);
        Integer userID = authService.getIdByNick(userNick);
        if (removeFromDB(userID, blacklistID)) {
            userBlacklist.setUpdated(true);
            userBlacklist.remove(nickToBlacklist);
            LogService.USERS.info(userNick + " удалил " + nickToBlacklist + " из черного списка");
            return "Вы удалили пользователя " + nickToBlacklist + " из черного списка";
        } else return "Ошибка при удалении пользователя из черного списка";
    }
}
