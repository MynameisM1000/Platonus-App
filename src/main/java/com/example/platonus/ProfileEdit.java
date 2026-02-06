package com.example.platonus;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class ProfileEdit {
    Titlebar titlebar = new Titlebar();
    @FXML private HBox titleBar;
    @FXML private Button minBtn, maxBtn, closeBtn;
    HelloApplication user = new HelloApplication();
    ArrayList<String> userData = user.UserDataCheck();
    @FXML TextField nameField;
    Connect con = new Connect();
    @FXML int chosedIconIndex = Integer.parseInt(userData.get(5));
    public void initialize() {
        titlebar.setup(titleBar, minBtn, maxBtn, closeBtn);
        nameField.setText(userData.get(0));
        changeIcon();
    }

    private ImageView selectedIcon = null;
    private HBox selectedBox = null;
    @FXML private VBox icons;
    private void changeIcon() {
        int index = Integer.parseInt(userData.get(5));
        HBox second = new HBox();
        HBox fisrt = new HBox();
        fisrt.setSpacing(20);
        second.setSpacing(20);
        for (int idx=1; idx<11;idx++){
            HBox box = new HBox();
            ImageView icon = new ImageView(
                    new Image(getClass().getResourceAsStream("/com/example/platonus/img/profileIcons/" + idx + ".png"))
            );
            int indexOfButton = idx;
            icon.setFitWidth(180);
            icon.setFitHeight(180);
            icon.getStyleClass().add("icons");
            if (index == idx) {
                icon.setId("chosed");
                box.setId("chosedBox");
                chosedIconIndex = indexOfButton;
                selectedIcon = icon;
                selectedBox = box;
            }
            box.setMinWidth(200);
            box.setMinHeight(200);
            box.setAlignment(Pos.CENTER);
            if (idx == 1 || idx == 3 || idx == 5 || idx == 10 || idx == 8){
                box.getStyleClass().add("mimiplanet2");
            } else if (idx == 4 || idx == 6 || idx == 7) {
                box.getStyleClass().add("mimiplanet3");
            } else if (idx == 2 || idx == 9) {
                box.getStyleClass().add("mimiplanet1");
            }
            box.getChildren().add(icon);
            Button cardButton = new Button();
            cardButton.setGraphic(box);
            cardButton.getStyleClass().add("iconButton");
            cardButton.setOnAction(e->{
                if (selectedIcon != null) selectedIcon.setId(null);
                if (selectedBox != null) selectedBox.setId(null);
                icon.setId("chosed");
                box.setId("chosedBox");
                chosedIconIndex = indexOfButton;
                selectedIcon = icon;
                selectedBox = box;
            });
            if (idx < 6 && idx > 0) {
                fisrt.getChildren().add(cardButton);
            }else {
                second.getChildren().add(cardButton);
            }
        }
        icons.getChildren().addAll(fisrt, second);

    }
    public void SaveEdit(ActionEvent event) throws IOException{
        String name = nameField.getText().trim();
        int iconIndex = chosedIconIndex;
        String filePath = "C:/myapp/profile-data.txt";
        String noteslines;
        try {
            noteslines = Files.readString(Paths.get(filePath));
        } catch (IOException e) {
            System.out.println("Не удалось прочитать файл");
            return;
        }
        ArrayList<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("C:/myapp/profile-data.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.out.println("Файла нету");
        }
        lines.set(5, iconIndex+"");
        lines.set(0, name);
        try (FileWriter writer = new FileWriter(filePath)) {
            for (String s : lines) {
                writer.write(s + System.lineSeparator());
            }
        } catch (IOException e) {
            System.out.println("Ошибка записи");
        }
        changeNameIndex();
        Parent root = FXMLLoader.load(getClass().getResource("profile/profile.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
    }
    public void changeNameIndex() {
        userData = user.UserDataCheck();
        Task<Void> del = new Task<>() {
            @Override
            protected Void call() {
                String sql = "UPDATE users SET iconindex = ?, fio = ? WHERE id = ?;";
                try (Connection conn = con.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, Integer.parseInt(userData.get(5)));
                    ps.setString(2, userData.get(0));
                    ps.setInt(3, Integer.parseInt(userData.get(4)));
                    ps.executeUpdate();
                } catch (SQLException e) {
                    System.out.println("Ошибка таска инсерд");
                }
                return null;
            }
        };
        new Thread(del).start();
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
    public void ToMain(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
    }
    public void ToProfile(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("profile/profile.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
    }
}
