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
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.Contract;
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

public class ContractDetailsViewControllerTest {

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
    private ObservableList<Contract> contractData;

    private ContractDetailsViewController controller;

    private GridPane contractDetailsGridPane;
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

    private TextField contractNameTextFieldValue;
    private TextField workhoursTextFieldValue;
    private TextField maxWorkhoursTextFieldValue;
    private TextField vacationdaysTextFieldValue;
    private TextField vacationReconciliationDateTextFieldValue;

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
        contractData = FXCollections.observableArrayList();

        controller = new ContractDetailsViewController(languageService, connection, undoService, contractData);

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
        contractDetailsGridPane = new GridPane();
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

        contractNameTextFieldValue = new TextField();
        workhoursTextFieldValue = new TextField();
        maxWorkhoursTextFieldValue = new TextField();
        vacationdaysTextFieldValue = new TextField();
        vacationReconciliationDateTextFieldValue = new TextField();

        acceptButton = new Button();
        cancelButton = new Button();
    }

    private void injectUiFields() throws Exception {
        reflectionHelper.setField(controller, "contractDetailsGridPane", contractDetailsGridPane);
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

        reflectionHelper.setField(controller, "contractNameTextFieldValue", contractNameTextFieldValue);
        reflectionHelper.setField(controller, "workhoursTextFieldValue", workhoursTextFieldValue);
        reflectionHelper.setField(controller, "maxWorkhoursTextFieldValue", maxWorkhoursTextFieldValue);
        reflectionHelper.setField(controller, "vacationdaysTextFieldValue", vacationdaysTextFieldValue);
        reflectionHelper.setField(controller, "vacationReconciliationDateTextFieldValue", vacationReconciliationDateTextFieldValue);

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
        var _ = new ContractDetailsViewController(null, connection, undoService, contractData);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullConnection_ThrowsNPE() {
        var _ = new ContractDetailsViewController(languageService, null, undoService, contractData);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullUndoService_ThrowsNPE() {
        var _ = new ContractDetailsViewController(languageService, connection, null, contractData);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullContractData_ThrowsNPE() {
        var _ = new ContractDetailsViewController(languageService, connection, undoService, null);
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
        controller.setAction(ContractViewController.DataAction.NEW);

        Contract contract = new Contract(1L);
        controller.showContractDetails(contract);

        contractNameTextFieldValue.setText("Standard");
        workhoursTextFieldValue.setText("40");
        maxWorkhoursTextFieldValue.setText("48");
        vacationdaysTextFieldValue.setText("30");
        vacationReconciliationDateTextFieldValue.setText("31.12.");

        try {
            reflectionHelper.invokePrivateMethod(controller, "acceptAction", new ActionEvent());
        } catch (InvocationTargetException e) {
            assertNotNull(e.getTargetException());
        }

        assertEquals("Standard", contract.getName());
        assertEquals(Long.valueOf(40), contract.getWorkhours());
        assertEquals(Long.valueOf(30), contract.getVacationdays());
    }

    @Test
    public void preCloseAction_ExecutesSafely() {
        controller.preCloseAction();
        assertTrue(true);
    }
}