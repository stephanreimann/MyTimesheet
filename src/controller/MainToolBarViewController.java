/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import javafx.stage.StageStyle;
import properties.TranslationStringProperty;
import service.*;
import utils.DialogFactory;
import utils.EventManager;

/**
 *
 * @author ADREST18
 */
public class MainToolBarViewController implements Initializable, IViewController {

    private static final String PREF_WIDTH_KEY = "WorkItemTrackingToolViewDialogWidth";
    private static final String PREF_WIDTH_DEFAULT = "512";
    private static final String PREF_HEIGHT_KEY = "WorkItemTrackingToolViewDialogHeight";
    private static final String PREF_HEIGHT_DEFAULT = "850";
    private static final String PREF_XPOS_KEY = "WorkItemTrackingToolViewDialogXPos";
    private static final String PREF_XPOS_DEFAULT = "0.0";
    private static final String PREF_YPOS_KEY = "WorkItemTrackingToolViewDialogYPos";
    private static final String PREF_YPOS_DEFAULT = "0.0";
    
    private static final String LANG_SELECTOR_KEY = "languageSelectorButton";
    private static final String WORK_ITEM_TOOLTIP_KEY = "WorkItemToolTip";
    private static final String WORK_ITEM_TITLE_KEY = "WorkItemViewTitle";
    private static final String WORK_ITEM_RESOURCE = "/view/WorkItemView.fxml";
    private static final String WORK_ITEM_ICON = "icons/app-maid.png";
    
    private static final String EVENT_DATE_CHANGED = "WorkItemDateChanged";
    private static final String EVENT_SELECTED_RECORD_CHANGED = "SelectedWorkRecordChanged";
    private static final String EVENT_NEW_ITEM = "NewTrackingItem";
    private static final String EVENT_EDIT_ITEM = "EditTrackingItem";
    private static final String EVENT_DELETE_ITEM = "DeleteTrackingItem";

    private static final String EVENT_NEW_WORKITEM = "NewWorkItem";
    private static final String EVENT_EDIT_WORKITEM = "EditWorkItem";
    private static final String EVENT_DELETE_WORKITEM = "DeleteWorkItem";

    private Stage primaryStage;
    private Stage workItemTrackingToolViewDialog;
    private final LanguageService languageService;
    private final Connection connection;
    private final UndoService undoService;
    private final PropertiesService propertiesService;
    private MenuItem activeMenuItem;
    private ResourceBundle rb;
    private final ControllerRepository controllerRepository;
    @SuppressWarnings("unused")
    private final EventManager eventManager;
    
    @FXML private Button undoButton;
    @FXML private Tooltip undoTooltip;
    @FXML private Button redoButton;
    @FXML private Tooltip redoTooltip;
    @FXML private SplitMenuButton languageSelectorButton;
    @FXML private MenuItem languageDE;
    @FXML private MenuItem languageEN;
    @FXML private MenuItem languageES;
    @FXML private MenuItem languageFR;
    @FXML private MenuItem languageIT;
    @FXML private Button workItemButton;
    @FXML private Tooltip workItemTooltip;
    
    public TranslationStringProperty undoButtonText;

    public MainToolBarViewController(LanguageService languageService, Connection connection, 
                                     UndoService undoService, PropertiesService propertiesService) {
        this.languageService = Objects.requireNonNull(languageService, "languageService");
        this.connection = Objects.requireNonNull(connection, "connection");
        this.undoService = Objects.requireNonNull(undoService, "undoService");
        this.propertiesService = Objects.requireNonNull(propertiesService, "propertiesService");
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
        if (rb == null) return;
        
        toggleUndoRedoButtons();
        toggleMenuItemsForLanguageSelection(rb.getLocale());
        
        languageSelectorButton.setText(rb.getString(LANG_SELECTOR_KEY));
        languageDE.setText(rb.getString("LanguageDE"));
        languageEN.setText(rb.getString("LanguageEN"));
        languageES.setText(rb.getString("LanguageES"));
        languageFR.setText(rb.getString("LanguageFR"));
        languageIT.setText(rb.getString("LanguageIT"));
        
        String undoText = rb.getString("Undo");
        undoButton.setText(undoText);
        undoTooltip.setText(undoText);
        
        String redoText = rb.getString("Redo");
        redoButton.setText(redoText);
        redoTooltip.setText(redoText);
        
        workItemTooltip.setText(rb.getString(WORK_ITEM_TOOLTIP_KEY));
        
        if (workItemTrackingToolViewDialog != null) {
            workItemTrackingToolViewDialog.setTitle(rb.getString(WORK_ITEM_TITLE_KEY));
        }
    }

    public MenuItem GetActiveMenuItem() {
        return activeMenuItem;
    }

    public Button getWorkItemButton() {
        return workItemButton;
    }
    
    public void toggleUndoRedoButtons() {
        undoButton.setDisable(undoService.isUndoStackEmpty());
        redoButton.setDisable(undoService.isRedoStackEmpty());
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
        undoService.execute(new ChangeLanguageCommand(oldLocale, newLocale, languageService, controllerRepository));
    }

