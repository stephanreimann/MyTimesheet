/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import controller.ContractViewController.DataAction;
import controls.LocalTimeSpinner;
import java.net.URL;
import java.sql.Connection;
import java.time.LocalTime;
import java.time.format.*;
import java.util.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.Contract;
import org.apache.logging.log4j.*;
import service.*;
import utils.ControllerUtilities;

/**
 *
 * @author adrest18
 */
public class ContractDetailsViewController implements Initializable, IViewController {

    private final int dummyColumWidth = 210;

    private final String timeFormat = "HH:mm:ss";
    private final String contractNameResourceKey = "ContractName";
    private final String workhoursResourceKey = "Workhours";
    private final String maxWorkhoursResourceKey = "MaxWorkhours";
    private final String vacationdaysResourceKey = "Vacationdays";
    private final String vacationReconciliationDateResourceKey = "VacationReconciliationDate";
    private final String breakfastOfftimeStartResourceKey = "BreakfastOfftimeStart";
    private final String breakfastOfftimeEndResourceKey = "BreakfastOfftimeEnd";
    private final String lunchOfftimeStartResourceKey = "LunchOfftimeStart";
    private final String lunchOfftimeEndResourceKey = "LunchOfftimeEnd";
    private final String earliestWorktimeStartResourceKey = "EarliestWorktimeStart";
    private final String latestWorktimeEndResourceKey = "LatestWorktimeEnd";
    private final String acceptResourceKey = "Accept";
    private final String cancelResourceKey = "Cancel";
    
    private final Logger log = LogManager.getLogger(ContractDetailsViewController.class.getName());
    
    private Stage primaryStage;
    private Contract contract;
    private final LanguageService languageService;
    private final Connection connection;
    private final UndoService undoService;
    private ResourceBundle rb;
    private final ControllerRepository controllerRepository;
    private final ObservableList<Contract> contractData;
    private DataAction dataAction;

    private String oldContractName = "";
    private String newContractName = "";
    private Long oldWorkhours = 0L;
    private Long newWorkhours = 0L;
    private Long oldMaxWorkhours = 0L;
    private Long newMaxWorkhours = 0L;
    private Long oldVacationdays = 0L;
    private Long newVacationdays = 0L;
    private String oldVacationReconciliationDate = "";
    private String newVacationReconciliationDate = "";
    private LocalTime oldBreakfastOfftimeEnd = LocalTime.MIN;
    private LocalTime newBreakfastOfftimeEnd = LocalTime.MIN;;
    private LocalTime oldBreakfastOfftimeStart = LocalTime.MIN;;
    private LocalTime newBreakfastOfftimeStart = LocalTime.MIN;;
    private LocalTime oldLunchOfftimeEnd = LocalTime.MIN;;
    private LocalTime newLunchOfftimeEnd = LocalTime.MIN;;
    private LocalTime oldLunchOfftimeStart = LocalTime.MIN;;
    private LocalTime newLunchOfftimeStart = LocalTime.MIN;;
    private LocalTime oldEarliestWorktimeStart = LocalTime.MIN;;
    private LocalTime newEarliestWorktimeStart = LocalTime.MIN;
    private LocalTime oldLatestWorktimeEnd = LocalTime.MIN;;
    private LocalTime newLatestWorktimeEnd = LocalTime.MIN;;
    
    @FXML
    private GridPane contractDetailsGridPane;
    @FXML
    private Label contractNameLabel;
    @FXML
    private Label workhoursLabel;
    @FXML
    private Label maxWorkhoursLabel;
    @FXML
    private Label vacationdaysLabel;
    @FXML
    private Label vacationReconciliationDateLabel;
    @FXML
    private Label breakfastOfftimeEndLabel;
    @FXML
    private Label breakfastOfftimeStartLabel;
    @FXML
    private Label lunchOfftimeEndLabel;
    @FXML
    private Label lunchOfftimeStartLabel;
    @FXML
    private Label earliestWorktimeStartLabel;
    @FXML
    private Label latestWorktimeEndLabel;

