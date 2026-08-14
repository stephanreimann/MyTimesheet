/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalTime;
import java.util.Objects;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author adrest18
 */
public class WorkItemTrackingData {
    
    private final StringProperty shortcut = new SimpleStringProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final ObjectProperty<LocalTime> starttime = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalTime> endtime = new SimpleObjectProperty<>();    

    public WorkItemTrackingData(String shortcut, String name, LocalTime starttime, LocalTime endtime) {
        this.shortcut.setValue(shortcut);
        this.name.setValue(name);
        this.starttime.setValue(starttime);
        this.endtime.setValue(endtime);
    }

    // <editor-fold defaultstate="collapsed" desc="Shortcut Property">
    public String getShortcut() {
        return shortcut.get();
    }
    
    public void setShortcut(String value) {
        shortcut.set(value);
    }
    
    public StringProperty getShortcutProperty() {
        return shortcut;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Name Property">
    public String getName() {
        return name.get();
    }
    
    public void setName(String value) {
        name.set(value);
    }
    
    public StringProperty getNameProperty() {
        return name;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="StartTime Property">
    public LocalTime getStartTime() {
        return starttime.get();
    }
    
    public void setStartTime(LocalTime value) {
        starttime.set(value);
    }
    
    public ObjectProperty<LocalTime> getStartTimeProperty() {
        return starttime;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EndTime Property">
    public LocalTime getEndTime() {
        return endtime.get();
    }
    
    public void setEndTime(LocalTime value) {
        endtime.set(value);
    }
    
    public ObjectProperty<LocalTime> getEndTimeProperty() {
        return endtime;
    }
    // </editor-fold>
    
    @Override
    public int hashCode() {
        int hash = 4;
        hash = 53 * hash + Objects.hashCode(this.shortcut);
        hash = 53 * hash + Objects.hashCode(this.name);
        hash = 53 * hash + Objects.hashCode(this.starttime);
        hash = 53 * hash + Objects.hashCode(this.endtime);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WorkItemTrackingData other = (WorkItemTrackingData) obj;
        if (!this.shortcut.getValue().equals(other.shortcut.getValue())) {
            return false;
        }
        if (!this.name.getValue().equals(other.name.getValue())) {
            return false;
        }
        if (!this.starttime.getValue().equals(other.starttime.getValue())) {
            return false;
        }
        return this.endtime.getValue().equals(other.endtime.getValue());
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(shortcut.getValue()).append(", ");
        sb.append(name.getValue()).append(", ");
        sb.append(starttime.getValue()).append(", ");
        sb.append(endtime.getValue()).append(", ");
        return sb.toString();
    }

}
