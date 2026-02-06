package com.example.platonus;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Titlebar {
    private double xOffset = 0;
    private double yOffset = 0;
    private boolean isMaximizedManual = true;


    public void setup(HBox titleBar, Button minBtn, Button maxBtn, Button closeBtn) {
        titleBar.setOnMousePressed(e -> {
            xOffset = e.getSceneX();
            yOffset = e.getSceneY();
        });
        titleBar.setOnMouseDragged(e -> {
            Stage stage = (Stage) titleBar.getScene().getWindow();
            stage.setX(e.getScreenX() - xOffset);
            stage.setY(e.getScreenY() - yOffset);
        });
        minBtn.setOnAction(e -> {
            ((Stage) minBtn.getScene().getWindow()).setIconified(true);
        });
        maxBtn.setOnAction(e -> {
            Stage stage = (Stage) maxBtn.getScene().getWindow();

            if (isMaximizedManual) {
                stage.setWidth(1200);
                stage.setHeight(800);
                stage.centerOnScreen();
                isMaximizedManual = false;
            } else {
                Rectangle2D screen = Screen.getPrimary().getVisualBounds();
                isMaximizedManual = true;
                stage.setX(screen.getMinX());
                stage.setY(screen.getMinY());
                stage.setWidth(screen.getWidth());
                stage.setHeight(screen.getHeight());
            }
        });
        closeBtn.setOnAction(e -> {
            ((Stage) closeBtn.getScene().getWindow()).close();
        });
    }
    public void maximize(Button maxBtn) {
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        Stage stage = (Stage) maxBtn.getScene().getWindow();
        isMaximizedManual = true;
        stage.setX(screen.getMinX());
        stage.setY(screen.getMinY());
        stage.setWidth(screen.getWidth());
        stage.setHeight(screen.getHeight());
    }
}
