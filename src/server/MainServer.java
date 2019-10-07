package server;

import resources.ControlMessage;
import server.service.AuthService;
import server.service.ClientHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class MainServer {
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
                AuthService.connect();
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

    public void broadcastMsg(String s) {
        if (clients.size() > 0) {
            SimpleDateFormat dateformat = new SimpleDateFormat("[HH:mm:ss] ");
            clients.forEach(client -> client.sendMsg(dateformat.format(new Date()) + s));
        }
    }

    private void addClient(Socket socket) {
        clients.add(new ClientHandler(this, socket));
    }

    public void deleteClient(ClientHandler client) {
        clients.remove(client);
        System.out.println("Client disconnected. " + getConnectionsCountInfo());
    }
}
