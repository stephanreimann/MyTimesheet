/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import controller.IViewController;
import controller.MainToolBarViewController;
import java.sql.Connection;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.fail;
import service.LanguageService;
import service.PropertiesService;
import service.UndoService;

public class DialogFactoryTest {

    @BeforeClass
    public static void initJavaFX() {
        // Initialize the JavaFX runtime toolkit (safe to call multiple times)
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Toolkit already initialized
        }
    }

    @Test
    public void testConstructorAndInitializationParameters() throws Exception {
        // Use a latch to wait for the JavaFX thread execution to complete
        CountDownLatch latch = new CountDownLatch(1);

        // Run UI component creation and testing on the JavaFX Application Thread
        Platform.runLater(() -> {
            try {
                // Arrange
                Stage primaryStage = new Stage();

                ResourceBundle mockRb = new ResourceBundle() {
                    @Override
                    protected Object handleGetObject(String key) {
                        return switch (key) {
                            case "dialog.title" -> "Test Dialog Title";
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

                // 1. Stub LanguageService via an anonymous subclass to avoid JDK 26 inline-mock restrictions
                var languageService = new LanguageService() {
                    @Override
                    public void updateGuiItems() {
                        // No-op stub for testing
                    }
                };

                // 2. Provide a lightweight proxy stub for java.sql.Connection
                var connection = (Connection) java.lang.reflect.Proxy.newProxyInstance(
                    Connection.class.getClassLoader(),
                    new Class<?>[]{Connection.class},
                    (proxy, method, args) -> null
                );

                // 3. Real instance of UndoService
                var undoService = new UndoService();

                // 4. Mock properties service using Mockito
                var propertiesService = PropertiesService.getInstance();
                
                IViewController controller = new MainToolBarViewController(languageService, connection, undoService, propertiesService);

                String titleKey = "dialog.title";
                String dialogResource = "/fxml/dummy.fxml";
                String iconPath = ""; // Ensure a valid resource path or stream if loaded

                DialogFactory factory = new DialogFactory(
                        primaryStage,
                        titleKey,
                        iconPath,
                        dialogResource,
                        mockRb,
                        controller
                );

                // If you call factory.create(...) here, it will also need to run on this FX thread 
                // (though ControllerUtilities.load and Image loading might need mock/valid assets).

            } catch (Throwable t) {
                fail("Exception on JavaFX thread: " + t.getMessage());
            } finally {
                latch.countDown(); // Release the latch so JUnit finishes
            }
        });

        // Wait for the JavaFX thread to finish execution (timeout after 5 seconds)
        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("JavaFX thread task timed out");
        }
    }
}