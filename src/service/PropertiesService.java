/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import java.io.*;
import java.util.Properties;
import org.apache.logging.log4j.*;

/**
 * This class handles the application properties. Properties will be read/save
 * into "properties.xml" file.
 *
 * @author ADREST18
 */
public class PropertiesService extends Properties {

    private final Logger log = LogManager.getLogger(PropertiesService.class.getName());
    private static PropertiesService instance;

    private PropertiesService() {

    }

    public static PropertiesService getInstance() {
        synchronized (PropertiesService.class) {
            if (PropertiesService.instance == null) {
                PropertiesService.instance = new PropertiesService();
            }
        }
        return PropertiesService.instance;
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        if (this.containsKey(key)) {
            String value = super.getProperty(key);
            return value;
        }
        return defaultValue;
    }

    @Override
    public Object setProperty(String key, String value) {
        return super.setProperty(key, value);
    }

    public void loadPropertiesFromXmlFile(String fileName) {
        try {
            File file = new File(fileName);
            try (FileInputStream fis = new FileInputStream(file)) {
                loadFromXML(fis);
            }
        } catch (FileNotFoundException ex) {
            System.out.println("FileNotFoundException:" + ex.getMessage());            
            log.fatal(instance, ex);
        } catch (IOException ex) {
            log.fatal(instance, ex);
        }
    }

    public void savePropertiesToXmlFile(String fileName) {
        try {
            File file = new File(fileName);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                storeToXML(fos, "Application Properties");
            }
        } catch (FileNotFoundException ex) {
            log.fatal(instance, ex);
        } catch (IOException ex) {
            log.fatal(instance, ex);
        }
    }

}
