/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controls;

import java.util.concurrent.CountDownLatch;
import javafx.application.Platform;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class ValidatingOverTimeThresholdTextFieldTest {

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
    public void constructor_PromptText_InitializedCorrectly() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                ValidatingOverTimeThresholdTextField textField = new ValidatingOverTimeThresholdTextField("HH:mm");
                assertEquals("HH:mm", textField.getPromptText());
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    public void compare_ValidInputs_ReturnsTrue() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                ValidatingOverTimeThresholdTextField textField = new ValidatingOverTimeThresholdTextField("");

                assertTrue(textField.compare("5"));
                assertTrue(textField.compare("5:"));
                assertTrue(textField.compare("5:3"));
                assertTrue(textField.compare("5:30"));
                assertTrue(textField.compare("12"));
                assertTrue(textField.compare("12:"));
                assertTrue(textField.compare("12:45"));
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    public void compare_InvalidInputs_ReturnsFalse() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                ValidatingOverTimeThresholdTextField textField = new ValidatingOverTimeThresholdTextField("");

                assertFalse(textField.compare("-5"));
                assertFalse(textField.compare("abc"));
                assertFalse(textField.compare("12:60")); // minutes > 59
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    public void replaceText_ValidText_UpdatesSuccessfully() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                ValidatingOverTimeThresholdTextField textField = new ValidatingOverTimeThresholdTextField("");
                textField.replaceText(0, 0, "08");
                assertEquals("08", textField.getText());

                textField.replaceText(2, 2, ":15");
                assertEquals("08:15", textField.getText());
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }
}