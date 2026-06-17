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
    private final ObjectProperty<User> user = new SimpleObjectProperty<>();
    private final ObjectProperty<Workrecord> workrecord = new SimpleObjectProperty<>();
    private final ObjectProperty<Sprint> sprint = new SimpleObjectProperty<>();
    private final ObjectProperty<TrackingItem> trackingItem = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalTime> starttime = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalTime> endtime = new SimpleObjectProperty<>();
    private final StringProperty description = new SimpleStringProperty();
    
    public WorkItem(Long id) {
        this.id.set(id);
    }
    
    public WorkItem(Long id, User user, Workrecord workrecord, Sprint sprint, 
                TrackingItem trackingItem, LocalTime starttime, LocalTime endtime, 
                String description) {
        this.id.set(id);
        this.user.set(user);
        this.workrecord.set(workrecord);
        this.sprint.set(sprint);
        this.trackingItem.set(trackingItem);
        this.starttime.set(starttime);
        this.endtime.set(endtime);
        this.description.set(description);
    }
    
    public WorkItem(WorkItem workitem) {
        this.id.set(workitem.getId());
        this.user.set(workitem.getUser());
        this.workrecord.set(workitem.getWorkrecord());
        this.sprint.set(workitem.getSprint());
        this.trackingItem.set(workitem.getTrackingItem());
        this.starttime.set(workitem.getStartTime());
        this.endtime.set(workitem.getEndTime());
        this.description.set(workitem.getDescription());
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
    // <editor-fold defaultstate="collapsed" desc="User Property">
    public User getUser() {
        return user.get();
    }
    
    public void setUser(User value) {
        user.set(value);
    }
    
    public ObjectProperty<User> getUserProperty() {
        return user;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Workrecord Property">
    public Workrecord getWorkrecord() {
        return workrecord.get();
    }
    
    public void setWorkrecord(Workrecord value) {
        workrecord.set(value);
    }
    
    public ObjectProperty<Workrecord> getWorkrecordProperty() {
        return workrecord;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Sprint Property">
    public Sprint getSprint() {
        return sprint.get();
    }
    
    public void setSprint(Sprint value) {
        sprint.set(value);
    }
    
    public ObjectProperty<Sprint> getSprintProperty() {
        return sprint;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TrackingItem Property">
    public TrackingItem getTrackingItem() {
        return trackingItem.get();
    }
    
    public void setTrackingItem(TrackingItem value) {
        trackingItem.set(value);
    }
    
    public ObjectProperty<TrackingItem> getTrackingItemProperty() {
        return trackingItem;
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

    @Override
    public int hashCode() {
        int hash = 4;
        hash = 53 * hash + Objects.hashCode(this.id);
        hash = 53 * hash + Objects.hashCode(this.user);
        hash = 53 * hash + Objects.hashCode(this.workrecord);
        hash = 53 * hash + Objects.hashCode(this.sprint);
        hash = 53 * hash + Objects.hashCode(this.trackingItem);
        hash = 53 * hash + Objects.hashCode(this.starttime);
        hash = 53 * hash + Objects.hashCode(this.endtime);
        hash = 53 * hash + Objects.hashCode(this.description);
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
        if(this.user.getValue() == null || 
           this.workrecord.getValue() == null ||
           this.sprint.getValue() == null ||
           this.trackingItem.getValue() == null) {
            return false;
        }
        if (!this.user.getValue().equals(other.user.getValue())) {
            return false;
        }
        if (!this.workrecord.getValue().equals(other.workrecord.getValue())) {
            return false;
        }
        if (!this.sprint.getValue().equals(other.sprint.getValue())) {
            return false;
        }
        if (!this.trackingItem.getValue().equals(other.trackingItem.getValue())) {
            return false;
        } 
        if (!this.starttime.getValue().equals(other.starttime.getValue())) {
            return false;
        }
        if (!this.endtime.getValue().equals(other.endtime.getValue())) {
            return false;
        }
        return this.description.getValue().equals(other.description.getValue());
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(id.getValue()).append(", ");
        sb.append(user.getValue().getId()).append(", ");
        sb.append(workrecord.getValue().getId()).append(", ");
        sb.append(sprint.getValue().getId()).append(", ");
        sb.append(trackingItem.getValue().getId()).append(", ");
        sb.append(starttime.getValue()).append(", ");
        sb.append(endtime.getValue()).append(", ");
        sb.append(description.getValue()).append("");
        return sb.toString();
    }

}
