/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.text.DateFormat;
import javafx.collections.*;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.*;

/**
 *
 * @author adrest18
 */
@Plugin(
  name = "InfoViewLogAppender", 
  category = Core.CATEGORY_NAME, 
  elementType = Appender.ELEMENT_TYPE)
public class InfoViewLogAppender extends AbstractAppender {
    
    private final ObservableList<String> infoViewEntries = FXCollections.observableArrayList();   

    protected InfoViewLogAppender(String name, Filter filter) {
        super(name, filter, null, true, null);
    }

    public ObservableList<String> GetInfoViewEntries() {
        return infoViewEntries;
    }
    
    @PluginFactory
    public static InfoViewLogAppender createAppender(
      @PluginAttribute("name") String name, 
      @PluginElement("Filter") Filter filter) {
        return new InfoViewLogAppender(name, filter);
    }

    @Override
    public void append(LogEvent event) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(millisToDate(event.getTimeMillis())).append(" ");
        sb.append(millisToTime(event.getTimeMillis())).append(" ");
        sb.append("]\t");
        sb.append(event.getLevel()).append("\t\t");
        sb.append(event.getMessage().getFormattedMessage());
        infoViewEntries.add(sb.toString());
    }
    
    private String millisToDate(long millis) {
        return DateFormat.getDateInstance(DateFormat.LONG).format(millis);
    }

    private String millisToTime(long millis) {
        return DateFormat.getTimeInstance(DateFormat.LONG).format(millis);
    }

}
