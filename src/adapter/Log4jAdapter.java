/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package adapter;

import java.io.*;
import org.apache.logging.log4j.*;
import org.apache.logging.log4j.core.LoggerContext;
import utils.InfoViewLogAppender;

/**
 *
 * @author adrest18
 */
public class Log4jAdapter implements ILog4jAdapter {
    
    private final LoggerContext loggerContext;
    
    public Log4jAdapter(String log4j2PathAndFullName) throws FileNotFoundException {
        loggerContext = (LoggerContext)LogManager.getContext(false);
        File configFile = new File(log4j2PathAndFullName);
        if(configFile.exists()) {
            loggerContext.setConfigLocation(configFile.toURI());           
        } else {
            System.out.println("FileNotFoundException:" + configFile.getAbsolutePath());
            throw new FileNotFoundException(configFile.getAbsolutePath());
        }
    }

    @Override
    public Logger getLogger(String loggerName) {
        return loggerContext.getLogger(loggerName);
    }

    @Override
    public InfoViewLogAppender getInfoViewLogAppender() {
        return (InfoViewLogAppender)((org.apache.logging.log4j.core.Logger)LogManager.getLogger()).getAppenders().get("InfoViewLogAppender");
    }
    
}
