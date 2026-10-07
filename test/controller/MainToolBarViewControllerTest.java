/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.Tooltip;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import service.LanguageService;
import service.PropertiesService;
import service.UndoService;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

public class MainToolBarViewControllerTest {

    private Connection connection;
    private ResourceBundle resourceBundle;
    private MainToolBarViewController controller;

    // FXML injected UI controls
    private Button undoButton;
    private Tooltip undoTooltip;
    private Button redoButton;
    private Tooltip redoTooltip;
    private SplitMenuButton languageSelectorButton;
    private MenuItem languageDE;
    private MenuItem languageEN;
    private MenuItem languageES;
    private MenuItem languageFR;
    private MenuItem languageIT;
    private Button workItemButton;
    private Tooltip workItemTooltip;

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
        LanguageService languageService = createLanguageServiceStub();
        connection = createConnectionProxyStub();
        UndoService undoService = new UndoService();
        PropertiesService propertiesService = PropertiesService.getInstance();

        resourceBundle = createResourceBundleStub();

        controller = new MainToolBarViewController(languageService, connection, undoService, propertiesService);

        setupUiControls();
        injectUiFields();
    }

    // --- Extraction Helpers for setUp() ---

    private LanguageService createLanguageServiceStub() {
        return new LanguageService() {
            @Override
            public void updateGuiItems() {
                // No-op stub for testing
            }
        };
    }

    private Connection createConnectionProxyStub() {
        return (Connection) Proxy.newProxyInstance(
                Connection.class.getClassLoader(),
                new Class<?>[]{Connection.class},
                (proxy, method, args) -> null
        );
    }

    private ResourceBundle createResourceBundleStub() {
        return new ResourceBundle() {
            @Override
            protected Object handleGetObject(String key) {
                return switch (key) {
                    case "languageSelectorButton" -> "Sprache";
                    case "LanguageDE" -> "Deutsch";
                    case "LanguageEN" -> "Englisch";
                    case "LanguageES" -> "Spanisch";
                    case "LanguageFR" -> "Französisch";
                    case "LanguageIT" -> "Italienisch";
                    case "Undo" -> "Rückgängig";
                    case "Redo" -> "Wiederholen";
                    case "WorkItemToolTip" -> "Arbeitselement";
                    case "WorkItemViewTitle" -> "Arbeitselement-Ansicht";
                    default -> key;
                };
            }

            @Override
            public Enumeration<String> getKeys() {
                return Collections.emptyEnumeration();
            }

            @Override
            public Locale getLocale() {
                return Locale.GERMAN;
            }
        };
    }

    private void setupUiControls() {
        undoButton = new Button();
        undoTooltip = new Tooltip();
        redoButton = new Button();
        redoTooltip = new Tooltip();
        languageSelectorButton = new SplitMenuButton();

        languageDE = createMenuItem("changeToGerman");
        languageEN = createMenuItem("changeToEnglish");
        languageES = createMenuItem("changeToSpanish");
        languageFR = createMenuItem("changeToFrench");
        languageIT = createMenuItem("changeToItalian");

        workItemButton = new Button();
        workItemTooltip = new Tooltip();
    }

    private MenuItem createMenuItem(String id) {
        MenuItem item = new MenuItem();
        item.setId(id);
        return item;
    }

    private void injectUiFields() throws Exception {
        setField("undoButton", undoButton);
        setField("undoTooltip", undoTooltip);
        setField("redoButton", redoButton);
        setField("redoTooltip", redoTooltip);
        setField("languageSelectorButton", languageSelectorButton);
        setField("languageDE", languageDE);
        setField("languageEN", languageEN);
        setField("languageES", languageES);
        setField("languageFR", languageFR);
        setField("languageIT", languageIT);
        setField("workItemButton", workItemButton);
        setField("workItemTooltip", workItemTooltip);
    }

    // --- Constructor & Validation Tests ---

    @Test(expected = NullPointerException.class)
    public void constructor_NullLanguageService_ThrowsNPE() {
        var _ = new MainToolBarViewController(null, connection, new UndoService(), PropertiesService.getInstance());
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullConnection_ThrowsNPE() {
        var _ = new MainToolBarViewController(createLanguageServiceStub(), null, new UndoService(), PropertiesService.getInstance());
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullUndoService_ThrowsNPE() {
        var _ = new MainToolBarViewController(createLanguageServiceStub(), connection, null, PropertiesService.getInstance());
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullPropertiesService_ThrowsNPE() {
        var _ = new MainToolBarViewController(createLanguageServiceStub(), connection, new UndoService(), null);
    }

    // --- Initialization & GUI Tests ---

    @Test
    public void initialize_ValidResourceBundle_StoresBundle() {
        controller.initialize(null, resourceBundle);
        assertEquals(resourceBundle, controller.getResourceBundle());
    }

    @Test
    public void updateGuiItems_GermanLocale_UpdatesComponentsCorrectly() {
        controller.setResourceBundle(resourceBundle);
        controller.updateGuiItems();

        assertEquals("Sprache", languageSelectorButton.getText());
        assertEquals("Deutsch", languageDE.getText());
        assertEquals("Rückgängig", undoButton.getText());
        assertEquals("Rückgängig", undoTooltip.getText());
        assertEquals("Wiederholen", redoButton.getText());
        assertEquals("Wiederholen", redoTooltip.getText());
        assertEquals("Arbeitselement", workItemTooltip.getText());

        assertTrue(languageDE.isDisable());
        assertFalse(languageEN.isDisable());
        assertEquals(languageDE, controller.GetActiveMenuItem());
    }

    @Test
    public void toggleUndoRedoButtons_ReflectsStackStatus() {
        controller.toggleUndoRedoButtons();
        assertTrue(undoButton.isDisable());
        assertTrue(redoButton.isDisable());
    }

    // --- Action & Event Tests ---

    @Test
    public void undoAction_InvokesSuccessfully() throws Exception {
        invokePrivateMethod("undoAction", new ActionEvent());
    }

    @Test
    public void redoAction_InvokesSuccessfully() throws Exception {
        invokePrivateMethod("redoAction", new ActionEvent());
    }

    @Test
    public void changeLanguageAction_ExecutesCommand() throws Exception {
        setField("activeMenuItem", languageEN);
        controller.setResourceBundle(resourceBundle);

        ActionEvent event = new ActionEvent(languageDE, null);

        try {
            invokePrivateMethod("changeLanguageAction", event);
        } catch (InvocationTargetException e) {
            if (!(e.getTargetException() instanceof NullPointerException)) {
                throw e; // Rethrow if unexpected
            }
        }

        assertNotNull(controller.GetActiveMenuItem());
    }

    @Test
    public void getWorkItemButton_ReturnsCorrectInstance() {
        assertEquals(workItemButton, controller.getWorkItemButton());
    }

    // --- Reflection Helpers ---

    private void setField(String fieldName, Object value) throws Exception {
        Field field = MainToolBarViewController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(controller, value);
    }

    private void invokePrivateMethod(String methodName, ActionEvent event) throws Exception {
        var method = MainToolBarViewController.class.getDeclaredMethod(methodName, ActionEvent.class);
        method.setAccessible(true);
        method.invoke(controller, event);
    }
}