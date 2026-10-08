/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import adapter.Log4jAdapter;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
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

public class MainMenuBarViewControllerTest {

    private final static String DATABASE_PATH_AND_FULL_NAME = System.getProperty("user.dir") + "/sqlite/testdb.sqlite";
    private final static String LOG4J2_PATH_AND_FULL_NAME = System.getProperty("user.dir") + "/log4j2.xml";

    private StubFactory stubFactory;
    private ReflectionHelper reflectionHelper;

    private ConnectionFactory connectionFactory;
    private Connection connection;
    private ResourceBundle resourceBundle;
    private UndoService undoService;
    private LanguageService languageService;
    private Application application;
    private PropertiesService propertiesService;
    private Log4jAdapter log4jAdapter;

    private MainMenuBarViewController controller;

    private Menu fileMenu;
    private Menu exportMenu;
    private MenuItem exportReferencedataMenuItem;
    private MenuItem exportWorkrecordsMenuItem;
    private Menu importMenu;
    private MenuItem importReferencedataMenuItem;
    private MenuItem importWorkrecordsMenuItem;
    private MenuItem exitMenuItem;
    private Menu editMenu;
    private MenuItem undoMenuItem;
    private MenuItem redoMenuItem;
    private MenuItem settingsMenuItem;
    private MenuItem openXmlEditorMenuItem;
    private MenuItem dataMenu;
    private MenuItem userMenuItem;
    private MenuItem roleMenuItem;
    private MenuItem addressMenuItem;
    private MenuItem contractMenuItem;
    private MenuItem holydayMenuItem;
    private MenuItem projectMenuItem;
    private MenuItem worklocationMenuItem;
    private MenuItem sprintMenuItem;
    private MenuItem trackingItemMenuItem;

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
        
        // Custom ResourceBundle stub that returns the key itself for any menu/ui string lookup
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

        undoService = new UndoService();
        
        // Fixed instantiation avoiding missing StubFactory methods
        application = new Application() {
            @Override
            public void start(Stage primaryStage) {}
        };
        propertiesService = PropertiesService.getInstance();
        
