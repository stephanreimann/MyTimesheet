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
import javafx.application.Platform;
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
import sqlite.SprintDAO;
import sqlite.TrackingItemDAO;
import sqlite.WorkItemDAO;
import utils.*;

/**
 * Controller for managing WorkItems per WorkRecord/Sprint.
 */
public class WorkItemViewController implements Initializable, IViewController, IEventListener {

    private static final String PREF_DIVIDER_KEY = "DividerPositionTrackingItemSplitPane";
    private static final String PREF_DIVIDER_DEFAULT = "0.25";
    private static final String COLOR_LIGHT_RED = "Red";
    private static final String ICON_TIME_NOW = "icons/timeNow.png";
    
    private static final String KEY_NEW = "New";
    private static final String KEY_EDIT = "Edit";
    private static final String KEY_DELETE = "Delete";
    
    private static final String KEY_DATE = "Date";
    private static final String KEY_SPRINT = "Sprint";
    private static final String KEY_SPRINT_NOT_FOUND = "SprintNotFound";
    private static final String KEY_SHORTCUT = "TrackingItemShortcut";
    private static final String KEY_NAME = "TrackingItemName";
    private static final String KEY_START_TIME = "TrackingItemStartTime";
    private static final String KEY_END_TIME = "TrackingItemEndTime";
    private static final String KEY_TIME = "TrackingItemTime";
    private static final String KEY_DETAILS_HEADER = "TrackingItemDetailsHeader";
    private static final String KEY_ITEM = "TrackingItem";
    private static final String KEY_DESCRIPTION = "TrackingItemDescription";
    private static final String KEY_START_TOOLTIP = "StartTimeButtonToolTip";
    private static final String KEY_START_TOOLTIP_EXISTING = "StartTimeButtonToolTipForExsistingData"; 
    private static final String KEY_END_TOOLTIP = "EndTimeButtonToolTip";
    
    private static final String EVENT_DATE_CHANGED = "WorkItemDateChanged";
    private static final String EVENT_SELECTED_RECORD_CHANGED = "SelectedWorkRecordChanged";
    private static final String EVENT_NEW_ITEM = "NewTrackingItem";
    private static final String EVENT_EDIT_ITEM = "EditTrackingItem";
    private static final String EVENT_DELETE_ITEM = "DeleteTrackingItem";
    
    private static final String ALERT_TITLE = "NoSelectionAlertTitle";
    private static final String ALERT_HEADER = "NoWorkItemSelectionAlertHeader";
    private static final String ALERT_CONTENT = "NoWorkItemSelectionAlertContent";
    
    // FXML Members
    @FXML private ToolBar trackingItemToolBar;
    @FXML private Label selectedDateLabel;
    @FXML private DatePicker selectedDateDatePicker;
    @FXML private Label sprintLabel;
    @FXML private Label sprintNumberLabelValue;
    @FXML SplitPane trackingItemSplitPane;
    
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
    @FXML private Label trackingItemSumLabel;
    @FXML private Label trackingItemSumValueLabel;
    
    @FXML private Button newButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    
    private final Logger log = LogManager.getLogger(WorkItemViewController.class);

    private Stage primaryStage;
    private final ControllerRepository controllerRepository;
    private final LanguageService languageService;
    private final Connection connection;
    private final UndoService undoService;
    private final PropertiesService propertiesService;
    private ResourceBundle rb;
    private final EventManager eventManager;

    private LocalTimeSpinner trackingItemStartTimeTimeSpinner;
    private LocalTimeSpinner trackingItemEndTimeTimeSpinner;
    private DurationSpinner trackingItemTimeDurationSpinner;
    
    private Sprint sprint;
    private final SprintDAO sprintDAO;
    private final TrackingItemDAO trackingItemDAO;
    private final WorkItemDAO workItemDao;

    private double dividerPosition;
    private long oldTrackingItemId;
    private LocalTime oldStartTime;
    private LocalTime oldEndTime;
    private String oldDescription = "";
            
    private final ObservableList<WorkItem> workItemData = FXCollections.observableArrayList();
    
    private final WorkRecordDetailsViewController workRecordDetailsViewController;
    private final WorkRecordViewController workRecordViewController;
    private Workrecord selectedWorkrecord;

    private boolean isSyncing = false;
    private LocalTime workitemEndtimeDeltaThreshold;
            
