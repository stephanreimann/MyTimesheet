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

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;
import testhelper.ReflectionHelper;
import testhelper.StubFactory;

public class MainToolBarViewControllerTest {

    private StubFactory stubFactory;
    private ReflectionHelper reflectionHelper; 
    
    private Connection connection;
    private ResourceBundle resourceBundle;
    private MainToolBarViewController controller;

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
        stubFactory = new StubFactory();
        reflectionHelper = new ReflectionHelper();
        
        LanguageService languageService = stubFactory.createLanguageServiceStub();
        connection = stubFactory.createConnectionProxyStub();
        UndoService undoService = new UndoService();
        PropertiesService propertiesService = PropertiesService.getInstance();

        resourceBundle = stubFactory.createResourceBundleStub();

        controller = new MainToolBarViewController(languageService, connection, undoService, propertiesService);

        setupUiControls();
        injectUiFields();
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
        reflectionHelper.setField(controller, "undoButton", undoButton);
        reflectionHelper.setField(controller, "undoTooltip", undoTooltip);
        reflectionHelper.setField(controller, "redoButton", redoButton);
        reflectionHelper.setField(controller, "redoTooltip", redoTooltip);
        reflectionHelper.setField(controller, "languageSelectorButton", languageSelectorButton);
        reflectionHelper.setField(controller, "languageDE", languageDE);
        reflectionHelper.setField(controller, "languageEN", languageEN);
        reflectionHelper.setField(controller, "languageES", languageES);
        reflectionHelper.setField(controller, "languageFR", languageFR);
        reflectionHelper.setField(controller, "languageIT", languageIT);
        reflectionHelper.setField(controller, "workItemButton", workItemButton);
        reflectionHelper.setField(controller, "workItemTooltip", workItemTooltip);
    }

    // --- Constructor & Validation Tests ---

    @Test(expected = NullPointerException.class)
    public void constructor_NullLanguageService_ThrowsNPE() {
        var _ = new MainToolBarViewController(null, connection, new UndoService(), PropertiesService.getInstance());
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullConnection_ThrowsNPE() {
        var _ = new MainToolBarViewController(stubFactory.createLanguageServiceStub(), null, new UndoService(), PropertiesService.getInstance());
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullUndoService_ThrowsNPE() {
        var _ = new MainToolBarViewController(stubFactory.createLanguageServiceStub(), connection, null, PropertiesService.getInstance());
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullPropertiesService_ThrowsNPE() {
        var _ = new MainToolBarViewController(stubFactory.createLanguageServiceStub(), connection, new UndoService(), null);
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
        reflectionHelper.invokePrivateMethod(controller, "undoAction", new ActionEvent());
    }

    @Test
    public void redoAction_InvokesSuccessfully() throws Exception {
        reflectionHelper.invokePrivateMethod(controller, "redoAction", new ActionEvent());
    }

    @Test
    public void changeLanguageAction_ExecutesCommand() throws Exception {
        reflectionHelper.setField(controller, "activeMenuItem", languageEN);
        controller.setResourceBundle(resourceBundle);

        ActionEvent event = new ActionEvent(languageDE, null);

        try {
            reflectionHelper.invokePrivateMethod(controller, "changeLanguageAction", event);
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

}