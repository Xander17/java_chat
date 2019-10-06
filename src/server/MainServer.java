package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

class MainServer {
    private Vector<ClientHandler> clients = new Vector<>();
    private ServerSocket server = null;
    private Socket socket = null;

    private final int SOCKET_PORT = 8190;
    private final String END_MESSAGE = "/end";

    MainServer() {
        runServer();
        runConsoleHandler();
    }

    private void runServer() {
        new Thread(() -> {
            try {
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
                    if (consoleString.equalsIgnoreCase(END_MESSAGE)) break;
                    else broadcastMsg("Server: " + consoleString);
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
            if (!server.isClosed()) {
                server.close();
                System.out.println("Server stopped.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void broadcastMsg(String s) {
        if (clients.size() > 0) {
            SimpleDateFormat dateformat = new SimpleDateFormat("[HH:mm:ss] ");
            clients.forEach(client -> client.sendMsg(dateformat.format(new Date()) + s));
        }
    }

    void addClient(Socket socket) {
        clients.add(new ClientHandler(this, socket));
    }

    void deleteClient(ClientHandler client) {
        clients.remove(client);
        System.out.println("Client disconnected. " + getConnectionsCountInfo());
    }
}
