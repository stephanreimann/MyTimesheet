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
public class Worklocation {
 
    private final LongProperty id = new SimpleLongProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();

    public Worklocation(Long id) {
        this.id.set(id);
    }
    
    public Worklocation(long id, String name, String description) {
        this.id.set(id);
        this.name.set(name);
        this.description.set(description);
    }
    
    public Worklocation(Worklocation worklocation) {
        this.id.set(worklocation.getId());
        this.name.set(worklocation.getName());
        this.description.set(worklocation.getDescription());
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
        int hash = 1;
        hash = 53 * hash + Objects.hashCode(this.id);
        hash = 53 * hash + Objects.hashCode(this.name);
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
        final Worklocation other = (Worklocation) obj;
        if (!this.id.getValue().equals(other.id.getValue())) {
            return false;
        }
        if (!this.name.getValue().equals(other.name.getValue())) {
            return false;
        }
        return this.description.getValue().equals(other.description.getValue());
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(/*Worklocation.class.getSimpleName()*/);
        //sb.append(": ");
        //sb.append(id.getValue()).append(", ");
        sb.append(name.getValue());//.append(", ");
        //sb.append(description.getValue()).append("");
        return sb.toString();
    }
    
}
