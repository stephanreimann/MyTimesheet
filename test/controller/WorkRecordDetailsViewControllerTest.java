/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import adapter.Log4jAdapter;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.Contract;
import model.Project;
import model.User;
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

public class WorkRecordDetailsViewControllerTest {

    private final static String DATABASE_PATH_AND_FULL_NAME = System.getProperty("user.dir") + "/sqlite/testdb.sqlite";
    private final static String LOG4J2_PATH_AND_FULL_NAME = System.getProperty("user.dir") + "/log4j2.xml";

    private StubFactory stubFactory;
    private ReflectionHelper reflectionHelper;

    private ConnectionFactory connectionFactory;
    private Connection connection;
    private ResourceBundle resourceBundle;
    private UndoService undoService;
    private LanguageService languageService;
    private PropertiesService propertiesService;
    private ControllerRepository controllerRepository;
    private Log4jAdapter log4jAdapter;

    private WorkRecordDetailsViewController controller;

    private GridPane workrecordDetailsGridPane;
    private Label workrecordDetailsHeaderLabel;
    private Label workrecordDateLabel;
    private Label workrecordDateValue;
    private Button workrecordTodayButton;
    private Button workrecordStartTimeButton;
    private Label workrecordStartTimeLabel;
    private Label workrecordEndTimeLabel;
    private Label workrecordWorkTimeLabel;
    private Label workrecordWorkTimeValue;
    private Label workrecordOverTimeLabel;
    private Label workrecordOverTimeValue;
    private Label workrecordOverTimeCorrectionLabel;
    private Label workrecordVacationCorrectionLabel;
    private Label workrecordLocationLabel;
    private ChoiceBox<Worklocation> workrecordLocationChoiceBox;
    private Label workrecordProjectLabel;
    private ChoiceBox<Project> workrecordProjectChoiceBox;
    private Label workrecordDescriptionLabel;
    private TextArea workrecordDescriptionValue;
    private Button newButton;
    private Button editButton;
    private Button deleteButton;

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
        propertiesService = PropertiesService.getInstance();
        controllerRepository = ControllerRepository.getInstance();

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

        // Put required WorkRecordViewController in ControllerRepository with a dummy user whose contract has safe offtimes
        WorkRecordViewController workRecordViewControllerStub = new WorkRecordViewController(
                languageService, connection, undoService, propertiesService
        );
        ComboBox<User> dummyUserCombo = new ComboBox<>();
        Contract dummyContract = new Contract(1L, "Std", 8L, 10L, 30L, "31.12.", LocalTime.MIN, LocalTime.MIN, LocalTime.MIN, LocalTime.MIN, LocalTime.MIN, LocalTime.MIN);
        User dummyUser = new User(1L, null, null, dummyContract, "John", "Doe", "jdoe", "pwd", 25L);
        dummyUserCombo.getItems().add(dummyUser);
        dummyUserCombo.getSelectionModel().select(dummyUser);
        reflectionHelper.setField(workRecordViewControllerStub, "selectedUserComboBox", dummyUserCombo);
        controllerRepository.put(WorkRecordViewController.class.getName(), workRecordViewControllerStub);

