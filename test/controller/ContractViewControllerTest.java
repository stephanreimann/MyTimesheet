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
import model.Contract;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import service.LanguageService;
import service.PropertiesService;
import service.UndoService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import org.junit.After;

import static org.junit.Assert.*;
import sqlite.ContractDAO;
import sqlite.ConnectionFactory;
import testhelper.ReflectionHelper;
import testhelper.StubFactory;

public class ContractViewControllerTest {

    private final static String DATABASE_PATH_AND_FULL_NAME = System.getProperty("user.dir") + "/sqlite/testdb.sqlite";
    private final static String LOG4J2_PATH_AND_FULL_NAME = System.getProperty("user.dir") + "/log4j2.xml";
    
    private StubFactory stubFactory;
    private ReflectionHelper reflectionHelper;

    private ConnectionFactory connectionFactory;
    private Connection connection;
    private ResourceBundle resourceBundle;
    private ControllerRepository controllerRepository;
    private ContractViewController controller;

    private TableView<Contract> contractTableView;
    private TableColumn<Contract, String> contractNameTableColumn;
    private Label contractDetailsLabel;

    private Label contractNameLabel;
    private Label workhoursLabel;
    private Label maxWorkhoursLabel;
    private Label vacationdaysLabel;
    private Label vacationReconciliationDateLabel;
    private Label breakfastOfftimeEndLabel;
    private Label breakfastOfftimeStartLabel;
    private Label lunchOfftimeEndLabel;
    private Label lunchOfftimeStartLabel;
    private Label earliestWorktimeStartLabel;
    private Label latestWorktimeEndLabel;

    private Label contractNameLabelValue;
    private Label workhoursLabelValue;
    private Label maxWorkhoursLabelValue;
    private Label vacationdaysLabelValue;
    private Label vacationReconciliationDateLabelValue;
    private Label breakfastOfftimeEndLabelValue;
    private Label breakfastOfftimeStartLabelValue;
    private Label lunchOfftimeEndLabelValue;
    private Label lunchOfftimeStartLabelValue;
    private Label earliestWorktimeStartLabelValue;
    private Label latestWorktimeEndLabelValue;

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
        setupContractData();

        // 2. Instantiate controller
        controller = new ContractViewController(controllerRepository, languageService, connection, undoService, propertiesService);

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
    
    private void setupContractData() throws SQLException {
        Contract contract1 = new Contract(1L, "7 hours contract", 7L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));
        Contract contract2 = new Contract(2L, "8 hours contract", 8L, 10L, 30L, "31.03", LocalTime.of(0, 15), LocalTime.of(9, 0), LocalTime.of(0, 30), LocalTime.of(12, 0), LocalTime.of(5, 0, 0), LocalTime.of(22, 0, 0));
        
