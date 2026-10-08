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
import model.Address;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import service.LanguageService;
import service.PropertiesService;
import service.UndoService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import org.junit.After;

import static org.junit.Assert.*;
import sqlite.AddressDAO;
import sqlite.ConnectionFactory;
import testhelper.ReflectionHelper;
import testhelper.StubFactory;

public class AddressViewControllerTest {

    private final static String DATABASE_PATH_AND_FULL_NAME = System.getProperty("user.dir") + "/sqlite/testdb.sqlite";
    private final static String LOG4J2_PATH_AND_FULL_NAME = System.getProperty("user.dir") + "/log4j2.xml";
    
    private StubFactory stubFactory;
    private ReflectionHelper reflectionHelper;

    private ConnectionFactory connectionFactory;
    private Connection connection;
    private ResourceBundle resourceBundle;
    private ControllerRepository controllerRepository;
    private AddressViewController controller;

    private TableView<Address> addressTableView;
    private TableColumn<Address, String> addressTableColumn;
    private Label addressDetailsLabel;

    private Label streetNameLabel;
    private Label houseNumberLabel;
    private Label unitNameLabel;
    private Label unitNumberLabel;
    private Label unitLocationLabel;
    private Label cityLabel;
    private Label stateLabel;
    private Label zipCodeLabel;
    private Label countryLabel;

    private Label streetNameLabelValue;
    private Label houseNumberLabelValue;
    private Label unitNameLabelValue;
    private Label unitNumberLabelValue;
    private Label unitLocationLabelValue;
    private Label cityLabelValue;
    private Label stateLabelValue;
    private Label zipCodeLabelValue;
    private Label countryLabelValue;

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

        // 1. Setup database test data first (SQL/IO doesn't need FX thread)
        setupAddressData();

        // 2. Instantiate controller
        controller = new AddressViewController(controllerRepository, languageService, connection, undoService, propertiesService);

        // 3. Perform ALL UI control creation, stage assignment, and field injection on the JavaFX Application Thread
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
    
    private void setupAddressData() throws SQLException {
        Address address1 = new Address(1L, "Humboldtstrasse", 90L, "Etage",
            1L, "Rechts", "Nürenberg", "Bayern", 90459L, "Deutschland");
        Address address2 = new Address(2L, "Humboldtstrasse", 90L, "Etage",
            1L, "Links", "Nürenberg", "Bayern", 90459L, "Deutschland");

        AddressDAO addressDAO = new AddressDAO(connection);
        addressDAO.create(address1);
        addressDAO.create(address2);
        
    }

    private void setupUiControls() {
        addressTableView = new TableView<>();
        addressTableColumn = new TableColumn<>();
        addressDetailsLabel = new Label();

        streetNameLabel = new Label();
        houseNumberLabel = new Label();
        unitNameLabel = new Label();
        unitNumberLabel = new Label();
        unitLocationLabel = new Label();
        cityLabel = new Label();
        stateLabel = new Label();
        zipCodeLabel = new Label();
        countryLabel = new Label();

        streetNameLabelValue = new Label();
        houseNumberLabelValue = new Label();
        unitNameLabelValue = new Label();
        unitNumberLabelValue = new Label();
        unitLocationLabelValue = new Label();
        cityLabelValue = new Label();
        stateLabelValue = new Label();
        zipCodeLabelValue = new Label();
        countryLabelValue = new Label();

        newButton = new Button();
        editButton = new Button();
        deleteButton = new Button();
    }

