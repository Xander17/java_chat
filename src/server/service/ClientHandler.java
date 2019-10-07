package server.service;

import resources.ControlMessage;
import server.MainServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {

    private String nickname = null;

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
                getLoginPass();
                getMessages();
            } catch (IOException ignored) {
            } finally {
                closeIOStreams();
                mainServer.deleteClient(this);
            }
        }).start();
    }

    private void getLoginPass() throws IOException {
        String[] loginPassPair;
        String inputStr;
        while (true) {
            inputStr = inputStream.readUTF();
            loginPassPair = inputStr.split(" ", 3);
            if (loginPassPair.length != 3 || !loginPassPair[0].equals(ControlMessage.AUTH.toString())) continue;
            nickname = AuthService.getNickByLoginPass(loginPassPair[1], loginPassPair[2]);
            if (nickname != null) {
                sendMsg(ControlMessage.AUTH_OK.toString());
                break;
            }
            else sendMsg(ControlMessage.AUTH_FAIL.toString());
        }
    }

    private void getMessages() throws IOException {
        String inputStr;
        while (true) {
            inputStr = inputStream.readUTF();
            if (inputStr.equalsIgnoreCase(ControlMessage.CLOSE_CONNECTION.toString())) break;
            mainServer.broadcastMsg(inputStr);
        }
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

    public void sendMsg(String s) {
        if (!s.isEmpty() & !socket.isClosed()) {
            try {
                outputStream.writeUTF(s);
            } catch (IOException ignored) {
            }
        }
    }
}
