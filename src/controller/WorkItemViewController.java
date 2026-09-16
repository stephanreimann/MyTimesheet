/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */ 
package controller;

import command.workitem.*;
import controls.*;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.*;
import java.util.*;
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
 * Controller for managing WorkItems per WorkRecord/Sprint.
 */
public class WorkItemViewController implements Initializable, IViewController, IEventListener {

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
    private final String trackingItemStartTimeResourceKey = "TrackingItemStartTime";
    private final String trackingItemEndTimeResourceKey = "TrackingItemEndTime";
    private final String trackingItemTimeResourceKey = "TrackingItemTime";
    private final String trackingItemDetailsHeaderResourceKey = "TrackingItemDetailsHeader";
    private final String trackingItemItemResourceKey = "TrackingItem";
    private final String trackingItemDescriptionResourceKey = "TrackingItemDescription";
    private final String startTimeButtonToolTipResourceKey = "StartTimeButtonToolTip";
    private final String endTimeButtonToolTipResourceKey = "EndTimeButtonToolTip";
    
    private final String workItemDateChangedEvent = "WorkItemDateChanged";
    private final String selectedWorkRecordChangedEvent = "SelectedWorkRecordChanged";
    
    private final String newTrackingItemEvent = "NewTrackingItem";
    private final String editTrackingItemEvent = "EditTrackingItem";
    private final String deleteTrackingItemEvent = "DeleteTrackingItem";
    
    private final String noTrackingItemSelectionAlertTitle = "NoSelectionAlertTitle";
    private final String noTrackingItemSelectionAlertHeader = "NoWorkItemSelectionAlertHeader";
    private final String noTrackingItemSelectionAlertContent = "NoWorkItemSelectionAlertContent";
    
    // <editor-fold defaultstate="collapsed" desc="FXML Members">
    @SuppressWarnings("unused")
    @FXML private ToolBar trackingItemToolBar;
    @FXML private Label selectedDateLabel;
    @FXML private DatePicker selectedDateDatePicker;
    @FXML private Label sprintLabel;
    @FXML private Label sprintNumberLabelValue;
    
    @FXML private TableView<WorkItem> trackingItemTableView;
    @FXML private TableColumn<WorkItem, String> trackingItemShortcutTableColumn;
    @FXML private TableColumn<WorkItem, String> trackingItemNameTableColumn;
    @FXML private TableColumn<WorkItem, LocalTime> trackingItemStartTimeTableColumn;
    @FXML private TableColumn<WorkItem, LocalTime> trackingItemEndTimeTableColumn;
    
    @FXML private GridPane trackingItemDetailsGridPane;
    @FXML private Label trackingItemDetailsHeaderLabel;
    @FXML private Label trackingItemNameLabel;
    @FXML private Label trackingItemStartTimeLabel;
    @FXML private Button trackingItemStartTimeButton;
    @FXML private Label trackingItemEndTimeLabel;
    @FXML private Button trackingItemEndTimeButton;
    @FXML private Label trackingItemTimeTableColumn;
    @FXML private Label trackingItemDescriptionLabel;
    @FXML private ChoiceBox<TrackingItem> trackingItemChoiceBox;
    @FXML private TextArea trackingItemDescriptionValue;
    
    @FXML private Button newButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    // </editor-fold>
    
    private final Logger log = LogManager.getLogger(WorkItemViewController.class.getName());

    private Stage primaryStage;
    private final ControllerRepository controllerRepository;
    private final LanguageService languageService;
    @SuppressWarnings("unused")
    private final Connection connection;
    private final UndoService undoService;
    private ResourceBundle rb;
    private final EventManager eventManager;

    private LocalTimeSpinner trackingItemStartTimeTimeSpinner;
    private LocalTimeSpinner trackingItemEndTimeTimeSpinner;
    private DurationSpinner trackingItemTimeDurationSpinner;
    
    private Sprint sprint;
    private final SprintDAO sprintDAO;
    private final TrackingItemDAO trackingItemDAO;
    private final WorkItemDAO workItemDao;

    // WorkItem baseline snapshot for change detection
    private long oldTrackingItemId;
    private LocalTime oldStartTime;
    private LocalTime oldEndTime;
    private String oldDescription = "";
            
    private final ObservableList<WorkItem> workItemData = FXCollections.observableArrayList();
    
