package com.example.platonus;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import javafx.concurrent.Task;
import javafx.util.Duration;

public class Tasks {
    @FXML private Label fullNameHeader, emailHeader;
    HelloApplication userData = new HelloApplication();
    ArrayList<String> userdata = userData.UserDataCheck();
    private final Titlebar titlebar = new Titlebar();
    @FXML private HBox titleBar;
    @FXML private Button minBtn, maxBtn, closeBtn, remove;
    @FXML VBox backPlanets;
    @FXML ImageView profileIconHD;
    Connect con = new Connect();
    ArrayList<ArrayList<String>> tasks;

    @FXML ScrollPane taskBox;
    public void initialize() {
        titlebar.setup(titleBar, minBtn, maxBtn, closeBtn);
        setupProfileIcon();
        SQLNewPotok();
        Platform.runLater(() -> {
            titlebar.maximize(maxBtn);
            fullNameHeader.setText(userdata.get(0));
            emailHeader.setText(userdata.get(1));
        });
    }

    private void displayTasks() {
        // Очистка текущего содержимого
        VBox content = new VBox();
        content.setSpacing(10); // Расстояние между задачами
        content.setStyle("-fx-padding: 10; -fx-background-color: transparent;");

        if (tasks == null || tasks.isEmpty()) {
            Label emptyLabel = new Label("Нет задач для отображения");
            emptyLabel.setStyle("-fx-text-fill: gray; -fx-font-size: 14px;");
            content.getChildren().add(emptyLabel);
        } else {
            for (ArrayList<String> task : tasks) {
                String title = task.get(0);
                String text = task.get(1);
                String publishDate = task.get(2);
                String deadline = task.get(3);

                VBox taskCard = new VBox();
                taskCard.setSpacing(5);
                taskCard.getStyleClass().add("card");
                taskCard.setStyle("""
                -fx-padding: 15;
            """);

                Label titleLabel = new Label(title);
                titleLabel.getStyleClass().add("h4Text");

                Label textLabel = new Label(text);
                textLabel.setWrapText(true);
                textLabel.getStyleClass().add("h5Text");

                Label dateLabel = new Label("Published: " + publishDate + " | Deadline: " + deadline);
                dateLabel.getStyleClass().add("staticText");

                dateLabel.setMaxWidth(Double.MAX_VALUE);
                dateLabel.setAlignment(Pos.BASELINE_RIGHT);
                HBox.setHgrow(dateLabel, Priority.ALWAYS);
                taskCard.getChildren().addAll(titleLabel, textLabel, dateLabel);

                content.getChildren().add(taskCard);
            }
        }

        taskBox.setContent(content);
    }


    private void SQLNewPotok() {
        Task<ArrayList<ArrayList<String>>> task = getTasks(userdata);
        task.setOnSucceeded(e -> {
            ArrayList<ArrayList<String>> result = task.getValue();
            tasks = result;
            displayTasks();
        });
        task.setOnFailed(e -> {
            System.out.println("Ошибка SQL");
        });
        new Thread(task).start();
    }
    private Task<ArrayList<ArrayList<String>>> getTasks(ArrayList<String> userData) {
        return new Task<>() {
            @Override
            protected ArrayList<ArrayList<String>> call() {
                ArrayList<ArrayList<String>> lines = new ArrayList<>();
                String sql = "SELECT title, text, publish_date, deadline FROM tasks WHERE group_id = ? ;";
                try (Connection conn = con.connect();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, Integer.parseInt(userData.get(3)));

                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        ArrayList<String> line = new ArrayList<>();
                        line.add(rs.getString("title"));
                        line.add(rs.getString("text"));
                        line.add(rs.getString("publish_date"));
                        line.add(rs.getString("deadline"));
                        lines.add(line);
                    }
                } catch (Exception e) {
                    System.out.println("Ошибка запроса");
                }
                return lines;
            }
        };
    }

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

    public void ToProfile(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("profile/profile.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
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
}
