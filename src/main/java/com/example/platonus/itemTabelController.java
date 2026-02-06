package com.example.platonus;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class itemTabelController {
    private final Titlebar titlebar = new Titlebar();
    @FXML
    private HBox titleBar;
    @FXML private Button minBtn, maxBtn, closeBtn;
    @FXML int currentItem = 0;
    @FXML String itemName;
    @FXML private VBox interP;
    @FXML Label itemNameL;
    NumberAxis xAxis = new NumberAxis();
    NumberAxis yAxis = new NumberAxis();
    LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
    HelloApplication userData = new HelloApplication();
    Connect con = new Connect();
    ArrayList<String> UserDatas = userData.UserDataCheck();
    @FXML VBox backPlanets;
    @FXML ImageView profileIconHD;
    @FXML private Label fullNameHeader, emailHeader;

    public void initialize() {
        titlebar.setup(titleBar, minBtn, maxBtn, closeBtn);
        SQLNewPotok();
        Platform.runLater(() -> {
            titlebar.maximize(maxBtn);});
            fullNameHeader.setText(UserDatas.get(0));
            emailHeader.setText(UserDatas.get(1));
            setupProfileIcon();
    }
    public void setIndex(int index, String name) {
        currentItem = index;
        itemName = name;
        itemNameL.setText(itemName);
    }

    @FXML private VBox grade;
    @FXML ImageView waveImg;
    @FXML Circle waveLay;
    @FXML Label procentCircle, o3, mt1,mt2;
    int count=0;
    private void setupGrade(String v) {
        String values = v;
        String[] nums = values.split(",\\s*");
        int[] cnt = new int[v.split(",").length];
        for (int i = 0; i < cnt.length; i++) {
            cnt[i]=Integer.parseInt(nums[i]);
            count+=cnt[i];
        }
        count/= cnt.length;
        if (count == 0) {
            nums = new String[] {"0","0","0","0","0","0","0","0"};
            procentCircle.setText("0%");
            o3.setText("0%");
            mt1.setText("0%");
            mt2.setText("0%");

            waveImg.setFitHeight(1);
            waveLay.setLayoutY(1-100);
        }else{
            procentCircle.setText(count+"%");
            o3.setText(count+"%");
            waveImg.setFitHeight(200*(count*0.01));
            waveLay.setLayoutY(200*(count*0.01)-100);
            if (cnt.length>4){
                int mt1cnt=0;
                int mt2cnt=0;
                for (int i=0;i<4;i++){
                    mt1cnt+=cnt[i];
                }mt1cnt/=4;
                mt1.setText(mt1cnt+"%");
                for (int i=4;i<cnt.length;i++){
                    mt2cnt+=cnt[i];
                }mt2cnt/=(cnt.length-4);
                mt2.setText(mt2cnt+"%");
            }else if (cnt.length>0 && cnt.length<=4){
                int mt1cnt=0;
                for (int i=0;i<cnt.length;i++){
                    mt1cnt+=cnt[i];
                }mt1cnt/=(cnt.length);
                mt1.setText(mt1cnt+"%");
                mt2.setText("0%");
            }
        }

        grade.getChildren().clear();
        HBox indexRow = new HBox();
        indexRow.setSpacing(0);
        indexRow.setAlignment(Pos.CENTER);
        HBox valueRow = new HBox();
        valueRow.setSpacing(0);
        valueRow.setAlignment(Pos.CENTER);
        HBox reg = new HBox();
        reg.getStyleClass().add("reg");
        reg.setAlignment(Pos.CENTER);
        reg.setMaxWidth(500);
        reg.setMinHeight(3);
        for (int i = 0; i < nums.length; i++) {
            VBox regs = new VBox();
            regs.getStyleClass().add("reg");
            regs.setAlignment(Pos.CENTER);
            regs.setMinWidth(3);
            regs.setMaxHeight(20);
            StackPane regPane = new StackPane(regs);
            regPane.setAlignment(Pos.CENTER);
            VBox regsS = new VBox();
            regsS.getStyleClass().add("reg");
            regsS.setAlignment(Pos.CENTER);
            regsS.setMinWidth(3);
            regsS.setMaxHeight(20);
            StackPane regPaneS = new StackPane(regsS);
            regPaneS.setAlignment(Pos.CENTER);
            HBox cell = new HBox();
            cell.setAlignment(Pos.CENTER);
            cell.setMinWidth(60);
            Label indexLabel = new Label(String.valueOf(i + 1));
            indexLabel.getStyleClass().add("gradeI");
            cell.getChildren().add(indexLabel);
            HBox cellS = new HBox();
            cellS.setAlignment(Pos.CENTER);
            cellS.setMinWidth(60);
            Label valueLabel = new Label(nums[i]);
            valueLabel.getStyleClass().add("gradeV");
            cellS.getChildren().add(valueLabel);

            if (i > 0 && i < nums.length) {
                indexRow.getChildren().add(regPane);
                indexRow.getChildren().add(cell);
                valueRow.getChildren().add(regPaneS);
                valueRow.getChildren().add(cellS);
            } else {
                indexRow.getChildren().add(cell);
                valueRow.getChildren().add(cellS);
            }
        }
        StackPane regPaneMain = new StackPane(reg);
        regPaneMain.setAlignment(Pos.CENTER);
        grade.getChildren().addAll(indexRow, regPaneMain, valueRow);
    }


        private void addDates(String v) {
        xAxis.setAutoRanging(false);
        yAxis.setAutoRanging(false);
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(9);
        xAxis.setTickUnit(1);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(120);
        yAxis.setTickUnit(20);
        xAxis.setTickMarkVisible(false);
        xAxis.setMinorTickVisible(false);
        yAxis.setTickMarkVisible(false);
        yAxis.setMinorTickVisible(false);
        lineChart.setPrefHeight(250);
        lineChart.setPrefWidth(900);
        lineChart.setAnimated(false);
        lineChart.setLegendVisible(false);

        XYChart.Series<Number, Number> smoothSeries = new XYChart.Series<>();
        XYChart.Series<Number, Number> pointSeries = new XYChart.Series<>();
        String yStr =  "-2,"+v;
        String[] yStrings = yStr.split(",\\s*");
        for (int i=0;i<(9-yStrings.length);i++){
            yStr+=", "+0;
        }
        yStr +=",-2";
        yStrings = yStr.split(",\\s*");
        double[] xa = new double[yStrings.length];
        double[] ya = new double[yStrings.length];
        for (int i = 0; i < yStrings.length; i++) {
            xa[i] = Double.parseDouble(i+"");
            ya[i] = Double.parseDouble(yStrings[i]);
        }
        for (int i = 0; i < xa.length; i++) {
            pointSeries.getData().add(new XYChart.Data<>(xa[i], ya[i]));
        }
        lineChart.getData().clear();
        lineChart.getData().addAll(smoothSeries, pointSeries);
        interP.getChildren().setAll(lineChart);
        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        int stepsPerSegment = 10;
        Duration stepDuration = Duration.millis(10);
        ArrayList<XYChart.Data<Number, Number>> allPoints = new ArrayList<>();
        for (int i = 0; i < xa.length - 1; i++) {
            double x1 = xa[i];
            double y1 = ya[i];
            double x2 = xa[i + 1];
            double y2 = ya[i + 1];
            for (int s = 0; s <= stepsPerSegment; s++) {
                double t = (double)s / stepsPerSegment;
                double x = x1 + t * (x2 - x1);
                double y = y1 + (y2 - y1) * smooth(t);
                allPoints.add(new XYChart.Data<>(x, y));
            }
        }
        final int[] index = {0};
        KeyFrame keyFrame = new KeyFrame(stepDuration, e -> {
            if (index[0] < allPoints.size()) {
                smoothSeries.getData().add(allPoints.get(index[0]));
                index[0]++;
            }
        });
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }
    private double smooth(double t) {
        return t * t * (3 - 2 * t);
    }

    private void SQLNewPotok(){
        Task<ArrayList<String>> task = getLines();
        task.setOnSucceeded(e -> {
            ArrayList<String> result = task.getValue();
            System.out.println(result.get(currentItem));
            setupGrade(result.get(currentItem));
            addDates(result.get(currentItem));
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
    public void ToTabelBack(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("tabel/studentTabel.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
    }
    public void ToTask(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("task/task.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
    }
    public void ToChat(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("chat/chat.fxml"));
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
