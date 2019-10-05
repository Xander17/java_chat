package client;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;


public class Controller implements Initializable {
    @FXML
    Label titleLabel;
    @FXML
    BorderPane titleBox;
    @FXML
    ToggleGroup skinsGroup;
    @FXML
    VBox vboxNickname, vboxAbout;
    @FXML
    TextArea taChat;
    @FXML
    TextField tfMessage, tfNickname;
    @FXML
    Button btnSend;
    @FXML
    MenuItem mClear, mAbout, mNick;
    @FXML
    BorderPane mainPane;

    private String nickname;
    private boolean isMaximized = false;
    private double lastW, lastH;
    private double dragX, dragY;

    private DataInputStream inputStream = null;
    private DataOutputStream outputStream = null;
    private Socket socket = null;

    private final String IP_ADDRESS = "localhost";
    private final int PORT = 8190;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            socket = new Socket(IP_ADDRESS, PORT);
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
            new Thread(() -> {
                try {
                    String inputString;
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
            setTitleStatus();
        }
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
        Platform.runLater(this::setTitleStatus);
    }

    public void sendMsg() {
        String s = tfMessage.getText().trim();
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
        closeIOStreams();
        Stage stage = (Stage) mainPane.getScene().getWindow();
        stage.close();
    }

    public void setNickname() {
        String s = tfNickname.getText().trim();
        tfNickname.setText(s);
        tfNickname.requestFocus();
        if (!s.isEmpty()) {
            nickname = tfNickname.getText();
            setTitleStatus();
            changeDisable(false);
            vboxNickname.setVisible(false);
            tfMessage.requestFocus();
        }
    }

    public void changeNick() {
        changeDisable(true);
        vboxNickname.setVisible(true);
        tfNickname.requestFocus();
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
        mNick.setDisable(status);
    }

    public void setStyle() {
        MenuItem m = (MenuItem) skinsGroup.getSelectedToggle();
        String pathToCSS = "/client/css/" + m.getText().toLowerCase() + ".css";
        mainPane.getScene().getStylesheets().remove(2);
        mainPane.getScene().getStylesheets().add(getClass().getResource(pathToCSS).toExternalForm());
    }

    public void maximize() {
        Stage stage = (Stage) titleBox.getScene().getWindow();
        ObservableList<Screen> screens = Screen.getScreensForRectangle(new Rectangle2D(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight()));
        Rectangle2D bounds = screens.get(0).getVisualBounds();
        if (!isMaximized) {
            saveWindowState(stage);
            setWindowState(stage, bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());
        } else {
            double newX = bounds.getMinX() + (bounds.getMaxX() - bounds.getMinX() - lastW) / 2;
            double newY = bounds.getMinY() + (bounds.getMaxY() - bounds.getMinY() - lastH) / 2;
            setWindowState(stage, newX, newY, lastW, lastH);
        }
        isMaximized = !isMaximized;
    }

    private void saveWindowState(Stage stage) {
        lastW = stage.getWidth();
        lastH = stage.getHeight();
    }

    private void setWindowState(Stage stage, double x, double y, double width, double height) {
        stage.setX(x);
        stage.setY(y);
        stage.setWidth(width);
        stage.setHeight(height);
    }

    public void minimize() {
        ((Stage) titleBox.getScene().getWindow()).setIconified(true);
    }

    public void titlePressed(MouseEvent mouseEvent) {
        dragX = mouseEvent.getSceneX();
        dragY = mouseEvent.getSceneY();
    }

    public void titleDragged(MouseEvent mouseEvent) {
        Stage stage = (Stage) titleBox.getScene().getWindow();
        stage.setX(mouseEvent.getScreenX() - dragX);
        stage.setY(mouseEvent.getScreenY() - dragY);
    }

    private void setTitleStatus() {
        titleLabel.setText("GB Chat [Nickname: " + nickname + "]" + (socket.isClosed() ? " [No connection]" : ""));
//        System.out.println("GB Chat [Nickname: " + nickname + "]" + (socket.isClosed() ? " [No connection]" : ""));
    }
}

