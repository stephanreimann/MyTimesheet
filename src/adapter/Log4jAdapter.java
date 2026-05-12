package adapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI; // Use URI directly for better type safety
import java.util.Map; // Import Map for appender retrieval
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender; // Import Appender for type safety
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration; // Import Configuration
import utils.InfoViewLogAppender;

/**
 * Optimized adapter for Log4j2 logging functionality.
 * This class aims to reduce overhead and improve efficiency,
 * especially during appended retrieval.
 *
 * @author adrest18
 */
public class Log4jAdapter implements ILog4jAdapter {

    private final LoggerContext loggerContext;
    private volatile InfoViewLogAppender infoViewLogAppenderInstance; // Volatile for thread safety and lazy init

    /**
     * Constructs a Log4jAdapter, initializing the Log4j2 LoggerContext
     * with the specified configuration file.
     *
     * @param log4j2PathAndFullName The full path and name of the Log4j2 configuration file.
     * @throws FileNotFoundException If the specified configuration file does not exist.
     */
    public Log4jAdapter(String log4j2PathAndFullName) throws FileNotFoundException {
        // Get the LoggerContext. Using `false` means we don't force initialization
        // if it's not already started, giving us control.
        loggerContext = (LoggerContext) LogManager.getContext(false);

        File configFile = new File(log4j2PathAndFullName);
        if (configFile.exists()) {
            URI configUri = configFile.toURI();
            loggerContext.setConfigLocation(configUri);
            // Explicitly reconfigure the context to apply the new configuration.
            // This is crucial if the context was already initialized with a default config.
            loggerContext.reconfigure();
        } else {
            // Use Log4j2 for internal logging if possible, but fallback to System.out
            // if the logging system itself isn't fully configured yet.
            Logger internalLogger = LogManager.getLogger(Log4jAdapter.class);
            if (internalLogger != null && internalLogger.isErrorEnabled()) {
                 internalLogger.error("Log4j2 configuration file not found: {}", configFile.getAbsolutePath());
            } else {
                System.err.println("ERROR: Log4j2 configuration file not found: " + configFile.getAbsolutePath());
            }
            throw new FileNotFoundException("Log4j2 configuration file not found at: " + configFile.getAbsolutePath());
        }
    }

    /**
     * Retrieves a Log4j2 Logger instance by name.
     *
     * @param loggerName The name of the logger to retrieve.
     * @return The Log4j2 Logger instance.
     */
    @Override
    public Logger getLogger(String loggerName) {
        // This method is already efficient as Log4j2 caches loggers internally.
        return loggerContext.getLogger(loggerName);
    }

    /**
     * Retrieves the InfoViewLogAppender instance.
     * This method uses lazy initialization and double-checked locking
     * for thread-safe and efficient retrieval of the appended.
     *
     * @return The InfoViewLogAppender instance, or null if not found or not of the correct type.
     */
    @Override
    @SuppressWarnings("DoubleCheckedLocking")
    public InfoViewLogAppender getInfoViewLogAppender() {
        // Use double-checked locking for lazy, thread-safe initialization
        if (infoViewLogAppenderInstance == null) {
            synchronized (this) { 
                // Synchronize on the instance to prevent multiple initializations
                if (infoViewLogAppenderInstance == null) {
                    try {
                        // Get the current configuration from the loggerContext
                        Configuration config = loggerContext.getConfiguration();
                        if (config != null) {
                            // Get all appenders from the configuration
                            Map<String, Appender> appenders = config.getAppenders();
                            Appender appender = appenders.get("InfoViewLogAppender");

                            if (appender instanceof InfoViewLogAppender infoViewLogAppender) {
                                this.infoViewLogAppenderInstance = infoViewLogAppenderInstance = infoViewLogAppender;
                            } else if (appender != null) {
                                // Log a warning if the appender exists but is of the wrong type
                                Logger internalLogger = loggerContext.getLogger(Log4jAdapter.class);
                                if (internalLogger.isWarnEnabled()) {
                                    internalLogger.warn("Appender named 'InfoViewLogAppender' found, but it is not an instance of InfoViewLogAppender. Actual type: {}", appender.getClass().getName());
                                }
                            } else {
                                // Log a warning if the appender is not found
                                Logger internalLogger = loggerContext.getLogger(Log4jAdapter.class);
                                if (internalLogger.isWarnEnabled()) {
                                    internalLogger.warn("Appender named 'InfoViewLogAppender' not found in the Log4j2 configuration.");
                                }
                            }
                        } else {
                            Logger internalLogger = loggerContext.getLogger(Log4jAdapter.class);
                            if (internalLogger.isErrorEnabled()) {
                                internalLogger.error("Log4j2 Configuration is null. Cannot retrieve appenders.");
                            }
                        }
                    } catch (Exception e) {
                        // Catch any unexpected exceptions during appender retrieval
                        Logger internalLogger = loggerContext.getLogger(Log4jAdapter.class);
                        if (internalLogger.isErrorEnabled()) {
                            internalLogger.error("An error occurred while trying to retrieve InfoViewLogAppender", e);
                        }
                        // Optionally re-throw or handle more gracefully depending on requirements
                    }
                }
            }
        }
        return infoViewLogAppenderInstance;
    }
}