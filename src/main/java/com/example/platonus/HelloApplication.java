package com.example.platonus;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.*;
import java.util.ArrayList;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Font.loadFont(getClass().getResourceAsStream("css/Fonts/Raleway-VariableFont_wght.ttf"), 10);
        Font.loadFont(getClass().getResourceAsStream("css/Fonts/Outfit-VariableFont_wght.ttf"), 10);
        Font.loadFont(getClass().getResourceAsStream("css/Fonts/Outfit-Bold.ttf"), 10);
        Font.loadFont(getClass().getResourceAsStream("css/Fonts/Outfit-Light.ttf"), 10);
        Font.loadFont(getClass().getResourceAsStream("css/Fonts/Outfit-Medium.ttf"), 10);
        Font.loadFont(getClass().getResourceAsStream("css/Fonts/Raleway-Medium.ttf"), 10);
        Font.loadFont(getClass().getResourceAsStream("css/Fonts/Raleway-SemiBold.ttf"), 10);
        File folder = new File("C:/myapp");
        File file = new File("C:/myapp/profile-data.txt");
        ArrayList<String> UserData = UserDataCheck();

//        Кеш проверка
        if (file.exists() && folder.exists() && (UserData.size()>3)) {
//            Когда в файле есть данные
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("main.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1200, 700);
            stage.setTitle("Platonus 0.1 version");
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(scene);
            stage.show();

        }else {
//            когда в файле нету данных или вообще файла нету
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("sign/login-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1200, 700);
            stage.setTitle("Platonus 0.1 version");
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(scene);
            stage.show();
        }
    }
//    чекаем файл чтобы узанть естиь ли данные в файле
    public ArrayList<String> UserDataCheck() {
        ArrayList<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("C:/myapp/profile-data.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.out.println("Файла нету");
        }
        return lines;
    }
}
