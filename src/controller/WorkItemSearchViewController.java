/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.WorkItem;
import model.Workrecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.LanguageService;
import service.PropertiesService;
import service.UndoService;
import sqlite.WorkItemDAO;
import sqlite.WorkrecordDAO;

/**
 * FXML Controller class
 *
 * @author adrest18
 */
public class WorkItemSearchViewController implements Initializable, IViewController {

    private static final String PREF_WORKITEM_SEARCH_TOOL_VIEW_DIALOG_SELECTED_RADIOBUTTON_KEY = "WorkItemSearchToolViewDialogSelectedRadioButton";
    private static final String PREF_WORKITEM_SEARCH_TOOL_VIEW_DIALOG_SELECTED_RADIOBUTTON_DEFAULT = "WorkrecordRadioButton";
    
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
    @FXML private TextArea searchResultTextArea;
    
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
    private RadioButton selectedRadioButton;
    private final WorkItemDAO workitemDAO;
    private final WorkrecordDAO workrecordDAO;
    
    public WorkItemSearchViewController(ControllerRepository controllerRepository, LanguageService languageService, 
                                        Connection connection, UndoService undoService, PropertiesService propertiesService) {
        this.controllerRepository = Objects.requireNonNull(controllerRepository, "controllerRepository");
        this.languageService = Objects.requireNonNull(languageService, "languageService");
        this.connection = Objects.requireNonNull(connection, "connection");
        this.undoService = Objects.requireNonNull(undoService, "undoService");
        this.propertiesService = Objects.requireNonNull(propertiesService, "propertiesService");
        
        this.workitemDAO = new WorkItemDAO(connection);
        this.workrecordDAO = new WorkrecordDAO(connection);
    }
    
    /**
     * Initializes the controller class.
     * @param location
     * @param rb
     */
    @Override
    public void initialize(URL location, ResourceBundle rb) {
        this.rb = rb;
        setupManualExclusiveGroup(workrecordRadioButton, sprintRadioButton, overallRadioButton);
        
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
    
    @FXML private void searchAction(ActionEvent event) throws SQLException {
        searchResultTextArea.clear();
        String searchText = searchForTextField.getText();
        List<WorkItem> workItemsFound = workitemDAO.searchForTerm(searchText);
        StringBuilder formatedText = new StringBuilder();
        if(!workItemsFound.isEmpty()) {
            for(WorkItem workItem : workItemsFound) {
                Optional<Workrecord> workrecord = workrecordDAO.selectWorkrecordFromId(workItem.getWorkrecordId());
                if(!workrecord.isEmpty()) {
                    formatedText.append("Sprint: ").append(workItem.getSprintId()).append(System.lineSeparator());
                    formatedText.append("Date: ").append(workrecord.get().getDate()).append(System.lineSeparator());
                    formatedText.append("Workitem: ").append(workItem.getName()).append(" (").append(workItem.getShortcut()).append(")").append(System.lineSeparator());
                    formatedText.append("Start: ").append(workItem.getStartTime()).append(" End: ").append(workItem.getEndTime()).append(System.lineSeparator());
                    formatedText.append("Description: ").append(System.lineSeparator()).append(workItem.getDescription()).append(System.lineSeparator()).append(System.lineSeparator());
                }
            }
            searchResultTextArea.setText(formatedText.toString());
        }
    }
    
    public void setupManualExclusiveGroup(RadioButton rb1, RadioButton rb2, RadioButton rb3) {
        RadioButton[] radioButtons = {rb1, rb2, rb3};
        
        String selectedRadionButton = propertiesService.getProperty(PREF_WORKITEM_SEARCH_TOOL_VIEW_DIALOG_SELECTED_RADIOBUTTON_KEY, PREF_WORKITEM_SEARCH_TOOL_VIEW_DIALOG_SELECTED_RADIOBUTTON_DEFAULT);
        switch(selectedRadionButton) {
            case "RadioButton[id=workrecordRadioButton, styleClass=radio-button]'Workrecord'" -> {
                workrecordRadioButton.setSelected(true);
                selectedRadioButton = workrecordRadioButton;
            }
            case "RadioButton[id=sprintRadioButton, styleClass=radio-button]'Sprint'" -> {
                sprintRadioButton.setSelected(true);
                selectedRadioButton = sprintRadioButton;
            }
            case "RadioButton[id=overallRadioButton, styleClass=radio-button]'Overall'" -> {
                overallRadioButton.setSelected(true);
                selectedRadioButton = overallRadioButton;
            }
            default -> {
                workrecordRadioButton.setSelected(true);
                selectedRadioButton = workrecordRadioButton;
            }

        }
        for (RadioButton radioButton : radioButtons) {
            radioButton.setOnAction((ActionEvent event) -> {
                // If the user clicks the already selected one, keep it selected
                if (!radioButton.isSelected()) {
                    radioButton.setSelected(true);
                    return;
                }
                // Unselect all other buttons and update the active one
                for (RadioButton other : radioButtons) {
                    if (other != radioButton) {
                        other.setSelected(false);
                    }
                }
                selectedRadioButton = radioButton;
            });
        }
    }
    
    public RadioButton getSelectedRadioButton() {
        return selectedRadioButton;
    }

}