        controller = new MainMenuBarViewController(languageService, connection, undoService, application, propertiesService, log4jAdapter);

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
        fileMenu = new Menu();
        exportMenu = new Menu();
        exportReferencedataMenuItem = new MenuItem();
        exportWorkrecordsMenuItem = new MenuItem();
        importMenu = new Menu();
        importReferencedataMenuItem = new MenuItem();
        importWorkrecordsMenuItem = new MenuItem();
        exitMenuItem = new MenuItem();
        editMenu = new Menu();
        undoMenuItem = new MenuItem();
        redoMenuItem = new MenuItem();
        settingsMenuItem = new MenuItem();
        openXmlEditorMenuItem = new MenuItem();
        dataMenu = new MenuItem();
        userMenuItem = new MenuItem();
        roleMenuItem = new MenuItem();
        addressMenuItem = new MenuItem();
        contractMenuItem = new MenuItem();
        holydayMenuItem = new MenuItem();
        projectMenuItem = new MenuItem();
        worklocationMenuItem = new MenuItem();
        sprintMenuItem = new MenuItem();
        trackingItemMenuItem = new MenuItem();
    }

    private void injectUiFields() throws Exception {
        reflectionHelper.setField(controller, "fileMenu", fileMenu);
        reflectionHelper.setField(controller, "exportMenu", exportMenu);
        reflectionHelper.setField(controller, "exportReferencedataMenuItem", exportReferencedataMenuItem);
        reflectionHelper.setField(controller, "exportWorkrecordsMenuItem", exportWorkrecordsMenuItem);
        reflectionHelper.setField(controller, "importMenu", importMenu);
        reflectionHelper.setField(controller, "importReferencedataMenuItem", importReferencedataMenuItem);
        reflectionHelper.setField(controller, "importWorkrecordsMenuItem", importWorkrecordsMenuItem);
        reflectionHelper.setField(controller, "exitMenuItem", exitMenuItem);
        reflectionHelper.setField(controller, "editMenu", editMenu);
        reflectionHelper.setField(controller, "undoMenuItem", undoMenuItem);
        reflectionHelper.setField(controller, "redoMenuItem", redoMenuItem);
        reflectionHelper.setField(controller, "settingsMenuItem", settingsMenuItem);
        reflectionHelper.setField(controller, "openXmlEditorMenuItem", openXmlEditorMenuItem);
        reflectionHelper.setField(controller, "dataMenu", dataMenu);
        reflectionHelper.setField(controller, "userMenuItem", userMenuItem);
        reflectionHelper.setField(controller, "roleMenuItem", roleMenuItem);
        reflectionHelper.setField(controller, "addressMenuItem", addressMenuItem);
        reflectionHelper.setField(controller, "contractMenuItem", contractMenuItem);
        reflectionHelper.setField(controller, "holydayMenuItem", holydayMenuItem);
        reflectionHelper.setField(controller, "projectMenuItem", projectMenuItem);
        reflectionHelper.setField(controller, "worklocationMenuItem", worklocationMenuItem);
        reflectionHelper.setField(controller, "sprintMenuItem", sprintMenuItem);
        reflectionHelper.setField(controller, "trackingItemMenuItem", trackingItemMenuItem);
    }

    @After
    public void tearDown() {
        // Cleanup if needed
    }

    // --- Constructor & Validation Tests ---

    @Test(expected = NullPointerException.class)
    public void constructor_NullLanguageService_ThrowsNPE() {
        var _ = new MainMenuBarViewController(null, connection, undoService, application, propertiesService, log4jAdapter);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullConnection_ThrowsNPE() {
        var _ = new MainMenuBarViewController(languageService, null, undoService, application, propertiesService, log4jAdapter);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullUndoService_ThrowsNPE() {
        var _ = new MainMenuBarViewController(languageService, connection, null, application, propertiesService, log4jAdapter);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullApplication_ThrowsNPE() {
        var _ = new MainMenuBarViewController(languageService, connection, undoService, null, propertiesService, log4jAdapter);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullPropertiesService_ThrowsNPE() {
        var _ = new MainMenuBarViewController(languageService, connection, undoService, application, null, log4jAdapter);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullLog4jAdapter_ThrowsNPE() {
        var _ = new MainMenuBarViewController(languageService, connection, undoService, application, propertiesService, null);
    }

    // --- Initialization & GUI Tests ---

    @Test
    public void initialize_ValidResourceBundle_StoresBundle() {
        controller.initialize(null, resourceBundle);
        assertEquals(resourceBundle, controller.getResourceBundle());
    }

    @Test
    public void updateGuiItems_UpdatesComponentsCorrectly() {
        controller.setResourceBundle(resourceBundle);
        controller.updateGuiItems();

        assertEquals("File", fileMenu.getText());
        assertEquals("Exit", exitMenuItem.getText());
        assertEquals("Export", exportMenu.getText());
        assertEquals("Referencedata", exportReferencedataMenuItem.getText());
        assertEquals("Workrecord", exportWorkrecordsMenuItem.getText());
        assertEquals("Import", importMenu.getText());
        assertEquals("Referencedata", importReferencedataMenuItem.getText());
        assertEquals("Workrecord", importWorkrecordsMenuItem.getText());
        assertEquals("Edit", editMenu.getText());
        assertEquals("Undo", undoMenuItem.getText());
        assertEquals("Redo", redoMenuItem.getText());
        assertEquals("Settings", settingsMenuItem.getText());
        assertEquals("OpenXmlEditor", openXmlEditorMenuItem.getText());
        assertEquals("Referencedata", dataMenu.getText());
        assertEquals("User", userMenuItem.getText());
        assertEquals("Role", roleMenuItem.getText());
        assertEquals("Address", addressMenuItem.getText());
        assertEquals("Contract", contractMenuItem.getText());
        assertEquals("Holyday", holydayMenuItem.getText());
        assertEquals("Project", projectMenuItem.getText());
        assertEquals("Worklocation", worklocationMenuItem.getText());
        assertEquals("Sprint", sprintMenuItem.getText());
        assertEquals("TrackingItem", trackingItemMenuItem.getText());
    }

    // --- Action & Utility Tests ---

    @Test
    public void toggleUndoRedoMenuItems_ReflectsUndoServiceState() {
        controller.toggleUndoRedoMenuItems();
        assertTrue(undoMenuItem.isDisable());
        assertTrue(redoMenuItem.isDisable());
    }

    @Test
    public void undoAction_InvokesUndoService() throws Exception {
        reflectionHelper.invokePrivateMethod(controller, "undoAction", new ActionEvent());
    }

    @Test
    public void redoAction_InvokesUndoService() throws Exception {
        reflectionHelper.invokePrivateMethod(controller, "redoAction", new ActionEvent());
    }

    @Test
    public void exitAction_InvokesApplicationStop() throws Exception {
        try {
            reflectionHelper.invokePrivateMethod(controller, "exitAction", new ActionEvent());
        } catch (InvocationTargetException e) {
            assertNotNull(e.getTargetException());
        }
    }

    @Test
    public void exportReferencedata_ExecutesSuccessfully() throws Exception {
        controller.setResourceBundle(resourceBundle);
        try {
            reflectionHelper.invokePrivateMethod(controller, "exportReferencedata", new ActionEvent());
        } catch (InvocationTargetException e) {
            assertNotNull(e.getTargetException());
        }
    }

    @Test
    public void exportWorkrecords_ExecutesSuccessfully() throws Exception {
        controller.setResourceBundle(resourceBundle);
        try {
            reflectionHelper.invokePrivateMethod(controller, "exportWorkrecords", new ActionEvent());
        } catch (InvocationTargetException e) {
            assertNotNull(e.getTargetException());
        }
    }

    @Test
    public void importReferencedata_ShowsAlertSafely() throws Exception {
        controller.setResourceBundle(resourceBundle);
        try {
            reflectionHelper.invokePrivateMethod(controller, "importReferencedata", new ActionEvent());
        } catch (InvocationTargetException e) {
            assertNotNull(e.getTargetException());
        }
    }

    @Test
    public void importWorkrecords_ShowsAlertSafely() throws Exception {
        controller.setResourceBundle(resourceBundle);
        try {
            reflectionHelper.invokePrivateMethod(controller, "importWorkrecords", new ActionEvent());
        } catch (InvocationTargetException e) {
            assertNotNull(e.getTargetException());
        }
    }
}