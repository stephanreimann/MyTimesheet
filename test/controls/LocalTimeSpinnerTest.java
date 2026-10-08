/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controls;

import java.time.LocalTime;
import java.util.concurrent.CountDownLatch;
import javafx.application.Platform;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class LocalTimeSpinnerTest {

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
                LocalTimeSpinner spinner1 = new LocalTimeSpinner();
                assertEquals(LocalTime.MIN, spinner1.getValue());
                assertEquals(LocalTimeSpinner.Mode.HOURS, spinner1.getMode());

                LocalTime targetTime = LocalTime.of(14, 30);
                LocalTimeSpinner spinner2 = new LocalTimeSpinner(targetTime);
                assertEquals(targetTime, spinner2.getValue());
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    public void modeProperty_GetAndSet_WorksCorrectly() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                LocalTimeSpinner spinner = new LocalTimeSpinner();
                assertEquals(LocalTimeSpinner.Mode.HOURS, spinner.getMode());

                spinner.setMode(LocalTimeSpinner.Mode.MINUTES);
                assertEquals(LocalTimeSpinner.Mode.MINUTES, spinner.getMode());
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    public void incrementAndDecrement_ChangesTimeAccurately() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                LocalTimeSpinner spinner = new LocalTimeSpinner(LocalTime.of(10, 15));

                spinner.getValueFactory().increment(2); // +2 hours
                assertEquals(LocalTime.of(12, 15), spinner.getValue());

                spinner.setMode(LocalTimeSpinner.Mode.MINUTES);
                spinner.getValueFactory().decrement(30); // -30 minutes
                assertEquals(LocalTime.of(11, 45), spinner.getValue());
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }
    
    @Test
    public void formatLocalTime_FormatsAndParsesCorrectly() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                LocalTimeSpinner spinner = new LocalTimeSpinner();
                LocalTime time = LocalTime.of(8, 5);

                LocalTime formattedHHMM = spinner.formatLocalTime(time, LocalTimeSpinner.TimeFormat.HH_MM);
                assertEquals(LocalTime.of(8, 5), formattedHHMM);

                LocalTime formattedSS = spinner.formatLocalTime(time, LocalTimeSpinner.TimeFormat.HH_MM_SS);
                assertEquals(LocalTime.of(8, 5, 0), formattedSS);

                assertNull(spinner.formatLocalTime(null, LocalTimeSpinner.TimeFormat.HH_MM));
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }
}