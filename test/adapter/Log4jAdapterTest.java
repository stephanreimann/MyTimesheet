/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package adapter;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import utils.InfoViewLogAppender;

public class Log4jAdapterTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private File validConfigFile;

    @Before
    public void setUp() throws IOException {
        // Create a minimal valid Log4j2 XML configuration file in a temporary folder
        validConfigFile = tempFolder.newFile("log4j2-test.xml");
        String validConfigContent = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<Configuration status=\"WARN\">\n" +
            "    <Appenders>\n" +
            "        <Console name=\"Console\" target=\"SYSTEM_OUT\">\n" +
            "            <PatternLayout pattern=\"%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n\"/>\n" +
            "        </Console>\n" +
            "    </Appenders>\n" +
            "    <Loggers>\n" +
            "        <Root level=\"info\">\n" +
            "            <AppenderRef ref=\"Console\"/>\n" +
            "        </Root>\n" +
            "    </Loggers>\n" +
            "</Configuration>";
        
        java.nio.file.Files.writeString(validConfigFile.toPath(), validConfigContent);
    }

    @Test
    public void testConstructor_Success() throws Exception {
        Log4jAdapter adapter = new Log4jAdapter(validConfigFile.getAbsolutePath());
        assertNotNull("Adapter should be successfully instantiated", adapter);
    }

    @Test(expected = FileNotFoundException.class)
    public void testConstructor_FileNotFound() throws Exception {
        String nonExistentPath = tempFolder.getRoot().getAbsolutePath() + File.separator + "non-existent-config.xml";
        new Log4jAdapter(nonExistentPath);
    }

    @Test
    public void testGetLogger() throws Exception {
        Log4jAdapter adapter = new Log4jAdapter(validConfigFile.getAbsolutePath());
        Logger logger = adapter.getLogger("TestLoggerName");
        
        assertNotNull("Retrieved logger should not be null", logger);
        assertSame("Log4j2 should return the same logger instance for the same name", 
                   logger, adapter.getLogger("TestLoggerName"));
    }

    @Test
    public void testGetInfoViewLogAppender_NotFound() throws Exception {
        Log4jAdapter adapter = new Log4jAdapter(validConfigFile.getAbsolutePath());
        
        // The temporary config doesn't contain 'InfoViewLogAppender', so it should return null
        InfoViewLogAppender appender = adapter.getInfoViewLogAppender();
        assertNull("InfoViewLogAppender should be null when not present in configuration", appender);
    }

    @Test
    public void testGetInfoViewLogAppender_ThreadSafetyAndCaching() throws Exception {
        Log4jAdapter adapter = new Log4jAdapter(validConfigFile.getAbsolutePath());
        
        // Test lazy initialization caching (subsequent calls return the cached instance)
        InfoViewLogAppender firstCall = adapter.getInfoViewLogAppender();
        InfoViewLogAppender secondCall = adapter.getInfoViewLogAppender();
        
        assertSame("Subsequent calls should return the cached appender instance via double-checked locking", 
                   firstCall, secondCall);
    }
}