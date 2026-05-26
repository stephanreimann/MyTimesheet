/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import adapter.Log4jAdapter;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.stage.*;
import org.apache.logging.log4j.*;
import service.*;
import sqlite.Database;
import utils.ControllerUtilities;
import utils.DialogFactory;
import utils.IEventListener;

/**
 *
 * @author ADREST18
 */
public class MainMenuBarViewController implements Initializable, IViewController {

    private final String fileResourceKey = "File";
    private final String exportResourceKey = "Export";
    private final String exportReferenceDataResourceKey = "Referencedata";
    private final String exportWorkrecordsResourceKey = "Workrecord";
    private final String importResourceKey = "Import";
    private final String importReferenceDataResourceKey = "Referencedata";
    private final String importWorkrecordsResourceKey = "Workrecord";
    private final String exitResourceKey = "Exit";
    private final String editResourceKey = "Edit";
    private final String undoResourceKey = "Undo";
    private final String redoResourceKey = "Redo";
    private final String settingsResourceKey = "Settings";
    private final String openXmlEditorResourceKey = "OpenXmlEditor";
    private final String dataResourceKey = "Referencedata";
    private final String userDataResourceKey = "User";
    private final String roleDataResourceKey = "Role";
    private final String addressDataResourceKey = "Address";
    private final String contractDataResourceKey = "Contract";
    private final String holydayDataResourceKey = "Holyday";
    private final String projectDataResourceKey = "Project";
    private final String worklocationDataResourceKey = "Worklocation";
 
    private final String newUserEvent = "NewUser";
    private final String editUserEvent = "EditUser";
    private final String deleteUserEvent = "DeleteUser";
    
    private final String newContractEvent = "NewContract";
    private final String editContractEvent = "EditContract";
    private final String deleteContractEvent = "DeleteContract";
  
    private final String newProjectEvent = "NewProject";
    private final String editProjectEvent = "EditProject";
    private final String deleteProjectEvent = "DeleteProject";
    
    private final String newWorkLocationEvent = "NewWorkLocation";
    private final String editWorkLocationEvent = "EditWorkLocation";
    private final String deleteWorkLocationEvent = "DeleteWorkLocation";

    private final String newHolydayEvent = "NewHolyday";
    private final String editHolydayEvent = "EditHolyday";
    private final String deleteHolydayEvent = "DeleteHolyday";
    private final String importHolydayEvent = "ImportHolyday";
    
    private final Logger log = LogManager.getLogger(PropertiesService.class.getName());

    private Stage primaryStage;    
    private final LanguageService languageService;
    private final Connection connection;
    private final UndoService undoService;
    private final Application application;
    private final PropertiesService propertiesService;
    private final Log4jAdapter log4jAdapter;
    private ResourceBundle rb;
    
    private final String viewDialogIcon = "icons/app-maid.png";
    
    private final String settingsViewDialogTitleResourceKey = "SettingsViewTitle";
    private final String settingsViewResource = "/view/SettingsView.fxml";

    private final String userViewDialogTitleResourceKey = "UserViewTitle";
    private final String userViewResource = "/view/UserView.fxml";
    
    private final String roleViewDialogTitleResourceKey = "RoleViewTitle";
    private final String roleViewResource = "/view/RoleView.fxml";
    
    private final String addressViewDialogTitleResourceKey = "AddressViewTitle";
    private final String addressViewResource = "/view/AddressView.fxml";

    private final String contractViewDialogTitleResourceKey = "ContractViewTitle";
    private final String contractViewResource = "/view/ContractView.fxml";

    private final String holydayViewDialogTitleResourceKey = "HolydayViewTitle";
    private final String holydayViewResource = "/view/HolydayView.fxml";

    private final String projectViewDialogTitleResourceKey = "ProjectViewTitle";
    private final String projectViewResource = "/view/ProjectView.fxml";

    private final String worklocationViewDialogTitleResourceKey = "WorklocationViewTitle";
    private final String worklocationViewResource = "/view/WorkLocationView.fxml";

    private final String xmlEditorViewDialogTitleResourceKey = "XmlEditorViewTitle";
    private final String xmlEditorViewResource = "/view/XmlEditorView.fxml";
    
    private final ControllerRepository controllerRepository;
    
    private Stage settingsViewDialog;
    private Stage userViewDialog;
    private Stage roleViewDialog;
    private Stage addressViewDialog;
    private Stage contractViewDialog;
    private Stage holydayViewDialog;
    private Stage projectViewDialog;
    private Stage worklocationViewDialog;
    private Stage xmlEditorViewDialog;
    
