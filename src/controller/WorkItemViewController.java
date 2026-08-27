/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import command.workitemtracking.*;
import controls.LocalTimeSpinner;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.*;
import java.util.*;
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
class WorkItemViewController implements Initializable, IViewController, IEventListener {

    private enum DataAction { NEW, EDIT, DELETE };
    private DataAction dataAction;
    
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
    
    private final String workItemDateChangedEvent = "WorkItemDateChanged";
    private final String selectedWorkRecordChangedEvent = "SelectedWorkRecordChanged";
    
    private final String newTrackingItemEvent = "NewTrackingItem";
    private final String editTrackingItemEvent = "EditTrackingItem";
    private final String deleteTrackingItemEvent = "DeleteTrackingItem";
    
    private final String noTrackingItemSelectionAlertTitle = "NoSelectionAlertTitle";
    private final String noTrackingItemSelectionAlertHeader = "NoTrackingItemSelectionAlertHeader";
    private final String noTrackingItemSelectionAlertContent = "NoTrackingItemSelectionAlertContent";
    
    // <editor-fold defaultstate="collapsed" desc="FXML Member">
    @FXML
    @SuppressWarnings("unused")
    private ToolBar trackingItemToolBar;
    @FXML
    private Label selectedDateLabel;
    @FXML
    private DatePicker selectedDateDatePicker;
    @FXML
    private Label sprintLabel;
    @FXML
    private Label sprintNumberLabelValue;
    
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
    // </editor-fold>
    
    private final Logger log = LogManager.getLogger(WorkItemViewController.class.getName());

    private Stage primaryStage;
    private final ControllerRepository controllerRepository;
    private final LanguageService languageService;
    @SuppressWarnings("unused")
    private final Connection connection;
    private final UndoService undoService;
    private ResourceBundle rb;
    private EventManager eventManager;

    private LocalTimeSpinner trackingItemStartTimeTimeSpinner;
    private LocalTimeSpinner trackingItemEndTimeTimeSpinner;

    private Sprint sprint;
    private final SprintDAO sprintDAO;
    private final TrackingItemDAO trackingItemDAO;
    private WorkItem selectedWorkItem;
    private final WorkItemDAO workItemDao;

    private long oldId;
    private long newId;
    private long oldWorkrecordId;
    private long newWorkrecordId;
    private long oldSprintId;
    private long newSprintId;
    private long oldTrackingItemId;
    private long newTrackingItemId;
    private LocalTime oldStartTime;
    private LocalTime newStartTime;
    private LocalTime oldEndTime;
    private LocalTime newEndTime;
    private String oldDescription;
    private String newDescription;
    private String oldShortcut;
    private String newShortcut;
    private String oldName;
    private String newName;
            
    private final ObservableList<WorkItem> workItemData = FXCollections.observableArrayList();
    
    private final WorkRecordDetailsViewController workRecordDetailsViewController;
    private final WorkRecordViewController workRecordViewController;
    private Workrecord selectedWorkrecord;
    
    public WorkItemViewController(ControllerRepository controllerRepository, LanguageService languageService, Connection connection, UndoService undoService) throws SQLException {
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
        this.selectedWorkrecord = workRecordDetailsViewController.getSelectedWorkrecord();
    }
    
    @FXML
    @SuppressWarnings("unused")
    private void newAction(ActionEvent event) throws SQLException, IOException {
        dataAction = DataAction.NEW;
        WorkItem newWorkItem = new WorkItem(workItemDao.getNextId());
        newWorkItem.setWorkrecordId(selectedWorkrecord.getId());
        newWorkItem.setSprintId(Long.valueOf(sprintNumberLabelValue.getText()));
        newWorkItem.setTrackingItemId(trackingItemChoiceBox.getSelectionModel().getSelectedItem().getId());
        newWorkItem.setStartTime(trackingItemStartTimeTimeSpinner.getValue());
        newWorkItem.setEndTime(trackingItemEndTimeTimeSpinner.getValue());
        newWorkItem.setDescription(trackingItemDescriptionValue.getText());
        newWorkItem.setShortcut(trackingItemChoiceBox.getSelectionModel().getSelectedItem().getShortcut());
        newWorkItem.setName(trackingItemChoiceBox.getSelectionModel().getSelectedItem().getName());
        if(isInputValid()) {
            NewWorkItemCommand cmd = new NewWorkItemCommand(controllerRepository, eventManager, trackingItemTableView, newWorkItem, workItemDao);
            undoService.execute(cmd);
        }
    }
    
