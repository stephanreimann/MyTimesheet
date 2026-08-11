/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import command.holyday.*;
import java.io.IOException;
import sqlite.HolydayDAO;
import model.Holyday;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.*;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.stage.*;
import javafx.util.Callback;
import org.apache.logging.log4j.*;
import service.*;
import utils.ControllerUtilities;
import utils.DialogFactory;
import utils.EventManager;

/**
 *
 * @author adrest18
 */
public class HolydayViewController implements Initializable, IViewController {

    public enum DataAction { NEW, EDIT, DELETE };
    
    private final String holydayResourceKey = "Holyday";
    private final String holydayDetailsResourceKey = "HolydayDetails";    
    private final String holydayDateResourceKey = "HolydayDate";
    private final String holydayNameResourceKey = "HolydayName";
    private final String holydayStateResourceKey = "HolydayState";
    private final String importResourceKey = "Import";
    private final String newResourceKey = "New";
    private final String editResourceKey = "Edit";
    private final String deleteResourceKey = "Delete";
    private final String noHolydaySelectionAlertTitle = "NoSelectionAlertTitle";
    private final String noHolydaySelectionAlertHeader = "NoHolydaySelectionAlertHeader";
    private final String noHolydaySelectionAlertContent = "NoHolydaySelectionAlertContent";
    private final String newHolydayEvent = "NewHolyday";
    private final String editHolydayEvent = "EditHolyday";
    private final String deleteHolydayEvent = "DeleteHolyday";
    private final String importHolydayEvent = "ImportHolyday";

    private final Logger log = LogManager.getLogger(HolydayViewController.class.getName());
    
    private Stage primaryStage;
    private final ControllerRepository controllerRepository;
    private final LanguageService languageService;
    private final Connection connection;
    private final UndoService undoService;
    private final PropertiesService propertiesService;
    private ResourceBundle rb;
    private final HolydayDAO holydayDao;
    private ObservableList<Holyday> holydayData;
    private Stage holydayDetailsViewDialog;
    private Stage importHolydaysViewDialog;
    public EventManager eventManager;

    private final String holydayDetailsViewDialogIcon = "icons/app-maid.png";
    private final String holydayDetailsViewDialogTitleResourceKey = "HolydayDetailsViewTitle";
    private final String holydayDetailsViewResource = "/view/HolydayDetailsView.fxml";
    
    private final String importHolydaysViewDialogIcon = "icons/app-maid.png";
    private final String importHolydaysViewDialogTitleResourceKey = "ImportHolydaysViewTitle";
    private final String importHolydaysViewResource = "/view/ImportHolydaysView.fxml";

    @FXML
    private TableView<Holyday> holydayTableView;
    @FXML
    private TableColumn<Holyday, LocalDate> holydayDateTableColumn;
    @FXML
    private TableColumn<Holyday, String> holydayNameTableColumn;
    @FXML
    private TableColumn<Holyday, String> holydayStateTableColumn;
    @FXML
    private Label holydayDetailsLabel;
    
    @FXML
    private Label holydayDateLabel;
    @FXML
    private Label holydayNameLabel;
    @FXML
    private Label holydayStateLabel;
    
    @FXML
    private Label holydayDateLabelValue;
    @FXML
    private Label holydayNameLabelValue;
    @FXML
    private Label holydayStateLabelValue;
    
    @FXML
    private Button importButton;
    @FXML
    private Button newButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    
    HolydayViewController(ControllerRepository controllerRepository, LanguageService languageService, Connection connection, UndoService undoService, PropertiesService propertiesService) throws SQLException {
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
        this.holydayDao = new HolydayDAO(connection);
        this.holydayData = FXCollections.observableArrayList(this.holydayDao.selectAll());        
        this.eventManager = new EventManager();
        this.eventManager.registerEventType(newHolydayEvent);
        this.eventManager.registerEventType(editHolydayEvent);
        this.eventManager.registerEventType(deleteHolydayEvent);
        this.eventManager.registerEventType(importHolydayEvent);
    }
    