    @FXML
    private Menu fileMenu;

    @FXML
    private Menu exportMenu;
    @FXML
    private MenuItem exportReferencedataMenuItem;
    @FXML
    private MenuItem exportWorkrecordsMenuItem;

    @FXML
    private Menu importMenu;
    @FXML
    private MenuItem importReferencedataMenuItem;
    @FXML
    private MenuItem importWorkrecordsMenuItem;

    @FXML
    private MenuItem exitMenuItem;
    @FXML
    private Menu editMenu;
    @FXML
    private MenuItem undoMenuItem;
    @FXML
    private MenuItem redoMenuItem;
    @FXML
    private MenuItem settingsMenuItem;
    @FXML
    private MenuItem openXmlEditorMenuItem;
    @FXML
    private MenuItem dataMenu;
    @FXML
    private MenuItem userMenuItem;
    @FXML
    private MenuItem roleMenuItem;
    @FXML
    private MenuItem addressMenuItem;
    @FXML
    private MenuItem contractMenuItem;
    @FXML
    private MenuItem holydayMenuItem;
    @FXML
    private MenuItem projectMenuItem;
    @FXML
    private MenuItem worklocationMenuItem;

    public MainMenuBarViewController(LanguageService languageService, Connection connection, UndoService undoService, Application application, PropertiesService propertiesService, Log4jAdapter log4jAdapter) {
        if(languageService == null) throw new NullPointerException("languageService");
        if(connection == null) throw new NullPointerException("connection");
        if(undoService == null) throw new NullPointerException("undoService");
        if(application == null) throw new NullPointerException("application");
        if(propertiesService == null) throw new NullPointerException("propertiesService");
        if(log4jAdapter == null) throw new NullPointerException("log4jAdapter");
        
        this.languageService = languageService;
        this.connection = connection;        
        this.undoService = undoService;
        this.application = application;
        this.propertiesService = propertiesService;
        this.log4jAdapter = log4jAdapter;
        this.controllerRepository = ControllerRepository.getInstance();
    }

    public void toggleUndoRedoMenuItems() {
        undoMenuItem.disableProperty().set(this.undoService.isUndoStackEmpty());
        redoMenuItem.disableProperty().set(this.undoService.isRedoStackEmpty());
    }

    @FXML
    private void exitAction(ActionEvent event) throws Exception {
        application.stop();
    }
    
    @FXML
    private void exportReferencedata(ActionEvent event) throws Exception {
        Database database = new Database(rb, log4jAdapter);
        
        String[] tables = new String[] { "Address", "Contract", "Holyday", "Project", "Role", "User", "Worklocation" };
        for(var table : tables) {
            database.exportTableInformationToCSV(connection, table, System.getProperty("user.dir").concat("/exports/").concat(table).concat(".csv"));    
        }
    }

    @FXML
    private void exportWorkrecords(ActionEvent event) throws Exception {
        Database database = new Database(rb, log4jAdapter);
        
        String[] tables = new String[] {"Workrecord"};
        for(var table : tables) {
            database.exportTableInformationToCSV(connection, table, System.getProperty("user.dir").concat("/exports/").concat(table).concat(".csv"));    
        }
    }

    @FXML
    private void importReferencedata(ActionEvent event) throws Exception {
        
    }

    @FXML
    private void importWorkrecords(ActionEvent event) throws Exception {

    }

    @FXML
    private void undoAction(ActionEvent event) {
        undoService.undo();
    }

    @FXML
    private void redoAction(ActionEvent event) {
        undoService.redo();
    }

    @FXML
    private void openSettingsDialog(ActionEvent event) throws IOException {
        WorkRecordDetailsViewController workRecordDetailsViewController = (WorkRecordDetailsViewController) controllerRepository.get(WorkRecordDetailsViewController.class.getName());
        SettingsViewController settingsViewController = (SettingsViewController) controllerRepository.get(SettingsViewController.class.getName());
        if(settingsViewController == null) {
            settingsViewController = new SettingsViewController(propertiesService, connection, workRecordDetailsViewController);
            controllerRepository.put(SettingsViewController.class.getName(), settingsViewController);
        }
        
        if(settingsViewDialog == null) {
            DialogFactory dialogFactory = new DialogFactory(
                    primaryStage, 
                    settingsViewDialogTitleResourceKey, 
                    viewDialogIcon, 
                    settingsViewResource, 
                    rb, 
                    settingsViewController);
            settingsViewDialog = dialogFactory.create();
        }
        
        ControllerUtilities.CenterOnDialog(primaryStage, settingsViewDialog);
        
        settingsViewDialog.showAndWait();
    }

