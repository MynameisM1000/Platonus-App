package com.example.platonus;

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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class Chat {
    private final Titlebar titlebar = new Titlebar();
    @FXML private HBox titleBar;
    @FXML private Button minBtn, maxBtn, closeBtn;
//    USERS DATA
    HelloApplication userData = new HelloApplication();
    ArrayList<String> userDatas = new ArrayList<>();
    ArrayList<String[]> people = new ArrayList<>();
//    SQL
    Connect con = new Connect();

//    CHAT DATA
    public String sender;
    public String receiverEmail;
    public String receiverName;
    @FXML TextField inputChat;
    @FXML VBox chatContainer;
    @FXML Label chatName,chatEmail;
    @FXML ImageView profileIcon;
    @FXML VBox chatBox, backIcon;
    @FXML ScrollPane chatScroll;
    @FXML VBox backPlanets;
    @FXML ImageView profileIconHD;
    @FXML private Label fullNameHeader, emailHeader;
    public void initialize() {
        titlebar.setup(titleBar, minBtn, maxBtn, closeBtn);
        userDatas = userData.UserDataCheck();
        SQLNewpotok();
        Platform.runLater(() -> {
            titlebar.maximize(maxBtn);});

//        background
        Image image = new Image(getClass().getResource("/com/example/platonus/img/background/chatBack1.png").toExternalForm());
        BackgroundImage bgImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER, new BackgroundSize(80,80,true,true,true, true));
        chatContainer.setBackground(new Background(bgImage));

//        defolt chat
        sender=userDatas.get(1);
        fullNameHeader.setText(userDatas.get(0));
        emailHeader.setText(userDatas.get(1));
        setupProfileIcon();
    }
    @FXML VBox friendsList;

    public Task<ArrayList<ArrayList<String>>> setMessages() {
        return new Task<>(){
            @Override
            protected ArrayList<ArrayList<String>> call() {
                ArrayList<ArrayList<String>> messages = new ArrayList<>();
                String sql = "SELECT sender, content, timestamp FROM messages " +
                        "WHERE (sender = ? AND receiver = ?) OR (sender = ? AND receiver = ?) " +
                        "ORDER BY timestamp ASC";
                try (Connection conn = con.connect();
                     PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, sender);
                    ps.setString(2, receiverEmail);

                    ps.setString(3, receiverEmail);
                    ps.setString(4, sender);

                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        ArrayList<String> message = new ArrayList<>();
                        message.add(rs.getString("sender"));
                        message.add(rs.getString("content"));
                        message.add(rs.getString("timestamp"));
                        messages.add(message);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return messages;
            }
        };
    }
    Boolean openinMain = false;
    @FXML
    public void setEmail(String email, String name, String index) {
        setChatDatas(email, name, index);
        openinMain=true;
    }

    private void setupMessages(ArrayList<ArrayList<String>> messages) {
        chatBox.getChildren().clear();
        for (int i = 0; i < messages.size(); i++) {
            ArrayList<String> msg = messages.get(i);
            String senderName = msg.get(0);
            String content = msg.get(1);
            String timestamp = msg.get(2);
            String time = timestamp.substring(11, 16);

            Label nameLabel = new Label();
            nameLabel.getStyleClass().add("Name");

            Label messageLabel = new Label(content);
            messageLabel.getStyleClass().add("messageText");
            messageLabel.setWrapText(true);
            Label timeLabel = new Label(time);
            timeLabel.getStyleClass().add("data");

            HBox timeBox = new HBox(timeLabel);
            timeBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

            VBox messageVBox = new VBox();
            messageVBox.getChildren().addAll(nameLabel, messageLabel, timeBox);
            messageVBox.setMinHeight(30);
            messageVBox.setMaxWidth(300);
            messageVBox.setPadding(new Insets(10, 20, 10, 20));

            HBox messageHBox = new HBox(messageVBox);
            HBox.setHgrow(messageVBox, javafx.scene.layout.Priority.ALWAYS);

            if (senderName.equals(userDatas.get(1))) {
                nameLabel.setText(userDatas.get(0));
                messageVBox.getStyleClass().add("myMess");
                messageHBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
            } else {
                nameLabel.setText(receiverName);
                messageVBox.getStyleClass().add("parMess");
                messageHBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            }
            chatBox.getChildren().add(messageHBox);
        }
        chatScroll.setVvalue(1.0);
    }


    public void sendMassage() {
        String message = inputChat.getText().trim();
        if (message.isEmpty()) return;
        inputChat.clear();
        Task<Void> insertTask = new Task<>() {
            @Override
            protected Void call() {
                String sql = "INSERT INTO messages (sender, receiver, content) VALUES (?, ?, ?)";
                try (Connection conn = con.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, sender);
                    ps.setString(2, receiverEmail);
                    ps.setString(3, message);
                    ps.executeUpdate();
                } catch (SQLException e) {
                    System.out.println("Ошибка таска инсерд");
                }
                return null;
            }
        };
        insertTask.setOnSucceeded(e -> {
            Task<ArrayList<ArrayList<String>>> task = setMessages();
            task.setOnSucceeded(ev -> {
                setupMessages(task.getValue());
                chatScroll.layout();
                chatScroll.setVvalue(1.0);
            });
            new Thread(task).start();
        });
        new Thread(insertTask).start();
    }

    private void setChatDatas(String name,String email,String index) {
        chatBox.getChildren().clear();
        receiverEmail=email;
        receiverName=name;
        chatName.setText(name);
        chatEmail.setText(email);
        profileIcon.setImage(new Image(getClass().getResource("/com/example/platonus/img/profileIcons/"+index+".png").toExternalForm()));
        int idx = Integer.parseInt(index);
        backIcon.getStyleClass().removeAll("mimiplanet1", "mimiplanet2", "mimiplanet3");
        if (idx == 1 || idx == 3 || idx == 5 || idx == 10 || idx == 8){
            backIcon.getStyleClass().add("mimiplanet2");
        } else if (idx == 4 || idx == 6 || idx == 7) {
            backIcon.getStyleClass().add("mimiplanet3");
        } else if (idx == 2 || idx == 9) {
            backIcon.getStyleClass().add("mimiplanet1");
        }
        Task<ArrayList<ArrayList<String>>> task = setMessages();
        task.setOnSucceeded(e -> {
            setupMessages(task.getValue());
            markMessagesAsRead();
        });
        new Thread(task).start();
    }

    private void markMessagesAsRead() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                String sql = "UPDATE messages SET is_read = TRUE WHERE sender = ? AND receiver = ? AND is_read = FALSE";
                try (Connection conn = con.connect();
                     PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, receiverEmail);
                    ps.setString(2, sender);
                    ps.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        new Thread(task).start();
    }


    private void setPeople(ArrayList<String[]> people) {
        for (int i = 0; i < people.size(); i++) {
            String name = people.get(i)[0];
            String email = people.get(i)[1];
            String iconIndex = people.get(i)[2];
            String unread = people.get(i)[3];
            HBox miniCard = new HBox();
            miniCard.getStyleClass().add("miniCard");
            miniCard.setPrefHeight(40);
            miniCard.setPrefWidth(320);
            miniCard.setSpacing(10);

            HBox iconBox = new HBox();
            iconBox.setMinWidth(40);
            iconBox.setMaxHeight(40);
            iconBox.setAlignment(Pos.CENTER);

            ImageView planet = new ImageView(
                    new Image(getClass().getResourceAsStream(
                            "/com/example/platonus/img/profileIcons/" + iconIndex + ".png"
                    ))
            );
            planet.setFitWidth(35);
            planet.setFitHeight(35);
            iconBox.getChildren().add(planet);
            int idx = Integer.parseInt(iconIndex);
            if (idx == 1 || idx == 3 || idx == 5 || idx == 10 || idx == 8){
                iconBox.getStyleClass().add("mimiplanet2");
            } else if (idx == 4 || idx == 6 || idx == 7) {
                iconBox.getStyleClass().add("mimiplanet3");
            } else if (idx == 2 || idx == 9) {
                iconBox.getStyleClass().add("mimiplanet1");
            }

            VBox textBox = new VBox();
            textBox.setPadding(new Insets(2, 0, 0, 10));

            Label nameLabel = new Label(name);
            nameLabel.getStyleClass().add("h4Text");

            Label emailLabel = new Label(email);
            emailLabel.getStyleClass().add("staticText");

            textBox.getChildren().addAll(nameLabel, emailLabel);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            VBox unreadBoxC = new VBox();
            unreadBoxC.setAlignment(Pos.CENTER);
            HBox unreadBox = new HBox();
            unreadBoxC.getChildren().add(unreadBox);
            if (Integer.parseInt(unread)>0){
                Label unreadLabel = new Label(unread);
                unreadLabel.getStyleClass().add("h6Text");
                unreadBox.getStyleClass().add("unreadBox");
                unreadBox.getChildren().add(unreadLabel);
                unreadBoxC.setMinWidth(15);
                unreadBoxC.setMaxHeight(40);
                unreadBox.setMinWidth(15);
                unreadBox.setMaxHeight(15);
                unreadBox.setAlignment(Pos.CENTER);
            }else {
                unreadBox.setMaxWidth(1);
                unreadBox.setMaxHeight(10);
            }
            miniCard.getChildren().addAll(iconBox, textBox, spacer, unreadBoxC);
            Button cardButton = new Button();
            cardButton.setGraphic(miniCard);
            cardButton.getStyleClass().add("miniCardButton");

            cardButton.setPrefWidth(320);
            cardButton.setPrefHeight(40);

            cardButton.setOnAction(e -> {
                if (receiverEmail !=email){
                    unreadBoxC.getChildren().clear();
                    setChatDatas(name, email, iconIndex);
                }
            });
            friendsList.getChildren().add(cardButton);
        }
    }
    private void SQLNewpotok() {
        Task<ArrayList<String[]>> task = nameAndEmail(userDatas);
        task.setOnSucceeded(e -> {
            ArrayList<String[]> resalt = task.getValue();
            setPeople(resalt);
            people = resalt;
            if (!openinMain) {
                receiverEmail = people.get(0)[1];
                receiverName = people.get(0)[0];
            }
            if (!openinMain){
                setChatDatas(people.get(0)[0], receiverEmail, people.get(0)[2]);
            }

            Task<ArrayList<ArrayList<String>>> task2 = setMessages();
            task2.setOnSucceeded(ev -> setupMessages(task2.getValue()));
            new Thread(task2).start();
        });
        task.setOnFailed(e -> {
            System.out.println("Ошибка таска");
        });
        new Thread(task).start();

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(4), event -> {
            Task<ArrayList<ArrayList<String>>> r = setMessages();
            r.setOnSucceeded(e -> {
                setupMessages(r.getValue());
            });
            new Thread(r).start();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private Task<ArrayList<String[]>> nameAndEmail(ArrayList<String> UserData) {
        return new Task<>() {
            @Override
            protected ArrayList<String[]> call() {
                ArrayList<String[]> lines = new ArrayList<>();

                String sql = """
                    SELECT
                        u.id,
                        u.fio,
                        u.email,
                        u.iconindex,
                        COUNT(m.id) AS unread_count
                    FROM users u
                    LEFT JOIN messages m
                        ON m.sender = u.email
                       AND m.receiver = ?
                       AND m.is_read = FALSE
                    WHERE u.group_id = ?
                      AND u.fio <> ?
                    GROUP BY u.id, u.fio, u.email, u.iconindex
                    ORDER BY unread_count DESC, u.id;
                        
                """;
                try (Connection conn = con.connect();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {

                    stmt.setString(1, UserData.get(1));
                    stmt.setInt(2, Integer.parseInt(UserData.get(3)));
                    stmt.setString(3, UserData.get(0));

                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        String[] line = new String[4];
                        line[0] = rs.getString("fio");
                        line[1] = rs.getString("email");
                        line[2] = rs.getString("iconindex");
                        line[3] = String.valueOf(rs.getInt("unread_count"));
                        lines.add(line);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                return lines;
            }
        };
    }
    private void setupProfileIcon() {
        int idx = Integer.parseInt(userDatas.get(5));
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

    public void ToTask(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("task/task.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
    }
    public void ToProfile(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("profile/profile.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
    }
    public void ToTable(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("tabel/studentTabel.fxml"));
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
    public void ToMain(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
    }
}
