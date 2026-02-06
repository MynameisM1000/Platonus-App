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
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.w3c.dom.Text;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class noteEditController implements Initializable {
    @FXML private HBox titleBar;
    @FXML private Button minBtn, maxBtn, closeBtn;
    @FXML int indexEditing;
    @FXML TextField noteTitle;
    @FXML TextArea noteText;
    ArrayList<String> TitleAndText = new ArrayList<String>();
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
    public void setIdx(int idx) {
        indexEditing = idx;
        TitleAndText = getInfo(indexEditing);
        noteTitle.setText(TitleAndText.get(0));
        noteText.setText(TitleAndText.get(1));
        installPlanet(indexEditing+1);
    }

    public void saveNote(ActionEvent event) throws IOException {
        if(highlightIfEmpty(noteTitle, noteText)){
            System.out.println("Пусто");
        }else{
            writeNote(noteTitle,noteText,indexEditing);
            Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        }
    }
    public void delNote(ActionEvent event) throws IOException {
        deleNote(indexEditing);
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
    }
    public ArrayList<String> getInfo(int index) {
        String filePath = "C:/myapp/notes.txt";
        String noteslines;
        ArrayList<String> Titels = new ArrayList<String>();
        ArrayList<String> Texts = new ArrayList<String>();
        ArrayList<Boolean> Selecked = new ArrayList<Boolean>();
        try {
            noteslines = Files.readString(Paths.get(filePath));
        } catch (IOException e) {
            System.out.println("Не удалось прочитать файл");
            return Titels;
        }
        String[] lines = noteslines.replace("\r", "").split("\n");
        ArrayList<String> cleanLines = new ArrayList<>();
        for(String line : lines){
            if(!line.trim().isEmpty()){
                cleanLines.add(line.trim());
            }
        }
        for (int i = 0; i + 2 < cleanLines.size(); i += 3) {
            Titels.add(cleanLines.get(i));
            Texts.add(cleanLines.get(i + 1));
            Selecked.add(Boolean.parseBoolean(cleanLines.get(i + 2)));
        }
        ArrayList<String> info = new ArrayList<String>();
        info.add(Titels.get(index));
        info.add(Texts.get(index));
        return info;
    }

    public void writeNote(TextField field, TextArea Text, int idx) {
        String filePath = "C:/myapp/notes.txt";
        String noteslines;
        try {
            noteslines = Files.readString(Paths.get(filePath));
        } catch (IOException e) {
            System.out.println("Не удалось прочитать файл");
            return;
        }
        String[] lines = noteslines.replace("\r", "").split("\n");
        ArrayList<String> cleanLines = new ArrayList<>();
        for(String line : lines){
            if(!line.trim().isEmpty()){
                cleanLines.add(line.trim());
            }
        }
        cleanLines.set((idx*3), field.getText().replace("\r", ""));
        cleanLines.set((idx*3+1), Text.getText().replace("\r", "").replace("\n", " "));
        try (FileWriter writer = new FileWriter(filePath)) {
            for (String s : cleanLines) {
                writer.write(s + System.lineSeparator());
            }
        } catch (IOException e) {
            System.out.println("Ошибка записи");
        }
    }
    public void deleNote(int idx) {
        String filePath = "C:/myapp/notes.txt";
        String noteslines;
        try {
            noteslines = Files.readString(Paths.get(filePath));
        } catch (IOException e) {
            System.out.println("Не удалось прочитать файл");
            return;
        }
        String[] lines = noteslines.replace("\r", "").split("\n");
        ArrayList<String> cleanLines = new ArrayList<>();
        for(String line : lines){
            if(!line.trim().isEmpty()){
                cleanLines.add(line.trim());
            }
        }
        cleanLines.remove(idx*3 + 2);
        cleanLines.remove(idx*3 + 1);
        cleanLines.remove(idx*3);

        try (FileWriter writer = new FileWriter(filePath)) {
            for (String s : cleanLines) {
                writer.write(s + System.lineSeparator());
            }
        } catch (IOException e) {
            System.out.println("Ошибка записи");
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
    @FXML ImageView bigHalfPlanet;
    private void installPlanet(int idx) {
        InputStream stream = getClass().getResourceAsStream("/com/example/platonus/img/planets/bigPlanetHalf" + idx + ".png");
        if (stream != null) {
            Image img = new Image(stream);
            bigHalfPlanet.setFitWidth(460);
            bigHalfPlanet.setFitHeight(220);
            bigHalfPlanet.setImage(img);
        } else {
            System.out.println("Изображение не найдено: " + idx);
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
