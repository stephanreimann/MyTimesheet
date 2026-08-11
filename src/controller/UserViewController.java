/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import commands.user.*;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.stage.*;
import model.*;
import org.apache.logging.log4j.*;
import service.*;
import sqlite.UserDAO;
import utils.*;

/**
 *
 * @author adrest18
 */
public class UserViewController implements Initializable, IViewController {

    public enum DataAction { NEW, EDIT, DELETE };
    
    private final String userFirstNameResourceKey = "UserFirstName";
    private final String userLastNameResourceKey = "UserLastName";
    private final String userDetailsLabelResourceKey = "UserDetailsLabel";
    private final String userLoginResourceKey = "UserLogin";
    private final String userPasswordResourceKey ="UserPassword";
    private final String userVacationLeftResourceKey = "UserVacationLeft";
    private final String userContractNameResourceKey = "UserContractName";
    private final String userRoleNameResourceKey = "UserRoleName";
    private final String userAddressResourceKey = "UserAddress";
    private final String newResourceKey = "New";
    private final String editResourceKey = "Edit";
    private final String deleteResourceKey = "Delete";
    private final String noUserSelectionAlertTitle = "NoSelectionAlertTitle";
    private final String noUserSelectionAlertHeader = "NoUserSelectionAlertHeader";
    private final String noUserSelectionAlertContent = "NoUserSelectionAlertContent";
    private final String daysResourceKey = "Days";
    private final String newUserEvent = "NewUser";
    private final String editUserEvent = "EditUser";
    private final String deleteUserEvent = "DeleteUser";
   
    private final Logger log = LogManager.getLogger(UserViewController.class.getName());
    
    private Stage primaryStage;
    private final ControllerRepository controllerRepository;
    private final LanguageService languageService;
    private final Connection connection;
    private final UndoService undoService;
    private final PropertiesService propertiesService;
    private ResourceBundle rb;
    private final UserDAO userDao;
    private ObservableList<User> userData;
    private Stage userDetailsViewDialog;
    public EventManager eventManager;
    
    private final String userDetailsViewDialogIcon = "icons/app-maid.png";
    private final String userDetailsViewDialogTitleResourceKey = "UserDetailsViewTitle";
    private final String userDetailsViewResource = "/view/UserDetailsView.fxml";

    @FXML
    private TableView<User> userTableView;
    @FXML
    private TableColumn<User, String> userFirstNameTableColumn;
    @FXML
    private TableColumn<User, String> userLastNameTableColumn;
    @FXML
    private Label userDetailsLabel;
    
    @FXML
    private Label firstNameLabel;
    @FXML
    private Label lastNameLabel;
    @FXML
    private Label loginLabel;
    @FXML
    private Label passwordLabel;
    @FXML
    private Label vacationLeftLabel;
    @FXML
    private Label contractNameLabel;
    @FXML
    private Label roleNameLabel;
    @FXML
    private Label addressLabel;
    
    @FXML
    private Label firstNameLabelValue;
    @FXML
    private Label lastNameLabelValue;
    @FXML
    private Label loginLabelValue;
    @FXML
    private PasswordField passwordFieldValue;
    @FXML
    private Label vacationLeftLabelValue;
    @FXML
    private Label contractNameLabelValue;
    @FXML
    private Label roleNameLabelValue;
    @FXML
    private Label addressLabelValue;
    
    @FXML
    private Button newButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    
    public UserViewController(ControllerRepository controllerRepository, LanguageService languageService, Connection connection, UndoService undoService, PropertiesService propertiesService) throws SQLException {
        if(controllerRepository == null) throw new NullPointerException("controllerRepository");
        if(languageService == null) throw new NullPointerException("languageService");
        if(connection == null) throw new NullPointerException("connection");
        if(undoService == null) throw new NullPointerException("undoService");
        if(propertiesService == null) throw new NullPointerException("propertiesService");
        
        this.controllerRepository = controllerRepository;
        this.languageService = languageService;
        this.connection = connection;
        this.undoService = undoService;
        this.propertiesService = propertiesService;
        this.userDao = new UserDAO(connection);
        this.userData = FXCollections.observableArrayList(this.userDao.selectAll());
        this.eventManager = new EventManager();
        this.eventManager.registerEventType(newUserEvent);
        this.eventManager.registerEventType(editUserEvent);
        this.eventManager.registerEventType(deleteUserEvent);
    }
    
