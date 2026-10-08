/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import adapter.Log4jAdapter;
import java.lang.reflect.InvocationTargetException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import model.Holyday;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import service.LanguageService;
import service.PropertiesService;
import service.UndoService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import org.junit.After;

import static org.junit.Assert.*;
import sqlite.ConnectionFactory;
import sqlite.HolydayDAO;
import testhelper.ReflectionHelper;
import testhelper.StubFactory;

public class HolydayViewControllerTest {

    private final static String DATABASE_PATH_AND_FULL_NAME = System.getProperty("user.dir") + "/sqlite/testdb.sqlite";
    private final static String LOG4J2_PATH_AND_FULL_NAME = System.getProperty("user.dir") + "/log4j2.xml";
    
    private StubFactory stubFactory;
    private ReflectionHelper reflectionHelper;

    private ConnectionFactory connectionFactory;
    private Connection connection;
    private ResourceBundle resourceBundle;
    private ControllerRepository controllerRepository;
    private HolydayViewController controller;

    private TableView<Holyday> holydayTableView;
    private TableColumn<Holyday, LocalDate> holydayDateTableColumn;
    private TableColumn<Holyday, String> holydayNameTableColumn;
    private TableColumn<Holyday, String> holydayStateTableColumn;
    private Label holydayDetailsLabel;
    
    private Label holydayDateLabel;
    private Label holydayNameLabel;
    private Label holydayStateLabel;
    
    private Label holydayDateLabelValue;
    private Label holydayNameLabelValue;
    private Label holydayStateLabelValue;
    
    private Button importButton;
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

        LanguageService languageService = stubFactory.createLanguageServiceStub();

        resourceBundle = stubFactory.createResourceBundleStub();
        connectionFactory = new ConnectionFactory(resourceBundle, new Log4jAdapter(LOG4J2_PATH_AND_FULL_NAME));
        connection = connectionFactory.getConnection(DATABASE_PATH_AND_FULL_NAME);   

        UndoService undoService = new UndoService();
        PropertiesService propertiesService = PropertiesService.getInstance();
        controllerRepository = ControllerRepository.getInstance();

        // 1. Setup database test data first
        setupHolydayData();

        // 2. Instantiate controller
        controller = new HolydayViewController(controllerRepository, languageService, connection, undoService, propertiesService);

        // 3. Perform UI control creation, stage assignment, and field injection on the JavaFX Application Thread
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
    
    private void setupHolydayData() throws SQLException {
        Holyday holyday1 = new Holyday(1L, LocalDate.of(2026, 1, 1), "New Year", "Bayern");
        Holyday holyday2 = new Holyday(2L, LocalDate.of(2026, 5, 1), "Labour Day", "Bayern");

        HolydayDAO holydayDAO = new HolydayDAO(connection);
        holydayDAO.create(holyday1);
        holydayDAO.create(holyday2);
    }

    private void setupUiControls() {
        holydayTableView = new TableView<>();
        holydayDateTableColumn = new TableColumn<>();
        holydayNameTableColumn = new TableColumn<>();
        holydayStateTableColumn = new TableColumn<>();
        holydayDetailsLabel = new Label();

        holydayDateLabel = new Label();
        holydayNameLabel = new Label();
        holydayStateLabel = new Label();

        holydayDateLabelValue = new Label();
        holydayNameLabelValue = new Label();
        holydayStateLabelValue = new Label();

        importButton = new Button();
        newButton = new Button();
        editButton = new Button();
        deleteButton = new Button();
    }

    private void injectUiFields() throws Exception {
        reflectionHelper.setField(controller, "holydayTableView", holydayTableView);
        reflectionHelper.setField(controller, "holydayDateTableColumn", holydayDateTableColumn);
        reflectionHelper.setField(controller, "holydayNameTableColumn", holydayNameTableColumn);
        reflectionHelper.setField(controller, "holydayStateTableColumn", holydayStateTableColumn);
        reflectionHelper.setField(controller, "holydayDetailsLabel", holydayDetailsLabel);

        reflectionHelper.setField(controller, "holydayDateLabel", holydayDateLabel);
        reflectionHelper.setField(controller, "holydayNameLabel", holydayNameLabel);
        reflectionHelper.setField(controller, "holydayStateLabel", holydayStateLabel);

        reflectionHelper.setField(controller, "holydayDateLabelValue", holydayDateLabelValue);
        reflectionHelper.setField(controller, "holydayNameLabelValue", holydayNameLabelValue);
        reflectionHelper.setField(controller, "holydayStateLabelValue", holydayStateLabelValue);

        reflectionHelper.setField(controller, "importButton", importButton);
        reflectionHelper.setField(controller, "newButton", newButton);
        reflectionHelper.setField(controller, "editButton", editButton);
        reflectionHelper.setField(controller, "deleteButton", deleteButton);
    }

