/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import commands.workrecord.*;
import controls.DurationSpinner;
import controls.LocalTimeSpinner;
import java.sql.*;
import java.net.URL;
import java.time.*;
import java.time.format.*;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.*;
import org.apache.logging.log4j.*;
import service.*;
import sqlite.*;
import utils.*;

/**
 *
 * @author adrest18
 */
@SuppressWarnings("unused")
public class WorkRecordDetailsViewController implements Initializable, IViewController, IEventListener {
        
    private final String todayIcon = "icons/today.png";
    private final String timeNowIcon = "icons/timeNow.png";
    
    private final String workrecordDetailsHeaderResourceKey = "WorkrecordDetailsHeader";
    private final String dateResourceKey = "Date";
    private final String startTimeResourceKey = "StartTime";
    private final String endTimeResourceKey = "EndTime";
    private final String workTimeResourceKey = "WorkTime";
    private final String overTimeResourceKey = "OverTime";
    private final String overallOverTimeCorrectionResourceKey = "OverallOverTimeCorrection";
    private final String overallVacationCorrectionResourceKey = "OverallVacationCorrection";
    private final String locationResourceKey = "Location";
    private final String projectResourceKey = "Project";
    private final String descriptionResourceKey = "Description";
    private final String newResourceKey = "New";
    private final String editResourceKey = "Edit";
    private final String deleteResourceKey = "Delete";
    private final String noWorkrecordSelectionAlertTitle = "NoSelectionAlertTitle";
    private final String noWorkrecordSelectionAlertHeader = "NoWorkrecordSelectionAlertHeader";
    private final String noWorkrecordSelectionAlertContent = "NoWorkrecordSelectionAlertContent";
    private final String todayButtonToolTipResourceKey = "TodayButtonToolTip";
    private final String startTimeButtonToolTipResourceKey = "StartTimeButtonToolTip";
    private final String selectedComptimeResourceKey = "SelectedComptimeInfo";
            
    private final String defaultWorkrecordStartTimeDelta = LocalTime.MIN.toString();
    private final String defaultWorkrecordEndTimeDelta = LocalTime.MIN.toString();
    
    private final String newWorkrecordEvent = "NewWorkrecord";
    private final String editWorkrecordEvent = "EditWorkrecord";
    private final String deleteWorkrecordEvent = "DeleteWorkrecord";
    
    private final String selectedWorkRecordChangedEvent = "SelectedWorkRecordChanged";
    
    private final String newProjectEvent = "NewProject";
    private final String editProjectEvent = "EditProject";
    private final String deleteProjectEvent = "DeleteProject";
    
    private final String newWorkLocationEvent = "NewWorkLocation";
    private final String editWorkLocationEvent = "EditWorkLocation";
    private final String deleteWorkLocationEvent = "DeleteWorkLocation";
    
    private final String newContractEvent = "NewContract";
    private final String editContractEvent = "EditContract";
    private final String deleteContractEvent = "DeleteContract";    
    
    private final String userChangedEvent = "UserChanged";
    
    private final String workRecordDetailsDateChangedEvent = "WorkRecordDetailsDateChanged";

    private final String workrecordAutomaticCreationChangeEvent = "WorkrecordAutomaticCreationChangeEvent";
    private final String defaultProjectChangedEvent = "DefaultProjectChanged";
    private final String defaultLocationChangedEvent = "DefaultLocationChanged";
    private final String useLastWorkrecordConfigurationChangedEvent = "UseLastWorkrecordConfigurationChanged";

    private final String workItemTrackingDateChangedEvent = "WorkItemTrackingDateChanged";
    
    private final Logger log = LogManager.getLogger(WorkRecordDetailsViewController.class.getName());
    
    private Stage primaryStage;    
    private final LanguageService languageService;
    private final Connection connection;
    private final UndoService undoService;
    private final PropertiesService propertiesService;
    private ResourceBundle rb;
    private final WorkRecordViewController workRecordViewController;
    private final WorkrecordDAO workrecordDao;
    private Workrecord selectedWorkrecord;
    private final ControllerRepository controllerRepository;
    private final TableView<Workrecord> workrecordTableView;
    private final WorklocationDAO worklocationDao;
    private final ProjectDAO projectDao;
    private LocalDate workrecordDate;
    private LocalTime newWorkrecordStartTime;
    private LocalTime newWorkrecordEndTime;
    private Duration newWorkrecordWorkTime;
    private Duration newWorkrecordOverTime;
    private LocalTimeSpinner workrecordStartTimeTimeSpinner;
    private LocalTimeSpinner workrecordEndTimeTimeSpinner;
    private Long contractWorkhours;
    private LocalTime contractBreakfastOfftimeStart;
    private LocalTime contractBreakfastOfftimeEnd;
    private LocalTime contractLunchOfftimeStart;
    private LocalTime contractLunchOfftimeEnd;
    public EventManager eventManager;
    private DurationSpinner workrecordOverTimeCorrectionValue;
    private Spinner<Integer> workrecordVacationCorrectionValue;
    
