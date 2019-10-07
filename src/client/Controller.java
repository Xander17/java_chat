package client;

import client.controller.TitleBarController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import resources.ControlMessage;
import resources.LoginRegError;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;


public class Controller implements Initializable {
    @FXML
    private BorderPane mainPane;
    @FXML
    private ToggleGroup skinsGroup;
    @FXML
    private VBox vboxLogin, vboxRegistration, vboxAbout;
    @FXML
    private TextArea taChat;
    @FXML
    private TextField tfMessage, tfLogin, tfRegLogin, tfRegNickname;
    @FXML
    private PasswordField tfPassword, tfRegPassword;
    @FXML
    private Button btnSend, btnLogin, btnReg;
    @FXML
    private MenuItem mClear, mAbout, mSignOut, mDisconnect;
    @FXML
    private Label lblLoginInfo, lblRegInfo;
    @FXML
    private BorderPane titleBar;
    @FXML
    private TitleBarController titleBarController;

    private String nickname = null;
    private boolean loginState = true;

    private DataInputStream inputStream = null;
    private DataOutputStream outputStream = null;
    private Socket socket = null;

    private final String IP_ADDRESS = "localhost";
    private final int PORT = 8190;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        runServerListener();
        setTitleStatus();
    }

    private void runServerListener() {
        try {
            socket = new Socket(IP_ADDRESS, PORT);
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
            mDisconnect.setDisable(false);
            new Thread(() -> {
                try {
                    loginRegWindow();
                    getMessages();
                } catch (IOException ignored) {
                } finally {
                    closeIOStreams();
                }
            }).start();
        } catch (IOException e) {
            System.out.println("Server connection error!");
        }
    }

    private void loginRegWindow() throws IOException {
        String inputString;
        while (true) {
            inputString = inputStream.readUTF();
            if (ControlMessage.AUTH_OK.check(inputString) && vboxLogin.isVisible()) {
                setLoginState(false);
                break;
            } else if (ControlMessage.AUTH_FAIL.check(inputString) && vboxLogin.isVisible()) {
                setLoginInfo(LoginRegError.INCORRECT_LOGIN_PASS);
            } else if (ControlMessage.REG_OK.check(inputString) && vboxRegistration.isVisible()) {
                String login = tfRegLogin.getText().trim();
                String pass = tfRegPassword.getText();
                swapLoginReg();
                tfLogin.setText(login);
                tfPassword.setText(pass);
            } else if (ControlMessage.REG_LOGIN_EXISTS.check(inputString) && vboxRegistration.isVisible()) {
                setRegInfo(LoginRegError.LOGIN_EXISTS);
            } else if (ControlMessage.REG_NICKNAME_EXISTS.check(inputString) && vboxRegistration.isVisible()) {
                setRegInfo(LoginRegError.NICKNAME_EXISTS);
            }
        }
    }

    private void getMessages() throws IOException {
        String inputString;
        while (true) {
            inputString = inputStream.readUTF();
            taChat.appendText(inputString + "\n");
        }
    }

    private void setLoginInfo(LoginRegError s) {
        Platform.runLater(() -> lblLoginInfo.setText(s.toString()));
    }

    private void setRegInfo(LoginRegError s) {
        Platform.runLater(() -> lblRegInfo.setText(s.toString()));
        switch (s) {
            case LOGIN_EXISTS:
                tfRegLogin.clear();
                tfRegLogin.requestFocus();
                break;
            case NICKNAME_EXISTS:
                tfRegNickname.clear();
                tfRegNickname.requestFocus();
                break;
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
        mDisconnect.setDisable(true);
        Platform.runLater(this::setTitleStatus);
    }

    public void sendMsg() {
        sendMsg(tfMessage.getText());
    }

    private void sendMsg(String... strings) {
        sendMsg(String.join(" ", strings));
    }

    private void sendMsg(String s) {
        s = s.trim();
        if (!s.isEmpty() & !isSocketOpen()) {
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
        btnLogin.requestFocus();
        String login = tfLogin.getText().trim();
        String pass = tfPassword.getText();
        if (!isSocketOpen()) setLoginInfo(LoginRegError.NO_CONNECTION);
        else if (!login.isEmpty() && !pass.isEmpty()) {
            sendMsg(ControlMessage.AUTH.toString(), login, pass);
        } else {
            tfLogin.setText(login);
            setLoginInfo(LoginRegError.NOT_ENOUGH_DATA);
        }
    }

    public void signUp() {
    }

    public void aboutWindow() {
        setFieldsDisable(true);
        vboxAbout.setVisible(true);
    }

    public void aboutWindowClose() {
        setFieldsDisable(false);
        vboxAbout.setVisible(false);
        tfMessage.requestFocus();
    }

    private void setFieldsDisable(boolean status) {
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
        titleBarController.setTitle(title);
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
        tfLogin.clear();
        tfPassword.clear();
        tfRegLogin.clear();
        tfRegPassword.clear();
        tfRegNickname.clear();
        lblRegInfo.setText("");
        lblLoginInfo.setText("");
    }

    public void signOut() {
        nickname = null;
        taChat.clear();
        setLoginState(true);
    }

    private void setLoginState(boolean status) {
        setFieldsDisable(status);
        if (status) lblLoginInfo.setText("");
        vboxLogin.setVisible(status);
        loginState = status;
    }

    public void disconnect() {
        closeIOStreams();
        if (!loginState) signOut();
    }
}

