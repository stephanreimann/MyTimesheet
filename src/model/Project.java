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
public class Project {
    
    private final LongProperty id = new SimpleLongProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty costunit = new SimpleStringProperty();
    private final StringProperty isworktimerelevant = new SimpleStringProperty();
    private final StringProperty isvacationrelevant = new SimpleStringProperty();
    private final StringProperty iscomptimerelevant = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    
    public Project(Long id) {
        this.id.set(id);
    }
    
    public Project(Project project) {
        this.id.set(project.getId());
        this.name.set(project.getName());
        this.costunit.set(project.getCostunit());
        this.isworktimerelevant.set(project.getIsWorktimeRelevant());
        this.isvacationrelevant.set(project.getIsVacationRelevant());
        this.iscomptimerelevant.set(project.getIsComptimeRelevant());
        this.description.set(project.getDescription());
    }
    
    public Project(Long id, String name, String costunit, String isworktimerelevant, String isvacationrelevant, String iscomptimerelevant, String description) {
        this.id.set(id);
        this.name.set(name);
        this.costunit.set(costunit);
        this.isworktimerelevant.set(isworktimerelevant);
        this.isvacationrelevant.set(isvacationrelevant);
        this.iscomptimerelevant.set(iscomptimerelevant);
        this.description.set(description);
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
    // <editor-fold defaultstate="collapsed" desc="Costunit Property">
    public String getCostunit() {
        return costunit.get();
    }
    
    public void setCostunit(String value) {
        costunit.set(value);
    }
    
    public StringProperty getCostunitProperty() {
        return costunit;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="IsWorktimeRelevant Property">
    public String getIsWorktimeRelevant() {
        return isworktimerelevant.get();
    }
    
    public void setIsWorktimeRelevant(String value) {
        isworktimerelevant.set(value);
    }
    
    public StringProperty getIsWorktimeRelevantProperty() {
        return isworktimerelevant;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="IsVacationRelevant Property">
    public String getIsVacationRelevant() {
        return isvacationrelevant.get();
    }
    
    public void setIsVacationRelevant(String value) {
        isvacationrelevant.set(value);
    }
    
    public StringProperty getIsVacationRelevantProperty() {
        return isvacationrelevant;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="IsComptimeRelevant Property">
    public String getIsComptimeRelevant() {
        return iscomptimerelevant.get();
    }
    
    public void setIsComptimeRelevant(String value) {
        iscomptimerelevant.set(value);
    }
    
    public StringProperty getIsComptimeRelevantProperty() {
        return iscomptimerelevant;
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
        hash = 53 * hash + Objects.hashCode(this.costunit);
        hash = 53 * hash + Objects.hashCode(this.isworktimerelevant);
        hash = 53 * hash + Objects.hashCode(this.isvacationrelevant);
        hash = 53 * hash + Objects.hashCode(this.iscomptimerelevant);
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
        final Project other = (Project) obj;
        if (!this.id.getValue().equals(other.id.getValue())) {
            return false;
        }
        if (!this.name.getValue().equals(other.name.getValue())) {
            return false;
        }
        if (!this.costunit.getValue().equals(other.costunit.getValue())) {
            return false;
        }
        if (!this.isworktimerelevant.getValue().equals(other.isworktimerelevant.getValue())) {
            return false;
        }
        if (!this.isvacationrelevant.getValue().equals(other.isvacationrelevant.getValue())) {
            return false;
        }
        if (!this.iscomptimerelevant.getValue().equals(other.iscomptimerelevant.getValue())) {
            return false;
        }
        return this.description.getValue().equals(other.description.getValue());
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(/*Project.class.getSimpleName()*/);
        //sb.append(": ");
        //sb.append(id.getValue()).append(", ");
        sb.append(name.getValue());//.append(", ");
        //sb.append(costunit.getValue()).append(", ");
        //sb.append(isworktimerelevant.getValue()).append(", ");
        //sb.append(isvacationrelevant.getValue()).append(", ");
        //sb.append(iscomptimerelevant.getValue()).append(", ");
        //sb.append(description.getValue()).append("");
        return sb.toString();
    }
    
}
