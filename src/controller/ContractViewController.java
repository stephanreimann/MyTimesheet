/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import commands.contract.*;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.stage.*;
import model.Contract;
import org.apache.logging.log4j.*;
import service.*;
import sqlite.ContractDAO;
import utils.*;

/**
 *
 * @author adrest18
 */
public class ContractViewController implements Initializable, IViewController {

    public enum DataAction { NEW, EDIT, DELETE };
    
    private final String contractNameResourceKey = "ContractName";
    private final String contractDetailsResourceKey = "ContractDetails";
    private final String workhoursResourceKey = "Workhours";
    private final String maxWorkhoursResourceKey = "MaxWorkhours";
    private final String vacationdaysResourceKey = "Vacationdays";
    private final String vacationReconciliationDateResourceKey = "VacationReconciliationDate";
    private final String breakfastOfftimeEndResourceKey = "BreakfastOfftimeEnd";
    private final String breakfastOfftimeStartResourceKey = "BreakfastOfftimeStart";
    private final String lunchOfftimeEndResourceKey = "LunchOfftimeEnd";
    private final String lunchOfftimeStartResourceKey = "LunchOfftimeStart";
    private final String earliestWorktimeStartResourceKey = "EarliestWorktimeStart";
    private final String latestWorktimeEndResourceKey = "LatestWorktimeEnd";
    private final String newResourceKey = "New";
    private final String editResourceKey = "Edit";
    private final String deleteResourceKey = "Delete";
    private final String noContractSelectionAlertTitle = "NoSelectionAlertTitle";
    private final String noContractSelectionAlertHeader = "NoContractSelectionAlertHeader";
    private final String noContractSelectionAlertContent = "NoContractSelectionAlertContent";
    private final String newContractEvent = "NewContract";
    private final String editContractEvent = "EditContract";
    private final String deleteContractEvent = "DeleteContract";
   
    private final Logger log = LogManager.getLogger(ContractViewController.class.getName());
    
    private Stage primaryStage;    
    private final ControllerRepository controllerRepository;
    private final LanguageService languageService;
    private final Connection connection;
    private final UndoService undoService;
    private final PropertiesService propertiesService;    
    private ResourceBundle rb;
    private final ContractDAO contractDao;
    private ObservableList<Contract> contractData;
    private Stage contractDetailsViewDialog;
    public EventManager eventManager;
    
    private final String contractDetailsViewDialogIcon = "icons/app-maid.png";
    private final String contractDetailsViewDialogTitleResourceKey = "ContractDetailsViewTitle";
    private final String contractDetailsViewResource = "/view/ContractDetailsView.fxml";
  
    @FXML
    private TableView<Contract> contractTableView;
    @FXML
    private TableColumn<Contract, String> contractNameTableColumn;
    @FXML
    private Label contractDetailsLabel;
    
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
    private Label contractNameLabelValue;
    @FXML
    private Label workhoursLabelValue;
    @FXML
    private Label maxWorkhoursLabelValue;
    @FXML
    private Label vacationdaysLabelValue;
    @FXML
    private Label vacationReconciliationDateLabelValue;
    @FXML
    private Label breakfastOfftimeEndLabelValue;
    @FXML
    private Label breakfastOfftimeStartLabelValue;
    @FXML
    private Label lunchOfftimeEndLabelValue;
    @FXML
    private Label lunchOfftimeStartLabelValue;
    @FXML
    private Label earliestWorktimeStartLabelValue;
    @FXML
    private Label latestWorktimeEndLabelValue;

    @FXML
    private Button newButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    
    ContractViewController(ControllerRepository controllerRepository, LanguageService languageService, Connection connection, UndoService undoService, PropertiesService propertiesService) throws SQLException {
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
        this.contractDao = new ContractDAO(connection);
        this.contractData = FXCollections.observableArrayList(this.contractDao.selectAll());
        this.eventManager = new EventManager();
        this.eventManager.registerEventType(newContractEvent);
        this.eventManager.registerEventType(editContractEvent);
        this.eventManager.registerEventType(deleteContractEvent);
    }

    @FXML
    private void newContractAction(ActionEvent event) throws SQLException, IOException {
        Contract newContract = new Contract(contractDao.getNextId());
        openContractDetailsDialog(newContract, DataAction.NEW);
        if(isContractValid(newContract)) {
            NewContractCommand cmd = new NewContractCommand(controllerRepository, eventManager, contractTableView, newContract, contractDao);
            undoService.execute(cmd);
        }
    }
    
    @FXML
    private void editContractAction(ActionEvent event) throws SQLException, IOException {
        Contract selectedContract = contractTableView.getSelectionModel().getSelectedItem();
        if(selectedContract != null) {
            Contract originalContract = new Contract(selectedContract);
            openContractDetailsDialog(selectedContract, DataAction.EDIT);
            showContractDetails(selectedContract);
            if(!originalContract.equals(selectedContract)) {
                EditContractCommand cmd = new EditContractCommand(controllerRepository, eventManager, contractTableView, originalContract, selectedContract, contractDao);
                undoService.execute(cmd);
            }
        } else {
            ControllerUtilities.showNoItemSelectedAlert(primaryStage, rb, noContractSelectionAlertTitle, noContractSelectionAlertHeader, noContractSelectionAlertContent);
        }
    }