    private final WorkRecordDetailsViewController workRecordDetailsViewController;
    private final WorkRecordViewController workRecordViewController;
    private Workrecord selectedWorkrecord;

    // Flag to prevent recursive listener updates
    private boolean isSyncing = false;
    
    public WorkItemViewController(ControllerRepository controllerRepository, LanguageService languageService, Connection connection, UndoService undoService) throws SQLException {
        if (controllerRepository == null) throw new NullPointerException("controllerRepository");
        if (languageService == null) throw new NullPointerException("languageService");
        if (connection == null) throw new NullPointerException("connection");
        if (undoService == null) throw new NullPointerException("undoService");

        this.controllerRepository = controllerRepository;
        this.languageService = languageService;
        this.connection = connection;
        this.undoService = undoService;
        this.sprintDAO = new SprintDAO(connection);
        this.trackingItemDAO = new TrackingItemDAO(connection);
        this.workItemDao = new WorkItemDAO(connection);

        this.workRecordDetailsViewController = (WorkRecordDetailsViewController) controllerRepository.get(WorkRecordDetailsViewController.class.getName());
        this.workRecordViewController = (WorkRecordViewController) controllerRepository.get(WorkRecordViewController.class.getName());
        this.eventManager = new EventManager();
    }
    
    @FXML
    @SuppressWarnings("unused")
    private void newAction(ActionEvent event) throws SQLException, IOException {
        if (!isInputValid(true) || selectedWorkrecord == null || sprint == null) {
            return;
        }

        TrackingItem selectedTracking = trackingItemChoiceBox.getSelectionModel().getSelectedItem();
        long nextId = workItemDao.getNextId();
        
        WorkItem newWorkItem = new WorkItem(nextId);
        newWorkItem.setWorkrecordId(selectedWorkrecord.getId());
        newWorkItem.setSprintId(sprint.getId());
        newWorkItem.setTrackingItemId(selectedTracking.getId());
        newWorkItem.setStartTime(trackingItemStartTimeTimeSpinner.getValue());
        newWorkItem.setEndTime(trackingItemEndTimeTimeSpinner.getValue());
        newWorkItem.setDescription(trackingItemDescriptionValue.getText());
        newWorkItem.setShortcut(selectedTracking.getShortcut());
        newWorkItem.setName(selectedTracking.getName());
        
        NewWorkItemCommand cmd = new NewWorkItemCommand(controllerRepository, eventManager, trackingItemTableView, newWorkItem, workItemDao);
        undoService.execute(cmd);
        sortWorkItems();
    }
    
    @FXML
    @SuppressWarnings("unused")
    private void editAction(ActionEvent event) throws SQLException, IOException {
        WorkItem selectedWorkItem = trackingItemTableView.getSelectionModel().getSelectedItem();

        if (selectedWorkItem != null && isInputValid(false) && hasWorkItemChanged()) {
            TrackingItem selectedTracking = trackingItemChoiceBox.getSelectionModel().getSelectedItem();

            WorkItem modifiedWorkItem = new WorkItem(selectedWorkItem.getId());
            modifiedWorkItem.setWorkrecordId(selectedWorkrecord.getId());
            modifiedWorkItem.setSprintId(selectedWorkItem.getSprintId());
            modifiedWorkItem.setTrackingItemId(selectedTracking.getId());
            modifiedWorkItem.setStartTime(trackingItemStartTimeTimeSpinner.getValue());
            modifiedWorkItem.setEndTime(trackingItemEndTimeTimeSpinner.getValue());
            modifiedWorkItem.setDescription(trackingItemDescriptionValue.getText());
            modifiedWorkItem.setShortcut(selectedTracking.getShortcut());
            modifiedWorkItem.setName(selectedTracking.getName());

            EditWorkItemCommand cmd = new EditWorkItemCommand(controllerRepository, eventManager, trackingItemTableView, selectedWorkItem, modifiedWorkItem, workItemDao);
            undoService.execute(cmd);
            sortWorkItems();
        } else if (selectedWorkItem == null) {
            ControllerUtilities.showNoItemSelectedAlert(primaryStage, rb, noTrackingItemSelectionAlertTitle, noTrackingItemSelectionAlertHeader, noTrackingItemSelectionAlertContent);
        }
    }

