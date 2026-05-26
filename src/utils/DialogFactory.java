/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import controller.IViewController;
import java.io.IOException;
import java.util.ResourceBundle;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.*;

/**
 *
 * @author adrest18
 */
public class DialogFactory {

    private final Stage primaryStage;
    private final String dialogTitleResourceKey;
    private final String dialogIconPath;
    private final String dialogResource;
    private final ResourceBundle rb;
    private final IViewController dialogController;
    
    public DialogFactory(Stage primaryStage, String dialogTitleResourceKey, String dialogIconPath, String dialogResource, ResourceBundle rb, IViewController dialogController) {
        this.primaryStage = primaryStage;
        this.dialogTitleResourceKey = dialogTitleResourceKey;
        this.dialogIconPath = dialogIconPath;
        this.dialogResource = dialogResource;
        this.rb = rb;
        this.dialogController = dialogController;
    }
    
    public Stage create() throws IOException {
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(primaryStage);        
        stage.setAlwaysOnTop(true);
        stage.setResizable(false);
        stage.initStyle(StageStyle.UTILITY);
        stage.setTitle(this.rb.getString(dialogTitleResourceKey));
        stage.getIcons().add(new Image(dialogIconPath));
        AnchorPane anchorPane = (AnchorPane)ControllerUtilities.load(this.getClass(), dialogResource, rb, dialogController);
        stage.setScene(new Scene(anchorPane));
        stage.setOnCloseRequest((request) -> {
            dialogController.preCloseAction();
            stage.close();
        });

        dialogController.setPrimaryStage(stage);
        stage.setWidth(800);
        stage.setHeight(500);
        return stage;
    }
    
}