    @FXML
    private void deleteContractAction(ActionEvent event) throws SQLException, IOException {
        Contract selectedContract = contractTableView.getSelectionModel().getSelectedItem();
        if(selectedContract != null) {
            DeleteContractCommand cmd = new DeleteContractCommand(controllerRepository, eventManager, contractTableView, selectedContract, contractDao);
            undoService.execute(cmd);
        } else {
            ControllerUtilities.showNoItemSelectedAlert(primaryStage, rb, noContractSelectionAlertTitle, noContractSelectionAlertHeader, noContractSelectionAlertContent);
        }
    }
    
    @Override
    public void initialize(URL location, ResourceBundle rb) {
        this.rb = rb;
        
        contractTableView.setItems(contractData);

        contractNameTableColumn.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
        
        showContractDetails(null);

        contractTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showContractDetails(newValue));
                
        languageService.updateGuiItems();        
    }

    @Override
    public void updateGuiItems() {
        contractNameTableColumn.setText(rb.getString(contractNameResourceKey));
        contractDetailsLabel.setText(rb.getString(contractDetailsResourceKey));
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

    public EventManager getEventManager() {
        return eventManager;
    }
    
    private void showContractDetails(Contract contract) {
        if(contract != null) {
            contractNameLabelValue.setText(contract.getName());
            workhoursLabelValue.setText(contract.getWorkhours().toString());
            maxWorkhoursLabelValue.setText(contract.getMaxworkhours().toString());
            vacationdaysLabelValue.setText(contract.getVacationdays().toString());
            vacationReconciliationDateLabelValue.setText(contract.getVacationreconciliationdate());
            breakfastOfftimeEndLabelValue.setText(contract.getBreakfastofftimeend().toString());
            breakfastOfftimeStartLabelValue.setText(contract.getBreakfastofftimestart().toString());
            lunchOfftimeEndLabelValue.setText(contract.getLunchofftimeend().toString());
            lunchOfftimeStartLabelValue.setText(contract.getLunchofftimestart().toString());
            earliestWorktimeStartLabelValue.setText(contract.getEarliestworktimestart().toString());
            latestWorktimeEndLabelValue.setText(contract.getLatestworktimeend().toString());
        } else {
            contractNameLabelValue.setText("");
            workhoursLabelValue.setText("");
            maxWorkhoursLabelValue.setText("");
            vacationdaysLabelValue.setText("");
            vacationReconciliationDateLabelValue.setText("");
            breakfastOfftimeEndLabelValue.setText("");
            breakfastOfftimeStartLabelValue.setText("");
            lunchOfftimeEndLabelValue.setText("");
            lunchOfftimeStartLabelValue.setText("");
            earliestWorktimeStartLabelValue.setText("");
            latestWorktimeEndLabelValue.setText("");
        }
    }

    private void openContractDetailsDialog(Contract contract, DataAction dataAction) throws IOException {
        ContractDetailsViewController contractDetailsViewController = new ContractDetailsViewController(languageService, connection, undoService, contractData);
        controllerRepository.put(ContractDetailsViewController.class.getName(), contractDetailsViewController);

        DialogFactory dialogFactory = new DialogFactory(
            primaryStage, 
            contractDetailsViewDialogTitleResourceKey, 
            contractDetailsViewDialogIcon, 
            contractDetailsViewResource, 
            rb, 
            contractDetailsViewController);
        contractDetailsViewDialog = dialogFactory.create();
        contractDetailsViewDialog.setWidth(400);
        contractDetailsViewDialog.setHeight(450);
        
        contractDetailsViewController.setAction(dataAction);
        contractDetailsViewController.showContractDetails(contract);
                
        ControllerUtilities.CenterOnDialog(primaryStage, contractDetailsViewDialog);
        
        contractDetailsViewDialog.showAndWait();        
    
        controllerRepository.remove(ContractDetailsViewController.class.getName());
    }

    private boolean isContractValid(Contract contract) {
        return !ControllerUtilities.isNullOrEmpty(contract.getName()) &&
               !ControllerUtilities.isNullOrEmpty(contract.getWorkhours().toString()) &&
               !ControllerUtilities.isNullOrEmpty(contract.getMaxworkhours().toString()) &&
               !ControllerUtilities.isNullOrEmpty(contract.getVacationdays().toString()) &&
               !ControllerUtilities.isNullOrEmpty(contract.getVacationreconciliationdate()) &&
               !ControllerUtilities.isNullOrEmpty(contract.getBreakfastofftimeend().toString()) &&
               !ControllerUtilities.isNullOrEmpty(contract.getBreakfastofftimestart().toString()) &&
               !ControllerUtilities.isNullOrEmpty(contract.getLunchofftimeend().toString()) &&
               !ControllerUtilities.isNullOrEmpty(contract.getLunchofftimestart().toString()) &&
               !ControllerUtilities.isNullOrEmpty(contract.getEarliestworktimestart().toString()) &&
               !ControllerUtilities.isNullOrEmpty(contract.getLatestworktimeend().toString());
    }
  
}
