/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalTime;
import java.util.Objects;
import javafx.beans.property.*;

/**
 *
 * @author adrest18
 */
public class Contract {

    private final LongProperty id = new SimpleLongProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final LongProperty workhours = new SimpleLongProperty();
    private final LongProperty maxworkhours = new SimpleLongProperty();
    private final LongProperty vacationdays = new SimpleLongProperty();
    private final StringProperty vacationreconciliationdate = new SimpleStringProperty();
    private final ObjectProperty<LocalTime> breakfastofftimeend = new SimpleObjectProperty();
    private final ObjectProperty<LocalTime> breakfastofftimestart = new SimpleObjectProperty();
    private final ObjectProperty<LocalTime> lunchofftimeend = new SimpleObjectProperty();
    private final ObjectProperty<LocalTime> lunchofftimestart = new SimpleObjectProperty();
    private final ObjectProperty<LocalTime> earliestworktimestart = new SimpleObjectProperty();
    private final ObjectProperty<LocalTime> latestworktimeend = new SimpleObjectProperty();
      
    public Contract(Long id) {
        this.id.set(id);
    }
    
    public Contract(Long id, String name, Long workhours, Long maxworkhours, 
            Long vacationdays, String vacationreconciliationdate, LocalTime breakfastofftimeend, 
            LocalTime breakfastofftimestart, LocalTime lunchofftimeend, LocalTime lunchofftimestart,
            LocalTime earliestworktimestart, LocalTime latestworktimeend) {
        this.id.set(id);
        this.name.set(name);
        this.workhours.set(workhours);
        this.maxworkhours.set(maxworkhours);
        this.vacationdays.set(vacationdays);
        this.vacationreconciliationdate.set(vacationreconciliationdate);
        this.breakfastofftimeend.set(breakfastofftimeend);
        this.breakfastofftimestart.set(breakfastofftimestart);
        this.lunchofftimeend.set(lunchofftimeend);
        this.lunchofftimestart.set(lunchofftimestart);
        this.earliestworktimestart.set(earliestworktimestart);
        this.latestworktimeend.set(latestworktimeend);
    }

