/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDate;
import java.util.Objects;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 *
 * @author adrest18
 */
public class Sprint {
    
    private final LongProperty id = new SimpleLongProperty();
    private final ObjectProperty<LocalDate> startdate = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> enddate = new SimpleObjectProperty<>();
    private final ObjectProperty<Integer> numberofsprintdays = new SimpleObjectProperty<>();

    public Sprint(Long id) {
        this.id.set(id);
    }
    
    public Sprint(Long id, LocalDate startdate, LocalDate enddate, Integer numberofsprintdays) {
        this.id.set(id);
        this.startdate.set(startdate);
        this.enddate.set(enddate);
        this.numberofsprintdays.set(numberofsprintdays);
    }
    
    public Sprint(Sprint sprint) {
        this.id.set(sprint.getId());
        this.startdate.set(sprint.getStartDate());
        this.enddate.set(sprint.getEndDate());
        this.numberofsprintdays.set(sprint.getNumberOfSprintDays());
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
    // <editor-fold defaultstate="collapsed" desc="StartDate Property">
    public LocalDate getStartDate() {
        return startdate.get();
    }
    
    public void setStartDate(LocalDate value) {
        startdate.set(value);
    }
    
    public ObjectProperty<LocalDate> getStartDateProperty() {
        return startdate;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EndDate Property">
    public LocalDate getEndDate() {
        return enddate.get();
    }
    
    public void setEndDate(LocalDate value) {
        enddate.set(value);
    }
    
    public ObjectProperty<LocalDate> getEndDateProperty() {
        return enddate;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="NumberOfSprintDays Property">
    public Integer getNumberOfSprintDays() {
        return numberofsprintdays.get();
    }
    
    public void setNumberOfSprintDays(Integer value) {
        numberofsprintdays.set(value);
    }
    
    public ObjectProperty<Integer> getNumberOfSprintDaysProperty() {
        return numberofsprintdays;
    }
    // </editor-fold>
   
    @Override
    public int hashCode() {
        int hash = 4;
        hash = 53 * hash + Objects.hashCode(this.id);
        hash = 53 * hash + Objects.hashCode(this.startdate);
        hash = 53 * hash + Objects.hashCode(this.enddate);
        hash = 53 * hash + Objects.hashCode(this.numberofsprintdays);
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
        final Sprint other = (Sprint) obj;
        if (!this.id.getValue().equals(other.id.getValue())) {
            return false;
        }
        if(this.startdate.getValue() == null || 
           this.enddate.getValue() == null ||
           this.numberofsprintdays.getValue() == null) {
            return false;
        }
        if (!this.startdate.getValue().equals(other.startdate.getValue())) {
            return false;
        }
        if (!this.enddate.getValue().equals(other.enddate.getValue())) {
            return false;
        }
        return this.numberofsprintdays.getValue().equals(other.numberofsprintdays.getValue());
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(id.getValue()).append("");
        return sb.toString();
    }
    
}
