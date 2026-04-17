/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import controller.ProjectViewController.DataAction;
import java.net.URL;
import java.sql.*;
import java.util.*;
import javafx.beans.value.*;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Project;
import org.apache.logging.log4j.*;
import service.*;
import utils.ControllerUtilities;

/**
 *
 * @author adrest18
 */
@SuppressWarnings("unused")
class ProjectDetailsViewController implements Initializable, IViewController {

    private final String projectNameResourceKey = "ProjectName";
    private final String projectCostUnitResourceKey = "ProjectCostUnit";
    private final String projectIsWorktimeRelevant = "ProjectIsWorktimeRelevant";    
    private final String projectIsVacationRelevant = "ProjectIsVacationRelevant";    
    private final String projectIsComptimeRelevant = "ProjectIsComptimeRelevant";    
    private final String projectDescriptionResourceKey = "ProjectDescription";
    private final String acceptResourceKey = "Accept";
    private final String cancelResourceKey = "Cancel";

    private final Logger log = LogManager.getLogger(ProjectDetailsViewController.class.getName());
    
    private Stage primaryStage;
    private Project project;
    private final LanguageService languageService;
    private final Connection connection;
    private final UndoService undoService;
    private ResourceBundle rb;
    private final ControllerRepository controllerRepository;
    private final ObservableList<Project> projectData;
    private DataAction dataAction;
    
    private String oldProjectName;
    private String newProjectName;
    private String oldProjectCostUnit;
    private String newProjectCostUnit;
    private String oldProjectIsWorkrecordRelevant;
    private String newProjectIsWorkrecordRelevant;
    private String oldProjectIsVacationRelevant;
    private String newProjectIsVacationRelevant;
    private String oldProjectIsComptimeRelevant;
    private String newProjectIsComptimeRelevant;
    private String oldProjectDescription;
    private String newProjectDescription;
    
    @FXML
    private Label projectNameLabel;
    @FXML
    private Label projectCostUnitLabel;
    @FXML
    private Label projectIsWorktimeRelevantLabel;
    @FXML
    private Label projectIsVacationRelevantLabel;
    @FXML
    private Label projectIsComptimeRelevantLabel;
    @FXML
    private Label projectDescriptionLabel;
    
    @FXML
    private TextField projectNameTextFieldValue;
    @FXML
    private TextField projectCostUnitTextFieldValue;
    @FXML
    private ComboBox<String> projectIsWorktimeRelevantComboBoxValue;
    @FXML
    private ComboBox<String> projectIsVacationRelevantComboBoxValue;
    @FXML
    private ComboBox<String> projectIsComptimeRelevantComboBoxValue;
    @FXML
    private TextArea projectDescriptionTextAreaValue;
    
    @FXML 
    private Button acceptButton;
    @FXML
    private Button cancelButton;
    
    ProjectDetailsViewController(LanguageService languageService, Connection connection, UndoService undoService, ObservableList<Project> projectData) {
        if(languageService == null) throw new NullPointerException("languageService");
        if(connection == null) throw new NullPointerException("connection");
        if(undoService == null) throw new NullPointerException("undoService");
        if(projectData == null) throw new NullPointerException("projectData");
        
        this.languageService = languageService;
        this.connection = connection;
        this.undoService = undoService;
        this.controllerRepository = ControllerRepository.getInstance();
        
        this.projectData = projectData;
    }

    @FXML
    @SuppressWarnings("unused")
    private void acceptAction(ActionEvent event) {
        project.setName(projectNameTextFieldValue.getText());
        project.setCostunit(projectCostUnitTextFieldValue.getText());
        project.setIsWorktimeRelevant(projectIsWorktimeRelevantComboBoxValue.getSelectionModel().getSelectedItem());
        project.setIsVacationRelevant(projectIsVacationRelevantComboBoxValue.getSelectionModel().getSelectedItem());
        project.setIsComptimeRelevant(projectIsComptimeRelevantComboBoxValue.getSelectionModel().getSelectedItem());
        project.setDescription(projectDescriptionTextAreaValue.getText());
        
        primaryStage.close();
    }
    
