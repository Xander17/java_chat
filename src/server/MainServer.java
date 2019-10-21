package server;

import resources.ControlMessage;
import server.service.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class MainServer {
    private Vector<ClientHandler> clients = new Vector<>();
    private ServerSocket server = null;
    private Socket socket = null;

    private final int SOCKET_PORT = 8190;

    MainServer() {
        runServer();
        runConsoleHandler();
    }

    private void runServer() {
        new Thread(() -> {
            try {
                DatabaseSQL.connect();
                server = new ServerSocket(SOCKET_PORT);
                System.out.println("Server started.");
                while (true) {
                    socket = server.accept();
                    addClient(socket);
                    System.out.println("New client connected. " + getConnectionsCountInfo());
                }
            } catch (IOException ignored) {
            } finally {
                serverShutDown();
            }
        }).start();
    }

    private void runConsoleHandler() {
        Thread consoleThread = new Thread(() -> {
            BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in));
            String consoleString;
            try {
                while (true) {
                    consoleString = consoleIn.readLine();
                    if (consoleString.trim().isEmpty()) continue;
                    if (consoleString.equalsIgnoreCase(ControlMessage.CLOSE_CONNECTION.toString())) break;
                    else broadcastMsg(null, consoleString);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                serverShutDown();
            }

        });
        consoleThread.setDaemon(true);
        consoleThread.start();
    }

    private String getConnectionsCountInfo() {
        return "Total connected clients: " + clients.size();
    }

    private void serverShutDown() {

        try {
            clients.forEach(ClientHandler::closeIOStreams);
            DatabaseSQL.shutdown();
            if (!server.isClosed()) {
                server.close();
                System.out.println("Server stopped.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcastMsg(ClientHandler srcClient, String msg) {
        String srcNickname;
        long currentTime = System.currentTimeMillis();
        if (srcClient != null) {
            srcNickname = srcClient.getNickname();
            ChatHistory.addMsg(srcNickname, currentTime, msg);
        } else srcNickname = "Server";
        if (clients.size() > 0) {
            msg = MessageFormating.broadcast(srcNickname, currentTime, msg);
            for (ClientHandler client : clients) {
                if (srcClient == null || !Blacklist.isBlacklistRelations(srcClient, client)) client.sendMsg(msg);
            }
        }
    }

    public void whisper(ClientHandler srcClient, String dstNickname, String message) {
        ClientHandler dstClient = getClientByNickname(dstNickname);
        if (dstClient != null) {
            message = MessageFormating.whisper(srcClient.getNickname(), dstNickname, message);
            srcClient.sendMsg(message);
            dstClient.sendMsg(message);
        } else srcClient.sendMsg("User " + dstNickname + " is not online");
    }

    public void whisperOneWayMessage(String srcNickname, String dstNickname, String message) {
        ClientHandler dstClient = getClientByNickname(dstNickname);
        if (dstClient != null) {
            message = MessageFormating.whisper(srcNickname, dstNickname, message);
            dstClient.sendMsg(message);
        }
    }

    private void addClient(Socket socket) {
        clients.add(new ClientHandler(this, socket));
    }

    public void deleteClient(ClientHandler client) {
        clients.remove(client);
        System.out.println("Client disconnected. " + getConnectionsCountInfo());
    }

    private ClientHandler getClientByNickname(String nickname) {
        for (ClientHandler client : clients) {
            if (client.getNickname().equals(nickname)) return client;
        }
        return null;
    }

    public boolean isUserOnline(String nickname) {
        for (ClientHandler client : clients) {
            if (client.getNickname().equals(nickname)) return true;
        }
        return false;
    }
}
