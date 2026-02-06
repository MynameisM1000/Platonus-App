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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class Profile {
    @FXML private Label fullNameHeader, emailHeader, studentId, fullName, email, groupID, program, status, gpaProcent, gpa, procentCircle;
    HelloApplication userData = new HelloApplication();
    ArrayList<String> userdata = userData.UserDataCheck();
    private final Titlebar titlebar = new Titlebar();
    @FXML private HBox titleBar;
    @FXML private Button minBtn, maxBtn, closeBtn, remove;
    @FXML VBox backPlanets, panelProfile;
    @FXML HBox backProfile;
    @FXML Circle gpaLay;
    @FXML ImageView profileIconHD, profileIcon, gpaImg;
    StudentTableController gpaLines = new StudentTableController();
    boolean shown = false;
    boolean delClik = false;
    Connect con = new Connect();

    public void initialize() {
        titlebar.setup(titleBar, minBtn, maxBtn, closeBtn);
        setupProfileIcon();
        Platform.runLater(() -> {
            titlebar.maximize(maxBtn);
            fullNameHeader.setText(userdata.get(0));
            emailHeader.setText(userdata.get(1));
            setUserdata();
        });
    }


    private void setUserdata(){
        fullName.setText(userdata.get(0));
        email.setText(userdata.get(1));
        studentId.setText("User ID: "+userdata.get(4));
        groupID.setText(userdata.get(3));
        program.setText(userdata.get(8));
        if (userdata.get(7).equalsIgnoreCase("s")){
            status.setText("Student");
        }

        Task<ArrayList<String>> task = gpaLines.getLines();
        task.setOnSucceeded(e->{
            ArrayList<String> result = task.getValue();
            ArrayList<Double> gpas = new ArrayList<>();
            for (String item : result) {
                String[] grades = item.split(",");
                for (String s : grades) {
                    int grade = Integer.parseInt(s.trim());
                    gpas.add(gradeToGPA(grade));
                }
            }
            double sum = 0;
            for (double g : gpas) sum += g;
            double totalGPA = sum / gpas.size();
            int height = (int) (((totalGPA / 4) * 100)*0.01*200);
            double percent = (totalGPA / 4.0) * 100;
            if(totalGPA<1 || height<1){
                height = 1;
                percent=0;
                totalGPA =0;
            }
            gpaImg.setFitHeight(height);
            gpaLay.setLayoutY(height-100);
            gpa.setText("Your GPA: "+String.format("%.2f%n", totalGPA));
            gpaProcent.setText(String.format("%.1f%%", percent));
            procentCircle.setText(String.format("%.1f%%", percent));

        });
        task.setOnFailed(e-> {
            System.out.println("ошбка крч");
        });
        new Thread(task).start();
    }

    public static double gradeToGPA(int grade) {
        if (grade >= 93) return 4.0;
        else if (grade >= 90) return 3.7;
        else if (grade >= 87) return 3.3;
        else if (grade >= 83) return 3.0;
        else if (grade >= 80) return 2.7;
        else if (grade >= 77) return 2.3;
        else if (grade >= 73) return 2.0;
        else if (grade >= 70) return 1.7;
        else if (grade >= 67) return 1.3;
        else if (grade >= 65) return 1.0;
        else return 0.0;
    }

    private void setupProfileIcon() {
        int idx = Integer.parseInt(userdata.get(5));
        Image image = new Image(getClass().getResourceAsStream("/com/example/platonus/img/profileIcons/"+idx+".png"));
        backProfile.getStyleClass().add("");
        profileIcon.setImage(image);
        profileIconHD.setImage(image);
        if (idx == 1 || idx == 3 || idx == 5 || idx == 10 || idx == 8){
            backPlanets.getStyleClass().add("mimiplanets2");
            backProfile.getStyleClass().add("mimiplanet2");
        } else if (idx == 4 || idx == 6 || idx == 7) {
            backPlanets.getStyleClass().add("mimiplanets3");
            backProfile.getStyleClass().add("mimiplanet3");
        } else if (idx == 2 || idx == 9) {
            backPlanets.getStyleClass().add("mimiplanets1");
            backProfile.getStyleClass().add("mimiplanet1");
        }
    }

    public void delAcc(ActionEvent event) throws IOException {
        if (!delClik) {
            if(!shown) {
                HBox hbox = new HBox();
                hbox.setAlignment(Pos.CENTER_RIGHT);
                hbox.setPadding(new Insets(0, 100, 0, 80)); // top, right, bottom, left
                Label label = new Label("(Are you sure?)");
                label.getStyleClass().add("bigStaticText");
                hbox.getChildren().add(label);
                panelProfile.getChildren().add(0, hbox);
                shown=true;
                final int[] seconds = {3};
                Timeline timeline = new Timeline(
                        new KeyFrame(Duration.seconds(1), e -> {
                            seconds[0]--;
                            if (seconds[0] > 0) {
                                remove.setText("Delete ("+String.valueOf(seconds[0])+"s)");
                            } else {
                                remove.setText("Sure");
                            }
                        })
                );
                timeline.setCycleCount(3);
                timeline.play();
            }
        }
        if(remove.getText()=="Sure") {
            delClik = true;
        }
        if(delClik==true){
            delAccount();
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
    }

    public void delAccount() {
        Task<Void> del = new Task<>() {
            @Override
            protected Void call() {
                String sql = "DELETE FROM users WHERE id = ?;";
                try (Connection conn = con.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, Integer.parseInt(userdata.get(4)));
                    ps.executeUpdate();
                } catch (SQLException e) {
                    System.out.println("Ошибка таска инсерд");
                }
                return null;
            }
        };
        new Thread(del).start();
    }


    public void ToEdit(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("profile/profileEdit.fxml"));
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
    public void ToMain(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
    }
}
