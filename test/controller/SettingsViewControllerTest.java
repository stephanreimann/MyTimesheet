/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import adapter.Log4jAdapter;
import java.sql.Connection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.Project;
import model.Worklocation;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import service.LanguageService;
import service.PropertiesService;
import service.UndoService;
import sqlite.ConnectionFactory;
import testhelper.ReflectionHelper;
import testhelper.StubFactory;

import static org.junit.Assert.*;

public class SettingsViewControllerTest {

    private final static String DATABASE_PATH_AND_FULL_NAME = System.getProperty("user.dir") + "/sqlite/testdb.sqlite";
    private final static String LOG4J2_PATH_AND_FULL_NAME = System.getProperty("user.dir") + "/log4j2.xml";

    private StubFactory stubFactory;
    private ReflectionHelper reflectionHelper;

    private ConnectionFactory connectionFactory;
    private Connection connection;
    private ResourceBundle resourceBundle;
    private PropertiesService propertiesService;
    private LanguageService languageService;
    private UndoService undoService;
    private Log4jAdapter log4jAdapter;
    private WorkRecordDetailsViewController workRecordDetailsViewController;

    private SettingsViewController controller;

    private Tab settingsApplicationTab;
    private Label applicationAlwaysOnTopLabel;
    private ToggleButton applicationAlwaysOnTopToggleButton;
    private Label showSplasScreenLabel;
    private ToggleButton showSplashScreenToggleButton;

    private Tab settingsWorkrecordTab;
    private GridPane settingsWorkrecordTabGridPane;
    private Label workrecordCreateAutomatic;
    private ToggleButton workrecordAutomaticCreationToggleButton;
    private Label workrecordStartTimeDelta;
    private Label workrecordEndTimeDelta;
    private Label workrecordUseLastWorkrecordConfiguration;
    private ToggleButton workrecordUseLastWorkrecordConfigurationToggleButton;
    private Label workrecordLocationLabel;
    private ChoiceBox<Worklocation> workrecordLocationChoiceBox;
    private Label workrecordProjectLabel;
    private ChoiceBox<Project> workrecordProjectChoiceBox;

    private GridPane settingsWorktimeTabGridPane;
    private Label upperOverallOvertimeThresholdLabel;
    private Label lowerOverallOvertimeThresholdLabel;

    private Tab settingsWorkitemTab;
    private GridPane settingsWorkitemTabGridPane;
    private Label workitemEndTimeDelta;