    @FXML
    private void openXmlEditor(ActionEvent event) throws IOException {
        XmlEditorViewController xmlEditorViewController = new XmlEditorViewController(languageService, undoService);

        DialogFactory dialogFactory = new DialogFactory(
            primaryStage, 
            xmlEditorViewDialogTitleResourceKey, 
            viewDialogIcon, 
            xmlEditorViewResource, 
            rb, 
            xmlEditorViewController);
        xmlEditorViewDialog = dialogFactory.create();
        xmlEditorViewDialog.setWidth(650);
        xmlEditorViewDialog.setHeight(450);
                
        ControllerUtilities.CenterOnDialog(primaryStage, xmlEditorViewDialog);
        
        xmlEditorViewDialog.showAndWait();            
    }
    
    @FXML
    private void openUserDialog(ActionEvent event) throws IOException, SQLException
    {
        UserViewController userViewController = (UserViewController) controllerRepository.get(UserViewController.class.getName());
        if(userViewController == null) {
            userViewController = new UserViewController(controllerRepository, languageService, connection, undoService, propertiesService);
            controllerRepository.put(UserViewController.class.getName(), userViewController);
            IEventListener workRecordViewController = (IEventListener) controllerRepository.get(WorkRecordViewController.class.getName());
            userViewController.eventManager.subscribeEventToListener(newUserEvent, workRecordViewController);
            userViewController.eventManager.subscribeEventToListener(editUserEvent, workRecordViewController);
            userViewController.eventManager.subscribeEventToListener(deleteUserEvent, workRecordViewController);
        }
        
        if(userViewDialog == null) {
            DialogFactory dialogFactory = new DialogFactory(
                    primaryStage, 
                    userViewDialogTitleResourceKey, 
                    viewDialogIcon, 
                    userViewResource, 
                    rb, 
                    userViewController);
            userViewDialog = dialogFactory.create();
        }
        
        ControllerUtilities.CenterOnDialog(primaryStage, userViewDialog);
        
        userViewDialog.showAndWait();
    }
    
    @FXML
    private void openRoleDialog(ActionEvent event) throws IOException, SQLException
    {
        RoleViewController roleViewController = (RoleViewController) controllerRepository.get(RoleViewController.class.getName());
        if(roleViewController == null) {
            roleViewController = new RoleViewController(controllerRepository, languageService, connection, undoService, propertiesService);
            controllerRepository.put(RoleViewController.class.getName(), roleViewController);
        }
        
        if(roleViewDialog == null) {
            DialogFactory dialogFactory = new DialogFactory(
                    primaryStage, 
                    roleViewDialogTitleResourceKey, 
                    viewDialogIcon, 
                    roleViewResource, 
                    rb, 
                    roleViewController);
            roleViewDialog = dialogFactory.create();
            roleViewDialog.setWidth(750);
            roleViewDialog.setHeight(500);
        }
        
        ControllerUtilities.CenterOnDialog(primaryStage, roleViewDialog);

        roleViewDialog.showAndWait();
    }
    
    @FXML
    private void openAddressDialog(ActionEvent event) throws SQLException, IOException
    {
        AddressViewController addressViewController = (AddressViewController) controllerRepository.get(AddressViewController.class.getName());
        if(addressViewController == null) {
            addressViewController = new AddressViewController(controllerRepository, languageService, connection, undoService, propertiesService);
            controllerRepository.put(AddressViewController.class.getName(), addressViewController);
        }
        
        if(addressViewDialog == null) {
            DialogFactory dialogFactory = new DialogFactory(
                    primaryStage, 
                    addressViewDialogTitleResourceKey, 
                    viewDialogIcon, 
                    addressViewResource, 
                    rb, 
                    addressViewController);
            addressViewDialog = dialogFactory.create();
            addressViewDialog.setWidth(750);
            addressViewDialog.setHeight(500);
        }
        
        ControllerUtilities.CenterOnDialog(primaryStage, addressViewDialog);
        
        addressViewDialog.showAndWait();
    }
    
