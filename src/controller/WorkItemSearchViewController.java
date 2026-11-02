/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import java.net.URL;
import java.sql.Connection;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.LanguageService;
import service.PropertiesService;
import service.UndoService;

/**
 * FXML Controller class
 *
 * @author adrest18
 */
public class WorkItemSearchViewController implements Initializable, IViewController {

    @SuppressWarnings("unused") 
    private final Logger log = LogManager.getLogger(WorkItemSearchViewController.class);

    @SuppressWarnings("unused") 
    private Stage primaryStage;
    private final ControllerRepository controllerRepository;
    private final LanguageService languageService;
    @SuppressWarnings("unused") 
    private final Connection connection;
    @SuppressWarnings("unused") 
    private final UndoService undoService;
    @SuppressWarnings("unused") 
    private final PropertiesService propertiesService;
    private ResourceBundle rb;
    
    public WorkItemSearchViewController(ControllerRepository controllerRepository, LanguageService languageService, 
                                        Connection connection, UndoService undoService, PropertiesService propertiesService) {
        this.controllerRepository = Objects.requireNonNull(controllerRepository, "controllerRepository");
        this.languageService = Objects.requireNonNull(languageService, "languageService");
        this.connection = Objects.requireNonNull(connection, "connection");
        this.undoService = Objects.requireNonNull(undoService, "undoService");
        this.propertiesService = Objects.requireNonNull(propertiesService, "propertiesService");
        
    }
    
    /**
     * Initializes the controller class.
     * @param location
     * @param rb
     */
    @Override
    public void initialize(URL location, ResourceBundle rb) {
        this.rb = rb;

        languageService.updateGuiItems();          
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
        MainToolBarViewController mainController = (MainToolBarViewController) controllerRepository.get(MainToolBarViewController.class.getName());
        if (mainController != null) {
            mainController.getWorkItemSearchButton().setDisable(false);
        }
    }
    
}
