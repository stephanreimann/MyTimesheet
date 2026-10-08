package main;

import javafx.application.Platform;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MainTest {

    @BeforeClass
    public static void initJFX() {
        // Initialize the JavaFX runtime (safe to call multiple times)
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Toolkit already initialized
        }
    }

    @Test
    public void testMainInstantiation() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        
        Platform.runLater(() -> {
            try {
                Main mainApp = new Main();
                assertNotNull(mainApp);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                latch.countDown();
            }
        });

        assertTrue("Main instantiation timed out", latch.await(5, TimeUnit.SECONDS));
    }

    @Test
    public void testMainAppLaunchStructureExists() {
        // Verifies that Main is a proper subclass of JavaFX Application 
        assertTrue(javafx.application.Application.class.isAssignableFrom(Main.class));
    }
}