    @FXML
    private void importHolydayAction(ActionEvent event) throws SQLException, IOException {
        openImportHolydaysDialog();
    }

    @FXML
    private void newHolydayAction(ActionEvent event) throws SQLException, IOException {
        Holyday newHolyday = new Holyday(holydayDao.getNextId());
        openHolydayDetailsDialog(newHolyday, DataAction.NEW);
        if(isHolydayValid(newHolyday)) {
            NewHolydayCommand cmd = new NewHolydayCommand(controllerRepository,eventManager, holydayTableView, newHolyday, holydayDao);
            undoService.execute(cmd);
        }
    }
    
    @FXML
    private void editHolydayAction(ActionEvent event) throws SQLException, IOException {
        Holyday selectedHolyday = holydayTableView.getSelectionModel().getSelectedItem();
        if(selectedHolyday != null) {
            Holyday originalHolyday = new Holyday(selectedHolyday);
            openHolydayDetailsDialog(selectedHolyday, DataAction.EDIT);
            showHolydayDetails(selectedHolyday);
            if(!originalHolyday.equals(selectedHolyday)) {
                EditHolydayCommand cmd = new EditHolydayCommand(controllerRepository, eventManager, holydayTableView, originalHolyday, selectedHolyday, holydayDao);
                undoService.execute(cmd);
            }
        } else {
            ControllerUtilities.showNoItemSelectedAlert(primaryStage, rb, noHolydaySelectionAlertTitle, noHolydaySelectionAlertHeader, noHolydaySelectionAlertContent);
        }
    }

    @FXML
    private void deleteHolydayAction(ActionEvent event) throws SQLException, IOException {
        Holyday selectedHolyday = holydayTableView.getSelectionModel().getSelectedItem();
        if(selectedHolyday != null) {
            DeleteHolydayCommand cmd = new DeleteHolydayCommand(controllerRepository, eventManager, holydayTableView, selectedHolyday, holydayDao);
            undoService.execute(cmd);
        } else {
            ControllerUtilities.showNoItemSelectedAlert(primaryStage, rb, noHolydaySelectionAlertTitle, noHolydaySelectionAlertHeader, noHolydaySelectionAlertContent);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle rb) {
        this.rb = rb;
        holydayTableView.setItems(holydayData);

        //HOWTO: Cell Value Factory
        //The cell must know which part of Holyday it needs to display. For all cells in the holydayDateTableColumn this will be the Holyday date value.
        holydayDateTableColumn.setCellValueFactory(cellData -> cellData.getValue().getDateProperty());
        holydayDateTableColumn.setSortable(false);
        holydayNameTableColumn.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
        holydayNameTableColumn.setSortable(false);
        holydayStateTableColumn.setCellValueFactory(cellData -> cellData.getValue().getStateProperty());
        holydayStateTableColumn.setSortable(false);
        
        Optional<Holyday> firstHolyday = holydayData.stream().findFirst();
        if(firstHolyday != null) {
            showHolydayDetails(firstHolyday.get());
            holydayTableView.getSelectionModel().select(0);
        } else {
            showHolydayDetails(null);
        }

        holydayTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showHolydayDetails(newValue));
                
        languageService.updateGuiItems();        
    }

    @Override
    public void updateGuiItems() {
        holydayDateTableColumn.setText(rb.getString(holydayDateResourceKey));
        renderHolydayDateTableColumn();
        holydayNameTableColumn.setText(rb.getString(holydayNameResourceKey));
        holydayStateTableColumn.setText(rb.getString(holydayStateResourceKey));
        holydayDetailsLabel.setText(rb.getString(holydayResourceKey));
        holydayDateLabel.setText(rb.getString(holydayDateResourceKey));
        holydayNameLabel.setText(rb.getString(holydayNameResourceKey));
        holydayStateLabel.setText(rb.getString(holydayStateResourceKey));
        importButton.setText(rb.getString(importResourceKey));
        newButton.setText(rb.getString(newResourceKey));
        editButton.setText(rb.getString(editResourceKey));
        deleteButton.setText(rb.getString(deleteResourceKey));

        if(holydayTableView.getSelectionModel().selectedItemProperty().getValue() != null) {
            DateTimeFormatter dateTimeFormater = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
            holydayDateLabelValue.setText(dateTimeFormater.format(holydayTableView.getSelectionModel().selectedItemProperty().getValue().getDate()));
        }
    }

