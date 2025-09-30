/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.util.ResourceBundle;
import javafx.stage.Stage;

/**
 * Interface for all Controller classes.
 *
 * @author ADREST18
 */
public interface IViewController {

    public void updateGuiItems();
    public ResourceBundle getResourceBundle();
    public void setResourceBundle(ResourceBundle rb);
    public void setPrimaryStage(Stage primaryStage);

    public void preCloseAction();
}
