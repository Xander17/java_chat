package client;

import client.controller.TitleBarController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;


public class Controller implements Initializable {
    @FXML
    BorderPane mainPane;
    @FXML
    ToggleGroup skinsGroup;
    @FXML
    VBox vboxLogin, vboxRegistration, vboxAbout;
    @FXML
    TextArea taChat;
    @FXML
    TextField tfMessage, tfLogin, tfRegLogin, tfRegNickname;
    @FXML
    PasswordField tfPassword, tfRegPassword;
    @FXML
    Button btnSend;
    @FXML
    MenuItem mClear, mAbout, mSignOut;

    private String nickname = null;
    private TitleBarController titleController;

    private DataInputStream inputStream = null;
    private DataOutputStream outputStream = null;
    private Socket socket = null;

    private final String IP_ADDRESS = "localhost";
    private final int PORT = 8190;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getTitleController();
        runServerListener();
        //runConsoleHandler();
    }

/*    public void afterLoad() {
        System.out.println("123");
    }*/

    private void runServerListener() {
        try {
            socket = new Socket(IP_ADDRESS, PORT);
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
            new Thread(() -> {
                try {
                    String inputString;
                    setTitleStatus();
                    while (true) {
                        inputString = inputStream.readUTF();
                        taChat.appendText(inputString + "\n");
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

    /*private void runConsoleHandler() {
        Thread consoleThread = new Thread(() -> {
            BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in));
            String consoleString;
            try {
                while (true) {
                    consoleString = consoleIn.readLine();
                    if (consoleString.trim().isEmpty()) continue;
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
    }*/

    private void getTitleController() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        try {
            fxmlLoader.load(getClass().getResourceAsStream("fxml/titleBar.fxml"));
            titleController = fxmlLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeIOStreams() {
        try {
            inputStream.close();
        } catch (IOException | NullPointerException ignored) {
        }
        try {
            outputStream.close();
        } catch (IOException | NullPointerException ignored) {
        }
        try {
            socket.close();
        } catch (IOException | NullPointerException ignored) {
        }
        Platform.runLater(this::setTitleStatus);
    }

    public void sendMsg() {
        sendMsg(tfMessage.getText());
    }

    private void sendMsg(String s) {
        s = s.trim();
        if (!s.isEmpty() & !socket.isClosed()) {
            try {
                outputStream.writeUTF(s);
            } catch (IOException e) {
                e.printStackTrace();
            }
            tfMessage.clear();
            tfMessage.requestFocus();
        }
    }

    public void clearChat() {
        taChat.clear();
        tfMessage.requestFocus();
    }

    public void exitChat() {
        Platform.exit();
        System.exit(0);
    }

    public void loginToServer() {
        System.out.println("LOGIN!");
        setTitleStatus();
    }

    public void signUp() {
    }

    public void aboutWindow() {
        changeDisable(true);
        vboxAbout.setVisible(true);
    }

    public void aboutWindowClose() {
        changeDisable(false);
        vboxAbout.setVisible(false);
        tfMessage.requestFocus();
    }

    private void changeDisable(boolean status) {
        btnSend.setDisable(status);
        tfMessage.setDisable(status);
        taChat.setDisable(status);
        mAbout.setDisable(status);
        mClear.setDisable(status);
        mSignOut.setDisable(status);
    }

    public void setStyle() {
        MenuItem m = (MenuItem) skinsGroup.getSelectedToggle();
        String pathToCSS = "/client/css/" + m.getText().toLowerCase() + ".css";
        mainPane.getScene().getStylesheets().remove(2);
        mainPane.getScene().getStylesheets().add(getClass().getResource(pathToCSS).toExternalForm());
    }

    private void setTitleStatus() {
        String title = "GB Chat";
        title += (nickname != null) ? " [Nickname: " + nickname + "]" : "";
        title += (!isSocketOpen()) ? " [No connection]" : " [Connected to " + IP_ADDRESS + ":" + PORT + "]";
        titleController.setTitle(title);
    }

    private boolean isSocketOpen() {
        return socket != null && !socket.isClosed();
    }

    public void passwordFocus() {
        tfPassword.requestFocus();
    }

    public void regPasswordFocus() {
        tfRegPassword.requestFocus();
    }

    public void regNickFocus() {
        tfRegNickname.requestFocus();
    }

    public void swapLoginReg() {
        vboxLogin.setVisible(!vboxLogin.isVisible());
        vboxRegistration.setVisible(!vboxLogin.isVisible());
    }

    public void disconnect() {
        nickname = null;
        taChat.clear();
        closeIOStreams();
        setLoginWindowState();
    }

    private void setLoginWindowState() {
        vboxLogin.setVisible(true);
    }
}

