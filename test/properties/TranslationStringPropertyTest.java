/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package properties;

import adapter.Log4jAdapter;
import java.io.FileNotFoundException;
import javafx.application.Platform;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import testhelper.StubFactory;

import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;

public class TranslationStringPropertyTest {

    private final static String LOG4J2_PATH_AND_FULL_NAME = System.getProperty("user.dir") + "/log4j2.xml";

    private StubFactory stubFactory;
    private Log4jAdapter log4jAdapter;
    private ResourceBundle resourceBundle;
    private TranslationStringProperty translationProperty;

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
    public void setUp() throws FileNotFoundException {
        stubFactory = new StubFactory();
        log4jAdapter = new Log4jAdapter(LOG4J2_PATH_AND_FULL_NAME);
        resourceBundle = stubFactory.createResourceBundleStub();

        translationProperty = new TranslationStringProperty(log4jAdapter);
    }

    // --- Translation Tests ---

    @Test
    public void translate_ValidKeyWithoutArgs_SetsCorrectValue() {
        translationProperty.translate("NotExistingKey", resourceBundle);
        assertEquals("Undef", translationProperty.get());
    }

    @Test
    public void translate_NullKey_DoesNotChangeValue() {
        translationProperty.set("Initial");
        translationProperty.translate((String) null, resourceBundle);
        assertEquals("Initial", translationProperty.get());
    }

    @Test
    public void translate_NullResourceBundle_SetsUndef() {
        translationProperty.translate("SomeKey", null);
        assertEquals("Undef", translationProperty.get());
    }

    @Test
    public void translate_KeyWithArgs_NullBundle_SetsUndef() {
        Object[] args = {"Test"};
        translationProperty.translate("SomeKey", args, null);
        assertEquals("Undef", translationProperty.get());
    }
}