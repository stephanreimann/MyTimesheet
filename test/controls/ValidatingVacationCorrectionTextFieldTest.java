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

public class ValidatingVacationCorrectionTextFieldTest {

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
                ValidatingVacationCorrectionTextField textField = new ValidatingVacationCorrectionTextField("+/- Days");
                assertEquals("+/- Days", textField.getPromptText());
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    public void replaceText_EmptyText_AllowedAlways() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                ValidatingVacationCorrectionTextField textField = new ValidatingVacationCorrectionTextField("");
                textField.setText("30");
                textField.replaceText(0, 2, "");
                assertEquals("", textField.getText());
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    public void replaceText_ValidPositiveInput_Accepted() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                ValidatingVacationCorrectionTextField textField = new ValidatingVacationCorrectionTextField("");
                
                // Type '3' at start 0
                textField.replaceText(0, 0, "3");
                assertEquals("3", textField.getText());

                // Type '0' at start 1
                textField.replaceText(1, 1, "0");
                assertEquals("30", textField.getText());
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    public void replaceText_ValidNegativeInput_Accepted() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                ValidatingVacationCorrectionTextField textField = new ValidatingVacationCorrectionTextField("");
                
                // Type '-' at start 0
                textField.replaceText(0, 0, "-");
                assertEquals("-", textField.getText());

                // Type '3' at start 1
                textField.replaceText(1, 1, "3");
                assertEquals("-3", textField.getText());

                // Type '0' at start 2
                textField.replaceText(2, 2, "0");
                assertEquals("-30", textField.getText());
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }
}