/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import controller.SprintViewController.DataAction;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import model.Sprint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.LanguageService;
import service.UndoService;
import utils.ControllerUtilities;
import utils.DateConverter;

/**
 *
 * @author adrest18
 */
public class SprintDetailsViewController implements Initializable, IViewController {

    private final String dateFormat = "dd.MM.yyyy";
    private final String sprintIdResourceKey = "SprintId";
    private final String startDateResourceKey = "StartDate";
    private final String endDateResourceKey = "EndDate";
    private final String numberOfSprintDaysResourceKey = "NumberOfSprintDays";
    private final String acceptResourceKey = "Accept";
    private final String cancelResourceKey = "Cancel";
    private final String endDateBeforStartDateInfoResourceKey = "EndDateBeforStartDate";
    
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
    private Long oldNumberOfSprintDays;
    private Long newNumberOfSprintDays;
    
    @FXML
    private Label sprintIdLabel;
    @FXML
    private Label sprintStartDateLabel;
    @FXML
    private Label sprintEndDateLabel;
    @FXML
    private Label numberOfSprintDaysLabel;
    @FXML
    private Label sprintInfoLabel;

    @FXML
    private TextField sprintIdLabelValue;
    @FXML
    private DatePicker sprintStartDatePicker;
    @FXML
    private DatePicker sprintEndDatePicker;
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
        String sprintIdAsString = sprintIdLabelValue.getText();
        if(ControllerUtilities.isNullOrEmpty(sprintIdAsString)) {
            sprintIdAsString = "0";
        }
        sprint.setId(Long.valueOf(sprintIdAsString));
        sprint.setStartDate(sprintStartDatePicker.getValue());
        sprint.setEndDate(sprintEndDatePicker.getValue());
        String numberOfSprintDaysAsString = numberOfSprintDaysLabelValue.getText();
        if(ControllerUtilities.isNullOrEmpty(numberOfSprintDaysAsString)) {
            numberOfSprintDaysAsString = "0";
        }
        sprint.setNumberOfSprintDays(Long.valueOf(numberOfSprintDaysAsString));
        primaryStage.close();
    }
    
    @FXML
    private void cancelAction(ActionEvent event) {
        primaryStage.close();
    }
    
    @Override
    public void initialize(URL location, ResourceBundle rb) {
        this.rb = rb;

        acceptButton.setDisable(true);
        initStartDateCellFactory();
        initEndDateCellFactory();

        sprintIdLabelValue.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            validateNumberInput(newValue, sprintIdLabelValue);            
            try {
                newSprintId = Long.valueOf(newValue);
            } catch (NumberFormatException ex) {
                //sprintIdLabelValue.setText(newValue.replaceAll("[^\\d]", ""));
                acceptButton.setDisable(true);
            }
            validateInput();
        });        
        sprintStartDatePicker.valueProperty().addListener((ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) -> {
            newStartDate = newValue;
            newNumberOfSprintDays = calculateNumberOfSprintDays();
            numberOfSprintDaysLabelValue.setText(newNumberOfSprintDays.toString());
            validateInput();
        });
        sprintEndDatePicker.valueProperty().addListener((ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) -> {
            newEndDate = newValue;
            newNumberOfSprintDays = calculateNumberOfSprintDays();
            numberOfSprintDaysLabelValue.setText(newNumberOfSprintDays.toString());
            validateInput();
        });        
        numberOfSprintDaysLabelValue.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            newNumberOfSprintDays = Long.valueOf(newValue);
            validateInput();
        });
    }
    
    @Override
    public void updateGuiItems() {
        sprintIdLabel.setText(rb.getString(sprintIdResourceKey));
        numberOfSprintDaysLabel.setText(rb.getString(numberOfSprintDaysResourceKey));
        acceptButton.setText(rb.getString(acceptResourceKey));
        cancelButton.setText(rb.getString(cancelResourceKey));

        initStartDateCellFactory();
        initEndDateCellFactory();        
        refreshSprintDatesFormat();        
    }

    private void refreshSprintDatesFormat() {
        formatDatePickerValue(sprintStartDatePicker);        
        formatDatePickerValue(sprintEndDatePicker);        
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
        
        //We save the actual sprint information to be able to 
        //check for changes of each Information at validation of Innput.
        saveActualSprintInformation(sprint);
        
        String idAsString = sprint.getId().toString();
        sprintIdLabelValue.setText(idAsString);
        
        LocalDate startDate;
        if(sprint.getStartDate() == null) {
            startDate = LocalDate.now();
        } else {
            startDate = sprint.getStartDate();
        }
        sprintStartDatePicker.setValue(startDate);
        
        LocalDate endDate;
        if(sprint.getEndDate() == null) {
            endDate = startDate.plusDays(1);
        } else {
            endDate = sprint.getEndDate();
        }
        sprintEndDatePicker.setValue(endDate);
        String numberOfSprintDaysAsString = calculateNumberOfSprintDays().toString();
        numberOfSprintDaysLabelValue.setText(numberOfSprintDaysAsString);
    }

    private void initStartDateCellFactory() {
        Callback<DatePicker, DateCell> dayCellFactory = dp -> {
            return new DateCell() {
                @Override
                public void updateItem(LocalDate item, boolean empty)
                {
                    super.updateItem(item, empty);
                    LocalDate selectedEndDate = sprintEndDatePicker.getValue();
                    if(item.isAfter(selectedEndDate))
                    {
                        setStyle("-fx-background-color: #ffc0cb;");
                        setDisable(true);
                    }
                }
            };
        };
        sprintStartDatePicker.setDayCellFactory(dayCellFactory);
    }
    
    private void initEndDateCellFactory() {
        Callback<DatePicker, DateCell> dayCellFactory = dp -> {
            return new DateCell() {
                @Override
                public void updateItem(LocalDate item, boolean empty)
                {
                    super.updateItem(item, empty);
                    LocalDate selectedStartDate = sprintStartDatePicker.getValue();
                    if(item.isBefore(selectedStartDate))
                    {
                        setStyle("-fx-background-color: #ffc0cb;");
                        setDisable(true);
                    }
                }
            };
        };
        sprintEndDatePicker.setDayCellFactory(dayCellFactory);
    }
    
    private void saveActualSprintInformation(Sprint sprint) {
        oldSprintId = sprint.getId();
        oldStartDate = sprint.getStartDate();
        oldEndDate = sprint.getEndDate();
        oldNumberOfSprintDays = sprint.getNumberOfSprintDays();
    }

    private boolean validateInput() {
        if(isInputValid()) {
            acceptButton.setDisable(false);
            return true;
        } else {
            acceptButton.setDisable(true);
            return false;
        }
    }

    private boolean isInputValid() {
        boolean result = false;
        switch(dataAction) {
            case DataAction.NEW -> {
                boolean r1 = isInputFilled();
                boolean r2 = isInputUnique(); 
                boolean r3 = isStartDateBeforeEndDate();
                boolean r4 = isSprintNumberValid();
                
                return r1 && r2 && r3 && r4;
            }
            case DataAction.EDIT -> {
                boolean r1 = isInputFilled();
                boolean r2 = hasInputChanged();
                boolean r3 = isStartDateBeforeEndDate();
                boolean r4 = isSprintNumberValid();
                
                return r1 && r2 && r3 && r4;
            }
        }
        
        if(result) {
            acceptButton.setDisable(false);
            return true;
        } else {
            acceptButton.setDisable(true);
            return false;
        }
    }
    
    private boolean isInputFilled() {
        boolean r1 = isSprintIdFilled(sprintIdLabelValue);
        boolean r2 = isDateFilled(sprintStartDatePicker);
        boolean r3 = isDateFilled(sprintEndDatePicker);
        
        boolean result = r1 && r2 && r3;
        return result;
    }

    private boolean isSprintIdFilled(TextField sprintId) {
        return !isNullOrEmpty(sprintId);
    }
    
    private boolean isDateFilled(DatePicker datePicker) {
        LocalDate date = datePicker.getValue();
        String formatedDate;
        if(date == null) {
            formatedDate = "";
        } else {
            formatedDate = DateConverter.format(date, DateTimeFormatter.ofPattern(dateFormat));
        }
        return !ControllerUtilities.isNullOrEmpty(formatedDate);
    }
    
    private boolean isNullOrEmpty(TextField textField) {
        return ControllerUtilities.isNullOrEmpty(textField.getText());
    }    

    private boolean isInputUnique() {
        if(newStartDate == null || newEndDate == null) {
            return false;
        }
        Sprint tempSprint = new Sprint(newSprintId, newStartDate, newEndDate, newNumberOfSprintDays);
        return !sprintData.contains(tempSprint);    }

    private boolean hasInputChanged() {
        boolean r1 = !oldSprintId.equals(newSprintId);
        boolean r2 = !oldStartDate.equals(newStartDate);
        boolean r3 = !oldEndDate.equals(newEndDate);
        boolean r4 = !oldNumberOfSprintDays.equals(newNumberOfSprintDays);

        boolean result = r1 || r2 || r3 || r4;      
        return result;
    }
  
    private boolean isStartDateBeforeEndDate() {
        boolean result = false;
        LocalDate startDate = newStartDate;
        LocalDate endDate = newEndDate;
        
        if(startDate == null || endDate == null) {
            return result;
        }
        
        result = startDate.isBefore(endDate);
        if(result == false) {
            sprintEndDatePicker.setStyle("-fx-background-color: red;");
            sprintInfoLabel.setText(rb.getString(endDateBeforStartDateInfoResourceKey));
            sprintInfoLabel.setStyle("-fx-text-fill: red;");
        } else {
            sprintEndDatePicker.setStyle("");
            sprintInfoLabel.setText("");
            sprintInfoLabel.setStyle("");
        }
        
        return result;
    }
    
    private boolean isSprintNumberValid() {        
        boolean r1 = isSprintIdGreaterOrEqualZero();
        boolean r2 = isSprintIdUnique();
        
        boolean result = r1 && r2;
        return result;
    }
    
    private boolean isSprintIdGreaterOrEqualZero() {
        return newSprintId >= 0;
    }
    
    private boolean isSprintIdUnique() {
        List<Sprint> filteredList = sprintData.stream().filter(s -> s.getId().equals(newSprintId)).collect(Collectors.toList());
        return filteredList.isEmpty();
    }
    
    private Long calculateNumberOfSprintDays() {
        Long sprintDays = 0L;
        LocalDate startDate = newStartDate;
        LocalDate endDate = newEndDate;
        
        if(startDate == null || endDate == null) {
            return sprintDays;
        }
        
        if (endDate.isBefore(startDate)) {
            LocalDate temp = startDate;
            startDate = endDate;
            endDate = temp;
        }

        LocalDate date = startDate;

        while (!date.isAfter(endDate)) {
            DayOfWeek day = date.getDayOfWeek();
            if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY) {
                sprintDays++;
            }
            date = date.plusDays(1);
        }
        
        return sprintDays;
    }
    
    private void validateNumberInput(String newValue, TextField textField) {
        if (!newValue.matches("\\d*")) {
            textField.setText(newValue.replaceAll("[^\\d]", ""));
        }
    }
    
}
