package com.example.platonus;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.text.Text;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class StudentTableController {
    private final Titlebar titlebar = new Titlebar();
    @FXML private HBox titleBar;
    @FXML private Button minBtn, maxBtn, closeBtn;
    @FXML private ImageView fillRect1, fillRect2, fillRect3, fillRect4, fillRect5, fillRect6;
    private ImageView[] fillRects;
    @FXML Circle clip1, clip2, clip3, clip4, clip5, clip6;
    private Circle[] clips;
    //    MitTerm1 , MitTerm2 , Overall
    @FXML Label mt1i1,mt1i2,mt1i3,mt1i4,mt1i5,mt1i6, mt2i1,mt2i2,mt2i3,mt2i4,mt2i5,mt2i6, oi1, oi2, oi3, oi4, oi5, oi6, procent1, procent2, procent3, procent4, procent5, procent6;
    @FXML Button item1, item2, item3, item4, item5, item6;
    private Label[] mt1, mt2, overall, procents;
    HelloApplication userData = new HelloApplication();
    Connect con = new Connect();
    ArrayList<String> UserDatas = userData.UserDataCheck();
    @FXML VBox backPlanets;
    @FXML ImageView profileIconHD;
    @FXML private Label fullNameHeader, emailHeader;
    public void initialize() {
        titlebar.setup(titleBar, minBtn, maxBtn, closeBtn);
        setup();
        SQLNewPotok();
        Platform.runLater(() -> {
            titlebar.maximize(maxBtn);});
            fullNameHeader.setText(UserDatas.get(0));
            emailHeader.setText(UserDatas.get(1));
            setupProfileIcon();
    }

    private void setup() {
        fillRects = new ImageView[]{fillRect1, fillRect2, fillRect3, fillRect4, fillRect5, fillRect6};
        clips = new Circle[]{clip1, clip2, clip3, clip4, clip5, clip6};
        procents = new Label[]{procent1, procent2, procent3, procent4, procent5, procent6};
        mt1 = new Label[]{mt1i1,mt1i2,mt1i3,mt1i4,mt1i5,mt1i6};
        mt2 = new Label[]{mt2i1,mt2i2,mt2i3,mt2i4,mt2i5,mt2i6};
        overall = new Label[]{oi1, oi2, oi3, oi4, oi5, oi6};
        item1.setText(UserDatas.get(9));
        item2.setText(UserDatas.get(10));
        item3.setText(UserDatas.get(11));
        item4.setText(UserDatas.get(12));
        item5.setText(UserDatas.get(13));
        item6.setText(UserDatas.get(14));
    }


    private void SQLNewPotok(){
        Task<ArrayList<String>> task = getLines();
        task.setOnSucceeded(e -> {
            ArrayList<String> result = task.getValue();
            for (int i=0;i<6;i++){
                setPercent(result.get(i),fillRects[i],clips[i],procents[i],mt1[i],mt2[i],overall[i]);
            }
        });
        task.setOnFailed(e->{
            System.out.println("Ошибка SQL");
        });
        new Thread(task).start();
    }
    public Task<ArrayList<String>> getLines(){
        return new Task<>() {
            @Override
            protected ArrayList<String> call() {
                ArrayList<String> lines = new ArrayList<>();
                String sql ="SELECT item1, item2, item3, item4, item5, item6 FROM student_items WHERE student_id = ?;";
                ArrayList<String> UserData = userData.UserDataCheck();
                try (Connection conn = con.connect();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, Integer.parseInt(UserData.get(4)));
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        lines.add(rs.getString("item1"));
                        lines.add(rs.getString("item2"));
                        lines.add(rs.getString("item3"));
                        lines.add(rs.getString("item4"));
                        lines.add(rs.getString("item5"));
                        lines.add(rs.getString("item6"));
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                } catch (NumberFormatException ex) {
                    System.out.println("Ошибка преобразования group_id");
                }
                return lines;
            }
        };
    }
    private void setPercent(String percents, ImageView fillRect, Circle clip, Label procent, Label mt1, Label mt2, Label overall) {
        if (percents == null || percents.trim().isEmpty()) {
            procent.setText("0%");
            overall.setText("0%");
            mt1.setText("0%");
            mt2.setText("0%");

            fillRect.setFitHeight(1);
            clip.setLayoutY(1-60);
            return;
        }

        String[] line = percents
                .replaceAll("\\s+", "")
                .split(",");
        if (line.length == 0) {
            return;
        }
        int cnt = 0;
        if (line.length>4){
            int mt1cnt=0;
            int mt2cnt=0;
            for (int i=0;i<4;i++){
                mt1cnt+=Integer.parseInt(line[i]);
            }mt1cnt/=4;
            mt1.setText(mt1cnt+"%");
            for (int i=4;i<line.length;i++){
                mt2cnt+=Integer.parseInt(line[i]);
            }mt2cnt/=(line.length-4);
            mt2.setText(mt2cnt+"%");
        }else if (line.length>0 && line.length<=4){
            int mt1cnt=0;
            for (int i=0;i<line.length;i++){
                mt1cnt+=Integer.parseInt(line[i]);
            }mt1cnt/=(line.length);
            mt1.setText(mt1cnt+"%");
            mt2.setText("0%");
        }
        if (line.length>0){
            for (String s: line){
                cnt+= Integer.parseInt(s);
            }cnt/=line.length;
            if (cnt < 0) cnt = 0;
            if (cnt > 100) cnt = 100;
            if(cnt==0){
                procent.setText(cnt+"%");
                overall.setText(cnt+"%");
                fillRect.setFitHeight(1);
                clip.setLayoutY((1-60));
            }else{
                double maxHeight = 60 * 2;
                double newHeight = maxHeight * cnt / 100;
                procent.setText(cnt+"%");
                overall.setText(cnt+"%");
                fillRect.setFitHeight(newHeight);
                clip.setLayoutY((newHeight-60));
            }
        }
    }


    @FXML
    private void itemOpen(ActionEvent event) throws IOException {
        Button clickedButton = (Button) event.getSource();
        int index = 0;
        String name = "None";
        if (clickedButton == item1) {index=0; name=UserDatas.get(9);}
        else if (clickedButton == item2) {index = 1;name=UserDatas.get(10);}
        else if (clickedButton == item3) {index = 2;name=UserDatas.get(11);}
        else if (clickedButton == item4) {index = 3;name=UserDatas.get(12);}
        else if (clickedButton == item5) {index = 4;name=UserDatas.get(13);}
        else if (clickedButton == item6) {index = 5;name=UserDatas.get(14);}

        FXMLLoader loader = new FXMLLoader(getClass().getResource("tabel/itemTabel.fxml"));
        Parent root = loader.load();
        itemTabelController item = loader.getController();
        item.setIndex(index , name);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
    }
    private void setupProfileIcon() {
        int idx = Integer.parseInt(UserDatas.get(5));
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
    public void ToChat(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("chat/chat.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
    }
    public void ToTask(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("task/task.fxml"));
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