    public Contract(Contract contract) {
        this.id.set(contract.getId());
        this.name.set(contract.getName());
        this.workhours.set(contract.getWorkhours());
        this.maxworkhours.set(contract.getMaxworkhours());
        this.vacationdays.set(contract.getVacationdays());
        this.vacationreconciliationdate.set(contract.getVacationreconciliationdate());
        this.breakfastofftimeend.set(contract.getBreakfastofftimeend());
        this.breakfastofftimestart.set(contract.getBreakfastofftimestart());
        this.lunchofftimeend.set(contract.getLunchofftimeend());
        this.lunchofftimestart.set(contract.getLunchofftimestart());
        this.earliestworktimestart.set(contract.getEarliestworktimestart());
        this.latestworktimeend.set(contract.getLatestworktimeend());
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
    // <editor-fold defaultstate="collapsed" desc="Workhours Property">
    public Long getWorkhours() {
        return workhours.get();
    }
    
    public void setWorkhours(Long value) {
        workhours.set(value);
    }
    
    public LongProperty getWorkhoursProperty() {
        return workhours;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Maxworkhours Property">
    public Long getMaxworkhours() {
        return maxworkhours.get();
    }
    
    public void setMaxworkhours(Long value) {
        maxworkhours.set(value);
    }
    
    public LongProperty getMaxworkhoursProperty() {
        return maxworkhours;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Vacationdays Property">
    public Long getVacationdays() {
        return vacationdays.get();
    }
    
    public void setVacationdays(Long value) {
        vacationdays.set(value);
    }
    
    public LongProperty getVacationdaysProperty() {
        return vacationdays;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Vacationreconciliationdate Property">
    public String getVacationreconciliationdate() {
        return vacationreconciliationdate.get();
    }
    
    public void setVacationreconciliationdate(String value) {
        vacationreconciliationdate.set(value);
    }
    
    public StringProperty getVacationreconciliationdateProperty() {
        return vacationreconciliationdate;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Breakfastofftimeend Property">
    public LocalTime getBreakfastofftimeend() {
        return breakfastofftimeend.get();
    }
    
    public void setBreakfastofftimeend(LocalTime value) {
        breakfastofftimeend.set(value);
    }
    
    public ObjectProperty<LocalTime> getBreakfastofftimeProperty() {
        return breakfastofftimeend;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Breakfastofftimestart Property">
    public LocalTime getBreakfastofftimestart() {
        return breakfastofftimestart.get();
    }
    
    public void setBreakfastofftimestart(LocalTime value) {
        breakfastofftimestart.set(value);
    }
    
    public ObjectProperty<LocalTime> getBreakfastofftimestartProperty() {
        return breakfastofftimestart;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Lunchofftimeend Property">
    public LocalTime getLunchofftimeend() {
        return lunchofftimeend.get();
    }
    
    public void setLunchofftimeend(LocalTime value) {
        lunchofftimeend.set(value);
    }
    
    public ObjectProperty<LocalTime> getLunchofftimeProperty() {
        return lunchofftimeend;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Lunchofftimestart Property">
    public LocalTime getLunchofftimestart() {
        return lunchofftimestart.get();
    }
    
    public void setLunchofftimestart(LocalTime value) {
        lunchofftimestart.set(value);
    }
    
    public ObjectProperty<LocalTime> getLunchofftimestartProperty() {
        return lunchofftimestart;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Earliestworktimestart Property">
    public LocalTime getEarliestworktimestart() {
        return earliestworktimestart.get();
    }
    
    public void setEarliestworktimestart(LocalTime value) {
        earliestworktimestart.set(value);
    }
    
    public ObjectProperty<LocalTime> getEarliestworktimestartProperty() {
        return earliestworktimestart;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Latestworktimeend Property">
    public LocalTime getLatestworktimeend() {
        return latestworktimeend.get();
    }
    
    public void setLatestworktimeend(LocalTime value) {
        latestworktimeend.set(value);
    }
    
    public ObjectProperty<LocalTime> getLatestworktimeendProperty() {
        return latestworktimeend;
    }
    // </editor-fold>

    @Override
    public int hashCode() {
        int hash = 2;
        hash = 53 * hash + Objects.hashCode(this.id);
        hash = 53 * hash + Objects.hashCode(this.name);
        hash = 53 * hash + Objects.hashCode(this.workhours);
        hash = 53 * hash + Objects.hashCode(this.maxworkhours);
        hash = 53 * hash + Objects.hashCode(this.vacationdays);
        hash = 53 * hash + Objects.hashCode(this.vacationreconciliationdate);
        hash = 53 * hash + Objects.hashCode(this.breakfastofftimeend);
        hash = 53 * hash + Objects.hashCode(this.breakfastofftimestart);
        hash = 53 * hash + Objects.hashCode(this.lunchofftimeend);
        hash = 53 * hash + Objects.hashCode(this.lunchofftimestart);
        hash = 53 * hash + Objects.hashCode(this.earliestworktimestart);
        hash = 53 * hash + Objects.hashCode(this.latestworktimeend);
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
        final Contract other = (Contract) obj;
        if (!this.id.getValue().equals(other.id.getValue())) {
            return false;
        }
        if (!this.name.getValue().equals(other.name.getValue())) {
            return false;
        }
        if (!this.workhours.getValue().equals(other.workhours.getValue())) {
            return false;
        }
        if (!this.maxworkhours.getValue().equals(other.maxworkhours.getValue())) {
            return false;
        }
        if (!this.vacationdays.getValue().equals(other.vacationdays.getValue())) {
            return false;
        }
        if (!this.vacationreconciliationdate.getValue().equals(other.vacationreconciliationdate.getValue())) {
            return false;
        }
        if (!this.breakfastofftimeend.getValue().equals(other.breakfastofftimeend.getValue())) {
            return false;
        }
        if (!this.breakfastofftimestart.getValue().equals(other.breakfastofftimestart.getValue())) {
            return false;
        }
        if (!this.lunchofftimeend.getValue().equals(other.lunchofftimeend.getValue())) {
            return false;
        }
        if (!this.lunchofftimestart.getValue().equals(other.lunchofftimestart.getValue())) {
            return false;
        }
        if (!this.earliestworktimestart.getValue().equals(other.earliestworktimestart.getValue())) {
            return false;
        }
        return this.latestworktimeend.getValue().equals(other.latestworktimeend.getValue());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name.getValue());
        return sb.toString();
    }
    
}
