package com.example.platonus;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.nio.file.Files;

import java.nio.file.Paths;
import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MainController {
    @FXML
    private Label fullName, fullNameHeader, emailHeader;
    @FXML
    VBox timeBox;
    private Image[] images;
    private int currentIndex = 0;
    Connect con = new Connect();
    HelloApplication userData = new HelloApplication();
    ArrayList<String> userdata = userData.UserDataCheck();
    @FXML
    VBox backPlanets;
    @FXML
    ImageView profileIcon;

    // BUTTONS___________________________________________________________________________________________________________
    // title bar
    private final Titlebar titlebar = new Titlebar();
    @FXML
    private HBox titleBar;
    @FXML
    private Button minBtn, maxBtn, closeBtn, bigPlanets;

    @FXML
    VBox notesCard;

    public void initialize() {
        titlebar.setup(titleBar, minBtn, maxBtn, closeBtn);
        Platform.runLater(() -> {
            // Максимизация окна
            titlebar.maximize(maxBtn);
            setupProfileIcon();
            // Загрузка расписания и заметок и фото
            setupSchedule("C:/myapp/profile-data.txt");
            setupNotes();
            setupBigPlanets();
            fullName.setText(userdata.get(0) + ",");
            fullNameHeader.setText(userdata.get(0));
            emailHeader.setText(userdata.get(1));
            // Загрузка одногруппников
            BackgroundTask();
        });
    }

    private void BackgroundTask() {
        new Thread(() -> {
            try {
                Thread.sleep(0);
                javafx.application.Platform.runLater(() -> {
                    SQLNewPotok();
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void SQLNewPotok() {
        Task<ArrayList<String>> task = nameAndEmail(userdata);
        task.setOnSucceeded(e -> {
            ArrayList<String> result = task.getValue();
            showFriends(result);
        });
        task.setOnFailed(e -> {
            System.out.println("Ошибка SQL");
        });

        new Thread(task).start();
    }

    @FXML
    private VBox friendsContainer;

    private void showFriends(ArrayList<String> nameEmailMaybyFriends) {
        String[] planetIcons = {
                "/com/example/platonus/img/planets/orange.png",
                "/com/example/platonus/img/planets/pink.png",
                "/com/example/platonus/img/planets/moon.png"
        };
        for (int i = 0; i + 2 < nameEmailMaybyFriends.size(); i += 3) {
            int cardIndex = i / 3;
            if (cardIndex >= 10)
                break;
            int cycle = (cardIndex % 3) + 1;
            String name = nameEmailMaybyFriends.get(i);
            String email = nameEmailMaybyFriends.get(i + 1);
            String index = nameEmailMaybyFriends.get(i + 2);
            HBox miniCard = new HBox();
            miniCard.setId("miniCard" + cycle);
            miniCard.getStyleClass().add("miniCard");
            miniCard.setPrefHeight(40);
            miniCard.setPrefWidth(320);
            miniCard.setSpacing(10);
            HBox iconBox = new HBox();
            iconBox.getStyleClass().add("mimiplanets" + cycle);
            iconBox.setMinWidth(40);
            iconBox.setMaxHeight(40);
            iconBox.setAlignment(Pos.CENTER);
            ImageView planet = new ImageView(
                    new Image(getClass().getResourceAsStream(planetIcons[(cycle - 1) % planetIcons.length])));
            planet.setFitWidth(35);
            planet.setFitHeight(35);
            iconBox.getChildren().add(planet);
            VBox textBox = new VBox();
            textBox.setPadding(new Insets(2, 0, 0, 10));
            Label nameLabel = new Label(name);
            nameLabel.getStyleClass().add("h4Text");
            Label emailLabel = new Label(email);
            emailLabel.getStyleClass().add("staticText");
            textBox.getChildren().addAll(nameLabel, emailLabel);
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            Region reg = new Region();
            reg.setMinWidth(10);
            Button iconBoxR = new Button();
            iconBoxR.getStyleClass().add("boxRight");
            iconBoxR.setMaxHeight(27);
            iconBoxR.setAlignment(Pos.CENTER);
            ImageView arrow = new ImageView(
                    new Image(getClass().getResourceAsStream("/com/example/platonus/img/icons/right.png")));
            arrow.setFitWidth(12);
            arrow.setFitHeight(12);
            iconBoxR.setGraphic(arrow);
            iconBoxR.setOnAction(event -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("chat/chat.fxml"));
                    Parent root = loader.load();
                    Chat chat = loader.getController();
                    chat.setEmail(name, email, index);
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.getScene().setRoot(root);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            miniCard.getChildren().addAll(iconBox, textBox, spacer, iconBoxR, reg);
            friendsContainer.getChildren().add(miniCard);
        }
    }

    private void setupProfileIcon() {
        int idx = Integer.parseInt(userdata.get(5));
        Image image = new Image(
                getClass().getResourceAsStream("/com/example/platonus/img/profileIcons/" + idx + ".png"));
        profileIcon.setImage(image);
        profileIcon.setFitWidth(40);
        profileIcon.setFitHeight(40);
        if (idx == 1 || idx == 3 || idx == 5 || idx == 10 || idx == 8) {
            backPlanets.getStyleClass().add("mimiplanets2");
        } else if (idx == 4 || idx == 6 || idx == 7) {
            backPlanets.getStyleClass().add("mimiplanets3");
        } else if (idx == 2 || idx == 9) {
            backPlanets.getStyleClass().add("mimiplanets1");
        }
    }

    private Task<ArrayList<String>> nameAndEmail(ArrayList<String> userData) {
        return new Task<>() {
            @Override
            protected ArrayList<String> call() {
                ArrayList<String> lines = new ArrayList<>();
                String sql = "SELECT fio, email, iconindex FROM users WHERE group_id = ? AND fio <> ? ORDER BY id DESC LIMIT 10;";
                try (Connection conn = con.connect();
                        PreparedStatement stmt = conn.prepareStatement(sql)) {

                    stmt.setInt(1, Integer.parseInt(userData.get(3)));
                    stmt.setString(2, userData.get(0));

                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        lines.add(rs.getString("fio"));
                        lines.add(rs.getString("email"));
                        lines.add(rs.getString("iconindex"));
                    }
                } catch (Exception e) {
                    System.out.println("Ошибка запроса");
                }
                return lines;
            }
        };
    }

    private void setupBigPlanets() {
        images = new Image[] {
                new Image(getClass().getResource("/com/example/platonus/img/planets/bigPlanet.png").toExternalForm()),
                new Image(
                        getClass().getResource("/com/example/platonus/img/planets/bigPlanetTwo.png").toExternalForm()),
                new Image(
                        getClass().getResource("/com/example/platonus/img/planets/bigPlanetThree.png").toExternalForm())
        };

        ImageView i = new ImageView(images[currentIndex]);
        i.setFitWidth(320);
        i.setFitHeight(240);
        i.setPreserveRatio(true);
        bigPlanets.setGraphic(i);

        Timeline t = new Timeline(new KeyFrame(Duration.seconds(5), e -> changeImage(i)));
        t.setCycleCount(Timeline.INDEFINITE);
        t.play();

        bigPlanets.setOnAction(e -> {
            t.playFromStart();
            changeImage(i);
        });
    }

    private void changeImage(ImageView i) {
        FadeTransition out = new FadeTransition(Duration.seconds(0.5), i);
        out.setFromValue(1.0);
        out.setToValue(0.0);
        out.setOnFinished(e -> {
            currentIndex = (currentIndex + 1) % images.length;
            i.setImage(images[currentIndex]);
            FadeTransition in = new FadeTransition(Duration.seconds(0.5), i);
            in.setFromValue(0.0);
            in.setToValue(1.0);
            in.play();
        });
        out.play();
    }

    public void setupSchedule(String filePath) {
        String scheduleText;
        try {
            scheduleText = Files.readString(Paths.get(filePath));
        } catch (IOException e) {
            System.out.println("Не удалось прочитать файл");
            return;
        }
        DayOfWeek day = LocalDate.now().getDayOfWeek();
        String currentDay = day.toString().substring(0, 1) + day.toString().substring(1).toLowerCase();
        String[] lines = scheduleText.split("\n");
        int dayIndex = -1;
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].trim().equalsIgnoreCase(currentDay)) {
                dayIndex = i;
                break;
            }
        }
        if (dayIndex == -1) {
            System.out.println("Выходной");
            return;
        }
        List<String> lessons = new ArrayList<>();
        List<String> times = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            if (dayIndex + i < lines.length) {
                String line = lines[dayIndex + i].trim();
                if (!line.isEmpty()) {
                    times.add(line.substring(line.length() - 11));
                    lessons.add(line.substring(0, line.length() - 12));
                }
            }
        }
        if (lessons.isEmpty()) {
            System.out.println("Нет задач");
        } else {
            for (int i = 0; i < times.size(); i++) {
                HBox hbox = new HBox();
                HBox hboxTime = new HBox();
                Label labelTime = new Label(times.get(i));
                labelTime.getStyleClass().add("lessonTime" + (i + 1));
                Label labelName = new Label(lessons.get(i));
                labelName.getStyleClass().add("lessonName");
                hboxTime.getStyleClass().add("hboxTime" + (i + 1));
                hboxTime.setPrefHeight(22);
                hboxTime.setPrefWidth(80);
                hboxTime.getChildren().add(labelTime);
                hbox.setSpacing(10);
                hbox.setAlignment(Pos.CENTER_LEFT);
                hbox.getChildren().add(hboxTime);
                hbox.getChildren().add(labelName);
                timeBox.getChildren().add(hbox);
            }
        }
    }

    public void setupNotes() {
        String filePath = "C:/myapp/notes.txt";
        String noteslines;
        try {
            noteslines = Files.readString(Paths.get(filePath));
        } catch (IOException e) {
            System.out.println("Не удалось прочитать файл");
            return;
        }
        ArrayList<String> Titels = new ArrayList<String>();
        ArrayList<String> Texts = new ArrayList<String>();
        ArrayList<Boolean> Selecked = new ArrayList<Boolean>();
        String[] lines = noteslines.replace("\r", "").split("\n");
        ArrayList<String> cleanLines = new ArrayList<>();
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                cleanLines.add(line.trim());
            }
        }
        for (int i = 0; i + 2 < cleanLines.size(); i += 3) {
            Titels.add(cleanLines.get(i));
            Texts.add(cleanLines.get(i + 1));
            Selecked.add(Boolean.parseBoolean(cleanLines.get(i + 2)));
        }
        if (Titels.isEmpty()) {
            System.out.println("Нет заметок");
        } else {
            for (int i = 0; i < Titels.size(); i++) {
                HBox miniCard = new HBox();
                miniCard.getStyleClass().add("miniCard");
                miniCard.setPrefSize(320, 40);
                HBox miniPlanets = new HBox();
                miniPlanets.setMinWidth(40);
                miniPlanets.setMaxHeight(40);
                miniPlanets.setAlignment(Pos.CENTER);
                miniPlanets.getStyleClass().add("mimiplanets" + ((i % 3) + 1));
                CheckBox checkBox = new CheckBox();
                checkBox.setId("checkBox" + ((i % 3) + 1));
                checkBox.getStyleClass().add("checkBoxs");
                final int idx = i;
                checkBox.setOnAction(e -> writeNote(idx));
                miniPlanets.getChildren().add(checkBox);
                VBox textBox = new VBox();
                textBox.setPadding(new Insets(2, 0, 0, 10));
                Label titleLabel = new Label((Titels.get(i)));
                titleLabel.getStyleClass().add("h4Text");
                Label textLabel = new Label((Texts.get(i)));
                textLabel.getStyleClass().add("staticTextNote");
                textBox.getChildren().addAll(titleLabel, textLabel);
                Region region = new Region();
                HBox.setHgrow(region, Priority.ALWAYS);
                Region reg = new Region();
                reg.setMinWidth(10);
                Button iconBox = new Button();
                iconBox.getStyleClass().add("boxRight");
                iconBox.setMaxHeight(27);
                iconBox.setAlignment(Pos.CENTER);
                ImageView arrow = new ImageView(
                        new Image(getClass().getResourceAsStream("/com/example/platonus/img/icons/right.png")));
                arrow.setFitWidth(12);
                arrow.setFitHeight(12);
                iconBox.setGraphic(arrow);
                iconBox.setOnAction(e -> {
                    try {
                        editNote(e, idx);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
                miniCard.getChildren().addAll(miniPlanets, textBox, region, iconBox, reg);
                notesCard.getChildren().add(miniCard);
                checkBox.setSelected(Selecked.get(i));
            }
        }
    }

    public void writeNote(int index) {
        String filePath = "C:/myapp/notes.txt";
        String noteslines;
        try {
            noteslines = Files.readString(Paths.get(filePath));
        } catch (IOException e) {
            System.out.println("Не удалось прочитать файл");
            return;
        }
        ArrayList<String> Titels = new ArrayList<String>();
        ArrayList<String> Texts = new ArrayList<String>();
        ArrayList<Boolean> Selecked = new ArrayList<Boolean>();
        String[] lines = noteslines.replace("\r", "").split("\n");
        ArrayList<String> cleanLines = new ArrayList<>();
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                cleanLines.add(line.trim());
            }
        }
        for (int i = 0; i + 2 < cleanLines.size(); i += 3) {
            Titels.add(cleanLines.get(i));
            Texts.add(cleanLines.get(i + 1));
            Selecked.add(Boolean.parseBoolean(cleanLines.get(i + 2)));
        }
        Selecked.set(index, !Selecked.get(index));

        ArrayList<String> writerLines = new ArrayList<String>();
        for (int i = 0; i < Titels.size(); i++) {
            writerLines.add(Titels.get(i));
            writerLines.add(Texts.get(i));
            writerLines.add(Selecked.get(i).toString());
        }
        try (FileWriter writer = new FileWriter(filePath)) {
            ArrayList<String> note = new ArrayList<String>();
            for (int i = 0; i < writerLines.size(); i++) {
                note.add(writerLines.get(i));
            }
            for (String s : note) {
                writer.write(s + System.lineSeparator());
            }
        } catch (IOException e) {
            System.out.println("Ошибка записи");
        }

    }

    @FXML
    private void shareBtn() {
        try {
            String message = """
                    **Discover Platonus** — `the smart platform for students! Keep track of your grades, schedule, and assignments all in one place, receive important notifications from your university, and always stay one step ahead. Platonus makes learning easier, faster, and more efficient!`

                    **Откройте для себя Платонус** — `умную платформу для студентов! Следите за своими оценками, расписанием и заданиями в одном месте, получайте важные уведомления от университета и будьте всегда на шаг впереди. Платонус делает обучение проще, удобнее и эффективнее!`
                                        """;
            java.awt.Toolkit.getDefaultToolkit().getSystemClipboard()
                    .setContents(new java.awt.datatransfer.StringSelection(message), null);
            String url = "tg://msg?text=" + java.net.URLEncoder.encode(message, "UTF-8");
            ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "start", url);
            pb.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -------------- Переходы
    public void modalNote(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("note/modalNote.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
    }

    public void editNote(ActionEvent event, int idx) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("note/noteEdit.fxml"));
        Parent root = loader.load();
        noteEditController editController = loader.getController();
        editController.setIdx(idx);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
    }

    // header преходы
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
