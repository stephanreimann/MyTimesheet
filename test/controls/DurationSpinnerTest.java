/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controls;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import javafx.application.Platform;
import javafx.util.StringConverter;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class DurationSpinnerTest {

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

    @Test
    public void constructors_DefaultValues_InitializedCorrectly() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                DurationSpinner spinner1 = new DurationSpinner();
                assertTrue(spinner1.isAllowNegative());
                assertEquals(Duration.ZERO, spinner1.getValue());

                DurationSpinner spinner2 = new DurationSpinner(false);
                assertFalse(spinner2.isAllowNegative());
                assertEquals(Duration.ZERO, spinner2.getValue());

                DurationSpinner spinner3 = new DurationSpinner(Duration.ofHours(2), false);
                assertFalse(spinner3.isAllowNegative());
                assertEquals(Duration.ofHours(2), spinner3.getValue());
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    public void durationConverter_ToStringAndFromString_ConvertsAccurately() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                DurationSpinner spinner = new DurationSpinner(true);
                StringConverter<Duration> converter = spinner.getDurationConverter();

                // Positive duration
                Duration d1 = Duration.ofHours(5).plusMinutes(30);
                assertEquals("05:30", converter.toString(d1));
                assertEquals(d1, converter.fromString("05:30"));

                // Negative duration
                Duration d2 = Duration.ofHours(-2).plusMinutes(-15);
                assertEquals("-02:15", converter.toString(d2));
                assertEquals(d2, converter.fromString("-02:15"));

                // Null duration
                assertEquals("00:00", converter.toString(null));

                // Empty / null string
                assertEquals(Duration.ZERO, converter.fromString(""));
                assertEquals(Duration.ZERO, converter.fromString(null));
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    public void incrementAndDecrement_ChangesValueCorrectly() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                DurationSpinner spinner = new DurationSpinner(Duration.ofHours(1), true);
                
                spinner.getValueFactory().increment(2); // Increment 2 hours (HOURS mode)
                assertEquals(Duration.ofHours(3), spinner.getValue());

                spinner.getValueFactory().decrement(1); // Decrement 1 hour (HOURS mode)
                assertEquals(Duration.ofHours(2), spinner.getValue());
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    public void allowNegative_False_ClampsNegativeValuesToZero() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                DurationSpinner spinner = new DurationSpinner(Duration.ofMinutes(15), false);
                
                spinner.getValueFactory().decrement(30); // Try to go below zero
                assertEquals(Duration.ZERO, spinner.getValue());
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }
}