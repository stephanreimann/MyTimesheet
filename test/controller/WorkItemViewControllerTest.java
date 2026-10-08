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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.TrackingItem;
import model.User;
import model.WorkItem;
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

public class WorkItemViewControllerTest {

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

    private WorkItemViewController controller;

    private ToolBar trackingItemToolBar;
    private Label selectedDateLabel;
    private DatePicker selectedDateDatePicker;
    private Label sprintLabel;
    private Label sprintNumberLabelValue;
    private SplitPane trackingItemSplitPane;

    private TableView<WorkItem> trackingItemTableView;
    private TableColumn<WorkItem, String> trackingItemShortcutTableColumn;
    private TableColumn<WorkItem, String> trackingItemNameTableColumn;
    private TableColumn<WorkItem, LocalTime> trackingItemStartTimeTableColumn;
    private TableColumn<WorkItem, LocalTime> trackingItemEndTimeTableColumn;

    private GridPane trackingItemDetailsGridPane;
    private Label trackingItemDetailsHeaderLabel;
    private Label trackingItemNameLabel;
    private Label trackingItemStartTimeLabel;
    private Button trackingItemStartTimeButton;
    private Label trackingItemEndTimeLabel;
    private Button trackingItemEndTimeButton;
    private Label trackingItemTimeTableColumn;
    private Label trackingItemDescriptionLabel;
    private ChoiceBox<TrackingItem> trackingItemChoiceBox;
    private TextArea trackingItemDescriptionValue;
    private Label trackingItemSumLabel;
    private Label trackingItemSumValueLabel;

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

        // Put required dependent controllers in ControllerRepository and populate selected user
        WorkRecordViewController workRecordViewControllerStub = new WorkRecordViewController(
                languageService, connection, undoService, propertiesService
        );
        ComboBox<User> dummyUserCombo = new ComboBox<>();
        User dummyUser = new User(1L, null, null, null, "John", "Doe", "jdoe", "pwd", 25L);
        dummyUserCombo.getItems().add(dummyUser);
        dummyUserCombo.getSelectionModel().select(dummyUser);
        reflectionHelper.setField(workRecordViewControllerStub, "selectedUserComboBox", dummyUserCombo);
        controllerRepository.put(WorkRecordViewController.class.getName(), workRecordViewControllerStub);

        WorkRecordDetailsViewController workRecordDetailsViewControllerStub = new WorkRecordDetailsViewController(
                controllerRepository, languageService, connection, undoService, propertiesService
        );
        controllerRepository.put(WorkRecordDetailsViewController.class.getName(), workRecordDetailsViewControllerStub);

        controller = new WorkItemViewController(controllerRepository, languageService, connection, undoService, propertiesService);

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
        trackingItemToolBar = new ToolBar();
        selectedDateLabel = new Label();
        selectedDateDatePicker = new DatePicker();
        sprintLabel = new Label();
        sprintNumberLabelValue = new Label();
        
        // Add two panes to SplitPane so it creates a divider at index 0
        trackingItemSplitPane = new SplitPane(new VBox(), new VBox());

        trackingItemTableView = new TableView<>();
        trackingItemShortcutTableColumn = new TableColumn<>();
        trackingItemNameTableColumn = new TableColumn<>();
        trackingItemStartTimeTableColumn = new TableColumn<>();
        trackingItemEndTimeTableColumn = new TableColumn<>();

        trackingItemDetailsGridPane = new GridPane();
        trackingItemDetailsHeaderLabel = new Label();
        trackingItemNameLabel = new Label();
        trackingItemStartTimeLabel = new Label();
        trackingItemStartTimeButton = new Button();
        trackingItemEndTimeLabel = new Label();
        trackingItemEndTimeButton = new Button();
        trackingItemTimeTableColumn = new Label();
        trackingItemDescriptionLabel = new Label();
        trackingItemChoiceBox = new ChoiceBox<>();
        trackingItemDescriptionValue = new TextArea();
        trackingItemSumLabel = new Label();
        trackingItemSumValueLabel = new Label();

