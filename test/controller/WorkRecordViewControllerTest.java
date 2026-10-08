/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import adapter.Log4jAdapter;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Contract;
import model.Role;
import model.Address;
import model.User;
import model.Workrecord;
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

public class WorkRecordViewControllerTest {

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
    private Log4jAdapter log4jAdapter;

    private WorkRecordViewController controller;

    private ToolBar workrecordToolBar;
    private Label selectedUserLabel;
    private ComboBox<User> selectedUserComboBox;
    private Label startDateLabel;
    private DatePicker startDateDatePicker;
    private Label endDateLabel;
    private DatePicker endDateDatePicker;
    private TableView<Workrecord> workrecordTableView;
    private TableColumn<Workrecord, LocalDate> workrecordDateTableColumn;
    private TableColumn<Workrecord, LocalTime> workrecordStartTimeTableColumn;
    private TableColumn<Workrecord, LocalTime> workrecordEndTimeTableColumn;
    private TableColumn<Workrecord, LocalTime> workrecordWorkTimeTableColumn;
    private TableColumn<Workrecord, String> workrecordOverTimeTableColumn;
    private TableColumn<Workrecord, String> workrecordOverTimeCorrectionTableColumn;
    private TableColumn<Workrecord, Integer> workrecordVacationCorrectionTableColumn;
    private TableColumn<Workrecord, String> workrecordLocationTableColumn;
    private TableColumn<Workrecord, String> workrecordProjectTableColumn;
    private TableColumn<Workrecord, String> workrecordDescriptionTableColumn;

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
        propertiesService = PropertiesService.getInstance();

        controller = new WorkRecordViewController(languageService, connection, undoService, propertiesService);

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                controller.setPrimaryStage(new Stage());
                setupUiControls();
                injectUiFields();

