/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.LanguageService;
import service.PropertiesService;
import service.UndoService;

/**
 *
 * @author adrest18
 */
public class TrackingItemViewController implements Initializable, IViewController {

    private final Logger log = LogManager.getLogger(TrackingItemViewController.class.getName());
    
    private Stage primaryStage;
    private final ControllerRepository controllerRepository;
    private final LanguageService languageService;
    private final Connection connection;
    private final UndoService undoService;
    private final PropertiesService propertiesService;
    private ResourceBundle rb;
    
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
