/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.stage.*;
import model.Holyday;
import net.fortuna.ical4j.data.ParserException;
import org.apache.logging.log4j.*;
import service.*;
import sqlite.HolydayDAO;
import utils.CalendarLoader;

/**
 *
 * @author adrest18
 */
class ImportHolydaysViewController implements Initializable, IViewController {

    private final String holydayDateResourceKey = "HolydayDate";
    private final String holydayNameResourceKey = "HolydayName";
    private final String holydayStateResourceKey = "HolydayState";
    private final String selectResourceKey = "Select";
    private final String acceptResourceKey = "Accept";
    private final String cancelResourceKey = "Cancel";
    
    private final Logger log = LogManager.getLogger(ImportHolydaysViewController.class.getName());

    @FXML
    TextField filePathAndNameTextFieldValue;
    @FXML 
    Button selectButton;
    @FXML ListView holydaysListView;
    
    @FXML
    private TableView<Holyday> holydayTableView;
    @FXML
    private TableColumn<Holyday, LocalDate> holydayDateTableColumn;
    @FXML
    private TableColumn<Holyday, String> holydayNameTableColumn;
    @FXML
    private TableColumn<Holyday, String> holydayStateTableColumn;

    @FXML 
    private Button acceptButton;
    @FXML
    private Button cancelButton;
    
    private Stage primaryStage;
    private final LanguageService languageService;
    private final Connection connection;
    private final UndoService undoService;
    private ResourceBundle rb;
    private final ControllerRepository controllerRepository;
    private final ObservableList<Holyday> holydayData;
    private final HolydayDAO holydayDao;
    private final List<String> holydaysToImport;
    
    ImportHolydaysViewController(LanguageService languageService, Connection connection, UndoService undoService, ObservableList<Holyday> holydayData) {
        if(languageService == null) throw new NullPointerException("languageService");
        if(connection == null) throw new NullPointerException("connection");
        if(undoService == null) throw new NullPointerException("undoService");
        if(holydayData == null) throw new NullPointerException("holydayData");
        
        this.languageService = languageService;
        this.connection = connection;
        this.undoService = undoService;
        this.controllerRepository = ControllerRepository.getInstance();
        
        this.holydayData = holydayData;
        this.holydayDao = new HolydayDAO(connection);
        this.holydaysToImport = new ArrayList<>();
    }

    @FXML
    private void selectAction(ActionEvent event) throws IOException, ParserException {
        File choosenFile = initializeFileChooserAndShowIt();
        if(choosenFile != null && choosenFile.exists()) {
            String file = choosenFile.getPath();
            filePathAndNameTextFieldValue.textProperty().unbind();
            filePathAndNameTextFieldValue.setText(file);
            try {
                loadFromFile(filePathAndNameTextFieldValue.getText());
                if(!holydayTableView.getItems().isEmpty()) {
                    acceptButton.setDisable(false);
                } else {
                    acceptButton.setDisable(true);
                }
            } catch(IOException | ParserException ex) {
                log.fatal("Exception: " + ex.getMessage());
                throw ex;
            }
        }    
    }

    private File initializeFileChooserAndShowIt() throws IOException {
        String holydaysDirectory = new File(".").getCanonicalPath();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(rb.getString("FileChooserTitle"));
        fileChooser.setInitialDirectory(new File(holydaysDirectory));
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("ICS files (*.ics)", "*.ics");
        fileChooser.getExtensionFilters().add(extFilter);
        return fileChooser.showOpenDialog(primaryStage);
    }
    
    private void loadFromFile(String filePathAndName) throws IOException, ParserException {
        CalendarLoader calendarLoader = new CalendarLoader();
        calendarLoader.loadHolydays(filePathAndName, "Bayern");
        ObservableList<Holyday> holydays = calendarLoader.getHolydays();    
        holydayTableView.setItems(holydays);
    }
        
    @FXML
    private void acceptAction(ActionEvent event) throws SQLException {
        for(Holyday holyday : holydayTableView.getItems()) {
            holyday.setId(holydayDao.getNextId());
            
            String msgPart = "Holyday: " + holyday.getDate() + ";" + holyday.getName() + ";" + holyday.getState();
            if(!holydayData.contains(holyday)) {
                holydayData.add(holyday);
                holydayDao.create(holyday);
            } else {
                log.warn(msgPart + " allready exists!");
            }
        }
        primaryStage.close();
    }
    
    @FXML
    private void cancelAction(ActionEvent event) {
        primaryStage.close();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.rb = rb;
        
        holydayDateTableColumn.setCellValueFactory(cellData -> cellData.getValue().getDateProperty());
        holydayDateTableColumn.setSortable(false);
        holydayNameTableColumn.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
        holydayNameTableColumn.setSortable(false);
        holydayStateTableColumn.setCellValueFactory(cellData -> cellData.getValue().getStateProperty());
        holydayStateTableColumn.setSortable(false);
                
        acceptButton.setDisable(true);
        Platform.runLater(() -> selectButton.requestFocus());
        
        languageService.updateGuiItems();        
    }

    @Override
    public void updateGuiItems() {
        holydayDateTableColumn.setText(rb.getString(holydayDateResourceKey));
        holydayNameTableColumn.setText(rb.getString(holydayNameResourceKey));
        holydayStateTableColumn.setText(rb.getString(holydayStateResourceKey));
        selectButton.setText(rb.getString(selectResourceKey));
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
            
}
