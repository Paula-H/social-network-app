package com.example.socialnetworkapp.controller;

import com.example.socialnetworkapp.domain.FriendshipRequest;
import com.example.socialnetworkapp.domain.Tuple;
import com.example.socialnetworkapp.domain.User;
import com.example.socialnetworkapp.event.EventImplementation;
import com.example.socialnetworkapp.observer.Observer;
import com.example.socialnetworkapp.service.RequestStatus;
import com.example.socialnetworkapp.service.Service;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.ArrayList;

public class FriendshipRequestsController implements Observer<EventImplementation> {
    @FXML
    TableView<FriendshipRequest> friendshipRequestTableView = new TableView<>();
    @FXML
    TableColumn<FriendshipRequest, String> fromColumn;
    @FXML
    private Label name = new Label();
    private User loggedUser;
    private Service service = Service.getInstance();
    ObservableList<FriendshipRequest> model = FXCollections.observableArrayList();

    public FriendshipRequestsController() throws SQLException, ClassNotFoundException {
    }

    public void setLoggedUser(User loggedUser) throws SQLException {
        this.loggedUser = loggedUser;
        this.service.addObserver(this);
        name.setText(loggedUser.getUsername());
        initialize();
    }

    public void handleAccept() throws SQLException {
        if (friendshipRequestTableView.getSelectionModel().isEmpty()) {
            MessageAlert.showMessage(
                    null,
                    Alert.AlertType.WARNING,
                    "Selection Empty",
                    "Please select a Friend Request First!");
            return;
        }

        FriendshipRequest fr = friendshipRequestTableView.getSelectionModel().getSelectedItem();
        this.service.proceedWithFriendRequest(new Tuple<>(fr.getFrom().getId(), fr.getTo().getId()), RequestStatus.accepted);
    }

    public void handleDecline() throws SQLException {
        if (friendshipRequestTableView.getSelectionModel().isEmpty()) {
            MessageAlert.showMessage(
                    null,
                    Alert.AlertType.WARNING,
                    "Selection Empty",
                    "Please select a Friend Request First!");
            return;
        }
        FriendshipRequest fr = friendshipRequestTableView.getSelectionModel().getSelectedItem();
        this.service.proceedWithFriendRequest(new Tuple<>(
                fr.getFrom().getId(),
                fr.getTo().getId()),
                RequestStatus.declined);
    }

    private void initialize() throws SQLException {
        fromColumn.setCellValueFactory(new PropertyValueFactory<>("from"));
        friendshipRequestTableView.setItems(model);
        initModel();
    }

    private void initModel() throws SQLException {
        model.clear();
        ArrayList<FriendshipRequest> pending = new ArrayList<>();
        this.service.filterFriendshipRequests(
                loggedUser.getId(),
                RequestStatus.pending)
                .forEach(pending::add);
        model.addAll(pending);
    }

    @Override
    public void update(EventImplementation eventImplementation) throws SQLException {
        Platform.runLater(() -> {
            try {
                initModel();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
}

