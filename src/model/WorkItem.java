/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalTime;
import java.util.Objects;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author adrest18
 */
public class WorkItem {

    private final LongProperty id = new SimpleLongProperty();
    private final LongProperty workrecordId = new SimpleLongProperty();
    private final LongProperty sprintId = new SimpleLongProperty();
    private final LongProperty trackingItemId = new SimpleLongProperty();
    private final ObjectProperty<LocalTime> starttime = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalTime> endtime = new SimpleObjectProperty<>();
    private final StringProperty description = new SimpleStringProperty();
    private final StringProperty shortcut = new SimpleStringProperty();
    private final StringProperty name = new SimpleStringProperty();
    
    public WorkItem(Long id) {
        this.id.set(id);
    }
    
    public WorkItem(Long id, Long workrecordId, Long sprintId, 
                Long trackingItemId, LocalTime starttime, LocalTime endtime, 
                String description, String shortcut, String name) {
        this.id.set(id);
        this.workrecordId.set(workrecordId);
        this.sprintId.set(sprintId);
        this.trackingItemId.set(trackingItemId);
        this.starttime.set(starttime);
        this.endtime.set(endtime);
        this.description.set(description);
        this.shortcut.set(shortcut);
        this.name.set(name);
    }
    
    public WorkItem(WorkItem workitem) {
        this.id.set(workitem.getId());
        this.workrecordId.set(workitem.getWorkrecordId());
        this.sprintId.set(workitem.getSprintId());
        this.trackingItemId.set(workitem.getTrackingItemId());
        this.starttime.set(workitem.getStartTime());
        this.endtime.set(workitem.getEndTime());
        this.description.set(workitem.getDescription());
        this.shortcut.set(workitem.getShortcut());
        this.name.set(workitem.getName());
    }

    // <editor-fold defaultstate="collapsed" desc="Id Property">
    public Long getId() {
        return id.get();
    }
    
    public void setId(Long value) {
        id.set(value);
    }
    
    public LongProperty getIdProperty() {
        return id;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="WorkrecordId Property">
    public Long getWorkrecordId() {
        return workrecordId.get();
    }
    
    public void setWorkrecord(Long value) {
        workrecordId.set(value);
    }
    
    public LongProperty getWorkrecordProperty() {
        return workrecordId;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="SprintId Property">
    public Long getSprintId() {
        return sprintId.get();
    }
    
    public void setSprintId(Long value) {
        sprintId.set(value);
    }
    
    public LongProperty getSprintIdProperty() {
        return sprintId;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TrackingItemId Property">
    public Long getTrackingItemId() {
        return trackingItemId.get();
    }
    
    public void setTrackingItemId(Long value) {
        trackingItemId.set(value);
    }
    
    public LongProperty getTrackingItemIdProperty() {
        return trackingItemId;
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
    // <editor-fold defaultstate="collapsed" desc="Description Property">
    public String getDescription() {
        return description.get();
    }
    
    public void setDescription(String value) {
        description.set(value);
    }
    
    public StringProperty getDescriptionProperty() {
        return description;
    }
    // </editor-fold>
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

    @Override
    public int hashCode() {
        int hash = 4;
        hash = 53 * hash + Objects.hashCode(this.id);
        hash = 53 * hash + Objects.hashCode(this.workrecordId);
        hash = 53 * hash + Objects.hashCode(this.sprintId);
        hash = 53 * hash + Objects.hashCode(this.trackingItemId);
        hash = 53 * hash + Objects.hashCode(this.starttime);
        hash = 53 * hash + Objects.hashCode(this.endtime);
        hash = 53 * hash + Objects.hashCode(this.description);
        hash = 53 * hash + Objects.hashCode(this.shortcut);
        hash = 53 * hash + Objects.hashCode(this.name);
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
        final WorkItem other = (WorkItem) obj;
        if (!this.id.getValue().equals(other.id.getValue())) {
            return false;
        }
        if(this.workrecordId.getValue() == null ||
           this.sprintId.getValue() == null ||
           this.trackingItemId.getValue() == null) {
            return false;
        }
        if (!this.workrecordId.getValue().equals(other.workrecordId.getValue())) {
            return false;
        }
        if (!this.sprintId.getValue().equals(other.sprintId.getValue())) {
            return false;
        }
        if (!this.trackingItemId.getValue().equals(other.trackingItemId.getValue())) {
            return false;
        } 
        if (!this.starttime.getValue().equals(other.starttime.getValue())) {
            return false;
        }
        if (!this.endtime.getValue().equals(other.endtime.getValue())) {
            return false;
        }
        if (!this.shortcut.getValue().equals(other.shortcut.getValue())) {
            return false;
        }
        if (!this.name.getValue().equals(other.name.getValue())) {
            return false;
        }
        return this.description.getValue().equals(other.description.getValue());
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(id.getValue()).append(", ");
        sb.append(workrecordId.getValue()).append(", ");
        sb.append(sprintId.getValue()).append(", ");
        sb.append(trackingItemId.getValue()).append(", ");
        sb.append(starttime.getValue()).append(", ");
        sb.append(endtime.getValue()).append(", ");
        sb.append(description.getValue()).append(", ");
        sb.append(shortcut.getValue()).append(", ");
        sb.append(name.getValue()).append("");
        return sb.toString();
    }

}
