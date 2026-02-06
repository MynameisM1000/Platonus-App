package com.example.platonus;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ModalController implements Initializable {
    @FXML private HBox titleBar;
    @FXML private Button minBtn, maxBtn, closeBtn;
    Titlebar titlebar = new Titlebar();
    HelloApplication userData = new HelloApplication();
    ArrayList<String> userdata = userData.UserDataCheck();
    @FXML private Label fullNameHeader, emailHeader;
    @Override public void initialize(URL location, ResourceBundle resources) {
        setupProfileIcon();
        fullNameHeader.setText(userdata.get(0));
        emailHeader.setText(userdata.get(1));
        titlebar.setup(titleBar, minBtn, maxBtn, closeBtn);;
        Platform.runLater(() -> titlebar.maximize(maxBtn));
    }
    public void ToMain(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
    }

    @FXML TextField noteTitle;
    @FXML TextArea noteText;
    public void saveNote(ActionEvent event) throws IOException {
        if(highlightIfEmpty(noteTitle, noteText)){
            System.out.println("Пусто");
        }else{
            writeNote(noteTitle, noteText);
            Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        }
    }

    public void writeNote(TextField field, TextArea textArea) {
        String filePath = "C:/myapp/notes.txt";

        String title = field.getText().trim();
        String text = textArea.getText()
                .replace("\n", " ")
                .replace("\r", " ")
                .trim();

        try (FileWriter writer = new FileWriter(filePath, true)) { // ← ВАЖНО: true
            writer.write(title + System.lineSeparator());
            writer.write(text + System.lineSeparator());
            writer.write("false" + System.lineSeparator());
        } catch (IOException e) {
            System.out.println("Ошибка записи");
        }
    }
    @FXML VBox backPlanets;
    @FXML ImageView profileIconHD;
    private void setupProfileIcon() {
        int idx = Integer.parseInt(userdata.get(5));
        Image image = new Image(getClass().getResourceAsStream("/com/example/platonus/img/profileIcons/"+idx+".png"));
        profileIconHD.setImage(image);
        if (idx == 1 || idx == 3 || idx == 5 || idx == 10 || idx == 8){
            backPlanets.getStyleClass().add("mimiplanets2");
        } else if (idx == 4 || idx == 6 || idx == 7) {
            backPlanets.getStyleClass().add("mimiplanets3");
        } else if (idx == 2 || idx == 9) {
            backPlanets.getStyleClass().add("mimiplanets1");
        }
    }

    private boolean highlightIfEmpty(TextField field, TextArea Text ) {
        if (field.getText().isEmpty() || Text.getText().isEmpty()) {
            if(field.getText().isEmpty()){
                field.setStyle("-fx-border-color: red; -fx-border-width: 2;");
            }if (Text.getText().isEmpty()) {
                Text.setStyle("-fx-border-color: red; -fx-border-width: 2;");
            }
            return true;
        }else {
            field.setStyle("");
            return false;
        }
    }
    public void ToLog(ActionEvent event) throws IOException {
        FileWriter fw = new FileWriter("C:/myapp/profile-data.txt", false);
        FileWriter note = new FileWriter("C:/myapp/notes.txt", false);
        fw.write("");
        note.write("");
        fw.close();
        note.close();

        Parent root = FXMLLoader.load(getClass().getResource("sign/login-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
    }
    public void ToTask(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("task/task.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
    }
    public void ToTable(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("tabel/studentTabel.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
    }
    public void ToChat(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("chat/chat.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
    }
    public void ToProfile(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("profile/profile.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
    }
}
