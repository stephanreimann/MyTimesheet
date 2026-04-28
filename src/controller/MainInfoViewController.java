/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import adapter.Log4jAdapter;
import commands.*;
import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import javafx.beans.value.ObservableValue;
import javafx.collections.*;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.*;
import service.*;

@SuppressWarnings("unused")
/**
 *
 * @author adrest18
 */
public class MainInfoViewController implements Initializable, IViewController {

    private final String toggleButtonStyle = "-fx-border-radius: 3; "
                                           + "-fx-background-radius: 3; "
                                           + "-fx-background-color: AQUAMARINE; "
                                           + "-fx-border-color: LIGHTGRAY;";
    
    private final String infoResourceKey = "GeneralInfo";
    private final String clearLogResourceKey = "ClearLog";
    private final String filterResourceKey = "FilterLable";
    private final String resetFilterResourceKey = "ResetFilter";
    private final String undoRedoInfoResourceKey = "UndoRedoInfo";
    private final String undoStackResourceKey = "UndoStack";
    private final String undoResourceKey = "Undo";
    private final String redoStackResourceKey = "RedoStack";
    private final String redoResourceKey = "Redo";
    
    private Stage primaryStage;
    private final LanguageService languageService;
    private final Connection connection;    
    private ResourceBundle rb;
    private ObservableList<String> infoViewEntries;
    private final UndoService undoService;
    private final Log4jAdapter log4jAdapter;
    private final ControllerRepository controllerRepository;
    
    private final Predicate<String> infoPredicate = str -> str.contains("INFO");
    private final Predicate<String> debugPredicate = str -> str.contains("DEBUG");
    private final Predicate<String> warningPredicate = str -> str.contains("WARN");
    private final Predicate<String> errorPredicate = str -> str.contains("ERROR");
    private final Predicate<String> fatalPredicate = str -> str.contains("FATAL");
    private final Predicate<String> allPredicate = str -> true;
    private final Predicate<String> nonPredicate = str -> false;

    @FXML
    private Tab generalInfoTab;
    @FXML
    private Button undoButton;
    @FXML
    private Tooltip undoTooltip;
    @FXML
    private Button redoButton;
    @FXML
    private Tooltip redoTooltip;    
    @FXML
    private ScrollPane logScrollPane;
    @FXML
    private Label filterLable;
    @FXML
    private ListView<String> logListView;
    @FXML
    private Button clearLogListButton;
    @FXML
    private ToggleButton debugToggleButton;
    @FXML
    private ToggleButton infoToggleButton;
    @FXML
    private ToggleButton warningToggleButton;
    @FXML
    private ToggleButton errorToggleButton;
    @FXML
    private ToggleButton fatalToggleButton;
    @FXML
    private Button resetFilterButton;
    @FXML
    private Tab undoRedoTab;
    @FXML
    private SplitPane undoRedoSplitPane;
    @FXML
    private Label undoStackLabel;
    @FXML
    private ScrollPane undoScrollPane;
    @FXML
    private ListView<String> undoListView;
    @FXML
    private Label redoStackLabel;
    @FXML
    private ScrollPane redoScrollPane;
    @FXML
    private ListView<String> redoListView;
    
    public MainInfoViewController(LanguageService languageService, Connection connection, UndoService undoService, Log4jAdapter log4jAdapter) {
        if(languageService == null) throw new NullPointerException("languageService");
        if(connection == null) throw new NullPointerException("connection");
        if(undoService == null) throw new NullPointerException("undoService");
        if(log4jAdapter == null) throw new NullPointerException("log4jAdapter");
        
        this.languageService = languageService;
        this.connection = connection;
        this.undoService = undoService;
        this.log4jAdapter = log4jAdapter;
        this.controllerRepository = ControllerRepository.getInstance();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if(url == null) throw new NullPointerException("url");
        if(rb == null) throw new NullPointerException("rb");
        
        this.rb = rb;
        languageService.updateGuiItems();
      
        initListChangeListener();
        initInfoView();
        
        Level rootLoggerLevel = getRootLoggerLevel();
        initInfoViewButtons(rootLoggerLevel);

        undoListView.setItems(undoService.getUndoStackComments());
        redoListView.setItems(undoService.getRedoStackComments());
    }

    private Level getRootLoggerLevel() {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration config = context.getConfiguration();
        LoggerConfig rootLoggerConfig = config.getRootLogger();
        return rootLoggerConfig.getLevel();
    }

