/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import controls.LocalTimeSpinner;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Sprint;
import model.TrackingItem;
import model.WorkItemTrackingData;
import model.Workrecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.LanguageService;
import service.UndoService;
import sqlite.SprintDAO;
import sqlite.TrackingItemDAO;
import sqlite.WorkItemDAO;
import utils.EventManager;
import utils.IEventListener;

/**
 *
 * @author adrest18
 */
class WorkItemTrackingToolViewController implements Initializable, IViewController, IEventListener {

    private static final String COLOR_LIGHT_RED = "Red";

    private final String timeNowIcon = "icons/timeNow.png";
    
    private final String newResourceKey = "New";
    private final String editResourceKey = "Edit";
    private final String deleteResourceKey = "Delete";
    
    private final String trackingItemDateResourceKey = "Date";
    private final String trackingItemSprintResourceKey = "Sprint";
    private final String sprintNotFoundResourceKey = "SprintNotFound";
    private final String trackingItemShortcutResourceKey = "TrackingItemShortcut";
    private final String trackingItemNameResourceKey = "TrackingItemName";
    private final String trackingItemStartTimeResourceKey  ="TrackingItemStartTime";
    private final String trackingItemEndTimeResourceKey  ="TrackingItemEndTime";
    private final String trackingItemDetailsHeaderResourceKey = "TrackingItemDetailsHeader";
    private final String trackingItemItemResourceKey = "TrackingItem";
    private final String trackingItemDescriptionResourceKey = "TrackingItemDescription";
    private final String startTimeButtonToolTipResourceKey = "StartTimeButtonToolTip";
    private final String endTimeButtonToolTipResourceKey = "EndTimeButtonToolTip";
    
    private final String workItemTrackingDateChangedEvent = "WorkItemTrackingDateChanged";
    private final String selectedWorkRecordChangedEvent = "SelectedWorkRecordChanged";
    
    @FXML
    private ToolBar trackingItemToolBar;
    @FXML
    private Label selectedDateLabel;
    @FXML
    private DatePicker selectedDateDatePicker;
    @FXML
    private Label sprintLabel;
    @FXML
    private Label sprintNumberLabel;
    @FXML
    private TableView<WorkItemTrackingData> trackingItemTableView;
    @FXML
    private TableColumn<WorkItemTrackingData, String> trackingItemShortcutTableColumn;
    @FXML
    private TableColumn<WorkItemTrackingData, String> trackingItemNameTableColumn;
    @FXML
    private TableColumn<WorkItemTrackingData, LocalTime> trackingItemStartTimeTableColumn;
    @FXML
    private TableColumn<WorkItemTrackingData, LocalTime> trackingItemEndTimeTableColumn;
    @FXML
    private GridPane trackingItemDetailsGridPane;
    @FXML
    private Label trackingItemDetailsHeaderLabel;
    @FXML
    private Label trackingItemNameLabel;
    @FXML
    private Label trackingItemStartTimeLabel;
    @FXML
    private Button trackingItemStartTimeButton;
    @FXML
    private Label trackingItemEndTimeLabel;
    @FXML
    private Button trackingItemEndTimeButton;
    @FXML
    private Label trackingItemDescriptionLabel;
    @FXML
    private ChoiceBox<TrackingItem> trackingItemChoiceBox;
    @FXML
    private TextArea trackingItemDescriptionValue;
    
    @FXML
    private Button newButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    
    private final Logger log = LogManager.getLogger(WorkItemTrackingToolViewController.class.getName());

    private Stage primaryStage;
    private final ControllerRepository controllerRepository;
    private final LanguageService languageService;
    private final Connection connection;
    private final UndoService undoService;
    private ResourceBundle rb;
    private EventManager eventManager;

    private LocalTimeSpinner trackingItemStartTimeTimeSpinner;
    private LocalTimeSpinner trackingItemEndTimeTimeSpinner;

    private Sprint sprint;
    private final SprintDAO sprintDAO;
    private final TrackingItemDAO trackingItemDAO;
    private final WorkItemDAO workItemDao;

    private ObservableList<WorkItemTrackingData> workItemTrackingData;
    
    private final WorkRecordDetailsViewController workRecordDetailsViewController;
    private Workrecord actualSelectedWorkrecord;
    
    public WorkItemTrackingToolViewController(ControllerRepository controllerRepository, LanguageService languageService, Connection connection, UndoService undoService) throws SQLException {
        if(controllerRepository == null) throw new NullPointerException("controllerRepository");
        if(languageService == null) throw new NullPointerException("languageService");
        if(connection == null) throw new NullPointerException("connection");
        if(undoService == null) throw new NullPointerException("undoService");

        this.controllerRepository = controllerRepository;
        this.languageService = languageService;
        this.connection = connection;
        this.undoService = undoService;
        this.sprintDAO = new SprintDAO(connection);
        this.trackingItemDAO = new TrackingItemDAO(connection);
        this.workItemDao = new WorkItemDAO(connection);
        this.workItemTrackingData = FXCollections.observableArrayList();  
        this.workRecordDetailsViewController = (WorkRecordDetailsViewController)controllerRepository.get(WorkRecordDetailsViewController.class.getName());
        this.eventManager = new EventManager();
    }
    
    @FXML
    private void newAction(ActionEvent event) throws SQLException, IOException {

    }
    