    @FXML
    private TextField contractNameTextFieldValue;
    @FXML
    private TextField workhoursTextFieldValue;
    @FXML
    private TextField maxWorkhoursTextFieldValue;
    @FXML
    private TextField vacationdaysTextFieldValue;
    @FXML
    private TextField vacationReconciliationDateTextFieldValue;
    private LocalTimeSpinner breakfastOfftimeTimeSpinner;
    private LocalTimeSpinner breakfastOfftimeStartTimeSpinner;
    private LocalTimeSpinner lunchOfftimeTimeSpinner;
    private LocalTimeSpinner lunchOfftimeStartTimeSpinner;
    private LocalTimeSpinner earliestWorktimeStartTimeSpinner;
    private LocalTimeSpinner latestWorktimeEndTimeSpinner;
    @FXML 
    private Button acceptButton;
    @FXML
    private Button cancelButton;

    public ContractDetailsViewController(LanguageService languageService, Connection connection, UndoService undoService, ObservableList<Contract> contractData) {
        if(languageService == null) throw new NullPointerException("languageService");
        if(connection == null) throw new NullPointerException("connection");
        if(undoService == null) throw new NullPointerException("undoService");
        if(contractData == null) throw new NullPointerException("contractData");
        
        this.languageService = languageService;
        this.connection = connection;
        this.undoService = undoService;
        this.controllerRepository = ControllerRepository.getInstance();
        
        this.contractData = contractData;
    }

