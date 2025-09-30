/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import controller.IViewController;
import java.io.IOException;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.stage.*;

/**
 *
 * @author adrest18
 */
public class ControllerUtilities {
    
    public static void showNoItemSelectedAlert(Stage primaryStage, ResourceBundle rb, String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.initStyle(StageStyle.UTILITY);
        alert.initOwner(primaryStage);
        alert.setTitle(rb.getString(title));
        alert.setHeaderText(rb.getString(headerText));
        alert.setContentText(rb.getString(contentText));
        alert.showAndWait();        
    }
    
    public static boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }
    
    public static Parent load(Class resourceClass, String viewResource, ResourceBundle rb, IViewController controller) throws IOException {
        FXMLLoader loader = new FXMLLoader(resourceClass.getResource(viewResource));
        loader.setResources(rb);
        loader.setController(controller);
        return loader.load();
    }

    public static void CenterOnDialog(Stage primaryStage, Stage dialog) {
        Bounds bounds = primaryStage.getScene().getRoot().getLayoutBounds();
        double primaryStageWidth = bounds.getWidth();
        double primaryStageHeight = bounds.getHeight();
        double userViewDialogWidth = dialog.getWidth();
        double userViewDialogHeight = dialog.getHeight();
        
        dialog.setX(primaryStage.getX() + (primaryStageWidth - userViewDialogWidth) / 2);
        dialog.setY(primaryStage.getY() + (primaryStageHeight- userViewDialogHeight) / 2);      
    }
    
}
