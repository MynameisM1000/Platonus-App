package com.example.platonus;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect {
    public Connection connect() throws SQLException {
        String url = "your_url";
        String user = "your_username";
        String pass = "your_password";
        return DriverManager.getConnection(url, user, pass);
    }
}
