/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import controller.HolydayViewController.DataAction;
import java.net.URL;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.*;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.Holyday;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.*;
import utils.*;

/**
 *
 * @author adrest18
 */
public class HolydayDetailsViewController implements Initializable, IViewController {

    private final String dateFormat = "dd.MM.yyyy";
    private final String holydayDateResourceKey = "HolydayDate";
    private final String holydayNameResourceKey = "HolydayName";
    private final String holydayStateResourceKey = "HolydayState";
    private final String acceptResourceKey = "Accept";
    private final String cancelResourceKey = "Cancel";

    private final Logger log = LogManager.getLogger(HolydayDetailsViewController.class.getName());
    
    private Stage primaryStage;
    private Holyday holyday;
    private final LanguageService languageService;
    private final Connection connection;
    private final UndoService undoService;
    private ResourceBundle rb;
    private final ControllerRepository controllerRepository;
    private final ObservableList<Holyday> holydayData;
    private DataAction dataAction;
    
    private LocalDate oldHolydayDate;
    private LocalDate newHolydayDate;
    private String oldHolydayName;
    private String newHolydayName;
    private String oldHolydayState;
    private String newHolydayState;

    @FXML
    private Label holydayDateLabel;
    @FXML
    private Label holydayNameLabel;
    @FXML
    private Label holydayStateLabel;

    @FXML
    private DatePicker holydayDatePicker;
    @FXML
    private TextField holydayNameTextFieldValue;
    @FXML
    private TextField holydayStateTextFieldValue;

    @FXML 
    private Button acceptButton;
    @FXML
    private Button cancelButton;
    
    public HolydayDetailsViewController(LanguageService languageService, Connection connection, UndoService undoService, ObservableList<Holyday> holydayData) {
        if(languageService == null) throw new NullPointerException("languageService");
        if(connection == null) throw new NullPointerException("connection");
        if(undoService == null) throw new NullPointerException("undoService");
        if(holydayData == null) throw new NullPointerException("holydayData");
        
        this.languageService = languageService;
        this.connection = connection;
        this.undoService = undoService;
        this.controllerRepository = ControllerRepository.getInstance();
        
        this.holydayData = holydayData;
    }

    @FXML
    private void acceptAction(ActionEvent event) {
        holyday.setDate(holydayDatePicker.getValue());
        holyday.setName(holydayNameTextFieldValue.getText());
        holyday.setState(holydayStateTextFieldValue.getText());
        
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
        
        holydayDatePicker.valueProperty().addListener((ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) -> {
            newHolydayDate = newValue;
            validateInput();
        });
        holydayNameTextFieldValue.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            newHolydayName = newValue;
            validateInput();
        });        
        holydayStateTextFieldValue.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            newHolydayState = newValue;
            validateInput();
        });        
    }

    @Override
    public void updateGuiItems() {
        holydayDateLabel.setText(rb.getString(holydayDateResourceKey));
        holydayNameLabel.setText(rb.getString(holydayNameResourceKey));
        holydayStateLabel.setText(rb.getString(holydayStateResourceKey));
        acceptButton.setText(rb.getString(acceptResourceKey));
        cancelButton.setText(rb.getString(cancelResourceKey));
        
        refreshHolydayDateFormat();
    }

    private void refreshHolydayDateFormat() {
        formatDatePickerValue(holydayDatePicker);        
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

    void showHolydayDetails(Holyday holyday) {
        this.holyday = holyday;
        
        //We save the actual contract information to be able to 
        //check for changes of each Information at validation of Innput.
        saveActualHolydayInformation(holyday);
        
        LocalDate date;
        if(holyday.getDate() == null) {
            date = LocalDate.now();
        } else {
            date = holyday.getDate();
        }
        holydayDatePicker.setValue(date);
        holydayNameTextFieldValue.setText(holyday.getName());
        holydayStateTextFieldValue.setText(holyday.getState());
    }
   
    private void saveActualHolydayInformation(Holyday holyday) {
        oldHolydayDate = holyday.getDate();
        oldHolydayName = holyday.getName();
        oldHolydayState = holyday.getState();
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

                return r1 && r2;
            }
            case DataAction.EDIT -> {
                boolean r1 = isInputFilled();
                boolean r2 = hasInputChanged();
                return r1 && r2;
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
        boolean r1 = isDateFilled(holydayDatePicker);
        boolean r2 = isHolydayNameFilled(holydayNameTextFieldValue);
        boolean r3 = isHolydayStateFilled(holydayStateTextFieldValue);
        
        boolean result = r1 && r2 && r3;

        return result;
    }
    
    private boolean isDateFilled(DatePicker holydayDatePicker) {
        String formatedDate = DateConverter.format(holydayDatePicker.getValue(), DateTimeFormatter.ofPattern(dateFormat));
        return !ControllerUtilities.isNullOrEmpty(formatedDate);
    }

    private boolean isHolydayNameFilled(TextField holydayName) {
        return !isNullOrEmpty(holydayName);
    }
    
    private boolean isHolydayStateFilled(TextField holydayState) {
        return !isNullOrEmpty(holydayState);
    }

    private boolean isNullOrEmpty(TextField textField) {
        return ControllerUtilities.isNullOrEmpty(textField.getText());
    }
    
    private boolean isInputUnique() {
        String name = holydayNameTextFieldValue.getText();
        String state = holydayStateTextFieldValue.getText();
        Holyday tempHolyday = new Holyday(holydayDatePicker.getValue(), name, state);
        return !holydayData.contains(tempHolyday);
    }

    private boolean hasInputChanged() {
        boolean r1 = !oldHolydayDate.equals(newHolydayDate);
        boolean r2 = !oldHolydayName.equals(newHolydayName);
        boolean r3 = !oldHolydayState.equals(newHolydayState);
        
        boolean result = r1 || r2 || r3;
        
        return result;
    }
    
}