    private void initInfoViewButtons(Level rootLoggerLevel) {
        switch(rootLoggerLevel.toString()) {
            case "ALL" ->
            {
                debugToggleButton.setDisable(false);
                infoToggleButton.setDisable(false);
                warningToggleButton.setDisable(false);
                errorToggleButton.setDisable(false);
                fatalToggleButton.setDisable(false);
                clearLogListButton.setDisable(false);
                resetFilterButton.setDisable(false);
            }
            case "TRACE" -> {
                debugToggleButton.setDisable(false);
                infoToggleButton.setDisable(false);
                warningToggleButton.setDisable(false);
                errorToggleButton.setDisable(false);
                fatalToggleButton.setDisable(false);
                clearLogListButton.setDisable(false);
                resetFilterButton.setDisable(false);
            }
            case "DEBUG" -> {
                debugToggleButton.setDisable(false);
                infoToggleButton.setDisable(false);
                warningToggleButton.setDisable(false);
                errorToggleButton.setDisable(false);
                fatalToggleButton.setDisable(false);
                clearLogListButton.setDisable(false);
                resetFilterButton.setDisable(false);
            }
            case "INFO" -> {
                debugToggleButton.setDisable(true);
                infoToggleButton.setDisable(false);
                warningToggleButton.setDisable(false);
                errorToggleButton.setDisable(false);
                fatalToggleButton.setDisable(false);
                clearLogListButton.setDisable(false);
                resetFilterButton.setDisable(false);
            }
            case "WARN" -> {
                debugToggleButton.setDisable(true);
                infoToggleButton.setDisable(true);
                warningToggleButton.setDisable(false);
                errorToggleButton.setDisable(false);
                fatalToggleButton.setDisable(false);
                clearLogListButton.setDisable(false);
                resetFilterButton.setDisable(false);
            }
            case "ERROR" -> {
                debugToggleButton.setDisable(true);
                infoToggleButton.setDisable(true);
                warningToggleButton.setDisable(true);
                errorToggleButton.setDisable(false);
                fatalToggleButton.setDisable(false);
                clearLogListButton.setDisable(false);
                resetFilterButton.setDisable(false);
            }
            case "FATAL" -> {
                debugToggleButton.setDisable(true);
                infoToggleButton.setDisable(true);
                warningToggleButton.setDisable(true);
                errorToggleButton.setDisable(true);
                fatalToggleButton.setDisable(false);
                clearLogListButton.setDisable(false);
                resetFilterButton.setDisable(false);
            }
            case "OFF" -> {
                debugToggleButton.setDisable(true);
                infoToggleButton.setDisable(true);
                warningToggleButton.setDisable(true);
                errorToggleButton.setDisable(true);
                fatalToggleButton.setDisable(true);
                clearLogListButton.setDisable(true);
                resetFilterButton.setDisable(true);
            }
            default -> {
                debugToggleButton.setDisable(false);
                infoToggleButton.setDisable(false);
                warningToggleButton.setDisable(false);
                errorToggleButton.setDisable(false);
                fatalToggleButton.setDisable(false);
                clearLogListButton.setDisable(false);
                resetFilterButton.setDisable(false);
            }
        }        
    }
    
    private void initListChangeListener() {
        infoViewEntries = log4jAdapter.getInfoViewLogAppender().GetInfoViewEntries();
        infoViewEntries.addListener((ListChangeListener.Change<? extends String> c) -> {
            logListView.scrollTo(logListView.getItems().size() - 1);
        });
    }    

    private void initInfoView() {
        FilteredList<String> filteredData = new FilteredList<>(infoViewEntries);
        initToggleButtonChangedListener(infoToggleButton, filteredData);
        initToggleButtonChangedListener(debugToggleButton, filteredData);
        initToggleButtonChangedListener(warningToggleButton, filteredData);
        initToggleButtonChangedListener(errorToggleButton, filteredData);
        initToggleButtonChangedListener(fatalToggleButton, filteredData);
        
        if(logListView != null) {
            logListView.setItems(filteredData);
        }
        if(logScrollPane != null) {
            logScrollPane.setContent(logListView);
        }
        if(infoToggleButton != null) { 
            infoToggleButton.setSelected(Boolean.FALSE);
        }
        if(debugToggleButton != null) { 
            debugToggleButton.setSelected(Boolean.FALSE);
        }
        if(warningToggleButton != null) { 
            warningToggleButton.setSelected(Boolean.FALSE);
        }
        if(errorToggleButton != null) { 
            errorToggleButton.setSelected(Boolean.FALSE);
        }
        if(fatalToggleButton != null) { 
            fatalToggleButton.setSelected(Boolean.FALSE);
        }
    }