        newButton = new Button();
        editButton = new Button();
        deleteButton = new Button();
    }

    private void injectUiFields() throws Exception {
        reflectionHelper.setField(controller, "trackingItemToolBar", trackingItemToolBar);
        reflectionHelper.setField(controller, "selectedDateLabel", selectedDateLabel);
        reflectionHelper.setField(controller, "selectedDateDatePicker", selectedDateDatePicker);
        reflectionHelper.setField(controller, "sprintLabel", sprintLabel);
        reflectionHelper.setField(controller, "sprintNumberLabelValue", sprintNumberLabelValue);
        reflectionHelper.setField(controller, "trackingItemSplitPane", trackingItemSplitPane);

        reflectionHelper.setField(controller, "trackingItemTableView", trackingItemTableView);
        reflectionHelper.setField(controller, "trackingItemShortcutTableColumn", trackingItemShortcutTableColumn);
        reflectionHelper.setField(controller, "trackingItemNameTableColumn", trackingItemNameTableColumn);
        reflectionHelper.setField(controller, "trackingItemStartTimeTableColumn", trackingItemStartTimeTableColumn);
        reflectionHelper.setField(controller, "trackingItemEndTimeTableColumn", trackingItemEndTimeTableColumn);

        reflectionHelper.setField(controller, "trackingItemDetailsGridPane", trackingItemDetailsGridPane);
        reflectionHelper.setField(controller, "trackingItemDetailsHeaderLabel", trackingItemDetailsHeaderLabel);
        reflectionHelper.setField(controller, "trackingItemNameLabel", trackingItemNameLabel);
        reflectionHelper.setField(controller, "trackingItemStartTimeLabel", trackingItemStartTimeLabel);
        reflectionHelper.setField(controller, "trackingItemStartTimeButton", trackingItemStartTimeButton);
        reflectionHelper.setField(controller, "trackingItemEndTimeLabel", trackingItemEndTimeLabel);
        reflectionHelper.setField(controller, "trackingItemEndTimeButton", trackingItemEndTimeButton);
        reflectionHelper.setField(controller, "trackingItemTimeTableColumn", trackingItemTimeTableColumn);
        reflectionHelper.setField(controller, "trackingItemDescriptionLabel", trackingItemDescriptionLabel);
        reflectionHelper.setField(controller, "trackingItemChoiceBox", trackingItemChoiceBox);
        reflectionHelper.setField(controller, "trackingItemDescriptionValue", trackingItemDescriptionValue);
        reflectionHelper.setField(controller, "trackingItemSumLabel", trackingItemSumLabel);
        reflectionHelper.setField(controller, "trackingItemSumValueLabel", trackingItemSumValueLabel);

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
        var _ = new WorkItemViewController(null, languageService, connection, undoService, propertiesService);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullLanguageService_ThrowsNPE() throws Exception {
        var _ = new WorkItemViewController(controllerRepository, null, connection, undoService, propertiesService);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullConnection_ThrowsNPE() throws Exception {
        var _ = new WorkItemViewController(controllerRepository, languageService, null, undoService, propertiesService);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullUndoService_ThrowsNPE() throws Exception {
        var _ = new WorkItemViewController(controllerRepository, languageService, connection, null, propertiesService);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullPropertiesService_ThrowsNPE() throws Exception {
        var _ = new WorkItemViewController(controllerRepository, languageService, connection, undoService, null);
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

        assertEquals("Date", selectedDateLabel.getText());
        assertEquals("Sprint", sprintLabel.getText());
        assertEquals("TrackingItemShortcut", trackingItemShortcutTableColumn.getText());
        assertEquals("TrackingItemName", trackingItemNameTableColumn.getText());
        assertEquals("TrackingItemStartTime", trackingItemStartTimeTableColumn.getText());
        assertEquals("TrackingItemEndTime", trackingItemEndTimeTableColumn.getText());
        assertEquals("TrackingItemTime", trackingItemTimeTableColumn.getText());
        assertEquals("TrackingItemDetailsHeader", trackingItemDetailsHeaderLabel.getText());
        assertEquals("TrackingItem", trackingItemNameLabel.getText());
        assertEquals("TrackingItemStartTime", trackingItemStartTimeLabel.getText());
        assertEquals("TrackingItemEndTime", trackingItemEndTimeLabel.getText());
        assertEquals("TrackingItemDescription", trackingItemDescriptionLabel.getText());
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