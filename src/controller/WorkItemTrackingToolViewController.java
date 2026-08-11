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
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Sprint;
import model.TrackingItem;
import model.Workrecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.LanguageService;
import service.UndoService;
import sqlite.SprintDAO;
import sqlite.TrackingItemDAO;
import utils.EventManager;

/**
 *
 * @author adrest18
 */
class WorkItemTrackingToolViewController implements Initializable, IViewController {

    private static final String COLOR_LIGHT_RED = "Red";

    private final String timeNowIcon = "icons/timeNow.png";
    
    private final String newResourceKey = "New";
    private final String editResourceKey = "Edit";
    private final String deleteResourceKey = "Delete";
    
    private final String sprintNotFoundResourceKey = "SprintNotFound";
    private final String trackingItemShortcutResourceKey = "TrackingItemShortcut";
    private final String trackingItemNameResourceKey = "TrackingItemName";
    private final String trackingItemStartTimeResourceKey  ="TrackingItemStartTime";
    private final String trackingItemEndTimeResourceKey  ="TrackingItemEndTime";
    private final String trackingItemDetailsHeaderResourceKey = "TrackingItemDetailsHeader";
    
    private final String startTimeButtonToolTipResourceKey = "StartTimeButtonToolTip";
    private final String endTimeButtonToolTipResourceKey = "EndTimeButtonToolTip";
    
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
    private TableView trackingItemTableView;
    @FXML
    private TableColumn trackingItemShortcutTableColumn;
    @FXML
    private TableColumn trackingItemNameTableColumn;
    @FXML
    private TableColumn trackingItemStartTimeTableColumn;
    @FXML
    private TableColumn trackingItemEndTimeTableColumn;
    @FXML
    private GridPane trackingItemDetailsGridPane;
    @FXML
    private Label trackingItemDetailsHeaderLabel;
    @FXML
    private Label trackingItemLabel;
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

    private final SprintDAO sprintDAO;
    private final TrackingItemDAO trackingItemDAO;
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
        this.workRecordDetailsViewController = (WorkRecordDetailsViewController)controllerRepository.get(WorkRecordDetailsViewController.class.getName());
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
        
        selectedDateDatePicker.valueProperty().addListener((ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) -> {
            trySetSprintNumberLabel(newValue);
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
            trackingItemChoiceBox.getSelectionModel().select(0);
        } catch (SQLException ex) {
            log.fatal(ex.getMessage());
        }
        
    }

    @Override
    public void updateGuiItems() {
        trackingItemStartTimeButton.setTooltip(new Tooltip(rb.getString(startTimeButtonToolTipResourceKey)));
        trackingItemEndTimeButton.setTooltip(new Tooltip(rb.getString(endTimeButtonToolTipResourceKey)));
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
    
    private void trySetSprintNumberLabel(LocalDate date) {
            try {
                Sprint sprint = sprintDAO.selectSprintForDate(date);
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
