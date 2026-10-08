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
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import service.LanguageService;
import service.UndoService;
import sqlite.ConnectionFactory;
import testhelper.ReflectionHelper;
import testhelper.StubFactory;

import static org.junit.Assert.*;

public class MainStatusBarViewControllerTest {

    private final static String DATABASE_PATH_AND_FULL_NAME = System.getProperty("user.dir") + "/sqlite/testdb.sqlite";
    private final static String LOG4J2_PATH_AND_FULL_NAME = System.getProperty("user.dir") + "/log4j2.xml";

    private StubFactory stubFactory;
    private ReflectionHelper reflectionHelper;

    private ConnectionFactory connectionFactory;
    private Connection connection;
    private ResourceBundle resourceBundle;
    private LanguageService languageService;
    private MainInfoViewController mainInfoViewController;
    private Log4jAdapter log4jAdapter;

    private MainStatusBarViewController controller;

    private Label messageLabel;
    private ImageView actualLanguage;

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
        
        // Custom ResourceBundle stub returning German locale by default for language indicator tests
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
                return Locale.GERMAN;
            }
        };

        log4jAdapter = new Log4jAdapter(LOG4J2_PATH_AND_FULL_NAME);
        connectionFactory = new ConnectionFactory(resourceBundle, log4jAdapter);
        connection = connectionFactory.getConnection(DATABASE_PATH_AND_FULL_NAME);

        // Instantiate MainInfoViewController with correct constructor arguments
        mainInfoViewController = new MainInfoViewController(
                languageService,
                connection,
                new UndoService(),
                log4jAdapter
        );

        controller = new MainStatusBarViewController(languageService, connection, mainInfoViewController, log4jAdapter);

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
        messageLabel = new Label();
        actualLanguage = new ImageView();
    }

    private void injectUiFields() throws Exception {
        reflectionHelper.setField(controller, "messageLabel", messageLabel);
        reflectionHelper.setField(controller, "actualLanguage", actualLanguage);
    }

    @After
    public void tearDown() {
        // Cleanup if needed
    }

    // --- Constructor & Validation Tests ---

    @Test(expected = NullPointerException.class)
    public void constructor_NullLanguageService_ThrowsNPE() {
        var _ = new MainStatusBarViewController(null, connection, mainInfoViewController, log4jAdapter);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullConnection_ThrowsNPE() {
        var _ = new MainStatusBarViewController(languageService, null, mainInfoViewController, log4jAdapter);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullMainInfoViewController_ThrowsNPE() {
        var _ = new MainStatusBarViewController(languageService, connection, null, log4jAdapter);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullLog4jAdapter_ThrowsNPE() {
        var _ = new MainStatusBarViewController(languageService, connection, mainInfoViewController, null);
    }

    // --- Initialization & GUI Tests ---

    @Test
    public void initialize_ValidSetup_BindsStateMessageAndInitializes() throws Exception {
        controller.setResourceBundle(resourceBundle);
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                controller.initialize(null, resourceBundle);
                assertEquals(resourceBundle, controller.getResourceBundle());
                assertNotNull(controller.getMessageLabel());
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    public void updateGuiItems_GermanLocale_UpdatesLanguageIndicator() throws Exception {
        controller.setResourceBundle(resourceBundle);
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                controller.updateGuiItems();
                assertNotNull(actualLanguage.getImage());
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    public void updateGuiItems_EnglishLocale_UpdatesLanguageIndicator() throws Exception {
        ResourceBundle enBundle = new ResourceBundle() {
            @Override
            protected Object handleGetObject(String key) { return key; }
            @Override
            public Enumeration<String> getKeys() { return Collections.emptyEnumeration(); }
            @Override
            public Locale getLocale() { return Locale.ENGLISH; }
        };
        controller.setResourceBundle(enBundle);
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                controller.updateGuiItems();
                assertNotNull(actualLanguage.getImage());
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    public void updateGuiItems_SpanishLocale_UpdatesLanguageIndicator() throws Exception {
        ResourceBundle esBundle = new ResourceBundle() {
            @Override
            protected Object handleGetObject(String key) { return key; }
            @Override
            public Enumeration<String> getKeys() { return Collections.emptyEnumeration(); }
            @Override
            public Locale getLocale() { return new Locale("es"); }
        };
        controller.setResourceBundle(esBundle);
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                controller.updateGuiItems();
                assertNotNull(actualLanguage.getImage());
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    public void updateGuiItems_FrenchLocale_UpdatesLanguageIndicator() throws Exception {
        ResourceBundle frBundle = new ResourceBundle() {
            @Override
            protected Object handleGetObject(String key) { return key; }
            @Override
            public Enumeration<String> getKeys() { return Collections.emptyEnumeration(); }
            @Override
            public Locale getLocale() { return Locale.FRENCH; }
        };
        controller.setResourceBundle(frBundle);
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                controller.updateGuiItems();
                assertNotNull(actualLanguage.getImage());
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    public void updateGuiItems_ItalianLocale_UpdatesLanguageIndicator() throws Exception {
        ResourceBundle itBundle = new ResourceBundle() {
            @Override
            protected Object handleGetObject(String key) { return key; }
            @Override
            public Enumeration<String> getKeys() { return Collections.emptyEnumeration(); }
            @Override
            public Locale getLocale() { return Locale.ITALIAN; }
        };
        controller.setResourceBundle(itBundle);
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                controller.updateGuiItems();
                assertNotNull(actualLanguage.getImage());
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    // --- Binding Tests ---

    @Test
    public void bindProgressBarAndMessageLabel_Bind_BindsSuccessfully() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() { return null; }
                };
                controller.bindProgressBarAndMessageLabel(MainStatusBarViewController.BindingMethode.Bind, task);
                assertTrue(messageLabel.textProperty().isBound());
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    public void bindProgressBarAndMessageLabel_UnBind_UnbindsSuccessfully() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() { return null; }
                };
                controller.bindProgressBarAndMessageLabel(MainStatusBarViewController.BindingMethode.Bind, task);
                controller.bindProgressBarAndMessageLabel(MainStatusBarViewController.BindingMethode.UnBind, task);
                assertFalse(messageLabel.textProperty().isBound());
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    public void bindProgressBarAndMessageLabel_NullBinding_ThrowsException() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        final boolean[] exceptionThrown = {false};
        Platform.runLater(() -> {
            try {
                controller.bindProgressBarAndMessageLabel(null, null);
            } catch (NullPointerException e) {
                exceptionThrown[0] = true;
            } finally {
                latch.countDown();
            }
        });
        latch.await();
        assertTrue("Expected NullPointerException was not thrown", exceptionThrown[0]);
    }}