/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package properties;

import adapter.Log4jAdapter;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author ADREST18
 */
public class TranslationStringProperty extends SimpleStringProperty {

    private final Logger log;

    public TranslationStringProperty(Log4jAdapter log4jAdapter) {
        this.log = log4jAdapter.getLogger(TranslationStringProperty.class.getName());
    }
    
    public void translate(String key, Object[] args, ResourceBundle rb) {
        if (key != null) {
            if (rb != null && rb.containsKey(key)) {
                String value = rb.getString(key);
                String formatted = MessageFormat.format(value, args);
                log.info(formatted);
                super.set(formatted);
            } else {
                super.set("Undef");
            }
        }
    }

    public void translate(String key, ResourceBundle rb) {
        if (key != null) {
            if (rb != null && rb.containsKey(key)) {
                String value = rb.getString(key);
                log.info(value);
                super.set(value);
            } else {
                super.set("Undef");
            }
        }
    }

}
