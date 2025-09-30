/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import commands.ChangeLanguageCommand;
import java.net.URL;
import java.sql.Connection;
import java.util.*;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.stage.Stage;
import properties.TranslationStringProperty;
import service.*;

/**
 *
 * @author ADREST18
 */
public class MainToolBarViewController implements Initializable, IViewController {

    private final String languageSelectorinfoResourceKey = "languageSelectorButton";
    private final String languageDeResourceKey = "LanguageDE";
    private final String languageEnResourceKey = "LanguageEN";
    private final String languageEsResourceKey = "LanguageES";
    private final String languageFrResourceKey = "LanguageFR";
    private final String languageItResourceKey = "LanguageIT";
    private final String undoResourceKey = "Undo";
    private final String redoResourceKey = "Redo";
    
    private Stage primaryStage;
    private final LanguageService languageService;
    private final Connection connection;
    private final UndoService undoService;
    private MenuItem activeMenuItem;
    private ResourceBundle rb;
    private final ControllerRepository controllerRepository;
    
    @FXML
    private Button undoButton;
    @FXML
    private Tooltip undoTooltip;
    @FXML
    private Button redoButton;
    @FXML
    private Tooltip redoTooltip;
    @FXML
    private SplitMenuButton languageSelectorButton;
    @FXML
    private MenuItem languageDE;
    @FXML
    private MenuItem languageEN;
    @FXML
    private MenuItem languageES;
    @FXML
    private MenuItem languageFR;
    @FXML
    private MenuItem languageIT;
    
    public TranslationStringProperty undoButtonText;

    public MainToolBarViewController(LanguageService languageService, Connection connection, UndoService undoService) {
        if(languageService == null) throw new NullPointerException("languageService");
        if(connection == null) throw new NullPointerException("connection");
        if(undoService == null) throw new NullPointerException("undoService");
        
        this.languageService = languageService;
        this.connection = connection;        
        this.undoService = undoService;
        this.controllerRepository = ControllerRepository.getInstance();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.rb = rb;
        languageService.updateGuiItems();
    }

    @Override
    public void updateGuiItems() {
        toggleUndoRedoButtons();
        toggleMenuItemsForLanguageSelection(rb.getLocale());
        languageSelectorButton.setText(rb.getString(languageSelectorinfoResourceKey));
        languageDE.setText(rb.getString(languageDeResourceKey));
        languageEN.setText(rb.getString(languageEnResourceKey));
        languageES.setText(rb.getString(languageEsResourceKey));
        languageFR.setText(rb.getString(languageFrResourceKey));
        languageIT.setText(rb.getString(languageItResourceKey));
        undoButton.setText(rb.getString(undoResourceKey));
        undoTooltip.setText(rb.getString(undoResourceKey));
        redoButton.setText(rb.getString(redoResourceKey));
        redoTooltip.setText(rb.getString(redoResourceKey));
    }

    public MenuItem GetActiveMenuItem() {
        return activeMenuItem;
    }

    public void toggleUndoRedoButtons() {
        undoButton.disableProperty().set(undoService.isUndoStackEmpty());
        redoButton.disableProperty().set(undoService.isRedoStackEmpty());
    }

    @FXML
    private void undoAction(ActionEvent event) {
        undoService.undo();
    }

    @FXML
    private void redoAction(ActionEvent event) {
        undoService.redo();
    }

    @FXML
    private void changeLanguageAction(ActionEvent event) {
        Locale newLocale = getLocale(event);
        Locale oldLocale = getLocale(activeMenuItem);
        ChangeLanguageCommand cmd = new ChangeLanguageCommand(oldLocale, newLocale, languageService, controllerRepository);
        undoService.execute(cmd);
    }

    private Locale getLocale(ActionEvent event) {
        return createLocaleBy((MenuItem)event.getSource());
    }
    
    private Locale getLocale(MenuItem menuItem) {
        return createLocaleBy(menuItem);
    }

    private Locale createLocaleBy(MenuItem menuItem) {
        Locale locale;
        
        locale = switch (menuItem.getId()) {
            case "changeToGerman" -> Locale.of("de", "DE");
            case "changeToEnglish" -> Locale.of("en", "EN");
            case "changeToSpanish" -> Locale.of("es", "ES");
            case "changeToFrench" -> Locale.of("fr", "FR");
            case "changeToItalian" -> Locale.of("it", "IT");
            default -> Locale.ROOT;
        };
        return locale;
    }
    
    private void toggleMenuItemsForLanguageSelection(Locale locale) {
        switch (locale.toLanguageTag()) {
            case "de" -> {
                SetActiveMenuItem(languageDE);
                languageDE.setDisable(true);
                languageEN.setDisable(false);
                languageES.setDisable(false);
                languageFR.setDisable(false);
                languageIT.setDisable(false);
            }
            case "en" -> {
                languageDE.setDisable(false);
                SetActiveMenuItem(languageEN);
                languageEN.setDisable(true);
                languageES.setDisable(false);
                languageFR.setDisable(false);
                languageIT.setDisable(false);
            }
            case "es" -> {
                languageDE.setDisable(false);
                languageEN.setDisable(false);
                SetActiveMenuItem(languageES);
                languageES.setDisable(true);
                languageFR.setDisable(false);
                languageIT.setDisable(false);
            }
            case "fr" -> {
                languageDE.setDisable(false);
                languageEN.setDisable(false);
                languageES.setDisable(false);
                SetActiveMenuItem(languageFR);
                languageFR.setDisable(true);
                languageIT.setDisable(false);
            }
            case "it" -> {
                languageDE.setDisable(false);
                languageEN.setDisable(false);
                languageES.setDisable(false);
                languageFR.setDisable(false);
                SetActiveMenuItem(languageIT);
                languageIT.setDisable(true);
            }
            default -> {
            }
        }
    }

    private void SetActiveMenuItem(MenuItem menuItem) {
        activeMenuItem = menuItem;
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
