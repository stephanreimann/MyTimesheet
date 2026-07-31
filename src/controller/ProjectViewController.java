/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import commands.project.*;
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
import model.Project;
import org.apache.logging.log4j.*;
import service.*;
import sqlite.ProjectDAO;
import utils.*;

/**
 *
 * @author adrest18
 */
public class ProjectViewController implements Initializable, IViewController {

    public enum DataAction { NEW, EDIT, DELETE };
    
    private final String projectNameResourceKey = "ProjectName";
    private final String projectCostUnitResourceKey = "ProjectCostUnit";
    private final String projectIsWorktimeRelevant = "ProjectIsWorktimeRelevant";
    private final String projectIsVacationRelevant = "ProjectIsVacationRelevant";
    private final String projectIsComptimeRelevant = "ProjectIsComptimeRelevant";
    private final String projectDescriptionResourceKey = "ProjectDescription";
    private final String projectDetailsLabelResourceKey = "ProjectDetailsLabel";
    private final String newResourceKey = "New";
    private final String editResourceKey = "Edit";
    private final String deleteResourceKey = "Delete";
    private final String noProjectSelectionAlertTitle = "NoSelectionAlertTitle";
    private final String noProjectSelectionAlertHeader = "NoProjectSelectionAlertHeader";
    private final String noProjectSelectionAlertContent = "NoProjectSelectionAlertContent";
    private final String newProjectEvent = "NewProject";
    private final String editProjectEvent = "EditProject";
    private final String deleteProjectEvent = "DeleteProject";
    
    private final Logger log = LogManager.getLogger(RoleViewController.class.getName());
    
    private Stage primaryStage;
    private final ControllerRepository controllerRepository;
    private final LanguageService languageService;
    private final Connection connection;
    private final UndoService undoService;
    private final PropertiesService propertiesService;
    private ResourceBundle rb;
    private final ProjectDAO projectDao;
    private ObservableList<Project> projectData;
    private Stage projectDetailsViewDialog;
    public EventManager eventManager;
    
    private final String projectDetailsViewDialogIcon = "icons/app-maid.png";
    private final String projectDetailsViewDialogTitleResourceKey = "ProjectDetailsViewTitle";
    private final String projectDetailsViewResource = "/view/ProjectDetailsView.fxml";
    
    @FXML
    private TableView<Project> projectTableView;
    @FXML
    private TableColumn<Project, String> projectNameTableColumn;
    @FXML
    private Label projectDetailsLabel;

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
    private Label projectNameLabelValue;
    @FXML
    private Label projectCostUnitLabelValue;
    @FXML
    private Label projectIsWorktimeRelevantLabelValue;
    @FXML
    private Label projectIsVacationRelevantLabelValue;
    @FXML
    private Label projectIsComptimeRelevantLabelValue;
    @FXML
    private Label projectDescriptionLabelValue;

    @FXML
    private Button newButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    
    public ProjectViewController(ControllerRepository controllerRepository, LanguageService languageService, Connection connection, UndoService undoService, PropertiesService propertiesService) throws SQLException {
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
        this.projectDao = new ProjectDAO(connection);
        this.projectData = FXCollections.observableArrayList(this.projectDao.selectAll());
        this.eventManager = new EventManager();
        this.eventManager.registerEventType(newProjectEvent);
        this.eventManager.registerEventType(editProjectEvent);
        this.eventManager.registerEventType(deleteProjectEvent);
    }
    
    @FXML
    private void newProjectAction(ActionEvent event) throws SQLException, IOException {
        Project newProject = new Project(projectDao.getNextId());
        openProjectDetailsDialog(newProject, DataAction.NEW);
        if(isProjectValid(newProject)) {
            NewProjectCommand cmd = new NewProjectCommand(controllerRepository, eventManager, projectTableView, newProject, projectDao);
            undoService.execute(cmd);
        }
    }
    
    @FXML
    private void editProjectAction(ActionEvent event) throws SQLException, IOException {
        Project selectedProject = projectTableView.getSelectionModel().getSelectedItem();
        if(selectedProject != null) {
            Project originalProject = new Project(selectedProject);
            openProjectDetailsDialog(selectedProject, DataAction.EDIT);
            showProjectDetails(selectedProject);
            if(!originalProject.equals(selectedProject)) {
                EditProjectCommand cmd = new EditProjectCommand(controllerRepository, eventManager, projectTableView, originalProject, selectedProject, projectDao);
                undoService.execute(cmd);
            }
        } else {
            ControllerUtilities.showNoItemSelectedAlert(primaryStage, rb, noProjectSelectionAlertTitle, noProjectSelectionAlertHeader, noProjectSelectionAlertContent);
        }
    }

