/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import adapter.Log4jAdapter;
import java.net.URL;
import java.sql.Connection;
import java.util.*;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.*;
import javafx.scene.control.Label;
import javafx.scene.image.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import properties.TranslationStringProperty;
import service.*;

/**
 *
 * @author ADREST18
 */
public class MainStatusBarViewController implements Initializable, IViewController {

    private Stage primaryStage;
    private final Duration messageLabelFadeTime = Duration.seconds(5);
    private final LanguageService languageService;
    private final Connection connection;
    private final MainInfoViewController mainInfoViewController;
    private final Log4jAdapter log4jAdapter;
    private ResourceBundle rb;
    
    @FXML
    private Label messageLabel;
    @FXML
    private ImageView actualLanguage;

    public TranslationStringProperty stateMessage;

    public MainStatusBarViewController(LanguageService languageService, Connection connection, MainInfoViewController mainInfoViewController, Log4jAdapter log4jAdapter) {
        if(languageService == null) throw new NullPointerException("languageService");
        if(connection == null) throw new NullPointerException("connection");
        if(mainInfoViewController == null) throw new NullPointerException("mainInfoViewController");
        if(log4jAdapter == null) throw new NullPointerException("log4jAdapter");

        this.languageService = languageService;
        this.connection = connection;        
        this.mainInfoViewController = mainInfoViewController;
        this.log4jAdapter = log4jAdapter;
    }

    @Override
    public void initialize(URL location, ResourceBundle rb) {
        this.rb = rb;
        stateMessage = new TranslationStringProperty(log4jAdapter);
        messageLabel.textProperty().bind(stateMessage);
        registerListener();
        languageService.updateGuiItems();
    }

    private void registerListener() {
        messageLabel.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            NodeFaderService.createFader(messageLabel, messageLabelFadeTime).play();
        });
    }

    @Override
    public void updateGuiItems() {
        changeLanguageIndicator(this.rb.getLocale());
    }

    private void changeLanguageIndicator(Locale locale) {
        switch (locale.toLanguageTag()) {
            case "de":
                actualLanguage.setImage(new Image("/icons/flag-de.png"));
                break;
            case "en":
                actualLanguage.setImage(new Image("/icons/flag-gb.png"));
                break;
            case "es":
                actualLanguage.setImage(new Image("/icons/flag-es.png"));
                break;
            case "fr":
                this.actualLanguage.setImage(new Image("/icons/flag-fr.png"));
                break;
            case "it":
                actualLanguage.setImage(new Image("/icons/flag-it.png"));
                break;
            default:
                break;
        }
    }

    public Label getMessageLabel() {
        return messageLabel;
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
    
    public enum BindingMethode {
        Bind, UnBind
    };

    public void bindProgressBarAndMessageLabel(BindingMethode binding, Task task) {
        switch (binding) {
            case Bind:
                messageLabel.textProperty().bind(task.messageProperty());
                break;
            case UnBind:
                messageLabel.textProperty().unbind();
                break;
            default:
                throw new IllegalArgumentException("BindingMethode");
        }
    }

}
