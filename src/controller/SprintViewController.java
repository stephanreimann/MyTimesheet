/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import service.LanguageService;
import service.PropertiesService;
import service.UndoService;

/**
 *
 * @author adrest18
 */
public class SprintViewController implements Initializable, IViewController {

    private Stage primaryStage;
    private final ControllerRepository controllerRepository;
    private final LanguageService languageService;
    private final Connection connection;
    private final UndoService undoService;
    private final PropertiesService propertiesService;
    private ResourceBundle rb;
    
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

    }

    @Override
    public void preCloseAction() {

    }
    
}