    @BeforeClass
    public static void initJfx() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        try {
            Platform.startup(latch::countDown);
        } catch (IllegalStateException e) {
            latch.countDown();
        }
        latch.await();
    }

    @Before
    public void setUp() throws Exception {
        stubFactory = new StubFactory();
        reflectionHelper = new ReflectionHelper();

        languageService = stubFactory.createLanguageServiceStub();
        undoService = new UndoService();

        // Custom ResourceBundle stub returning keys for lookup
        resourceBundle = new ResourceBundle() {
            @Override
            protected Object handleGetObject(String key) {
                return key;
            }

            @Override
            public Enumeration<String> getKeys() {
                return Collections.emptyEnumeration();
            }

            @Override
            public Locale getLocale() {
                return Locale.ENGLISH;
            }
        };

        log4jAdapter = new Log4jAdapter(LOG4J2_PATH_AND_FULL_NAME);
        connectionFactory = new ConnectionFactory(resourceBundle, log4jAdapter);
        connection = connectionFactory.getConnection(DATABASE_PATH_AND_FULL_NAME);

        propertiesService = PropertiesService.getInstance();
        
        controller = new SettingsViewController(propertiesService, connection, workRecordDetailsViewController);

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                controller.setPrimaryStage(new Stage());
                setupUiControls();
                injectUiFields();
                controller.initialize(null, resourceBundle);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    private void setupUiControls() {
        settingsApplicationTab = new Tab();
        applicationAlwaysOnTopLabel = new Label();
        applicationAlwaysOnTopToggleButton = new ToggleButton();
        showSplasScreenLabel = new Label();
        showSplashScreenToggleButton = new ToggleButton();

        settingsWorkrecordTab = new Tab();
        settingsWorkrecordTabGridPane = new GridPane();
        workrecordCreateAutomatic = new Label();
        workrecordAutomaticCreationToggleButton = new ToggleButton();
        workrecordStartTimeDelta = new Label();
        workrecordEndTimeDelta = new Label();
        workrecordUseLastWorkrecordConfiguration = new Label();
        workrecordUseLastWorkrecordConfigurationToggleButton = new ToggleButton();
        workrecordLocationLabel = new Label();
        workrecordLocationChoiceBox = new ChoiceBox<>();
        workrecordProjectLabel = new Label();
        workrecordProjectChoiceBox = new ChoiceBox<>();

        settingsWorktimeTabGridPane = new GridPane();
        upperOverallOvertimeThresholdLabel = new Label();
        lowerOverallOvertimeThresholdLabel = new Label();

        settingsWorkitemTab = new Tab();
        settingsWorkitemTabGridPane = new GridPane();
        workitemEndTimeDelta = new Label();
    }

    private void injectUiFields() throws Exception {
        reflectionHelper.setField(controller, "settingsApplicationTab", settingsApplicationTab);
        reflectionHelper.setField(controller, "applicationAlwaysOnTopLabel", applicationAlwaysOnTopLabel);
        reflectionHelper.setField(controller, "applicationAlwaysOnTopToggleButton", applicationAlwaysOnTopToggleButton);
        reflectionHelper.setField(controller, "showSplasScreenLabel", showSplasScreenLabel);
        reflectionHelper.setField(controller, "showSplashScreenToggleButton", showSplashScreenToggleButton);

        reflectionHelper.setField(controller, "settingsWorkrecordTab", settingsWorkrecordTab);
        reflectionHelper.setField(controller, "settingsWorkrecordTabGridPane", settingsWorkrecordTabGridPane);
        reflectionHelper.setField(controller, "workrecordCreateAutomatic", workrecordCreateAutomatic);
        reflectionHelper.setField(controller, "workrecordAutomaticCreationToggleButton", workrecordAutomaticCreationToggleButton);
        reflectionHelper.setField(controller, "workrecordStartTimeDelta", workrecordStartTimeDelta);
        reflectionHelper.setField(controller, "workrecordEndTimeDelta", workrecordEndTimeDelta);
        reflectionHelper.setField(controller, "workrecordUseLastWorkrecordConfiguration", workrecordUseLastWorkrecordConfiguration);
        reflectionHelper.setField(controller, "workrecordUseLastWorkrecordConfigurationToggleButton", workrecordUseLastWorkrecordConfigurationToggleButton);
        reflectionHelper.setField(controller, "workrecordLocationLabel", workrecordLocationLabel);
        reflectionHelper.setField(controller, "workrecordLocationChoiceBox", workrecordLocationChoiceBox);
        reflectionHelper.setField(controller, "workrecordProjectLabel", workrecordProjectLabel);
        reflectionHelper.setField(controller, "workrecordProjectChoiceBox", workrecordProjectChoiceBox);

        reflectionHelper.setField(controller, "settingsWorktimeTabGridPane", settingsWorktimeTabGridPane);
        reflectionHelper.setField(controller, "upperOverallOvertimeThresholdLabel", upperOverallOvertimeThresholdLabel);
        reflectionHelper.setField(controller, "lowerOverallOvertimeThresholdLabel", lowerOverallOvertimeThresholdLabel);

        reflectionHelper.setField(controller, "settingsWorkitemTab", settingsWorkitemTab);
        reflectionHelper.setField(controller, "settingsWorkitemTabGridPane", settingsWorkitemTabGridPane);
        reflectionHelper.setField(controller, "workitemEndTimeDelta", workitemEndTimeDelta);
    }

    @After
    public void tearDown() {
        // Cleanup if needed
    }

    // --- Initialization & GUI Tests ---

    @Test
    public void initialize_ValidResourceBundle_StoresBundleAndInitializes() {
        controller.initialize(null, resourceBundle);
        assertEquals(resourceBundle, controller.getResourceBundle());
        assertNotNull(controller.eventManager);
    }

    @Test
    public void updateGuiItems_UpdatesComponentsCorrectly() {
        controller.setResourceBundle(resourceBundle);
        controller.updateGuiItems();

        assertEquals("Application", settingsApplicationTab.getText());
        assertEquals("ApplicationOnTop", applicationAlwaysOnTopLabel.getText());
        assertEquals("ShowSplashScreen", showSplasScreenLabel.getText());
        assertEquals("Workrecord", settingsWorkrecordTab.getText());
        assertEquals("WorkrecordAutomaticCreation", workrecordCreateAutomatic.getText());
        assertEquals("WorkrecordStartTimeDelta", workrecordStartTimeDelta.getText());
        assertEquals("WorkrecordEndTimeDelta", workrecordEndTimeDelta.getText());
        assertEquals("WorkrecordUseLastWorkrecordConfiguration", workrecordUseLastWorkrecordConfiguration.getText());
        assertEquals("Location", workrecordLocationLabel.getText());
        assertEquals("Project", workrecordProjectLabel.getText());
        assertEquals("UpperOverallOvertimeThreshold", upperOverallOvertimeThresholdLabel.getText());
        assertEquals("LowerOverallOvertimeThreshold", lowerOverallOvertimeThresholdLabel.getText());
        assertEquals("WorkitemEndTimeDelta", workitemEndTimeDelta.getText());
    }

    @Test
    public void preCloseAction_ExecutesSafely() {
        controller.setResourceBundle(resourceBundle);
        controller.preCloseAction();
        assertTrue(true);
    }
}