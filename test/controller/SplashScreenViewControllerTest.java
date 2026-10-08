/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import testhelper.ReflectionHelper;

import static org.junit.Assert.*;

public class SplashScreenViewControllerTest {

    private ReflectionHelper reflectionHelper;
    private ResourceBundle resourceBundle;
    private SplashScreenViewController controller;

    private Label appNameLabel;
    private Label statusLabel;
    private Label appVersionLabel;
    private Stage primaryStage;

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
        reflectionHelper = new ReflectionHelper();

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

        controller = new SplashScreenViewController();

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                primaryStage = new Stage();
                controller.setPrimaryStage(primaryStage);
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
        appNameLabel = new Label();
        statusLabel = new Label();
        appVersionLabel = new Label();
    }

    private void injectUiFields() throws Exception {
        reflectionHelper.setField(controller, "appNameLabel", appNameLabel);
        reflectionHelper.setField(controller, "statusLabel", statusLabel);
        reflectionHelper.setField(controller, "appVersionLabel", appVersionLabel);
    }

    @After
    public void tearDown() {
        // Cleanup if needed
    }

    // --- Initialization & GUI Tests ---

    @Test
    public void initialize_ValidResourceBundle_StoresBundle() {
        controller.initialize(null, resourceBundle);
        assertEquals(resourceBundle, controller.getResourceBundle());
    }

    @Test
    public void updateGuiItems_UpdatesLabelsCorrectly() throws Exception {
        controller.setResourceBundle(resourceBundle);
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                controller.updateGuiItems();
                assertEquals("AppName", appNameLabel.getText());
                assertEquals("AppVersion", appVersionLabel.getText());
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    public void setStatus_UpdatesStatusLabelWithTranslatedKey() throws Exception {
        controller.setResourceBundle(resourceBundle);
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                controller.setStatus("AppName");
                assertEquals("AppName", statusLabel.getText());
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    public void preCloseAction_ExecutesSafely() {
        controller.preCloseAction();
        assertTrue(true);
    }
}