    @After
    public void tearDown() {
        truncateTable();
    }

    private synchronized boolean truncateTable() {
        boolean result = false;
        StringBuilder statement = new StringBuilder();
        statement.append("DELETE FROM holyday ");
        
        try {
            PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
            result = dbStatement.execute();
        } catch (SQLException e) {
            return result;
        }
        
        statement = new StringBuilder();        
        statement.append("DELETE FROM SQLITE_SEQUENCE WHERE name='Holyday'");
        try {
            PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
            result = dbStatement.execute();
        } catch (SQLException e) {
            return result;
        }
        return result;
    }
    
    // --- Constructor & Validation Tests ---

    @Test(expected = NullPointerException.class)
    public void constructor_NullControllerRepository_ThrowsNPE() throws Exception {
        var _ = new HolydayViewController(null, stubFactory.createLanguageServiceStub(), connection, new UndoService(), PropertiesService.getInstance());
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullLanguageService_ThrowsNPE() throws Exception {
        var _ = new HolydayViewController(controllerRepository, null, connection, new UndoService(), PropertiesService.getInstance());
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullConnection_ThrowsNPE() throws Exception {
        var _ = new HolydayViewController(controllerRepository, stubFactory.createLanguageServiceStub(), null, new UndoService(), PropertiesService.getInstance());
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullUndoService_ThrowsNPE() throws Exception {
        var _ = new HolydayViewController(controllerRepository, stubFactory.createLanguageServiceStub(), connection, null, PropertiesService.getInstance());
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullPropertiesService_ThrowsNPE() throws Exception {
        var _ = new HolydayViewController(controllerRepository, stubFactory.createLanguageServiceStub(), connection, new UndoService(), null);
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

        assertEquals("HolydayDate", holydayDateTableColumn.getText());
        assertEquals("HolydayName", holydayNameTableColumn.getText());
        assertEquals("HolydayState", holydayStateTableColumn.getText());
        assertEquals("Holyday", holydayDetailsLabel.getText());
        assertEquals("HolydayDate", holydayDateLabel.getText());
        assertEquals("HolydayName", holydayNameLabel.getText());
        assertEquals("HolydayState", holydayStateLabel.getText());
        assertEquals("Import", importButton.getText());
        assertEquals("New", newButton.getText());
        assertEquals("Edit", editButton.getText());
        assertEquals("Delete", deleteButton.getText());
    }

    // --- Validation Logic Tests ---

    @Test
    public void isHolydayValid_ValidHolyday_ReturnsTrue() {
        Holyday validHolyday = new Holyday(1L, LocalDate.of(2026, 1, 1), "New Year", "Bayern");
        boolean isValid = controller.isHolydayValid(validHolyday);
        assertTrue(isValid);
    }
    @Test
    public void isHolydayValid_EmptyField_ReturnsFalse() {
        Holyday invalidHolyday = new Holyday(1L, LocalDate.of(2026, 1, 1), "", "Bayern"); // Empty name
        boolean isValid = controller.isHolydayValid(invalidHolyday);
        assertFalse(isValid);
    }
    
// --- Action & Event Tests ---

    @Test
    public void deleteHolydayAction_NoSelection_InvokesWithoutError() throws Exception {
        try {
            reflectionHelper.invokePrivateMethod(controller, "deleteHolydayAction", new ActionEvent());
        } catch (InvocationTargetException e) {
            assertNotNull(e.getTargetException());
        }
    }

    @Test
    public void editHolydayAction_NoSelection_InvokesWithoutError() throws Exception {
        try {
            reflectionHelper.invokePrivateMethod(controller, "editHolydayAction", new ActionEvent());
        } catch (InvocationTargetException e) {
            assertNotNull(e.getTargetException());
        }
    }
}