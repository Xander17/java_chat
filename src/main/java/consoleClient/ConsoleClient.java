package consoleClient;

import java.io.*;
import java.net.Socket;

public class ConsoleClient {
    private DataInputStream inputStream = null;
    private DataOutputStream outputStream = null;
    private Socket socket = null;

    private final String IP_ADDRESS = "localhost";
    private final int PORT = 8190;

    private final String END_MESSAGE = "/end";

    public ConsoleClient() {
        runServerListener();
        runConsoleHandler();
    }

    private void runServerListener() {
        try {
            socket = new Socket(IP_ADDRESS, PORT);
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
            new Thread(() -> {
                try {
                    String inputString;
                    while (true) {
                        inputString = inputStream.readUTF();
                        System.out.println(inputString);
                    }
                } catch (IOException ignored) {
                } finally {
                    closeIOStreams();
                }
            }).start();
        } catch (IOException e) {
            System.out.println("Server connection error!");
        }
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
                    sendMsg("Console: " + consoleString);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeIOStreams();
            }
        });
        consoleThread.setDaemon(true);
        consoleThread.start();
    }

    private void closeIOStreams() {
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

    private void sendMsg(String s) {
        if (!socket.isClosed()) {
            try {
                outputStream.writeUTF(s);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
