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
    String url = "jdbc:postgresql://localhost:5432/socialnetwork";
    String user = "postgres";
    String password = "1";

    Service service = Service.getInstance();


    @FXML
    private TextField email;


    @FXML
    private TextField passwd;

    public LoginScreen() throws SQLException, ClassNotFoundException {
    }

    public void handleSuccessfulLogin() throws SQLException {
        if(email.getText().equals("admin") && passwd.getText().equals("admin")){
            showAdminScreen();
        }
        else if(this.service.checkCredentials(email.getText(),passwd.getText()).isPresent()){
            showUserScreen(this.service.checkCredentials(email.getText(),passwd.getText()).get());
        }
        else
            handleUnsuccessfulLogin();

    }


    private void showUserScreen(User loggedUser) {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/example/socialnetworkapp/user-screen.fxml"));


            AnchorPane root = (AnchorPane) loader.load();


            Stage dialogStage = new Stage();
            dialogStage.setTitle("User Page");
            dialogStage.initModality(Modality.WINDOW_MODAL);

            Scene scene = new Scene(root);
            dialogStage.setScene(scene);


            UserScreen userScreen = loader.getController();

            userScreen.setLoggedUser(loggedUser);
//            userScreen.setService(service,frservice,messageService);


            dialogStage.show();

        } catch (IOException e ) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleUnsuccessfulLogin(){
        MessageAlert.showErrorMessage(null,"Could NOT log in! Try again.");
        this.email.clear();
        this.passwd.clear();
    }

    private void showAdminScreen(){
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/example/socialnetworkapp/admin-screen.fxml"));


            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Admin Settings");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            AdminScreen adminScreen= loader.getController();


            Scene scene = new Scene(root);
            dialogStage.setScene(scene);



            dialogStage.show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
