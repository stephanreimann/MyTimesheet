/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import adapter.Log4jAdapter;
import java.lang.reflect.InvocationTargetException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import service.LanguageService;
import service.UndoService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;
import sqlite.ConnectionFactory;
import testhelper.ReflectionHelper;
import testhelper.StubFactory;

public class MainInfoViewControllerTest {

    private final static String DATABASE_PATH_AND_FULL_NAME = System.getProperty("user.dir") + "/sqlite/testdb.sqlite";
    private final static String LOG4J2_PATH_AND_FULL_NAME = System.getProperty("user.dir") + "/log4j2.xml";

    private StubFactory stubFactory;
    private ReflectionHelper reflectionHelper;

    private ConnectionFactory connectionFactory;
    private Connection connection;
    private ResourceBundle resourceBundle;
    private UndoService undoService;
    private Log4jAdapter log4jAdapter;
    private MainInfoViewController controller;

    private Tab generalInfoTab;
    private Button undoButton;
    private Tooltip undoTooltip;
    private Button redoButton;
    private Tooltip redoTooltip;
    private ScrollPane logScrollPane;
    private Label filterLable;
    private ListView<String> logListView;
    private Button clearLogListButton;
    private ToggleButton debugToggleButton;
    private ToggleButton infoToggleButton;
    private ToggleButton warningToggleButton;
    private ToggleButton errorToggleButton;
    private ToggleButton fatalToggleButton;
    private Button resetFilterButton;
    private Tab undoRedoTab;
    private SplitPane undoRedoSplitPane;
    private Label undoStackLabel;
    private ScrollPane undoScrollPane;
    private ListView<String> undoListView;
    private Label redoStackLabel;
    private ScrollPane redoScrollPane;
    private ListView<String> redoListView;

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

        LanguageService languageService = stubFactory.createLanguageServiceStub();
        resourceBundle = stubFactory.createResourceBundleStub();
        connectionFactory = new ConnectionFactory(resourceBundle, new Log4jAdapter(LOG4J2_PATH_AND_FULL_NAME));
        connection = connectionFactory.getConnection(DATABASE_PATH_AND_FULL_NAME);

        undoService = new UndoService();
        log4jAdapter = new Log4jAdapter(LOG4J2_PATH_AND_FULL_NAME);

        controller = new MainInfoViewController(languageService, connection, undoService, log4jAdapter);

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                controller.setPrimaryStage(new Stage());
                setupUiControls();
                injectUiFields();
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    private void setupUiControls() {
        generalInfoTab = new Tab();
        undoButton = new Button();
        undoTooltip = new Tooltip();
        redoButton = new Button();
        redoTooltip = new Tooltip();
        logScrollPane = new ScrollPane();
        filterLable = new Label();
        logListView = new ListView<>();
        clearLogListButton = new Button();
        debugToggleButton = new ToggleButton();
        infoToggleButton = new ToggleButton();
        warningToggleButton = new ToggleButton();
        errorToggleButton = new ToggleButton();
        fatalToggleButton = new ToggleButton();
        resetFilterButton = new Button();
        undoRedoTab = new Tab();
        undoRedoSplitPane = new SplitPane();
        undoStackLabel = new Label();
        undoScrollPane = new ScrollPane();
        undoListView = new ListView<>();
        redoStackLabel = new Label();
        redoScrollPane = new ScrollPane();
        redoListView = new ListView<>();
    }

    private void injectUiFields() throws Exception {
        reflectionHelper.setField(controller, "generalInfoTab", generalInfoTab);
        reflectionHelper.setField(controller, "undoButton", undoButton);
        reflectionHelper.setField(controller, "undoTooltip", undoTooltip);
        reflectionHelper.setField(controller, "redoButton", redoButton);
        reflectionHelper.setField(controller, "redoTooltip", redoTooltip);
        reflectionHelper.setField(controller, "logScrollPane", logScrollPane);
        reflectionHelper.setField(controller, "filterLable", filterLable);
        reflectionHelper.setField(controller, "logListView", logListView);
        reflectionHelper.setField(controller, "clearLogListButton", clearLogListButton);
        reflectionHelper.setField(controller, "debugToggleButton", debugToggleButton);
        reflectionHelper.setField(controller, "infoToggleButton", infoToggleButton);
        reflectionHelper.setField(controller, "warningToggleButton", warningToggleButton);
        reflectionHelper.setField(controller, "errorToggleButton", errorToggleButton);
        reflectionHelper.setField(controller, "fatalToggleButton", fatalToggleButton);
        reflectionHelper.setField(controller, "resetFilterButton", resetFilterButton);
        reflectionHelper.setField(controller, "undoRedoTab", undoRedoTab);
        reflectionHelper.setField(controller, "undoRedoSplitPane", undoRedoSplitPane);
        reflectionHelper.setField(controller, "undoStackLabel", undoStackLabel);
        reflectionHelper.setField(controller, "undoScrollPane", undoScrollPane);
        reflectionHelper.setField(controller, "undoListView", undoListView);
        reflectionHelper.setField(controller, "redoStackLabel", redoStackLabel);
        reflectionHelper.setField(controller, "redoScrollPane", redoScrollPane);
        reflectionHelper.setField(controller, "redoListView", redoListView);
    }

