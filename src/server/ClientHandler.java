package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

class ClientHandler {

    private MainServer mainServer;
    private Socket socket;
    private DataInputStream inputStream = null;
    private DataOutputStream outputStream = null;

    private final String END_MESSAGE = "/end";

    ClientHandler(MainServer mainServer, Socket socket) {
        this.mainServer = mainServer;
        this.socket = socket;
        startClientThread();
    }

    private void startClientThread() {
        new Thread(() -> {
            try {
                inputStream = new DataInputStream(socket.getInputStream());
                outputStream = new DataOutputStream(socket.getOutputStream());
                while (true) {
                    String inputStr = inputStream.readUTF();
                    if (inputStr.equalsIgnoreCase(END_MESSAGE)) break;
                    System.out.println(inputStr);
                    mainServer.broadcastMsg(inputStr);
                }
            } catch (IOException ignored) {
            } finally {
                closeIOStreams();
                mainServer.removeClient(this);
            }
        }).start();
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

    void sendMsg(String s) {
        if (!s.isEmpty() & !socket.isClosed()) {
            try {
                outputStream.writeUTF(s);
            } catch (IOException ignored) {
            }
        }
    }
}
