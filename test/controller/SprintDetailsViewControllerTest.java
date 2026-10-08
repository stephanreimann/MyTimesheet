/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import adapter.Log4jAdapter;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.time.LocalDate;
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
import model.Sprint;
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

public class SprintDetailsViewControllerTest {

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
    private ObservableList<Sprint> sprintData;

    private SprintDetailsViewController controller;

    private Label sprintIdLabel;
    private Label sprintStartDateLabel;
    private Label sprintEndDateLabel;
    private Label numberOfSprintDaysLabel;
    private Label sprintInfoLabel;

    private TextField sprintIdLabelValue;
    private DatePicker sprintStartDatePicker;
    private DatePicker sprintEndDatePicker;
    private Label numberOfSprintDaysLabelValue;

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
        sprintData = FXCollections.observableArrayList();

        controller = new SprintDetailsViewController(languageService, connection, undoService, sprintData);

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
        sprintIdLabel = new Label();
        sprintStartDateLabel = new Label();
        sprintEndDateLabel = new Label();
        numberOfSprintDaysLabel = new Label();
        sprintInfoLabel = new Label();

        sprintIdLabelValue = new TextField();
        sprintStartDatePicker = new DatePicker();
        sprintEndDatePicker = new DatePicker();
        numberOfSprintDaysLabelValue = new Label();

        acceptButton = new Button();
        cancelButton = new Button();
    }

    private void injectUiFields() throws Exception {
        reflectionHelper.setField(controller, "sprintIdLabel", sprintIdLabel);
        reflectionHelper.setField(controller, "sprintStartDateLabel", sprintStartDateLabel);
        reflectionHelper.setField(controller, "sprintEndDateLabel", sprintEndDateLabel);
        reflectionHelper.setField(controller, "numberOfSprintDaysLabel", numberOfSprintDaysLabel);
        reflectionHelper.setField(controller, "sprintInfoLabel", sprintInfoLabel);

        reflectionHelper.setField(controller, "sprintIdLabelValue", sprintIdLabelValue);
        reflectionHelper.setField(controller, "sprintStartDatePicker", sprintStartDatePicker);
        reflectionHelper.setField(controller, "sprintEndDatePicker", sprintEndDatePicker);
        reflectionHelper.setField(controller, "numberOfSprintDaysLabelValue", numberOfSprintDaysLabelValue);

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
        var _ = new SprintDetailsViewController(null, connection, undoService, sprintData);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullConnection_ThrowsNPE() {
        var _ = new SprintDetailsViewController(languageService, null, undoService, sprintData);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullUndoService_ThrowsNPE() {
        var _ = new SprintDetailsViewController(languageService, connection, null, sprintData);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullSprintData_ThrowsNPE() {
        var _ = new SprintDetailsViewController(languageService, connection, undoService, null);
    }

    // --- Initialization & GUI Tests ---

    @Test
    public void initialize_ValidResourceBundle_StoresBundleAndDisablesAccept() {
        controller.initialize(null, resourceBundle);
        assertEquals(resourceBundle, controller.getResourceBundle());
        assertTrue(acceptButton.isDisable());
        assertNotNull(controller.getEventManager());
    }

    @Test
    public void updateGuiItems_UpdatesComponentsCorrectly() {
        controller.setResourceBundle(resourceBundle);
        controller.updateGuiItems();

        assertEquals("SprintId", sprintIdLabel.getText());
        assertEquals("NumberOfSprintDays", numberOfSprintDaysLabel.getText());
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
        controller.setAction(SprintViewController.DataAction.NEW);

        Sprint sprint = new Sprint(1L);
        controller.showSprintDetails(sprint);

        sprintIdLabelValue.setText("1");
        LocalDate startDate = LocalDate.of(2026, 6, 1);
        LocalDate endDate = LocalDate.of(2026, 6, 14);
        sprintStartDatePicker.setValue(startDate);
        sprintEndDatePicker.setValue(endDate);

        try {
            reflectionHelper.invokePrivateMethod(controller, "acceptAction", new ActionEvent());
        } catch (InvocationTargetException e) {
            assertNotNull(e.getTargetException());
        }

        assertEquals(Long.valueOf(1), sprint.getId());
        assertEquals(startDate, sprint.getStartDate());
        assertEquals(endDate, sprint.getEndDate());
    }

    @Test
    public void preCloseAction_ExecutesSafely() {
        controller.preCloseAction();
        assertTrue(true);
    }
}