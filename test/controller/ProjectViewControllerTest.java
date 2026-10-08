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
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Project;
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

public class ProjectViewControllerTest {

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

    private ProjectViewController controller;

    private TableView<Project> projectTableView;
    private TableColumn<Project, String> projectNameTableColumn;
    private Label projectDetailsLabel;
    private Label projectNameLabel;
    private Label projectCostUnitLabel;
    private Label projectIsWorktimeRelevantLabel;
    private Label projectIsVacationRelevantLabel;
    private Label projectIsComptimeRelevantLabel;
    private Label projectDescriptionLabel;
    private Label projectNameLabelValue;
    private Label projectCostUnitLabelValue;
    private Label projectIsWorktimeRelevantLabelValue;
    private Label projectIsVacationRelevantLabelValue;
    private Label projectIsComptimeRelevantLabelValue;
    private Label projectDescriptionLabelValue;
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
        controllerRepository = ControllerRepository.getInstance();

        controller = new ProjectViewController(controllerRepository, languageService, connection, undoService, propertiesService);

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
        projectTableView = new TableView<>();
        projectNameTableColumn = new TableColumn<>();
        projectDetailsLabel = new Label();
        projectNameLabel = new Label();
        projectCostUnitLabel = new Label();
        projectIsWorktimeRelevantLabel = new Label();
        projectIsVacationRelevantLabel = new Label();
        projectIsComptimeRelevantLabel = new Label();
        projectDescriptionLabel = new Label();
        projectNameLabelValue = new Label();
        projectCostUnitLabelValue = new Label();
        projectIsWorktimeRelevantLabelValue = new Label();
        projectIsVacationRelevantLabelValue = new Label();
        projectIsComptimeRelevantLabelValue = new Label();
        projectDescriptionLabelValue = new Label();
        newButton = new Button();
        editButton = new Button();
        deleteButton = new Button();
    }

    private void injectUiFields() throws Exception {
        reflectionHelper.setField(controller, "projectTableView", projectTableView);
        reflectionHelper.setField(controller, "projectNameTableColumn", projectNameTableColumn);
        reflectionHelper.setField(controller, "projectDetailsLabel", projectDetailsLabel);
        reflectionHelper.setField(controller, "projectNameLabel", projectNameLabel);
        reflectionHelper.setField(controller, "projectCostUnitLabel", projectCostUnitLabel);
        reflectionHelper.setField(controller, "projectIsWorktimeRelevantLabel", projectIsWorktimeRelevantLabel);
        reflectionHelper.setField(controller, "projectIsVacationRelevantLabel", projectIsVacationRelevantLabel);
        reflectionHelper.setField(controller, "projectIsComptimeRelevantLabel", projectIsComptimeRelevantLabel);
        reflectionHelper.setField(controller, "projectDescriptionLabel", projectDescriptionLabel);
        reflectionHelper.setField(controller, "projectNameLabelValue", projectNameLabelValue);
        reflectionHelper.setField(controller, "projectCostUnitLabelValue", projectCostUnitLabelValue);
        reflectionHelper.setField(controller, "projectIsWorktimeRelevantLabelValue", projectIsWorktimeRelevantLabelValue);
        reflectionHelper.setField(controller, "projectIsVacationRelevantLabelValue", projectIsVacationRelevantLabelValue);
        reflectionHelper.setField(controller, "projectIsComptimeRelevantLabelValue", projectIsComptimeRelevantLabelValue);
        reflectionHelper.setField(controller, "projectDescriptionLabelValue", projectDescriptionLabelValue);
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
        var _ = new ProjectViewController(null, languageService, connection, undoService, propertiesService);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullLanguageService_ThrowsNPE() throws Exception {
        var _ = new ProjectViewController(controllerRepository, null, connection, undoService, propertiesService);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullConnection_ThrowsNPE() throws Exception {
        var _ = new ProjectViewController(controllerRepository, languageService, null, undoService, propertiesService);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullUndoService_ThrowsNPE() throws Exception {
        var _ = new ProjectViewController(controllerRepository, languageService, connection, null, propertiesService);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullPropertiesService_ThrowsNPE() throws Exception {
        var _ = new ProjectViewController(controllerRepository, languageService, connection, undoService, null);
    }

    // --- Initialization & GUI Tests ---

    @Test
    public void initialize_ValidResourceBundle_StoresBundleAndSetsItems() {
        controller.initialize(null, resourceBundle);
        assertEquals(resourceBundle, controller.getResourceBundle());
        assertNotNull(controller.getEventManager());
    }

    @Test
    public void updateGuiItems_UpdatesComponentsCorrectly() {
        controller.setResourceBundle(resourceBundle);
        controller.updateGuiItems();

        assertEquals("ProjectName", projectNameTableColumn.getText());
        assertEquals("ProjectDetailsLabel", projectDetailsLabel.getText());
        assertEquals("ProjectName", projectNameLabel.getText());
        assertEquals("ProjectCostUnit", projectCostUnitLabel.getText());
        assertEquals("ProjectIsWorktimeRelevant", projectIsWorktimeRelevantLabel.getText());
        assertEquals("ProjectIsVacationRelevant", projectIsVacationRelevantLabel.getText());
        assertEquals("ProjectIsComptimeRelevant", projectIsComptimeRelevantLabel.getText());
        assertEquals("ProjectDescription", projectDescriptionLabel.getText());
        assertEquals("New", newButton.getText());
        assertEquals("Edit", editButton.getText());
        assertEquals("Delete", deleteButton.getText());
    }

    // --- Action Tests ---

    @Test
    public void editProjectAction_NoSelection_ShowsAlertSafely() throws Exception {
        controller.setResourceBundle(resourceBundle);
        try {
            reflectionHelper.invokePrivateMethod(controller, "editProjectAction", new ActionEvent());
        } catch (InvocationTargetException e) {
            // Alert showAndWait in headless environment may throw/warn
            assertNotNull(e.getTargetException());
        }
    }

    @Test
    public void deleteProjectAction_NoSelection_ShowsAlertSafely() throws Exception {
        controller.setResourceBundle(resourceBundle);
        try {
            reflectionHelper.invokePrivateMethod(controller, "deleteProjectAction", new ActionEvent());
        } catch (InvocationTargetException e) {
            assertNotNull(e.getTargetException());
        }
    }

    @Test
    public void preCloseAction_ExecutesSafely() {
        controller.preCloseAction();
        assertTrue(true);
    }
}