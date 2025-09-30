/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package main;

import controller.UserInfoViewController;
import adapter.Log4jAdapter;
import controller.*;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.*;
import sqlite.ConnectionFactory;
import sqlite.WorkrecordDAO;

/**
 *
 * @author adrest18
 */
public class Main extends Application {
    private final String appIcon = "icons/app-maid.png";
    
    private final String dividerPositionCenterBorderPaneSplitPaneResourceKey = "DividerPositionCenterBorderPane";
    private final String dividerPositionCenterBorderPaneSplitPaneDefaultValue = "0.75";
    
    private final String appLanguageResourceKey = "Language";
    private final String appLanguageDefaultValue = "en";
    private final String appNameResourceKey = "AppName";
    private final String appHeightResourceKey = "AppHeight";
    private final String appHeightDefaultValue = "800";
    private final String appWidthResourceKey = "AppWidth";
    private final String appWidthDefaultValue = "1200";
    private final String appXPositionResourceKey = "AppXPosition";
    private final String appXPositionDefaultValue = "0.0";
    private final String appYPositionResourceKey = "AppYPosition";
    private final String appYPositionDefaultValue = "0.0";
    private final String appStartedMsgResourceKey = "AppStarted";
    private final String appStoppedMsgResourceKey = "AppStopped";
    
    private final String infoToggleButtonState = "InfoToggleButtonState";
    private final String debugToggleButtonState = "DebugToggleButtonState";
    private final String warningToggleButtonState = "WarningToggleButtonState";
    private final String errorToggleButtonState = "ErrorToggleButtonState";
    private final String fatalToggleButtonState = "FatalToggleButtonState";
    
    private final String languageRes = "languages.bundle";

    private final String mainViewResource = "/view/MainView.fxml";
    private final String mainMenuBarViewResource = "/view/MainMenuBarView.fxml";
    private final String mainToolBarViewResource = "/view/MainToolBarView.fxml";
    private final String mainStautsBarViewResource = "/view/MainStatusBarView.fxml";
    private final String mainInfoViewResource = "/view/MainInfoView.fxml";
    private final String userInfoViewResource = "/view/UserInfoView.fxml";
    private final String workRecordViewResource = "/view/WorkRecordView.fxml";
    private final String workRecordDetailsViewResource ="/view/WorkRecordDetailsView.fxml";

    private final String settingsFilePathAndFullName = "./properties.xml";
    private final String databaseFilePathAndFullName = "./sqlite/proddb.sqlite";
    private final String log4jFilePathAndFullName = "./log4j2.xml";

    private final String newWorkrecordEvent = "NewWorkrecord";
    private final String editWorkrecordEvent = "EditWorkrecord";
    private final String deleteWorkrecordEvent = "DeleteWorkrecord";

    private final String workRecordDetailsDateChangedEvent = "WorkRecordDetailsDateChanged";
    private final String selectedWorkRecordChangedEvent = "SelectedWorkRecordChanged";
    private final String startDateChangedEvent = "StartDateChanged";
    private final String endDateChangedEvent = "EndDateChanged";
    private final String userChangedEvent = "UserChanged";
    
    private final PropertiesService propertiesService;
    private final LanguageService languageService;
    private final UndoService undoService;
    private final ApplicationInstance applicationInstance;
    
    private final ControllerRepository controllerRepository;
    private Connection connection;
    private ResourceBundle bundle;
    private Stage primaryStage;
    private BorderPane mainScene;
    private MainMenuBarViewController mainMenuBarViewController;
    private MainToolBarViewController mainToolBarViewController;
    private UserInfoViewController userInfoViewController;
    private MainInfoViewController mainInfoViewController;
    private MainStatusBarViewController mainStatusBarViewController;
    private MainViewController mainViewController;
    private WorkRecordViewController workRecordViewController;
    private WorkRecordDetailsViewController workRecordDetailsViewController; 
    private Scene scene;
    private Log4jAdapter log4jAdapter;
    private Logger log;
    
