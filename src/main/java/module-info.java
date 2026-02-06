module com.example.platonus {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires javafx.graphics;
    requires javafx.base;


    opens com.example.platonus to javafx.fxml;
    exports com.example.platonus;
}