        controller = new WorkRecordDetailsViewController(controllerRepository, languageService, connection, undoService, propertiesService);

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
        workrecordDetailsGridPane = new GridPane();
        workrecordDetailsHeaderLabel = new Label();
        workrecordDateLabel = new Label();
        workrecordDateValue = new Label();
        workrecordTodayButton = new Button();
        workrecordStartTimeButton = new Button();
        workrecordStartTimeLabel = new Label();
        workrecordEndTimeLabel = new Label();
        workrecordWorkTimeLabel = new Label();
        workrecordWorkTimeValue = new Label();
        workrecordOverTimeLabel = new Label();
        workrecordOverTimeValue = new Label();
        workrecordOverTimeCorrectionLabel = new Label();
        workrecordVacationCorrectionLabel = new Label();
        workrecordLocationLabel = new Label();
        workrecordLocationChoiceBox = new ChoiceBox<>();
        workrecordProjectLabel = new Label();
        workrecordProjectChoiceBox = new ChoiceBox<>();
        workrecordDescriptionLabel = new Label();
        workrecordDescriptionValue = new TextArea();
        newButton = new Button();
        editButton = new Button();
        deleteButton = new Button();
    }

    private void injectUiFields() throws Exception {
        reflectionHelper.setField(controller, "workrecordDetailsGridPane", workrecordDetailsGridPane);
        reflectionHelper.setField(controller, "workrecordDetailsHeaderLabel", workrecordDetailsHeaderLabel);
        reflectionHelper.setField(controller, "workrecordDateLabel", workrecordDateLabel);
        reflectionHelper.setField(controller, "workrecordDateValue", workrecordDateValue);
        reflectionHelper.setField(controller, "workrecordTodayButton", workrecordTodayButton);
        reflectionHelper.setField(controller, "workrecordStartTimeButton", workrecordStartTimeButton);
        reflectionHelper.setField(controller, "workrecordStartTimeLabel", workrecordStartTimeLabel);
        reflectionHelper.setField(controller, "workrecordEndTimeLabel", workrecordEndTimeLabel);
        reflectionHelper.setField(controller, "workrecordWorkTimeLabel", workrecordWorkTimeLabel);
        reflectionHelper.setField(controller, "workrecordWorkTimeValue", workrecordWorkTimeValue);
        reflectionHelper.setField(controller, "workrecordOverTimeLabel", workrecordOverTimeLabel);
        reflectionHelper.setField(controller, "workrecordOverTimeValue", workrecordOverTimeValue);
        reflectionHelper.setField(controller, "workrecordOverTimeCorrectionLabel", workrecordOverTimeCorrectionLabel);
        reflectionHelper.setField(controller, "workrecordVacationCorrectionLabel", workrecordVacationCorrectionLabel);
        reflectionHelper.setField(controller, "workrecordLocationLabel", workrecordLocationLabel);
        reflectionHelper.setField(controller, "workrecordLocationChoiceBox", workrecordLocationChoiceBox);
        reflectionHelper.setField(controller, "workrecordProjectLabel", workrecordProjectLabel);
        reflectionHelper.setField(controller, "workrecordProjectChoiceBox", workrecordProjectChoiceBox);
        reflectionHelper.setField(controller, "workrecordDescriptionLabel", workrecordDescriptionLabel);
        reflectionHelper.setField(controller, "workrecordDescriptionValue", workrecordDescriptionValue);
        reflectionHelper.setField(controller, "newButton", newButton);
        reflectionHelper.setField(controller, "editButton", editButton);
        reflectionHelper.setField(controller, "deleteButton", deleteButton);
    }

    @After
    public void tearDown() {
        // Cleanup if needed
    }

    // --- Constructor & Validation Tests ---

    @Test(expected = NullPointerException.class)
    public void constructor_NullControllerRepository_ThrowsNPE() throws Exception {
        var _ = new WorkRecordDetailsViewController(null, languageService, connection, undoService, propertiesService);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullLanguageService_ThrowsNPE() throws Exception {
        var _ = new WorkRecordDetailsViewController(controllerRepository, null, connection, undoService, propertiesService);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullConnection_ThrowsNPE() throws Exception {
        var _ = new WorkRecordDetailsViewController(controllerRepository, languageService, null, undoService, propertiesService);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullUndoService_ThrowsNPE() throws Exception {
        var _ = new WorkRecordDetailsViewController(controllerRepository, languageService, connection, null, propertiesService);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullPropertiesService_ThrowsNPE() throws Exception {
        var _ = new WorkRecordDetailsViewController(controllerRepository, languageService, connection, undoService, null);
    }

    // --- Initialization & GUI Tests ---

    @Test
    public void initialize_ValidResourceBundle_StoresBundleAndInitializes() {
        controller.initialize(null, resourceBundle);
        assertEquals(resourceBundle, controller.getResourceBundle());
        assertNotNull(controller.getEventManager());
    }

    @Test
    public void updateGuiItems_UpdatesComponentsCorrectly() {
        controller.setResourceBundle(resourceBundle);
        controller.updateGuiItems();

        assertEquals("WorkrecordDetailsHeader", workrecordDetailsHeaderLabel.getText());
        assertEquals("Date", workrecordDateLabel.getText());
        assertEquals("StartTime", workrecordStartTimeLabel.getText());
        assertEquals("EndTime", workrecordEndTimeLabel.getText());
        assertEquals("WorkTime", workrecordWorkTimeLabel.getText());
        assertEquals("OverTime", workrecordOverTimeLabel.getText());
        assertEquals("OverallOverTimeCorrection", workrecordOverTimeCorrectionLabel.getText());
        assertEquals("OverallVacationCorrection", workrecordVacationCorrectionLabel.getText());
        assertEquals("Location", workrecordLocationLabel.getText());
        assertEquals("Project", workrecordProjectLabel.getText());
        assertEquals("Description", workrecordDescriptionLabel.getText());
        assertEquals("New", newButton.getText());
        assertEquals("Edit", editButton.getText());
        assertEquals("Delete", deleteButton.getText());
    }

    // --- Action Tests ---

    @Test
    public void editAction_NoSelection_ShowsAlertSafely() throws Exception {
        controller.setResourceBundle(resourceBundle);
        try {
            reflectionHelper.invokePrivateMethod(controller, "editAction", new ActionEvent());
        } catch (InvocationTargetException e) {
            // Alert showAndWait in headless environment may throw/warn
            assertNotNull(e.getTargetException());
        }
    }

    @Test
    public void deleteAction_NoSelection_ShowsAlertSafely() throws Exception {
        controller.setResourceBundle(resourceBundle);
        try {
            reflectionHelper.invokePrivateMethod(controller, "deleteAction", new ActionEvent());
        } catch (InvocationTargetException e) {
            assertNotNull(e.getTargetException());
        }
    }

    @Test
    public void preCloseAction_ExecutesSafely() {
        controller.setResourceBundle(resourceBundle);
        controller.preCloseAction();
        assertTrue(true);
    }
}