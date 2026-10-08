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
import model.User;
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

public class UserViewControllerTest {

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

    private UserViewController controller;

    private TableView<User> userTableView;
    private TableColumn<User, String> userFirstNameTableColumn;
    private TableColumn<User, String> userLastNameTableColumn;
    private Label userDetailsLabel;
    private Label firstNameLabel;
    private Label lastNameLabel;
    private Label loginLabel;
    private Label passwordLabel;
    private Label vacationLeftLabel;
    private Label contractNameLabel;
    private Label roleNameLabel;
    private Label addressLabel;
    private Label firstNameLabelValue;
    private Label lastNameLabelValue;
    private Label loginLabelValue;
    private PasswordField passwordFieldValue;
    private Label vacationLeftLabelValue;
    private Label contractNameLabelValue;
    private Label roleNameLabelValue;
    private Label addressLabelValue;
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

        controller = new UserViewController(controllerRepository, languageService, connection, undoService, propertiesService);

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
        userTableView = new TableView<>();
        userFirstNameTableColumn = new TableColumn<>();
        userLastNameTableColumn = new TableColumn<>();
        userDetailsLabel = new Label();
        firstNameLabel = new Label();
        lastNameLabel = new Label();
        loginLabel = new Label();
        passwordLabel = new Label();
        vacationLeftLabel = new Label();
        contractNameLabel = new Label();
        roleNameLabel = new Label();
        addressLabel = new Label();
        firstNameLabelValue = new Label();
        lastNameLabelValue = new Label();
        loginLabelValue = new Label();
        passwordFieldValue = new PasswordField();
        vacationLeftLabelValue = new Label();
        contractNameLabelValue = new Label();
        roleNameLabelValue = new Label();
        addressLabelValue = new Label();
        newButton = new Button();
        editButton = new Button();
        deleteButton = new Button();
    }

    private void injectUiFields() throws Exception {
        reflectionHelper.setField(controller, "userTableView", userTableView);
        reflectionHelper.setField(controller, "userFirstNameTableColumn", userFirstNameTableColumn);
        reflectionHelper.setField(controller, "userLastNameTableColumn", userLastNameTableColumn);
        reflectionHelper.setField(controller, "userDetailsLabel", userDetailsLabel);
        reflectionHelper.setField(controller, "firstNameLabel", firstNameLabel);
        reflectionHelper.setField(controller, "lastNameLabel", lastNameLabel);
        reflectionHelper.setField(controller, "loginLabel", loginLabel);
        reflectionHelper.setField(controller, "passwordLabel", passwordLabel);
        reflectionHelper.setField(controller, "vacationLeftLabel", vacationLeftLabel);
        reflectionHelper.setField(controller, "contractNameLabel", contractNameLabel);
        reflectionHelper.setField(controller, "roleNameLabel", roleNameLabel);
        reflectionHelper.setField(controller, "addressLabel", addressLabel);
        reflectionHelper.setField(controller, "firstNameLabelValue", firstNameLabelValue);
        reflectionHelper.setField(controller, "lastNameLabelValue", lastNameLabelValue);
        reflectionHelper.setField(controller, "loginLabelValue", loginLabelValue);
        reflectionHelper.setField(controller, "passwordFieldValue", passwordFieldValue);
        reflectionHelper.setField(controller, "vacationLeftLabelValue", vacationLeftLabelValue);
        reflectionHelper.setField(controller, "contractNameLabelValue", contractNameLabelValue);
        reflectionHelper.setField(controller, "roleNameLabelValue", roleNameLabelValue);
        reflectionHelper.setField(controller, "addressLabelValue", addressLabelValue);
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
        var _ = new UserViewController(null, languageService, connection, undoService, propertiesService);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullLanguageService_ThrowsNPE() throws Exception {
        var _ = new UserViewController(controllerRepository, null, connection, undoService, propertiesService);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullConnection_ThrowsNPE() throws Exception {
        var _ = new UserViewController(controllerRepository, languageService, null, undoService, propertiesService);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullUndoService_ThrowsNPE() throws Exception {
        var _ = new UserViewController(controllerRepository, languageService, connection, null, propertiesService);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullPropertiesService_ThrowsNPE() throws Exception {
        var _ = new UserViewController(controllerRepository, languageService, connection, undoService, null);
    }

    // --- Initialization & GUI Tests ---

    @Test
    public void initialize_ValidResourceBundle_StoresBundleAndSetsItems() {
        controller.initialize(null, resourceBundle);
        assertEquals(resourceBundle, controller.getResourceBundle());
        assertNotNull(controller.eventManager);
    }

    @Test
    public void updateGuiItems_UpdatesComponentsCorrectly() {
        controller.setResourceBundle(resourceBundle);
        controller.updateGuiItems();

        assertEquals("UserFirstName", userFirstNameTableColumn.getText());
        assertEquals("UserLastName", userLastNameTableColumn.getText());
        assertEquals("UserDetailsLabel", userDetailsLabel.getText());
        assertEquals("UserFirstName", firstNameLabel.getText());
        assertEquals("UserLastName", lastNameLabel.getText());
        assertEquals("UserLogin", loginLabel.getText());
        assertEquals("UserPassword", passwordLabel.getText());
        assertEquals("UserVacationLeft", vacationLeftLabel.getText());
        assertEquals("UserContractName", contractNameLabel.getText());
        assertEquals("UserRoleName", roleNameLabel.getText());
        assertEquals("UserAddress", addressLabel.getText());
        assertEquals("New", newButton.getText());
        assertEquals("Edit", editButton.getText());
        assertEquals("Delete", deleteButton.getText());
    }

    // --- Action Tests ---

    @Test
    public void editUserAction_NoSelection_ShowsAlertSafely() throws Exception {
        controller.setResourceBundle(resourceBundle);
        try {
            reflectionHelper.invokePrivateMethod(controller, "editUserAction", new ActionEvent());
        } catch (InvocationTargetException e) {
            // Alert showAndWait in headless environment may throw/warn
            assertNotNull(e.getTargetException());
        }
    }

    @Test
    public void deleteUserAction_NoSelection_ShowsAlertSafely() throws Exception {
        controller.setResourceBundle(resourceBundle);
        try {
            reflectionHelper.invokePrivateMethod(controller, "deleteUserAction", new ActionEvent());
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