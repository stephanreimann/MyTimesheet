/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import commands.trackingitem.*;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.TrackingItem;
import org.apache.logging.log4j.*;
import service.*;
import sqlite.TrackingItemDAO;
import utils.*;

/**
 *
 * @author adrest18
 */
public class TrackingItemViewController implements Initializable, IViewController {

    public enum DataAction { NEW, EDIT, DELETE };

    private final String newResourceKey = "New";
    private final String editResourceKey = "Edit";
    private final String deleteResourceKey = "Delete";
    
    private final String trackingItemDetailsLabelResourceKey = "TrackingItemDetailsLabel";
    
    private final String trackingItemIdResourceKey = "TrackingItemId";
    private final String trackingItemNameResourceKey = "TrackingItemName";
    private final String trackingItemShortcutResourceKey = "TrackingItemShortcut";
    private final String trackingItemDescriptionResourceKey = "TrackingItemDescription";
    
    private final String noTrackingItemSelectionAlertTitle = "NoSelectionAlertTitle";
    private final String noTrackingItemSelectionAlertHeader = "NoTrackingItemSelectionAlertHeader";
    private final String noTrackingItemSelectionAlertContent = "NoTrackingItemSelectionAlertContent";
    
    private final String trackingItemDetailsViewDialogIcon = "icons/app-maid.png";
    private final String trackingItemDetailsViewDialogTitleResourceKey = "TrackingItemDetailsViewTitle";
    private final String trackingItemDetailsViewResource = "/view/TrackingItemDetailsView.fxml";
    
    private final String newTrackingItemEvent = "NewTrackingItem";
    private final String editTrackingItemEvent = "EditTrackingItem";
    private final String deleteTrackingItemEvent = "DeleteTrackingItem";
    
    @FXML
    private TableView<TrackingItem> trackingItemTableView;
    @FXML
    private TableColumn<TrackingItem, String> trackingItemIdTableColumn;
    @FXML
    private TableColumn<TrackingItem, String> trackingItemNameTableColumn;
    @FXML
    private Label trackingItemDetailsLabel;
    @FXML
    private Label trackingItemIdLabel;
    @FXML
    private Label trackingItemNameLabel;
    @FXML
    private Label trackingItemShortcutLabel;
    @FXML
    private Label trackingItemDescriptionLabel;
    @FXML
    private Label trackingItemIdLabelValue;
    @FXML
    private Label trackingItemNameLabelValue;
    @FXML
    private Label trackingItemShortcutLabelValue;
    @FXML
    private Label trackingItemDescriptionLabelValue;
    
    @FXML
    private Button newButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;

    private final Logger log = LogManager.getLogger(TrackingItemViewController.class.getName());
    
    private Stage primaryStage;
    private final ControllerRepository controllerRepository;
    private final LanguageService languageService;
    private final Connection connection;
    private final UndoService undoService;
    private final PropertiesService propertiesService;
    private ResourceBundle rb;
    private EventManager eventManager;
    
    private TrackingItemDAO trackingItemDao;
    private ObservableList<TrackingItem> trackingItemData;
    
    private Stage trackingItemDetailsViewDialog;
    
    public TrackingItemViewController(ControllerRepository controllerRepository, LanguageService languageService, Connection connection, UndoService undoService, PropertiesService propertiesService) throws SQLException {
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
        this.trackingItemDao = new TrackingItemDAO(connection);
        this.trackingItemData = FXCollections.observableArrayList(this.trackingItemDao.selectAll());        
        this.eventManager = new EventManager();
        this.eventManager.registerEventType(newTrackingItemEvent);
        this.eventManager.registerEventType(editTrackingItemEvent);
        this.eventManager.registerEventType(deleteTrackingItemEvent);
    }

    @FXML
    private void newTrackingItemAction(ActionEvent event) throws SQLException, IOException {
        TrackingItem newTrackingItem = new TrackingItem(trackingItemDao.getNextId());
        openTrackingItemDetailsDialog(newTrackingItem, DataAction.NEW);
        if(isTrackingItemValid(newTrackingItem)) {
            NewTrackingItemCommand cmd = new NewTrackingItemCommand(controllerRepository,eventManager, trackingItemTableView, newTrackingItem, trackingItemDao);
            undoService.execute(cmd);
            trackingItemTableView.sort();
        }
    }
    
    @FXML
    private void editTrackingItemAction(ActionEvent event) throws SQLException, IOException {
        TrackingItem selectedTrackingItem = trackingItemTableView.getSelectionModel().getSelectedItem();
        if(selectedTrackingItem != null) {
            TrackingItem originalTrackingItem = new TrackingItem(selectedTrackingItem);
            openTrackingItemDetailsDialog(selectedTrackingItem, DataAction.EDIT);
            showTrackingItemDetails(selectedTrackingItem);
            if(!originalTrackingItem.equals(selectedTrackingItem)) {
                EditTrackingItemCommand cmd = new EditTrackingItemCommand(controllerRepository, eventManager, trackingItemTableView, originalTrackingItem, selectedTrackingItem, trackingItemDao);
                undoService.execute(cmd);
                trackingItemTableView.sort();
            }
        } else {
            ControllerUtilities.showNoItemSelectedAlert(primaryStage, rb, noTrackingItemSelectionAlertTitle, noTrackingItemSelectionAlertHeader, noTrackingItemSelectionAlertContent);
        }
    }