    @FXML
    @SuppressWarnings("unused")
    private void deleteAction(ActionEvent event) throws SQLException, IOException {
        WorkItem selectedWorkItem = trackingItemTableView.getSelectionModel().getSelectedItem();

        if (selectedWorkItem != null) {
            DeleteWorkItemCommand cmd = new DeleteWorkItemCommand(controllerRepository, eventManager, trackingItemTableView, selectedWorkItem, workItemDao);
            undoService.execute(cmd);
            sortWorkItems();
        } else {
            ControllerUtilities.showNoItemSelectedAlert(primaryStage, rb, noTrackingItemSelectionAlertTitle, noTrackingItemSelectionAlertHeader, noTrackingItemSelectionAlertContent);
        }
    }

    @FXML
    @SuppressWarnings("unused")
    private void handleOnSelectedDateChangedAction(ActionEvent event) throws SQLException, IOException {
        DatePicker datePicker = (DatePicker) event.getSource();
        LocalDate newDate = datePicker.getValue();
        
        selectedWorkrecord = getWorkrecordOfDate(newDate);
        refreshWorkItemData();
        selectTrackingItemAndRefreshDetails();
    }
    
    @FXML
    @SuppressWarnings("unused")
    private void handleOnSetStartTimeButtonClickAction(ActionEvent event) {
        trackingItemStartTimeTimeSpinner.getValueFactory().setValue(
            trackingItemStartTimeTimeSpinner.formatLocalTime(LocalTime.now(), LocalTimeSpinner.TimeFormat.HH_MM)
        );
    }

    @FXML
    @SuppressWarnings("unused")
    private void handleOnSetEndTimeButtonClickAction(ActionEvent event) {
        trackingItemEndTimeTimeSpinner.getValueFactory().setValue(
            trackingItemEndTimeTimeSpinner.formatLocalTime(LocalTime.now(), LocalTimeSpinner.TimeFormat.HH_MM)
        );
    }
    
    @Override
    public void initialize(URL location, ResourceBundle rb) {
        this.rb = rb;
        
        workItemData.clear();
        trackingItemTableView.setItems(workItemData);

        selectedWorkrecord = workRecordDetailsViewController.getSelectedWorkrecord();
        
        initCellValueFactoryTableColumns();
        initStartTimeTimeSpinner();
        initEndTimeTimeSpinner();
        initTimeDurationSpinner();
        initListeners();
        initDatePickerBySelectedWorkrecord(selectedWorkrecord);
        initTrackingItemChoiceBox();
        
        try {
            if (selectedWorkrecord != null) {
                List<WorkItem> workItems = workItemDao.selectAll(selectedWorkrecord.getId());
                workItemData.addAll(workItems);
            }
        } catch (SQLException ex) {
            log.fatal("No WorkItemData could be loaded!", ex);
        }
        
        sortWorkItems();
        selectTrackingItemAndRefreshDetails();
        refreshButtonState();
        languageService.updateGuiItems();        
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
        MainToolBarViewController mainToolBarViewController = (MainToolBarViewController) controllerRepository.get(MainToolBarViewController.class.getName());
        if (mainToolBarViewController != null) {
            mainToolBarViewController.getWorkItemButton().disableProperty().set(false);
        }
    }
    
    @Override
    public void update(String eventType, Object source) {
        switch (eventType) {
            case newTrackingItemEvent, editTrackingItemEvent, deleteTrackingItemEvent -> initTrackingItemChoiceBox();
            case selectedWorkRecordChangedEvent -> {
                selectedWorkrecord = (Workrecord) source;
                if (selectedWorkrecord != null) {
                    selectedDateDatePicker.setValue(selectedWorkrecord.getDate());
                    try {
                        refreshWorkItemData();
                        selectTrackingItemAndRefreshDetails();
                    } catch (SQLException ex) {
                        log.error("Failed to update work items for new workrecord", ex);
                    }
                }
            }
        }
    }