    public WorkItemViewController(ControllerRepository controllerRepository, LanguageService languageService, 
                                  Connection connection, UndoService undoService, PropertiesService propertiesService) throws SQLException {
        this.controllerRepository = Objects.requireNonNull(controllerRepository, "controllerRepository");
        this.languageService = Objects.requireNonNull(languageService, "languageService");
        this.connection = Objects.requireNonNull(connection, "connection");
        this.undoService = Objects.requireNonNull(undoService, "undoService");
        this.propertiesService = Objects.requireNonNull(propertiesService, "propertiesService");
        
        this.sprintDAO = new SprintDAO(connection);
        this.trackingItemDAO = new TrackingItemDAO(connection);
        this.workItemDao = new WorkItemDAO(connection);

        this.workRecordDetailsViewController = (WorkRecordDetailsViewController) controllerRepository.get(WorkRecordDetailsViewController.class.getName());
        this.workRecordViewController = (WorkRecordViewController) controllerRepository.get(WorkRecordViewController.class.getName());
        this.eventManager = new EventManager();
        
        parseDeltaThreshold();
    }

    private void parseDeltaThreshold() {
        String thresholdStr = propertiesService.getProperty("WorkitemEndtimeDeltaThreshold", "PT0S");
        try {
            Duration duration = Duration.parse(thresholdStr);
            this.workitemEndtimeDeltaThreshold = LocalTime.MIDNIGHT.plus(duration);
        } catch (Exception ex) {
            log.warn("Could not parse WorkitemEndtimeDeltaThreshold property: {}. Defaulting to 00:00", thresholdStr, ex);
            this.workitemEndtimeDeltaThreshold = LocalTime.MIN;
        }
    }
    
    @FXML
    private void newAction(ActionEvent event) throws SQLException, IOException {
        if (!isInputValid(true) || selectedWorkrecord == null || sprint == null) return;

        WorkItem newWorkItem = buildWorkItemFromCurrentInput(workItemDao.getNextId());
        undoService.execute(new NewWorkItemCommand(controllerRepository, eventManager, trackingItemTableView, newWorkItem, workItemDao));
        sortWorkItems();
        refreshStartTimeButtonTooltip();
    }
    
    @FXML
    private void editAction(ActionEvent event) throws SQLException, IOException {
        WorkItem selectedWorkItem = trackingItemTableView.getSelectionModel().getSelectedItem();

        if (selectedWorkItem != null && isInputValid(false) && hasWorkItemChanged()) {
            WorkItem modifiedWorkItem = buildWorkItemFromCurrentInput(selectedWorkItem.getId());
            undoService.execute(new EditWorkItemCommand(controllerRepository, eventManager, trackingItemTableView, selectedWorkItem, modifiedWorkItem, workItemDao));
            sortWorkItems();
            refreshStartTimeButtonTooltip();
        } else if (selectedWorkItem == null) {
            ControllerUtilities.showNoItemSelectedAlert(primaryStage, rb, ALERT_TITLE, ALERT_HEADER, ALERT_CONTENT);
        }
    }

    @FXML
    private void deleteAction(ActionEvent event) throws SQLException, IOException {
        WorkItem selectedWorkItem = trackingItemTableView.getSelectionModel().getSelectedItem();

        if (selectedWorkItem != null) {
            undoService.execute(new DeleteWorkItemCommand(controllerRepository, eventManager, trackingItemTableView, selectedWorkItem, workItemDao));
            sortWorkItems();
            refreshStartTimeButtonTooltip();
        } else {
            ControllerUtilities.showNoItemSelectedAlert(primaryStage, rb, ALERT_TITLE, ALERT_HEADER, ALERT_CONTENT);
        }
    }

    @FXML
    private void handleOnSelectedDateChangedAction(ActionEvent event) throws SQLException, IOException {
        DatePicker datePicker = (DatePicker) event.getSource();
        selectedWorkrecord = getWorkrecordOfDate(datePicker.getValue());
        refreshWorkItemData();
        selectTrackingItemAndRefreshDetails();
    }
    
    @FXML
    private void handleOnSetStartTimeButtonClickAction(ActionEvent event) {
        LocalTime newEndtime = workItemData.isEmpty() ? LocalTime.now() : workItemData.get(workItemData.size() - 1).getEndTime();
        trackingItemStartTimeTimeSpinner.getValueFactory().setValue(
            trackingItemStartTimeTimeSpinner.formatLocalTime(newEndtime, LocalTimeSpinner.TimeFormat.HH_MM)
        );
    }

