package com.example.socialnetworkapp.controller;

import com.example.socialnetworkapp.domain.User;
import com.example.socialnetworkapp.event.EventImplementation;
import com.example.socialnetworkapp.observer.Observer;
import com.example.socialnetworkapp.repository.paging.Page;
import com.example.socialnetworkapp.repository.paging.PageableImplementation;
import com.example.socialnetworkapp.service.Service;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AdminScreen implements Observer<EventImplementation> {
    String url = "jdbc:postgresql://localhost:5432/socialnetwork";
    String user = "postgres";
    String passwd = "1";

    private Service service = Service.getInstance();

//    UserPageableRepository userRepository = new UserPageableRepository(url,user,passwd);
//
//    FriendshipPageableRepository friendshipRepository = new FriendshipPageableRepository(url,user,passwd);

    Page<User> page = null;
    int initialNumberPage = 1;
    int initialSizePage = 8;

    PageableImplementation initialPageable = new PageableImplementation(initialNumberPage,initialSizePage);

//    MessageRepository messageRepository = new MessageRepository(url,user,passwd);
//    FriendshipRequestRepository friendshipRequestRepository = new FriendshipRequestRepository(url, user,passwd);
    ObservableList<User> model = FXCollections.observableArrayList();
    @FXML
    private TableView<User> userTable = new TableView<>();
    @FXML
    private TableColumn<User,Long> id = new TableColumn<>();
    @FXML
    private TableColumn<User,String> first_name= new TableColumn<>();
    @FXML
    private TableColumn<User,String> last_name= new TableColumn<>();
    @FXML
    private TableColumn<User,String> username= new TableColumn<>();
    @FXML
    private TableColumn<User,String> email= new TableColumn<>();
    @FXML
    private TableColumn<User,String> password= new TableColumn<>();
    @FXML
    private TextField noOfElements = new TextField();
//    private GeneralService service= new GeneralService(userRepository,friendshipRepository,messageRepository, friendshipRequestRepository);
    public AdminScreen() throws SQLException, ClassNotFoundException {
        service.addObserver(this);
        userTable.setEditable(false);
        initialize();


    }

    public void handleDelete() throws SQLException {
        if (userTable.getSelectionModel().isEmpty()) {
            MessageAlert.showMessage(null, Alert.AlertType.WARNING, "Selection Empty", "Please select an User first!");
            return;
        }

        User toBeDeleted = userTable.getSelectionModel().getSelectedItem();
        this.service.deleteUser(toBeDeleted);
    }

    public void handleAdd(){
        showAddUserDialogue();
    }

    public void handleUpdate(){
        if (userTable.getSelectionModel().isEmpty()) {
            MessageAlert.showMessage(null, Alert.AlertType.WARNING, "Selection Empty", "Please select an User first!");
            return;
        }
        User toBeUpdated = userTable.getSelectionModel().getSelectedItem();
        showUpdateUserDialogue(toBeUpdated.getId());
    }

    private void showUpdateUserDialogue(Long ID) {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/example/socialnetworkapp/update-user.fxml"));


            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Friendship Requests");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);


            UpdateUser updateUser = loader.getController();
            updateUser.setIDforUpdate(ID);


            dialogStage.show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void update(EventImplementation event) throws SQLException {
        initModel();

    }

    public void handleNextPage() throws SQLException {

        if(service.findAllUsers(page.nextPageable().getPageNumber(),page.nextPageable().getPageSize()).getContent().toList().isEmpty()){
            MessageAlert.showMessage(null, Alert.AlertType.WARNING, "No more pages!", "No more pages!");
            return;
        }
        //page = service.findAllPage(page.nextPageable());
        initialNumberPage++;
        //initialSizePage++;
        initModel();
    }

    public void handleLastPage() throws SQLException {

        if(page.getPageable().getPageNumber()==1){
            MessageAlert.showMessage(null, Alert.AlertType.WARNING, "First page!", "Cannot go further than first page!");
            return;
        }
        //page = service.findAllPage(page.lastPageable());
        //page = service.findAllPage(new PageableImplementation(initialNumberPage-1,initialSizePage-1));
        //initialSizePage--;
        initialNumberPage--;
        initModel();
    }


    @FXML
    protected void initialize() throws SQLException {
        id.setCellValueFactory(new PropertyValueFactory<User,Long>("id"));
        first_name.setCellValueFactory(new PropertyValueFactory<User,String>("first_name"));
        last_name.setCellValueFactory(new PropertyValueFactory<User,String>("last_name"));
        username.setCellValueFactory(new PropertyValueFactory<User,String>("username"));
        email.setCellValueFactory(new PropertyValueFactory<User,String>("email"));
        password.setCellValueFactory(new PropertyValueFactory<User,String>("password"));
        userTable.setItems(model);
        page = service.findAllUsers(initialNumberPage,initialSizePage);
        noOfElements.setText("8");
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
        initModel();

    }

    private void initModel() throws SQLException {
        model.clear();
        page = null;
        page = this.service.findAllUsers(initialNumberPage,initialSizePage);
        //List<User> userList = page.getStreamContent();
        List<User> userList = page.getContent().toList();
        model.addAll(userList);
    }

    private void showAddUserDialogue(){
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/example/socialnetworkapp/add-user.fxml"));


            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Friendship Requests");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);


            AddUser addUser = loader.getController();
            //addUser.setService(service);


            dialogStage.show();
            //initModel();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
