package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class SplashScreenViewController implements Initializable, IViewController {

    private final String appNameResourceKey = "AppName";
    private final String appVersionResourceKey = "AppVersion";
    
    private Stage primaryStage;
    private ResourceBundle rb;
    
    @FXML
    private Label appNameLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private Label appVersionLabel;

    public void setStatus(String messageResourceKey) {
        if (statusLabel != null) {
            statusLabel.setText(rb.getString(messageResourceKey));
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle rb) {
        this.rb = rb;
    }

    @Override
    public void updateGuiItems() {
        appNameLabel.setText(rb.getString(appNameResourceKey));
        appVersionLabel.setText(rb.getString(appVersionResourceKey));        
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
        ;
    }

}