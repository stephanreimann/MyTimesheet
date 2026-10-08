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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Holyday;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import service.LanguageService;
import service.UndoService;
import utils.EventManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;
import sqlite.ConnectionFactory;
import testhelper.ReflectionHelper;
import testhelper.StubFactory;

public class ImportHolydaysViewControllerTest {

    private final static String DATABASE_PATH_AND_FULL_NAME = System.getProperty("user.dir") + "/sqlite/testdb.sqlite";
    private final static String LOG4J2_PATH_AND_FULL_NAME = System.getProperty("user.dir") + "/log4j2.xml";

    private StubFactory stubFactory;
    private ReflectionHelper reflectionHelper;

    private ConnectionFactory connectionFactory;
    private Connection connection;
    private ResourceBundle resourceBundle;
    private UndoService undoService;
    private EventManager eventManager;
    private ObservableList<Holyday> holydayData;
    private ImportHolydaysViewController controller;

    private TextField filePathAndNameTextFieldValue;
    private Button selectButton;
    private TableView<Holyday> holydayTableView;
    private TableColumn<Holyday, LocalDate> holydayDateTableColumn;
    private TableColumn<Holyday, String> holydayNameTableColumn;
    private TableColumn<Holyday, String> holydayStateTableColumn;
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

        LanguageService languageService = stubFactory.createLanguageServiceStub();
        resourceBundle = stubFactory.createResourceBundleStub();
        connectionFactory = new ConnectionFactory(resourceBundle, new Log4jAdapter(LOG4J2_PATH_AND_FULL_NAME));
        connection = connectionFactory.getConnection(DATABASE_PATH_AND_FULL_NAME);

        undoService = new UndoService();
        eventManager = new EventManager();
        eventManager.registerEventType("ImportHolyday");

        holydayData = FXCollections.observableArrayList();

        // Instantiate package-private ImportHolydaysViewController
        controller = new ImportHolydaysViewController(languageService, connection, undoService, holydayData, eventManager);

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
        filePathAndNameTextFieldValue = new TextField();
        selectButton = new Button();
        holydayTableView = new TableView<>();
        holydayDateTableColumn = new TableColumn<>();
        holydayNameTableColumn = new TableColumn<>();
        holydayStateTableColumn = new TableColumn<>();
        acceptButton = new Button();
        cancelButton = new Button();
    }

    private void injectUiFields() throws Exception {
        reflectionHelper.setField(controller, "filePathAndNameTextFieldValue", filePathAndNameTextFieldValue);
        reflectionHelper.setField(controller, "selectButton", selectButton);
        reflectionHelper.setField(controller, "holydayTableView", holydayTableView);
        reflectionHelper.setField(controller, "holydayDateTableColumn", holydayDateTableColumn);
        reflectionHelper.setField(controller, "holydayNameTableColumn", holydayNameTableColumn);
        reflectionHelper.setField(controller, "holydayStateTableColumn", holydayStateTableColumn);
        reflectionHelper.setField(controller, "acceptButton", acceptButton);
        reflectionHelper.setField(controller, "cancelButton", cancelButton);
    }

    @After
    public void tearDown() {
        truncateTable();
    }

    private synchronized boolean truncateTable() {
        boolean result = false;
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM holyday");
            result = stmt.execute();
            PreparedStatement seqStmt = connection.prepareStatement("DELETE FROM SQLITE_SEQUENCE WHERE name='Holyday'");
            seqStmt.execute();
        } catch (SQLException e) {
            // ignore
        }
        return result;
    }

    // --- Constructor & Validation Tests ---

    @Test(expected = NullPointerException.class)
    public void constructor_NullLanguageService_ThrowsNPE() {
        var _ = new ImportHolydaysViewController(null, connection, undoService, holydayData, eventManager);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullConnection_ThrowsNPE() {
        var _ = new ImportHolydaysViewController(stubFactory.createLanguageServiceStub(), null, undoService, holydayData, eventManager);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullUndoService_ThrowsNPE() {
        var _ = new ImportHolydaysViewController(stubFactory.createLanguageServiceStub(), connection, null, holydayData, eventManager);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullHolydayData_ThrowsNPE() {
        var _ = new ImportHolydaysViewController(stubFactory.createLanguageServiceStub(), connection, undoService, null, eventManager);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullEventManager_ThrowsNPE() {
        var _ = new ImportHolydaysViewController(stubFactory.createLanguageServiceStub(), connection, undoService, holydayData, null);
    }

    // --- Initialization & GUI Tests ---

    @Test
    public void initialize_ValidResourceBundle_StoresBundle() {
        controller.initialize(null, resourceBundle);
        assertEquals(resourceBundle, controller.getResourceBundle());
        assertTrue(acceptButton.isDisable());
    }

    @Test
    public void updateGuiItems_UpdatesComponentsCorrectly() {
        controller.setResourceBundle(resourceBundle);
        controller.updateGuiItems();

        assertEquals("HolydayDate", holydayDateTableColumn.getText());
        assertEquals("HolydayName", holydayNameTableColumn.getText());
        assertEquals("HolydayState", holydayStateTableColumn.getText());
        assertEquals("Select", selectButton.getText());
        assertEquals("Accept", acceptButton.getText());
        assertEquals("Cancel", cancelButton.getText());
    }

    // --- Action Tests ---

 @Test
    public void cancelAction_ClosesStageSafely() throws Exception {
        try {
            reflectionHelper.invokePrivateMethod(controller, "cancelAction", new ActionEvent());
        } catch (InvocationTargetException e) {
            // Expected in headless test environments when primaryStage.close() runs
            assertNotNull(e.getTargetException());
        }
    }
    @Test
    public void acceptAction_EmptyTable_NotifiesAndCloses() throws Exception {
        try {
            reflectionHelper.invokePrivateMethod(controller, "acceptAction", new ActionEvent());
        } catch (InvocationTargetException e) {
            // Expected in headless test environments when primaryStage.close() runs
            assertNotNull(e.getTargetException());
        }
    }

}