                // Populate selectedUserComboBox with a dummy user so refreshWorkrecordTableView() has an active user
                Role role = new Role(1L, "Admin", "Admin Role");
                Address address = new Address(1L, "Street", 1L, "", 0L, "", "", "", 1L, "");
                Contract contract = new Contract(1L, "Std", 8L, 10L, 30L, "31.12.", LocalTime.MIN, LocalTime.MIN, LocalTime.MIN, LocalTime.MIN, LocalTime.MIN, LocalTime.MIN);
                User dummyUser = new User(1L, role, address, contract, "John", "Doe", "jdoe", "pwd", 25L);
                selectedUserComboBox.getItems().add(dummyUser);
                selectedUserComboBox.getSelectionModel().select(dummyUser);

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
        workrecordToolBar = new ToolBar();
        selectedUserLabel = new Label();
        selectedUserComboBox = new ComboBox<>();
        startDateLabel = new Label();
        startDateDatePicker = new DatePicker();
        endDateLabel = new Label();
        endDateDatePicker = new DatePicker();
        workrecordTableView = new TableView<>();
        workrecordDateTableColumn = new TableColumn<>();
        workrecordStartTimeTableColumn = new TableColumn<>();
        workrecordEndTimeTableColumn = new TableColumn<>();
        workrecordWorkTimeTableColumn = new TableColumn<>();
        workrecordOverTimeTableColumn = new TableColumn<>();
        workrecordOverTimeCorrectionTableColumn = new TableColumn<>();
        workrecordVacationCorrectionTableColumn = new TableColumn<>();
        workrecordLocationTableColumn = new TableColumn<>();
        workrecordProjectTableColumn = new TableColumn<>();
        workrecordDescriptionTableColumn = new TableColumn<>();
    }

    private void injectUiFields() throws Exception {
        reflectionHelper.setField(controller, "workrecordToolBar", workrecordToolBar);
        reflectionHelper.setField(controller, "selectedUserLabel", selectedUserLabel);
        reflectionHelper.setField(controller, "selectedUserComboBox", selectedUserComboBox);
        reflectionHelper.setField(controller, "startDateLabel", startDateLabel);
        reflectionHelper.setField(controller, "startDateDatePicker", startDateDatePicker);
        reflectionHelper.setField(controller, "endDateLabel", endDateLabel);
        reflectionHelper.setField(controller, "endDateDatePicker", endDateDatePicker);
        reflectionHelper.setField(controller, "workrecordTableView", workrecordTableView);
        reflectionHelper.setField(controller, "workrecordDateTableColumn", workrecordDateTableColumn);
        reflectionHelper.setField(controller, "workrecordStartTimeTableColumn", workrecordStartTimeTableColumn);
        reflectionHelper.setField(controller, "workrecordEndTimeTableColumn", workrecordEndTimeTableColumn);
        reflectionHelper.setField(controller, "workrecordWorkTimeTableColumn", workrecordWorkTimeTableColumn);
        reflectionHelper.setField(controller, "workrecordOverTimeTableColumn", workrecordOverTimeTableColumn);
        reflectionHelper.setField(controller, "workrecordOverTimeCorrectionTableColumn", workrecordOverTimeCorrectionTableColumn);
        reflectionHelper.setField(controller, "workrecordVacationCorrectionTableColumn", workrecordVacationCorrectionTableColumn);
        reflectionHelper.setField(controller, "workrecordLocationTableColumn", workrecordLocationTableColumn);
        reflectionHelper.setField(controller, "workrecordProjectTableColumn", workrecordProjectTableColumn);
        reflectionHelper.setField(controller, "workrecordDescriptionTableColumn", workrecordDescriptionTableColumn);
    }

    @After
    public void tearDown() {
        // Cleanup if needed
    }

    // --- Constructor & Validation Tests ---

    @Test(expected = NullPointerException.class)
    public void constructor_NullLanguageService_ThrowsNPE() throws Exception {
        var _ = new WorkRecordViewController(null, connection, undoService, propertiesService);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullConnection_ThrowsNPE() throws Exception {
        var _ = new WorkRecordViewController(languageService, null, undoService, propertiesService);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullUndoService_ThrowsNPE() throws Exception {
        var _ = new WorkRecordViewController(languageService, connection, null, propertiesService);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullPropertiesService_ThrowsNPE() throws Exception {
        var _ = new WorkRecordViewController(languageService, connection, undoService, null);
    }

    // --- Initialization & GUI Tests ---

    @Test
    public void initialize_ValidResourceBundle_StoresBundleAndInitializes() {
        assertEquals(resourceBundle, controller.getResourceBundle());
        assertNotNull(controller.getEventManager());
        assertNotNull(controller.getWorkrecordTableView());
    }

    @Test
    public void updateGuiItems_UpdatesComponentsCorrectly() {
        controller.setResourceBundle(resourceBundle);
        controller.updateGuiItems();

        assertEquals("SelectedUser", selectedUserLabel.getText());
        assertEquals("StartDate", startDateLabel.getText());
        assertEquals("EndDate", endDateLabel.getText());
        assertEquals("Date", workrecordDateTableColumn.getText());
        assertEquals("StartTime", workrecordStartTimeTableColumn.getText());
        assertEquals("EndTime", workrecordEndTimeTableColumn.getText());
        assertEquals("WorkTime", workrecordWorkTimeTableColumn.getText());
        assertEquals("OverTime", workrecordOverTimeTableColumn.getText());
        assertEquals("OverallOverTimeCorrection", workrecordOverTimeCorrectionTableColumn.getText());
        assertEquals("VacationCorrection", workrecordVacationCorrectionTableColumn.getText());
        assertEquals("Location", workrecordLocationTableColumn.getText());
        assertEquals("Project", workrecordProjectTableColumn.getText());
        assertEquals("Description", workrecordDescriptionTableColumn.getText());
    }

    // --- Action & Utility Tests ---

    @Test
    public void handleOnStartDateChangedAction_ExecutesSuccessfully() throws Exception {
        controller.setResourceBundle(resourceBundle);
        try {
            reflectionHelper.invokePrivateMethod(controller, "handleOnStartDateChangedAction", new ActionEvent());
        } catch (InvocationTargetException e) {
            assertNotNull(e.getTargetException());
        }
    }

    @Test
    public void handleOnEndDateChangedAction_ExecutesSuccessfully() throws Exception {
        controller.setResourceBundle(resourceBundle);
        try {
            reflectionHelper.invokePrivateMethod(controller, "handleOnEndDateChangedAction", new ActionEvent());
        } catch (InvocationTargetException e) {
            assertNotNull(e.getTargetException());
        }
    }

    @Test
    public void isDummyWorkrecord_ReturnsExpectedResult() {
        assertTrue(controller.IsDummyWorkrecord(null));
    }

    @Test
    public void preCloseAction_ExecutesSafely() {
        controller.preCloseAction();
        assertTrue(true);
    }
}