/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.Objects;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author adrest18
 */
public class TrackingItem {

    private final LongProperty id = new SimpleLongProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty shortcut = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    
    public TrackingItem(Long id) {
        this.id.set(id);
    }
    
    public TrackingItem(Long id, String name, String shortcut, String description) {
        this.id.set(id);
        this.name.set(name);
        this.shortcut.set(shortcut);
        this.description.set(description);
    }

    public TrackingItem(TrackingItem trakingItem) {
        this.id.set(trakingItem.getId());
        this.name.set(trakingItem.getName());
        this.shortcut.set(trakingItem.getShortcut());
        this.description.set(trakingItem.getDescription());
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
    // <editor-fold defaultstate="collapsed" desc="Name Shortcut">
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
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.id);
        hash = 53 * hash + Objects.hashCode(this.name);
        hash = 53 * hash + Objects.hashCode(this.shortcut);
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
        final TrackingItem other = (TrackingItem) obj;
        if (!this.id.getValue().equals(other.id.getValue())) {
            return false;
        }
        if (!this.name.getValue().equals(other.name.getValue())) {
            return false;
        }
        if (!this.shortcut.getValue().equals(other.shortcut.getValue())) {
            return false;
        }
        return this.description.getValue().equals(other.description.getValue());
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name.getValue());
        return sb.toString();
    }
    
}