    @FXML
    private GridPane workrecordDetailsGridPane;
    @FXML
    private Label workrecordDetailsHeaderLabel;
    @FXML
    private Label workrecordDateLabel;
    @FXML
    private Label workrecordDateValue;
    @FXML
    private Button workrecordTodayButton;
    @FXML
    private Button workrecordStartTimeButton;
    @FXML
    private Label workrecordStartTimeLabel;
    @FXML
    private Label workrecordEndTimeLabel;
    @FXML
    private Label workrecordWorkTimeLabel;
    @FXML
    private Label workrecordWorkTimeValue;
    @FXML
    private Label workrecordOverTimeLabel;
    @FXML
    private Label workrecordOverTimeValue;
    @FXML
    private Label workrecordOverTimeCorrectionLabel;
    @FXML
    private Label workrecordVacationCorrectionLabel;
    @FXML
    private Label workrecordLocationLabel;
    @FXML 
    private ChoiceBox<Worklocation> workrecordLocationChoiceBox;
    @FXML
    private Label workrecordProjectLabel;
    @FXML 
    private ChoiceBox<Project> workrecordProjectChoiceBox;
    @FXML
    private Label workrecordDescriptionLabel;
    @FXML
    private TextArea workrecordDescriptionValue;
    @FXML 
    private Button newButton;
    @FXML 
    private Button editButton;
    @FXML
    private Button deleteButton;

    public WorkRecordDetailsViewController(ControllerRepository controllerRepository, LanguageService languageService, Connection connection, UndoService undoService, PropertiesService propertiesService) {
        if(controllerRepository == null) throw new NullPointerException("controllerRepository");
        if(languageService == null) throw new NullPointerException("languageService");
        if(connection == null) throw new NullPointerException("connection");
        if(undoService == null) throw new NullPointerException("undoService");
        if(propertiesService == null) throw new NullPointerException("propertiesService");
        this.workRecordViewController = (WorkRecordViewController) controllerRepository.get(WorkRecordViewController.class.getName());
        this.workrecordTableView = this.workRecordViewController.getWorkrecordTableView();
        
        this.languageService = languageService;
        this.connection = connection;        
        this.undoService = undoService;
        this.propertiesService = propertiesService;
        this.workrecordDao = new WorkrecordDAO(connection);
        this.controllerRepository = ControllerRepository.getInstance();
        this.worklocationDao = new WorklocationDAO(connection);
        this.projectDao = new ProjectDAO(connection);
        this.eventManager = new EventManager();
        this.eventManager.registerEventType(workRecordDetailsDateChangedEvent);
        this.eventManager.registerEventType(newWorkrecordEvent);
        this.eventManager.registerEventType(editWorkrecordEvent);
        this.eventManager.registerEventType(deleteWorkrecordEvent);
        this.eventManager.registerEventType(workItemTrackingDateChangedEvent);
    }

    @FXML
    private void newAction(ActionEvent event) throws SQLException {
        Workrecord newWorkrecord = new Workrecord(workrecordDao.getNextId());
        newWorkrecord.setUser(workRecordViewController.getSelectedUser());
        newWorkrecord.setDate(workrecordDate == null? LocalDate.now(): workrecordDate);
        newWorkrecord.setStarttime(newWorkrecordStartTime);
        newWorkrecord.setEndtime(newWorkrecordEndTime);
        newWorkrecord.setWorktime(DurationConverter.convertDurationToLocalTime(newWorkrecordWorkTime));
        Project project = workrecordProjectChoiceBox.getSelectionModel().getSelectedItem();
        newWorkrecord.setProject(project);
        if("true".equalsIgnoreCase(project.getIsVacationRelevant())) {
            newWorkrecord.setOvertime(DurationConverter.convertDurationToSignedStringOfHoursAndMinutes(Duration.ZERO));        
        } else {
            newWorkrecord.setOvertime(DurationConverter.convertDurationToSignedStringOfHoursAndMinutes(newWorkrecordOverTime));        
        }
        newWorkrecord.setOvertimecorrection(getOvertimecorrectionOrDefault());
        newWorkrecord.setVacationcorrection(workrecordVacationCorrectionValue.getValue());
        newWorkrecord.setWorklocation((Worklocation)workrecordLocationChoiceBox.getSelectionModel().getSelectedItem());        
        newWorkrecord.setDescription(workrecordDescriptionValue.getText());
        if(isInputFilled()) {
            NewWorkrecordCommand cmd = new NewWorkrecordCommand(controllerRepository, eventManager, workrecordTableView, newWorkrecord, workrecordDao);
            undoService.execute(cmd);
        }
    }
    
    @FXML
    private void editAction(ActionEvent event) throws SQLException {
        if(selectedWorkrecord != null && !workRecordViewController.IsDummyWorkrecord(selectedWorkrecord)) {
            Workrecord modifiedWorkrecord = new Workrecord(selectedWorkrecord.getId());
            modifiedWorkrecord.setUser(workRecordViewController.getSelectedUser());
            modifiedWorkrecord.setDate(workrecordDate);
            modifiedWorkrecord.setStarttime(newWorkrecordStartTime);
            modifiedWorkrecord.setEndtime(newWorkrecordEndTime);
            modifiedWorkrecord.setWorktime(DurationConverter.convertDurationToLocalTime(newWorkrecordWorkTime));
            Project project = workrecordProjectChoiceBox.getSelectionModel().getSelectedItem();
            modifiedWorkrecord.setProject(project);
            if("true".equalsIgnoreCase(project.getIsVacationRelevant())) {
                modifiedWorkrecord.setOvertime(DurationConverter.convertDurationToSignedStringOfHoursAndMinutes(Duration.ZERO));        
            } else {
                modifiedWorkrecord.setOvertime(DurationConverter.convertDurationToSignedStringOfHoursAndMinutes(newWorkrecordOverTime));        
            }
            modifiedWorkrecord.setOvertimecorrection(getOvertimecorrectionOrDefault());
            modifiedWorkrecord.setVacationcorrection(workrecordVacationCorrectionValue.getValue());
            modifiedWorkrecord.setWorklocation((Worklocation)workrecordLocationChoiceBox.getSelectionModel().getSelectedItem());
            modifiedWorkrecord.setProject(workrecordProjectChoiceBox.getSelectionModel().getSelectedItem());
            modifiedWorkrecord.setDescription(workrecordDescriptionValue.getText());
            if(!selectedWorkrecord.equals(modifiedWorkrecord)) {
                EditWorkrecordCommand cmd = new EditWorkrecordCommand(controllerRepository, eventManager, workrecordTableView, selectedWorkrecord, modifiedWorkrecord, workrecordDao);
                undoService.execute(cmd);
            }
        } else {
            ControllerUtilities.showNoItemSelectedAlert(primaryStage, rb, noWorkrecordSelectionAlertTitle, noWorkrecordSelectionAlertHeader, noWorkrecordSelectionAlertContent);
        }
    }
    
