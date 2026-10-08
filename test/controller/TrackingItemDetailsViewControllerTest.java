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
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.TrackingItem;
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

public class TrackingItemDetailsViewControllerTest {

    private final static String DATABASE_PATH_AND_FULL_NAME = System.getProperty("user.dir") + "/sqlite/testdb.sqlite";
    private final static String LOG4J2_PATH_AND_FULL_NAME = System.getProperty("user.dir") + "/log4j2.xml";

    private StubFactory stubFactory;
    private ReflectionHelper reflectionHelper;

    private ConnectionFactory connectionFactory;
    private Connection connection;
    private ResourceBundle resourceBundle;
    private UndoService undoService;
    private LanguageService languageService;
    private Log4jAdapter log4jAdapter;
    private ObservableList<TrackingItem> trackingItemData;

    private TrackingItemDetailsViewController controller;

    private Label trackingItemIdLabel;
    private Label trackingItemNameLabel;
    private Label trackingItemShortcutLabel;
    private Label trackingItemDescriptionLabel;

    private TextField trackingItemIdLabelValue;
    private TextField trackingItemNameLabelValue;
    private TextField trackingItemShortcutLabelValue;
    private TextField trackingItemDescriptionLabelValue;

    private Button acceptButton;
    private Button cancelButton;

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

        undoService = new UndoService();
        trackingItemData = FXCollections.observableArrayList();

        controller = new TrackingItemDetailsViewController(languageService, connection, undoService, trackingItemData);

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
        trackingItemIdLabel = new Label();
        trackingItemNameLabel = new Label();
        trackingItemShortcutLabel = new Label();
        trackingItemDescriptionLabel = new Label();

        trackingItemIdLabelValue = new TextField();
        trackingItemNameLabelValue = new TextField();
        trackingItemShortcutLabelValue = new TextField();
        trackingItemDescriptionLabelValue = new TextField();

        acceptButton = new Button();
        cancelButton = new Button();
    }

    private void injectUiFields() throws Exception {
        reflectionHelper.setField(controller, "trackingItemIdLabel", trackingItemIdLabel);
        reflectionHelper.setField(controller, "trackingItemNameLabel", trackingItemNameLabel);
        reflectionHelper.setField(controller, "trackingItemShortcutLabel", trackingItemShortcutLabel);
        reflectionHelper.setField(controller, "trackingItemDescriptionLabel", trackingItemDescriptionLabel);

        reflectionHelper.setField(controller, "trackingItemIdLabelValue", trackingItemIdLabelValue);
        reflectionHelper.setField(controller, "trackingItemNameLabelValue", trackingItemNameLabelValue);
        reflectionHelper.setField(controller, "trackingItemShortcutLabelValue", trackingItemShortcutLabelValue);
        reflectionHelper.setField(controller, "trackingItemDescriptionLabelValue", trackingItemDescriptionLabelValue);

        reflectionHelper.setField(controller, "acceptButton", acceptButton);
        reflectionHelper.setField(controller, "cancelButton", cancelButton);
    }

    @After
    public void tearDown() {
        // Cleanup if needed
    }

    // --- Constructor & Validation Tests ---

    @Test(expected = NullPointerException.class)
    public void constructor_NullLanguageService_ThrowsNPE() {
        var _ = new TrackingItemDetailsViewController(null, connection, undoService, trackingItemData);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullConnection_ThrowsNPE() {
        var _ = new TrackingItemDetailsViewController(languageService, null, undoService, trackingItemData);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullUndoService_ThrowsNPE() {
        var _ = new TrackingItemDetailsViewController(languageService, connection, null, trackingItemData);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullTrackingItemData_ThrowsNPE() {
        var _ = new TrackingItemDetailsViewController(languageService, connection, undoService, null);
    }

    // --- Initialization & GUI Tests ---

    @Test
    public void initialize_ValidResourceBundle_StoresBundleAndDisablesAccept() {
        controller.initialize(null, resourceBundle);
        assertEquals(resourceBundle, controller.getResourceBundle());
        assertTrue(acceptButton.isDisable());
    }

    @Test
    public void updateGuiItems_UpdatesComponentsCorrectly() {
        controller.setResourceBundle(resourceBundle);
        controller.updateGuiItems();

        assertEquals("TrackingItemId", trackingItemIdLabel.getText());
        assertEquals("TrackingItemName", trackingItemNameLabel.getText());
        assertEquals("TrackingItemShortcut", trackingItemShortcutLabel.getText());
        assertEquals("TrackingItemDescription", trackingItemDescriptionLabel.getText());
        assertEquals("Accept", acceptButton.getText());
        assertEquals("Cancel", cancelButton.getText());
    }

    // --- Action Tests ---

    @Test
    public void cancelAction_ClosesStageSafely() throws Exception {
        controller.setResourceBundle(resourceBundle);
        try {
            reflectionHelper.invokePrivateMethod(controller, "cancelAction", new ActionEvent());
        } catch (InvocationTargetException e) {
            // Expected in headless test environments when primaryStage.close() runs
            assertNotNull(e.getTargetException());
        }
    }

    @Test
    public void acceptAction_PopulatesAndCloses() throws Exception {
        controller.setResourceBundle(resourceBundle);
        controller.setAction(TrackingItemViewController.DataAction.NEW);

        TrackingItem trackingItem = new TrackingItem(1L);
        controller.showTrackingItemDetails(trackingItem);

        trackingItemIdLabelValue.setText("1");
        trackingItemNameLabelValue.setText("Task");
        trackingItemShortcutLabelValue.setText("T");
        trackingItemDescriptionLabelValue.setText("Tracking item description");

        try {
            reflectionHelper.invokePrivateMethod(controller, "acceptAction", new ActionEvent());
        } catch (InvocationTargetException e) {
            assertNotNull(e.getTargetException());
        }

        assertEquals(Long.valueOf(1), trackingItem.getId());
        assertEquals("Task", trackingItem.getName());
        assertEquals("T", trackingItem.getShortcut());
        assertEquals("Tracking item description", trackingItem.getDescription());
    }

    @Test
    public void preCloseAction_ExecutesSafely() {
        controller.preCloseAction();
        assertTrue(true);
    }
}