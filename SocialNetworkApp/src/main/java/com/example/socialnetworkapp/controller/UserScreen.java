package com.example.socialnetworkapp.controller;

import com.example.socialnetworkapp.domain.Tuple;
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
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserScreen implements Observer<EventImplementation> {

    User loggedUser;

    ObservableList<User> model = FXCollections.observableArrayList();
    private Service service = Service.getInstance();

    Page<User> page = null;
    int initialNumberPage = 1;
    int initialSizePage = 3;

    PageableImplementation initialPageable = new PageableImplementation(initialNumberPage,initialSizePage);

    @FXML
    TextField noOfElements = new TextField();

    @FXML
    ListView<User> userListView = new ListView<>();

    @FXML
    Label loggedUsername;

    public UserScreen() throws SQLException, ClassNotFoundException {

    }

    public void setLoggedUser(User loggedUser) throws SQLException {
        this.loggedUser = loggedUser;
        this.loggedUsername.setText(this.loggedUser.getUsername());
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
        //page = service.findAllUsers(initialNumberPage,initialSizePage);
        initModel();
    }

    public void handleSendFriendshipRequest(){ sendFriendshipRequestScreen();}

    public void handleSeeChats(){ showChatsDialogue();}

    private void showChatsDialogue() {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/example/socialnetworkapp/chat-history.fxml"));


            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Send Friendship Request");
            dialogStage.initModality(Modality.WINDOW_MODAL);


            Scene scene = new Scene(root);
            dialogStage.setScene(scene);



            ChatHistory chatHistory = loader.getController();
            chatHistory.setLoggedUser(loggedUser);

            dialogStage.show();


        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendFriendshipRequestScreen() {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/example/socialnetworkapp/send-request.fxml"));


            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Send Friendship Request");
            dialogStage.initModality(Modality.WINDOW_MODAL);


            Scene scene = new Scene(root);
            dialogStage.setScene(scene);



            SendRequest sendRequest = loader.getController();
            sendRequest.setLoggedUser(loggedUser);


            dialogStage.show();


        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleSeeFriendshipRequests(){
        showFriendshipRequestsScreen();
    }

    public void handleDeleteFriend() throws SQLException {
        if (userListView.getSelectionModel().isEmpty()) {
            MessageAlert.showMessage(null, Alert.AlertType.WARNING, "Selection Empty", "Please select an User first!");
            return;
        }

        User toBeDeleted = userListView.getSelectionModel().getSelectedItem();
        this.service.deleteFriendship(new Tuple(this.loggedUser.getId(),toBeDeleted.getId()));

    }

    public void handleNextPage() throws SQLException {
        if(service.friendsForUser(page.nextPageable().getPageNumber(),page.nextPageable().getPageSize(), loggedUser.getId()).getContent().toList().isEmpty()){
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
        page = this.service.friendsForUser(initialNumberPage,initialSizePage, loggedUser.getId());
        List<User> friends = page.getContent().toList();
        ArrayList<User> userArrayList = new ArrayList<>();
        friends.forEach(userArrayList::add);
        model.setAll(userArrayList);
        userListView.setCellFactory(param -> new ListCell<User>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getUsername());
                }
            }
        });
        userListView.setItems(model);
    }

    private void showFriendshipRequestsScreen(){
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/example/socialnetworkapp/friendship_requests.fxml"));


            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Friendship Requests");
            dialogStage.initModality(Modality.WINDOW_MODAL);

            Scene scene = new Scene(root);
            dialogStage.setScene(scene);


            FriendshipRequestsController friendshipRequestsController = loader.getController();
            friendshipRequestsController.setLoggedUser(loggedUser);


            dialogStage.show();


        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void update(EventImplementation friendshipRequestChangeEvent) {
        Platform.runLater(() -> {
            try {
                initModel();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
