package server.service;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class ClientBlacklist {

    private ArrayList<String> list;
    @Setter
    private boolean isUpdated;
    @Getter
    private String nickname;

    public ClientBlacklist(String nickname, DatabaseService databaseService) {
        this.nickname = nickname;
        list = databaseService.getBlacklistService().getBlacklist(nickname);
        this.isUpdated = false;
    }

    void add(String nickToBlacklist) {
        list.add(nickToBlacklist);
    }

    void remove(String nickToBlacklist) {
        list.remove(nickToBlacklist);
    }

    boolean containsNick(String nickname) {
        for (String blacklisted : list) {
            if (blacklisted.equalsIgnoreCase(nickname)) return true;
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
}