    @FXML
    void handleClearLogList(ActionEvent event) {
        ClearLogListCommand cmd = new ClearLogListCommand(controllerRepository, infoViewEntries, log4jAdapter);
        undoService.execute(cmd);
        toggleUndoRedoButtons();
    }
    @FXML
    void handleResetFilter(ActionEvent event) {
        ResetFilterCommand cmd = new ResetFilterCommand(controllerRepository, log4jAdapter);
        undoService.execute(cmd);
        toggleUndoRedoButtons();
    }   
    
    @FXML
    private void undoAction(ActionEvent event) {
        undoService.undo();
        toggleUndoRedoButtons();
    }

    @FXML
    private void redoAction(ActionEvent event) {
        undoService.redo();
        toggleUndoRedoButtons();
    }
    
    @Override
    public void updateGuiItems() {
        toggleUndoRedoButtons();
        generalInfoTab.setText(rb.getString(infoResourceKey));
        clearLogListButton.setText(rb.getString(clearLogResourceKey));
        filterLable.setText(rb.getString(filterResourceKey));
        resetFilterButton.setText(rb.getString(resetFilterResourceKey));
        undoRedoTab.setText(rb.getString(undoRedoInfoResourceKey));
        undoStackLabel.setText(rb.getString(undoStackResourceKey));
        redoStackLabel.setText(rb.getString(redoStackResourceKey));
        undoButton.setText(rb.getString(undoResourceKey));
        undoTooltip.setText(rb.getString(undoResourceKey));
        redoButton.setText(rb.getString(redoResourceKey));
        redoTooltip.setText(rb.getString(redoResourceKey));        
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

    public void toggleUndoRedoButtons() {
        undoButton.disableProperty().set(undoService.isUndoStackEmpty());
        redoButton.disableProperty().set(undoService.isRedoStackEmpty());
    }
    
    public Boolean getInfoToggleButtonState() {
        return infoToggleButton.selectedProperty().getValue(); 
    }

    public void setInfoToggleButtonState(Boolean state) {
        if(infoToggleButton != null) {
            infoToggleButton.selectedProperty().set(state);
        }
    }
    
    public Boolean getDebugToggleButtonState() {
        return debugToggleButton.selectedProperty().getValue();        
    }
  
    public void setDebugToggleButtonState(Boolean state) {
        if(debugToggleButton != null) {
            debugToggleButton.selectedProperty().setValue(state);
        }
    }
    
    public Boolean getWarningToggleButtonState() {
        return warningToggleButton.selectedProperty().getValue();
    }

    public void setWarningToggleButtonState(Boolean state) {
        if(warningToggleButton != null) {
            warningToggleButton.selectedProperty().setValue(state);
        }
    }

    public Boolean getErrorToggleButtonState() {
        return errorToggleButton.selectedProperty().getValue();
    }

    public void setErrorToggleButtonState(Boolean state) {
        if(errorToggleButton != null) {
            errorToggleButton.selectedProperty().setValue(state);
        }
    }

    public Boolean getFatalToggleButtonState() {
        return fatalToggleButton.selectedProperty().getValue();
    }

    public void setFatalToggleButtonState(Boolean state) {
        if(fatalToggleButton != null) {
            fatalToggleButton.selectedProperty().setValue(state);
        }
    }

    private void initToggleButtonChangedListener(ToggleButton toggleButton, FilteredList<String> filteredData) {
        if(toggleButton != null) {
            toggleButton.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                if(newValue) {
                    toggleButton.setStyle(toggleButtonStyle);
                } else {
                    toggleButton.setStyle(null);
                }
                filterData(filteredData);
            });
        }
    }
    
    private void filterData(FilteredList<String> filteredData) {
        Predicate<String> filter = nonPredicate;
        
        if(infoToggleButton.selectedProperty().getValue()) {
            filter = filter.or(infoPredicate);
        }
        if(debugToggleButton.selectedProperty().getValue()) {
            filter = filter.or(debugPredicate);
        }
        if(warningToggleButton.selectedProperty().getValue()) {
            filter = filter.or(warningPredicate);
        }
        if(errorToggleButton.selectedProperty().getValue()) {
            filter = filter.or(errorPredicate);
        }
        if(fatalToggleButton.selectedProperty().getValue()) {
            filter = filter.or(fatalPredicate);
        }

        filteredData.setPredicate(filter);
    }

    @Override
    public void preCloseAction() {

    }

}
