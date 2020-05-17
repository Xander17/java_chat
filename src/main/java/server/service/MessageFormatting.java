package server.service;

import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class MessageFormatting {

    public String broadcast(String nickname, String message) {
        SimpleDateFormat dateformat = new SimpleDateFormat("[HH:mm:ss] ");
        return dateformat.format(new Date()) +
                nickname +
                ": " +
                message;
    }

    public String broadcast(String nickname, long time, String message) {
        SimpleDateFormat dateformat = new SimpleDateFormat("[HH:mm:ss] ");
        return dateformat.format(new Date(time)) +
                nickname +
                ": " +
                message;
    }

    public String whisper(String srcNickname, String dstNickname, String message) {
        SimpleDateFormat dateformat = new SimpleDateFormat("[HH:mm:ss] ");
        return dateformat.format(new Date()) +
                srcNickname +
                " -> " +
                dstNickname +
                ": " +
                message;
    }
}