    @FXML
    private void deleteTrackingItemAction(ActionEvent event) throws SQLException, IOException {
        TrackingItem selectedTrackingItem = trackingItemTableView.getSelectionModel().getSelectedItem();
        if(selectedTrackingItem != null) {
            DeleteTrackingItemCommand cmd = new DeleteTrackingItemCommand(controllerRepository, eventManager, trackingItemTableView, selectedTrackingItem, trackingItemDao);
            undoService.execute(cmd);
            trackingItemTableView.sort();
        } else {
            ControllerUtilities.showNoItemSelectedAlert(primaryStage, rb, noTrackingItemSelectionAlertTitle, noTrackingItemSelectionAlertHeader, noTrackingItemSelectionAlertContent);
        }
    }
    
    @Override
    public void initialize(URL location, ResourceBundle rb) {
        this.rb = rb;
        trackingItemTableView.setItems(trackingItemData);

        //HOWTO: Cell Value Factory
        //The cell must know which part of TrackingItem it needs to display. For all cells in the trackingItemDateTableColumn this will be the TrackingItem date value.
        trackingItemIdTableColumn.setCellValueFactory(cellData -> cellData.getValue().getIdProperty().asString());
        trackingItemIdTableColumn.setSortable(true);
        trackingItemIdTableColumn.setSortType(TableColumn.SortType.ASCENDING);
        trackingItemTableView.sort();
        
        trackingItemNameTableColumn.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
        
        Optional<TrackingItem> firstTrackingItem = trackingItemData.stream().findFirst();
        if(firstTrackingItem != null) {
            showTrackingItemDetails(firstTrackingItem.get());
            trackingItemTableView.getSelectionModel().select(0);
        } else {
            showTrackingItemDetails(null);        
        }

        trackingItemTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showTrackingItemDetails(newValue));
                
        languageService.updateGuiItems();                
    }

    @Override
    public void updateGuiItems() {
        trackingItemIdLabel.setText(rb.getString(trackingItemIdResourceKey));
        trackingItemNameLabel.setText(rb.getString(trackingItemNameResourceKey));
        trackingItemShortcutLabel.setText(rb.getString(trackingItemShortcutResourceKey));
        trackingItemDescriptionLabel.setText(rb.getString(trackingItemDescriptionResourceKey));        
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

    private void showTrackingItemDetails(TrackingItem trackingItem) {
        if(trackingItem != null) {
            trackingItemIdLabelValue.setText(trackingItem.getId().toString());
            trackingItemNameLabelValue.setText(trackingItem.getName());
            trackingItemShortcutLabelValue.setText(trackingItem.getShortcut());
            trackingItemDescriptionLabelValue.setText(trackingItem.getDescription());
        } else {
            trackingItemIdLabelValue.setText("");
            trackingItemNameLabelValue.setText("");
            trackingItemShortcutLabelValue.setText("");
            trackingItemDescriptionLabelValue.setText("");
        }
    }
    
    private void openTrackingItemDetailsDialog(TrackingItem trackingItem, DataAction dataAction) throws IOException {
        TrackingItemDetailsViewController trackingItemDetailsViewController = (TrackingItemDetailsViewController)controllerRepository.get(TrackingItemDetailsViewController.class.getName());
        if(trackingItemDetailsViewController == null) {
            trackingItemDetailsViewController = new TrackingItemDetailsViewController(languageService, connection, undoService, trackingItemData);
            controllerRepository.put(TrackingItemDetailsViewController.class.getName(), trackingItemDetailsViewController);
        }

        DialogFactory dialogFactory = new DialogFactory(
            primaryStage, 
            trackingItemDetailsViewDialogTitleResourceKey, 
            trackingItemDetailsViewDialogIcon, 
            trackingItemDetailsViewResource, 
            rb, 
            trackingItemDetailsViewController);
        trackingItemDetailsViewDialog = dialogFactory.create();
        trackingItemDetailsViewDialog.setWidth(400);
        trackingItemDetailsViewDialog.setHeight(350);
                
        trackingItemDetailsViewController.setAction(dataAction);
        trackingItemDetailsViewController.showTrackingItemDetails(trackingItem);
        
        ControllerUtilities.CenterOnDialog(primaryStage, trackingItemDetailsViewDialog);

        trackingItemDetailsViewDialog.showAndWait();        
    
        controllerRepository.remove(TrackingItemDetailsViewController.class.getName());
    }

    private boolean isTrackingItemValid(TrackingItem trackingItem) {
        return !ControllerUtilities.isNullOrEmpty(trackingItem.getId().toString()) &&
               !ControllerUtilities.isNullOrEmpty(trackingItem.getName()) &&
               !ControllerUtilities.isNullOrEmpty(trackingItem.getShortcut()) &&
               !ControllerUtilities.isNullOrEmpty(trackingItem.getDescription());
    }
    
}