    @Override
    public void updateGuiItems() {
        selectedDateLabel.setText(rb.getString(trackingItemDateResourceKey));
        sprintLabel.setText(rb.getString(trackingItemSprintResourceKey));

        if (sprint == null) {
            sprintNumberLabelValue.setText(rb.getString(sprintNotFoundResourceKey));
        }
        
        trackingItemShortcutTableColumn.setText(rb.getString(trackingItemShortcutResourceKey));
        trackingItemNameTableColumn.setText(rb.getString(trackingItemNameResourceKey));
        trackingItemStartTimeTableColumn.setText(rb.getString(trackingItemStartTimeResourceKey));
        trackingItemEndTimeTableColumn.setText(rb.getString(trackingItemEndTimeResourceKey));
        trackingItemTimeTableColumn.setText(rb.getString(trackingItemTimeResourceKey));
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
    
    public EventManager getEventManager() {
        return eventManager;
    }
    
    public void refreshWorkItemData() throws SQLException {
        workItemData.clear();
        if (selectedWorkrecord != null) {
            List<WorkItem> workItemsOfActualSelectedWorkrecord = workItemDao.selectAll(selectedWorkrecord.getId());
            workItemData.addAll(workItemsOfActualSelectedWorkrecord);
        }
        sortWorkItems();
    }
     
    @SuppressWarnings("unchecked")
    public void sortWorkItems() {
        FXCollections.sort(workItemData, Comparator.comparing(WorkItem::getStartTime, Comparator.nullsFirst(Comparator.naturalOrder())));
        if (!trackingItemTableView.getSortOrder().contains(trackingItemStartTimeTableColumn)) {
            trackingItemTableView.getSortOrder().setAll(trackingItemStartTimeTableColumn);
        }
        trackingItemTableView.sort();
    }
    
    @SuppressWarnings("unchecked")
    private void initCellValueFactoryTableColumns() {
        trackingItemShortcutTableColumn.setCellValueFactory(cellData -> cellData.getValue().getShortcutProperty());
        trackingItemNameTableColumn.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
        trackingItemStartTimeTableColumn.setCellValueFactory(cellData -> cellData.getValue().getStartTimeProperty());
        trackingItemEndTimeTableColumn.setCellValueFactory(cellData -> cellData.getValue().getEndTimeProperty());

        trackingItemStartTimeTableColumn.setSortType(TableColumn.SortType.ASCENDING);
        trackingItemTableView.getSortOrder().setAll(trackingItemStartTimeTableColumn);
    }

    private void initStartTimeTimeSpinner() {
        trackingItemStartTimeButton.setGraphic(new ImageView(timeNowIcon));
        trackingItemStartTimeTimeSpinner = new LocalTimeSpinner();
        trackingItemDetailsGridPane.add(trackingItemStartTimeTimeSpinner, 2, 2);
    }

    private void initEndTimeTimeSpinner() {
        trackingItemEndTimeButton.setGraphic(new ImageView(timeNowIcon));
        trackingItemEndTimeTimeSpinner = new LocalTimeSpinner();
        trackingItemDetailsGridPane.add(trackingItemEndTimeTimeSpinner, 2, 3);
    }

    private void initTimeDurationSpinner() {
        trackingItemTimeDurationSpinner = new DurationSpinner(false);
        trackingItemDetailsGridPane.add(trackingItemTimeDurationSpinner, 2, 4);
    }
    
    private void initListeners() {
        selectedDateDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            trySetSprintNumberLabel(newVal);
            eventManager.notifyListenerOfEvent(workItemDateChangedEvent, newVal);
            refreshButtonState();
        });

        trackingItemTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            showTrackingItemDetails(newVal);
            refreshButtonState();
        });

        // SYNC: StartTime changed -> Update EndTime (maintaining Duration)
        trackingItemStartTimeTimeSpinner.valueProperty().addListener((obs, oldVal, newVal) -> { 
            if (!isSyncing && newVal != null) {
                isSyncing = true;
                try {
                    Duration duration = trackingItemTimeDurationSpinner.getValue();
                    if (duration != null) {
                        trackingItemEndTimeTimeSpinner.getValueFactory().setValue(newVal.plus(duration));
                    }
                } finally {
                    isSyncing = false;
                }
            }
            refreshButtonState();
        });
        
        // SYNC: EndTime changed -> Update Duration
        trackingItemEndTimeTimeSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!isSyncing && newVal != null) {
                isSyncing = true;
                try {
                    LocalTime startTime = trackingItemStartTimeTimeSpinner.getValue();
                    if (startTime != null && newVal.isAfter(startTime)) {
                        trackingItemTimeDurationSpinner.getValueFactory().setValue(Duration.between(startTime, newVal));
                    }
                } finally {
                    isSyncing = false;
                }
            }
            refreshButtonState();
        });
        
        // SYNC: Duration changed -> Update EndTime
        trackingItemTimeDurationSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            DurationStyler.styleSpinner(trackingItemTimeDurationSpinner, newVal);
            if (!isSyncing && newVal != null) {
                isSyncing = true;
                try {
                    LocalTime startTime = trackingItemStartTimeTimeSpinner.getValue();
                    if (startTime != null) {
                        trackingItemEndTimeTimeSpinner.getValueFactory().setValue(startTime.plus(newVal));
                    }
                } finally {
                    isSyncing = false;
                }
            }
            refreshButtonState();
        });
        
        trackingItemChoiceBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            refreshButtonState();
        });
        
        trackingItemDescriptionValue.textProperty().addListener((obs, oldVal, newVal) -> {
            refreshButtonState();
        });
    }

    private void trySetSprintNumberLabel(LocalDate date) {
        try {
            sprint = sprintDAO.selectSprintForDate(date);
            if (sprint == null) {
                sprintNumberLabelValue.setText(rb.getString(sprintNotFoundResourceKey));
                sprintNumberLabelValue.setStyle("-fx-text-fill: " + COLOR_LIGHT_RED + ";");
                log.info("Sprint for date {} not found, please update sprint referencedata!", date);                
            } else {
                sprintNumberLabelValue.setText(sprint.getId().toString());
                sprintNumberLabelValue.setStyle("");
            }
        } catch (SQLException ex) {
            sprint = null;
            sprintNumberLabelValue.setText(rb.getString(sprintNotFoundResourceKey));
            sprintNumberLabelValue.setStyle("-fx-text-fill: " + COLOR_LIGHT_RED + ";");
            log.info("Sprint for date {} not found, please update sprint referencedata!", date);                
        }
    }

    private void initDatePickerBySelectedWorkrecord(Workrecord selectedWorkrecord) {
        if (selectedWorkrecord != null) {
            selectedDateDatePicker.setValue(selectedWorkrecord.getDate());
        } else {
            selectedDateDatePicker.setValue(LocalDate.now());
        }
        trySetSprintNumberLabel(selectedDateDatePicker.getValue());
    }
    
    private void initTrackingItemChoiceBox() {
        try {
            trackingItemChoiceBox.getItems().clear();
            List<TrackingItem> items = trackingItemDAO.selectAll();
            trackingItemChoiceBox.getItems().addAll(items);
            if (!items.isEmpty()) {
                trackingItemChoiceBox.getSelectionModel().select(0);
            }
        } catch (SQLException ex) {
            log.fatal("No TrackingItems could be loaded!", ex);
        }
    }

    public void selectTrackingItemAndRefreshDetails() {
        if (!workItemData.isEmpty()) {
            WorkItem lastItem = workItemData.get(workItemData.size() - 1);
            trackingItemTableView.getSelectionModel().select(lastItem);  
            showTrackingItemDetails(lastItem);
        } else {
            trackingItemTableView.getSelectionModel().clearSelection();
            showTrackingItemDetails(null);
        }
    }

    private boolean isInputValid(boolean isNew) {
        LocalTime startTime = trackingItemStartTimeTimeSpinner.getValue();
        LocalTime endTime = trackingItemEndTimeTimeSpinner.getValue();
        TrackingItem selectedTracking = trackingItemChoiceBox.getSelectionModel().getSelectedItem();
        
        // 1. Basic Validity
        if (selectedTracking == null || startTime == null || endTime == null) return false;
        if (startTime.equals(LocalTime.MIN) && endTime.equals(LocalTime.MIN)) return false;
        if (!startTime.isBefore(endTime)) return false;
        
        // 2. Overlap Check against existing items
        WorkItem selectedItem = trackingItemTableView.getSelectionModel().getSelectedItem();
        
        for (WorkItem item : workItemData) {
            if (!isNew && selectedItem != null && Objects.equals(item.getId(), selectedItem.getId())) {
                continue; // Ignore current item when validating an Edit
            }
            // Overlap condition: (StartA < EndB) AND (EndA > StartB)
            if (startTime.isBefore(item.getEndTime()) && endTime.isAfter(item.getStartTime())) {
                log.info("Overlap detected with existing item: {}", item.getName());
                return false; 
            }
        }
        return true;
    }

    @SuppressWarnings("null")
    private boolean hasWorkItemChanged() {
        if (!isWorkItemSelected()) return false;
        
        TrackingItem currentChoice = trackingItemChoiceBox.getValue();
        long currentTrackingId = (currentChoice != null) ? currentChoice.getId() : 0L;
        
        LocalTime currentStart = trackingItemStartTimeTimeSpinner.getValue();
        LocalTime currentEnd = trackingItemEndTimeTimeSpinner.getValue();
        String currentDesc = trackingItemDescriptionValue.getText() == null ? "" : trackingItemDescriptionValue.getText();
        
        return currentTrackingId != oldTrackingItemId
            || !Objects.equals(currentStart, oldStartTime)
            || !Objects.equals(currentEnd, oldEndTime)
            || !Objects.equals(currentDesc, oldDescription);
    }

    public void refreshButtonState() {
        LocalDate date = selectedDateDatePicker.getValue();
        boolean recordExists = workrecordExistsForDate(date);
        boolean itemSelected = isWorkItemSelected();
        
        boolean canCreateNew = recordExists && isInputValid(true);
        boolean canEditExisting = itemSelected && isInputValid(false) && hasWorkItemChanged();
        
        newButton.setDisable(!canCreateNew);
        editButton.setDisable(!canEditExisting);
        deleteButton.setDisable(!itemSelected);
    }

    private boolean isWorkItemSelected() {
        return trackingItemTableView.getSelectionModel().getSelectedItem() != null;
    }
    
    private boolean workrecordExistsForDate(LocalDate date) {
        return getWorkrecordOfDate(date) != null;
    }
    
    private Workrecord getWorkrecordOfDate(LocalDate date) {
        try {
            List<Workrecord> workRecords = workRecordDetailsViewController.getWorkrecordDao().selectAll(workRecordViewController.getSelectedUser(), date);
            return workRecords.stream().findFirst().orElse(null);
        } catch (SQLException ex) {
            log.fatal("No Workrecords could be loaded!", ex);    
        }
        return null;
    }
    
    private void showTrackingItemDetails(WorkItem workItem) {
        isSyncing = true; // Disable sync while loading data to avoid jitter
        try {
            if (workItem != null) {
                saveActualWorkItemInformation(workItem);

                trackingItemChoiceBox.getItems().stream()
                        .filter(item -> Objects.equals(item.getId(), workItem.getTrackingItemId()))
                        .findFirst()
                        .ifPresent(trackingItemChoiceBox.getSelectionModel()::select);

                trackingItemStartTimeTimeSpinner.getValueFactory().setValue(workItem.getStartTime());
                trackingItemEndTimeTimeSpinner.getValueFactory().setValue(workItem.getEndTime());
                trackingItemTimeDurationSpinner.getValueFactory().setValue(Duration.between(workItem.getStartTime(), workItem.getEndTime()));
                trackingItemDescriptionValue.setText(workItem.getDescription());
            } else {
                saveActualWorkItemInformation(null);
                if (!trackingItemChoiceBox.getItems().isEmpty()) {
                    trackingItemChoiceBox.getSelectionModel().select(0);
                }
                if (selectedWorkrecord != null) {
                    trackingItemStartTimeTimeSpinner.getValueFactory().setValue(selectedWorkrecord.getStarttime());
                    trackingItemEndTimeTimeSpinner.getValueFactory().setValue(selectedWorkrecord.getEndtime());
                    trackingItemTimeDurationSpinner.getValueFactory().setValue(Duration.between(selectedWorkrecord.getStarttime(), selectedWorkrecord.getEndtime()));
                } else {
                    trackingItemStartTimeTimeSpinner.getValueFactory().setValue(LocalTime.MIN);
                    trackingItemEndTimeTimeSpinner.getValueFactory().setValue(LocalTime.MIN);
                    trackingItemTimeDurationSpinner.getValueFactory().setValue(Duration.ZERO);
                }
                trackingItemDescriptionValue.setText("");
            }
        } finally {
            isSyncing = false;
        }
    }

    private void saveActualWorkItemInformation(WorkItem workItem) {        
        if (workItem == null) {
            oldTrackingItemId = 0L;
            oldStartTime = null;
            oldEndTime = null;
            oldDescription = "";
            return;
        }
        
        oldTrackingItemId = workItem.getTrackingItemId();
        oldStartTime = workItem.getStartTime();
        oldEndTime = workItem.getEndTime();
        oldDescription = workItem.getDescription() == null ? "" : workItem.getDescription();
    }
}