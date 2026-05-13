/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import controls.*;
import java.io.*;
import java.net.URL;
import java.sql.*;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;
import org.apache.logging.log4j.*;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.*;
import model.Project;
import model.Worklocation;
import org.apache.logging.log4j.LogManager;
import service.PropertiesService;
import sqlite.*;
import utils.DurationConverter;
import utils.DurationStyler;
import utils.EventManager;

/**
 *
 * @author adrest18
 */
public class SettingsViewController implements Initializable, IViewController {
    
    private final String applicationResourceKey = "Application";
    private final String onResourceKey = "On";
    private final String offResourceKey = "Off";
    private final String applicationAlwaysOnTopResourceKey = "ApplicationOnTop";
    private final String showSplashScreenLabelResourceKey = "ShowSplashScreen";

    private final String workrecordResourceKey = "Workrecord";
    private final String workrecordAutomaticCreationResourceKey = "WorkrecordAutomaticCreation";
    private final String workrecordStartTimeDeltaResourceKey = "WorkrecordStartTimeDelta";
    private final String defaultWorkrecordStartTimeDelta = LocalTime.MIN.toString();
    private final String workrecordEndTimeDeltaResourceKey = "WorkrecordEndTimeDelta";
    private final String defaultWorkrecordEndTimeDelta = LocalTime.MIN.toString();
    private final String useLastWorkrecordConfiguration = "WorkrecordUseLastWorkrecordConfiguration";
    private final String locationResourceKey = "Location";
    private final String projectResourceKey = "Project";
    
    private final String worktimeResourceKey = "WorkTime";
    private final String upperOverallOvertimeThresholdResourceKey = "UpperOverallOvertimeThreshold";
    private final String lowerOverallOvertimeThresholdResourceKey = "LowerOverallOvertimeThreshold";
    private final String defaultOvertimeThreshold = Duration.ZERO.toString();

    private final String workrecordAutomaticCreationChangeEvent = "WorkrecordAutomaticCreationChangeEvent";
    private final String defaultProjectChangedEvent = "DefaultProjectChanged";
    private final String defaultLocationChangedEvent = "DefaultLocationChanged";
    private final String useLastWorkrecordConfigurationChangedEvent = "UseLastWorkrecordConfigurationChanged";
    
    @FXML
    private TabPane settingsTabPane;
    @FXML
    private Tab settingsApplicationTab;
    @FXML
    private Label applicationAlwaysOnTopLabel;
    @FXML
    private ToggleButton applicationAlwaysOnTopToggleButton;
    @FXML
    private Label showSplasScreenLabel;
    @FXML
    private ToggleButton showSplashScreenToggleButton;
    
    @FXML
    private Tab settingsWorkrecordTab;
    @FXML
    private GridPane settingsWorkrecordTabGridPane;
    @FXML
    private Label workrecordCreateAutomatic;
    @FXML
    private ToggleButton workrecordAutomaticCreationToggleButton;
    @FXML
    private Label workrecordStartTimeDelta;
    @FXML
    private Label workrecordEndTimeDelta;
    @FXML
    private Label workrecordUseLastWorkrecordConfiguration;
    @FXML
    private ToggleButton workrecordUseLastWorkrecordConfigurationToggleButton;
    @FXML
    private Label workrecordLocationLabel;
    @FXML
    private ChoiceBox<Worklocation> workrecordLocationChoiceBox;
    @FXML
    private Label workrecordProjectLabel;
    @FXML
    private ChoiceBox<Project> workrecordProjectChoiceBox;
    
    @FXML
    private Tab settingsWorktimeTab;
    @FXML
    private GridPane settingsWorktimeTabGridPane;
    @FXML
    private Label upperOverallOvertimeThresholdLabel;
    @FXML
    private Label lowerOverallOvertimeThresholdLabel;

    private DurationSpinner workrecordStartTimeDeltaValue;
    private DurationSpinner workrecordEndTimeDeltaValue;
    private DurationSpinner upperOverallOvertimeThresholdValue; 
    private DurationSpinner lowerOverallOvertimeThresholdValue; 
    
    private final WorklocationDAO worklocationDao;
    private final ProjectDAO projectDao;
   
    private final Logger log = LogManager.getLogger(SettingsViewController.class.getName());
    
    private Stage primaryStage;
    private ResourceBundle rb;
    private final PropertiesService propertiesService;
    public EventManager eventManager;
    private final WorkRecordDetailsViewController workRecordDetailsViewController;

