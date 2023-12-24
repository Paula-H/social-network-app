package com.example.socialnetworkapp;

import com.example.socialnetworkapp.service.RequestStatus;
import com.example.socialnetworkapp.service.Service;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class MainApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("login-screen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 634, 450);
        stage.setTitle("Social Network App");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        launch();
    }
}