    @FXML
    private void workItemAction(ActionEvent event) throws IOException, SQLException {      
        WorkItemViewController workItemViewController = new WorkItemViewController(controllerRepository, languageService, connection, undoService, propertiesService);
        controllerRepository.put(WorkItemViewController.class.getName(), workItemViewController);
            
        // Event registrations
        var workItemEventManager = workItemViewController.getEventManager();
        workItemEventManager.registerEventType(EVENT_DATE_CHANGED);
        workItemEventManager.registerEventType(EVENT_NEW_WORKITEM);
        workItemEventManager.registerEventType(EVENT_EDIT_WORKITEM);
        workItemEventManager.registerEventType(EVENT_DELETE_WORKITEM);
        
        WorkRecordDetailsViewController detailsController = (WorkRecordDetailsViewController) controllerRepository.get(WorkRecordDetailsViewController.class.getName());
        workItemEventManager.subscribeEventToListener(EVENT_DATE_CHANGED, detailsController);

        WorkRecordViewController recordController = (WorkRecordViewController) controllerRepository.get(WorkRecordViewController.class.getName());
        recordController.getEventManager().subscribeEventToListener(EVENT_SELECTED_RECORD_CHANGED, workItemViewController);            

        TrackingItemViewController trackingController = (TrackingItemViewController) controllerRepository.get(TrackingItemViewController.class.getName());
        if (trackingController != null) {
            trackingController.getEventManager().subscribeEventToListener(EVENT_NEW_ITEM, workItemViewController);
            trackingController.getEventManager().subscribeEventToListener(EVENT_EDIT_ITEM, workItemViewController);
            trackingController.getEventManager().subscribeEventToListener(EVENT_DELETE_ITEM, workItemViewController);
        }

        UserInfoViewController userInfoController = (UserInfoViewController) controllerRepository.get(UserInfoViewController.class.getName());
        if (userInfoController != null) {
            workItemEventManager.subscribeEventToListener(EVENT_NEW_WORKITEM, userInfoController);
            workItemEventManager.subscribeEventToListener(EVENT_EDIT_WORKITEM, userInfoController);
            workItemEventManager.subscribeEventToListener(EVENT_DELETE_WORKITEM, userInfoController);
        }
        
        DialogFactory dialogFactory = new DialogFactory(
            primaryStage, WORK_ITEM_TITLE_KEY, WORK_ITEM_ICON, WORK_ITEM_RESOURCE, rb, workItemViewController);
        workItemTrackingToolViewDialog = dialogFactory.create(Modality.NONE, StageStyle.DECORATED, true);
        
        applyStageDimensions();
        
        if (!workItemTrackingToolViewDialog.isShowing()) {
            workItemButton.setDisable(true);
            workItemTrackingToolViewDialog.showAndWait();        
        }

        saveStageDimensions();
        
        if (controllerRepository.contains(WorkItemViewController.class.getName())) {
            controllerRepository.remove(WorkItemViewController.class.getName());
        }
    }

    private void applyStageDimensions() {
        workItemTrackingToolViewDialog.setWidth(getDoubleProperty(PREF_WIDTH_KEY, PREF_WIDTH_DEFAULT));
        workItemTrackingToolViewDialog.setHeight(getDoubleProperty(PREF_HEIGHT_KEY, PREF_HEIGHT_DEFAULT));
        workItemTrackingToolViewDialog.setX(getDoubleProperty(PREF_XPOS_KEY, PREF_XPOS_DEFAULT));
        workItemTrackingToolViewDialog.setY(getDoubleProperty(PREF_YPOS_KEY, PREF_YPOS_DEFAULT));
    }

    private void saveStageDimensions() {
        propertiesService.setProperty(PREF_WIDTH_KEY, String.valueOf(workItemTrackingToolViewDialog.getWidth()));
        propertiesService.setProperty(PREF_HEIGHT_KEY, String.valueOf(workItemTrackingToolViewDialog.getHeight()));
        propertiesService.setProperty(PREF_XPOS_KEY, String.valueOf(workItemTrackingToolViewDialog.getX()));
        propertiesService.setProperty(PREF_YPOS_KEY, String.valueOf(workItemTrackingToolViewDialog.getY()));
    }

    private double getDoubleProperty(String key, String defaultValue) {
        return Double.parseDouble(propertiesService.getProperty(key, defaultValue));
    }

    private Locale getLocale(ActionEvent event) {
        return event != null && event.getSource() instanceof MenuItem mi ? createLocaleBy(mi) : Locale.ROOT;
    }
    
    private Locale getLocale(MenuItem menuItem) {
        return menuItem != null ? createLocaleBy(menuItem) : Locale.ROOT;
    }

    private Locale createLocaleBy(MenuItem menuItem) {
        return switch (menuItem.getId()) {
            case "changeToGerman" -> Locale.of("de", "DE");
            case "changeToEnglish" -> Locale.of("en", "EN");
            case "changeToSpanish" -> Locale.of("es", "ES");
            case "changeToFrench" -> Locale.of("fr", "FR");
            case "changeToItalian" -> Locale.of("it", "IT");
            default -> Locale.ROOT;
        };
    }
    
    private void toggleMenuItemsForLanguageSelection(Locale locale) {
        String langTag = locale != null ? locale.toLanguageTag() : "";
        
        languageDE.setDisable("de".equals(langTag));
        languageEN.setDisable("en".equals(langTag));
        languageES.setDisable("es".equals(langTag));
        languageFR.setDisable("fr".equals(langTag));
        languageIT.setDisable("it".equals(langTag));

        MenuItem matchedItem = switch (langTag) {
            case "de" -> languageDE;
            case "en" -> languageEN;
            case "es" -> languageES;
            case "fr" -> languageFR;
            case "it" -> languageIT;
            default -> null;
        };
        
        if (matchedItem != null) {
            SetActiveMenuItem(matchedItem);
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
        // No-op
    }
}