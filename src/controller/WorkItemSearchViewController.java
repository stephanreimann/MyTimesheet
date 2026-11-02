/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import java.net.URL;
import java.sql.Connection;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.LanguageService;
import service.PropertiesService;
import service.UndoService;

/**
 * FXML Controller class
 *
 * @author adrest18
 */
public class WorkItemSearchViewController implements Initializable, IViewController {

    private final String searchForLabelTextResourceKey = "SearchForLabelText";
    private final String searchForTextFieldResourceKey = "SearchForTextFieldText";
    private final String searchButtonTextResourceKey = "SearchButtonText";
    private final String searchInLabelResourceKey = "SearchInLabel";
    private final String workrecordRadioButtonTextResourceKey = "WorkrecordRadioButtonText";
    private final String sprintRadioButtonTextResourceKey = "SprintRadioButtonText";
    private final String overallRadioButtonTextResourceKey = "OverallRadioButtonText";
    
    @FXML private Label searchForLabel;
    @FXML private TextField searchForTextField;
    @FXML private Button searchForButton;
    @FXML private Label searchInLabel;
    @FXML private RadioButton workrecordRadioButton;
    @FXML private RadioButton sprintRadioButton;
    @FXML private RadioButton overallRadioButton;
            
    @SuppressWarnings("unused") 
    private final Logger log = LogManager.getLogger(WorkItemSearchViewController.class);

    @SuppressWarnings("unused") 
    private Stage primaryStage;
    private final ControllerRepository controllerRepository;
    private final LanguageService languageService;
    @SuppressWarnings("unused") 
    private final Connection connection;
    @SuppressWarnings("unused") 
    private final UndoService undoService;
    @SuppressWarnings("unused") 
    private final PropertiesService propertiesService;
    private ResourceBundle rb;
    
    public WorkItemSearchViewController(ControllerRepository controllerRepository, LanguageService languageService, 
                                        Connection connection, UndoService undoService, PropertiesService propertiesService) {
        this.controllerRepository = Objects.requireNonNull(controllerRepository, "controllerRepository");
        this.languageService = Objects.requireNonNull(languageService, "languageService");
        this.connection = Objects.requireNonNull(connection, "connection");
        this.undoService = Objects.requireNonNull(undoService, "undoService");
        this.propertiesService = Objects.requireNonNull(propertiesService, "propertiesService");
        
    }
    
    /**
     * Initializes the controller class.
     * @param location
     * @param rb
     */
    @Override
    public void initialize(URL location, ResourceBundle rb) {
        this.rb = rb;

        languageService.updateGuiItems();          
    }    

    @Override
    public void updateGuiItems() {
        searchForLabel.setText(rb.getString(searchForLabelTextResourceKey));
        searchForTextField.setPromptText(rb.getString(searchForTextFieldResourceKey));
        searchForButton.setText(rb.getString(searchButtonTextResourceKey));
        searchInLabel.setText(rb.getString(searchInLabelResourceKey));
        workrecordRadioButton.setText(rb.getString(workrecordRadioButtonTextResourceKey));
        sprintRadioButton.setText(rb.getString(sprintRadioButtonTextResourceKey));
        overallRadioButton.setText(rb.getString(overallRadioButtonTextResourceKey));
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
        MainToolBarViewController mainController = (MainToolBarViewController) controllerRepository.get(MainToolBarViewController.class.getName());
        if (mainController != null) {
            mainController.getWorkItemSearchButton().setDisable(false);
        }
    }
    
}
