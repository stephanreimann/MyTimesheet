/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import controller.AddressViewController.DataAction;
import java.net.URL;
import java.sql.*;
import java.util.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Address;
import org.apache.logging.log4j.*;
import service.*;
import utils.ControllerUtilities;

/**
 * The business logic for handling address detail information.
 * @author adrest18
 */
public class AddressDetailsViewController implements Initializable, IViewController {

    private final String streetNameLabelResourceKey = "StreetName";
    private final String houseNumberLabelResourceKey = "HouseNumber";
    private final String unitNameLabelResourceKey = "UnitName";
    private final String unitNumberLabelResourceKey = "UnitNumber";
    private final String unitLocationLabelResourceKey = "UnitLocation";
    private final String cityLabelResourceKey = "City";
    private final String stateLabelResourceKey = "State";
    private final String zipCodeLabelResourceKey = "ZipCode";
    private final String countryLabelResourceKey = "Country";
    private final String acceptResourceKey = "Accept";
    private final String cancelResourceKey = "Cancel";
    
    private final Logger log = LogManager.getLogger(AddressDetailsViewController.class.getName());
    
    private Stage primaryStage;
    private Address address;
    private final LanguageService languageService;
    private final Connection connection;
    private final UndoService undoService;
    private ResourceBundle rb;
    private final ControllerRepository controllerRepository;
    private final ObservableList<Address> addressData;
    private DataAction dataAction;
    
    private String oldStreetName;
    private String newStreetName;
    private Long oldHouseNumber;
    private Long newHouseNumber;
    private String oldUnitName;
    private String newUnitName;
    private Long oldUnitNumber;
    private Long newUnitNumber;
    private String oldUnitLocation;
    private String newUnitLocation;
    private String oldCity;
    private String newCity;
    private String oldState;
    private String newState;
    private Long oldZipCode;
    private Long newZipCode;
    private String oldCountry;
    private String newCountry;
    
    @FXML
    private Label streetNameLabel;
    @FXML
    private Label houseNumberLabel;
    @FXML
    private Label unitNameLabel;
    @FXML
    private Label unitNumberLabel;
    @FXML
    private Label unitLocationLabel;
    @FXML
    private Label cityLabel;
    @FXML
    private Label stateLabel;
    @FXML
    private Label zipCodeLabel;
    @FXML
    private Label countryLabel;

    @FXML
    private TextField streetNameTextFieldValue;
    @FXML
    private TextField houseNumberTextFieldValue;
    @FXML
    private TextField unitNameTextFieldValue;
    @FXML
    private TextField unitNumberTextFieldValue;
    @FXML
    private TextField unitLocationTextFieldValue;
    @FXML
    private TextField cityTextFieldValue;
    @FXML
    private TextField stateTextFieldValue;
    @FXML
    private TextField zipCodeTextFieldValue;
    @FXML
    private TextField countryTextFieldValue;

    @FXML 
    private Button acceptButton;
    @FXML
    private Button cancelButton;
    
    public AddressDetailsViewController(LanguageService languageService, Connection connection, UndoService undoService, ObservableList<Address> addressData) throws SQLException {
        if(languageService == null) throw new NullPointerException("languageService");
        if(connection == null) throw new NullPointerException("connection");
        if(undoService == null) throw new NullPointerException("undoService");
        if(addressData == null) throw new NullPointerException("addressData");
        
        this.languageService = languageService;
        this.connection = connection;
        this.undoService = undoService;
        this.controllerRepository = ControllerRepository.getInstance();
        
        this.addressData = addressData;
    }

