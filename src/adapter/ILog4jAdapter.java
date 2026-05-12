/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package adapter;

import org.apache.logging.log4j.Logger;
import utils.InfoViewLogAppender;

/**
 *
 * @author adrest18
 */
interface ILog4jAdapter {
    
    /**
     * Retrieves a Log4j2 Logger instance by name.
     *
     * @param loggerName The name of the logger to retrieve.
     * @return The Log4j2 Logger instance.
     */
    public Logger getLogger(String loggerName);

    /**
     * Retrieves the InfoViewLogAppender instance.
     * This method uses lazy initialization and double-checked locking
     * for thread-safe and efficient retrieval of the appended.
     *
     * @return The InfoViewLogAppender instance, or null if not found or not of the correct type.
     */
    public InfoViewLogAppender getInfoViewLogAppender();
    
}
