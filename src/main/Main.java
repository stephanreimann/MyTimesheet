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
import javafx.stage.StageStyle; // Import for undecorated stage
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.*;
import sqlite.ConnectionFactory;
import sqlite.WorkrecordDAO;
import utils.MathUtilities;

/**
 *
 * @author adrest18
 */
public class Main extends Application {
    private final int threadSleep = 100;
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
    private final String applicationAlwaysOnTopKey = "ApplicationAlwaysOnTop";
    private final String instanceLockKey = "instanceLock";
    private final String workrecordAutomaticCreationKey = "WorkrecordAutomaticCreation";
    private final String falseKey = "false";
    private final String trueKey = "true";
    
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
    private final String splashScreenViewResource = "/view/SplashScreenView.fxml";

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
    private Stage splashStage;
    private SplashScreenViewController splashScreenViewController;
    
    public Main() {
        this.languageService = new LanguageService();
        this.undoService = new UndoService();
        this.applicationInstance = new ApplicationInstance(instanceLockKey);
        this.controllerRepository = ControllerRepository.getInstance();
        this.propertiesService = PropertiesService.getInstance();
    }

    @Override
    public void start(Stage primaryStage) throws SQLException, IOException {
        this.primaryStage = primaryStage;

        showSplashScreen();

        new Thread(() -> {
            try {            
                if (splashScreenViewController != null) {
                    javafx.application.Platform.runLater(() -> 
                        splashScreenViewController.setStatus("Initializing logging...")
                    );
                    Thread.sleep(threadSleep);
                }
                String log4NetStoragePathAndFullName = propertiesService.getProperty("Log4NetStoragePath", log4jFilePathAndFullName);
                log4jAdapter = new Log4jAdapter(log4NetStoragePathAndFullName);
                log4jAdapter.getInfoViewLogAppender();
                log = LogManager.getLogger(Main.class.getName());
                propertiesService.setProperty("Log4NetStoragePath", log4NetStoragePathAndFullName);
                
                if (splashScreenViewController != null) {
                    javafx.application.Platform.runLater(() -> 
                        splashScreenViewController.setStatus("Loading settings...")
                    );
                    Thread.sleep(threadSleep);
                }
                String settingsStoragePathAndFullName = propertiesService.getProperty("SettingsStoragePath", settingsFilePathAndFullName);
                propertiesService.loadPropertiesFromXmlFile(settingsStoragePathAndFullName);
                propertiesService.setProperty("SettingsStoragePath", settingsStoragePathAndFullName);

                bundle = ResourceBundle.getBundle(languageRes, Locale.of(propertiesService.getProperty(appLanguageResourceKey, appLanguageDefaultValue)));
                Locale.setDefault(bundle.getLocale());
                
                if(!applicationInstance.isRunning()) {
                    if (splashScreenViewController != null) {
                        javafx.application.Platform.runLater(() -> 
                            splashScreenViewController.setStatus("Connecting to database...")
                        );
                        Thread.sleep(threadSleep);
                    }
                    ConnectionFactory connectionFactory = new ConnectionFactory(bundle, log4jAdapter);
                    String databaseStoragePathAndFullName = propertiesService.getProperty("DatabaseStoragePath", databaseFilePathAndFullName);
                    connection = connectionFactory.getConnection(databaseStoragePathAndFullName);
                    if(this.connection == null) {
                        throw new NullPointerException("Connection to database can not be established, may database file does not exists!");
                    }
                    propertiesService.setProperty("DatabaseStoragePath", databaseStoragePathAndFullName);

                    if (splashScreenViewController != null) {
                        javafx.application.Platform.runLater(() -> splashScreenViewController.setStatus("Loading main view..."));
                        Thread.sleep(threadSleep);
                    }
                    javafx.application.Platform.runLater(() -> {
                        try {
                            initPrimaryStage(primaryStage);
                            initMainView(bundle);
                        } catch (IOException ex) {
                            log.error("Error initializing main view: " + ex.getMessage(), ex);
                        }
                    });
                    
                    if (splashScreenViewController != null) {
                        javafx.application.Platform.runLater(() -> splashScreenViewController.setStatus("Loading menu bar..."));
                        Thread.sleep(threadSleep);
                    }
                    javafx.application.Platform.runLater(() -> {
                        try {
                            initMainMenuBarView(bundle);
                        } catch (IOException ex) {
                            log.error("Error initializing menu bar view: " + ex.getMessage(), ex);
                        }
                    });


                    if (splashScreenViewController != null) {
                        javafx.application.Platform.runLater(() -> splashScreenViewController.setStatus("Loading toolbar..."));
                        Thread.sleep(threadSleep);
                    }
                    javafx.application.Platform.runLater(() -> {
                        try {
                            initMainToolBarView(bundle);
                        } catch (IOException ex) {
                            log.error("Error initializing toolbar view: " + ex.getMessage(), ex);
                        }
                    });

                    if (splashScreenViewController != null) {
                        javafx.application.Platform.runLater(() -> splashScreenViewController.setStatus("Loading work records..."));
                        Thread.sleep(threadSleep);
                    }
                    javafx.application.Platform.runLater(() -> {
                        try {
                            initWorkRecordsView(bundle);
                        } catch (IOException | SQLException ex) {
                            log.error("Error initializing work records view: " + ex.getMessage(), ex);
                        }
                    });
                    
                    if (splashScreenViewController != null) {
                        javafx.application.Platform.runLater(() -> splashScreenViewController.setStatus("Loading work record details..."));
                        Thread.sleep(threadSleep);
                    }
                    javafx.application.Platform.runLater(() -> {
                        try {
                            initWorkRecordsDetailsView(bundle);
                        } catch (IOException ex) {
                            log.error("Error initializing work record details view: " + ex.getMessage(), ex);
                        }
                    });

                    if (splashScreenViewController != null) {
                        javafx.application.Platform.runLater(() -> splashScreenViewController.setStatus("Loading user information..."));
                        Thread.sleep(threadSleep);
                    }
                    javafx.application.Platform.runLater(() -> {
                        try {
                            initUserInfoView(bundle);
                        } catch (IOException | SQLException ex) {
                            log.error("Error initializing user info view: " + ex.getMessage(), ex);
                        }
                    });
                    
                    if (splashScreenViewController != null) {
                        javafx.application.Platform.runLater(() -> splashScreenViewController.setStatus("Loading info panel..."));
                        Thread.sleep(threadSleep);
                    }
                    javafx.application.Platform.runLater(() -> {
                        try {
                            initMainInfoView(bundle);
                        } catch (IOException ex) {
                            log.error("Error initializing main info view: " + ex.getMessage(), ex);
                        }
                    });

                    if (splashScreenViewController != null) {
                        javafx.application.Platform.runLater(() -> splashScreenViewController.setStatus("Loading status bar..."));
                        Thread.sleep(threadSleep);
                    }
                    javafx.application.Platform.runLater(() -> {
                        try {
                            initMainStatusBarView(bundle);
                        } catch (IOException ex) {
                            log.error("Error initializing status bar view: " + ex.getMessage(), ex);
                        }
                    });

                    if (splashScreenViewController != null) {
                        javafx.application.Platform.runLater(() -> splashScreenViewController.setStatus("Subscribe to events..."));
                        Thread.sleep(threadSleep);
                    }
                    javafx.application.Platform.runLater(() -> {
                        try {
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
                        } catch (RuntimeException ex) {
                            log.error("Error subscribing to events: " + ex.getMessage(), ex);
                        }
                    });

                    if (splashScreenViewController != null) {
                        javafx.application.Platform.runLater(() -> splashScreenViewController.setStatus("Create workrecord if not exists..."));
                        Thread.sleep(threadSleep);
                    }
                    javafx.application.Platform.runLater(() -> {
                        try {
                            createWorkrecordAutomaticallyIfNotExist();
                            workRecordViewController.selectWorkrecordOf(LocalDate.now());
                        } catch (SQLException ex) {
                            log.error("Error creating workrecord: " + ex.getMessage(), ex);
                        }
                    });

                    javafx.application.Platform.runLater(() -> {
                        try {
                            if(mainStatusBarViewController != null) {
                                mainStatusBarViewController.stateMessage.set(appStartedMsgResourceKey);
                            }
                            primaryStage.show();
                            
                            String dividerPositionAsString = propertiesService.getProperty(dividerPositionCenterBorderPaneSplitPaneResourceKey, dividerPositionCenterBorderPaneSplitPaneDefaultValue);
                            double dividerPosition = Double.parseDouble(dividerPositionAsString);
                            dividerPosition = MathUtilities.round(Double.parseDouble(dividerPositionAsString), 2);
                            mainViewController.getCenterBorderPaneSplitPane().setDividerPosition(0,dividerPosition);
                            
                            hideSplashScreen();
                        } catch (RuntimeException ex) {
                            log.fatal(ex.getMessage());
                            log.fatal(Arrays.toString(ex.getStackTrace()));
                            hideSplashScreen();
                            showApplicationStartUpAlert(ex, AlertType.ERROR);
                            try {
                                applicationInstance.forceRemoveOfInstanceLock();
                            } catch (Exception ex1) {
                                log.fatal(ex1.getMessage());
                                log.fatal(Arrays.toString(ex1.getStackTrace()));
                                showApplicationStartUpAlert(ex1, AlertType.ERROR);
                            }
                        }
                    });
                } else {
                    javafx.application.Platform.runLater(() -> {
                        hideSplashScreen();
                        showApplicationRunningDialog(bundle);
                    });
                }
            } catch (IOException | RuntimeException | InterruptedException ex) { // Added InterruptedException
                log.fatal(ex.getMessage());
                log.fatal(Arrays.toString(ex.getStackTrace()));
                javafx.application.Platform.runLater(() -> {
                    hideSplashScreen();
                    showApplicationStartUpAlert(ex, AlertType.ERROR);
                    try {
                        applicationInstance.forceRemoveOfInstanceLock();
                    } catch (Exception ex1) {
                        log.fatal(ex1.getMessage());
                        log.fatal(Arrays.toString(ex1.getStackTrace()));
                        showApplicationStartUpAlert(ex1, AlertType.ERROR);
                    }
                });
            }
        }).start();
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
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                    log.info("Database connection closed successfully.");
                }
            } catch (SQLException ex) {
                log.error("Error closing database connection: " + ex.getMessage(), ex);
            }        
        }
        
        Runtime.getRuntime().exit(0);
    }

    private void setAppStateInformationProperties() {
        propertiesService.setProperty(appLanguageResourceKey, mainViewController.getResourceBundle().getLocale().getLanguage());            
        propertiesService.setProperty(appHeightResourceKey, Double.toString(primaryStage.getHeight()));
        propertiesService.setProperty(appWidthResourceKey, Double.toString(primaryStage.getWidth()));
        propertiesService.setProperty(appXPositionResourceKey, Double.toString(primaryStage.getX()));
        propertiesService.setProperty(appYPositionResourceKey, Double.toString(primaryStage.getY()));
        double dividerPosition = mainViewController.getCenterBorderPaneSplitPane().getDividerPositions()[0];
        dividerPosition = MathUtilities.round(dividerPosition, 2);
        propertiesService.setProperty(dividerPositionCenterBorderPaneSplitPaneResourceKey, Double.toString(dividerPosition));
    }
    
    private void setInfoViewButtonStatesProperties() {
        propertiesService.setProperty(infoToggleButtonState, Boolean.toString(mainInfoViewController.getInfoToggleButtonState()));
        propertiesService.setProperty(debugToggleButtonState, Boolean.toString(mainInfoViewController.getDebugToggleButtonState()));
        propertiesService.setProperty(warningToggleButtonState, Boolean.toString(mainInfoViewController.getWarningToggleButtonState()));
        propertiesService.setProperty(errorToggleButtonState, Boolean.toString(mainInfoViewController.getErrorToggleButtonState()));
        propertiesService.setProperty(fatalToggleButtonState, Boolean.toString(mainInfoViewController.getFatalToggleButtonState()));        
    }
    
    private void initPrimaryStage(Stage primaryStage) {
        this.primaryStage.setAlwaysOnTop(Boolean.parseBoolean(propertiesService.getProperty(applicationAlwaysOnTopKey, trueKey)));
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
        BorderPane mainSceneRoot = (BorderPane) load(rb, mainViewController, mainViewResource); // Renamed to avoid conflict
        scene = new Scene(mainSceneRoot);
        primaryStage.setScene(scene);
    }

    private void initMainMenuBarView(ResourceBundle rb) throws IOException {
        mainMenuBarViewController = new MainMenuBarViewController(languageService, connection, undoService, this, propertiesService, log4jAdapter);
        mainMenuBarViewController.setResourceBundle(rb);
        mainMenuBarViewController.setPrimaryStage(primaryStage);
        controllerRepository.put(MainMenuBarViewController.class.getName(), mainMenuBarViewController);
        MenuBar mainMenuBarView = (MenuBar) load(rb, mainMenuBarViewController, mainMenuBarViewResource);
        if (mainViewController != null && mainViewController.getTopBorderPaneVBox() != null) {
            mainViewController.getTopBorderPaneVBox().getChildren().add(mainMenuBarView);
        } else {
            log.error("MainViewController or its top VBox is null when trying to add MainMenuBarView.");
        }
    }

    private void initMainToolBarView(ResourceBundle rb) throws IOException {
        mainToolBarViewController = new MainToolBarViewController(languageService, connection, undoService);
        mainToolBarViewController.setResourceBundle(rb);
        mainToolBarViewController.setPrimaryStage(primaryStage);
        controllerRepository.put(MainToolBarViewController.class.getName(), mainToolBarViewController);
        ToolBar mainToolBarView = (ToolBar) load(rb, mainToolBarViewController, mainToolBarViewResource);
        if (mainViewController != null && mainViewController.getTopBorderPaneVBox() != null) {
            mainViewController.getTopBorderPaneVBox().getChildren().add(mainToolBarView);
        } else {
            log.error("MainViewController or its top VBox is null when trying to add MainToolBarView.");
        }
    }

    private void initWorkRecordsView(ResourceBundle rb) throws IOException, SQLException {
        workRecordViewController = new WorkRecordViewController(languageService, connection, undoService, propertiesService);
        workRecordViewController.setResourceBundle(rb);
        workRecordViewController.setPrimaryStage(primaryStage);
        controllerRepository.put(workRecordViewController.getClass().getName(), workRecordViewController);
        VBox workRecordsView = (VBox) load(rb, workRecordViewController, workRecordViewResource);
        if (mainViewController != null && mainViewController.getCenterBorderPaneSplitPane() != null) {
            mainViewController.getCenterBorderPaneSplitPane().getItems().add(workRecordsView);
        } else {
            log.error("MainViewController or its center SplitPane is null when trying to add WorkRecordsView.");
        }
    }
    
    private void initWorkRecordsDetailsView(ResourceBundle rb) throws IOException {
        workRecordDetailsViewController = new WorkRecordDetailsViewController(controllerRepository, languageService, connection, undoService, propertiesService);
        workRecordDetailsViewController.setResourceBundle(rb);
        workRecordDetailsViewController.setPrimaryStage(primaryStage);
        controllerRepository.put(workRecordDetailsViewController.getClass().getName(), workRecordDetailsViewController);
        VBox workRecordDetailsView = (VBox) load(rb, workRecordDetailsViewController, workRecordDetailsViewResource);
        workRecordDetailsView.setMinWidth(260);        
        if (mainViewController != null && mainViewController.getRightBorderPaneVBox() != null) {
            mainViewController.getRightBorderPaneVBox().getChildren().add(workRecordDetailsView);
        } else {
            log.error("MainViewController or its right VBox is null when trying to add WorkRecordDetailsView.");
        }
    }

    private void initUserInfoView(ResourceBundle rb) throws IOException, SQLException {
        userInfoViewController = new UserInfoViewController(controllerRepository, languageService, connection, undoService, propertiesService);
        userInfoViewController.setResourceBundle(rb);
        userInfoViewController.setPrimaryStage(primaryStage);
        controllerRepository.put(userInfoViewController.getClass().getName(), userInfoViewController);
        VBox userInfoView = (VBox) load(rb, userInfoViewController, userInfoViewResource);
        userInfoView.setMinWidth(300);        
        if (mainViewController != null && mainViewController.getLeftBorderPaneVBox() != null) {
            mainViewController.getLeftBorderPaneVBox().getChildren().add(userInfoView);
        } else {
            log.error("MainViewController or its left VBox is null when trying to add UserInfoView.");
        }
    }
    
    private void initMainInfoView(ResourceBundle rb) throws IOException {
        mainInfoViewController = new MainInfoViewController(languageService, connection, undoService, log4jAdapter);
        mainInfoViewController.setResourceBundle(rb);
        mainInfoViewController.setPrimaryStage(primaryStage);
        controllerRepository.put(mainInfoViewController.getClass().getName(), mainInfoViewController);
        TabPane mainInfoBarView = (TabPane) load(rb, mainInfoViewController, mainInfoViewResource);
        if (mainViewController != null && mainViewController.getCenterBorderPaneSplitPane() != null) {
            mainViewController.getCenterBorderPaneSplitPane().getItems().add(mainInfoBarView);
        } else {
            log.error("MainViewController or its center SplitPane is null when trying to add MainInfoView.");
        }
        mainInfoViewController.setInfoToggleButtonState(Boolean.valueOf(propertiesService.getProperty(infoToggleButtonState, falseKey)));
        mainInfoViewController.setDebugToggleButtonState(Boolean.valueOf(propertiesService.getProperty(debugToggleButtonState, falseKey)));
        mainInfoViewController.setWarningToggleButtonState(Boolean.valueOf(propertiesService.getProperty(warningToggleButtonState, falseKey)));
        mainInfoViewController.setErrorToggleButtonState(Boolean.valueOf(propertiesService.getProperty(errorToggleButtonState, falseKey)));
        mainInfoViewController.setFatalToggleButtonState(Boolean.valueOf(propertiesService.getProperty(fatalToggleButtonState, falseKey)));
    }

    private void initMainStatusBarView(ResourceBundle rb) throws IOException {
        mainStatusBarViewController = new MainStatusBarViewController(languageService, connection, mainInfoViewController, log4jAdapter);
        mainStatusBarViewController.setResourceBundle(rb);
        mainStatusBarViewController.setPrimaryStage(primaryStage);
        controllerRepository.put(this.mainStatusBarViewController.getClass().getName(), mainStatusBarViewController);
        GridPane mainStatusBarView = (GridPane) load(rb, mainStatusBarViewController, mainStautsBarViewResource);
        if (mainViewController != null && mainViewController.getBottomBorderPaneVBox() != null) {
            mainViewController.getBottomBorderPaneVBox().getChildren().add(mainStatusBarView);
        } else {
            log.error("MainViewController or its bottom VBox is null when trying to add MainStatusBarView.");
        }
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
        boolean workrecordAutomaticCreation = Boolean.parseBoolean(propertiesService.getProperty(workrecordAutomaticCreationKey, falseKey));
        if(workrecordsDoNotExist && workrecordAutomaticCreation) {
            workRecordDetailsViewController.createWorkrecordAutomatically();            
        }
    }

    // New methods for splash screen
    private void showSplashScreen() throws IOException {
        splashStage = new Stage();
        splashStage.initStyle(StageStyle.UNDECORATED);
        splashStage.getIcons().add(new Image(appIcon));

        FXMLLoader loader = new FXMLLoader(getClass().getResource(splashScreenViewResource));
        Parent splashLayout = loader.load();
        splashScreenViewController = loader.getController();
        Scene splashScene = new Scene(splashLayout);
        splashStage.setScene(splashScene);
        splashStage.show();
    }

    private void hideSplashScreen() {
        if (splashStage != null) {
            splashStage.hide();
            splashStage = null;
        }
    }
}