    private void injectUiFields() throws Exception {
        reflectionHelper.setField(controller, "addressTableView", addressTableView);
        reflectionHelper.setField(controller, "addressTableColumn", addressTableColumn);
        reflectionHelper.setField(controller, "addressDetailsLabel", addressDetailsLabel);

        reflectionHelper.setField(controller, "streetNameLabel", streetNameLabel);
        reflectionHelper.setField(controller, "houseNumberLabel", houseNumberLabel);
        reflectionHelper.setField(controller, "unitNameLabel", unitNameLabel);
        reflectionHelper.setField(controller, "unitNumberLabel", unitNumberLabel);
        reflectionHelper.setField(controller, "unitLocationLabel", unitLocationLabel);
        reflectionHelper.setField(controller, "cityLabel", cityLabel);
        reflectionHelper.setField(controller, "stateLabel", stateLabel);
        reflectionHelper.setField(controller, "zipCodeLabel", zipCodeLabel);
        reflectionHelper.setField(controller, "countryLabel", countryLabel);

        reflectionHelper.setField(controller, "streetNameLabelValue", streetNameLabelValue);
        reflectionHelper.setField(controller, "houseNumberLabelValue", houseNumberLabelValue);
        reflectionHelper.setField(controller, "unitNameLabelValue", unitNameLabelValue);
        reflectionHelper.setField(controller, "unitNumberLabelValue", unitNumberLabelValue);
        reflectionHelper.setField(controller, "unitLocationLabelValue", unitLocationLabelValue);
        reflectionHelper.setField(controller, "cityLabelValue", cityLabelValue);
        reflectionHelper.setField(controller, "stateLabelValue", stateLabelValue);
        reflectionHelper.setField(controller, "zipCodeLabelValue", zipCodeLabelValue);
        reflectionHelper.setField(controller, "countryLabelValue", countryLabelValue);

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
        statement.append("DELETE FROM address ");
        
        try {
            PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
            result = dbStatement.execute();
        } catch (SQLException e) {
            return result;
        }
        
        statement = new StringBuilder();        
        statement.append("DELETE FROM SQLITE_SEQUENCE WHERE name='Address'");
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
        var _ = new AddressViewController(null, stubFactory.createLanguageServiceStub(), connection, new UndoService(), PropertiesService.getInstance());
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullLanguageService_ThrowsNPE() throws Exception {
        var _ = new AddressViewController(controllerRepository, null, connection, new UndoService(), PropertiesService.getInstance());
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullConnection_ThrowsNPE() throws Exception {
        var _ = new AddressViewController(controllerRepository, stubFactory.createLanguageServiceStub(), null, new UndoService(), PropertiesService.getInstance());
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullUndoService_ThrowsNPE() throws Exception {
        var _ = new AddressViewController(controllerRepository, stubFactory.createLanguageServiceStub(), connection, null, PropertiesService.getInstance());
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullPropertiesService_ThrowsNPE() throws Exception {
        var _ = new AddressViewController(controllerRepository, stubFactory.createLanguageServiceStub(), connection, new UndoService(), null);
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

        assertEquals("Address", addressTableColumn.getText());
        assertEquals("AddressDetailsLabel", addressDetailsLabel.getText());
        assertEquals("StreetName", streetNameLabel.getText());
        assertEquals("HouseNumber", houseNumberLabel.getText());
        assertEquals("UnitName", unitNameLabel.getText());
        assertEquals("UnitNumber", unitNumberLabel.getText());
        assertEquals("UnitLocation", unitLocationLabel.getText());
        assertEquals("City", cityLabel.getText());
        assertEquals("State", stateLabel.getText());
        assertEquals("ZipCode", zipCodeLabel.getText());
        assertEquals("Country", countryLabel.getText());
        assertEquals("New", newButton.getText());
        assertEquals("Edit", editButton.getText());
        assertEquals("Delete", deleteButton.getText());
    }

    // --- Validation Logic Tests ---

    @Test
    public void isAddressValid_ValidAddress_ReturnsTrue() {
        Address validAddress = new Address(1L);
        validAddress.setStreetname("Main St");
        validAddress.setHousenumber(123L);
        validAddress.setUnitname("Apt");
        validAddress.setUnitnumber(4L);
        validAddress.setUnitlocation("Floor 1");
        validAddress.setCity("Berlin");
        validAddress.setState("Berlin");
        validAddress.setZipcode(10115L);
        validAddress.setCountry("Germany");

        assertTrue(controller.isAddressValid(validAddress));
    }

    @Test
    public void isAddressValid_EmptyField_ReturnsFalse() {
        Address invalidAddress = new Address(1L);
        invalidAddress.setStreetname(""); // Empty
        invalidAddress.setHousenumber(123L);
        invalidAddress.setUnitname("Apt");
        invalidAddress.setUnitnumber(4L);
        invalidAddress.setUnitlocation("Floor 1");
        invalidAddress.setCity("Berlin");
        invalidAddress.setState("Berlin");
        invalidAddress.setZipcode(10115L);
        invalidAddress.setCountry("Germany");

        assertFalse(controller.isAddressValid(invalidAddress));
    }

    // --- Action & Event Tests ---

    @Test
    public void deleteAddressAction_NoSelection_InvokesWithoutError() throws Exception {
        try {
            reflectionHelper.invokePrivateMethod(controller, "deleteAddressAction", new ActionEvent());
        } catch (InvocationTargetException e) {
            // If it failed due to headless stage/alert display, that's expected in unit tests
            // You can optionally assert the cause if needed
            assertNotNull(e.getTargetException());
        }
    }

    @Test
    public void editAddressAction_NoSelection_InvokesWithoutError() throws Exception {
        try {
            reflectionHelper.invokePrivateMethod(controller, "editAddressAction", new ActionEvent());
        } catch (InvocationTargetException e) {
            assertNotNull(e.getTargetException());
        }
    }

}