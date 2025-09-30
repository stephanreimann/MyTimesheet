/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import controller.RoleViewController.DataAction;
import java.net.URL;
import java.sql.*;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Role;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.*;
import utils.ControllerUtilities;

/**
 *
 * @author stephan
 */
public class RoleDetailsViewController implements Initializable, IViewController {

    private final String roleNameResourceKey = "RoleName";
    private final String roleDescriptionResourceKey = "RoleDescription";
    private final String acceptResourceKey = "Accept";
    private final String cancelResourceKey = "Cancel";

    private final Logger log = LogManager.getLogger(RoleDetailsViewController.class.getName());
    
    private Stage primaryStage;
    private Role role;
    private final LanguageService languageService;
    private final Connection connection;
    private final UndoService undoService;
    private ResourceBundle rb;
    private final ControllerRepository controllerRepository;
    private final ObservableList<Role> roleData;
    private DataAction dataAction;
    
    private String oldRoleName;
    private String newRoleName;
    private String oldRoleDescription;
    private String newRoleDescription;
            
    @FXML
    private Label roleNameLabel;
    @FXML
    private Label roleDescriptionLabel;
    
    @FXML
    private TextField roleNameTextFieldValue;
    @FXML
    private TextArea roleDescriptionTextAreaValue;
    
    @FXML 
    private Button acceptButton;
    @FXML
    private Button cancelButton;
    
    public RoleDetailsViewController(LanguageService languageService, Connection connection, UndoService undoService, ObservableList<Role> roleData) throws SQLException {
        if(languageService == null) throw new NullPointerException("languageService");
        if(connection == null) throw new NullPointerException("connection");
        if(undoService == null) throw new NullPointerException("undoService");
        if(roleData == null) throw new NullPointerException("roleData");
        
        this.languageService = languageService;
        this.connection = connection;
        this.undoService = undoService;
        this.controllerRepository = ControllerRepository.getInstance();
        
        this.roleData = roleData;
    }
    
    @FXML
    private void acceptAction(ActionEvent event) {
        role.setName(roleNameTextFieldValue.getText());
        role.setDescription(roleDescriptionTextAreaValue.getText());
        
        primaryStage.close();
    }
    
    @FXML
    private void cancelAction(ActionEvent event) {
        primaryStage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle rb) {
        this.rb = rb;
    
        acceptButton.setDisable(true);
        
        roleNameTextFieldValue.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            newRoleName = newValue;
            isInputValid();
        });        
        roleDescriptionTextAreaValue.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            newRoleDescription = newValue;
            isInputValid();
        });
    }

    @Override
    public void updateGuiItems() {
        roleNameLabel.setText(rb.getString(roleNameResourceKey));
        roleDescriptionLabel.setText(rb.getString(roleDescriptionResourceKey));
        acceptButton.setText(rb.getString(acceptResourceKey));
        cancelButton.setText(rb.getString(cancelResourceKey));
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
        
    public void setAction(DataAction action) {
        this.dataAction = action;
    }
    
    public void showRoleDetails(Role role) throws SQLException {
        this.role = role;
        
        //We save the actual role information to be able to 
        //check for changes of each Information at validation of Innput.
        saveActualRoleInformation(role);
        
        roleNameTextFieldValue.setText(role.getName());
        roleDescriptionTextAreaValue.setText(role.getDescription());
    }

    private void saveActualRoleInformation(Role role) {
        oldRoleName = role.getName();
        oldRoleDescription = role.getDescription();
    }
    
    private boolean isInputValid() {
        boolean result = false;

        switch(dataAction) {
            case DataAction.NEW -> {
                boolean r1 = isInputFilled();
                boolean r2 = isInputUnique();
                
                result = r1 && r2;       
            }
            case DataAction.EDIT -> {
                boolean r1 = isInputFilled();
                boolean r2 = hasInputChanged();

                result = r1 && r2;       
            }
        }
        
        if(result) {
            acceptButton.setDisable(false);
            return true;
        } else {
            acceptButton.setDisable(true);
            return false;
        }
    }

    private boolean isInputFilled() {
        boolean r1 = isRoleNameFilled(roleNameTextFieldValue);
        boolean r2 = isRoleDescriptionFilled(roleDescriptionTextAreaValue);
        
        boolean result = r1 && r2;
        
        return result;
    }
    
    private boolean isRoleNameFilled(TextField roleName) {
        return !ControllerUtilities.isNullOrEmpty(roleName.getText());
    }
    
    private boolean isRoleDescriptionFilled(TextArea roleDescription) {
        return !ControllerUtilities.isNullOrEmpty(roleDescription.getText());
    }

    private boolean isInputUnique() {
        boolean r1 = isRoleNameUnique(roleNameTextFieldValue);
        
        boolean result = r1;
        
        return result;
    }
    
    private boolean isRoleNameUnique(TextField roleName) {
        List<Role> result = roleData.stream().filter(c -> c.getName().equals(roleName.getText())).toList();
        return result.isEmpty();
    }
    
    private boolean hasInputChanged() {
        boolean r1 = !oldRoleName.equals(newRoleName);
        boolean r2 = !oldRoleDescription.equals(newRoleDescription);
        
        boolean result = r1 || r2;
        
        return result;
    }
    
}
