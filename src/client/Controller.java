// TODO: 23.10.2019 private message input field
// TODO: 23.10.2019 Connect menu button
// TODO: 23.10.2019 SignOut menu button with server realization
// TODO: 23.10.2019 English|Russian switch with server relization. String resources for translation

package client;

import client.controller.TitleBarController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import resources.ControlMessage;
import resources.LoginRegError;
import server.service.FormatChecker;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.Arrays;
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
    private ListView<String> listUsers;
    @FXML
    private TextField tfMessage, tfLogin, tfRegLogin, tfRegNickname;
    @FXML
    private PasswordField tfPassword, tfRegPassword;
    @FXML
    private Button btnSend, btnLogin, btnReg;
    @FXML
    private MenuItem mClear, mAbout, mSignOut, mDisconnect;
    @FXML
    private Label lblLoginInfo, lblRegInfo, lblOnline;
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
        String[] inputString;
        while (true) {
            inputString = inputStream.readUTF().split(" ", 2);
            if (ControlMessage.AUTH_OK.check(inputString[0]) && vboxLogin.isVisible()) {
                nickname = inputString[1];
                setLoginState(false);
                break;
            } else if (ControlMessage.ERROR.check(inputString[0]) && vboxLogin.isVisible()) {
                setLoginInfo(inputString[1]);
            } else if (ControlMessage.REG_OK.check(inputString[0]) && vboxRegistration.isVisible()) {
                String login = tfRegLogin.getText().trim();
                String pass = tfRegPassword.getText();
                swapLoginReg(login, pass);
            } else if (ControlMessage.ERROR.check(inputString[0]) && vboxRegistration.isVisible()) {
                setRegInfo(inputString[1]);
            }
        }
    }

    private void getMessages() throws IOException {
        String inputString;
        while (true) {
            inputString = inputStream.readUTF();
            if (ControlMessage.isControlMessage(inputString)) executeControlMessage(inputString);
            else {
                String finalInputString = inputString;
                Platform.runLater(() -> taChat.appendText(finalInputString + "\n"));
            }
        }
    }

    private void executeControlMessage(String inputString) {
        String[] controlStr = inputString.split(" ", 2);
        if (ControlMessage.CHAT_HISTORY.check(controlStr[0]) && controlStr.length > 1)
            Platform.runLater(() -> taChat.insertText(0, controlStr[1]));
            // TODO: 21.10.2019 сделать скролл вниз сразу после добавления истории (временное решение - задержка отправки welcomeMsg)
        else if (ControlMessage.USERLIST.check(controlStr[0]) && controlStr.length > 1) {
            fillUserList(controlStr[1].split(" "));
        }
    }

    private void fillUserList(String[] list) {
        Platform.runLater(() -> {
            listUsers.getItems().clear();
            Arrays.sort(list);
            for (String nickname : list) {
                listUsers.getItems().add(nickname);
            }
            lblOnline.setText("Online: " + list.length);
        });
    }

    private void clearUserList() {
        Platform.runLater(() -> {
            listUsers.getItems().clear();
            lblOnline.setText("");
        });
    }

    private void setLoginInfo(String s) {
        LoginRegError error = getErrorString(s);
        setLoginInfo(error);
    }

    private void setLoginInfo(LoginRegError error) {
        Platform.runLater(() -> {
            lblLoginInfo.setText(error.toString());
            tfLogin.requestFocus();
        });
    }

    private void setRegInfo(String s) {
        LoginRegError error = getErrorString(s);
        setRegInfo(error);
    }

    private void setRegInfo(LoginRegError error) {
        Platform.runLater(() -> {
            lblRegInfo.setText(error.toString());
            switch (error) {
                case LOGIN_EXISTS:
                    tfRegLogin.clear();
                    tfRegLogin.requestFocus();
                    break;
                case NICKNAME_EXISTS:
                    tfRegNickname.clear();
                    tfRegNickname.requestFocus();
                    break;
            }
        });
    }

    private LoginRegError getErrorString(String index) {
        LoginRegError errorString;
        try {
            int i = Integer.parseInt(index);
            if (i < LoginRegError.values().length)
                errorString = LoginRegError.values()[i];
            else errorString = LoginRegError.RESPONSE_ERROR;
        } catch (NumberFormatException e) {
            errorString = LoginRegError.RESPONSE_ERROR;
        }
        return errorString;
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
        setTitleStatus();
    }

    public void sendMsg() {
        sendMsg(tfMessage.getText());
    }

    private void sendMsg(ControlMessage c, String... strings) {
        sendMsg(String.join(" ", c.toString(), String.join(" ", strings)));
    }

    private void sendMsg(String s) {
        s = s.trim();
        if (ControlMessage.HELP.check(s)) showHelpMessage();
        else if (!s.isEmpty() & isSocketOpen()) {
            try {
                outputStream.writeUTF(s);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        tfMessage.clear();
        tfMessage.requestFocus();
    }

    private void showHelpMessage() {
        taChat.appendText("Перечень доступных команд:\n");
        for (ControlMessage msg : ControlMessage.values()) {
            if (msg.hasDescription()) taChat.appendText(msg.getFullDescription() + "\n");
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
        String pass = getPasswordString(tfPassword.getText());
        if (!isSocketOpen()) setLoginInfo(LoginRegError.NO_CONNECTION);
        else if (!login.isEmpty() && !pass.isEmpty()) {
            sendMsg(ControlMessage.AUTH, login, pass);
        } else {
            Platform.runLater(() -> tfLogin.setText(login));
            setLoginInfo(LoginRegError.NOT_ENOUGH_DATA);
        }
    }

    private String getPasswordString(String s) {
        return s.trim()
                .replace(" ", "%20")
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("'", "\\'");
    }

    public void signUp() {
        btnReg.requestFocus();
        String login = tfRegLogin.getText().trim();
        String pass = getPasswordString(tfRegPassword.getText());
        String nick = tfRegNickname.getText().trim();
        FormatChecker formatChecker = new FormatChecker();
        if (!isSocketOpen()) setRegInfo(LoginRegError.NO_CONNECTION);
        else if (!formatChecker.checkLoginFormat(login)) setRegInfo(formatChecker.getCurrentError());
        else if (!formatChecker.checkPasswordFormat(pass)) setRegInfo(formatChecker.getCurrentError());
        else if (!formatChecker.checkNicknameFormat(nick)) setRegInfo(formatChecker.getCurrentError());
        else if (!login.isEmpty() && !pass.isEmpty() && !nick.isEmpty()) {
            sendMsg(ControlMessage.REG, login, pass, nick);
        } else {
            Platform.runLater(() -> {
                tfRegLogin.setText(login);
                tfRegNickname.setText(nick);
                setRegInfo(LoginRegError.NOT_ENOUGH_DATA);
            });
        }
    }


    public void aboutWindow() {
        setElementsDisable(true);
        vboxAbout.setVisible(true);
    }

    public void aboutWindowClose() {
        setElementsDisable(false);
        vboxAbout.setVisible(false);
        tfMessage.requestFocus();
    }

    private void setElementsDisable(boolean status) {
        Platform.runLater(() -> {
            btnSend.setDisable(status);
            tfMessage.setDisable(status);
            taChat.setDisable(status);
            listUsers.setDisable(status);
            mAbout.setDisable(status);
            mClear.setDisable(status);
            // mSignOut.setDisable(status);
        });
    }

    private void setElementsVisible(boolean status) {
        btnSend.setVisible(status);
        tfMessage.setVisible(status);
        taChat.setVisible(status);
        listUsers.setVisible(status);
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
        setWindowTitle();
    }

    private void setWindowTitle() {
        Platform.runLater(() -> {
            try {
                if (nickname != null)
                    ((Stage) mainPane.getScene().getWindow()).setTitle("GB Chat - " + nickname);
                else ((Stage) mainPane.getScene().getWindow()).setTitle("GB Chat");
            } catch (NullPointerException ignored) {
            }
        });
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
        swapLoginReg("", "");
    }

    public void swapLoginReg(String login, String pass) {
        Platform.runLater(() -> {
            vboxLogin.setVisible(!vboxLogin.isVisible());
            vboxRegistration.setVisible(!vboxLogin.isVisible());
            tfLogin.setText(login);
            tfPassword.setText(pass);
            tfRegLogin.clear();
            tfRegPassword.clear();
            tfRegNickname.clear();
            lblRegInfo.setText("");
            lblLoginInfo.setText("");
        });
    }

    public void signOut() {
        nickname = null;
        taChat.clear();
        clearUserList();
        setLoginState(true);
    }

    private void setLoginState(boolean status) {
        setElementsDisable(status);
        setElementsVisible(!status);
        if (status) lblLoginInfo.setText("");
        vboxLogin.setVisible(status);
        loginState = status;
        setTitleStatus();
    }

    public void disconnect() {
        closeIOStreams();
        if (!loginState) signOut();
    }

    public void listClick(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() < 2) return;
        Platform.runLater(() -> {
            String nick = listUsers.getSelectionModel().getSelectedItem();
            if (nick != null && !nick.equals(nickname)) {
                listUsers.requestFocus();
                tfMessage.setText("/w " + nick + " ");
            }
            tfMessage.requestFocus();
            tfMessage.positionCaret(tfMessage.getText().length());
        });
    }
}

