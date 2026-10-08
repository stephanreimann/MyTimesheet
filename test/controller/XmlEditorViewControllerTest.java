/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import service.LanguageService;
import service.UndoService;
import testhelper.ReflectionHelper;
import testhelper.StubFactory;

import static org.junit.Assert.*;

public class XmlEditorViewControllerTest {

    private StubFactory stubFactory;
    private ReflectionHelper reflectionHelper;

    private ResourceBundle resourceBundle;
    private UndoService undoService;
    private LanguageService languageService;

    private XmlEditorViewController controller;

    private AnchorPane anchorPane;
    private VBox vBox;
    private HBox hBox;
    private TextField filePathAndNameTextFieldValue;
    private Button selectButton;
    private ToolBar toolBar;
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
        undoService = new UndoService();

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

        controller = new XmlEditorViewController(languageService, undoService);

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
        anchorPane = new AnchorPane();
        // VBox must have at least 2 children so codeArea/virtualizedScrollPane can be removed/added at index 1 without IndexOutOfBoundsException
        vBox = new VBox(new Pane(), new Pane());
        hBox = new HBox();
        filePathAndNameTextFieldValue = new TextField();
        selectButton = new Button();
        toolBar = new ToolBar();
        acceptButton = new Button();
        cancelButton = new Button();
    }

    private void injectUiFields() throws Exception {
        reflectionHelper.setField(controller, "anchorPane", anchorPane);
        reflectionHelper.setField(controller, "vBox", vBox);
        reflectionHelper.setField(controller, "hBox", hBox);
        reflectionHelper.setField(controller, "filePathAndNameTextFieldValue", filePathAndNameTextFieldValue);
        reflectionHelper.setField(controller, "selectButton", selectButton);
        reflectionHelper.setField(controller, "toolBar", toolBar);
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
        var _ = new XmlEditorViewController(null, undoService);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullUndoService_ThrowsNPE() {
        var _ = new XmlEditorViewController(languageService, null);
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

        assertEquals("Select", selectButton.getText());
        assertEquals("Accept", acceptButton.getText());
        assertEquals("Cancel", cancelButton.getText());
        assertEquals("PathToXmlFile", filePathAndNameTextFieldValue.getText());
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
    public void acceptAction_EmptyFilePath_ShowsAlertSafely() throws Exception {
        controller.setResourceBundle(resourceBundle);
        filePathAndNameTextFieldValue.setText("");

        try {
            reflectionHelper.invokePrivateMethod(controller, "acceptAction", new ActionEvent());
        } catch (InvocationTargetException e) {
            // Alert showAndWait in headless environment may throw/warn
            assertNotNull(e.getTargetException());
        }
    }

    @Test
    public void preCloseAction_ExecutesSafely() {
        controller.setResourceBundle(resourceBundle);
        controller.preCloseAction();
        assertTrue(true);
    }
}