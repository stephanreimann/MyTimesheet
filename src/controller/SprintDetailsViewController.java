/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import controller.SprintViewController.DataAction;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.Sprint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.LanguageService;
import service.UndoService;

/**
 *
 * @author adrest18
 */
public class SprintDetailsViewController implements Initializable, IViewController {

    private final String sprintIdResourceKey = "SprintId";
    private final String startDateResourceKey = "StartDate";
    private final String endDateResourceKey = "EndDate";
    private final String numberOfSprintDaysResourceKey = "NumberOfSprintDays";
    
    private final Logger log = LogManager.getLogger(SprintDetailsViewController.class.getName());
    
    private Stage primaryStage;
    private Sprint sprint;
    private final LanguageService languageService;
    private final Connection connection;
    private final UndoService undoService;
    private ResourceBundle rb;
    private final ControllerRepository controllerRepository;
    private final ObservableList<Sprint> sprintData;
    private DataAction dataAction;
    
    private Long oldSprintId;
    private Long newSprintId;
    private LocalDate oldStartDate;
    private LocalDate newStartDate;
    private LocalDate oldEndDate;
    private LocalDate newEndDate;
    private Integer oldNumberOfSprintDays;
    private Integer newNumberOfSprintDays;
    
    @FXML
    private Label sprintIdLabel;
    @FXML
    private Label startDateLabel;
    @FXML
    private Label endDateLabel;
    @FXML
    private Label numberOfSprintDaysLabel;

    @FXML
    private Label sprintIdLabelValue;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private Label numberOfSprintDaysLabelValue;

    @FXML 
    private Button acceptButton;
    @FXML
    private Button cancelButton;
    
    public SprintDetailsViewController(LanguageService languageService, Connection connection, UndoService undoService, ObservableList<Sprint> sprintData) {
        if(languageService == null) throw new NullPointerException("languageService");
        if(connection == null) throw new NullPointerException("connection");
        if(undoService == null) throw new NullPointerException("undoService");
        if(sprintData == null) throw new NullPointerException("sprintData");
        
        this.languageService = languageService;
        this.connection = connection;
        this.undoService = undoService;
        this.controllerRepository = ControllerRepository.getInstance();
        
        this.sprintData = sprintData;
    }

    @FXML
    private void acceptAction(ActionEvent event) {
        sprint.setId(Long.getLong(sprintIdLabelValue.getText()));
        sprint.setStartDate(startDatePicker.getValue());
        sprint.setEndDate(endDatePicker.getValue());
        sprint.setNumberOfSprintDays(Integer.getInteger(numberOfSprintDaysLabel.getText()));
        primaryStage.close();
    }
    
    @FXML
    private void cancelAction(ActionEvent event) {
        primaryStage.close();
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    public void updateGuiItems() {

        refreshSprintDatesFormat();        
    }

    private void refreshSprintDatesFormat() {
        formatDatePickerValue(startDatePicker);        
        formatDatePickerValue(endDatePicker);        
    }
    
    private void formatDatePickerValue(DatePicker datePicker) {
        DateTimeFormatter dateTimeFormater = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
        datePicker.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate localDate) {
                if(localDate == null) {
                    return "";
                }
                return localDate.format(dateTimeFormater);
            }
            @Override
            public LocalDate fromString(String string) {
                return LocalDate.parse(string, dateTimeFormater);
            }
        });
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
    
    public void setAction(DataAction action) {
        this.dataAction = action;
    }

    public void showSprintDetails(Sprint sprint) {
        this.sprint = sprint;
        
        //We save the actual holyday information to be able to 
        //check for changes of each Information at validation of Innput.
        saveActualSprintInformation(sprint);
        
        sprintIdLabelValue.setText(sprint.getId().toString());
        LocalDate startDate;
        if(sprint.getStartDate() == null) {
            startDate = LocalDate.now();
        } else {
            startDate = sprint.getStartDate();
        }
        LocalDate endDate;
        if(sprint.getEndDate() == null) {
            endDate = LocalDate.now();
        } else {
            endDate = sprint.getEndDate();
        }
        numberOfSprintDaysLabelValue.setText(sprint.getNumberOfSprintDays().toString());
    }

    private void saveActualSprintInformation(Sprint sprint) {
        oldSprintId = sprint.getId();
        oldStartDate = sprint.getStartDate();
        oldEndDate = sprint.getEndDate();
        oldNumberOfSprintDays = sprint.getNumberOfSprintDays();
    }
    
}
