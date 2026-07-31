/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import commands.role.*;
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
import model.Role;
import org.apache.logging.log4j.*;
import service.*;
import sqlite.RoleDAO;
import utils.*;

/**
 *
 * @author stephan
 */
public class RoleViewController implements Initializable, IViewController {

    public enum DataAction { NEW, EDIT, DELETE };

    private final String roleNameResourceKey = "RoleName";
    private final String roleDescriptionResourceKey = "RoleDescription";
    private final String roleDetailsLabelResourceKey = "RoleDetailsLabel";
    private final String newResourceKey = "New";
    private final String editResourceKey = "Edit";
    private final String deleteResourceKey = "Delete";
    private final String noRoleSelectionAlertTitle = "NoSelectionAlertTitle";
    private final String noRoleSelectionAlertHeader = "NoRoleSelectionAlertHeader";
    private final String noRoleSelectionAlertContent = "NoRoleSelectionAlertContent";
    private final String newRoleEvent = "NewRole";
    private final String editRoleEvent = "EditRole";
    private final String deleteRoleEvent = "DeleteRole";
    
    private final Logger log = LogManager.getLogger(RoleViewController.class.getName());
    
    private Stage primaryStage;
    private final ControllerRepository controllerRepository;
    private final LanguageService languageService;
    private final Connection connection;
    private final UndoService undoService;
    private final PropertiesService propertiesService;
    private ResourceBundle rb;
    private final RoleDAO roleDao;
    private ObservableList<Role> roleData;
    private Stage roleDetailsViewDialog;
    public EventManager eventManager;

    private final String roleDetailsViewDialogIcon = "icons/app-maid.png";
    private final String roleDetailsViewDialogTitleResourceKey = "RoleDetailsViewTitle";
    private final String roleDetailsViewResource = "/view/RoleDetailsView.fxml";
    
    @FXML
    private TableView<Role> roleTableView;
    @FXML
    private TableColumn<Role, String> roleNameTableColumn;
    @FXML
    private TableColumn<Role, String> roleDescriptionTableColumn;
    @FXML
    private Label roleDetailsLabel;
    
    @FXML
    private Label roleNameLabel;
    @FXML
    private Label roleDescriptionLabel;
    
    @FXML
    private Label roleNameLabelValue;
    @FXML
    private TextArea roleDescriptionTextArea;
    
    @FXML
    private Button newButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    
    public RoleViewController(ControllerRepository controllerRepository, LanguageService languageService, Connection connection, UndoService undoService, PropertiesService propertiesService) throws SQLException {
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
        this.roleDao = new RoleDAO(connection);
        this.roleData = FXCollections.observableArrayList(this.roleDao.selectAll());        
        this.eventManager = new EventManager();
        this.eventManager.registerEventType(newRoleEvent);
        this.eventManager.registerEventType(editRoleEvent);
        this.eventManager.registerEventType(deleteRoleEvent);        
    }

    @FXML
    private void newRoleAction(ActionEvent event) throws SQLException, IOException {
        Role newRole = new Role(roleDao.getNextId());
        openRoleDetailsDialog(newRole, DataAction.NEW);
        if(isRoleValid(newRole)) {
            NewRoleCommand cmd = new NewRoleCommand(controllerRepository, eventManager, roleTableView, newRole, roleDao);
            undoService.execute(cmd);
        }
    }
    
    @FXML
    private void editRoleAction(ActionEvent event) throws SQLException, IOException {
        Role selectedRole = roleTableView.getSelectionModel().getSelectedItem();
        if(selectedRole != null) {
            Role originalRole = new Role(selectedRole);
            openRoleDetailsDialog(selectedRole, DataAction.EDIT);
            showRoleDetails(selectedRole);
            if(!originalRole.equals(selectedRole)) {
                EditRoleCommand cmd = new EditRoleCommand(controllerRepository, eventManager, roleTableView, originalRole, selectedRole, roleDao);
                undoService.execute(cmd);
            }
        } else {
            ControllerUtilities.showNoItemSelectedAlert(primaryStage, rb, noRoleSelectionAlertTitle, noRoleSelectionAlertHeader, noRoleSelectionAlertContent);
        }
    }

