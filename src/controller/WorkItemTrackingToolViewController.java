/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

/**
 *
 * @author adrest18
 */
class WorkItemTrackingToolViewController implements Initializable, IViewController {

    private Stage primaryStage;
    private ResourceBundle rb;

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