    //HOWTO: Cell Factory
    //Once the cell has the value, it must know how to display that value. In our case, the Workrecord’s LocalDate value must be formatted and colored depending on the 
    //logic that is implemented
    // Custom rendering of the table cell.
    private void renderHolydayDateTableColumn() {
        Callback<TableColumn<Holyday, LocalDate>, TableCell<Holyday, LocalDate>> dayCellFactory = column -> {
            return new TableCell<Holyday, LocalDate>() {
                DateTimeFormatter dateTimeFormater = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
                @Override
                protected void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(dateTimeFormater.format(item));
                    }
                }
            };
        };
        holydayDateTableColumn.setCellFactory(dayCellFactory);
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
        
    private void showHolydayDetails(Holyday holyday) {
        if(holyday != null) {
            DateTimeFormatter dateTimeFormater = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
            holydayDateLabelValue.setText(dateTimeFormater.format(holyday.getDate()));
            holydayNameLabelValue.setText(holyday.getName());
            holydayStateLabelValue.setText(holyday.getState());
        } else {
            holydayDateLabelValue.setText("");
            holydayNameLabelValue.setText("");
            holydayStateLabelValue.setText("");
        }
    }

    private void openHolydayDetailsDialog(Holyday holyday, DataAction dataAction) throws IOException {
        HolydayDetailsViewController holydayDetailsViewController = (HolydayDetailsViewController)controllerRepository.get(HolydayDetailsViewController.class.getName());
        if(holydayDetailsViewController == null) {
            holydayDetailsViewController = new HolydayDetailsViewController(languageService, connection, undoService, holydayData);
            controllerRepository.put(HolydayDetailsViewController.class.getName(), holydayDetailsViewController);
        }

        DialogFactory dialogFactory = new DialogFactory(
            primaryStage, 
            holydayDetailsViewDialogTitleResourceKey, 
            holydayDetailsViewDialogIcon, 
            holydayDetailsViewResource, 
            rb, 
            holydayDetailsViewController);
        holydayDetailsViewDialog = dialogFactory.create(Modality.WINDOW_MODAL);
        holydayDetailsViewDialog.setWidth(350);
        holydayDetailsViewDialog.setHeight(250);
        
        holydayDetailsViewController.setAction(dataAction);
        holydayDetailsViewController.showHolydayDetails(holyday);
        
        ControllerUtilities.CenterOnDialog(primaryStage, holydayDetailsViewDialog);
        
        holydayDetailsViewDialog.showAndWait();        
    
        controllerRepository.remove(HolydayDetailsViewController.class.getName());
    }

    private boolean isHolydayValid(Holyday holyday) {
        return !ControllerUtilities.isNullOrEmpty(holyday.getName()) &&
               !ControllerUtilities.isNullOrEmpty(holyday.getDate().toString()) &&
               !ControllerUtilities.isNullOrEmpty(holyday.getState());
    }

    private void openImportHolydaysDialog() throws IOException {
        ImportHolydaysViewController importHolydaysViewController = new ImportHolydaysViewController(languageService, connection, undoService, holydayData, eventManager);

        DialogFactory dialogFactory = new DialogFactory(
            primaryStage, 
            importHolydaysViewDialogTitleResourceKey, 
            importHolydaysViewDialogIcon, 
            importHolydaysViewResource, 
            rb, 
            importHolydaysViewController);
        importHolydaysViewDialog = dialogFactory.create(Modality.WINDOW_MODAL);
        importHolydaysViewDialog.setWidth(450);
        importHolydaysViewDialog.setHeight(450);
                
        ControllerUtilities.CenterOnDialog(primaryStage, importHolydaysViewDialog);
        
        importHolydaysViewDialog.showAndWait();            
    }
    
}
