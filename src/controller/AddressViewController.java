/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import commands.address.*;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.stage.*;
import model.Address;
import org.apache.logging.log4j.*;
import service.*;
import sqlite.AddressDAO;
import utils.*;

/**
 * The business logic for handling address information. 
 * @author adrest18
 */
public class AddressViewController implements Initializable, IViewController {

    public enum DataAction { NEW, EDIT, DELETE };
    
    private final String addressResourceKey = "Address";
    private final String addressDetailsLabelResourceKey = "AddressDetailsLabel";
    private final String streetNameLabelResourceKey = "StreetName";
    private final String houseNumberLabelResourceKey = "HouseNumber";
    private final String unitNameLabelResourceKey = "UnitName";
    private final String unitNumberLabelResourceKey = "UnitNumber";
    private final String unitLocationLabelResourceKey = "UnitLocation";
    private final String cityLabelResourceKey = "City";
    private final String stateLabelResourceKey = "State";
    private final String zipCodeLabelResourceKey = "ZipCode";
    private final String countryLabelResourceKey = "Country";
    private final String newResourceKey = "New";
    private final String editResourceKey = "Edit";
    private final String deleteResourceKey = "Delete";
    private final String noAddressSelectionAlertTitle = "NoSelectionAlertTitle";
    private final String noAddressSelectionAlertHeader = "NoAddressSelectionAlertHeader";
    private final String noAddressSelectionAlertContent = "NoAddressSelectionAlertContent";
    private final String newAddressEvent = "NewAddress";
    private final String editAddressEvent = "EditAddress";
    private final String deleteAddressEvent = "DeleteAddress";

    private final Logger log = LogManager.getLogger(AddressViewController.class.getName());

    private Stage primaryStage;
    private final ControllerRepository controllerRepository;
    private final LanguageService languageService;
    private final Connection connection;
    private final UndoService undoService;
    private final PropertiesService propertiesService;
    private ResourceBundle rb;
    private final AddressDAO addressDao;
    private ObservableList<Address> addressData;
    private Stage addressDetailsViewDialog;
    public EventManager eventManager;
    
    private final String addressDetailsViewDialogIcon = "icons/app-maid.png";
    private final String addressDetailsViewDialogTitleResourceKey = "AddressDetailsViewTitle";
    private final String addressDetailsViewResource = "/view/AddressDetailsView.fxml";
    
    @FXML
    private TableView<Address> addressTableView;
    @FXML
    private TableColumn<Address, String> addressTableColumn;
    @FXML
    private Label addressDetailsLabel;
    
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
    private Label streetNameLabelValue;
    @FXML
    private Label houseNumberLabelValue;
    @FXML
    private Label unitNameLabelValue;
    @FXML
    private Label unitNumberLabelValue;
    @FXML
    private Label unitLocationLabelValue;
    @FXML
    private Label cityLabelValue;
    @FXML
    private Label stateLabelValue;
    @FXML
    private Label zipCodeLabelValue;
    @FXML
    private Label countryLabelValue;

    @FXML
    private Button newButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;


    AddressViewController(ControllerRepository controllerRepository, LanguageService languageService, Connection connection, UndoService undoService, PropertiesService propertiesService) throws SQLException {
        if(controllerRepository == null) throw new NullPointerException("controllerRepository");
        if(languageService == null) throw new NullPointerException("languageService");
        if(connection == null) throw new NullPointerException("connection");
        if(undoService == null) throw new NullPointerException("undoService");
        if(propertiesService == null) throw new NullPointerException("propertiesService");
        
        this.controllerRepository = controllerRepository;
        this.languageService = languageService;
        this.connection = connection;
        this.undoService = undoService;
        this.propertiesService = propertiesService;
        this.addressDao = new AddressDAO(connection);
        this.addressData = FXCollections.observableArrayList(this.addressDao.selectAll());
        this.eventManager = new EventManager();
        this.eventManager.registerEventType(newAddressEvent);
        this.eventManager.registerEventType(editAddressEvent);
        this.eventManager.registerEventType(deleteAddressEvent);
    }

    @FXML
    private void newAddressAction(ActionEvent event) throws SQLException, IOException {
        Address newAddress = new Address(addressDao.getNextId());
        openAddressDetailsDialog(newAddress, DataAction.NEW);
        if(isAddressValid(newAddress)) {
            NewAddressCommand cmd = new NewAddressCommand(controllerRepository, eventManager, addressTableView, newAddress, addressDao);
            undoService.execute(cmd);
        }
    }
    
    @FXML
    private void editAddressAction(ActionEvent event) throws SQLException, IOException {
        Address selectedAddress = addressTableView.getSelectionModel().getSelectedItem();
        if(selectedAddress != null) {
            Address originalAddress = new Address(selectedAddress);
            openAddressDetailsDialog(selectedAddress, DataAction.EDIT);
            showAddressDetails(selectedAddress);
            if(!originalAddress.equals(selectedAddress)) {
                EditAddressCommand cmd = new EditAddressCommand(controllerRepository, eventManager, addressTableView, originalAddress, selectedAddress, addressDao);
                undoService.execute(cmd);
            }
        } else {
            ControllerUtilities.showNoItemSelectedAlert(primaryStage, rb, noAddressSelectionAlertTitle, noAddressSelectionAlertHeader, noAddressSelectionAlertContent);
        }
    }

