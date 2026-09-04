/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import commands.ChangeLanguageCommand;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import properties.TranslationStringProperty;
import service.*;
import utils.ControllerUtilities;
import utils.DialogFactory;
import utils.EventManager;

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
    private final String workItemResourceKey = "WorkItemToolTip";
    
    private final String workItemTrackingToolViewDialogIcon = "icons/app-maid.png";
    private final String workItemViewTitleResourceKey = "WorkItemViewTitle";
    private final String workItemViewResource = "/view/WorkItemView.fxml";
    
    private final String workItemDateChangedEvent = "WorkItemDateChanged";
    private final String selectedWorkRecordChangedEvent = "SelectedWorkRecordChanged";

    private final String newTrackingItemEvent = "NewTrackingItem";
    private final String editTrackingItemEvent = "EditTrackingItem";
    private final String deleteTrackingItemEvent = "DeleteTrackingItem";
    
    private Stage primaryStage;
    private Stage workItemTrackingToolViewDialog;
    private final LanguageService languageService;
    private final Connection connection;
    private final UndoService undoService;
    private MenuItem activeMenuItem;
    private ResourceBundle rb;
    private final ControllerRepository controllerRepository;
    private final EventManager eventManager;
    
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
    @FXML
    private Button workItemButton;
    @FXML
    private Tooltip workItemTooltip;
    
    public TranslationStringProperty undoButtonText;

    public MainToolBarViewController(LanguageService languageService, Connection connection, UndoService undoService) {
        if(languageService == null) throw new NullPointerException("languageService");
        if(connection == null) throw new NullPointerException("connection");
        if(undoService == null) throw new NullPointerException("undoService");
        
        this.languageService = languageService;
        this.connection = connection;        
        this.undoService = undoService;
        this.controllerRepository = ControllerRepository.getInstance();
        this.eventManager = new EventManager();
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
        workItemTooltip.setText(rb.getString(workItemResourceKey));
        if(workItemTrackingToolViewDialog != null) {
            workItemTrackingToolViewDialog.setTitle(rb.getString(workItemViewTitleResourceKey));
        }
    }

    public MenuItem GetActiveMenuItem() {
        return activeMenuItem;
    }

    public Button getWorkItemButton() {
        return workItemButton;
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

    @FXML
    private void workItemAction(ActionEvent event) throws IOException, SQLException {      
        WorkItemViewController workItemViewController = new WorkItemViewController(controllerRepository, languageService, connection, undoService);
        controllerRepository.put(WorkItemViewController.class.getName(), workItemViewController);
            
        //This controller will be informed about the date changed action
        workItemViewController.getEventManager().registerEventType(workItemDateChangedEvent);
        WorkRecordDetailsViewController workRecordDetailsViewController = (WorkRecordDetailsViewController)this.controllerRepository.get(WorkRecordDetailsViewController.class.getName());
        workItemViewController.getEventManager().subscribeEventToListener(workItemDateChangedEvent, workRecordDetailsViewController);

        //Register WorkItemTrackingToolViewController for selectedWorkRecordChangedEvent
        WorkRecordViewController workRecordViewController = (WorkRecordViewController)controllerRepository.get(WorkRecordViewController.class.getName());
        workRecordViewController.getEventManager().subscribeEventToListener(selectedWorkRecordChangedEvent, workItemViewController);            

        //Register WorkItemTrackingToolViewController for newTrackingItemEvent, editTrackingItemEvent, deleteTrackingItemEvent
        TrackingItemViewController trackingItemViewController = (TrackingItemViewController)controllerRepository.get(TrackingItemViewController.class.getName());
        if(trackingItemViewController != null) {
            trackingItemViewController.getEventManager().subscribeEventToListener(newTrackingItemEvent, workItemViewController);
            trackingItemViewController.getEventManager().subscribeEventToListener(editTrackingItemEvent, workItemViewController);
            trackingItemViewController.getEventManager().subscribeEventToListener(deleteTrackingItemEvent, workItemViewController);
        }
            
        DialogFactory dialogFactory = new DialogFactory(
            primaryStage, 
            workItemViewTitleResourceKey, 
            workItemTrackingToolViewDialogIcon, 
            workItemViewResource, 
            rb, 
            workItemViewController);
        workItemTrackingToolViewDialog = dialogFactory.create(Modality.NONE);
        workItemTrackingToolViewDialog.setWidth(512);
        workItemTrackingToolViewDialog.setHeight(850);
            
        ControllerUtilities.CenterOnDialog(primaryStage, workItemTrackingToolViewDialog);
        
        if(!workItemTrackingToolViewDialog.isShowing()) {
            workItemButton.disableProperty().set(true);
            workItemTrackingToolViewDialog.showAndWait();        
        }
        
        if(controllerRepository.contains(WorkItemViewController.class.getName())) {
            controllerRepository.remove(WorkItemViewController.class.getName());
        }
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
