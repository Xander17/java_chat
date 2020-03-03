package server.service;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageFormating {

    public static String broadcast(String nickname, String message) {
        SimpleDateFormat dateformat = new SimpleDateFormat("[HH:mm:ss] ");
        //formattedMessage = "" + nickname + ": " + formattedMessage;
        //formattedMessage = dateformat.format(new Date()) + formattedMessage;
        return dateformat.format(new Date()) +
                nickname +
                ": " +
                message;
    }

    public static String broadcast(String nickname, long time, String message) {
        SimpleDateFormat dateformat = new SimpleDateFormat("[HH:mm:ss] ");
        //message = "" + nickname + ": " + message;
        //message = dateformat.format(new Date(time)) + message;
        return dateformat.format(new Date(time)) +
                nickname +
                ": " +
                message;
    }

    public static String whisper(String srcNickname, String dstNickname, String message) {
        SimpleDateFormat dateformat = new SimpleDateFormat("[HH:mm:ss] ");
        //message = srcNickname + " -> " + dstNickname + ": " + message;
        //message = dateformat.format(new Date()) + message;
        return dateformat.format(new Date()) +
                srcNickname +
                " -> " +
                dstNickname +
                ": " +
                message;
    }
}
