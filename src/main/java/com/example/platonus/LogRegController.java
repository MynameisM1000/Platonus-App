package com.example.platonus;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.control.*;

import java.io.FileWriter;
import java.io.File;
import java.io.IOException;

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
public class LogRegController {
    @FXML private HBox titleBar;
    @FXML private Button minBtn;
    @FXML private Button maxBtn;
    @FXML private Button closeBtn;
    private final Titlebar titlebar = new Titlebar();
    Connect con = new Connect();
    HelloApplication userData = new HelloApplication();
    String text = """
Russian language
Object oriented programming
Sociology-Political
Differential Equations
Mathematical analysis
Physical Culture

Monday





Tuesday
Russian language 14:10-15:00
Russian language 15:10-16:00
Object oriented programming 14:10-15:00



Wednesday
Sociology-Political 13:10-14:00
Differential Equations 14:10-15:00



Thursday
Object oriented programming 15:10-16:00
Differential Equations 16:10-17:00
Differential Equations 17:20-18:10



Friday
Mathematical analysis 12:10-13:00
Mathematical analysis 13:10-14:00
Russian language 14:10-15:00
Mathematical analysis 15:10-16:00
Sociology-Political science 16:10-17:00

Saturday
Physical Culture 12:10-13:00
Physical Culture 13:10-14:00
Object oriented programming 14:10-15:00
Object oriented programming 15:10-16:00
Object oriented programming 16:10-17:00
""";
    public void initialize() {
        startBackgroundTask();
        titlebar.setup(titleBar, minBtn, maxBtn, closeBtn);
    }
    private void startBackgroundTask() {
        new Thread(() -> {
            try {
                Thread.sleep(0);
                javafx.application.Platform.runLater(() -> {
                    Platform.runLater(() -> titlebar.maximize(maxBtn));
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML private TextField FIO;
    @FXML private TextField email;
    @FXML private PasswordField password;
    @FXML private TextField groupId;
    @FXML private TextField course;
    @FXML private ComboBox<String> program;

    @FXML private Label errorLabel;
    // ------------------- Регистрация ---------------------
    public void onRegClick(ActionEvent event) {
        boolean error = false;
        error |= highlightIfEmpty(FIO);
        error |= highlightIfEmpty(email);
        error |= highlightIfEmpty(password);
        error |= highlightIfEmpty(groupId);
        error |= highlightIfEmpty(course);

        if (program.getValue() == null || program.getValue().trim().isEmpty()) {
            program.setStyle("-fx-border-color: red; -fx-border-width: 2;");
            errorLabel.setText("Please select a program!");
            return;
        } else {
            program.setStyle("");
        }

        if (error) return;

        String groupText = groupId.getText();

        if (!groupText.matches("\\d+")) {
            errorLabel.setText("Group ID contains invalid characters!");
            return;
        }

        if (!isValidEmail(email.getText())) {
            email.setStyle("-fx-border-color: red; -fx-border-width: 2;");
            errorLabel.setText("Invalid email!");
            return;
        }
        if (password.getText().length() < 8) {
            password.setStyle("-fx-border-color: red; -fx-border-width: 2;");
            errorLabel.setText("At least 8 characters.");
            return;
        }

        String sqlInsert = "INSERT INTO users (fio, email, password, group_id, iconindex, course, program) VALUES (?, ?, ?, ?, 1,?, ?)";

        try (Connection conn = con.connect();
             PreparedStatement stmt = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, FIO.getText());
            stmt.setString(2, email.getText());
            stmt.setString(3, password.getText());
            stmt.setInt(4, Integer.parseInt(groupId.getText()));
            stmt.setInt(5, Integer.parseInt(course.getText()));
            stmt.setString(6, program.getValue());
            stmt.executeUpdate();
            int newUserId = 0;
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                newUserId = generatedKeys.getInt(1);
            }
            String sqlSelect = "SELECT * FROM users WHERE email = ?";
            ArrayList<String> userData = new ArrayList<>();
            try (PreparedStatement tmt = conn.prepareStatement(sqlSelect)) {
                tmt.setString(1, email.getText());
                ResultSet rs = tmt.executeQuery();
                if (rs.next()) {
                    userData.add(rs.getString("fio"));
                    userData.add(rs.getString("email"));
                    userData.add(rs.getString("password"));
                    userData.add(String.valueOf(rs.getInt("group_id")));
                    userData.add(String.valueOf(rs.getInt("id")));
                    userData.add(String.valueOf(rs.getInt("iconindex")));
                    userData.add(String.valueOf(rs.getInt("course")));
                    userData.add(String.valueOf(rs.getString("status")));
                    userData.add(String.valueOf(rs.getString("program")));
                }
            }
            File folder = new File("C:/myapp");
            if (!folder.exists()) folder.mkdirs();
            File file = new File("C:/myapp/profile-data.txt");
            File notes = new File("C:/myapp/notes.txt");
            if (!file.exists()) file.createNewFile();
            if (!notes.exists()) notes.createNewFile();
            try (FileWriter writer = new FileWriter(notes)) {
                String Title = "New Program!";
                String Text = "We created Platonus program for students!";
                String Selected = "false";
                ArrayList<String> note = new ArrayList<>();
                note.add(Title);
                note.add(Text);
                note.add(Selected);
                for (String s : note) writer.write(s + System.lineSeparator());
            } catch (IOException e) {
                System.out.println("Ошибка записи notes.txt");
            }
            try (FileWriter writer = new FileWriter(file)) {
                for (String s : userData) writer.write(s + System.lineSeparator());
                writer.write(text + System.lineSeparator());
            } catch (IOException e) {
                System.out.println("Ошибка записи profile-data.txt");
            }
            regToScore(userData.get(4));
            Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            errorLabel.setText("A user with this email already exists.");
        }
    }
    // ------------------- ЛОГИН ---------------------
    @FXML private TextField userID;
    @FXML private PasswordField userPassword;
    public void onLogClick(ActionEvent event) {
        if (userID.getText().isEmpty() || userPassword.getText().isEmpty()) {
            errorLabel.setText("Enter ID and password!");
            return;
        }
        String sql = "SELECT * FROM users WHERE id = ? AND password = ?";
        try (Connection conn = con.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            try {
                stmt.setInt(1, Integer.parseInt(userID.getText()));
            } catch (NumberFormatException ex) {
                errorLabel.setText("ID must be a number!");
                return;
            }
            stmt.setString(2, userPassword.getText());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ArrayList<String> userData = new ArrayList<>();
                userData.add(rs.getString("fio"));
                userData.add(rs.getString("email"));
                userData.add(rs.getString("password"));
                userData.add(String.valueOf(rs.getInt("group_id")));
                userData.add(String.valueOf(rs.getInt("id")));
                userData.add(String.valueOf(rs.getInt("iconindex")));
                userData.add(String.valueOf(rs.getInt("course")));
                userData.add(String.valueOf(rs.getString("status")));
                userData.add(String.valueOf(rs.getString("program")));
                File folder = new File("C:/myapp");
                File notes = new File("C:/myapp/notes.txt");
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                File file = new File("C:/myapp/profile-data.txt");
                if (!file.exists()) {
                    file.createNewFile();
                }
                if (!notes.exists()) {
                    notes.createNewFile();
                }
                try (FileWriter writer = new FileWriter(notes)) {
                    String Title = "New Program!";
                    String Text = "We craeted Platonus program for students!";
                    String Selecked = "false";
                    ArrayList<String> note = new ArrayList<String>();
                    note.add(Title);
                    note.add(Text);
                    note.add(Selecked);
                    for (String s : note) {
                        writer.write(s + System.lineSeparator());
                    }
                } catch (IOException e) {
                    System.out.println("Ошибка записи");
                }
                try (FileWriter writer = new FileWriter(file)) {
                    for (String s : userData) {
                        writer.write(s + System.lineSeparator());
                    }
                    writer.write(text + System.lineSeparator());
                } catch (IOException e) {
                    System.out.println("Ошибка записи");
                }

                Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.getScene().setRoot(root);
            } else {
                errorLabel.setText("Incorrect ID or password!");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка вход");
        } catch (IOException e) {
            System.out.println("Ошибка загрузки Мейн");
        }
    }
    // ------------------- Утилиты ---------------------
    private boolean highlightIfEmpty(TextField field) {
        if (field.getText().isEmpty()) {
            field.setStyle("-fx-border-color: red; -fx-border-width: 2;");
            return true;
        } else {
            field.setStyle("");
            return false;
        }
    }
    public void regToScore(String id) {
        String sql = "INSERT INTO student_items (student_id, item1, item2, item3, item4, item5, item6) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = con.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(id));
            ps.setString(2, "0");
            ps.setString(3, "0");
            ps.setString(4, "0");
            ps.setString(5, "0");
            ps.setString(6, "0");
            ps.setString(7, "0");
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("A:D<AS:LF:LAMF");
        }
    }
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    // --- Переход между страницами ---
    public void toRegPage(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("sign/signin-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
    }
    public void toLogPage(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("sign/login-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
    }
}
