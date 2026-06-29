/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import commands.sprint.*;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Sprint;
import org.apache.logging.log4j.*;
import service.*;
import sqlite.SprintDAO;
import utils.ControllerUtilities;
import utils.EventManager;

/**
 *
 * @author adrest18
 */
public class SprintViewController implements Initializable, IViewController {

    public enum DataAction { NEW, EDIT, DELETE };

    private final String newResourceKey = "New";
    private final String editResourceKey = "Edit";
    private final String deleteResourceKey = "Delete";
    
    private final String sprintDetailsLabelResourceKey = "SprintDetailsLabel";
    
    private final String sprintIdResourceKey = "SprintId";
    private final String startDateResourceKey = "StartDate";
    private final String endDateResourceKey = "EndDate";
    private final String numberOfSprintDaysResourceKey = "NumberOfSprintDays";
    
    private final String noSprintSelectionAlertTitle = "NoSelectionAlertTitle";
    private final String noSprintSelectionAlertHeader = "NoWorklocationSelectionAlertHeader";
    private final String noSprintSelectionAlertContent = "NoWorklocationSelectionAlertContent";
    
    private final String sprintDetailsViewDialogIcon = "icons/app-maid.png";
    private final String sprintDetailsViewDialogTitleResourceKey = "SprintDetailsViewTitle";
    private final String sprintDetailsViewResource = "/view/SprintDetailsView.fxml";
    
    private final String newSprintEvent = "NewSprint";
    private final String editSprintEvent = "EditSprint";
    private final String deleteSprintEvent = "DeleteSprint";
    
    @FXML
    private TableView<Sprint> sprintTableView;
    @FXML
    private TableColumn<Sprint, String> sprintIdTableColumn;
    @FXML
    private Label sprintIdLabel;
    @FXML
    private Label startDateLabel;
    @FXML
    private Label endDateLabel;
    @FXML
    private Label numberOfSprintDaysLabel;
    @FXML
    private Label sprintIdLabelValue;
    @FXML
    private Label startDateLabelValue;
    @FXML
    private Label endDateLabelValue;
    @FXML
    private Label numberOfSprintDaysLabelValue;
            
    @FXML
    private Button newButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    
    private final Logger log = LogManager.getLogger(SprintViewController.class.getName());
    
    private Stage primaryStage;
    private final ControllerRepository controllerRepository;
    private final LanguageService languageService;
    private final Connection connection;
    private final UndoService undoService;
    private final PropertiesService propertiesService;
    private ResourceBundle rb;
    private EventManager eventManager;
    
    private SprintDAO sprintDao;
    private ObservableList<Sprint> sprintData;
    
    private Stage sprintDetailsViewDialog;
    
    public SprintViewController(ControllerRepository controllerRepository, LanguageService languageService, Connection connection, UndoService undoService, PropertiesService propertiesService) throws SQLException {
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
        this.sprintDao = new SprintDAO(connection);
        this.sprintData = FXCollections.observableArrayList(this.sprintDao.selectAll());        
        this.eventManager = new EventManager();
        this.eventManager.registerEventType(newSprintEvent);
        this.eventManager.registerEventType(editSprintEvent);
        this.eventManager.registerEventType(deleteSprintEvent);
    }

    @FXML
    private void newSprintAction(ActionEvent event) throws SQLException, IOException {
        Sprint newSprint = new Sprint(sprintDao.getNextId());
        openSprintDetailsDialog(newSprint, DataAction.NEW);
        if(isSprintValid(newSprint)) {
            NewSprintCommand cmd = new NewSprintCommand(controllerRepository,eventManager, sprintTableView, newSprint, sprintDao);
            undoService.execute(cmd);
        }
    }
    
    @FXML
    private void editSprintAction(ActionEvent event) throws SQLException, IOException {
        Sprint selectedSprint = sprintTableView.getSelectionModel().getSelectedItem();
        if(selectedSprint != null) {
            Sprint originalSprint = new Sprint(selectedSprint);
            openSprintDetailsDialog(selectedSprint, DataAction.EDIT);
            showSprintDetails(selectedSprint);
            if(!originalSprint.equals(selectedSprint)) {
                EditSprintCommand cmd = new EditSprintCommand(controllerRepository, eventManager, sprintTableView, originalSprint, selectedSprint, sprintDao);
                undoService.execute(cmd);
            }
        } else {
            ControllerUtilities.showNoItemSelectedAlert(primaryStage, rb, noSprintSelectionAlertTitle, noSprintSelectionAlertHeader, noSprintSelectionAlertContent);
        }
    }

    @FXML
    private void deleteSprintAction(ActionEvent event) throws SQLException, IOException {
        Sprint selectedSprint = sprintTableView.getSelectionModel().getSelectedItem();
        if(selectedSprint != null) {
            DeleteSprintCommand cmd = new DeleteSprintCommand(controllerRepository, eventManager, sprintTableView, selectedSprint, sprintDao);
            undoService.execute(cmd);
        } else {
            ControllerUtilities.showNoItemSelectedAlert(primaryStage, rb, noSprintSelectionAlertTitle, noSprintSelectionAlertHeader, noSprintSelectionAlertContent);
        }
    }

    
    @Override
    public void initialize(URL location, ResourceBundle rb) {
        this.rb = rb;
        sprintTableView.setItems(sprintData);

        //HOWTO: Cell Value Factory
        //The cell must know which part of Sprint it needs to display. For all cells in the sprintDateTableColumn this will be the Sprint date value.
        sprintIdTableColumn.setCellValueFactory(cellData -> cellData.getValue().getIdProperty().asString());
        sprintIdTableColumn.setSortable(false);
        
        showSprintDetails(null);

        sprintTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showSprintDetails(newValue));
                
        languageService.updateGuiItems();        
        
    }

    @Override
    public void updateGuiItems() {

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
    
    private void openSprintDetailsDialog(Sprint newSprint, DataAction dataAction) {

    }

    private boolean isSprintValid(Sprint newSprint) {
        
        return false;
    }

    private void showSprintDetails(Sprint selectedSprint) {

    }
    
}
