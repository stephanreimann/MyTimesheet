/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sqlite;

import adapter.Log4jAdapter;
import java.io.File;
import java.sql.*;
import java.util.ResourceBundle;
import org.apache.logging.log4j.*;
import properties.TranslationStringProperty;

/**
 *
 * @author adrest18
 */
public class ConnectionFactory {

    private final Logger log;
    private final ResourceBundle resourceBundle;
    private final TranslationStringProperty logMessage;
    private static java.sql.Connection connection;
       
    public ConnectionFactory(ResourceBundle resourceBundle, Log4jAdapter log4jAdapter) {
        if(resourceBundle == null) throw new NullPointerException("rb");
        if(log4jAdapter == null) throw new NullPointerException("log4jAdapter");
        
        log = LogManager.getLogger(ConnectionFactory.class.getName());
        logMessage = new TranslationStringProperty(log4jAdapter);
        this.resourceBundle = resourceBundle;
    }
    
    public Connection getConnection(String filePathAndFullName) {
        if(connection == null) {
            if(exists(filePathAndFullName)) {
                connection = tryGetConnection(filePathAndFullName);
            }
        }
        return connection;
    }
    
    private Boolean exists(String filePathAndFullName) {
        File file = new File(filePathAndFullName);
        return file.exists();
    }
    
    private Connection tryGetConnection(String filePathAndFullName) {
        synchronized (ConnectionFactory.class) {
            try {
                connection = DriverManager.getConnection("jdbc:sqlite:" + filePathAndFullName);
                logMessage.translate("ConnectionOpened", new Object[] { filePathAndFullName }, resourceBundle);
            } catch (SQLException ex) {
                log.fatal(ConnectionFactory.class.getName(), ex.fillInStackTrace());
                return null;
            }
        }
        return connection;
    }
    
}