    @FXML
    @SuppressWarnings("unused")
    private void cancelAction(ActionEvent event) {
        primaryStage.close();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.rb = rb;
    
        projectIsWorktimeRelevantComboBoxValue.setItems(FXCollections.observableArrayList("True", "False"));
        projectIsVacationRelevantComboBoxValue.setItems(FXCollections.observableArrayList("True", "False"));
        projectIsComptimeRelevantComboBoxValue.setItems(FXCollections.observableArrayList("True", "False"));
        
        acceptButton.setDisable(true);
        
        projectNameTextFieldValue.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            newProjectName = newValue;
            isInputValid();
        });        
        projectCostUnitTextFieldValue.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            newProjectCostUnit = newValue;
            isInputValid();
        });
        projectIsWorktimeRelevantComboBoxValue.valueProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            newProjectIsWorkrecordRelevant = (String)newValue;
            isInputValid();        
        });
        projectIsVacationRelevantComboBoxValue.valueProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            newProjectIsVacationRelevant = (String)newValue;
            isInputValid();        
        });
        projectIsComptimeRelevantComboBoxValue.valueProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            newProjectIsComptimeRelevant = (String)newValue;
            isInputValid();        
        });
        projectDescriptionTextAreaValue.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            newProjectDescription = newValue;
            isInputValid();
        });
    }

    @Override
    public void updateGuiItems() {
        projectNameLabel.setText(rb.getString(projectNameResourceKey));
        projectCostUnitLabel.setText(rb.getString(projectCostUnitResourceKey));
        projectIsWorktimeRelevantLabel.setText(rb.getString(projectIsWorktimeRelevant));
        projectIsVacationRelevantLabel.setText(rb.getString(projectIsVacationRelevant));
        projectIsComptimeRelevantLabel.setText(rb.getString(projectIsComptimeRelevant));
        projectDescriptionLabel.setText(rb.getString(projectDescriptionResourceKey));
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
    
    void showProjectDetails(Project project) {
        this.project = project;
        
        //We save the actual project information to be able to 
        //check for changes of each Information at validation of Innput.
        saveActualProjectInformation(project);
        
        projectNameTextFieldValue.setText(project.getName());
        projectCostUnitTextFieldValue.setText(project.getCostunit());
        projectIsWorktimeRelevantComboBoxValue.setValue(project.getIsWorktimeRelevant());
        projectIsVacationRelevantComboBoxValue.setValue(project.getIsVacationRelevant());
        projectIsComptimeRelevantComboBoxValue.setValue(project.getIsComptimeRelevant());
        projectDescriptionTextAreaValue.setText(project.getDescription());
    }
    
    private void saveActualProjectInformation(Project project) {
        oldProjectName = project.getName();
        oldProjectCostUnit = project.getCostunit();
        oldProjectIsWorkrecordRelevant = project.getIsWorktimeRelevant();
        oldProjectIsVacationRelevant = project.getIsVacationRelevant();
        oldProjectIsComptimeRelevant = project.getIsComptimeRelevant();
        oldProjectDescription = project.getDescription();
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
        boolean r1 = isProjectNameFilled(projectNameTextFieldValue);
        boolean r2 = isProjectCostUnitFilled(projectCostUnitTextFieldValue);
        boolean r3 = isProjectDescriptionFilled(projectDescriptionTextAreaValue);
        
        boolean result = r1 && r2 && r3;
        
        return result;
    }

    private boolean isProjectNameFilled(TextField projectName) {
        return !ControllerUtilities.isNullOrEmpty(projectName.getText());
    }
    
    private boolean isProjectCostUnitFilled(TextField projectCostUnit) {
        return !ControllerUtilities.isNullOrEmpty(projectCostUnit.getText());
    }

    private boolean isProjectDescriptionFilled(TextArea projectDescription) {
        return !ControllerUtilities.isNullOrEmpty(projectDescription.getText());
    }
    
    private boolean isInputUnique() {
        boolean r1 = isProjectNameUnique(projectNameTextFieldValue);
        
        boolean result = r1;
        
        return result;
    }
    
    private boolean isProjectNameUnique(TextField projectName) {
        List<Project> result = projectData.stream().filter(c -> c.getName().equals(projectName.getText())).toList();
        return result.isEmpty();
    }
    
    private boolean hasInputChanged() {
        boolean r1 = !oldProjectName.equals(newProjectName);
        boolean r2 = !oldProjectCostUnit.equals(newProjectCostUnit);
        boolean r3 = !oldProjectIsWorkrecordRelevant.equals(newProjectIsWorkrecordRelevant);
        boolean r4 = !oldProjectIsVacationRelevant.equals(newProjectIsVacationRelevant);
        boolean r5 = !oldProjectIsComptimeRelevant.equals(newProjectIsComptimeRelevant);
        boolean r6 = !oldProjectDescription.equals(newProjectDescription);
        
        boolean result = r1 || r2 || r3 || r4 || r5 || r6;
        
        return result;
    }

}
