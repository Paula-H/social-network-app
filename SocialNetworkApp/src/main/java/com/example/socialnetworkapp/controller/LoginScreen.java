package com.example.socialnetworkapp.controller;

import com.example.socialnetworkapp.domain.User;
import com.example.socialnetworkapp.service.Service;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class LoginScreen {
    @FXML
    private TextField email;
    @FXML
    private TextField passwd;
    private Service service = Service.getInstance();

    public LoginScreen() throws SQLException, ClassNotFoundException {
    }

    public void handleSuccessfulLogin() throws SQLException {
        if(email.getText().equals("admin") && passwd.getText().equals("admin")){
            showAdminScreen();
        }
        else if(this.service.checkCredentials(email.getText(),passwd.getText()).isPresent()){
            showUserScreen(this.service.checkCredentials(email.getText(),passwd.getText()).get());
        }
        else {
            handleUnsuccessfulLogin();
        }
    }

    private void showUserScreen(User loggedUser) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass()
                    .getResource("/com/example/socialnetworkapp/user-screen.fxml"));

            AnchorPane root = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("User Page");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            UserScreen userScreen = loader.getController();
            userScreen.setLoggedUser(loggedUser);

            dialogStage.show();

        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleUnsuccessfulLogin(){
        MessageAlert.showErrorMessage(
                null,
                "Could NOT log in! Try again.");
        this.email.clear();
        this.passwd.clear();
    }

    private void showAdminScreen(){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass()
                    .getResource("/com/example/socialnetworkapp/admin-screen.fxml"));

            AnchorPane root = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Admin Settings");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            AdminScreen adminScreen= loader.getController();

            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            dialogStage.show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
