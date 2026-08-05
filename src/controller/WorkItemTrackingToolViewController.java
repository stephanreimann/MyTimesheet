/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.LanguageService;
import service.UndoService;
import utils.EventManager;

/**
 *
 * @author adrest18
 */
class WorkItemTrackingToolViewController implements Initializable, IViewController {

    private final String newResourceKey = "New";
    private final String editResourceKey = "Edit";
    private final String deleteResourceKey = "Delete";
    
    private final String trackingItemShortcutResourceKey = "TrackingItemShortcut";
    private final String trackingItemNameResourceKey = "TrackingItemName";
    private final String trackingItemStartTimeResourceKey  ="TrackingItemStartTime";
    private final String trackingItemEndTimeResourceKey  ="TrackingItemEndTime";
    private final String trackingItemDetailsHeaderResourceKey = "TrackingItemDetailsHeader";
    
    @FXML
    private ToolBar trackingItemToolBar;
    @FXML
    private Label selectedDateLabel;
    @FXML
    private DatePicker selectedDateDatePicker;
    @FXML
    private Label sprintLabel;
    @FXML
    private Label sprintNumberLabel;
    @FXML
    private TableView trackingItemTableView;
    @FXML
    private TableColumn trackingItemShortcutTableColumn;
    @FXML
    private TableColumn trackingItemNameTableColumn;
    @FXML
    private TableColumn trackingItemStartTimeTableColumn;
    @FXML
    private TableColumn trackingItemEndTimeTableColumn;
    @FXML
    private GridPane trackingItemDetailsGridPane;
    @FXML
    private Label trackingItemDetailsHeaderLabel;
    @FXML
    private Label trackingItemLabel;
    @FXML
    private Label trackingItemStartTimeLabel;
    @FXML
    private Label trackingItemEndTimeLabel;
    @FXML
    private Label trackingItemDescriptionLabel;
    @FXML
    private ChoiceBox trackingItemChoiceBox;
    @FXML
    private TextArea trackingItemDescriptionValue;
    
    @FXML
    private Button newButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    
    private final Logger log = LogManager.getLogger(WorkItemTrackingToolViewController.class.getName());

    private Stage primaryStage;
    private final ControllerRepository controllerRepository;
    private final LanguageService languageService;
    private final Connection connection;
    private final UndoService undoService;
    private ResourceBundle rb;
    private EventManager eventManager;

    public WorkItemTrackingToolViewController(ControllerRepository controllerRepository, LanguageService languageService, Connection connection, UndoService undoService) throws SQLException {
        if(controllerRepository == null) throw new NullPointerException("controllerRepository");
        if(languageService == null) throw new NullPointerException("languageService");
        if(connection == null) throw new NullPointerException("connection");
        if(undoService == null) throw new NullPointerException("undoService");

        this.controllerRepository = controllerRepository;
        this.languageService = languageService;
        this.connection = connection;
        this.undoService = undoService;
    }
    
    @FXML
    private void newAction(ActionEvent event) throws SQLException, IOException {

    }
    
    @FXML
    private void editAction(ActionEvent event) throws SQLException, IOException {

    }

    @FXML
    private void deleteAction(ActionEvent event) throws SQLException, IOException {

    }
    
    @FXML
    private void handleOnSelectedDateChangedAction(ActionEvent event) throws SQLException, IOException {

    }
    
    @Override
    public void initialize(URL location, ResourceBundle rb) {
        this.rb = rb;
    }

    @Override
    public void updateGuiItems() {

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