    @FXML
    private void acceptAction(ActionEvent event) {
        address.setStreetname(streetNameTextFieldValue.getText());
        address.setHousenumber(Long.valueOf(houseNumberTextFieldValue.getText()));
        address.setUnitname(unitNameTextFieldValue.getText());
        address.setUnitnumber(Long.valueOf(unitNumberTextFieldValue.getText()));
        address.setUnitlocation(unitLocationTextFieldValue.getText());
        address.setCity(cityTextFieldValue.getText());
        address.setState(stateTextFieldValue.getText());
        address.setZipcode(Long.valueOf(zipCodeTextFieldValue.getText()));
        address.setCountry(countryTextFieldValue.getText());
        address.setAddress(this.toString());
        
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
        
        streetNameTextFieldValue.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            newStreetName = newValue;
            isInputValid();
        });        
        houseNumberTextFieldValue.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            validateNumberInput(newValue, houseNumberTextFieldValue);
            try {
                newHouseNumber = Long.valueOf(newValue);
            } catch(NumberFormatException ex) {
                houseNumberTextFieldValue.setText(newValue.replaceAll("[^\\d]", ""));
                return;
            }
            isInputValid();
        });        
        unitNameTextFieldValue.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            newUnitName = newValue;
            isInputValid();
        });        
        unitNumberTextFieldValue.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            validateNumberInput(newValue, unitNumberTextFieldValue);
            try {
                newUnitNumber = Long.valueOf(newValue);
            } catch(NumberFormatException ex) {
                unitNumberTextFieldValue.setText(newValue.replaceAll("[^\\d]", ""));
                return;
            }
            isInputValid();
        });        
        unitLocationTextFieldValue.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            newUnitLocation = newValue;
            isInputValid();
        });        
        cityTextFieldValue.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            newCity = newValue;
            isInputValid();
        });        
        stateTextFieldValue.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            newState = newValue;
            isInputValid();
        });        
        zipCodeTextFieldValue.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            validateNumberInput(newValue, zipCodeTextFieldValue);
            try {
                newZipCode = Long.valueOf(newValue);
            } catch(NumberFormatException ex) {
                zipCodeTextFieldValue.setText(newValue.replaceAll("[^\\d]", ""));
                return;
            }
            isInputValid();
        });        
        countryTextFieldValue.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            newCountry = newValue;
            isInputValid();
        });        

    }
    
    @Override
    public void updateGuiItems() {
        streetNameLabel.setText(rb.getString(streetNameLabelResourceKey));
        houseNumberLabel.setText(rb.getString(houseNumberLabelResourceKey));
        unitNameLabel.setText(rb.getString(unitNameLabelResourceKey));
        unitNumberLabel.setText(rb.getString(unitNumberLabelResourceKey));
        unitLocationLabel.setText(rb.getString(unitLocationLabelResourceKey));
        cityLabel.setText(rb.getString(cityLabelResourceKey));
        stateLabel.setText(rb.getString(stateLabelResourceKey));
        zipCodeLabel.setText(rb.getString(zipCodeLabelResourceKey));
        countryLabel.setText(rb.getString(countryLabelResourceKey));
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
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(streetNameTextFieldValue.getText()).append(", ");
        sb.append(Long.valueOf(houseNumberTextFieldValue.getText())).append(", ");
        sb.append(unitNameTextFieldValue.getText()).append(", ");
        sb.append(Long.valueOf(unitNumberTextFieldValue.getText())).append(", ");
        sb.append(unitLocationTextFieldValue.getText()).append(", ");
        sb.append(cityTextFieldValue.getText()).append(", ");
        sb.append(stateTextFieldValue.getText()).append(", ");
        sb.append(Long.valueOf(zipCodeTextFieldValue.getText())).append(", ");
        sb.append(countryTextFieldValue.getText()).append("");
        return sb.toString();
    }
    
    @Override
    public void preCloseAction() {
    }
    
    public void setAction(DataAction action) {
        this.dataAction = action;
    }
    
    public void showAddressDetails(Address address) throws SQLException {
        this.address = address;
        
        //We save the actual contract information to be able to 
        //check for changes of each Information at validation of Innput.
        saveActualAddressInformation(address);
        
        streetNameTextFieldValue.setText(address.getStreetname());
        houseNumberTextFieldValue.setText(address.getHousenumber().toString());
        unitNameTextFieldValue.setText(address.getUnitname());
        unitLocationTextFieldValue.setText(address.getUnitlocation());
        cityTextFieldValue.setText(address.getCity());
        stateTextFieldValue.setText(address.getState());
        countryTextFieldValue.setText(address.getCountry());

        switch(dataAction) {
            case DataAction.NEW -> {
                houseNumberTextFieldValue.setText("");
                unitNumberTextFieldValue.setText("");
                zipCodeTextFieldValue.setText("");
            }
            case DataAction.EDIT -> {
                houseNumberTextFieldValue.setText(address.getHousenumber().toString());
                unitNumberTextFieldValue.setText(address.getUnitnumber().toString());
                zipCodeTextFieldValue.setText(address.getZipcode().toString());
            }
        }
    }
   
    private void saveActualAddressInformation(Address address) {
        oldStreetName = address.getStreetname();
        oldHouseNumber = address.getHousenumber();
        oldUnitName = address.getUnitname();
        oldUnitNumber = address.getUnitnumber();
        oldUnitLocation = address.getUnitlocation();
        oldCity = address.getCity();
        oldState = address.getState();
        oldZipCode = address.getZipcode();
        oldCountry = address.getCountry();
    }
    
    private void validateNumberInput(String newValue, TextField textField) {
        if (!newValue.matches("\\d*")) {
            textField.setText(newValue.replaceAll("[^\\d]", ""));
        }
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
        boolean r1 = isStreetNameFilled(streetNameTextFieldValue);
        boolean r2 = isHouseNumberFilled(houseNumberTextFieldValue);
        boolean r3 = isUnitNameFilled(unitNameTextFieldValue);
        boolean r4 = isUnitNumberFilled(unitNumberTextFieldValue);
        boolean r5 = isUnitLocationFilled(unitLocationTextFieldValue);
        boolean r6 = isCityFilled(cityTextFieldValue);
        boolean r7 = isStateFilled(stateTextFieldValue);
        boolean r8 = isZipCodeFilled(zipCodeTextFieldValue);
        boolean r9 = isCountryFilled(countryTextFieldValue);
        
        boolean result = r1 && r2 && r3 && r4 && r5 && r6 && r7 && r8 && r9;

        return result;
    }

    private boolean isStreetNameFilled(TextField streetName) {
        return !ControllerUtilities.isNullOrEmpty(streetName.getText());
    }

    private boolean isHouseNumberFilled(TextField houseNumber) {
        return !ControllerUtilities.isNullOrEmpty(houseNumber.getText());
    }

    private boolean isUnitNameFilled(TextField unitName) {
        return !ControllerUtilities.isNullOrEmpty(unitName.getText());
    }

    private boolean isUnitNumberFilled(TextField unitNumber) {
        return !ControllerUtilities.isNullOrEmpty(unitNumber.getText());
    }

    private boolean isUnitLocationFilled(TextField unitLocation) {
        return !ControllerUtilities.isNullOrEmpty(unitLocation.getText());
    }

    private boolean isCityFilled(TextField city) {
        return !ControllerUtilities.isNullOrEmpty(city.getText());
    }

    private boolean isStateFilled(TextField state) {
        return !ControllerUtilities.isNullOrEmpty(state.getText());
    }

    private boolean isZipCodeFilled(TextField zipCode) {
        return !ControllerUtilities.isNullOrEmpty(zipCode.getText());
    }

    private boolean isCountryFilled(TextField country) {
        return !ControllerUtilities.isNullOrEmpty(country.getText());
    }
    
    private boolean isInputUnique() {
        boolean r1 = isStreetNameUnique(streetNameTextFieldValue);
        boolean r2 = isHouseNumberUnique(houseNumberTextFieldValue);
        boolean r3 = isUnitNameUnique(unitNameTextFieldValue);
        boolean r4 = isUnitNumberUnique(unitNumberTextFieldValue);
        boolean r5 = isUnitLocationUnique(unitLocationTextFieldValue);
        boolean r6 = isCityUnique(cityTextFieldValue);
        boolean r7 = isStateUnique(stateTextFieldValue);
        boolean r8 = isZipCodeUnique(zipCodeTextFieldValue);
        boolean r9 = isCountryUnique(countryTextFieldValue);
        
        boolean result = r1 || r2 || r3 || r4 || r5 || r6 || r7 || r8 || r9;

        return result;
    }
    
    private boolean isStreetNameUnique(TextField streetName) {
        List<Address> result = addressData.stream().filter(c -> c.getStreetname().equals(streetName.getText())).toList();
        return result.isEmpty();
    }
    
    private boolean isHouseNumberUnique(TextField houseNumber) {
        List<Address> result = addressData.stream().filter(c -> c.getHousenumber().toString().equals(houseNumber.getText())).toList();
        return result.isEmpty();
    }

    private boolean isUnitNameUnique(TextField unitName) {
        List<Address> result = addressData.stream().filter(c -> c.getUnitname().equals(unitName.getText())).toList();
        return result.isEmpty();
    }

    private boolean isUnitNumberUnique(TextField unitNumber) {
        List<Address> result = addressData.stream().filter(c -> c.getUnitnumber().toString().equals(unitNumber.getText())).toList();
        return result.isEmpty();
    }

    private boolean isUnitLocationUnique(TextField unitLocation) {
        List<Address> result = addressData.stream().filter(c -> c.getUnitlocation().equals(unitLocation.getText())).toList();
        return result.isEmpty();
    }

    private boolean isCityUnique(TextField city) {
        List<Address> result = addressData.stream().filter(c -> c.getCity().equals(city.getText())).toList();
        return result.isEmpty();
    }

    private boolean isStateUnique(TextField state) {
        List<Address> result = addressData.stream().filter(c -> c.getState().equals(state.getText())).toList();
        return result.isEmpty();
    }

    private boolean isZipCodeUnique(TextField zipCode) {
        List<Address> result = addressData.stream().filter(c -> c.getZipcode().toString().equals(zipCode.getText())).toList();
        return result.isEmpty();
    }

    private boolean isCountryUnique(TextField country) {
        List<Address> result = addressData.stream().filter(c -> c.getCountry().equals(country.getText())).toList();
        return result.isEmpty();
    }

    private boolean hasInputChanged() {
        boolean r1 = !oldStreetName.equals(newStreetName);
        boolean r2 = !oldHouseNumber.equals(newHouseNumber);
        boolean r3 = !oldUnitName.equals(newUnitName);
        boolean r4 = !oldUnitNumber.equals(newUnitNumber);
        boolean r5 = !oldUnitLocation.equals(newUnitLocation);
        boolean r6 = !oldCity.equals(newCity);
        boolean r7 = !oldState.equals(newState);
        boolean r8 = !oldZipCode.equals(newZipCode);
        boolean r9 = !oldCountry.equals(newCountry);
        
        boolean result = r1 || r2 || r3 || r4 || r5 || r6 || r7 || r8 || r9;
        
        return result;
    }

}