    @FXML
    private void newUserAction(ActionEvent event) throws SQLException, IOException {
        User newUser = new User(userDao.getNextId());
        openUserDetailsDialog(newUser, DataAction.NEW);
        if(isUserValid(newUser)) {
            NewUserCommand cmd = new NewUserCommand(controllerRepository, eventManager, userTableView, newUser, userDao);
            undoService.execute(cmd);
        }
    }
    
    @FXML
    private void editUserAction(ActionEvent event) throws SQLException, IOException {        
        User selectedUser = userTableView.getSelectionModel().getSelectedItem();
        if(selectedUser != null) {
            User originalUser = new User(selectedUser);
            openUserDetailsDialog(selectedUser, DataAction.EDIT);
            showUserDetails(selectedUser);
            if(!originalUser.equals(selectedUser)) {
                EditUserCommand cmd = new EditUserCommand(controllerRepository, eventManager, userTableView, originalUser, selectedUser, userDao);
                undoService.execute(cmd);
            }
        } else {
            ControllerUtilities.showNoItemSelectedAlert(primaryStage, rb, noUserSelectionAlertTitle, noUserSelectionAlertHeader, noUserSelectionAlertContent);
        }
    }

    @FXML
    private void deleteUserAction(ActionEvent event) throws SQLException {
        User selectedUser = userTableView.getSelectionModel().getSelectedItem();
        if(selectedUser != null) {
            DeleteUserCommand cmd = new DeleteUserCommand(controllerRepository, eventManager, userTableView, selectedUser, userDao);
            undoService.execute(cmd);
        } else {
            ControllerUtilities.showNoItemSelectedAlert(primaryStage, rb, noUserSelectionAlertTitle, noUserSelectionAlertHeader, noUserSelectionAlertContent);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle rb) {
        this.rb = rb;
        
        userTableView.setItems(userData);

        userFirstNameTableColumn.setCellValueFactory(cellData -> cellData.getValue().getFirstnameProperty());
        userFirstNameTableColumn.prefWidthProperty().bind(userTableView.widthProperty().multiply(0.5));

        userLastNameTableColumn.setCellValueFactory(cellData -> cellData.getValue().getLastnameProperty());
        userLastNameTableColumn.prefWidthProperty().bind(userTableView.widthProperty().multiply(0.5));
            
        Optional<User> firstUser = userData.stream().findFirst();
        if(firstUser != null) {
            showUserDetails(firstUser.get());
            userTableView.getSelectionModel().select(0);
        } else {
            showUserDetails(null);
        }
           
        userTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showUserDetails(newValue));        

        languageService.updateGuiItems();
    }

    @Override
    public void updateGuiItems() {
        userFirstNameTableColumn.setText(rb.getString(userFirstNameResourceKey));
        userLastNameTableColumn.setText(rb.getString(userLastNameResourceKey));
        userDetailsLabel.setText(rb.getString(userDetailsLabelResourceKey));
        firstNameLabel.setText(rb.getString(userFirstNameResourceKey));
        lastNameLabel.setText(rb.getString(userLastNameResourceKey));
        loginLabel.setText(rb.getString(userLoginResourceKey));
        passwordLabel.setText(rb.getString(userPasswordResourceKey));
        vacationLeftLabel.setText(rb.getString(userVacationLeftResourceKey));
        contractNameLabel.setText(rb.getString(userContractNameResourceKey));
        roleNameLabel.setText(rb.getString(userRoleNameResourceKey));
        addressLabel.setText(rb.getString(userAddressResourceKey));
        newButton.setText(rb.getString(newResourceKey));
        editButton.setText(rb.getString(editResourceKey));
        deleteButton.setText(rb.getString(deleteResourceKey));
    
        if(userDetailsViewDialog != null) {
            userDetailsViewDialog.setTitle(rb.getString(userDetailsViewDialogTitleResourceKey));
        }
    }