    @FXML
    private void acceptAction(ActionEvent event) {
        contract.setName(contractNameTextFieldValue.getText());
        contract.setWorkhours(Long.valueOf(workhoursTextFieldValue.getText()));
        contract.setMaxworkhours(Long.valueOf(maxWorkhoursTextFieldValue.getText()));
        contract.setVacationdays(Long.valueOf(vacationdaysTextFieldValue.getText()));        
        contract.setVacationreconciliationdate(vacationReconciliationDateTextFieldValue.getText());
        contract.setBreakfastofftimeend(breakfastOfftimeTimeSpinner.getValue());
        contract.setBreakfastofftimestart(breakfastOfftimeStartTimeSpinner.getValue());
        contract.setLunchofftimeend(lunchOfftimeTimeSpinner.getValue());
        contract.setLunchofftimestart(lunchOfftimeStartTimeSpinner.getValue());
        contract.setEarliestworktimestart(earliestWorktimeStartTimeSpinner.getValue());
        contract.setLatestworktimeend(latestWorktimeEndTimeSpinner.getValue());
        
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

        breakfastOfftimeTimeSpinner = new LocalTimeSpinner();
        breakfastOfftimeTimeSpinner.setPrefWidth(dummyColumWidth);
        
        breakfastOfftimeStartTimeSpinner = new LocalTimeSpinner();
        breakfastOfftimeStartTimeSpinner.setPrefWidth(dummyColumWidth);

        lunchOfftimeTimeSpinner = new LocalTimeSpinner();
        lunchOfftimeTimeSpinner.setPrefWidth(dummyColumWidth);

        lunchOfftimeStartTimeSpinner = new LocalTimeSpinner();
        lunchOfftimeStartTimeSpinner.setPrefWidth(dummyColumWidth);

        earliestWorktimeStartTimeSpinner = new LocalTimeSpinner();
        earliestWorktimeStartTimeSpinner.setPrefWidth(dummyColumWidth);

        latestWorktimeEndTimeSpinner = new LocalTimeSpinner();
        latestWorktimeEndTimeSpinner.setPrefWidth(dummyColumWidth);
        
        contractDetailsGridPane.add(breakfastOfftimeStartTimeSpinner, 1, 5);
        contractDetailsGridPane.add(breakfastOfftimeTimeSpinner, 1, 6);
        contractDetailsGridPane.add(lunchOfftimeStartTimeSpinner, 1, 7);
        contractDetailsGridPane.add(lunchOfftimeTimeSpinner, 1, 8);
        contractDetailsGridPane.add(earliestWorktimeStartTimeSpinner, 1, 9);
        contractDetailsGridPane.add(latestWorktimeEndTimeSpinner, 1, 10);
        
        
        contractNameTextFieldValue.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            newContractName = newValue;
            isInputValid();
        });
        workhoursTextFieldValue.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            validateNumberInput(newValue, workhoursTextFieldValue);
            try {
                newWorkhours = Long.valueOf(newValue);
            } catch(NumberFormatException ex) {
                workhoursTextFieldValue.setText(newValue.replaceAll("[^\\d]", ""));
                return;
            }
            isInputValid();
        });
        maxWorkhoursTextFieldValue.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            validateNumberInput(newValue, maxWorkhoursTextFieldValue);
            try {
                newMaxWorkhours = Long.valueOf(newValue);
            } catch(NumberFormatException ex) {
                maxWorkhoursTextFieldValue.setText(newValue.replaceAll("[^\\d]", ""));
                return;
            }
            isInputValid();
        });
        vacationdaysTextFieldValue.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            validateNumberInput(newValue, vacationdaysTextFieldValue);
            try {
                newVacationdays = Long.valueOf(newValue);
            } catch(NumberFormatException ex) {
                vacationdaysTextFieldValue.setText(newValue.replaceAll("[^\\d]", ""));
                return;
            }
            isInputValid();
        });
        vacationReconciliationDateTextFieldValue.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            newVacationReconciliationDate = newValue;
            isInputValid();
        });
        breakfastOfftimeTimeSpinner.valueProperty().addListener((ObservableValue<? extends LocalTime> observable, LocalTime oldValue, LocalTime newValue) -> {
            newBreakfastOfftimeEnd = newValue;
            isInputValid();
        });
        breakfastOfftimeStartTimeSpinner.valueProperty().addListener((ObservableValue<? extends LocalTime> observable, LocalTime oldValue, LocalTime newValue) -> {
            newBreakfastOfftimeStart = newValue;
            isInputValid();
        });
        lunchOfftimeTimeSpinner.valueProperty().addListener((ObservableValue<? extends LocalTime> observable, LocalTime oldValue, LocalTime newValue) -> {
            newLunchOfftimeEnd = newValue;
            isInputValid();
        });
        lunchOfftimeStartTimeSpinner.valueProperty().addListener((ObservableValue<? extends LocalTime> observable, LocalTime oldValue, LocalTime newValue) -> {
            newLunchOfftimeStart = newValue;
            isInputValid();
        });
        earliestWorktimeStartTimeSpinner.valueProperty().addListener((ObservableValue<? extends LocalTime> observable, LocalTime oldValue, LocalTime newValue) -> {
            newEarliestWorktimeStart = newValue;
            isInputValid();
        });        
        latestWorktimeEndTimeSpinner.valueProperty().addListener((ObservableValue<? extends LocalTime> observable, LocalTime oldValue, LocalTime newValue) -> {
            newLatestWorktimeEnd = newValue;
            isInputValid();
        });
    }

    @Override
    public void updateGuiItems() {
        contractNameLabel.setText(rb.getString(contractNameResourceKey));
        workhoursLabel.setText(rb.getString(workhoursResourceKey));
        maxWorkhoursLabel.setText(rb.getString(maxWorkhoursResourceKey));
        vacationdaysLabel.setText(rb.getString(vacationdaysResourceKey));
        vacationReconciliationDateLabel.setText(rb.getString(vacationReconciliationDateResourceKey));
        breakfastOfftimeEndLabel.setText(rb.getString(breakfastOfftimeEndResourceKey));
        breakfastOfftimeStartLabel.setText(rb.getString(breakfastOfftimeStartResourceKey));
        lunchOfftimeEndLabel.setText(rb.getString(lunchOfftimeEndResourceKey));
        lunchOfftimeStartLabel.setText(rb.getString(lunchOfftimeStartResourceKey));
        earliestWorktimeStartLabel.setText(rb.getString(earliestWorktimeStartResourceKey));
        latestWorktimeEndLabel.setText(rb.getString(latestWorktimeEndResourceKey));
        acceptButton.setText(rb.getString(acceptResourceKey));
        cancelButton.setText(rb.getString(cancelResourceKey));
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
    
    void showContractDetails(Contract contract) {
        this.contract = contract;
        
        //We save the actual contract information to be able to 
        //check for changes of each Information at validation of Innput.
        saveActualProjectInformation(contract);
        
        contractNameTextFieldValue.setText(contract.getName());
        vacationReconciliationDateTextFieldValue.setText(contract.getVacationreconciliationdate());
        if(isNewContract(contract)) {
            workhoursTextFieldValue.setText("");
            maxWorkhoursTextFieldValue.setText("");
            vacationdaysTextFieldValue.setText("");
            breakfastOfftimeTimeSpinner.getValueFactory().setValue(LocalTime.MIN);
            breakfastOfftimeStartTimeSpinner.getValueFactory().setValue(LocalTime.MIN);
            lunchOfftimeTimeSpinner.getValueFactory().setValue(LocalTime.MIN);
            lunchOfftimeStartTimeSpinner.getValueFactory().setValue(LocalTime.MIN);
            earliestWorktimeStartTimeSpinner.getValueFactory().setValue(LocalTime.MIN);
            latestWorktimeEndTimeSpinner.getValueFactory().setValue(LocalTime.MIN);
        } else {
            workhoursTextFieldValue.setText(contract.getWorkhours().toString());
            maxWorkhoursTextFieldValue.setText(contract.getMaxworkhours().toString());
            vacationdaysTextFieldValue.setText(contract.getVacationdays().toString());
            breakfastOfftimeTimeSpinner.getValueFactory().setValue(contract.getBreakfastofftimeend());
            breakfastOfftimeStartTimeSpinner.getValueFactory().setValue(contract.getBreakfastofftimestart());
            lunchOfftimeTimeSpinner.getValueFactory().setValue(contract.getLunchofftimeend());
            lunchOfftimeStartTimeSpinner.getValueFactory().setValue(contract.getLunchofftimestart());
            earliestWorktimeStartTimeSpinner.getValueFactory().setValue(contract.getEarliestworktimestart());
            latestWorktimeEndTimeSpinner.getValueFactory().setValue(contract.getLatestworktimeend());
        }  
    }

    private void saveActualProjectInformation(Contract contract) {
        oldContractName = contract.getName();
        oldWorkhours = contract.getWorkhours();
        oldMaxWorkhours = contract.getMaxworkhours();
        oldVacationdays = contract.getVacationdays();
        oldVacationReconciliationDate = contract.getVacationreconciliationdate();
        oldBreakfastOfftimeEnd = contract.getBreakfastofftimeend();
        oldBreakfastOfftimeStart = contract.getBreakfastofftimestart();
        oldLunchOfftimeEnd = contract.getLunchofftimeend();
        oldLunchOfftimeStart = contract.getLunchofftimestart();
        oldEarliestWorktimeStart = contract.getEarliestworktimestart();
        oldLatestWorktimeEnd = contract.getLatestworktimeend();
    }

    private void validateNumberInput(String newValue, TextField textField) {
        if (!newValue.matches("\\d*")) {
            textField.setText(newValue.replaceAll("[^\\d]", ""));
        }
    }
        
    private boolean isNewContract(Contract contract) {
        return (contract.getName() == null &&
            contract.getWorkhours() == 0 &&
            contract.getMaxworkhours() == 0 &&
            contract.getVacationdays() == 0 &&
            contract.getVacationreconciliationdate() == null &&
            contract.getBreakfastofftimeend() == null &&
            contract.getBreakfastofftimestart() == null &&
            contract.getLunchofftimeend() == null &&
            contract.getLunchofftimestart() == null &&
            contract.getEarliestworktimestart() == null &&
            contract.getLatestworktimeend() == null);
    }
    
    private boolean isInputValid() {
        boolean result = false;

        switch(dataAction) {
            case DataAction.NEW -> {
                boolean r1 = isInputFilled();
                boolean r2 = isInputUnique();
                
                result = r1 && r2;
            }
            case DataAction.EDIT -> {
                boolean r1 = isInputFilled();
                boolean r2 = hasInputChanged();
                boolean r3 = isInputUnique();
                
                result = r1 && r2 && r3;
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
        boolean r1 = !isNullOrEmpty(contractNameTextFieldValue);
        boolean r2 = !isNullOrEmpty(workhoursTextFieldValue);
        boolean r3 = !isNullOrEmpty(maxWorkhoursTextFieldValue);
        boolean r4 = !isNullOrEmpty(vacationdaysTextFieldValue);
        boolean r5 = !isNullOrEmpty(vacationReconciliationDateTextFieldValue);
        
        boolean result = r1 && r2 && r3 && r4 && r5;
        
        return result;
    }

    private boolean isNullOrEmpty(TextField textField) {
        return ControllerUtilities.isNullOrEmpty(textField.getText());
    }

    private boolean isInputUnique() {
        boolean r1 = isContractUnique();
        
        boolean result = r1;
        
        return result;
    }

    private boolean isContractUnique() {
        List<Contract> result = contractData.stream()
                .filter(c -> c.getName().equals(newContractName))
                .filter(c -> c.getWorkhours().equals(newWorkhours))
                .filter(c -> c.getMaxworkhours().equals(newMaxWorkhours))
                .filter(c -> c.getVacationdays().equals(newVacationdays))
                .filter(c -> c.getVacationreconciliationdate().equals(newVacationReconciliationDate))
                .filter(c -> c.getBreakfastofftimestart().equals(newBreakfastOfftimeStart))
                .filter(c -> c.getBreakfastofftimeend().equals(newBreakfastOfftimeEnd))
                .filter(c -> c.getLunchofftimestart().equals(newLunchOfftimeStart))
                .filter(c -> c.getLunchofftimeend().equals(newLunchOfftimeEnd))
                .filter(c -> c.getEarliestworktimestart().equals(newEarliestWorktimeStart))
                .filter(c -> c.getLatestworktimeend().equals(newLatestWorktimeEnd))
                .toList();
        return result.isEmpty();
    }
    
    private boolean hasInputChanged() {
        boolean r1 = !oldContractName.equals(newContractName);
        boolean r2 = !oldWorkhours.equals(newWorkhours);
        boolean r3 = !oldMaxWorkhours.equals(newMaxWorkhours);
        boolean r4 = !oldVacationdays.equals(newVacationdays);
        boolean r5 = !oldVacationReconciliationDate.equals(newVacationReconciliationDate);
        boolean r6 = !oldBreakfastOfftimeStart.equals(newBreakfastOfftimeStart);
        boolean r7 = !oldBreakfastOfftimeEnd.equals(newBreakfastOfftimeEnd);
        boolean r8 = !oldLunchOfftimeStart.equals(newLunchOfftimeStart);
        boolean r9 = !oldLunchOfftimeEnd.equals(newLunchOfftimeEnd);
        boolean r10 = !oldEarliestWorktimeStart.equals(newEarliestWorktimeStart);
        boolean r11 = !oldLatestWorktimeEnd.equals(newLatestWorktimeEnd);
        
        boolean result = r1 || r2 || r3 || r4 || r5 || r6 || r7 || r8 || r9 || r10 || r11;
        
        return result;
    }
    
    private boolean isValidTimeFormat(String newValue) {
        if(ControllerUtilities.isNullOrEmpty(newValue)) {
            return false;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timeFormat);
        try {
            LocalTime.parse(newValue, formatter);
            return true;
        } catch(DateTimeParseException e) {
            return false;
        }
    }

}