    @FXML
    private void editAction(ActionEvent event) throws SQLException, IOException {

    }

    @FXML
    private void deleteAction(ActionEvent event) throws SQLException, IOException {

    }
    
    @FXML
    private void handleOnSelectedDateChangedAction(ActionEvent event) throws SQLException, IOException {

    }
    
    @FXML
    private void handleOnSetStartTimeButtonClickAction(ActionEvent event) {

    }

    @FXML
    private void handleOnSetEndTimeButtonClickAction(ActionEvent event) throws SQLException {

    }
    
    @Override
    @SuppressWarnings("unchecked")
    public void initialize(URL location, ResourceBundle rb) {
        this.rb = rb;
            
        trackingItemStartTimeButton.setGraphic(new ImageView(timeNowIcon));
        trackingItemEndTimeButton.setGraphic(new ImageView(timeNowIcon));
        
        trackingItemStartTimeTimeSpinner = new LocalTimeSpinner();
        trackingItemEndTimeTimeSpinner = new LocalTimeSpinner();

        trackingItemDetailsGridPane.add(trackingItemStartTimeTimeSpinner, 2, 2);
        trackingItemDetailsGridPane.add(trackingItemEndTimeTimeSpinner, 2, 3);
        
        selectedDateDatePicker.valueProperty().addListener((var observable, var oldValue, var newValue) -> {
            trySetSprintNumberLabel(newValue);
    
            //Fire event that selected date for tracking workitems has changed
            eventManager.notifyListenerOfEvent(workItemTrackingDateChangedEvent, newValue);
        });
        trackingItemStartTimeTimeSpinner.valueProperty().addListener((ObservableValue<? extends LocalTime> observable, LocalTime oldValue, LocalTime newValue) -> {

        });
        trackingItemEndTimeTimeSpinner.valueProperty().addListener((ObservableValue<? extends LocalTime> observable, LocalTime oldValue, LocalTime newValue) -> {

        });
        trackingItemChoiceBox.valueProperty().addListener((ObservableValue<? extends TrackingItem> obs, TrackingItem oldValue, TrackingItem newValue) ->  {

        });
        
        actualSelectedWorkrecord = workRecordDetailsViewController.getSelectedWorkrecord();
        if(actualSelectedWorkrecord != null) {
            selectedDateDatePicker.setValue(actualSelectedWorkrecord.getDate());
        } else {
            selectedDateDatePicker.setValue(LocalDate.now());
        }
        trySetSprintNumberLabel(selectedDateDatePicker.getValue());

        try {
            trackingItemChoiceBox.getItems().addAll(trackingItemDAO.selectAll());
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(WorkItemTrackingToolViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        trackingItemChoiceBox.getSelectionModel().select(0);
            
        workItemTrackingData.add(new WorkItemTrackingData("FD", "Feature Development", LocalTime.of(1, 0), LocalTime.of(2, 0)));
        trackingItemTableView.setItems(workItemTrackingData);
        
    }

    @Override
    public void updateGuiItems() {
        selectedDateLabel.setText(rb.getString(trackingItemDateResourceKey));
        sprintLabel.setText(rb.getString(trackingItemSprintResourceKey));
        sprintNumberLabel.setText(rb.getString(sprintNotFoundResourceKey));
        trackingItemShortcutTableColumn.setText(rb.getString(trackingItemShortcutResourceKey));
        trackingItemNameTableColumn.setText(rb.getString(trackingItemNameResourceKey));
        trackingItemStartTimeTableColumn.setText(rb.getString(trackingItemStartTimeResourceKey));
        trackingItemEndTimeTableColumn.setText(rb.getString(trackingItemEndTimeResourceKey));
        trackingItemDetailsHeaderLabel.setText(rb.getString(trackingItemDetailsHeaderResourceKey));        
        trackingItemNameLabel.setText(rb.getString(trackingItemItemResourceKey));
        trackingItemStartTimeLabel.setText(rb.getString(trackingItemStartTimeResourceKey));
        trackingItemStartTimeButton.setTooltip(new Tooltip(rb.getString(startTimeButtonToolTipResourceKey)));
        trackingItemEndTimeLabel.setText(rb.getString(trackingItemEndTimeResourceKey));        
        trackingItemEndTimeButton.setTooltip(new Tooltip(rb.getString(endTimeButtonToolTipResourceKey)));
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
    
    @Override
    public void update(String eventType, Object source) {
        switch (eventType) {
            case selectedWorkRecordChangedEvent -> {
                Workrecord workrecord = (Workrecord)source;
                selectedDateDatePicker.setValue(workrecord.getDate());
            }
        }
    }
    
    public EventManager getEventManager() {
        return eventManager;
    }
    
    private void trySetSprintNumberLabel(LocalDate date) {
            try {
                sprint = sprintDAO.selectSprintForDate(date);
                if(sprint == null) {
                    sprintNumberLabel.setText(rb.getString(sprintNotFoundResourceKey));
                    sprintNumberLabel.setStyle("-fx-text-fill: " + COLOR_LIGHT_RED + ";");
                    log.info("Sprint for date " + date + " not found, please update sprint referencedata!");                
                } else {
                    sprintNumberLabel.setText(sprint.getId().toString());
                    sprintNumberLabel.setStyle("");
                }
            } catch (SQLException ex) {
                log.info("Sprint for date " + date + " not found, please update sprint referencedata!");                
            }
    }

}
