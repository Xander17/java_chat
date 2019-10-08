package server.service;

import resources.ControlMessage;
import resources.LoginRegError;
import server.MainServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {

    private String nickname = "";

    private MainServer mainServer;
    private Socket socket;
    private DataInputStream inputStream = null;
    private DataOutputStream outputStream = null;

    public ClientHandler(MainServer mainServer, Socket socket) {
        this.mainServer = mainServer;
        this.socket = socket;
        startClientThread();
    }

    private void startClientThread() {
        new Thread(() -> {
            try {
                inputStream = new DataInputStream(socket.getInputStream());
                outputStream = new DataOutputStream(socket.getOutputStream());
                getUserLoginReg();
                getMessages();
            } catch (IOException ignored) {
            } finally {
                closeIOStreams();
                mainServer.deleteClient(this);
            }
        }).start();
    }

    private void getUserLoginReg() throws IOException {
        String[] loginPassPair;
        String inputStr;
        while (true) {
            inputStr = inputStream.readUTF();
            loginPassPair = inputStr.split(" ", 3);
            if (loginPassPair.length != 3) continue;
            if (ControlMessage.AUTH.check(loginPassPair[0])) {
                String loginNickname = AuthService.getNickByLoginPass(loginPassPair[1], loginPassPair[2]);
                if (loginNickname == null) sendLoginRegError(LoginRegError.INCORRECT_LOGIN_PASS);
                else if (mainServer.isUserOnline(loginNickname)) sendLoginRegError(LoginRegError.LOGGED_ALREADY);
                else {
                    nickname = loginNickname;
                    sendMsg(ControlMessage.AUTH_OK, nickname);
                    break;
                }
            } else if (ControlMessage.REG.check(loginPassPair[0])) {
                String login = loginPassPair[1];
                loginPassPair = loginPassPair[2].split(" ", 2);
                LoginRegError error = AuthService.registerAndEchoMsg(login, loginPassPair[0], loginPassPair[1]);
                if (error == null) sendMsg(ControlMessage.REG_OK);
                else sendLoginRegError(error);
            }
        }
    }

    private void getMessages() throws IOException {
        String inputStr;
        while (true) {
            inputStr = inputStream.readUTF().trim();
            if (!isControlMessage(inputStr)) {
                mainServer.broadcastMsg(nickname, inputStr);
                continue;
            }
            String[] controlCheck = inputStr.split(" ", 3);
            if (ControlMessage.CLOSE_CONNECTION.check(controlCheck[0])) break;
            if (ControlMessage.WHISPER.check(controlCheck[0]) && controlCheck.length == 3)
                mainServer.whisper(this, controlCheck[1], controlCheck[2]);
        }
    }

    private boolean isControlMessage(String s) {
        return s.startsWith("/");
    }

    public void closeIOStreams() {
        sendMsg("Server: Connection closed.");
        try {
            if (inputStream != null) inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (outputStream != null) outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (!socket.isClosed()) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendLoginRegError(LoginRegError err) {
        sendMsg(ControlMessage.ERROR.toString() + " " + err.ordinal());
    }

    public void sendMsg(ControlMessage m) {
        sendMsg(m.toString());
    }

    public void sendMsg(ControlMessage m, String s) {
        sendMsg(m.toString() + " " + s);
    }

    public void sendMsg(String s) {
        if (!s.isEmpty() & !socket.isClosed()) {
            try {
                outputStream.writeUTF(s);
            } catch (IOException ignored) {
            }
        }
    }

    public String getNickname() {
        return nickname;
    }

}
