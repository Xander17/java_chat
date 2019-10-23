package client.controller;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class TitleBarController {

    private boolean isMaximized = false;
    private double lastW, lastH;
    private double dragX, dragY;

    @FXML
    BorderPane titleBox;
    @FXML
    Label titleLabel;

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

    public void exitChat() {
        Platform.exit();
        System.exit(0);
    }

    public void setTitle(String title) {
        Platform.runLater(() -> titleLabel.setText(title));
    }

}
