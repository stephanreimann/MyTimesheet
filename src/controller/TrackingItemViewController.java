/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import model.TrackingItem;
import org.apache.logging.log4j.*;
import service.*;
import sqlite.TrackingItemDAO;
import utils.EventManager;

/**
 *
 * @author adrest18
 */
public class TrackingItemViewController implements Initializable, IViewController {

    public enum DataAction { NEW, EDIT, DELETE };

    private final String newResourceKey = "New";
    private final String editResourceKey = "Edit";
    private final String deleteResourceKey = "Delete";
    
    private final String trackingItemDetailsLabelResourceKey = "TrackingItemDetailsLabel";
    
    private final String trackingItemNameResourceKey = "TrackingItemName";
    private final String trackingItemShortcutResourceKey = "TrackingItemShortcut";
    private final String trackingItemDescriptionResourceKey = "TrackingItemDescription";
    
    private final String noTrackingItemSelectionAlertTitle = "NoSelectionAlertTitle";
    private final String noTrackingItemSelectionAlertHeader = "NoWorklocationSelectionAlertHeader";
    private final String noTrackingItemSelectionAlertContent = "NoWorklocationSelectionAlertContent";
    
    private final String trackingItemDetailsViewDialogIcon = "icons/app-maid.png";
    private final String trackingItemDetailsViewDialogTitleResourceKey = "TrackingItemDetailsViewTitle";
    private final String trackingItemDetailsViewResource = "/view/TrackingItemDetailsView.fxml";
    
    private final String newTrackingItemEvent = "NewTrackingItem";
    private final String editTrackingItemEvent = "EditTrackingItem";
    private final String deleteTrackingItemEvent = "DeleteTrackingItem";
    
    private final Logger log = LogManager.getLogger(TrackingItemViewController.class.getName());
    
    private Stage primaryStage;
    private final ControllerRepository controllerRepository;
    private final LanguageService languageService;
    private final Connection connection;
    private final UndoService undoService;
    private final PropertiesService propertiesService;
    private ResourceBundle rb;
    private EventManager eventManager;
    
    private TrackingItemDAO trackingItemDao;
    private ObservableList<TrackingItem> trackingItemData;
    
    private Stage trackingItemDetailsViewDialog;
    
    public TrackingItemViewController(ControllerRepository controllerRepository, LanguageService languageService, Connection connection, UndoService undoService, PropertiesService propertiesService) {
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