    @FXML
    private void deleteAddressAction(ActionEvent event) throws SQLException {
        Address selectedAddress = addressTableView.getSelectionModel().getSelectedItem();
        if(selectedAddress != null) {
            DeleteAddressCommand cmd = new DeleteAddressCommand(controllerRepository, eventManager, addressTableView, selectedAddress, addressDao);
            undoService.execute(cmd);
        } else {
            ControllerUtilities.showNoItemSelectedAlert(primaryStage, rb, noAddressSelectionAlertTitle, noAddressSelectionAlertHeader, noAddressSelectionAlertContent);
        }
    }
    
    @Override
    public void initialize(URL location, ResourceBundle rb) {
        this.rb = rb;
        
        addressTableView.setItems(addressData);

        addressTableColumn.setCellValueFactory(cellData -> cellData.getValue().getAddressProperty());
        
        showAddressDetails(null);

        addressTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showAddressDetails(newValue));
                
        languageService.updateGuiItems();        
    }

    @Override
    public void updateGuiItems() {
        addressTableColumn.setText(rb.getString(addressResourceKey));
        addressDetailsLabel.setText(rb.getString(addressDetailsLabelResourceKey));
        streetNameLabel.setText(rb.getString(streetNameLabelResourceKey));
        houseNumberLabel.setText(rb.getString(houseNumberLabelResourceKey));
        unitNameLabel.setText(rb.getString(unitNameLabelResourceKey));
        unitNumberLabel.setText(rb.getString(unitNumberLabelResourceKey));
        unitLocationLabel.setText(rb.getString(unitLocationLabelResourceKey));
        cityLabel.setText(rb.getString(cityLabelResourceKey));
        stateLabel.setText(rb.getString(stateLabelResourceKey));
        zipCodeLabel.setText(rb.getString(zipCodeLabelResourceKey));
        countryLabel.setText(rb.getString(countryLabelResourceKey));
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

    }

    private void showAddressDetails(Address address) {
        if(address != null) {
            streetNameLabelValue.setText(address.getStreetname());
            houseNumberLabelValue.setText(address.getHousenumber().toString());
            unitNameLabelValue.setText(address.getUnitname());
            unitNumberLabelValue.setText(address.getUnitnumber().toString());
            unitLocationLabelValue.setText(address.getUnitlocation());
            cityLabelValue.setText(address.getCity());
            stateLabelValue.setText(address.getState());
            zipCodeLabelValue.setText(address.getZipcode().toString());
            countryLabelValue.setText(address.getCountry());
        } else {
            streetNameLabelValue.setText("");
            houseNumberLabelValue.setText("");
            unitNameLabelValue.setText("");
            unitNumberLabelValue.setText("");
            unitLocationLabelValue.setText("");
            cityLabelValue.setText("");
            stateLabelValue.setText("");
            zipCodeLabelValue.setText("");
            countryLabelValue.setText("");
        }
    }

    private void openAddressDetailsDialog(Address address, DataAction dataAction) throws SQLException, IOException {
        AddressDetailsViewController addressDetailsViewController = new AddressDetailsViewController(languageService, connection, undoService, addressData);
        controllerRepository.put(AddressDetailsViewController.class.getName(), addressDetailsViewController);

        DialogFactory dialogFactory = new DialogFactory(
            primaryStage, 
            addressDetailsViewDialogTitleResourceKey, 
            addressDetailsViewDialogIcon, 
            addressDetailsViewResource, 
            rb, 
            addressDetailsViewController);
        addressDetailsViewDialog = dialogFactory.create();
        addressDetailsViewDialog.setWidth(400);
        addressDetailsViewDialog.setHeight(350);

        addressDetailsViewController.setAction(dataAction);
        addressDetailsViewController.showAddressDetails(address);
        
        ControllerUtilities.CenterOnDialog(primaryStage, addressDetailsViewDialog);
        
        addressDetailsViewDialog.showAndWait();        
    
        controllerRepository.remove(AddressDetailsViewController.class.getName());
    }
    
    public boolean isAddressValid(Address address) {
        boolean r1 = ControllerUtilities.isNullOrEmpty(address.getStreetname());
        boolean r2 = ControllerUtilities.isNullOrEmpty(address.getHousenumber().toString());
        boolean r3 = ControllerUtilities.isNullOrEmpty(address.getUnitname());
        boolean r4 = ControllerUtilities.isNullOrEmpty(address.getUnitnumber().toString());
        boolean r5 = ControllerUtilities.isNullOrEmpty(address.getUnitlocation());
        boolean r6 = ControllerUtilities.isNullOrEmpty(address.getCity());
        boolean r7 = ControllerUtilities.isNullOrEmpty(address.getState());
        boolean r8 = ControllerUtilities.isNullOrEmpty(address.getZipcode().toString());
        boolean r9 = ControllerUtilities.isNullOrEmpty(address.getCountry());
        
        return !(r1 || r2 || r3 || r4 || r5 || r6 || r7 || r8 || r9);
    }
    
}
