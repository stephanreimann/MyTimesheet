package controller;

import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class SplashScreenViewController {

    private Stage primaryStage;
    private ResourceBundle rb;
    
    @FXML
    private Label statusLabel;

    public void setStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }

}