    @FXML
    private void deleteAction(ActionEvent event) {
        if(selectedWorkrecord != null && !workRecordViewController.IsDummyWorkrecord(selectedWorkrecord)) {
            DeleteWorkrecordCommand cmd = new DeleteWorkrecordCommand(controllerRepository, eventManager, workrecordTableView, selectedWorkrecord, workrecordDao);
            undoService.execute(cmd);
        } else {
            ControllerUtilities.showNoItemSelectedAlert(primaryStage, rb, noWorkrecordSelectionAlertTitle, noWorkrecordSelectionAlertHeader, noWorkrecordSelectionAlertContent);
        }
    }
    
    @FXML
    private void handleOnSetTodayButtonClickAction(ActionEvent event) {
        handleSetTodayButtonClickAction(event);
    }

    @FXML
    private void handleOnSetStartTimeButtonClickAction(ActionEvent event) throws SQLException {
        handleSetStartTimeButtonClickAction(event);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.rb = rb;
                
        workrecordTodayButton.setGraphic(new ImageView(todayIcon));        
        workrecordStartTimeButton.setGraphic(new ImageView(timeNowIcon));
       
        User selectedUser = workRecordViewController.getSelectedUser();
        if(selectedUser != null) {
            Contract contract = selectedUser.getContract();
            if(contract != null) {
                setContractRelevantInformations(contract);
            }
        }
        
        workrecordStartTimeTimeSpinner = new LocalTimeSpinner();
        workrecordEndTimeTimeSpinner = new LocalTimeSpinner();
        workrecordOverTimeCorrectionValue = new DurationSpinner();
        workrecordVacationCorrectionValue = new Spinner<>(-30, 30, 0, 1);
        
        workrecordDetailsGridPane.add(workrecordStartTimeTimeSpinner, 2, 2);
        workrecordDetailsGridPane.add(workrecordEndTimeTimeSpinner, 2, 3);
        workrecordDetailsGridPane.add(workrecordOverTimeCorrectionValue, 2, 6);
        workrecordDetailsGridPane.add(workrecordVacationCorrectionValue, 2, 7);
        
        newWorkrecordStartTime = workrecordStartTimeTimeSpinner.getValue();
        newWorkrecordEndTime = workrecordEndTimeTimeSpinner.getValue();
        
        workrecordStartTimeTimeSpinner.valueProperty().addListener((ObservableValue<? extends LocalTime> observable, LocalTime oldValue, LocalTime newValue) -> {
            if(selectedUser != null) {
                calculateWorkTimeWithBreakfastAndLunchCorrection();
                calculateOverTimeWithBreakfastAndLunchCorrection();
                IsInputValid();
            }
        });
        workrecordEndTimeTimeSpinner.valueProperty().addListener((ObservableValue<? extends LocalTime> observable, LocalTime oldValue, LocalTime newValue) -> {
            if(selectedUser != null) {
                calculateWorkTimeWithBreakfastAndLunchCorrection();
                calculateOverTimeWithBreakfastAndLunchCorrection();
                IsInputValid();
            }
        });
        workrecordEndTimeTimeSpinner.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if(workrecordEndTimeTimeSpinner.getValue() == LocalTime.MIN) {
                if(contractWorkhours == null) {
                    log.warn("Endtime can not be calculated as no workhours available, please select a user");
                    return;
                }
                long breakfastTimeInMinutes = convertDifferenceToMinutes(contractBreakfastOfftimeStart, contractBreakfastOfftimeEnd);
                long lunchTimeInMinutes = convertDifferenceToMinutes(contractLunchOfftimeStart, contractLunchOfftimeEnd);
                workrecordEndTimeTimeSpinner.getValueFactory().setValue(workrecordStartTimeTimeSpinner.getValue().plusHours(contractWorkhours).plusMinutes(breakfastTimeInMinutes + lunchTimeInMinutes));
            }
        });
        workrecordOverTimeCorrectionValue.valueProperty().addListener((ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) -> {
            if(selectedUser != null) {
                DurationStyler.styleSpinner(workrecordOverTimeCorrectionValue, newValue);
                IsInputValid();
            }
        });
        workrecordVacationCorrectionValue.getEditor().textProperty().addListener((ObservableValue<? extends String> obs, String oldValue, String newValue) -> {
            if(selectedUser != null) {
                TimeStyler.styleSpinner(workrecordVacationCorrectionValue, newValue);
                IsInputValid();
            }
        });
        workrecordLocationChoiceBox.valueProperty().addListener((ObservableValue<? extends Worklocation> obs, Worklocation oldValue, Worklocation newValue) ->  {
            if(selectedUser != null) {
                IsInputValid();
            }
        });
        workrecordProjectChoiceBox.valueProperty().addListener((ObservableValue<? extends Project> obs, Project oldValue, Project newValue) ->  {
            if(selectedUser != null) {
                if((newValue).getIsComptimeRelevant().equalsIgnoreCase("true")) {
                    setWorkrecordLocationChoiceBox("Out of Office");
                    workrecordStartTimeTimeSpinner.getValueFactory().setValue(LocalTime.of(7, 0));
                    workrecordEndTimeTimeSpinner.getValueFactory().setValue(LocalTime.of(14, 45));
                    String maxWorkhours = String.valueOf(selectedUser.getContract().getWorkhours()*(-1));
                    workrecordOverTimeCorrectionValue.getValueFactory().setValue(DurationConverter.convertSignedStringOfHoursAndMinutesToDuration(maxWorkhours));
                    if(workrecordDescriptionValue.textProperty().getValue().isEmpty()) {
                        workrecordDescriptionValue.textProperty().setValue(rb.getString(selectedComptimeResourceKey));
                    }
                } else if((newValue).getIsVacationRelevant().equalsIgnoreCase("true")) {
                    setWorkrecordLocationChoiceBox("Out of Office");
                    workrecordStartTimeTimeSpinner.getValueFactory().setValue(LocalTime.MIN);
                    workrecordEndTimeTimeSpinner.getValueFactory().setValue(LocalTime.MIN);
                    workrecordOverTimeValue.setText(DurationConverter.convertDurationToSignedStringOfHoursAndMinutes(Duration.ZERO));
                    workrecordOverTimeValue.setStyle("");
                    if(!workrecordDescriptionValue.textProperty().getValue().isEmpty()) {
                        workrecordDescriptionValue.textProperty().setValue("");
                    }
                } else {
                    workrecordOverTimeCorrectionValue.getValueFactory().setValue(Duration.ZERO);
                    if(!workrecordDescriptionValue.textProperty().getValue().isEmpty()) {
                        workrecordDescriptionValue.textProperty().setValue("");
                    }
                }                
                IsInputValid();
            }
        });
        workrecordDescriptionValue.textProperty().addListener(event ->  {
            if(selectedUser != null) {
                IsInputValid();
            }
        });

        try {
            workrecordLocationChoiceBox.getItems().addAll(worklocationDao.selectAll());
            workrecordProjectChoiceBox.getItems().addAll(projectDao.selectAll());
            setLocationOrDefault(null);
            setProjectOrDefault(null);
            showWorkrecordDetails(null);
        } catch (SQLException ex) {
            log.fatal(ex.getMessage());
        }

        if(selectedUser != null) {        
            calculateWorkTimeWithBreakfastAndLunchCorrection();
            calculateOverTimeWithBreakfastAndLunchCorrection();
        }
        
        languageService.updateGuiItems();        
    }

    private boolean setWorkrecordLocationChoiceBox(String worklocation) {
        try {
            List<Worklocation> worklocations = worklocationDao.selectAll();
            Optional<Worklocation> result = worklocations.stream().filter(x -> x.getName().equalsIgnoreCase(worklocation)).findFirst();
            if(result.isPresent()) {
                workrecordLocationChoiceBox.setValue(result.get());
                return true;
            }
            return false;
        } catch (SQLException ex) {
            log.fatal(ex.getMessage());
            return false;
        }
        
    }
    
    public void createWorkrecordAutomatically() throws SQLException, NumberFormatException {        
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        LocalTime nowHHMM = LocalTime.of(now.getHour(), now.getMinute());
        
        workRecordViewController.selectWorkrecordOf(today);
        
        String startTimeDeltaValue = propertiesService.getProperty("WorkrecordStartTimeDelta", defaultWorkrecordStartTimeDelta);
        long startTimeDeltaValueAsLong = TimeConverter.hoursAndMinutesToLong(startTimeDeltaValue);
        String endTimeDeltaValue = propertiesService.getProperty("WorkrecordEndTimeDelta", defaultWorkrecordEndTimeDelta);
        long endTimeDeltaValueAsLong = TimeConverter.hoursAndMinutesToLong(endTimeDeltaValue);
        
        LocalTime startTime = nowHHMM.plusMinutes(startTimeDeltaValueAsLong);
        workrecordStartTimeTimeSpinner.getValueFactory().setValue(LocalTime.of(startTime.getHour(), startTime.getMinute()));
        
        long breakfastTimeInMinutes = convertDifferenceToMinutes(contractBreakfastOfftimeStart, contractBreakfastOfftimeEnd);
        long lunchTimeInMinutes = convertDifferenceToMinutes(contractLunchOfftimeStart, contractLunchOfftimeEnd);
        
        LocalTime endTime = startTime.plusHours(contractWorkhours).plusMinutes(endTimeDeltaValueAsLong);
        if(nowHHMM.isBefore(contractBreakfastOfftimeEnd)) {
            endTime = endTime.plusMinutes(breakfastTimeInMinutes);
        }        
        if(nowHHMM.isBefore(contractLunchOfftimeEnd)) {
            endTime = endTime.plusMinutes(lunchTimeInMinutes);
        }
                
        workrecordEndTimeTimeSpinner.getValueFactory().setValue(endTime);
        calculateWorkTimeWithBreakfastAndLunchCorrection();
        calculateOverTimeWithBreakfastAndLunchCorrection();
        newAction(null);        
    }

    @Override
    public void updateGuiItems() {
        if(workrecordDate != null) {
            workrecordDateValue.setText(DateConverter.format(workrecordDate, FormatStyle.SHORT, rb.getLocale()));
        }
        
        workrecordDetailsHeaderLabel.setText(rb.getString(workrecordDetailsHeaderResourceKey));
        workrecordDateLabel.setText(rb.getString(dateResourceKey));
        workrecordStartTimeLabel.setText(rb.getString(startTimeResourceKey));
        workrecordEndTimeLabel.setText(rb.getString(endTimeResourceKey));
        workrecordWorkTimeLabel.setText(rb.getString(workTimeResourceKey));
        workrecordOverTimeLabel.setText(rb.getString(overTimeResourceKey));
        workrecordOverTimeCorrectionLabel.setText(rb.getString(overallOverTimeCorrectionResourceKey));
        workrecordVacationCorrectionLabel.setText(rb.getString(overallVacationCorrectionResourceKey));
        workrecordLocationLabel.setText(rb.getString(locationResourceKey));
        workrecordProjectLabel.setText(rb.getString(projectResourceKey));
        workrecordDescriptionLabel.setText(rb.getString(descriptionResourceKey));
        newButton.setText(rb.getString(newResourceKey));
        editButton.setText(rb.getString(editResourceKey));
        deleteButton.setText(rb.getString(deleteResourceKey));
        workrecordTodayButton.setTooltip(new Tooltip(rb.getString(todayButtonToolTipResourceKey)));
        workrecordStartTimeButton.setTooltip(new Tooltip(rb.getString(startTimeButtonToolTipResourceKey)));
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
                selectedWorkrecord = (Workrecord)source;
                if(selectedWorkrecord != null) {
                    showWorkrecordDetails(selectedWorkrecord);
                    IsInputValid();
                }
            }
            case newProjectEvent, editProjectEvent, deleteProjectEvent -> {
                try {
                    refreshWorkrecordProjectChoiceBox();
                } catch (SQLException ex) {
                    log.fatal("Project choicebox could not be refreshed");
                }
            }
            case newWorkLocationEvent, editWorkLocationEvent, deleteWorkLocationEvent -> {
                try {
                    refreshWorkrecordLocationChoiceBox();
                } catch (SQLException ex) {
                    log.fatal("Worklocation choicebox could not be refreshed");
                }
            }
            case newContractEvent, editContractEvent, deleteContractEvent -> {
                Contract contract = (Contract)source;
                setContractRelevantInformations(contract);
            }
            case userChangedEvent -> {
                User user = (User)source;
                Contract contract = user.getContract();                
                setContractRelevantInformations(contract);
                calculateWorkTimeWithBreakfastAndLunchCorrection();
                calculateOverTimeWithBreakfastAndLunchCorrection();
            }
            case defaultProjectChangedEvent -> {
                int defaultProjectIdx = (Integer)source;
                workrecordProjectChoiceBox.getSelectionModel().select(defaultProjectIdx);
            }
            case defaultLocationChangedEvent -> {
                int defaultWorklocationIdx = (Integer)source;
                workrecordLocationChoiceBox.getSelectionModel().select(defaultWorklocationIdx);
            }
            case useLastWorkrecordConfigurationChangedEvent -> {
                setProjectOrDefault(null);
                setLocationOrDefault(null);
            }
            case workrecordAutomaticCreationChangeEvent -> {
                boolean workrecordAutomaticCreation = (Boolean)source;
                //Actually we do not need dynamic behaviour!
            }
            case workItemTrackingDateChangedEvent -> {
                LocalDate date = (LocalDate)source;
                workRecordViewController.selectWorkrecordOf(date);
            }
        }
    }

    public EventManager getEventManager() {
        return eventManager;
    }
    
    public Workrecord getSelectedWorkrecord() {
        return selectedWorkrecord;
    }

    public WorkrecordDAO getWorkrecordDao() {
        return workrecordDao;
    }
    
    
    private void calculateWorkTimeWithBreakfastAndLunchCorrection() {
        newWorkrecordStartTime = workrecordStartTimeTimeSpinner.getValue();
        newWorkrecordEndTime = workrecordEndTimeTimeSpinner.getValue();

        newWorkrecordWorkTime = Duration.between(newWorkrecordStartTime, newWorkrecordEndTime);
        newWorkrecordWorkTime = correctDurationForBreakfastAndLunchTime(newWorkrecordWorkTime);
        
        workrecordWorkTimeValue.setText(DurationConverter.convertDurationToSignedStringOfHoursAndMinutes(newWorkrecordWorkTime));
        TimeStyler.styleTimeLabel(workrecordWorkTimeValue, newWorkrecordWorkTime);
    }
    
    private void calculateOverTimeWithBreakfastAndLunchCorrection() {
        if(contractWorkhours == null) {
            log.warn("Overtime can not be calculated as no workhours available, please select a user");
            workrecordOverTimeValue.setText("N/A");
            return;
        }
        
        newWorkrecordStartTime = workrecordStartTimeTimeSpinner.getValue();
        newWorkrecordEndTime = workrecordEndTimeTimeSpinner.getValue();
        
        newWorkrecordOverTime = Duration.between(newWorkrecordStartTime, newWorkrecordEndTime);
        newWorkrecordOverTime = correctDurationForBreakfastAndLunchTime(newWorkrecordOverTime);
        newWorkrecordOverTime = newWorkrecordOverTime.minusHours(contractWorkhours);

        workrecordOverTimeValue.setText(DurationConverter.convertDurationToSignedStringOfHoursAndMinutes(newWorkrecordOverTime));
        TimeStyler.styleTimeLabel(workrecordOverTimeValue, newWorkrecordOverTime);
    }
        
    private Duration correctDurationForBreakfastAndLunchTime(Duration duration) {
        boolean inBreakfastTime = TimeChecker.isInBetween(contractBreakfastOfftimeStart, contractBreakfastOfftimeEnd, newWorkrecordEndTime);
        boolean inLunchTime = TimeChecker.isInBetween(contractLunchOfftimeStart, contractLunchOfftimeEnd, newWorkrecordEndTime);
        Duration breakfastTime = Duration.between(contractBreakfastOfftimeStart, contractBreakfastOfftimeEnd);
        Duration lunchTime = Duration.between(contractLunchOfftimeStart, contractLunchOfftimeEnd);
        
        if(inBreakfastTime || inLunchTime) {
            long workrecordStartTimeInMinutes = newWorkrecordStartTime.getHour() * 60 + newWorkrecordStartTime.getMinute();
            long correctionInMinutes = newWorkrecordEndTime.minusMinutes(workrecordStartTimeInMinutes).getMinute();
            duration = duration.minus(correctionInMinutes, ChronoUnit.MINUTES);
        } 
        if(newWorkrecordStartTime.isBefore(contractBreakfastOfftimeEnd) && newWorkrecordEndTime.plusMinutes(1).isAfter(contractBreakfastOfftimeEnd)) {
            duration = duration.minus(breakfastTime);
        }        
        if(newWorkrecordStartTime.isBefore(contractLunchOfftimeEnd) && newWorkrecordEndTime.plusMinutes(1).isAfter(contractLunchOfftimeEnd)) {
            duration = duration.minus(lunchTime);
        }
        return duration;
    } 
        
    private void showWorkrecordDetails(Workrecord workrecord) {
        if(workrecord != null) {
            workrecordDate = workrecord.getDate();
            workrecordDateValue.setText(DateConverter.format(workrecordDate, FormatStyle.SHORT, rb.getLocale()));
            workrecordStartTimeTimeSpinner.getValueFactory().setValue(workrecord.getStarttime());
            workrecordEndTimeTimeSpinner.getValueFactory().setValue(workrecord.getEndtime());
            workrecordWorkTimeValue.setText(workrecord.getWorktime().toString());
            workrecordOverTimeValue.setText(workrecord.getOvertime());
            if(!workrecord.getOvertimecorrection().equals(LocalTime.MIN.toString())) {
                workrecordOverTimeCorrectionValue.getValueFactory().setValue(DurationConverter.convertSignedStringOfHoursAndMinutesToDuration(workrecord.getOvertimecorrection()));
            } else {
                workrecordOverTimeCorrectionValue.getValueFactory().setValue(Duration.ZERO);
            }
            workrecordVacationCorrectionValue.getValueFactory().setValue(workrecord.getVacationcorrection());
            setLocationOrDefault(workrecord.getWorklocation());
            setProjectOrDefault(workrecord.getProject());
            workrecordDescriptionValue.setText(workrecord.getDescription());
        } else {
            workrecordWorkTimeValue.setText(LocalTime.MIN.toString());
            workrecordOverTimeValue.setText(LocalTime.MIN.toString());
        }
        TimeStyler.styleTimeLabel(workrecordWorkTimeValue, workrecordWorkTimeValue.getText());
        TimeStyler.styleTimeLabel(workrecordOverTimeValue, workrecordOverTimeValue.getText());
    }
    
    private void refreshWorkrecordProjectChoiceBox() throws SQLException {
        Project selectedProject = (Project)workrecordProjectChoiceBox.getSelectionModel().getSelectedItem();
        workrecordProjectChoiceBox.getItems().clear();
        workrecordProjectChoiceBox.getItems().addAll(projectDao.selectAll());
        setProjectOrDefault(selectedProject);
    }

    private void refreshWorkrecordLocationChoiceBox() throws SQLException {
        Worklocation selectedWorklocation = (Worklocation)workrecordLocationChoiceBox.getSelectionModel().getSelectedItem();
        workrecordLocationChoiceBox.getItems().clear();
        workrecordLocationChoiceBox.getItems().addAll(worklocationDao.selectAll());
        setLocationOrDefault(selectedWorklocation);
    }
    
    private void IsInputValid() {
        if(IsDummyWorkrecord()) {
            if(isInputFilled()) {
                newButton.setDisable(false);    // New enabled
            } else {
                newButton.setDisable(true);     // New disabled
            }
            editButton.setDisable(true);        // Edit disabled
            deleteButton.setDisable(true);      // Delete disabled
            return;
        }

        //we have selected a allready existing workrecord
        newButton.setDisable(true);             // New disabled
        deleteButton.setDisable(false);         // Delete enabled

        boolean workrecordModified = isWorkrecordModified();
        boolean inputFilled = isInputFilled();
        if(workrecordModified && inputFilled) {
            editButton.setDisable(false);       // Edit enabled
        } else {
            editButton.setDisable(true);        // Edit disabled
        }
    }

    private boolean IsDummyWorkrecord() {
        return workRecordViewController.IsDummyWorkrecord(selectedWorkrecord);
    }
    
    private boolean isWorkrecordModified() {
        boolean r1 = isStarttimeModified();
        boolean r2 = isEndtimeModified();
        boolean r3 = isOverTimeCorrectionModified();
        boolean r4 = isVacationCorrectionModified();
        boolean r5 = isWorkrecordLocationModified();
        boolean r6 = isProjectModified();
        boolean r7 = isDescriptionModified();
        
        return r1 || r2 || r3 || r4 || r5 || r6 || r7;
    }
    
    private boolean isStarttimeModified() {
        boolean isModified = false;
        LocalTime oldValue = selectedWorkrecord.getStarttime();
        LocalTime newValue = workrecordStartTimeTimeSpinner.getValue();
        if(newValue != null) {
            if(!oldValue.equals(newValue)) {
                isModified = true;
            }
        }
        return isModified;
    }
    
    private boolean isEndtimeModified() {
        boolean isModified = false;
        LocalTime oldValue = selectedWorkrecord.getEndtime();
        LocalTime newValue = workrecordEndTimeTimeSpinner.getValue();
        if(newValue != null) {
            if(!oldValue.equals(newValue)) {
                isModified = true;
            }
        }
        return isModified;
    }

    private boolean isOverTimeCorrectionModified() {
        boolean isModified = false;
        String oldValue = selectedWorkrecord.getOvertimecorrection();
        Duration duration = workrecordOverTimeCorrectionValue.getValueFactory().getValue();
        String newValue = DurationConverter.convertDurationToSignedStringOfHoursAndMinutes(duration);
        if(!ControllerUtilities.isNullOrEmpty(newValue)) {
            if(!oldValue.equals(newValue)) {
                isModified = true;
            }
        }
        return isModified;
    }

    private boolean isVacationCorrectionModified() {
        boolean isModified = false;
        Integer oldValue = selectedWorkrecord.getVacationcorrection();
        Integer newValue = workrecordVacationCorrectionValue.getValue();
        if(newValue != null) {
            if(!oldValue.equals(newValue)) {
                isModified = true;
            }
        }
        return isModified;
    }
    
    private boolean isWorkrecordLocationModified() {
        boolean result = false;
        if(!workrecordLocationChoiceBox.getSelectionModel().isEmpty() && selectedWorkrecord.getWorklocation() != null) {
            String selectedWorkrecordLocationName = selectedWorkrecord.getWorklocation().getName();
            String newLocationName = workrecordLocationChoiceBox.getSelectionModel().getSelectedItem().toString();
            result = !(selectedWorkrecordLocationName.equals(newLocationName)); 
        }
        return result;
    }

    private boolean isProjectModified() {
        boolean result = true;
        if(!workrecordProjectChoiceBox.getSelectionModel().isEmpty() && selectedWorkrecord.getProject() != null) {
            String selectedWorkrecordProjectName = selectedWorkrecord.getProject().getName();
            String newProjectName = workrecordProjectChoiceBox.getSelectionModel().getSelectedItem().toString();
            result = !(selectedWorkrecordProjectName.equals(newProjectName));    
        }
        return result;
    }

    private boolean isDescriptionModified() {
        boolean isModified = false;
        String oldValue = selectedWorkrecord.getDescription();
        String newValue = workrecordDescriptionValue.getText();
        if(!oldValue.equals(newValue)) {
            isModified = true;
        }
        return isModified;
    }

    private boolean isInputFilled() {
        boolean r1 = isWorkrecordStartTimeAndEndTimeSet();
        boolean r2 = isWorkRecordStartTimeBeforeOrEqualWorkrecordEndTime();
        boolean r3 = isWorkRecordWorkTimeZeroOrPositive();
        boolean r4 = isOvertimeCorrectionEmptyOrSet();
        boolean r5 = isWorkLocationSet();
        boolean r6 = isProjectSet();
        
        return  r1 && r2 && r3 && r4 && r5 && r6;                
    }
       
    private boolean isWorkrecordStartTimeAndEndTimeSet() {
        Project project = (Project)workrecordProjectChoiceBox.getSelectionModel().getSelectedItem();
        if(project != null) {
            String isVacationRelevant = project.getIsVacationRelevant();
            if("true".equalsIgnoreCase(isVacationRelevant)) {
                return true;
            }
        }
        return !(newWorkrecordStartTime.equals(LocalTime.MIN) && newWorkrecordEndTime.equals(LocalTime.MIN));
    }
    
    private boolean isWorkRecordStartTimeBeforeOrEqualWorkrecordEndTime() {
        return newWorkrecordStartTime.isBefore(newWorkrecordEndTime) || newWorkrecordStartTime.equals(newWorkrecordEndTime);
    }
    
    private boolean isWorkRecordWorkTimeZeroOrPositive() {
        if(newWorkrecordWorkTime == null) {
            return false;
        }
        return newWorkrecordWorkTime.isPositive() || newWorkrecordWorkTime.isZero();    
    }
    
    private boolean isOvertimeCorrectionEmptyOrSet() {
        String overtimeCorrection = DurationConverter.convertDurationToSignedStringOfHoursAndMinutes(workrecordOverTimeCorrectionValue.getValue());
        if(ControllerUtilities.isNullOrEmpty(overtimeCorrection)) {
            return true;
        }
        return overtimeCorrection.matches("^-?\\d{2}:\\d{2}$");
    }
    
    private boolean isWorkLocationSet() {
        return !workrecordLocationChoiceBox.getSelectionModel().isEmpty();
    }

    private boolean isProjectSet() {
        return !workrecordProjectChoiceBox.getSelectionModel().isEmpty();
    }
     
    private boolean isEndTimeBeforeOrEqualSelectedWorkrecordStartTime() {
        if(selectedWorkrecord == null) {
            return false;
        }
        LocalTime selectedWorkrecordStartTime = selectedWorkrecord.getStarttime();
        return newWorkrecordEndTime.isBefore(selectedWorkrecordStartTime) ||
                newWorkrecordEndTime.equals(selectedWorkrecordStartTime);
    }
        
    private boolean isStartTimeAfterOrEqualSelectedWorkrecordEndTime() {
        if(selectedWorkrecord == null) {
            return false;
        }
        LocalTime selectedWorkrecordEndTime = selectedWorkrecord.getEndtime();
        return newWorkrecordStartTime.isAfter(selectedWorkrecordEndTime) ||
                newWorkrecordStartTime.equals(selectedWorkrecordEndTime);
    }
    
    private void handleSetTodayButtonClickAction(ActionEvent event) {
        LocalDate date = LocalDate.now();
        workRecordViewController.selectWorkrecordOf(date);
    }

    private void handleSetStartTimeButtonClickAction(ActionEvent event) {
        if(contractBreakfastOfftimeStart == null || contractBreakfastOfftimeEnd == null) {
            return;
        }

        LocalTime now = LocalTime.now();
        workrecordStartTimeTimeSpinner.getValueFactory().setValue(LocalTime.of(now.getHour(), now.getMinute()));
        
        long breakfastTimeInMinutes = convertDifferenceToMinutes(contractBreakfastOfftimeStart, contractBreakfastOfftimeEnd);
        long lunchTimeInMinutes = convertDifferenceToMinutes(contractLunchOfftimeStart, contractLunchOfftimeEnd);
        
        LocalTime worktime = workrecordStartTimeTimeSpinner.getValue().plusHours(contractWorkhours);
        if(workrecordStartTimeTimeSpinner.getValue().isBefore(contractBreakfastOfftimeEnd)) {
            worktime = worktime.plusMinutes(breakfastTimeInMinutes);
        }        
        if(workrecordStartTimeTimeSpinner.getValue().isBefore(contractLunchOfftimeEnd)) {
            worktime = worktime.plusMinutes(lunchTimeInMinutes);
        }
        
        workrecordEndTimeTimeSpinner.getValueFactory().setValue(worktime);
        calculateWorkTimeWithBreakfastAndLunchCorrection();
        calculateOverTimeWithBreakfastAndLunchCorrection();
    }

    private long convertDifferenceToMinutes(LocalTime start, LocalTime end) {
        long startInMinutes = start.getHour() * 60 + start.getMinute();
        long endInMinutes = end.getHour() * 60 + end.getMinute();
        return endInMinutes - startInMinutes;
    }
    
    private void setContractRelevantInformations(Contract contract) {
        contractWorkhours = contract.getWorkhours();
        contractBreakfastOfftimeStart = contract.getBreakfastofftimestart();
        contractBreakfastOfftimeEnd = contract.getBreakfastofftimeend();
        contractLunchOfftimeStart = contract.getLunchofftimestart();
        contractLunchOfftimeEnd = contract.getLunchofftimeend();
    }

    private String getOvertimecorrectionOrDefault() {
        String workrecordOverTimeCorrection = DurationConverter.convertDurationToSignedStringOfHoursAndMinutes(workrecordOverTimeCorrectionValue.getValue());
        if(ControllerUtilities.isNullOrEmpty(workrecordOverTimeCorrection)) {
            return LocalTime.MIN.toString();
        }
        return workrecordOverTimeCorrection;
    }

    private void setProjectOrDefault(Project selectedProject) {
        if(selectedProject == null || selectedProject.getName().isEmpty()) {
            boolean useLastWorkrecordConfiguration = Boolean.parseBoolean(propertiesService.getProperty("UseLastWorkrecordConfiguration", "false"));
            if(useLastWorkrecordConfiguration) {
                int defaultProjectIdx = Integer.parseInt(propertiesService.getProperty("DefaultProjectIndex", "0"));
                workrecordProjectChoiceBox.getSelectionModel().select(defaultProjectIdx);
                return;
            }
        }
        workrecordProjectChoiceBox.getSelectionModel().select(selectedProject);
    }
    
    private void setLocationOrDefault(Worklocation selectedWorklocation) {
        if(selectedWorklocation == null || selectedWorklocation.getName().isEmpty()) {
            boolean useLastWorkrecordConfiguration = Boolean.parseBoolean(propertiesService.getProperty("UseLastWorkrecordConfiguration", "false"));
            if(useLastWorkrecordConfiguration) {
                int defaultWorklocationIdx = Integer.parseInt(propertiesService.getProperty("DefaultWorklocationIndex", "0"));
                workrecordLocationChoiceBox.getSelectionModel().select(defaultWorklocationIdx);         
                return;
            }
        }
        workrecordLocationChoiceBox.getSelectionModel().select(selectedWorklocation);
    }

}