    @FXML
    @SuppressWarnings("unused")
    private void editAction(ActionEvent event) throws SQLException, IOException {
        dataAction = DataAction.EDIT;
        if(selectedWorkItem != null) {
            WorkItem modifiedWorkItem = new WorkItem(selectedWorkItem.getId());
            modifiedWorkItem.setSprintId(selectedWorkItem.getSprintId());
            modifiedWorkItem.setTrackingItemId(trackingItemChoiceBox.getSelectionModel().getSelectedItem().getId());
            modifiedWorkItem.setStartTime(trackingItemStartTimeTimeSpinner.getValue());
            modifiedWorkItem.setEndTime(trackingItemEndTimeTimeSpinner.getValue());
            modifiedWorkItem.setDescription(trackingItemDescriptionValue.getText());
            modifiedWorkItem.setShortcut(trackingItemChoiceBox.getSelectionModel().getSelectedItem().getShortcut());
            modifiedWorkItem.setName(trackingItemChoiceBox.getSelectionModel().getSelectedItem().getName());
            if(!selectedWorkItem.equals(modifiedWorkItem) && isInputValid()) {
                EditWorkItemCommand cmd = new EditWorkItemCommand(controllerRepository, eventManager, trackingItemTableView, selectedWorkItem, modifiedWorkItem, workItemDao);
                undoService.execute(cmd);
            }
        } else {
            ControllerUtilities.showNoItemSelectedAlert(primaryStage, rb, noTrackingItemSelectionAlertTitle, noTrackingItemSelectionAlertHeader, noTrackingItemSelectionAlertContent);
        }

    }

    @FXML
    @SuppressWarnings("unused")
    private void deleteAction(ActionEvent event) throws SQLException, IOException {
        dataAction = DataAction.DELETE;
        if(selectedWorkItem != null) {
            DeleteWorkItemCommand cmd = new DeleteWorkItemCommand(controllerRepository, eventManager, trackingItemTableView, selectedWorkItem, workItemDao);
            undoService.execute(cmd);
        } else {
            ControllerUtilities.showNoItemSelectedAlert(primaryStage, rb, noTrackingItemSelectionAlertTitle, noTrackingItemSelectionAlertHeader, noTrackingItemSelectionAlertContent);
        }
    }
    
    @FXML
    @SuppressWarnings("unused")
    private void handleOnSelectedDateChangedAction(ActionEvent event) throws SQLException, IOException {
        DatePicker datePicker = (DatePicker)event.getSource();
        List<Workrecord> workRecords = workRecordDetailsViewController.getWorkrecordDao().selectAll(workRecordViewController.getSelectedUser(), datePicker.getValue());
        Optional<Workrecord> firstWorkrecord = workRecords.stream().findFirst();

        workItemData.clear();

        if(firstWorkrecord.isPresent()) {
            List<WorkItem> workItemsOfActualSelectedWorkrecord = workItemDao.selectAll(firstWorkrecord.get().getId());
            workItemData.addAll(workItemsOfActualSelectedWorkrecord);

            Optional<WorkItem> firstWorkItem = workItemData.stream().findFirst();
            if(firstWorkItem.isPresent()) {
                showTrackingItemDetails(firstWorkItem.get());
                trackingItemTableView.getSelectionModel().select(0);
            } else {
                showTrackingItemDetails(null);
            }
        }
    }
    
    @FXML
    @SuppressWarnings("unused")
    private void handleOnSetStartTimeButtonClickAction(ActionEvent event) {
        trackingItemStartTimeTimeSpinner.getValueFactory().setValue(LocalTime.now());
    }

