/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import model.Sprint;
import org.apache.logging.log4j.*;
import service.*;
import sqlite.SprintDAO;
import utils.EventManager;

/**
 *
 * @author adrest18
 */
public class SprintViewController implements Initializable, IViewController {

    public enum DataAction { NEW, EDIT, DELETE };

    private final String newResourceKey = "New";
    private final String editResourceKey = "Edit";
    private final String deleteResourceKey = "Delete";
    
    private final String sprintDetailsLabelResourceKey = "SprintDetailsLabel";
    
    private final String sprintIdResourceKey = "SprintId";
    private final String startDateResourceKey = "StartDate";
    private final String endDateResourceKey = "EndDate";
    private final String numberOfSprintDaysResourceKey = "NumberOfSprintDays";
    
    private final String noSprintSelectionAlertTitle = "NoSelectionAlertTitle";
    private final String noSprintSelectionAlertHeader = "NoWorklocationSelectionAlertHeader";
    private final String noSprintSelectionAlertContent = "NoWorklocationSelectionAlertContent";
    
    private final String sprintDetailsViewDialogIcon = "icons/app-maid.png";
    private final String sprintDetailsViewDialogTitleResourceKey = "SprintDetailsViewTitle";
    private final String sprintDetailsViewResource = "/view/SprintDetailsView.fxml";
    
    private final String newSprintEvent = "NewSprint";
    private final String editSprintEvent = "EditSprint";
    private final String deleteSprintEvent = "DeleteSprint";
    
    private final Logger log = LogManager.getLogger(SprintViewController.class.getName());
    
    private Stage primaryStage;
    private final ControllerRepository controllerRepository;
    private final LanguageService languageService;
    private final Connection connection;
    private final UndoService undoService;
    private final PropertiesService propertiesService;
    private ResourceBundle rb;
    private EventManager eventManager;
    
    private SprintDAO sprintDao;
    private ObservableList<Sprint> sprintData;
    
    private Stage sprintDetailsViewDialog;
    
    public SprintViewController(ControllerRepository controllerRepository, LanguageService languageService, Connection connection, UndoService undoService, PropertiesService propertiesService) {
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