    @FXML
    private void handleOnSetEndTimeButtonClickAction(ActionEvent event) {
        LocalTime timeToSet = trackingItemStartTimeTimeSpinner.getValue();

        if (workitemEndtimeDeltaThreshold != null && !workitemEndtimeDeltaThreshold.equals(LocalTime.MIN)) {
            timeToSet = timeToSet.plusHours(workitemEndtimeDeltaThreshold.getHour())
                                 .plusMinutes(workitemEndtimeDeltaThreshold.getMinute());
        }        

        trackingItemEndTimeTimeSpinner.getValueFactory().setValue(
            trackingItemEndTimeTimeSpinner.formatLocalTime(timeToSet, LocalTimeSpinner.TimeFormat.HH_MM)
        );
    }
    
    private WorkItem buildWorkItemFromCurrentInput(long id) {
        TrackingItem selectedTracking = trackingItemChoiceBox.getSelectionModel().getSelectedItem();
        WorkItem workItem = new WorkItem(id);
        workItem.setWorkrecordId(selectedWorkrecord.getId());
        workItem.setSprintId(sprint.getId());
        workItem.setTrackingItemId(selectedTracking.getId());
        workItem.setStartTime(trackingItemStartTimeTimeSpinner.getValue());
        workItem.setEndTime(trackingItemEndTimeTimeSpinner.getValue());
        workItem.setDescription(trackingItemDescriptionValue.getText());
        workItem.setShortcut(selectedTracking.getShortcut());
        workItem.setName(selectedTracking.getName());
        return workItem;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle rb) {
        this.rb = rb;
        workItemData.clear();
        trackingItemTableView.setItems(workItemData);

        selectedWorkrecord = workRecordDetailsViewController.getSelectedWorkrecord();
        
        initCellValueFactoryTableColumns();
        initSpinners();
        initListeners();
        initDatePickerBySelectedWorkrecord(selectedWorkrecord);
        initTrackingItemChoiceBox();
        initDividerPositionTrackingItemSplitPane();
        trackingItemSumValueLabel.setText("00:00");
        
        try {
            if (selectedWorkrecord != null) {
                workItemData.addAll(workItemDao.selectAll(selectedWorkrecord.getId()));
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
        propertiesService.setProperty(PREF_DIVIDER_KEY, Double.toString(MathUtilities.round(dividerPosition, 2)));

        MainToolBarViewController mainController = (MainToolBarViewController) controllerRepository.get(MainToolBarViewController.class.getName());
        if (mainController != null) {
            mainController.getWorkItemButton().setDisable(false);
        }
    }
    
    @Override
    public void update(String eventType, Object source) {
        switch (eventType) {
            case EVENT_NEW_ITEM, EVENT_EDIT_ITEM, EVENT_DELETE_ITEM -> initTrackingItemChoiceBox();
            case EVENT_SELECTED_RECORD_CHANGED -> {
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
        selectedDateLabel.setText(rb.getString(KEY_DATE));
        sprintLabel.setText(rb.getString(KEY_SPRINT));

        if (sprint == null) {
            sprintNumberLabelValue.setText(rb.getString(KEY_SPRINT_NOT_FOUND));
        }
        
        trackingItemShortcutTableColumn.setText(rb.getString(KEY_SHORTCUT));
        trackingItemNameTableColumn.setText(rb.getString(KEY_NAME));
        trackingItemStartTimeTableColumn.setText(rb.getString(KEY_START_TIME));
        trackingItemEndTimeTableColumn.setText(rb.getString(KEY_END_TIME));
        trackingItemTimeTableColumn.setText(rb.getString(KEY_TIME));
        trackingItemDetailsHeaderLabel.setText(rb.getString(KEY_DETAILS_HEADER));        
        trackingItemNameLabel.setText(rb.getString(KEY_ITEM));
        trackingItemStartTimeLabel.setText(rb.getString(KEY_START_TIME));
        refreshStartTimeButtonTooltip();
        trackingItemEndTimeLabel.setText(rb.getString(KEY_END_TIME));        
        trackingItemEndTimeButton.setTooltip(new Tooltip(rb.getString(KEY_END_TOOLTIP)));
        trackingItemDescriptionLabel.setText(rb.getString(KEY_DESCRIPTION));
        newButton.setText(rb.getString(KEY_NEW));
        editButton.setText(rb.getString(KEY_EDIT));
        deleteButton.setText(rb.getString(KEY_DELETE));    
    }
    
    public EventManager getEventManager() {
        return eventManager;
    }
    
    @SuppressWarnings("unchecked")
    public void sortWorkItems() {
        FXCollections.sort(workItemData, Comparator.comparing(WorkItem::getStartTime, Comparator.nullsFirst(Comparator.naturalOrder())));
        if (!trackingItemTableView.getSortOrder().contains(trackingItemStartTimeTableColumn)) {
            trackingItemTableView.getSortOrder().setAll(trackingItemStartTimeTableColumn);
        }
        trackingItemTableView.sort();        
        calculateAndDisplaySum();
    }
    
    @SuppressWarnings("unchecked")
    private void initCellValueFactoryTableColumns() {
        trackingItemShortcutTableColumn.setCellValueFactory(cell -> cell.getValue().getShortcutProperty());
        trackingItemNameTableColumn.setCellValueFactory(cell -> cell.getValue().getNameProperty());
        trackingItemStartTimeTableColumn.setCellValueFactory(cell -> cell.getValue().getStartTimeProperty());
        trackingItemEndTimeTableColumn.setCellValueFactory(cell -> cell.getValue().getEndTimeProperty());

        trackingItemStartTimeTableColumn.setSortType(TableColumn.SortType.ASCENDING);
        trackingItemTableView.getSortOrder().setAll(trackingItemStartTimeTableColumn);
    }

    private void initSpinners() {
        trackingItemStartTimeButton.setGraphic(new ImageView(ICON_TIME_NOW));
        trackingItemStartTimeTimeSpinner = new LocalTimeSpinner();
        trackingItemDetailsGridPane.add(trackingItemStartTimeTimeSpinner, 2, 2);

        trackingItemEndTimeButton.setGraphic(new ImageView(ICON_TIME_NOW));
        trackingItemEndTimeTimeSpinner = new LocalTimeSpinner();
        trackingItemDetailsGridPane.add(trackingItemEndTimeTimeSpinner, 2, 3);

        trackingItemTimeDurationSpinner = new DurationSpinner(false);
        trackingItemDetailsGridPane.add(trackingItemTimeDurationSpinner, 2, 4);
    }

    private void initListeners() {
        selectedDateDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            trySetSprintNumberLabel(newVal);
            eventManager.notifyListenerOfEvent(EVENT_DATE_CHANGED, newVal);
            refreshButtonState();
        });

        trackingItemTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            showTrackingItemDetails(newVal);
            refreshButtonState();
        });

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
        
        trackingItemEndTimeTimeSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!isSyncing && newVal != null) {
                isSyncing = true;
                try {
                    LocalTime startTime = trackingItemStartTimeTimeSpinner.getValue();
                    if (startTime != null && (newVal.isAfter(startTime) || newVal.equals(startTime))) {
                        trackingItemTimeDurationSpinner.getValueFactory().setValue(Duration.between(startTime, newVal));
                    }
                } finally {
                    isSyncing = false;
                }
            }
            refreshButtonState();
        });
        
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
        
        trackingItemChoiceBox.valueProperty().addListener((obs, oldVal, newVal) -> refreshButtonState());
        trackingItemDescriptionValue.textProperty().addListener((obs, oldVal, newVal) -> refreshButtonState());
        
        trackingItemSplitPane.getDividers().get(0).positionProperty().addListener((obs, oldVal, newVal) -> dividerPosition = newVal.doubleValue());
    }

    private void trySetSprintNumberLabel(LocalDate date) {
        try {
            sprint = sprintDAO.selectSprintForDate(date);
            if (sprint == null) {
                setSprintNotFoundState(date);
            } else {
                sprintNumberLabelValue.setText(sprint.getId().toString());
                sprintNumberLabelValue.setStyle("");
            }
        } catch (SQLException ex) {
            sprint = null;
            setSprintNotFoundState(date);
        }
    }

    private void setSprintNotFoundState(LocalDate date) {
        sprintNumberLabelValue.setText(rb.getString(KEY_SPRINT_NOT_FOUND));
        sprintNumberLabelValue.setStyle("-fx-text-fill: " + COLOR_LIGHT_RED + ";");
        log.info("Sprint for date {} not found, please update sprint referencedata!", date);
    }

    private void initDatePickerBySelectedWorkrecord(Workrecord selectedWorkrecord) {
        selectedDateDatePicker.setValue(selectedWorkrecord != null ? selectedWorkrecord.getDate() : LocalDate.now());
        trySetSprintNumberLabel(selectedDateDatePicker.getValue());
    }
    
    private void initTrackingItemChoiceBox() {
        try {
            trackingItemChoiceBox.getItems().setAll(trackingItemDAO.selectAll());
            if (!trackingItemChoiceBox.getItems().isEmpty()) {
                trackingItemChoiceBox.getSelectionModel().select(0);
            }
        } catch (SQLException ex) {
            log.fatal("No TrackingItems could be loaded!", ex);
        }
    }

    private void initDividerPositionTrackingItemSplitPane() {
        Platform.runLater(() -> {
            String val = propertiesService.getProperty(PREF_DIVIDER_KEY, PREF_DIVIDER_DEFAULT);
            dividerPosition = Double.parseDouble(val);
            trackingItemSplitPane.setDividerPositions(dividerPosition);
        });
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
        
        if (selectedTracking == null || startTime == null || endTime == null) return false;
        if (startTime.equals(LocalTime.MIN) && endTime.equals(LocalTime.MIN)) return false;
        if (!startTime.isBefore(endTime)) return false;
        
        WorkItem selectedItem = trackingItemTableView.getSelectionModel().getSelectedItem();
        for (WorkItem item : workItemData) {
            if (!isNew && selectedItem != null && Objects.equals(item.getId(), selectedItem.getId())) {
                continue;
            }
            if (startTime.isBefore(item.getEndTime()) && endTime.isAfter(item.getStartTime())) {
                log.info("Overlap detected with existing item: {}", item.getName());
                return false; 
            }
        }
        return true;
    }

    private boolean hasWorkItemChanged() {
        if (!isWorkItemSelected()) return false;
        
        TrackingItem currentChoice = trackingItemChoiceBox.getValue();
        long currentTrackingId = currentChoice != null ? currentChoice.getId() : 0L;
        String currentDesc = Objects.toString(trackingItemDescriptionValue.getText(), "");
        
        return currentTrackingId != oldTrackingItemId
            || !Objects.equals(trackingItemStartTimeTimeSpinner.getValue(), oldStartTime)
            || !Objects.equals(trackingItemEndTimeTimeSpinner.getValue(), oldEndTime)
            || !Objects.equals(currentDesc, oldDescription);
    }

    public void refreshButtonState() {
        boolean recordExists = workrecordExistsForDate(selectedDateDatePicker.getValue());
        boolean itemSelected = isWorkItemSelected();
        
        newButton.setDisable(!(recordExists && isInputValid(true)));
        editButton.setDisable(!(itemSelected && isInputValid(false) && hasWorkItemChanged()));
        deleteButton.setDisable(!itemSelected);
    }

    private void refreshStartTimeButtonTooltip() {
        String tooltipKey = workItemData.isEmpty() ? KEY_START_TOOLTIP : KEY_START_TOOLTIP_EXISTING;
        trackingItemStartTimeButton.setTooltip(new Tooltip(rb.getString(tooltipKey)));
    }

    public void refreshWorkItemData() throws SQLException {
        workItemData.clear();
        if (selectedWorkrecord != null) {
            workItemData.addAll(workItemDao.selectAll(selectedWorkrecord.getId()));
        }
        sortWorkItems();
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
        isSyncing = true;
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
        oldDescription = Objects.toString(workItem.getDescription(), "");
    }
    
    private void calculateAndDisplaySum() {
        Duration totalDuration = workItemData.stream()
                .filter(item -> item.getStartTime() != null && item.getEndTime() != null)
                .map(item -> Duration.between(item.getStartTime(), item.getEndTime()))
                .reduce(Duration.ZERO, Duration::plus);
        
        long hours = totalDuration.toHours();
        long minutes = totalDuration.toMinutes() % 60;
        
        trackingItemSumValueLabel.setText(String.format("%02d:%02d", hours, minutes));
    }    
}