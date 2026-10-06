/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import javafx.application.Platform;
import javafx.scene.control.Spinner;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DurationStylerTest {

    @BeforeClass
    public static void initJavaFX() {
        // Initialize JavaFX toolkit safely
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Toolkit already initialized
        }
    }

    @Test
    public void testStyleSpinner_NegativeDuration_SetsRedColor() throws Exception {
        runOnFXThread(() -> {
            Spinner<Integer> spinner = new Spinner<>(0, 10, 0);
            Duration negativeDuration = Duration.ofMinutes(-30);

            DurationStyler.styleSpinner(spinner, negativeDuration);

            assertEquals("-fx-text-fill: Red;", spinner.getEditor().getStyle());
        });
    }

    @Test
    public void testStyleSpinner_PositiveDuration_SetsGreenColor() throws Exception {
        runOnFXThread(() -> {
            Spinner<Integer> spinner = new Spinner<>(0, 10, 0);
            Duration positiveDuration = Duration.ofHours(2);

            DurationStyler.styleSpinner(spinner, positiveDuration);

            assertEquals("-fx-text-fill:Green;", spinner.getEditor().getStyle());
        });
    }

    @Test
    public void testStyleSpinner_ZeroDuration_ClearsStyle() throws Exception {
        runOnFXThread(() -> {
            Spinner<Integer> spinner = new Spinner<>(0, 10, 0);
            // Pre-set a style to verify it gets cleared
            spinner.getEditor().setStyle("-fx-text-fill: Red;");

            DurationStyler.styleSpinner(spinner, Duration.ZERO);

            assertEquals("", spinner.getEditor().getStyle());
        });
    }

    /**
     * Helper method to execute JavaFX operations on the correct thread 
     * and block the JUnit test thread until completion.
     */
    private void runOnFXThread(Runnable action) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        final Throwable[] caughtException = new Throwable[1];

        Platform.runLater(() -> {
            try {
                action.run();
            } catch (Throwable t) {
                caughtException[0] = t;
            } finally {
                latch.countDown();
            }
        });

        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("JavaFX thread task timed out");
        }

        if (caughtException[0] != null) {
            throw new Exception(caughtException[0]);
        }
    }
}