    @FXML
    @SuppressWarnings("unused")
    private void handleOnSetEndTimeButtonClickAction(ActionEvent event) {
        trackingItemEndTimeTimeSpinner.getValueFactory().setValue(LocalTime.now());
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public void initialize(URL location, ResourceBundle rb) {
        this.rb = rb;
        
        workItemData.clear();
        trackingItemTableView.setItems(workItemData);

        initCellValueFactoryTableColumns();
        initStartTimeTimeSpinner();
        initEndTimeTimeSpinner();
        initListeners();
        initDatePickerBySelectedWorkrecord();
        initTrackingItemChoiceBox();
        
        try {
            selectedWorkrecord = workRecordDetailsViewController.getSelectedWorkrecord();
            if(selectedWorkrecord != null) {
                List<WorkItem> workItemsOfActualSelectedWorkrecord = workItemDao.selectAll(selectedWorkrecord.getId());
                workItemData.addAll(workItemsOfActualSelectedWorkrecord);
            }
        } catch (SQLException ex) {
            log.fatal("No WorkItemData could be loaded!");
        }
        
        Optional<WorkItem> firstWorkItem = workItemData.stream().findFirst();
        if(!firstWorkItem.isEmpty()) {
            showTrackingItemDetails(firstWorkItem.get());
            trackingItemTableView.getSelectionModel().select(0);
        } else {
            showTrackingItemDetails(null);
        }

        isInputValid();

        languageService.updateGuiItems();        
    }

    private void initCellValueFactoryTableColumns() {
        //HOWTO: Cell Value Factory
        //The cell must know which part of WorkItemTrackingData it needs to display.
        trackingItemShortcutTableColumn.setCellValueFactory(cellData -> cellData.getValue().getShortcutProperty());
        trackingItemNameTableColumn.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
        trackingItemStartTimeTableColumn.setCellValueFactory(cellData -> cellData.getValue().getStartTimeProperty());
        trackingItemEndTimeTableColumn.setCellValueFactory(cellData -> cellData.getValue().getEndTimeProperty());
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

    private void initListeners() {
        selectedDateDatePicker.valueProperty().addListener((var observable, var oldValue, var newValue) -> {
            trySetSprintNumberLabel(newValue);
            eventManager.notifyListenerOfEvent(workItemDateChangedEvent, newValue);
            isInputValid();
        });
        sprintNumberLabelValue.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            try {
                newSprintId = Long.parseLong(newValue);
            } catch (NumberFormatException ex) {
                newSprintId = 0L;
            }
        });
        trackingItemTableView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends WorkItem> observable, WorkItem oldValue, WorkItem newValue) -> {
            if(newValue != null) {
                newWorkrecordId = newValue.getWorkrecordId();
            }
            showTrackingItemDetails(newValue);
            isInputValid();
        });
        trackingItemStartTimeTimeSpinner.valueProperty().addListener((ObservableValue<? extends LocalTime> observable, LocalTime oldValue, LocalTime newValue) -> {
            newStartTime = newValue;
            isInputValid();
        });
        trackingItemEndTimeTimeSpinner.valueProperty().addListener((ObservableValue<? extends LocalTime> observable, LocalTime oldValue, LocalTime newValue) -> {
            newEndTime = newValue;
            isInputValid();
        });
        trackingItemChoiceBox.valueProperty().addListener((ObservableValue<? extends TrackingItem> observable, TrackingItem oldValue, TrackingItem newValue) ->  {
            if(newValue != null) {
                newTrackingItemId = newValue.getId();
                newShortcut = newValue.getShortcut();
                newName = newValue.getName();
            }
            isInputValid();
        });
        trackingItemDescriptionValue.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            newDescription = newValue;
            isInputValid();
        });
    }

    private void trySetSprintNumberLabel(LocalDate date) {
            try {
                sprint = sprintDAO.selectSprintForDate(date);
                if(sprint == null) {
                    sprintNumberLabelValue.setText(rb.getString(sprintNotFoundResourceKey));
                    sprintNumberLabelValue.setStyle("-fx-text-fill: " + COLOR_LIGHT_RED + ";");
                    log.info("Sprint for date " + date + " not found, please update sprint referencedata!");                
                } else {
                    sprintNumberLabelValue.setText(sprint.getId().toString());
                    sprintNumberLabelValue.setStyle("");
                }
            } catch (SQLException ex) {
                log.info("Sprint for date " + date + " not found, please update sprint referencedata!");                
            }
    }

    private void initDatePickerBySelectedWorkrecord() {
        selectedWorkrecord = workRecordDetailsViewController.getSelectedWorkrecord();
        if(selectedWorkrecord != null) {
            selectedDateDatePicker.setValue(selectedWorkrecord.getDate());
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
        } catch (SQLException ex) {
            log.fatal("No TrackingItems could be loaded!");
        }
    }

    private boolean isSelectedWorkrecordValid() {
        return selectedWorkrecord != null && !selectedWorkrecord.getWorktime().equals(LocalTime.MIN);
    }
    
    // <editor-fold defaultstate="collapsed" desc="Input Validation Rules">
    private boolean isInputValid() {
        boolean result = false;
        
        if(dataAction != null) {
            switch(dataAction) {
                case DataAction.NEW -> {
                    boolean r1 = isInputFilled();
                    boolean r2 = isInputUnique();
                    boolean r3 = isSelectedWorkrecordValid();

                    result = r1 && r2 && r3;
                }
                case DataAction.EDIT -> {
                    boolean r1 = isInputFilled();
                    boolean r2 = isInputUnique();
                    boolean r3 = hasInputChanged();

                    result = r1 && r2 && r3;       
                }
                case DataAction.DELETE -> {
                    result = true;
                }
            }

            if(result) {
                newButton.setDisable(false);
                editButton.setDisable(false);
                deleteButton.setDisable(false);
            } else {
                newButton.setDisable(true);
                editButton.setDisable(true);
                deleteButton.setDisable(false); // DELETE should always be enabled if item selected
            }
        }
        return result;
    }
    
    private boolean isInputFilled() {
        return trackingItemChoiceBox.getSelectionModel().getSelectedItem() != null
        && trackingItemStartTimeTimeSpinner.getValue() != null
        && trackingItemEndTimeTimeSpinner.getValue() != null
        && !trackingItemDescriptionValue.getText().isBlank();        
    }

    private boolean isInputUnique() {
        boolean result = true;
        
        return result;
    }

    private boolean hasInputChanged() {
        return newId != oldId
        || newWorkrecordId != oldWorkrecordId
        || newSprintId != oldSprintId     
        || newTrackingItemId != oldTrackingItemId
        || !Objects.equals(newStartTime, oldStartTime)
        || !Objects.equals(newEndTime, oldEndTime)
        || !Objects.equals(newDescription, oldDescription)
        || !Objects.equals(newShortcut, oldShortcut)
        || !Objects.equals(newName, oldName);    
    }
    
    @Override
    public void updateGuiItems() {
        selectedDateLabel.setText(rb.getString(trackingItemDateResourceKey));
        sprintLabel.setText(rb.getString(trackingItemSprintResourceKey));
        sprintNumberLabelValue.setText(rb.getString(sprintNotFoundResourceKey));
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
            mainToolBarViewController.getWorkItemButton().disableProperty().set(false);
        }
    }
    
    @Override
    public void update(String eventType, Object source) {
        switch (eventType) {
            case newTrackingItemEvent, editTrackingItemEvent, deleteTrackingItemEvent -> {
                initTrackingItemChoiceBox();
            }
            case selectedWorkRecordChangedEvent -> {
                selectedWorkrecord = (Workrecord)source;
                if(selectedWorkrecord != null) {
                    selectedDateDatePicker.setValue(selectedWorkrecord.getDate());
                }
            }
        }
    }
    
    public EventManager getEventManager() {
        return eventManager;
    }

    private void showTrackingItemDetails(WorkItem workItem) {
        if(workItem != null) {
            //We save the actual workitem information to be able to 
            //check for changes of each Information at validation of Innput.
            saveActualWorkItemInformation(workItem);

            selectedWorkItem = workItem;
            ObservableList<TrackingItem> items = trackingItemChoiceBox.getItems();
            items.stream()
                    .filter(item -> item.getName().equals(workItem.getName()))
                    .findFirst()
                    .ifPresent(trackingItemChoiceBox.getSelectionModel()::select);
            trackingItemStartTimeTimeSpinner.getValueFactory().setValue(workItem.getStartTime());
            trackingItemEndTimeTimeSpinner.getValueFactory().setValue(workItem.getEndTime());
            trackingItemDescriptionValue.setText(workItem.getDescription());
        } else {
            trackingItemChoiceBox.getSelectionModel().select(0);
            trackingItemStartTimeTimeSpinner.getValueFactory().setValue(LocalTime.MIN);
            trackingItemEndTimeTimeSpinner.getValueFactory().setValue(LocalTime.MIN);
            trackingItemDescriptionValue.setText("");
        }
    }

    private void saveActualWorkItemInformation(WorkItem workItem) {
        oldId = workItem.getId();
        oldWorkrecordId = workItem.getWorkrecordId();
        oldSprintId = workItem.getSprintId();
        oldTrackingItemId = workItem.getTrackingItemId();
        oldStartTime = workItem.getStartTime();
        oldEndTime = workItem.getEndTime();
        oldDescription = workItem.getDescription();
        oldShortcut = workItem.getShortcut();
        oldName = workItem.getName();
    }
    
}
