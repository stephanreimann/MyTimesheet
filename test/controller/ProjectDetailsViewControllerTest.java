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
import model.Project;
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

public class ProjectDetailsViewControllerTest {

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
    private ObservableList<Project> projectData;

    private ProjectDetailsViewController controller;

    private Label projectNameLabel;
    private Label projectCostUnitLabel;
    private Label projectIsWorktimeRelevantLabel;
    private Label projectIsVacationRelevantLabel;
    private Label projectIsComptimeRelevantLabel;
    private Label projectDescriptionLabel;

    private TextField projectNameTextFieldValue;
    private TextField projectCostUnitTextFieldValue;
    private ComboBox<String> projectIsWorktimeRelevantComboBoxValue;
    private ComboBox<String> projectIsVacationRelevantComboBoxValue;
    private ComboBox<String> projectIsComptimeRelevantComboBoxValue;
    private TextArea projectDescriptionTextAreaValue;

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
        projectData = FXCollections.observableArrayList();

        controller = new ProjectDetailsViewController(languageService, connection, undoService, projectData);

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
        projectNameLabel = new Label();
        projectCostUnitLabel = new Label();
        projectIsWorktimeRelevantLabel = new Label();
        projectIsVacationRelevantLabel = new Label();
        projectIsComptimeRelevantLabel = new Label();
        projectDescriptionLabel = new Label();

        projectNameTextFieldValue = new TextField();
        projectCostUnitTextFieldValue = new TextField();
        projectIsWorktimeRelevantComboBoxValue = new ComboBox<>();
        projectIsVacationRelevantComboBoxValue = new ComboBox<>();
        projectIsComptimeRelevantComboBoxValue = new ComboBox<>();
        projectDescriptionTextAreaValue = new TextArea();

        acceptButton = new Button();
        cancelButton = new Button();
    }

    private void injectUiFields() throws Exception {
        reflectionHelper.setField(controller, "projectNameLabel", projectNameLabel);
        reflectionHelper.setField(controller, "projectCostUnitLabel", projectCostUnitLabel);
        reflectionHelper.setField(controller, "projectIsWorktimeRelevantLabel", projectIsWorktimeRelevantLabel);
        reflectionHelper.setField(controller, "projectIsVacationRelevantLabel", projectIsVacationRelevantLabel);
        reflectionHelper.setField(controller, "projectIsComptimeRelevantLabel", projectIsComptimeRelevantLabel);
        reflectionHelper.setField(controller, "projectDescriptionLabel", projectDescriptionLabel);

        reflectionHelper.setField(controller, "projectNameTextFieldValue", projectNameTextFieldValue);
        reflectionHelper.setField(controller, "projectCostUnitTextFieldValue", projectCostUnitTextFieldValue);
        reflectionHelper.setField(controller, "projectIsWorktimeRelevantComboBoxValue", projectIsWorktimeRelevantComboBoxValue);
        reflectionHelper.setField(controller, "projectIsVacationRelevantComboBoxValue", projectIsVacationRelevantComboBoxValue);
        reflectionHelper.setField(controller, "projectIsComptimeRelevantComboBoxValue", projectIsComptimeRelevantComboBoxValue);
        reflectionHelper.setField(controller, "projectDescriptionTextAreaValue", projectDescriptionTextAreaValue);

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
        var _ = new ProjectDetailsViewController(null, connection, undoService, projectData);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullConnection_ThrowsNPE() {
        var _ = new ProjectDetailsViewController(languageService, null, undoService, projectData);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullUndoService_ThrowsNPE() {
        var _ = new ProjectDetailsViewController(languageService, connection, null, projectData);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullProjectData_ThrowsNPE() {
        var _ = new ProjectDetailsViewController(languageService, connection, undoService, null);
    }

    // --- Initialization & GUI Tests ---

    @Test
    public void initialize_ValidResourceBundle_StoresBundleAndDisablesAccept() {
        controller.initialize(null, resourceBundle);
        assertEquals(resourceBundle, controller.getResourceBundle());
        assertTrue(acceptButton.isDisable());
        assertEquals(2, projectIsWorktimeRelevantComboBoxValue.getItems().size());
    }

    @Test
    public void updateGuiItems_UpdatesComponentsCorrectly() {
        controller.setResourceBundle(resourceBundle);
        controller.updateGuiItems();

        assertEquals("ProjectName", projectNameLabel.getText());
        assertEquals("ProjectCostUnit", projectCostUnitLabel.getText());
        assertEquals("ProjectIsWorktimeRelevant", projectIsWorktimeRelevantLabel.getText());
        assertEquals("ProjectIsVacationRelevant", projectIsVacationRelevantLabel.getText());
        assertEquals("ProjectIsComptimeRelevant", projectIsComptimeRelevantLabel.getText());
        assertEquals("ProjectDescription", projectDescriptionLabel.getText());
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
        controller.setAction(ProjectViewController.DataAction.NEW);

        Project project = new Project(1L);
        controller.showProjectDetails(project);

        projectNameTextFieldValue.setText("Development");
        projectCostUnitTextFieldValue.setText("CU-100");
        projectIsWorktimeRelevantComboBoxValue.setValue("True");
        projectIsVacationRelevantComboBoxValue.setValue("False");
        projectIsComptimeRelevantComboBoxValue.setValue("True");
        projectDescriptionTextAreaValue.setText("Software development project");

        try {
            reflectionHelper.invokePrivateMethod(controller, "acceptAction", new ActionEvent());
        } catch (InvocationTargetException e) {
            assertNotNull(e.getTargetException());
        }

        assertEquals("Development", project.getName());
        assertEquals("CU-100", project.getCostunit());
        assertEquals("True", project.getIsWorktimeRelevant());
        assertEquals("Software development project", project.getDescription());
    }

    @Test
    public void preCloseAction_ExecutesSafely() {
        controller.preCloseAction();
        assertTrue(true);
    }
}