    @FXML
    private void openContractDialog(ActionEvent event) throws SQLException, IOException
    {
        ContractViewController contractViewController = (ContractViewController) controllerRepository.get(ContractViewController.class.getName());
        if(contractViewController == null) {
            contractViewController = new ContractViewController(controllerRepository, languageService, connection, undoService, propertiesService);
            controllerRepository.put(ContractViewController.class.getName(), contractViewController);
            IEventListener workRecordDetailsViewController = (IEventListener) controllerRepository.get(WorkRecordDetailsViewController.class.getName());
            contractViewController.getEventManager().subscribeEventToListener(newContractEvent, workRecordDetailsViewController);
            contractViewController.getEventManager().subscribeEventToListener(editContractEvent, workRecordDetailsViewController);
            contractViewController.getEventManager().subscribeEventToListener(deleteContractEvent, workRecordDetailsViewController);
        }

        if(contractViewDialog == null) {
            DialogFactory dialogFactory = new DialogFactory(
                    primaryStage, 
                    contractViewDialogTitleResourceKey, 
                    viewDialogIcon, 
                    contractViewResource, 
                    rb, 
                    contractViewController);
            contractViewDialog = dialogFactory.create();
            contractViewDialog.setWidth(750);
            contractViewDialog.setHeight(500);
        }

        ControllerUtilities.CenterOnDialog(primaryStage, contractViewDialog);
        
        contractViewDialog.showAndWait();
    }

    @FXML
    private void openHolydayDialog(ActionEvent event) throws SQLException, IOException
    {
        HolydayViewController holydayViewController = (HolydayViewController) controllerRepository.get(HolydayViewController.class.getName());
        if(holydayViewController == null) {
            holydayViewController = new HolydayViewController(controllerRepository, languageService, connection, undoService, propertiesService);
            controllerRepository.put(HolydayViewController.class.getName(), holydayViewController);
            WorkRecordViewController workRecordViewController = (WorkRecordViewController)this.controllerRepository.get(WorkRecordViewController.class.getName());
            holydayViewController.getEventManager().subscribeEventToListener(newHolydayEvent, workRecordViewController);
            holydayViewController.getEventManager().subscribeEventToListener(editHolydayEvent, workRecordViewController);
            holydayViewController.getEventManager().subscribeEventToListener(deleteHolydayEvent, workRecordViewController);
            holydayViewController.getEventManager().subscribeEventToListener(importHolydayEvent, workRecordViewController);
        }
        
        if(holydayViewDialog == null) {
            DialogFactory dialogFactory = new DialogFactory(
                    primaryStage, 
                    holydayViewDialogTitleResourceKey, 
                    viewDialogIcon, 
                    holydayViewResource, 
                    rb, 
                    holydayViewController);
            holydayViewDialog = dialogFactory.create();
            holydayViewDialog.setWidth(700);
            holydayViewDialog.setHeight(500);
        }

        ControllerUtilities.CenterOnDialog(primaryStage, holydayViewDialog);
        
        holydayViewDialog.showAndWait();
    }

    @FXML
    private void openProjectDialog(ActionEvent event) throws SQLException, IOException
    {
        ProjectViewController projectViewController = (ProjectViewController) controllerRepository.get(ProjectViewController.class.getName());
        if(projectViewController == null) {
            projectViewController = new ProjectViewController(controllerRepository, languageService, connection, undoService, propertiesService);
            controllerRepository.put(ProjectViewController.class.getName(), projectViewController);
            WorkRecordDetailsViewController workRecordDetailsViewController = (WorkRecordDetailsViewController)this.controllerRepository.get(WorkRecordDetailsViewController.class.getName());
            projectViewController.getEventManager().subscribeEventToListener(newProjectEvent, workRecordDetailsViewController);
            projectViewController.getEventManager().subscribeEventToListener(editProjectEvent, workRecordDetailsViewController);
            projectViewController.getEventManager().subscribeEventToListener(deleteProjectEvent, workRecordDetailsViewController);
        }
        
        if(projectViewDialog == null) {
            DialogFactory dialogFactory = new DialogFactory(
                    primaryStage, 
                    projectViewDialogTitleResourceKey, 
                    viewDialogIcon, 
                    projectViewResource, 
                    rb, 
                    projectViewController);
            projectViewDialog = dialogFactory.create();
            projectViewDialog.setWidth(700);
            projectViewDialog.setHeight(500);
        }

        ControllerUtilities.CenterOnDialog(primaryStage, projectViewDialog);
        
        projectViewDialog.showAndWait();
    }

