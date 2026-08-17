/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import controls.LocalTimeSpinner;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.*;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Sprint;
import model.*;
import org.apache.logging.log4j.*;
import service.*;
import sqlite.*;
import utils.*;

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
    
    private final String newTrackingItemEvent = "NewTrackingItem";
    private final String editTrackingItemEvent = "EditTrackingItem";
    private final String deleteTrackingItemEvent = "DeleteTrackingItem";
    
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
    private TableView<WorkItem> trackingItemTableView;
    @FXML
    private TableColumn<WorkItem, String> trackingItemShortcutTableColumn;
    @FXML
    private TableColumn<WorkItem, String> trackingItemNameTableColumn;
    @FXML
    private TableColumn<WorkItem, LocalTime> trackingItemStartTimeTableColumn;
    @FXML
    private TableColumn<WorkItem, LocalTime> trackingItemEndTimeTableColumn;
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
    private TrackingItem trackingItem;
    private final TrackingItemDAO trackingItemDAO;
    private WorkItem workItem;
    private final WorkItemDAO workItemDao;

    private final ObservableList<WorkItem> workItemData = FXCollections.observableArrayList();
    
    private final WorkRecordDetailsViewController workRecordDetailsViewController;
    private final WorkRecordViewController workRecordViewController;
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

        this.workRecordDetailsViewController = (WorkRecordDetailsViewController)controllerRepository.get(WorkRecordDetailsViewController.class.getName());
        this.workRecordViewController = (WorkRecordViewController)controllerRepository.get(WorkRecordViewController.class.getName());
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
        DatePicker datePicker = (DatePicker)event.getSource();
        LocalDate date = datePicker.getValue();
        User selectedUser = workRecordViewController.getSelectedUser();
        List<Workrecord> workRecords = workRecordDetailsViewController.getWorkrecordDao().selectAll(selectedUser, date); 
        if(workRecords != null && !workRecords.isEmpty()) {
            actualSelectedWorkrecord = workRecords.getFirst();
        } else {
            actualSelectedWorkrecord = null;
        }
        initWorkItemData();
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
        
        trackingItemTableView.setItems(workItemData);

        initTableColumn();
        initStartTimeTracking();
        initEndTimeTracking();
        initListener();
        initDatePickerBySelectedWorkrecord();
        initTrackingItemChoiceBox();
        try {
            initWorkItemData();
        } catch (SQLException ex) {
            log.fatal("No WorkItemData could be loaded!");
        }
    }

    private void initTableColumn() {
        //HOWTO: Cell Value Factory
        //The cell must know which part of WorkItemTrackingData it needs to display.
        trackingItemShortcutTableColumn.setCellValueFactory(cellData -> cellData.getValue().getShortcutProperty());
        trackingItemNameTableColumn.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
        trackingItemStartTimeTableColumn.setCellValueFactory(cellData -> cellData.getValue().getStartTimeProperty());
        trackingItemEndTimeTableColumn.setCellValueFactory(cellData -> cellData.getValue().getEndTimeProperty());
    }

    private void initStartTimeTracking() {
        trackingItemStartTimeButton.setGraphic(new ImageView(timeNowIcon));
        trackingItemStartTimeTimeSpinner = new LocalTimeSpinner();
        trackingItemDetailsGridPane.add(trackingItemStartTimeTimeSpinner, 2, 2);
    }

    private void initEndTimeTracking() {
        trackingItemEndTimeButton.setGraphic(new ImageView(timeNowIcon));
        trackingItemEndTimeTimeSpinner = new LocalTimeSpinner();
        trackingItemDetailsGridPane.add(trackingItemEndTimeTimeSpinner, 2, 3);
    }

    private void initListener() {
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

    private void initDatePickerBySelectedWorkrecord() {
        actualSelectedWorkrecord = workRecordDetailsViewController.getSelectedWorkrecord();
        if(actualSelectedWorkrecord != null) {
            selectedDateDatePicker.setValue(actualSelectedWorkrecord.getDate());
        } else {
            selectedDateDatePicker.setValue(LocalDate.now());
        }
        trySetSprintNumberLabel(selectedDateDatePicker.getValue());
    }

    private void initTrackingItemChoiceBox() {
        try {
            trackingItemChoiceBox.getItems().clear();
            trackingItemChoiceBox.getItems().addAll(trackingItemDAO.selectAll());
            trackingItemChoiceBox.getSelectionModel().select(0);
            trackingItem = trackingItemChoiceBox.getValue();
        } catch (SQLException ex) {
            log.fatal("No TrackingItems could be loaded!");
        }
    }

    private void initWorkItemData() throws SQLException {
        workItemData.clear();
        if(actualSelectedWorkrecord != null) {
            List<WorkItem> workItemsOfActualSelectedWorkrecord = workItemDao.selectAll(actualSelectedWorkrecord.getId());
            for(int idx = 0; idx < workItemsOfActualSelectedWorkrecord.size(); idx++) {
                var workItem = workItemsOfActualSelectedWorkrecord.get(idx);
                workItemData.add(workItem);
            }
        }
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
        MainToolBarViewController mainToolBarViewController = (MainToolBarViewController)controllerRepository.get(MainToolBarViewController.class.getName());
        if(mainToolBarViewController != null) {
            mainToolBarViewController.getWorkItemTrackingToolButton().disableProperty().set(false);
        }
    }
    
    @Override
    public void update(String eventType, Object source) {
        switch (eventType) {
            case newTrackingItemEvent, editTrackingItemEvent, deleteTrackingItemEvent -> {
                initTrackingItemChoiceBox();
            }
            case selectedWorkRecordChangedEvent -> {
                Workrecord workrecord = (Workrecord)source;
                if(workrecord != null) {
                    selectedDateDatePicker.setValue(workrecord.getDate());
                }
            }
        }
    }
    
    public EventManager getEventManager() {
        return eventManager;
    }
    
}
