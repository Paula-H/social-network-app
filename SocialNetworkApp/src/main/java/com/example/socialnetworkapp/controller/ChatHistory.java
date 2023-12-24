package com.example.socialnetworkapp.controller;

import com.example.socialnetworkapp.domain.Message;
import com.example.socialnetworkapp.domain.User;
import com.example.socialnetworkapp.event.EventImplementation;
import com.example.socialnetworkapp.observer.Observer;
import com.example.socialnetworkapp.repository.paging.Page;
import com.example.socialnetworkapp.service.Service;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChatHistory implements Observer<EventImplementation> {
    @FXML
    public Label name = new Label();
    Page<User> page = null;
    int initialNumberPage = 1;
    int initialSizePage = 3;
    @FXML
    TextField noOfElements = new TextField();
    @FXML
    public ListView<HBox> conversation = new ListView<>();
    ObservableList<HBox> model = FXCollections.observableArrayList();
    private ArrayList<Message> messages = new ArrayList<>();

    @FXML
    public ListView<User> friendList= new ListView<>();

    @FXML
    public TextArea messageArea = new TextArea();

    private final ObservableList<User> friends = FXCollections.observableArrayList();

    private User loggedUser;
    private User selectedUser;

    private Service service = Service.getInstance();

    public ChatHistory() throws SQLException, ClassNotFoundException {
    }

    public void setLoggedUser(User loggedUser) throws SQLException {
        this.loggedUser = loggedUser;
        this.name.setText(loggedUser.getUsername());

        this.service.addObserver(this);
        //his.messageService.conversationBetween(loggedUser,this.generalService.findOne(4L)).forEach(messages::add);

//        for(int i=1;i<11;i++){
//            this.messages.add(this.messageService.conversationBetween(loggedUser, this.generalService.findOne(4L)).get(i));
//        }

        friendList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        friendList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && friendList.getSelectionModel().getSelectedItems().size()==1) {
                try {
                    handleSelectSingle();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        noOfElements.setText("3");
        noOfElements.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(!noOfElements.getText().isEmpty()){
                    try {
                        initialSizePage = Integer.parseInt(noOfElements.getText());
                        initializeFriendList();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        });

        //page = generalService.findAllPage(initialPageable);
        initializeFriendList();

    }

    public void handleSend() throws SQLException {
        if(messageArea.getText().isEmpty()){
            MessageAlert.showMessage(null, Alert.AlertType.WARNING, "Empty Message", "You cannot send an empty message!");
            return;
        }
        this.service.sendMessage(this.loggedUser,this.selectedUser,messageArea.getText());
        this.messageArea.clear();

    }

    public void handleSelectMultiple() throws SQLException {
        ObservableList<User> selectedUsers = friendList.getSelectionModel().getSelectedItems();
        if (selectedUsers.isEmpty()) {
            MessageAlert.showMessage(null, Alert.AlertType.WARNING, "Selection Empty", "Please select at least 2 users!");
            return;
        }
        else if(selectedUsers.size()==1){
            MessageAlert.showMessage(null, Alert.AlertType.WARNING, "One user", "Please select at least one more user!");
            return;
        }
        if(messageArea.getText().isEmpty()){
            MessageAlert.showMessage(null, Alert.AlertType.WARNING, "Empty Message", "You cannot send an empty message!");
            return;
        }
        ArrayList<User> friendsToSend = new ArrayList<>();
        selectedUsers.stream().forEach(friendsToSend::add);
        this.service.sendMessageToUsers(this.loggedUser,friendsToSend,messageArea.getText());
        this.messageArea.clear();
    }

    public void handleSelectSingle() throws SQLException {
        if (friendList.getSelectionModel().isEmpty()) {
            MessageAlert.showMessage(null, Alert.AlertType.WARNING, "Selection Empty", "Please select an User first!");
            return;
        }
        this.selectedUser =  friendList.getSelectionModel().getSelectedItem();
        this.messages.clear();
        this.service.conversationBetween(this.loggedUser,this.selectedUser).forEach(this.messages::add);
        initializeChat();

    }

    public void handleNextPage() throws SQLException {
        if(service.friendsForUser(page.nextPageable().getPageNumber(),page.nextPageable().getPageSize(),this.loggedUser.getId()).getContent().toList().isEmpty()){
            MessageAlert.showMessage(null, Alert.AlertType.WARNING, "No more pages!", "No more pages!");
            return;
        }
        initialNumberPage++;
        initializeFriendList();
    }

    public void handleLastPage() throws SQLException {
        if(page.getPageable().getPageNumber()==1){
            MessageAlert.showMessage(null, Alert.AlertType.WARNING, "First page!", "Cannot go further than first page!");
            return;
        }
        initialNumberPage--;
        initializeFriendList();
    }

    private void initializeFriendList() throws SQLException {
        friendList.getItems().clear();
        //this.loggedUser.getFriend_list().stream().forEach(friends::add);
        friends.clear();
        page = null;
        page = this.service.friendsForUser(initialNumberPage,initialSizePage,this.loggedUser.getId());
        page.getContent().forEach(friends::add);
        friendList.setCellFactory(param -> new ListCell<User>() {
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
        this.friendList.setItems(friends);

    }



    private void initializeChat() throws SQLException {
        this.messages.clear();
        this.service.conversationBetween(loggedUser,selectedUser).forEach(this.messages::add);
        //initializeFriendList();
        this.conversation.getItems().clear();
        if(this.messages.size()<=10){
        List<Message> mess= this.messages.subList(1,messages.size());
        mess.forEach(
                x->{
                    Label label = new Label(x.getMessage());
                    label.setWrapText(true);
                    HBox container = new HBox();
                    container.getChildren().add(label);
                    if(x.getFrom().equals(this.loggedUser)){
                        label.setStyle("-fx-background-color: #b5ce99;\n" +
                                "-fx-border-color: #7b9f6d;\n" +
                                "-fx-border-radius: 15;\n" +
                                "-fx-start-margin: 15;\n" +
                                "-fx-padding: 10;\n" +
                                "-fx-background-radius: 15;\n" +
                                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);");
                        container.setAlignment(Pos.CENTER_RIGHT);


                    }
                    else{
                        label.setStyle("-fx-background-color: #ffebed;\n" +
                                "-fx-border-color: rgb(232,169,179);\n" +
                                "-fx-border-radius: 15;\n" +
                                "    -fx-padding: 10;\n" +
                                "    -fx-background-radius: 15;\n" +
                                "    -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);");
                        container.setAlignment(Pos.CENTER_LEFT);
                    }
                    this.model.add(container);

                }
        );}
        else{
            List<Message> mess= this.messages.subList(this.messages.size()-10,this.messages.size());
            mess.forEach(
                    x->{
                        Label label = new Label(x.getMessage());
                        label.setWrapText(true);
                        HBox container = new HBox();
                        container.getChildren().add(label);
                        if(x.getFrom().equals(this.loggedUser)){
                            label.setStyle("-fx-background-color: #b5ce99;\n" +
                                    "-fx-border-color: #7b9f6d;\n" +
                                    "-fx-border-radius: 15;\n" +
                                    "-fx-start-margin: 15;\n" +
                                    "-fx-padding: 10;\n" +
                                    "-fx-background-radius: 15;\n" +
                                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);");
                            container.setAlignment(Pos.CENTER_RIGHT);


                        }
                        else{
                            label.setStyle("-fx-background-color: #ffebed;\n" +
                                    "-fx-border-color: rgb(232,169,179);\n" +
                                    "-fx-border-radius: 15;\n" +
                                    "    -fx-padding: 10;\n" +
                                    "    -fx-background-radius: 15;\n" +
                                    "    -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);");
                            container.setAlignment(Pos.CENTER_LEFT);
                        }
                        this.model.add(container);

                    }
            );

        }

        this.conversation.setItems(model);
        conversation.scrollTo(conversation.getItems().size()-1);
    }


    @Override
    public void update(EventImplementation event) {

        Platform.runLater(() -> {
            try {
                initializeChat();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

    }
}
