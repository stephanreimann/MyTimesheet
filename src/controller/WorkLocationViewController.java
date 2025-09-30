/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import commands.worklocation.*;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.stage.*;
import model.Worklocation;
import org.apache.logging.log4j.*;
import service.*;
import sqlite.WorklocationDAO;
import utils.*;

/**
 *
 * @author stephan
 */
public class WorkLocationViewController implements Initializable, IViewController {

    public enum DataAction { NEW, EDIT, DELETE };

    private final String worklocationNameResourceKey = "WorklocationName";
    private final String worklocationDescriptionResourceKey = "WorklocationDescription";
    private final String worklocationDetailsLabelResourceKey = "WorklocationDetailsLabel";
    private final String newResourceKey = "New";
    private final String editResourceKey = "Edit";
    private final String deleteResourceKey = "Delete";
    private final String noWorklocationSelectionAlertTitle = "NoSelectionAlertTitle";
    private final String noWorklocationSelectionAlertHeader = "NoWorklocationSelectionAlertHeader";
    private final String noWorklocationSelectionAlertContent = "NoWorklocationSelectionAlertContent";
    private final String newWorkLocationEvent = "NewWorkLocation";
    private final String editWorkLocationEvent = "EditWorkLocation";
    private final String deleteWorkLocationEvent = "DeleteWorkLocation";
    
    private final Logger log = LogManager.getLogger(WorkLocationViewController.class.getName());
    
    private Stage primaryStage;
    private final ControllerRepository controllerRepository;
    private final LanguageService languageService;
    private final Connection connection;
    private final UndoService undoService;
    private final PropertiesService propertiesService;
    private ResourceBundle rb;
    private final WorklocationDAO worklocationDao;
    private ObservableList<Worklocation> worklocationData;
    private Stage worklocationDetailsViewDialog;
    private EventManager eventManager;
    
    private final String worklocationDetailsViewDialogIcon = "icons/app-maid.png";
    private final String worklocationDetailsViewDialogTitleResourceKey = "WorklocationDetailsViewTitle";
    private final String worklocationDetailsViewResource = "/view/WorkLocationDetailsView.fxml";
    
    @FXML
    private TableView<Worklocation> worklocationTableView;
    @FXML
    private TableColumn<Worklocation, String> worklocationNameTableColumn;
    @FXML
    private TableColumn<Worklocation, String> worklocationDescriptionTableColumn;
    @FXML
    private Label worklocationDetailsLabel;
    
    @FXML
    private Label worklocationNameLabel;
    @FXML
    private Label worklocationDescriptionLabel;
    
    @FXML
    private Label worklocationNameLabelValue;
    @FXML
    private TextArea worklocationDescriptionTextArea;
    
    @FXML
    private Button newButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    
    public WorkLocationViewController(ControllerRepository controllerRepository, LanguageService languageService, Connection connection, UndoService undoService, PropertiesService propertiesService) throws SQLException {
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
        this.worklocationDao = new WorklocationDAO(connection);
        this.worklocationData = FXCollections.observableArrayList(this.worklocationDao.selectAll());
        this.eventManager = new EventManager();
        this.eventManager.registerEventType(newWorkLocationEvent);
        this.eventManager.registerEventType(editWorkLocationEvent);
        this.eventManager.registerEventType(deleteWorkLocationEvent);    
    }

    @FXML
    private void newWorklocationAction(ActionEvent event) throws SQLException, IOException {
        Worklocation newWorklocation = new Worklocation(worklocationDao.getNextId());
        openWorklocationDetailsDialog(newWorklocation, DataAction.NEW);
        if(isWorklocationValid(newWorklocation)) {
            NewWorklocationCommand cmd = new NewWorklocationCommand(controllerRepository, eventManager, worklocationTableView, newWorklocation, worklocationDao);
            undoService.execute(cmd);
        }
    }
    