    @Override
    public ResourceBundle getResourceBundle() {
        return rb;
    }

    @Override
    public void setResourceBundle(ResourceBundle rb) {
        this.rb = rb;
    }

    @Override
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    
    @Override
    public void preCloseAction() {

    }
        
    private void showUserDetails(User user) {
        if(user != null) {
            firstNameLabelValue.setText(user.getFirstname());
            lastNameLabelValue.setText(user.getLastname());
            loginLabelValue.setText(user.getLogin());
            passwordFieldValue.setText(user.getPassword());
            vacationLeftLabelValue.setText(formatVacationInfo(user));
            contractNameLabelValue.setText(user.getContract().getName());
            roleNameLabelValue.setText(user.getRole().getName());
            addressLabelValue.setText(formatAddressInfo(user.getAddress()));
        } else {
            firstNameLabelValue.setText("");
            lastNameLabelValue.setText("");
            loginLabelValue.setText("");
            passwordFieldValue.setText("");
            vacationLeftLabelValue.setText("");
            contractNameLabelValue.setText("");
            roleNameLabelValue.setText("");
            addressLabelValue.setText("");
        }
    }
    
    private String formatVacationInfo(User user) {
        StringBuilder sb = new StringBuilder();
        sb.append(user.getVacationleft()).append(" ").append(rb.getString(daysResourceKey));
        return sb.toString();
    }
    
    private String formatAddressInfo(Address address) {
        StringBuilder sb = new StringBuilder();
        sb.append(address.getStreetname()).append(" ");
        sb.append(address.getHousenumber()).append("\n");
        sb.append(address.getUnitnumber()).append(" ");
        sb.append(address.getUnitname()).append(" ");
        sb.append(address.getUnitlocation()).append("\n");
        sb.append(address.getZipcode()).append(" ");
        sb.append(address.getCity()).append("\n");
        sb.append(address.getCountry()).append("\\");
        sb.append(address.getState());
        return sb.toString();
    }

    private void openUserDetailsDialog(User user, DataAction dataAction) throws SQLException, IOException {
        UserDetailsViewController userDetailsViewController = (UserDetailsViewController)controllerRepository.get(UserDetailsViewController.class.getName());
        if(userDetailsViewController == null) {
            userDetailsViewController = new UserDetailsViewController(languageService, connection, undoService, userData);
            controllerRepository.put(UserDetailsViewController.class.getName(), userDetailsViewController);
        }

        DialogFactory dialogFactory = new DialogFactory(
            primaryStage, 
            userDetailsViewDialogTitleResourceKey, 
            userDetailsViewDialogIcon, 
            userDetailsViewResource, 
            rb, 
            userDetailsViewController);
        userDetailsViewDialog = dialogFactory.create(Modality.WINDOW_MODAL);
        userDetailsViewDialog.setWidth(400);
        userDetailsViewDialog.setHeight(350);
        
        userDetailsViewController.setAction(dataAction);
        userDetailsViewController.showUserDetails(user);
        
        ControllerUtilities.CenterOnDialog(primaryStage, userDetailsViewDialog);
        
        userDetailsViewDialog.showAndWait();        
    
        controllerRepository.remove(UserDetailsViewController.class.getName());
    }

    public boolean isUserValid(User user) {
        return !(ControllerUtilities.isNullOrEmpty(user.getFirstname()) ||
                ControllerUtilities.isNullOrEmpty(user.getLastname()) ||
                ControllerUtilities.isNullOrEmpty(user.getLogin()) ||
                ControllerUtilities.isNullOrEmpty(user.getPassword()) ||
                user.getVacationleft() == null ||
                user.getAddress() == null ||
                user.getRole() == null ||
                user.getContract() == null);
    }
    
}