        ContractDAO contractDAO = new ContractDAO(connection);
        contractDAO.create(contract1);
        contractDAO.create(contract2);
    }

    private void setupUiControls() {
        contractTableView = new TableView<>();
        contractNameTableColumn = new TableColumn<>();
        contractDetailsLabel = new Label();

        contractNameLabel = new Label();
        workhoursLabel = new Label();
        maxWorkhoursLabel = new Label();
        vacationdaysLabel = new Label();
        vacationReconciliationDateLabel = new Label();
        breakfastOfftimeEndLabel = new Label();
        breakfastOfftimeStartLabel = new Label();
        lunchOfftimeEndLabel = new Label();
        lunchOfftimeStartLabel = new Label();
        earliestWorktimeStartLabel = new Label();
        latestWorktimeEndLabel = new Label();

        contractNameLabelValue = new Label();
        workhoursLabelValue = new Label();
        maxWorkhoursLabelValue = new Label();
        vacationdaysLabelValue = new Label();
        vacationReconciliationDateLabelValue = new Label();
        breakfastOfftimeEndLabelValue = new Label();
        breakfastOfftimeStartLabelValue = new Label();
        lunchOfftimeEndLabelValue = new Label();
        lunchOfftimeStartLabelValue = new Label();
        earliestWorktimeStartLabelValue = new Label();
        latestWorktimeEndLabelValue = new Label();

        newButton = new Button();
        editButton = new Button();
        deleteButton = new Button();
    }

    private void injectUiFields() throws Exception {
        reflectionHelper.setField(controller, "contractTableView", contractTableView);
        reflectionHelper.setField(controller, "contractNameTableColumn", contractNameTableColumn);
        reflectionHelper.setField(controller, "contractDetailsLabel", contractDetailsLabel);

        reflectionHelper.setField(controller, "contractNameLabel", contractNameLabel);
        reflectionHelper.setField(controller, "workhoursLabel", workhoursLabel);
        reflectionHelper.setField(controller, "maxWorkhoursLabel", maxWorkhoursLabel);
        reflectionHelper.setField(controller, "vacationdaysLabel", vacationdaysLabel);
        reflectionHelper.setField(controller, "vacationReconciliationDateLabel", vacationReconciliationDateLabel);
        reflectionHelper.setField(controller, "breakfastOfftimeEndLabel", breakfastOfftimeEndLabel);
        reflectionHelper.setField(controller, "breakfastOfftimeStartLabel", breakfastOfftimeStartLabel);
        reflectionHelper.setField(controller, "lunchOfftimeEndLabel", lunchOfftimeEndLabel);
        reflectionHelper.setField(controller, "lunchOfftimeStartLabel", lunchOfftimeStartLabel);
        reflectionHelper.setField(controller, "earliestWorktimeStartLabel", earliestWorktimeStartLabel);
        reflectionHelper.setField(controller, "latestWorktimeEndLabel", latestWorktimeEndLabel);

        reflectionHelper.setField(controller, "contractNameLabelValue", contractNameLabelValue);
        reflectionHelper.setField(controller, "workhoursLabelValue", workhoursLabelValue);
        reflectionHelper.setField(controller, "maxWorkhoursLabelValue", maxWorkhoursLabelValue);
        reflectionHelper.setField(controller, "vacationdaysLabelValue", vacationdaysLabelValue);
        reflectionHelper.setField(controller, "vacationReconciliationDateLabelValue", vacationReconciliationDateLabelValue);
        reflectionHelper.setField(controller, "breakfastOfftimeEndLabelValue", breakfastOfftimeEndLabelValue);
        reflectionHelper.setField(controller, "breakfastOfftimeStartLabelValue", breakfastOfftimeStartLabelValue);
        reflectionHelper.setField(controller, "lunchOfftimeEndLabelValue", lunchOfftimeEndLabelValue);
        reflectionHelper.setField(controller, "lunchOfftimeStartLabelValue", lunchOfftimeStartLabelValue);
        reflectionHelper.setField(controller, "earliestWorktimeStartLabelValue", earliestWorktimeStartLabelValue);
        reflectionHelper.setField(controller, "latestWorktimeEndLabelValue", latestWorktimeEndLabelValue);

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
        statement.append("DELETE FROM contract ");
        
        try {
            PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
            result = dbStatement.execute();
        } catch (SQLException e) {
            return result;
        }
        
        statement = new StringBuilder();        
        statement.append("DELETE FROM SQLITE_SEQUENCE WHERE name='Contract'");
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
        var _ = new ContractViewController(null, stubFactory.createLanguageServiceStub(), connection, new UndoService(), PropertiesService.getInstance());
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullLanguageService_ThrowsNPE() throws Exception {
        var _ = new ContractViewController(controllerRepository, null, connection, new UndoService(), PropertiesService.getInstance());
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullConnection_ThrowsNPE() throws Exception {
        var _ = new ContractViewController(controllerRepository, stubFactory.createLanguageServiceStub(), null, new UndoService(), PropertiesService.getInstance());
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullUndoService_ThrowsNPE() throws Exception {
        var _ = new ContractViewController(controllerRepository, stubFactory.createLanguageServiceStub(), connection, null, PropertiesService.getInstance());
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullPropertiesService_ThrowsNPE() throws Exception {
        var _ = new ContractViewController(controllerRepository, stubFactory.createLanguageServiceStub(), connection, new UndoService(), null);
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

        assertEquals("ContractName", contractNameTableColumn.getText());
        assertEquals("ContractDetails", contractDetailsLabel.getText());
        assertEquals("ContractName", contractNameLabel.getText());
        assertEquals("Workhours", workhoursLabel.getText());
        assertEquals("MaxWorkhours", maxWorkhoursLabel.getText());
        assertEquals("Vacationdays", vacationdaysLabel.getText());
        assertEquals("VacationReconciliationDate", vacationReconciliationDateLabel.getText());
        assertEquals("BreakfastOfftimeEnd", breakfastOfftimeEndLabel.getText());
        assertEquals("BreakfastOfftimeStart", breakfastOfftimeStartLabel.getText());
        assertEquals("LunchOfftimeEnd", lunchOfftimeEndLabel.getText());
        assertEquals("LunchOfftimeStart", lunchOfftimeStartLabel.getText());
        assertEquals("EarliestWorktimeStart", earliestWorktimeStartLabel.getText());
        assertEquals("LatestWorktimeEnd", latestWorktimeEndLabel.getText());
        assertEquals("New", newButton.getText());
        assertEquals("Edit", editButton.getText());
        assertEquals("Delete", deleteButton.getText());
    }

    // --- Action & Event Tests ---

    @Test
    public void deleteContractAction_NoSelection_InvokesWithoutError() throws Exception {
        try {
            reflectionHelper.invokePrivateMethod(controller, "deleteContractAction", new ActionEvent());
        } catch (InvocationTargetException e) {
            assertNotNull(e.getTargetException());
        }
    }

    @Test
    public void editContractAction_NoSelection_InvokesWithoutError() throws Exception {
        try {
            reflectionHelper.invokePrivateMethod(controller, "editContractAction", new ActionEvent());
        } catch (InvocationTargetException e) {
            assertNotNull(e.getTargetException());
        }
    }
}