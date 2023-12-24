package com.example.socialnetworkapp.controller;

import com.example.socialnetworkapp.domain.User;
import com.example.socialnetworkapp.event.EventImplementation;
import com.example.socialnetworkapp.observer.Observer;
import com.example.socialnetworkapp.repository.paging.Page;
import com.example.socialnetworkapp.repository.paging.PageableImplementation;
import com.example.socialnetworkapp.service.Service;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.util.List;

public class SendRequest implements Observer<EventImplementation> {
    User loggedUser;
    ObservableList<User> model = FXCollections.observableArrayList();
    private Service service = Service.getInstance();
    @FXML
    ListView<User> userListView = new ListView<>();

    Page<User> page = null;
    int initialNumberPage = 1;
    int initialSizePage = 3;

    PageableImplementation initialPageable = new PageableImplementation(initialNumberPage,initialSizePage);

    @FXML
    TextField noOfElements = new TextField();

    public SendRequest() throws SQLException, ClassNotFoundException {

    }

    public void setLoggedUser(User loggedUser) throws SQLException {
        this.loggedUser = loggedUser;
        service.addObserver(this);
        noOfElements.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(!noOfElements.getText().isEmpty()){
                    try {
                        initialSizePage = Integer.parseInt(noOfElements.getText());
                        initModel();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        });
        noOfElements.setText("3");
        //page = service.findAllPage(initialPageable);
        initModel();


    }

    public void handleSendFriendRequest() throws SQLException {
        if (userListView.getSelectionModel().isEmpty()) {
            MessageAlert.showMessage(null, Alert.AlertType.WARNING, "Selection Empty", "Please select an User first!");
            return;
        }

        User possibleFriend = userListView.getSelectionModel().getSelectedItem();
        this.service.sendFriendRequest(loggedUser.getId(),possibleFriend.getId());

        //model.remove(possibleFriend);
        //userListView.refresh();
    }

    public void handleNextPage() throws SQLException {
        if(service.possibleFriendsForUser(page.nextPageable().getPageNumber(),page.nextPageable().getPageSize(),this.loggedUser.getId()).getContent().toList().isEmpty()){
            MessageAlert.showMessage(null, Alert.AlertType.WARNING, "No more pages!", "No more pages!");
            return;
        }
        initialNumberPage++;
        initModel();
    }

    public void handleLastPage() throws SQLException {
        if(page.getPageable().getPageNumber()==1){
            MessageAlert.showMessage(null, Alert.AlertType.WARNING, "First page!", "Cannot go further than first page!");
            return;
        }
        initialNumberPage--;
        initModel();
    }

    private void initModel() throws SQLException {
        model.clear();
        userListView.getItems().clear();

        page = null;
        page = this.service.possibleFriendsForUser(initialNumberPage,initialSizePage,this.loggedUser.getId());

        List<User> possibleFriends = page.getContent().toList();
        model.setAll(possibleFriends);
        userListView.setItems(model);
    }

    @Override
    public void update(EventImplementation event) {

        Platform.runLater(() -> {
            try {
                initModel();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