    @FXML
    private void deleteProjectAction(ActionEvent event) throws SQLException {
        Project selectedProject = projectTableView.getSelectionModel().getSelectedItem();
        if(selectedProject != null) {
            DeleteProjectCommand cmd = new DeleteProjectCommand(controllerRepository, eventManager, projectTableView, selectedProject, projectDao);
            undoService.execute(cmd);
        } else {
            ControllerUtilities.showNoItemSelectedAlert(primaryStage, rb, noProjectSelectionAlertTitle, noProjectSelectionAlertHeader, noProjectSelectionAlertContent);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle rb) {
        this.rb = rb;
        
        projectTableView.setItems(projectData);

        projectNameTableColumn.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
        projectNameTableColumn.prefWidthProperty().bind(projectTableView.widthProperty().multiply(0.3));

        Optional<Project> firstProject = projectData.stream().findFirst();
        if(firstProject != null) {
            showProjectDetails(firstProject.get());
            projectTableView.getSelectionModel().select(0);
        } else {
            showProjectDetails(null);
        }

        projectTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showProjectDetails(newValue));
                
        languageService.updateGuiItems();
    }
    
    @Override
    public void updateGuiItems() {
        projectNameTableColumn.setText(rb.getString(projectNameResourceKey));
        projectDetailsLabel.setText(rb.getString(projectDetailsLabelResourceKey));
        projectNameLabel.setText(rb.getString(projectNameResourceKey));
        projectCostUnitLabel.setText(rb.getString(projectCostUnitResourceKey));
        projectIsWorktimeRelevantLabel.setText(rb.getString(projectIsWorktimeRelevant));
        projectIsVacationRelevantLabel.setText(rb.getString(projectIsVacationRelevant));
        projectIsComptimeRelevantLabel.setText(rb.getString(projectIsComptimeRelevant));
        projectDescriptionLabel.setText(rb.getString(projectDescriptionResourceKey));
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
        
    public EventManager getEventManager() {
        return eventManager;
    }
    
    private void showProjectDetails(Project project) {
        if(project != null) {
            projectNameLabelValue.setText(project.getName());
            projectCostUnitLabelValue.setText(project.getCostunit());
            projectIsWorktimeRelevantLabelValue.setText(project.getIsWorktimeRelevant());
            projectIsVacationRelevantLabelValue.setText(project.getIsVacationRelevant());
            projectIsComptimeRelevantLabelValue.setText(project.getIsComptimeRelevant());
            projectDescriptionLabelValue.setText(project.getDescription());
        } else {
            projectNameLabelValue.setText("");
            projectCostUnitLabelValue.setText("");
            projectIsWorktimeRelevantLabelValue.setText("");
            projectIsVacationRelevantLabelValue.setText("");
            projectIsComptimeRelevantLabelValue.setText("");
            projectDescriptionLabelValue.setText("");
        }
    }

    private void openProjectDetailsDialog(Project project, DataAction dataAction) throws IOException {
        ProjectDetailsViewController projectDetailsViewController = (ProjectDetailsViewController)controllerRepository.get(ProjectDetailsViewController.class.getName());
        if(projectDetailsViewController == null) {
            projectDetailsViewController = new ProjectDetailsViewController(languageService, connection, undoService, projectData);
            controllerRepository.put(ProjectDetailsViewController.class.getName(), projectDetailsViewController);
        }

        DialogFactory dialogFactory = new DialogFactory(
            primaryStage, 
            projectDetailsViewDialogTitleResourceKey, 
            projectDetailsViewDialogIcon, 
            projectDetailsViewResource, 
            rb, 
            projectDetailsViewController);
        projectDetailsViewDialog = dialogFactory.create();
        projectDetailsViewDialog.setWidth(400);
        projectDetailsViewDialog.setHeight(400);
        
        projectDetailsViewController.setAction(dataAction);
        projectDetailsViewController.showProjectDetails(project);
                
        ControllerUtilities.CenterOnDialog(primaryStage, projectDetailsViewDialog);

        projectDetailsViewDialog.showAndWait();        
    
        controllerRepository.remove(RoleDetailsViewController.class.getName());
    }

    private boolean isProjectValid(Project project) {
        return !(ControllerUtilities.isNullOrEmpty(project.getName()) ||
                 ControllerUtilities.isNullOrEmpty(project.getCostunit()) ||
                 ControllerUtilities.isNullOrEmpty(project.getDescription()));
    }

}