    @FXML
    private void editWorklocationAction(ActionEvent event) throws SQLException, IOException {
        Worklocation selectedWorklocation = worklocationTableView.getSelectionModel().getSelectedItem();
        if(selectedWorklocation != null) {
            Worklocation originalWorklocation = new Worklocation(selectedWorklocation);
            openWorklocationDetailsDialog(selectedWorklocation, DataAction.EDIT);
            showWorklocationDetails(selectedWorklocation);
            if(!originalWorklocation.equals(selectedWorklocation)) {
                EditWorklocationCommand cmd = new EditWorklocationCommand(controllerRepository, eventManager, worklocationTableView, originalWorklocation, selectedWorklocation, worklocationDao);
                undoService.execute(cmd);
            }
        } else {
            ControllerUtilities.showNoItemSelectedAlert(primaryStage, rb, noWorklocationSelectionAlertTitle, noWorklocationSelectionAlertHeader, noWorklocationSelectionAlertContent);
        }
    }

    @FXML
    private void deleteWorklocationAction(ActionEvent event) throws SQLException {
        Worklocation selectedWorklocation = worklocationTableView.getSelectionModel().getSelectedItem();
        if(selectedWorklocation != null) {
            DeleteWorklocationCommand cmd = new DeleteWorklocationCommand(controllerRepository, eventManager, worklocationTableView, selectedWorklocation, worklocationDao);
            undoService.execute(cmd);
        } else {
            ControllerUtilities.showNoItemSelectedAlert(primaryStage, rb, noWorklocationSelectionAlertTitle, noWorklocationSelectionAlertHeader, noWorklocationSelectionAlertContent);
        }
    }
    
    @Override
    public void initialize(URL location, ResourceBundle rb) {
        this.rb = rb;
        
        worklocationTableView.setItems(worklocationData);

        worklocationNameTableColumn.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
        worklocationNameTableColumn.prefWidthProperty().bind(worklocationTableView.widthProperty().multiply(0.3));

        worklocationDescriptionTableColumn.setCellValueFactory(cellData -> cellData.getValue().getDescriptionProperty());
        worklocationDescriptionTableColumn.prefWidthProperty().bind(worklocationTableView.widthProperty().multiply(0.7));

        showWorklocationDetails(null);

        worklocationTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showWorklocationDetails(newValue));
                
        languageService.updateGuiItems();
    }

    @Override
    public void updateGuiItems() {
        worklocationNameTableColumn.setText(rb.getString(worklocationNameResourceKey));
        worklocationDescriptionTableColumn.setText(rb.getString(worklocationDescriptionResourceKey));
        worklocationDetailsLabel.setText(rb.getString(worklocationDetailsLabelResourceKey));
        worklocationNameLabel.setText(rb.getString(worklocationNameResourceKey));
        worklocationDescriptionLabel.setText(rb.getString(worklocationDescriptionResourceKey));
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
    
    private void showWorklocationDetails(Worklocation worklocation) {
        if(worklocation != null) {
            worklocationNameLabelValue.setText(worklocation.getName());
            worklocationDescriptionTextArea.setText(worklocation.getDescription());
        } else {
            worklocationNameLabelValue.setText("");
            worklocationDescriptionTextArea.setText("");
        }
    }
    
    private void openWorklocationDetailsDialog(Worklocation worklocation, DataAction dataAction) throws SQLException, IOException {
        WorkLocationDetailsViewController worklocationDetailsViewController = new WorkLocationDetailsViewController(languageService, connection, undoService, worklocationData);
        controllerRepository.put(WorkLocationDetailsViewController.class.getName(), worklocationDetailsViewController);

        DialogFactory dialogFactory = new DialogFactory(
            primaryStage, 
            worklocationDetailsViewDialogTitleResourceKey, 
            worklocationDetailsViewDialogIcon, 
            worklocationDetailsViewResource, 
            rb, 
            worklocationDetailsViewController);
        worklocationDetailsViewDialog = dialogFactory.create();
        worklocationDetailsViewDialog.setWidth(400);
        worklocationDetailsViewDialog.setHeight(350);
        
        worklocationDetailsViewController.setAction(dataAction);
        worklocationDetailsViewController.showWorklocationDetails(worklocation);
        
        ControllerUtilities.CenterOnDialog(primaryStage, worklocationDetailsViewDialog);

        worklocationDetailsViewDialog.showAndWait();        
    
        controllerRepository.remove(WorkLocationDetailsViewController.class.getName());
    }

    public boolean isWorklocationValid(Worklocation worklocation) {
        return !(ControllerUtilities.isNullOrEmpty(worklocation.getName()) ||
                ControllerUtilities.isNullOrEmpty(worklocation.getDescription()));
    }
    
}
