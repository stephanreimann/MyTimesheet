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

public class ValidatingDeltaTimeTextFieldTest {

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
                ValidatingDeltaTimeTextField textField = new ValidatingDeltaTimeTextField("HH:mm");
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
                ValidatingDeltaTimeTextField textField = new ValidatingDeltaTimeTextField("");

                // Negative formats
                assertTrue(textField.compare("-"));
                assertTrue(textField.compare("-1"));
                assertTrue(textField.compare("-1:"));
                assertTrue(textField.compare("-1:2"));
                assertTrue(textField.compare("-1:23"));
                assertTrue(textField.compare("-12"));
                assertTrue(textField.compare("-12:"));
                assertTrue(textField.compare("-12:3"));
                assertTrue(textField.compare("-12:34"));

                // Positive formats
                assertTrue(textField.compare("1"));
                assertTrue(textField.compare("1:"));
                assertTrue(textField.compare("1:2"));
                assertTrue(textField.compare("1:23"));
                assertTrue(textField.compare("12"));
                assertTrue(textField.compare("12:"));
                assertTrue(textField.compare("12:3"));
                assertTrue(textField.compare("12:34"));
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
                ValidatingDeltaTimeTextField textField = new ValidatingDeltaTimeTextField("");

                assertFalse(textField.compare("abc"));
                assertFalse(textField.compare("--"));
                assertFalse(textField.compare("12:60")); // minutes > 59
                assertFalse(textField.compare("-12:60"));
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
                ValidatingDeltaTimeTextField textField = new ValidatingDeltaTimeTextField("");
                textField.replaceText(0, 0, "12");
                assertEquals("12", textField.getText());

                textField.replaceText(2, 2, ":30");
                assertEquals("12:30", textField.getText());
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }
}