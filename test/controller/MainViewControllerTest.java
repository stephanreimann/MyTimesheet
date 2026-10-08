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
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import service.LanguageService;
import sqlite.ConnectionFactory;
import testhelper.ReflectionHelper;
import testhelper.StubFactory;

import static org.junit.Assert.*;

public class MainViewControllerTest {

    private final static String DATABASE_PATH_AND_FULL_NAME = System.getProperty("user.dir") + "/sqlite/testdb.sqlite";
    private final static String LOG4J2_PATH_AND_FULL_NAME = System.getProperty("user.dir") + "/log4j2.xml";

    private StubFactory stubFactory;
    private ReflectionHelper reflectionHelper;

    private ConnectionFactory connectionFactory;
    private Connection connection;
    private ResourceBundle resourceBundle;
    private LanguageService languageService;
    private Log4jAdapter log4jAdapter;

    private MainViewController controller;

    private BorderPane borderPane;
    private VBox topBorderPaneVBox;
    private VBox leftBorderPaneVBox;
    private VBox rightBorderPaneVBox;
    private VBox bottomBorderPaneVBox;
    private SplitPane centerBorderPaneSplitPane;
    private Stage primaryStage;

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
        
        // Custom ResourceBundle stub returning keys for AppName lookup
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

        controller = new MainViewController(languageService, connection);

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                primaryStage = new Stage();
                controller.setPrimaryStage(primaryStage);
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
        borderPane = new BorderPane();
        topBorderPaneVBox = new VBox();
        leftBorderPaneVBox = new VBox();
        rightBorderPaneVBox = new VBox();
        bottomBorderPaneVBox = new VBox();
        centerBorderPaneSplitPane = new SplitPane();
    }

    private void injectUiFields() throws Exception {
        reflectionHelper.setField(controller, "borderPane", borderPane);
        reflectionHelper.setField(controller, "topBorderPaneVBox", topBorderPaneVBox);
        reflectionHelper.setField(controller, "leftBorderPaneVBox", leftBorderPaneVBox);
        reflectionHelper.setField(controller, "rightBorderPaneVBox", rightBorderPaneVBox);
        reflectionHelper.setField(controller, "bottomBorderPaneVBox", bottomBorderPaneVBox);
        reflectionHelper.setField(controller, "centerBorderPaneSplitPane", centerBorderPaneSplitPane);
    }

    @After
    public void tearDown() {
        // Cleanup if needed
    }

    // --- Constructor & Validation Tests ---

    @Test(expected = NullPointerException.class)
    public void constructor_NullLanguageService_ThrowsNPE() {
        var _ = new MainViewController(null, connection);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullConnection_ThrowsNPE() {
        var _ = new MainViewController(languageService, null);
    }

    // --- Initialization & GUI Tests ---

    @Test
    public void initialize_ValidArguments_InitializesSuccessfully() throws Exception {
        controller.setResourceBundle(resourceBundle);
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                controller.initialize(null, resourceBundle);
                assertEquals(resourceBundle, controller.getResourceBundle());
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    public void updateGuiItems_UpdatesPrimaryStageTitle() throws Exception {
        controller.setResourceBundle(resourceBundle);
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                controller.updateGuiItems();
                assertEquals("AppName", primaryStage.getTitle());
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    // --- Getter Tests ---

    @Test
    public void getters_ReturnInjectedComponents() {
        assertEquals(borderPane, controller.getBorderPane());
        assertEquals(topBorderPaneVBox, controller.getTopBorderPaneVBox());
        assertEquals(leftBorderPaneVBox, controller.getLeftBorderPaneVBox());
        assertEquals(rightBorderPaneVBox, controller.getRightBorderPaneVBox());
        assertEquals(bottomBorderPaneVBox, controller.getBottomBorderPaneVBox());
        assertEquals(centerBorderPaneSplitPane, controller.getCenterBorderPaneSplitPane());
    }

    @Test
    public void preCloseAction_ExecutesSafely() {
        controller.preCloseAction();
        // Verifies no exception thrown
        assertTrue(true);
    }
}