    public Main() {
        this.languageService = new LanguageService();
        this.undoService = new UndoService();
        this.applicationInstance = new ApplicationInstance("instanceLock");
        this.controllerRepository = ControllerRepository.getInstance();
        this.propertiesService = PropertiesService.getInstance();
    }

    @Override
    public void start(Stage primaryStage) throws SQLException, IOException {
        try {
            String log4NetStoragePathAndFullName = propertiesService.getProperty("Log4NetStoragePath", log4jFilePathAndFullName);
            log4jAdapter = new Log4jAdapter(log4NetStoragePathAndFullName);
            log4jAdapter.getInfoViewLogAppender();
            log = LogManager.getLogger(Main.class.getName());
            propertiesService.setProperty("Log4NetStoragePath", log4NetStoragePathAndFullName);
            
            String settingsStoragePathAndFullName = propertiesService.getProperty("SettingsStoragePath", settingsFilePathAndFullName);
            propertiesService.loadPropertiesFromXmlFile(settingsStoragePathAndFullName);
            propertiesService.setProperty("SettingsStoragePath", settingsStoragePathAndFullName);

            bundle = ResourceBundle.getBundle(languageRes, Locale.of(propertiesService.getProperty(appLanguageResourceKey, appLanguageDefaultValue)));
            Locale.setDefault(bundle.getLocale());
            
            if(!applicationInstance.isRunning()) {
                ConnectionFactory connectionFactory = new ConnectionFactory(bundle, log4jAdapter);
                String databaseStoragePathAndFullName = propertiesService.getProperty("DatabaseStoragePath", databaseFilePathAndFullName);
                connection = connectionFactory.getConnection(databaseStoragePathAndFullName);
                if(this.connection == null) {
                    throw new NullPointerException("Connection to database can not be established, may database file does not exists!");
                }
                propertiesService.setProperty("DatabaseStoragePath", databaseStoragePathAndFullName);

                initPrimaryStage(primaryStage);
                
                //The order of initialization of Views will affect the order of views in different BorderPanes!
                //Further more the order is important for initialization of event handling.
                initMainView(bundle);
                initMainMenuBarView(bundle);
                initMainToolBarView(bundle);
                initWorkRecordsView(bundle);
                initWorkRecordsDetailsView(bundle);
                initUserInfoView(bundle);
                initMainInfoView(bundle);
                initMainStatusBarView(bundle);

                workRecordViewController.getEventManager().subscribeEventToListener(selectedWorkRecordChangedEvent, workRecordDetailsViewController);
                workRecordViewController.getEventManager().subscribeEventToListener(startDateChangedEvent, workRecordDetailsViewController);
                workRecordViewController.getEventManager().subscribeEventToListener(endDateChangedEvent, workRecordDetailsViewController);
                workRecordViewController.getEventManager().subscribeEventToListener(userChangedEvent, workRecordDetailsViewController);
                workRecordViewController.getEventManager().subscribeEventToListener(userChangedEvent, userInfoViewController);

                workRecordDetailsViewController.getEventManager().subscribeEventToListener(newWorkrecordEvent, workRecordViewController);
                workRecordDetailsViewController.getEventManager().subscribeEventToListener(editWorkrecordEvent, workRecordViewController);
                workRecordDetailsViewController.getEventManager().subscribeEventToListener(deleteWorkrecordEvent, workRecordViewController);
                workRecordDetailsViewController.getEventManager().subscribeEventToListener(newWorkrecordEvent, userInfoViewController);
                workRecordDetailsViewController.getEventManager().subscribeEventToListener(editWorkrecordEvent, userInfoViewController);
                workRecordDetailsViewController.getEventManager().subscribeEventToListener(deleteWorkrecordEvent, userInfoViewController);
                workRecordDetailsViewController.getEventManager().subscribeEventToListener(workRecordDetailsDateChangedEvent, workRecordViewController);
                
                createWorkrecordAutomaticallyIfNotExist();
                
                if(mainStatusBarViewController != null) {
                    mainStatusBarViewController.stateMessage.set(appStartedMsgResourceKey);
                }
            } else {
                showApplicationRunningDialog(bundle);
            }
        } catch (IOException | RuntimeException ex) {
            System.out.println(ex.getMessage());
            System.out.println(Arrays.toString(ex.getStackTrace()));
            showApplicationStartUpAlert(ex, AlertType.ERROR);
            try {
                applicationInstance.forceRemoveOfInstanceLock();
            } catch (Exception ex1) {
                System.out.println(ex.getMessage());
                System.out.println(Arrays.toString(ex.getStackTrace()));
                showApplicationStartUpAlert(ex, AlertType.ERROR);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() {
        if(mainViewController != null && mainStatusBarViewController != null && primaryStage != null) {
            mainStatusBarViewController.stateMessage.set(appStoppedMsgResourceKey);
            setAppStateInformationProperties();
            setInfoViewButtonStatesProperties();
            String settingsStoragePathAndFullName = propertiesService.getProperty("SettingsStoragePath", settingsFilePathAndFullName);
            propertiesService.savePropertiesToXmlFile(settingsStoragePathAndFullName);
        }
        
        Runtime.getRuntime().exit(0);
    }

    private void setAppStateInformationProperties() {
        propertiesService.setProperty(appLanguageResourceKey, mainViewController.getResourceBundle().getLocale().getLanguage());            
        propertiesService.setProperty(appHeightResourceKey, Double.toString(primaryStage.getHeight()));
        propertiesService.setProperty(appWidthResourceKey, Double.toString(primaryStage.getWidth()));
        propertiesService.setProperty(appXPositionResourceKey, Double.toString(primaryStage.getX()));
        propertiesService.setProperty(appYPositionResourceKey, Double.toString(primaryStage.getY()));
        propertiesService.setProperty(dividerPositionCenterBorderPaneSplitPaneResourceKey, Double.toString(mainViewController.getCenterBorderPaneSplitPane().getDividerPositions()[0]));
    }
    
    private void setInfoViewButtonStatesProperties() {
        propertiesService.setProperty(infoToggleButtonState, Boolean.toString(mainInfoViewController.getInfoToggleButtonState()));
        propertiesService.setProperty(debugToggleButtonState, Boolean.toString(mainInfoViewController.getDebugToggleButtonState()));
        propertiesService.setProperty(warningToggleButtonState, Boolean.toString(mainInfoViewController.getWarningToggleButtonState()));
        propertiesService.setProperty(errorToggleButtonState, Boolean.toString(mainInfoViewController.getErrorToggleButtonState()));
        propertiesService.setProperty(fatalToggleButtonState, Boolean.toString(mainInfoViewController.getFatalToggleButtonState()));        
    }
    
    private void initPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setAlwaysOnTop(Boolean.parseBoolean(propertiesService.getProperty("ApplicationAlwaysOnTop", "true")));
        this.primaryStage.getIcons().add(new Image(appIcon));
        this.primaryStage.setTitle(bundle.getString(appNameResourceKey));
        this.primaryStage.setHeight(Double.parseDouble(propertiesService.getProperty(appHeightResourceKey, appHeightDefaultValue)));
        this.primaryStage.setWidth(Double.parseDouble(propertiesService.getProperty(appWidthResourceKey, appWidthDefaultValue)));
        this.primaryStage.setX(Double.parseDouble(propertiesService.getProperty(appXPositionResourceKey, appXPositionDefaultValue)));
        this.primaryStage.setY(Double.parseDouble(propertiesService.getProperty(appYPositionResourceKey, appYPositionDefaultValue)));
    }
    
    private void initMainView(ResourceBundle rb) throws IOException {
        mainViewController = new MainViewController(languageService, connection);
        mainViewController.setResourceBundle(rb);
        mainViewController.setPrimaryStage(primaryStage);
        controllerRepository.put(MainViewController.class.getName(), mainViewController);
        mainScene = (BorderPane) load(rb, mainViewController, mainViewResource);
        scene = new Scene(mainScene);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initMainMenuBarView(ResourceBundle rb) throws IOException {
        mainMenuBarViewController = new MainMenuBarViewController(languageService, connection, undoService, this, propertiesService, log4jAdapter);
        mainMenuBarViewController.setResourceBundle(rb);
        mainMenuBarViewController.setPrimaryStage(primaryStage);
        controllerRepository.put(MainMenuBarViewController.class.getName(), mainMenuBarViewController);
        MenuBar mainMenuBarView = (MenuBar) load(rb, mainMenuBarViewController, mainMenuBarViewResource);
        mainViewController.getTopBorderPaneVBox().getChildren().add(mainMenuBarView);
    }

    private void initMainToolBarView(ResourceBundle rb) throws IOException {
        mainToolBarViewController = new MainToolBarViewController(languageService, connection, undoService);
        mainToolBarViewController.setResourceBundle(rb);
        mainToolBarViewController.setPrimaryStage(primaryStage);
        controllerRepository.put(MainToolBarViewController.class.getName(), mainToolBarViewController);
        ToolBar mainToolBarView = (ToolBar) load(rb, mainToolBarViewController, mainToolBarViewResource);
        mainViewController.getTopBorderPaneVBox().getChildren().add(mainToolBarView);
    }

    private void initWorkRecordsView(ResourceBundle rb) throws IOException, SQLException {
        workRecordViewController = new WorkRecordViewController(languageService, connection, undoService, propertiesService);
        workRecordViewController.setResourceBundle(rb);
        workRecordViewController.setPrimaryStage(primaryStage);
        controllerRepository.put(workRecordViewController.getClass().getName(), workRecordViewController);
        VBox workRecordsView = (VBox) load(rb, workRecordViewController, workRecordViewResource);
        mainViewController.getCenterBorderPaneSplitPane().getItems().add(workRecordsView);
    }
    
    private void initWorkRecordsDetailsView(ResourceBundle rb) throws IOException {
        workRecordDetailsViewController = new WorkRecordDetailsViewController(controllerRepository, languageService, connection, undoService, propertiesService);
        workRecordDetailsViewController.setResourceBundle(rb);
        workRecordDetailsViewController.setPrimaryStage(primaryStage);
        controllerRepository.put(workRecordDetailsViewController.getClass().getName(), workRecordDetailsViewController);
        VBox workRecordDetailsView = (VBox) load(rb, workRecordDetailsViewController, workRecordDetailsViewResource);
        workRecordDetailsView.setMinWidth(260);        
        mainViewController.getRightBorderPaneVBox().getChildren().add(workRecordDetailsView);
    }

    private void initUserInfoView(ResourceBundle rb) throws IOException, SQLException {
        userInfoViewController = new UserInfoViewController(controllerRepository, languageService, connection, undoService, propertiesService);
        userInfoViewController.setResourceBundle(rb);
        userInfoViewController.setPrimaryStage(primaryStage);
        controllerRepository.put(userInfoViewController.getClass().getName(), userInfoViewController);
        VBox userInfoView = (VBox) load(rb, userInfoViewController, userInfoViewResource);
        userInfoView.setMinWidth(300);        
        mainViewController.getLeftBorderPaneVBox().getChildren().add(userInfoView);
    }
    
    private void initMainInfoView(ResourceBundle rb) throws IOException {
        mainInfoViewController = new MainInfoViewController(languageService, connection, undoService, log4jAdapter);
        mainInfoViewController.setResourceBundle(rb);
        mainInfoViewController.setPrimaryStage(primaryStage);
        controllerRepository.put(mainInfoViewController.getClass().getName(), mainInfoViewController);
        TabPane mainInfoBarView = (TabPane) load(rb, mainInfoViewController, mainInfoViewResource);
        mainViewController.getCenterBorderPaneSplitPane().getItems().add(mainInfoBarView);
        mainViewController.getCenterBorderPaneSplitPane().setDividerPosition(0, Double.parseDouble(propertiesService.getProperty(dividerPositionCenterBorderPaneSplitPaneResourceKey, dividerPositionCenterBorderPaneSplitPaneDefaultValue)));
        mainInfoViewController.setInfoToggleButtonState(Boolean.valueOf(propertiesService.getProperty(infoToggleButtonState, "false")));
        mainInfoViewController.setDebugToggleButtonState(Boolean.valueOf(propertiesService.getProperty(debugToggleButtonState, "false")));
        mainInfoViewController.setWarningToggleButtonState(Boolean.valueOf(propertiesService.getProperty(warningToggleButtonState, "false")));
        mainInfoViewController.setErrorToggleButtonState(Boolean.valueOf(propertiesService.getProperty(errorToggleButtonState, "false")));
        mainInfoViewController.setFatalToggleButtonState(Boolean.valueOf(propertiesService.getProperty(fatalToggleButtonState, "false")));
    }

    private void initMainStatusBarView(ResourceBundle rb) throws IOException {
        mainStatusBarViewController = new MainStatusBarViewController(languageService, connection, mainInfoViewController, log4jAdapter);
        mainStatusBarViewController.setResourceBundle(rb);
        mainStatusBarViewController.setPrimaryStage(primaryStage);
        controllerRepository.put(this.mainStatusBarViewController.getClass().getName(), mainStatusBarViewController);
        GridPane mainStatusBarView = (GridPane) load(rb, mainStatusBarViewController, mainStautsBarViewResource);
        mainViewController.getBottomBorderPaneVBox().getChildren().add(mainStatusBarView);
    }

    private void showApplicationRunningDialog(ResourceBundle rb) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(rb.getString("AppRunningInfo"));
        alert.setHeaderText(rb.getString("AppRunningHeader"));
        alert.setContentText(rb.getString("AppRunningDetails"));
        //dialog allways on top of application
        alert.initOwner(primaryStage);
        alert.showAndWait();
    }

    private void showApplicationStartUpAlert(Exception ex, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle("Application startup error");
        alert.setHeaderText("The application could not start!");
        
        StringBuilder content = new StringBuilder();
        content.append("Message:").append("\n");
        content.append(ex.getMessage()).append("\n\n");
        content.append("Cause:").append("\n");
        content.append(ex.getCause()).append("\n\n");
        content.append("Stack Trace:").append("\n");
        content.append(Arrays.toString(ex.getStackTrace()));
        alert.setContentText(content.toString());
        
        //dialog allways on top of application
        alert.initOwner(primaryStage);
        alert.showAndWait().ifPresent(rs -> {
            if (rs == ButtonType.OK) {
                stop();
            }
        });
    }
    
    private Parent load(ResourceBundle rb, IViewController controller, String viewResource) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(viewResource));
        loader.setResources(rb);
        loader.setController(controller);
        return loader.load();
    }
    
    private void createWorkrecordAutomaticallyIfNotExist() throws SQLException {
        WorkrecordDAO workrecordDao = new WorkrecordDAO(connection);
        boolean workrecordsDoNotExist = workrecordDao.selectAll(workRecordViewController.getSelectedUser(), LocalDate.now()).isEmpty();
        boolean workrecordAutomaticCreation = Boolean.parseBoolean(propertiesService.getProperty("WorkrecordAutomaticCreation", "false"));
        if(workrecordsDoNotExist && workrecordAutomaticCreation) {
            workRecordDetailsViewController.createWorkrecordAutomatically();            
        }
    }
    
}