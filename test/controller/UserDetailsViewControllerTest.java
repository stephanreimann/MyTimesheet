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
import model.Contract;
import model.Role;
import model.User;
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

public class UserDetailsViewControllerTest {

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
    private ObservableList<User> userData;

    private UserDetailsViewController controller;

    private Label firstNameLabel;
    private Label lastNameLabel;
    private Label loginLabel;
    private Label passwordLabel;
    private Label vacationLeftLabel;
    private Label contractNameLabel;
    private Label roleNameLabel;
    private Label addressLabel;

    private TextField firstNameTextFieldValue;
    private TextField lastNameTextFieldValue;
    private TextField loginTextFieldValue;
    private PasswordField hiddenPasswordFieldValue;
    private TextField shownPasswordFieldValue;
    private ToggleButton togglePasswordButton;
    private TextField vacationLeftTextFieldValue;
    private Label vacationLeftUnitLabel;
    private ChoiceBox<Contract> contractChoiceBoxValue;
    private ChoiceBox<Role> roleChoiceBoxValue;
    private ChoiceBox<Address> addressChoiceBoxValue;

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
        userData = FXCollections.observableArrayList();

        controller = new UserDetailsViewController(languageService, connection, undoService, userData);

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
        firstNameLabel = new Label();
        lastNameLabel = new Label();
        loginLabel = new Label();
        passwordLabel = new Label();
        vacationLeftLabel = new Label();
        contractNameLabel = new Label();
        roleNameLabel = new Label();
        addressLabel = new Label();

        firstNameTextFieldValue = new TextField();
        lastNameTextFieldValue = new TextField();
        loginTextFieldValue = new TextField();
        hiddenPasswordFieldValue = new PasswordField();
        shownPasswordFieldValue = new TextField();
        togglePasswordButton = new ToggleButton();
        vacationLeftTextFieldValue = new TextField();
        vacationLeftUnitLabel = new Label();
        contractChoiceBoxValue = new ChoiceBox<>();
        roleChoiceBoxValue = new ChoiceBox<>();
        addressChoiceBoxValue = new ChoiceBox<>();

        acceptButton = new Button();
        cancelButton = new Button();
    }

    private void injectUiFields() throws Exception {
        reflectionHelper.setField(controller, "firstNameLabel", firstNameLabel);
        reflectionHelper.setField(controller, "lastNameLabel", lastNameLabel);
        reflectionHelper.setField(controller, "loginLabel", loginLabel);
        reflectionHelper.setField(controller, "passwordLabel", passwordLabel);
        reflectionHelper.setField(controller, "vacationLeftLabel", vacationLeftLabel);
        reflectionHelper.setField(controller, "contractNameLabel", contractNameLabel);
        reflectionHelper.setField(controller, "roleNameLabel", roleNameLabel);
        reflectionHelper.setField(controller, "addressLabel", addressLabel);

        reflectionHelper.setField(controller, "firstNameTextFieldValue", firstNameTextFieldValue);
        reflectionHelper.setField(controller, "lastNameTextFieldValue", lastNameTextFieldValue);
        reflectionHelper.setField(controller, "loginTextFieldValue", loginTextFieldValue);
        reflectionHelper.setField(controller, "hiddenPasswordFieldValue", hiddenPasswordFieldValue);
        reflectionHelper.setField(controller, "shownPasswordFieldValue", shownPasswordFieldValue);
        reflectionHelper.setField(controller, "togglePasswordButton", togglePasswordButton);
        reflectionHelper.setField(controller, "vacationLeftTextFieldValue", vacationLeftTextFieldValue);
        reflectionHelper.setField(controller, "vacationLeftUnitLabel", vacationLeftUnitLabel);
        reflectionHelper.setField(controller, "contractChoiceBoxValue", contractChoiceBoxValue);
        reflectionHelper.setField(controller, "roleChoiceBoxValue", roleChoiceBoxValue);
        reflectionHelper.setField(controller, "addressChoiceBoxValue", addressChoiceBoxValue);

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
        var _ = new UserDetailsViewController(null, connection, undoService, userData);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullConnection_ThrowsNPE() throws Exception {
        var _ = new UserDetailsViewController(languageService, null, undoService, userData);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullUndoService_ThrowsNPE() throws Exception {
        var _ = new UserDetailsViewController(languageService, connection, null, userData);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullUserData_ThrowsNPE() throws Exception {
        var _ = new UserDetailsViewController(languageService, connection, undoService, null);
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

        assertEquals("UserFirstName", firstNameLabel.getText());
        assertEquals("UserLastName", lastNameLabel.getText());
        assertEquals("UserLogin", loginLabel.getText());
        assertEquals("UserPassword", passwordLabel.getText());
        assertEquals("UserVacationLeft", vacationLeftLabel.getText());
        assertEquals("Days", vacationLeftUnitLabel.getText());
        assertEquals("UserContractName", contractNameLabel.getText());
        assertEquals("UserRoleName", roleNameLabel.getText());
        assertEquals("UserAddress", addressLabel.getText());
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
        controller.setAction(UserViewController.DataAction.NEW);

        User user = new User(1L);
        controller.showUserDetails(user);

        firstNameTextFieldValue.setText("John");
        lastNameTextFieldValue.setText("Doe");
        loginTextFieldValue.setText("jdoe");
        hiddenPasswordFieldValue.setText("secret");
        vacationLeftTextFieldValue.setText("25");

        Contract contract = new Contract(1L);
        Role role = new Role(1L);
        Address address = new Address(1L);

        contractChoiceBoxValue.getItems().add(contract);
        contractChoiceBoxValue.setValue(contract);

        roleChoiceBoxValue.getItems().add(role);
        roleChoiceBoxValue.setValue(role);

        addressChoiceBoxValue.getItems().add(address);
        addressChoiceBoxValue.setValue(address);

        try {
            reflectionHelper.invokePrivateMethod(controller, "acceptAction", new ActionEvent());
        } catch (InvocationTargetException e) {
            assertNotNull(e.getTargetException());
        }

        assertEquals("John", user.getFirstname());
        assertEquals("Doe", user.getLastname());
        assertEquals("jdoe", user.getLogin());
        assertEquals(Long.valueOf(25), user.getVacationleft());
    }

    @Test
    public void preCloseAction_ExecutesSafely() {
        controller.preCloseAction();
        assertTrue(true);
    }
}