    public SettingsViewController(PropertiesService propertiesService, Connection connection, WorkRecordDetailsViewController workRecordDetailsViewController) {
        this.propertiesService = propertiesService;
        this.worklocationDao = new WorklocationDAO(connection);
        this.projectDao = new ProjectDAO(connection);
        this.workRecordDetailsViewController = workRecordDetailsViewController;
        this.eventManager = new EventManager();
        this.eventManager.registerEventType(workrecordAutomaticCreationChangeEvent);
        this.eventManager.registerEventType(defaultProjectChangedEvent);
        this.eventManager.registerEventType(defaultLocationChangedEvent);
        this.eventManager.registerEventType(useLastWorkrecordConfigurationChangedEvent);
    }
    
    @Override
    public void initialize(URL location, ResourceBundle rb) {
        this.rb = rb;
        
        this.eventManager.subscribeEventToListener(workrecordAutomaticCreationChangeEvent, workRecordDetailsViewController);
        this.eventManager.subscribeEventToListener(defaultProjectChangedEvent, workRecordDetailsViewController);
        this.eventManager.subscribeEventToListener(defaultLocationChangedEvent, workRecordDetailsViewController);
        this.eventManager.subscribeEventToListener(useLastWorkrecordConfigurationChangedEvent, workRecordDetailsViewController);
        
        applicationAlwaysOnTopToggleButton.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            toggleApplicationAlwaysOnTopToggleButton(newValue);    
        });
        boolean applicationAlwaysOnTop = Boolean.parseBoolean(propertiesService.getProperty("ApplicationAlwaysOnTop", "true"));
        toggleApplicationAlwaysOnTopToggleButton(applicationAlwaysOnTop);
        
        showSplashScreenToggleButton.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            toggleShowSplashScreenToggleButton(newValue);    
        });
        boolean showSplashScreen = Boolean.parseBoolean(propertiesService.getProperty("ShowSplashScreen", "true"));
        toggleShowSplashScreenToggleButton(showSplashScreen);

        workrecordAutomaticCreationToggleButton.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            toggleWorkrecordAutomaticCreationToggleButton(newValue);
            eventManager.notifyListenerOfEvent(workrecordAutomaticCreationChangeEvent, newValue);
        });
        
        workrecordStartTimeDeltaValue = new DurationSpinner();
        String startTimeDeltaValue = propertiesService.getProperty("WorkrecordStartTimeDelta", defaultWorkrecordStartTimeDelta);
        Duration startTimeDeltaDuration = DurationConverter.convertSignedStringOfHoursAndMinutesToDuration(startTimeDeltaValue);
        workrecordStartTimeDeltaValue.getValueFactory().setValue(startTimeDeltaDuration);        
        DurationStyler.styleSpinner(workrecordStartTimeDeltaValue, startTimeDeltaDuration);
        settingsWorkrecordTabGridPane.add(workrecordStartTimeDeltaValue, 1, 1);
        workrecordStartTimeDeltaValue.valueProperty().addListener((ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) -> {
            DurationStyler.styleSpinner(workrecordStartTimeDeltaValue, newValue);
        });

        workrecordEndTimeDeltaValue = new DurationSpinner();
        String endTimeDeltaValue = propertiesService.getProperty("WorkrecordEndTimeDelta", defaultWorkrecordEndTimeDelta);
        workrecordEndTimeDeltaValue.getValueFactory().setValue(DurationConverter.convertSignedStringOfHoursAndMinutesToDuration(endTimeDeltaValue));        
        DurationStyler.styleSpinner(workrecordEndTimeDeltaValue, Duration.ZERO);
        settingsWorkrecordTabGridPane.add(workrecordEndTimeDeltaValue, 1, 2);
        workrecordEndTimeDeltaValue.valueProperty().addListener((ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) -> {
            DurationStyler.styleSpinner(workrecordEndTimeDeltaValue, newValue);
        });

        upperOverallOvertimeThresholdValue = new DurationSpinner();
        String upperOvertimeThreshold = propertiesService.getProperty("UpperOvertimeThreshold", defaultOvertimeThreshold);
        Duration upperDuration = Duration.parse(upperOvertimeThreshold);
        upperOverallOvertimeThresholdValue.getValueFactory().setValue(upperDuration);
        DurationStyler.styleSpinner(upperOverallOvertimeThresholdValue, upperDuration);
        settingsWorktimeTabGridPane.add(upperOverallOvertimeThresholdValue, 1, 0);
        upperOverallOvertimeThresholdValue.valueProperty().addListener((ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) -> {
            DurationStyler.styleSpinner(upperOverallOvertimeThresholdValue, newValue);
        });

        lowerOverallOvertimeThresholdValue = new DurationSpinner();
        String lowerOvertimeThreshold = propertiesService.getProperty("LowerOvertimeThreshold", defaultOvertimeThreshold);
        Duration lowerDuration = Duration.parse(lowerOvertimeThreshold);
        lowerOverallOvertimeThresholdValue.getValueFactory().setValue(lowerDuration);
        DurationStyler.styleSpinner(lowerOverallOvertimeThresholdValue, lowerDuration);
        settingsWorktimeTabGridPane.add(lowerOverallOvertimeThresholdValue, 1, 1);
        lowerOverallOvertimeThresholdValue.valueProperty().addListener((ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) -> {
            DurationStyler.styleSpinner(lowerOverallOvertimeThresholdValue, newValue);
        });

        boolean workrecordAutomaticCreation = Boolean.parseBoolean(propertiesService.getProperty("WorkrecordAutomaticCreation", "true"));
        toggleWorkrecordAutomaticCreationToggleButton(workrecordAutomaticCreation);
        
        workrecordUseLastWorkrecordConfigurationToggleButton.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            toggleWorkrecordUseLastWorkrecordConfigurationToggleButton(newValue);
            eventManager.notifyListenerOfEvent(useLastWorkrecordConfigurationChangedEvent, newValue);
        });
                
        boolean lastWorkrecordConfiguration = Boolean.parseBoolean(propertiesService.getProperty("UseLastWorkrecordConfiguration", "true"));
        toggleWorkrecordUseLastWorkrecordConfigurationToggleButton(lastWorkrecordConfiguration);
      
        workrecordLocationChoiceBox.setOnAction((event) -> {
            int selectedWorklocationIdx = workrecordLocationChoiceBox.getSelectionModel().getSelectedIndex();
            propertiesService.setProperty("DefaultWorklocationIndex", String.valueOf(selectedWorklocationIdx));
            if(Boolean.parseBoolean(propertiesService.getProperty("UseLastWorkrecordConfiguration", "false"))) {
                eventManager.notifyListenerOfEvent(defaultLocationChangedEvent, selectedWorklocationIdx);
            }
        });        
        
        workrecordProjectChoiceBox.setOnAction((event) -> {
            int selectedProjectIdx = workrecordProjectChoiceBox.getSelectionModel().getSelectedIndex();
            propertiesService.setProperty("DefaultProjectIndex", String.valueOf(selectedProjectIdx));
            if(Boolean.parseBoolean(propertiesService.getProperty("UseLastWorkrecordConfiguration", "false"))) {
                eventManager.notifyListenerOfEvent(defaultProjectChangedEvent, selectedProjectIdx);
            }
        });        

        try {
            workrecordLocationChoiceBox.getItems().addAll(worklocationDao.selectAll());
            String worklocationIdxAsString = propertiesService.getProperty("DefaultWorklocationIndex", "0");
            int worklocationIdx =  Integer.parseInt(worklocationIdxAsString);
            workrecordLocationChoiceBox.getSelectionModel().select(worklocationIdx);
            
            workrecordProjectChoiceBox.getItems().addAll(projectDao.selectAll());
            String projectIdxAsString = propertiesService.getProperty("DefaultProjectIndex", "0");
            int projectIdx =  Integer.parseInt(projectIdxAsString);
            workrecordProjectChoiceBox.getSelectionModel().select(projectIdx);
        } catch (SQLException ex) {
            log.fatal(ex.getMessage());
        }
    }
    
    @Override
    public void updateGuiItems() {
        settingsApplicationTab.setText(rb.getString(applicationResourceKey));
        applicationAlwaysOnTopLabel.setText(rb.getString(applicationAlwaysOnTopResourceKey));
        toggleApplicationAlwaysOnTopToggleButton(applicationAlwaysOnTopToggleButton.isSelected());

        showSplasScreenLabel.setText(rb.getString(showSplashScreenLabelResourceKey));
        toggleShowSplashScreenToggleButton(showSplashScreenToggleButton.isSelected());
        
        settingsWorkrecordTab.setText(rb.getString(workrecordResourceKey));
        workrecordCreateAutomatic.setText(rb.getString(workrecordAutomaticCreationResourceKey));
        toggleWorkrecordAutomaticCreationToggleButton(workrecordAutomaticCreationToggleButton.isSelected());
        workrecordStartTimeDelta.setText(rb.getString(workrecordStartTimeDeltaResourceKey));
        workrecordEndTimeDelta.setText(rb.getString(workrecordEndTimeDeltaResourceKey));
        workrecordUseLastWorkrecordConfiguration.setText(rb.getString(useLastWorkrecordConfiguration));
        toggleWorkrecordUseLastWorkrecordConfigurationToggleButton(workrecordUseLastWorkrecordConfigurationToggleButton.isSelected());
        workrecordLocationLabel.setText(rb.getString(locationResourceKey));
        workrecordProjectLabel.setText(rb.getString(projectResourceKey));
        
        settingsWorktimeTab.setText(rb.getString(worktimeResourceKey));
        upperOverallOvertimeThresholdLabel.setText(rb.getString(upperOverallOvertimeThresholdResourceKey));
        lowerOverallOvertimeThresholdLabel.setText(rb.getString(lowerOverallOvertimeThresholdResourceKey));
    }

    private void toggleApplicationAlwaysOnTopToggleButton(Boolean isSelected) {
        if(isSelected) {
            applicationAlwaysOnTopToggleButton.setText(rb.getString(onResourceKey));
        } else {
            applicationAlwaysOnTopToggleButton.setText(rb.getString(offResourceKey));
        }
        applicationAlwaysOnTopToggleButton.setSelected(isSelected);
    }
    
    private void toggleShowSplashScreenToggleButton(Boolean isSelected) {
        if(isSelected) {
            showSplashScreenToggleButton.setText(rb.getString(onResourceKey));
        } else {
            showSplashScreenToggleButton.setText(rb.getString(offResourceKey));
        }
        showSplashScreenToggleButton.setSelected(isSelected);
    }

    private void toggleWorkrecordAutomaticCreationToggleButton(Boolean isSelected) {
        if(isSelected) {
            workrecordAutomaticCreationToggleButton.setText(rb.getString(onResourceKey));
        } else {
            workrecordAutomaticCreationToggleButton.setText(rb.getString(offResourceKey));
            workrecordStartTimeDelta.disableProperty().setValue(isSelected);
        }

        workrecordUseLastWorkrecordConfigurationToggleButton.disableProperty().setValue(!isSelected);
        workrecordStartTimeDeltaValue.disableProperty().setValue(!isSelected);
        workrecordEndTimeDeltaValue.disableProperty().setValue(!isSelected);
        
        boolean isWorkrecordAutomaticCreationToggleButtonSelected = workrecordAutomaticCreationToggleButton.isSelected();
        boolean isWorkrecordUseLastWorkrecordConfigurationToggleButtonSelected = workrecordUseLastWorkrecordConfigurationToggleButton.isSelected();
        
        if(!isWorkrecordAutomaticCreationToggleButtonSelected || !isWorkrecordUseLastWorkrecordConfigurationToggleButtonSelected) {
            workrecordLocationChoiceBox.disableProperty().setValue(true);
            workrecordProjectChoiceBox.disableProperty().setValue(true);
        } else if(isWorkrecordAutomaticCreationToggleButtonSelected && isWorkrecordUseLastWorkrecordConfigurationToggleButtonSelected) {
            workrecordLocationChoiceBox.disableProperty().setValue(false);
            workrecordProjectChoiceBox.disableProperty().setValue(false);
        }
                
        workrecordAutomaticCreationToggleButton.setSelected(isSelected);
    }

    private void toggleWorkrecordUseLastWorkrecordConfigurationToggleButton(Boolean isSelected) {
        if(isSelected) {
            workrecordUseLastWorkrecordConfigurationToggleButton.setText(rb.getString(onResourceKey));
        } else {
            workrecordUseLastWorkrecordConfigurationToggleButton.setText(rb.getString(offResourceKey));
        }

        workrecordLocationChoiceBox.disableProperty().setValue(!isSelected);
        workrecordProjectChoiceBox.disableProperty().setValue(!isSelected);

        workrecordUseLastWorkrecordConfigurationToggleButton.setSelected(isSelected);
    }
    
    private File initializeFileChooserAndShowIt(String description, List<String> extensions) throws IOException {
        String holydaysDirectory = new File(".").getCanonicalPath();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(rb.getString("FileChooserTitle"));
        fileChooser.setInitialDirectory(new File(holydaysDirectory));
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(description, extensions);
        fileChooser.getExtensionFilters().add(extFilter);
        return fileChooser.showOpenDialog(primaryStage);
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
        writeProperties();
    }

    private void writeProperties() {
        propertiesService.setProperty("WorkrecordStartTimeDelta", DurationConverter.convertDurationToSignedStringOfHoursAndMinutes(workrecordStartTimeDeltaValue.getValue()));
        propertiesService.setProperty("WorkrecordEndTimeDelta", DurationConverter.convertDurationToSignedStringOfHoursAndMinutes(workrecordEndTimeDeltaValue.getValue()));        
        propertiesService.setProperty("UpperOvertimeThreshold", upperOverallOvertimeThresholdValue.getValue().toString());
        propertiesService.setProperty("LowerOvertimeThreshold", lowerOverallOvertimeThresholdValue.getValue().toString());
        propertiesService.setProperty("ShowSplashScreen", String.valueOf(showSplashScreenToggleButton.isSelected()));
        propertiesService.setProperty("ApplicationAlwaysOnTop", String.valueOf(applicationAlwaysOnTopToggleButton.isSelected()));
        propertiesService.setProperty("WorkrecordAutomaticCreation", String.valueOf(workrecordAutomaticCreationToggleButton.isSelected()));
        propertiesService.setProperty("UseLastWorkrecordConfiguration", String.valueOf(workrecordUseLastWorkrecordConfigurationToggleButton.isSelected()));
    }
        
}