    @FXML
    private void deleteRoleAction(ActionEvent event) throws SQLException {
        Role selectedRole = roleTableView.getSelectionModel().getSelectedItem();
        if(selectedRole != null) {
            DeleteRoleCommand cmd = new DeleteRoleCommand(controllerRepository, eventManager, roleTableView, selectedRole, roleDao);
            undoService.execute(cmd);
        } else {
            ControllerUtilities.showNoItemSelectedAlert(primaryStage, rb, noRoleSelectionAlertTitle, noRoleSelectionAlertHeader, noRoleSelectionAlertContent);
        }
    }
    
    @Override
    public void initialize(URL location, ResourceBundle rb) {
        this.rb = rb;
        
        roleTableView.setItems(roleData);

        roleNameTableColumn.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
        roleNameTableColumn.prefWidthProperty().bind(roleTableView.widthProperty().multiply(0.3));

        roleDescriptionTableColumn.setCellValueFactory(cellData -> cellData.getValue().getDescriptionProperty());
        roleDescriptionTableColumn.prefWidthProperty().bind(roleTableView.widthProperty().multiply(0.7));

        Optional<Role> firstRole = roleData.stream().findFirst();
        if(firstRole != null) {
            showRoleDetails(firstRole.get());
            roleTableView.getSelectionModel().select(0);
        } else {
            showRoleDetails(null);
        }

        roleTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showRoleDetails(newValue));
                
        languageService.updateGuiItems();
    }

    @Override
    public void updateGuiItems() {
        roleNameTableColumn.setText(rb.getString(roleNameResourceKey));
        roleDescriptionTableColumn.setText(rb.getString(roleDescriptionResourceKey));
        roleDetailsLabel.setText(rb.getString(roleDetailsLabelResourceKey));
        roleNameLabel.setText(rb.getString(roleNameResourceKey));
        roleDescriptionLabel.setText(rb.getString(roleDescriptionResourceKey));
        newButton.setText(rb.getString(newResourceKey));
        editButton.setText(rb.getString(editResourceKey));
        deleteButton.setText(rb.getString(deleteResourceKey));
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
        
    private void showRoleDetails(Role role) {
        if(role != null) {
            roleNameLabelValue.setText(role.getName());
            roleDescriptionTextArea.setText(role.getDescription());
        } else {
            roleNameLabelValue.setText("");
            roleDescriptionTextArea.setText("");
        }
    }
    
    private void openRoleDetailsDialog(Role role, DataAction dataAction) throws SQLException, IOException {
        RoleDetailsViewController roleDetailsViewController = (RoleDetailsViewController)controllerRepository.get(RoleDetailsViewController.class.getName());
        if(roleDetailsViewController == null) {
            roleDetailsViewController = new RoleDetailsViewController(languageService, connection, undoService, roleData);
            controllerRepository.put(RoleDetailsViewController.class.getName(), roleDetailsViewController);
        }

        DialogFactory dialogFactory = new DialogFactory(
            primaryStage, 
            roleDetailsViewDialogTitleResourceKey, 
            roleDetailsViewDialogIcon, 
            roleDetailsViewResource, 
            rb, 
            roleDetailsViewController);
        roleDetailsViewDialog = dialogFactory.create();
        roleDetailsViewDialog.setWidth(400);
        roleDetailsViewDialog.setHeight(350);
                
        roleDetailsViewController.setAction(dataAction);
        roleDetailsViewController.showRoleDetails(role);
        
        ControllerUtilities.CenterOnDialog(primaryStage, roleDetailsViewDialog);

        roleDetailsViewDialog.showAndWait();        
    
        controllerRepository.remove(RoleDetailsViewController.class.getName());
    }

    public boolean isRoleValid(Role role) {
        return !(ControllerUtilities.isNullOrEmpty(role.getName()) ||
                ControllerUtilities.isNullOrEmpty(role.getDescription()));
    }
    
}
