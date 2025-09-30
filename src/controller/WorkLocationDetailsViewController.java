/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import controller.WorkLocationViewController.DataAction;
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
import model.Worklocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.*;
import utils.ControllerUtilities;

/**
 *
 * @author stephan
 */
public class WorkLocationDetailsViewController implements Initializable, IViewController {

    private final String worklocationNameResourceKey = "WorklocationName";
    private final String worklocationDescriptionResourceKey = "WorklocationDescription";
    private final String acceptResourceKey = "Accept";
    private final String cancelResourceKey = "Cancel";

    private final Logger log = LogManager.getLogger(WorkLocationDetailsViewController.class.getName());
    
    private Stage primaryStage;
    private Worklocation worklocation;
    private final LanguageService languageService;
    private final Connection connection;
    private final UndoService undoService;
    private ResourceBundle rb;
    private final ControllerRepository controllerRepository;
    private final ObservableList<Worklocation> worklocationData;
    private DataAction dataAction;
    
    private String oldWorklocationName;
    private String newWorklocationName;
    private String oldWorklocationDescription;
    private String newWorklocationDescription;
    
    @FXML
    private Label worklocationNameLabel;
    @FXML
    private Label worklocationDescriptionLabel;
    
    @FXML
    private TextField worklocationNameTextFieldValue;
    @FXML
    private TextArea worklocationDescriptionTextAreaValue;
    
    @FXML 
    private Button acceptButton;
    @FXML
    private Button cancelButton;
    
    public WorkLocationDetailsViewController(LanguageService languageService, Connection connection, UndoService undoService, ObservableList<Worklocation> worklocationData) throws SQLException {
        if(languageService == null) throw new NullPointerException("languageService");
        if(connection == null) throw new NullPointerException("connection");
        if(undoService == null) throw new NullPointerException("undoService");
        if(worklocationData == null) throw new NullPointerException("roleData");
        
        this.languageService = languageService;
        this.connection = connection;
        this.undoService = undoService;
        this.controllerRepository = ControllerRepository.getInstance();
        
        this.worklocationData = worklocationData;
    }
    
    @FXML
    private void acceptAction(ActionEvent event) {
        worklocation.setName(worklocationNameTextFieldValue.getText());
        worklocation.setDescription(worklocationDescriptionTextAreaValue.getText());
        
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
        
        worklocationNameTextFieldValue.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            newWorklocationName = newValue;
            isInputValid();
        });        
        worklocationDescriptionTextAreaValue.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            newWorklocationDescription = newValue;
            isInputValid();
        });
    }

    @Override
    public void updateGuiItems() {
        worklocationNameLabel.setText(rb.getString(worklocationNameResourceKey));
        worklocationDescriptionLabel.setText(rb.getString(worklocationDescriptionResourceKey));
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
    
    public void showWorklocationDetails(Worklocation worklocation) throws SQLException {
        this.worklocation = worklocation;

        //We save the actual worklocation information to be able to 
        //check for changes of each Information at validation of Innput.
        saveActualWorklocationInformation(worklocation);
        
        worklocationNameTextFieldValue.setText(worklocation.getName());
        worklocationDescriptionTextAreaValue.setText(worklocation.getDescription());
    }

    private void saveActualWorklocationInformation(Worklocation worklocation) {
        oldWorklocationName = worklocation.getName();
        oldWorklocationDescription = worklocation.getDescription();
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
        boolean r1 = isWorklocationNameFilled(worklocationNameTextFieldValue);
        boolean r2 = isWorklocationDescriptionFilled(worklocationDescriptionTextAreaValue);
        
        boolean result = r1 && r2;
        
        return result;
    }
    
    private boolean isWorklocationNameFilled(TextField worklocationName) {
        return !ControllerUtilities.isNullOrEmpty(worklocationName.getText());
    }
    
    private boolean isWorklocationDescriptionFilled(TextArea worklocationDescription) {
        return !ControllerUtilities.isNullOrEmpty(worklocationDescription.getText());
    }

    private boolean isInputUnique() {
        boolean r1 = isWorklocationNameUnique(worklocationNameTextFieldValue);
        
        boolean result = r1;
        
        return result;
    }

    private boolean isWorklocationNameUnique(TextField worklocationName) {
        List<Worklocation> result = worklocationData.stream().filter(c -> c.getName().equals(worklocationName.getText())).toList();
        return result.isEmpty();
    }

    private boolean hasInputChanged() {
        boolean r1 = !oldWorklocationName.equals(newWorklocationName);
        boolean r2 = !oldWorklocationDescription.equals(newWorklocationDescription);
        
        boolean result = r1 || r2;
        
        return result;
    }
    
}
