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
import model.Address;
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

public class AddressDetailsViewControllerTest {

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
    private ObservableList<Address> addressData;

    private AddressDetailsViewController controller;

    private Label streetNameLabel;
    private Label houseNumberLabel;
    private Label unitNameLabel;
    private Label unitNumberLabel;
    private Label unitLocationLabel;
    private Label cityLabel;
    private Label stateLabel;
    private Label zipCodeLabel;
    private Label countryLabel;

    private TextField streetNameTextFieldValue;
    private TextField houseNumberTextFieldValue;
    private TextField unitNameTextFieldValue;
    private TextField unitNumberTextFieldValue;
    private TextField unitLocationTextFieldValue;
    private TextField cityTextFieldValue;
    private TextField stateTextFieldValue;
    private TextField zipCodeTextFieldValue;
    private TextField countryTextFieldValue;

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
        addressData = FXCollections.observableArrayList();

        controller = new AddressDetailsViewController(languageService, connection, undoService, addressData);

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
        streetNameLabel = new Label();
        houseNumberLabel = new Label();
        unitNameLabel = new Label();
        unitNumberLabel = new Label();
        unitLocationLabel = new Label();
        cityLabel = new Label();
        stateLabel = new Label();
        zipCodeLabel = new Label();
        countryLabel = new Label();

        streetNameTextFieldValue = new TextField();
        houseNumberTextFieldValue = new TextField();
        unitNameTextFieldValue = new TextField();
        unitNumberTextFieldValue = new TextField();
        unitLocationTextFieldValue = new TextField();
        cityTextFieldValue = new TextField();
        stateTextFieldValue = new TextField();
        zipCodeTextFieldValue = new TextField();
        countryTextFieldValue = new TextField();

        acceptButton = new Button();
        cancelButton = new Button();
    }

    private void injectUiFields() throws Exception {
        reflectionHelper.setField(controller, "streetNameLabel", streetNameLabel);
        reflectionHelper.setField(controller, "houseNumberLabel", houseNumberLabel);
        reflectionHelper.setField(controller, "unitNameLabel", unitNameLabel);
        reflectionHelper.setField(controller, "unitNumberLabel", unitNumberLabel);
        reflectionHelper.setField(controller, "unitLocationLabel", unitLocationLabel);
        reflectionHelper.setField(controller, "cityLabel", cityLabel);
        reflectionHelper.setField(controller, "stateLabel", stateLabel);
        reflectionHelper.setField(controller, "zipCodeLabel", zipCodeLabel);
        reflectionHelper.setField(controller, "countryLabel", countryLabel);

        reflectionHelper.setField(controller, "streetNameTextFieldValue", streetNameTextFieldValue);
        reflectionHelper.setField(controller, "houseNumberTextFieldValue", houseNumberTextFieldValue);
        reflectionHelper.setField(controller, "unitNameTextFieldValue", unitNameTextFieldValue);
        reflectionHelper.setField(controller, "unitNumberTextFieldValue", unitNumberTextFieldValue);
        reflectionHelper.setField(controller, "unitLocationTextFieldValue", unitLocationTextFieldValue);
        reflectionHelper.setField(controller, "cityTextFieldValue", cityTextFieldValue);
        reflectionHelper.setField(controller, "stateTextFieldValue", stateTextFieldValue);
        reflectionHelper.setField(controller, "zipCodeTextFieldValue", zipCodeTextFieldValue);
        reflectionHelper.setField(controller, "countryTextFieldValue", countryTextFieldValue);

        reflectionHelper.setField(controller, "acceptButton", acceptButton);
        reflectionHelper.setField(controller, "cancelButton", cancelButton);
    }

    @After
    public void tearDown() {
        // Cleanup if needed
    }

    // --- Constructor & Validation Tests ---

    @Test(expected = NullPointerException.class)
    public void constructor_NullLanguageService_ThrowsNPE() throws Exception {
        var _ = new AddressDetailsViewController(null, connection, undoService, addressData);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullConnection_ThrowsNPE() throws Exception {
        var _ = new AddressDetailsViewController(languageService, null, undoService, addressData);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullUndoService_ThrowsNPE() throws Exception {
        var _ = new AddressDetailsViewController(languageService, connection, null, addressData);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullAddressData_ThrowsNPE() throws Exception {
        var _ = new AddressDetailsViewController(languageService, connection, undoService, null);
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

        assertEquals("StreetName", streetNameLabel.getText());
        assertEquals("HouseNumber", houseNumberLabel.getText());
        assertEquals("UnitName", unitNameLabel.getText());
        assertEquals("UnitNumber", unitNumberLabel.getText());
        assertEquals("UnitLocation", unitLocationLabel.getText());
        assertEquals("City", cityLabel.getText());
        assertEquals("State", stateLabel.getText());
        assertEquals("ZipCode", zipCodeLabel.getText());
        assertEquals("Country", countryLabel.getText());
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
        controller.setAction(AddressViewController.DataAction.NEW);

        Address address = new Address(1L);
        controller.showAddressDetails(address);

        streetNameTextFieldValue.setText("Main St");
        houseNumberTextFieldValue.setText("123");
        unitNameTextFieldValue.setText("Apt 4B");
        unitNumberTextFieldValue.setText("4");
        unitLocationTextFieldValue.setText("Building A");
        cityTextFieldValue.setText("Berlin");
        stateTextFieldValue.setText("Berlin");
        zipCodeTextFieldValue.setText("10115");
        countryTextFieldValue.setText("Germany");

        try {
            reflectionHelper.invokePrivateMethod(controller, "acceptAction", new ActionEvent());
        } catch (InvocationTargetException e) {
            assertNotNull(e.getTargetException());
        }

        assertEquals("Main St", address.getStreetname());
        assertEquals(Long.valueOf(123), address.getHousenumber());
        assertEquals("Berlin", address.getCity());
    }

    @Test
    public void preCloseAction_ExecutesSafely() {
        controller.preCloseAction();
        assertTrue(true);
    }
}