    @FXML
    private void openWorklocationDialog(ActionEvent event) throws IOException, SQLException
    {
        WorkLocationViewController worklocationViewController = (WorkLocationViewController) controllerRepository.get(WorkLocationViewController.class.getName());
        if(worklocationViewController == null) {
            worklocationViewController = new WorkLocationViewController(controllerRepository, languageService, connection, undoService, propertiesService);
            controllerRepository.put(WorkLocationViewController.class.getName(), worklocationViewController);
            WorkRecordDetailsViewController workRecordDetailsViewController = (WorkRecordDetailsViewController)this.controllerRepository.get(WorkRecordDetailsViewController.class.getName());
            worklocationViewController.getEventManager().subscribeEventToListener(newWorkLocationEvent, workRecordDetailsViewController);
            worklocationViewController.getEventManager().subscribeEventToListener(editWorkLocationEvent, workRecordDetailsViewController);
            worklocationViewController.getEventManager().subscribeEventToListener(deleteWorkLocationEvent, workRecordDetailsViewController);

            UserInfoViewController userInfoViewController = (UserInfoViewController)this.controllerRepository.get(UserInfoViewController.class.getName());
            worklocationViewController.getEventManager().subscribeEventToListener(newWorkLocationEvent, userInfoViewController);
            worklocationViewController.getEventManager().subscribeEventToListener(editWorkLocationEvent, userInfoViewController);
            worklocationViewController.getEventManager().subscribeEventToListener(deleteWorkLocationEvent, userInfoViewController);
        }
        
        if(worklocationViewDialog == null) {
            DialogFactory dialogFactory = new DialogFactory(
                    primaryStage, 
                    worklocationViewDialogTitleResourceKey, 
                    viewDialogIcon, 
                    worklocationViewResource, 
                    rb, 
                    worklocationViewController);
            worklocationViewDialog = dialogFactory.create();
            worklocationViewDialog.setWidth(750);
            worklocationViewDialog.setHeight(500);
        }
        
        ControllerUtilities.CenterOnDialog(primaryStage, worklocationViewDialog);

        worklocationViewDialog.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        languageService.updateGuiItems();
        this.rb = rb;
    }

    @Override
    public void updateGuiItems() {
        fileMenu.setText(rb.getString(fileResourceKey));
        exitMenuItem.setText(rb.getString(exitResourceKey));
        exportMenu.setText(rb.getString(exportResourceKey));
        exportReferencedataMenuItem.setText(rb.getString(exportReferenceDataResourceKey));
        exportWorkrecordsMenuItem.setText(rb.getString(exportWorkrecordsResourceKey));
        importMenu.setText(rb.getString(importResourceKey));
        importReferencedataMenuItem.setText(rb.getString(importReferenceDataResourceKey));
        importWorkrecordsMenuItem.setText(rb.getString(importWorkrecordsResourceKey));
                
        editMenu.setText(rb.getString(editResourceKey));
        undoMenuItem.setText(rb.getString(undoResourceKey));
        redoMenuItem.setText(rb.getString(redoResourceKey));
        settingsMenuItem.setText(rb.getString(settingsResourceKey));
        openXmlEditorMenuItem.setText(rb.getString(openXmlEditorResourceKey));
        
        
        dataMenu.setText(rb.getString(dataResourceKey));
        userMenuItem.setText(rb.getString(userDataResourceKey));
        roleMenuItem.setText(rb.getString(roleDataResourceKey));
        addressMenuItem.setText(rb.getString(addressDataResourceKey));
        contractMenuItem.setText(rb.getString(contractDataResourceKey));
        holydayMenuItem.setText(rb.getString(holydayDataResourceKey));
        projectMenuItem.setText(rb.getString(projectDataResourceKey));
        worklocationMenuItem.setText(rb.getString(worklocationDataResourceKey));
        
        if(settingsViewDialog != null) {
            settingsViewDialog.setTitle(rb.getString(settingsViewDialogTitleResourceKey));
        }
        if(userViewDialog != null) {
            userViewDialog.setTitle(rb.getString(userViewDialogTitleResourceKey));
        }
        if(worklocationViewDialog != null) {
            worklocationViewDialog.setTitle(rb.getString(worklocationViewDialogTitleResourceKey));
        }
        if(addressViewDialog != null) {
            addressViewDialog.setTitle(rb.getString(addressViewDialogTitleResourceKey));
        }
        if(contractViewDialog != null) {
            contractViewDialog.setTitle(rb.getString(contractViewDialogTitleResourceKey));
        }
        if(holydayViewDialog != null) {
            holydayViewDialog.setTitle(rb.getString(holydayViewDialogTitleResourceKey));
        }
        if(projectViewDialog != null) {
            projectViewDialog.setTitle(rb.getString(projectViewDialogTitleResourceKey));
        }
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
    
}
