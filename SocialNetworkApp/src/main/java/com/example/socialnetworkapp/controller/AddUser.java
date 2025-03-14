package com.example.socialnetworkapp.controller;

import com.example.socialnetworkapp.domain.User;
import com.example.socialnetworkapp.event.EventImplementation;
import com.example.socialnetworkapp.observer.Observer;
import com.example.socialnetworkapp.service.Service;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class AddUser {
    @FXML
    public TextField firstName;
    @FXML
    public TextField lastName;
    @FXML
    public TextField username;
    @FXML
    public TextField email;
    @FXML
    public TextField password;
    private Service service = Service.getInstance();

    public AddUser() throws SQLException, ClassNotFoundException {
    }

    public void handleAdd() throws SQLException {
        if(firstName.getText().isEmpty() || lastName.getText().isEmpty() ||
                username.getText().isEmpty() || email.getText().isEmpty() ||
                password.getText().isEmpty()){
            MessageAlert.showErrorMessage(null,"ALL fields must be completed! Try again.");
            firstName.clear();
            lastName.clear();
            username.clear();
            email.clear();
            password.clear();
            return;
        }
        User u = new User(firstName.getText(),lastName.getText(),username.getText(),email.getText(),password.getText());
        this.service.addUser(u);
    }
}