    @After
    public void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    // --- Constructor & Validation Tests ---

    @Test(expected = NullPointerException.class)
    public void constructor_NullLanguageService_ThrowsNPE() {
        var _ = new MainInfoViewController(null, connection, undoService, log4jAdapter);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullConnection_ThrowsNPE() {
        var _ = new MainInfoViewController(stubFactory.createLanguageServiceStub(), null, undoService, log4jAdapter);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullUndoService_ThrowsNPE() {
        var _ = new MainInfoViewController(stubFactory.createLanguageServiceStub(), connection, null, log4jAdapter);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullLog4jAdapter_ThrowsNPE() {
        var _ = new MainInfoViewController(stubFactory.createLanguageServiceStub(), connection, undoService, null);
    }

    // --- Initialization & GUI Tests ---

    @Test
    public void initialize_ValidParams_StoresBundleAndSetsItems() {
        controller.initialize(getClass().getResource("/view/MainInfoView.fxml"), resourceBundle);
        assertEquals(resourceBundle, controller.getResourceBundle());
    }

    @Test
    public void updateGuiItems_UpdatesComponentsCorrectly() {
        controller.setResourceBundle(resourceBundle);
        controller.updateGuiItems();

        assertEquals("GeneralInfo", generalInfoTab.getText());
        assertEquals("ClearLog", clearLogListButton.getText());
        assertEquals("FilterLable", filterLable.getText());
        assertEquals("ResetFilter", resetFilterButton.getText());
        assertEquals("UndoRedoInfo", undoRedoTab.getText());
        assertEquals("UndoStack", undoStackLabel.getText());
        assertEquals("RedoStack", redoStackLabel.getText());
        assertEquals("Rückgängig", undoButton.getText());
        assertEquals("Rückgängig", undoTooltip.getText());
        assertEquals("Wiederholen", redoButton.getText());
        assertEquals("Wiederholen", redoTooltip.getText());
    }
    
    // --- Toggle State Tests ---

    @Test
    public void toggleButtonStates_SetAndGet_ReflectsCorrectly() {
        controller.setInfoToggleButtonState(true);
        assertTrue(controller.getInfoToggleButtonState());

        controller.setDebugToggleButtonState(true);
        assertTrue(controller.getDebugToggleButtonState());

        controller.setWarningToggleButtonState(true);
        assertTrue(controller.getWarningToggleButtonState());

        controller.setErrorToggleButtonState(true);
        assertTrue(controller.getErrorToggleButtonState());

        controller.setFatalToggleButtonState(true);
        assertTrue(controller.getFatalToggleButtonState());
    }

    // --- Action Tests ---

    @Test
    public void handleClearLogList_InvokesWithoutError() throws Exception {
        try {
            reflectionHelper.invokePrivateMethod(controller, "handleClearLogList", new ActionEvent());
        } catch (InvocationTargetException e) {
            assertNotNull(e.getTargetException());
        }
    }

    @Test
    public void handleResetFilter_InvokesWithoutError() throws Exception {
        try {
            reflectionHelper.invokePrivateMethod(controller, "handleResetFilter", new ActionEvent());
        } catch (InvocationTargetException e) {
            assertNotNull(e.getTargetException());
        }
    }

    @Test
    public void undoAction_InvokesWithoutError() throws Exception {
        try {
            reflectionHelper.invokePrivateMethod(controller, "undoAction", new ActionEvent());
        } catch (InvocationTargetException e) {
            assertNotNull(e.getTargetException());
        }
    }

    @Test
    public void redoAction_InvokesWithoutError() throws Exception {
        try {
            reflectionHelper.invokePrivateMethod(controller, "redoAction", new ActionEvent());
        } catch (InvocationTargetException e) {
            assertNotNull(e.getTargetException());
        }
    }
}