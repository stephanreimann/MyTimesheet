/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDate;
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
public class Workrecord {
    
    private final LongProperty id = new SimpleLongProperty();
    private final ObjectProperty<User> user = new SimpleObjectProperty<>();
    private final ObjectProperty<Project> project = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalTime> starttime = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalTime> endtime = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalTime> worktime = new SimpleObjectProperty<>();
    private final StringProperty overtime = new SimpleStringProperty();
    private final StringProperty overtimecorrection = new SimpleStringProperty();
    private final ObjectProperty<Integer> vacationcorrection = new SimpleObjectProperty<>();
    private final ObjectProperty<Worklocation> worklocation = new SimpleObjectProperty<>();
    private final StringProperty description = new SimpleStringProperty();

    public Workrecord(Long id) {
        this.id.set(id);
    }
    
    public Workrecord(Long id, User user, Project project, LocalDate date, LocalTime starttime,
            LocalTime endtime, LocalTime worktime, String overtime, String overtimecorrection,
            Integer vacationcorrection, Worklocation worklocation, String description) {
        this.id.set(id);
        this.user.set(user);
        this.project.set(project);
        this.date.set(date);
        this.starttime.set(starttime);
        this.endtime.set(endtime);
        this.worktime.set(worktime);
        this.overtime.set(overtime);
        this.overtimecorrection.set(overtimecorrection);
        this.vacationcorrection.set(vacationcorrection);
        this.worklocation.set(worklocation);
        this.description.set(description);
    }
    
    public Workrecord(Workrecord workrecord) {
        this.id.set(workrecord.getId());
        this.user.set(workrecord.getUser());
        this.project.set(workrecord.getProject());
        this.date.set(workrecord.getDate());
        this.starttime.set(workrecord.getStarttime());
        this.endtime.set(workrecord.getEndtime());
        this.worktime.set(workrecord.getWorktime());
        this.overtime.set(workrecord.getOvertime());
        this.overtimecorrection.set(workrecord.getOvertimecorrection());
        this.vacationcorrection.set(workrecord.getVacationcorrection());
        this.worklocation.set(workrecord.getWorklocation());
        this.description.set(workrecord.getDescription());
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
    // <editor-fold defaultstate="collapsed" desc="Project Property">
    public Project getProject() {
        return project.get();
    }
    
    public void setProject(Project value) {
        project.set(value);
    }
    
    public ObjectProperty<Project> getProjectProperty() {
        return project;
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
    // <editor-fold defaultstate="collapsed" desc="Starttime Property">
    public LocalTime getStarttime() {
        return starttime.get();
    }
    
    public void setStarttime(LocalTime value) {
        starttime.set(value);
    }
    
    public ObjectProperty<LocalTime> getStarttimeProperty() {
        return starttime;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Endtime Property">
    public LocalTime getEndtime() {
        return endtime.get();
    }
    
    public void setEndtime(LocalTime value) {
        endtime.set(value);
    }
    
    public ObjectProperty<LocalTime> getEndtimeProperty() {
        return endtime;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Worktime Property">
    public LocalTime getWorktime() {
        return worktime.get();
    }
    
    public void setWorktime(LocalTime value) {
        worktime.set(value);
    }
    
    public ObjectProperty<LocalTime> getWorktimeProperty() {
        return worktime;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Overtime Property">
    public String getOvertime() {
        return overtime.get();
    }
    
    public void setOvertime(String value) {
        overtime.set(value);
    }
    
    public StringProperty getOvertimeProperty() {
        return overtime;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Overtimecorrection Property">
    public String getOvertimecorrection() {
        return overtimecorrection.get();
    }
    
    public void setOvertimecorrection(String value) {
        overtimecorrection.set(value);
    }
    
    public StringProperty getOvertimecorrectionProperty() {
        return overtimecorrection;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Vacationcorrection Property">
    public Integer getVacationcorrection() {
        return vacationcorrection.get();
    }
    
    public void setVacationcorrection(Integer value) {
        vacationcorrection.set(value);
    }
    
    public ObjectProperty<Integer> getVacationcorrectionProperty() {
        return vacationcorrection;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Worklocation Property">
    public Worklocation getWorklocation() {
        return worklocation.get();
    }
    
    public void setWorklocation(Worklocation value) {
        worklocation.set(value);
    }
    
    public ObjectProperty<Worklocation> getWorklocationProperty() {
        return worklocation;
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
        hash = 53 * hash + Objects.hashCode(this.project);
        hash = 53 * hash + Objects.hashCode(this.date);
        hash = 53 * hash + Objects.hashCode(this.starttime);
        hash = 53 * hash + Objects.hashCode(this.endtime);
        hash = 53 * hash + Objects.hashCode(this.worktime);
        hash = 53 * hash + Objects.hashCode(this.overtime);
        hash = 53 * hash + Objects.hashCode(this.overtimecorrection);
        hash = 53 * hash + Objects.hashCode(this.vacationcorrection);
        hash = 53 * hash + Objects.hashCode(this.worklocation);
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
        final Workrecord other = (Workrecord) obj;
        if (!this.id.getValue().equals(other.id.getValue())) {
            return false;
        }
        if(this.project.getValue() == null || 
           this.user.getValue() == null ||
           this.worklocation.getValue() == null) {
            return false;
        }
        if (!this.user.getValue().equals(other.user.getValue())) {
            return false;
        }
        if (!this.project.getValue().equals(other.project.getValue())) {
            return false;
        }
        if (!this.date.getValue().equals(other.date.getValue())) {
            return false;
        }
        if (!this.starttime.getValue().equals(other.starttime.getValue())) {
            return false;
        }
        if (!this.endtime.getValue().equals(other.endtime.getValue())) {
            return false;
        }
        if (!this.worktime.getValue().equals(other.worktime.getValue())) {
            return false;
        }
        if (!this.overtime.getValue().equals(other.overtime.getValue())) {
            return false;
        }
        if (!this.overtimecorrection.getValue().equals(other.overtimecorrection.getValue())) {
            return false;
        }
        if(!this.vacationcorrection.getValue().equals(other.vacationcorrection.getValue())) {
            return false;
        }
        if (!this.worklocation.getValue().equals(other.worklocation.getValue())) {
            return false;
        }
        return this.description.getValue().equals(other.description.getValue());
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(User.class.getSimpleName());
        sb.append(": ");
        sb.append(id.getValue()).append(", ");
        sb.append(user.getValue()).append(", ");
        sb.append(project.getValue()).append(", ");
        sb.append(date.getValue()).append(", ");
        sb.append(starttime.getValue()).append(", ");
        sb.append(endtime.getValue()).append(", ");
        sb.append(worktime.getValue()).append(", ");
        sb.append(overtime.getValue()).append(", ");
        sb.append(overtime.getValue()).append(", ");
        sb.append(overtimecorrection.getValue()).append(", ");
        sb.append(vacationcorrection.getValue()).append(", ");
        sb.append(worklocation.getValue()).append(", ");
        sb.append(description.getValue()).append("");
        return sb.toString();
    }

}
