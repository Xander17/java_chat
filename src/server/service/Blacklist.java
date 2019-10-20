package server.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

class Blacklist {

    private static Statement statement = DatabaseSQL.getStatement();
    private boolean isUpdated = false;
    private ArrayList<String> list;

    Blacklist(String nickname) {
        list = getBlacklist(nickname);
    }

    private static ArrayList<String> getBlacklist(String nick) {
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
            e.printStackTrace();
        }
        return list;
    }

    private static boolean addToDB(Integer userID, Integer blacklistID) {
        try {
            statement.executeUpdate(String.format("insert into blacklist(id_user,id_blacklisted) values(%d,%d)", userID, blacklistID));
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean removeFromDB(Integer userID, Integer blacklistID) {
        try {
            statement.executeUpdate(String.format("delete from blacklist where id_user='%s' and id_blacklisted='%s'", userID, blacklistID));
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    boolean isUpdated() {
        if (isUpdated) {
            isUpdated = false;
            return true;
        }
        return false;
    }

    String addAndEcho(String userNick, String blacklistNick) {
        if (userNick.equals(blacklistNick)) return "Невозможно добавить себя же в черный список";
        if (contains(blacklistNick)) return "Пользователь " + blacklistNick + " уже есть в черном списке";
        Integer blacklistID = AuthService.getIdByNick(blacklistNick);
        if (blacklistID == null) return "Пользователя " + blacklistNick + " не существует";
        Integer userID = AuthService.getIdByNick(userNick);
        if (addToDB(userID, blacklistID)) {
            isUpdated = true;
            list.add(blacklistNick);
            return "Вы добавили пользователя " + blacklistNick + " в черный список";
        } else return "Ошибка при добавлении пользователя в черный список";
    }

    String removeAndEcho(String userNick, String blacklistNick) {
        if (!contains(blacklistNick)) return "Пользователя " + blacklistNick + " нет в черном списке";
        Integer blacklistID = AuthService.getIdByNick(blacklistNick);
        Integer userID = AuthService.getIdByNick(userNick);
        if (removeFromDB(userID, blacklistID)) {
            isUpdated = true;
            list.remove(blacklistNick);
            return "Вы удалили пользователя " + blacklistNick + " из черного списка";
        } else return "Ошибка при удалении пользователя из черного списка";
    }

    boolean contains(String nickname) {
        return list.contains(nickname);
    }
}
