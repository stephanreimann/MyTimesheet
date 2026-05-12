/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;
import javafx.fxml.*;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import service.LanguageService;

/**
 * FXML Controller class
 * @author ADREST18
 */
public class MainViewController implements Initializable, IViewController {

    private final String appNameResourceKey = "AppName";
    
    private Stage primaryStage;
    private final LanguageService languageService;
    private final Connection connection;
    private ResourceBundle rb;

    @FXML
    private BorderPane borderPane;
    @FXML
    private VBox topBorderPaneVBox;
    @FXML
    private VBox leftBorderPaneVBox;
    @FXML
    private VBox rightBorderPaneVBox;
    @FXML
    private VBox bottomBorderPaneVBox;
    @FXML
    private SplitPane centerBorderPaneSplitPane;

    public MainViewController(LanguageService languageService, Connection connection) {
        if(languageService == null) throw new NullPointerException("languageService");
        if(connection == null) throw new NullPointerException("connection");

        this.languageService = languageService;
        this.connection = connection;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.rb = rb;
        languageService.updateGuiItems();
    }

    @Override
    public void updateGuiItems() {
        this.primaryStage.setTitle(rb.getString(appNameResourceKey));
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
        
    public BorderPane getBorderPane() {
        return borderPane;
    }

    public VBox getTopBorderPaneVBox() {
        return topBorderPaneVBox;
    }

    public SplitPane getCenterBorderPaneSplitPane() {
        return centerBorderPaneSplitPane;
    }
    
    public VBox getLeftBorderPaneVBox() {
        return leftBorderPaneVBox;
    }

    public VBox getRightBorderPaneVBox() {
        return rightBorderPaneVBox;
    }
    
    public VBox getBottomBorderPaneVBox() {
        return bottomBorderPaneVBox;
    }

}
