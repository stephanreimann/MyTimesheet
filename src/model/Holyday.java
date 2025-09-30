/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDate;
import java.util.*;
import javafx.beans.property.*;

/**
 *
 * @author adrest18
 */
public class Holyday {

    private final LongProperty id = new SimpleLongProperty();
    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty state = new SimpleStringProperty();
    
    public Holyday(Long id) {
        this.id.set(id);
    }
    
    public Holyday(LocalDate date, String name, String state) {
        this.id.set(0L);
        this.date.set(date);
        this.name.set(name);
        this.state.set(state);
    }
    
    public Holyday(Long id, LocalDate date, String name, String state) {
        this.id.set(id);
        this.date.set(date);
        this.name.set(name);
        this.state.set(state);
    }

    public Holyday(Holyday holyday) {
        this.id.set(holyday.getId());
        this.date.set(holyday.getDate());
        this.name.set(holyday.getName());
        this.state.set(holyday.getState());
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
    // <editor-fold defaultstate="collapsed" desc="Date Property">
    public LocalDate getDate() {
        return date.get();
    }
    
    public void setDate(LocalDate value) {
        date.set(value);
    }
    
    public ObjectProperty<LocalDate> getDateProperty() {
        return date;
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
    // <editor-fold defaultstate="collapsed" desc="State Property">
    public String getState() {
        return state.get();
    }
    
    public void setState(String value) {
        state.set(value);
    }
    
    public StringProperty getStateProperty() {
        return state;
    }
    // </editor-fold>
   
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.id);
        hash = 53 * hash + Objects.hashCode(this.date);
        hash = 53 * hash + Objects.hashCode(this.name);
        hash = 53 * hash + Objects.hashCode(this.state);
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
        final Holyday other = (Holyday) obj;
        if (!this.date.getValue().equals(other.date.getValue())) {
            return false;
        }
        if (!this.name.getValue().equals(other.name.getValue())) {
            return false;
        }
        return this.state.getValue().equals(other.state.getValue());
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(date.getValue()).append(", ");
        sb.append(name.getValue()).append(", ");
        sb.append(state.getValue());
        return sb.toString();
    }
    
}
