package server.service;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageFormat {

    public static String broadcast(String nickname, String message) {
        SimpleDateFormat dateformat = new SimpleDateFormat("[HH:mm:ss] ");
        message = "" + nickname + ": " + message;
        message = dateformat.format(new Date()) + message;
        return message;
    }

    public static String whisper(String srcNickname, String dstNickname, String message) {
        SimpleDateFormat dateformat = new SimpleDateFormat("[HH:mm:ss] ");
        message = srcNickname + " -> " + dstNickname + ": " + message;
        message = dateformat.format(new Date()) + message;
        return message;
    }
}
