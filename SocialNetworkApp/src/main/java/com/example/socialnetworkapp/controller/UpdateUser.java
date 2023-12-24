package com.example.socialnetworkapp.controller;

import com.example.socialnetworkapp.domain.User;
import com.example.socialnetworkapp.event.EventImplementation;
import com.example.socialnetworkapp.observer.Observer;
import com.example.socialnetworkapp.service.Service;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class UpdateUser implements Observer<EventImplementation> {
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

    Long IDforUpdate;
    private Service service = Service.getInstance();

    public UpdateUser() throws SQLException, ClassNotFoundException {
        this.service.addObserver(this);
    }

    public void setIDforUpdate(Long IDforUpdate) {
        this.IDforUpdate = IDforUpdate;
    }

    public void handleUpdate() throws SQLException {
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
        u.setId(IDforUpdate);
        this.service.modifyUser(u);
    }

    @Override
    public void update(EventImplementation event) {

    }
}
