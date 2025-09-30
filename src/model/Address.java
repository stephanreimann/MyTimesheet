/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.Objects;
import javafx.beans.property.*;

/**
 *
 * @author adrest18
 */
public class Address {
    
    private final LongProperty id = new SimpleLongProperty();
    private final StringProperty streetname = new SimpleStringProperty();
    private final LongProperty housenumber = new SimpleLongProperty();
    private final StringProperty unitname = new SimpleStringProperty();
    private final LongProperty unitnumber = new SimpleLongProperty();
    private final StringProperty unitlocation = new SimpleStringProperty();
    private final StringProperty city = new SimpleStringProperty();
    private final StringProperty state = new SimpleStringProperty();
    private final LongProperty zipcode = new SimpleLongProperty();
    private final StringProperty country = new SimpleStringProperty();
    private final StringProperty address = new SimpleStringProperty();

    public Address(Long id) {
        this.id.set(id);
    }
    
    public Address(Long id, String streetname, Long housenumber, String unitname, 
            Long unitnumber, String unitlocation, String city, String state, Long zipcode, String country) {
        this.id.set(id);
        this.streetname.set(streetname);
        this.housenumber.set(housenumber);
        this.unitname.set(unitname);
        this.unitnumber.set(unitnumber);
        this.unitlocation.set(unitlocation);
        this.city.set(city);
        this.state.set(state);
        this.zipcode.set(zipcode);
        this.country.set(country);
        this.address.set(this.toString());
    }

    public Address(Address address) {
        this.id.set(address.getId());
        this.streetname.set(address.getStreetname());
        this.housenumber.set(address.getHousenumber());
        this.unitname.set(address.getUnitname());
        this.unitnumber.set(address.getUnitnumber());
        this.unitlocation.set(address.getUnitlocation());
        this.city.set(address.getCity());
        this.state.set(address.getState());
        this.zipcode.set(address.getZipcode());
        this.country.set(address.getCountry());
        this.address.set(address.toString());
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
    // <editor-fold defaultstate="collapsed" desc="Streetname Property">
    public String getStreetname() {
        return streetname.get();
    }
    
    public void setStreetname(String value) {
        streetname.set(value);
    }
    
    public StringProperty getStreetnameProperty() {
        return streetname;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Housenumber Property">
    public Long getHousenumber() {
        return housenumber.get();
    }
    
    public void setHousenumber(Long value) {
        housenumber.set(value);
    }
    
    public LongProperty getHousenumberProperty() {
        return housenumber;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Unitname Property">
    public String getUnitname() {
        return unitname.get();
    }
    
    public void setUnitname(String value) {
        unitname.set(value);
    }
    
    public StringProperty getUnitnameProperty() {
        return unitname;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Unitnumber Property">
    public Long getUnitnumber() {
        return unitnumber.get();
    }
    
    public void setUnitnumber(Long value) {
        unitnumber.set(value);
    }
    
    public LongProperty getUnitnumberProperty() {
        return unitnumber;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Unitlocation Property">
    public String getUnitlocation() {
        return unitlocation.get();
    }
    
    public void setUnitlocation(String value) {
        unitlocation.set(value);
    }
    
    public StringProperty getUnitlocationProperty() {
        return unitlocation;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="City Property">
    public String getCity() {
        return city.get();
    }
    
    public void setCity(String value) {
        city.set(value);
    }
    
    public StringProperty getCityProperty() {
        return city;
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
    // <editor-fold defaultstate="collapsed" desc="Zipcode Property">
    public Long getZipcode() {
        return zipcode.get();
    }
    
    public void setZipcode(Long value) {
        zipcode.set(value);
    }
    
    public LongProperty getZipcodeProperty() {
        return zipcode;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Country Property">
    public String getCountry() {
        return country.get();
    }
    
    public void setCountry(String value) {
        country.set(value);
    }
    
    public StringProperty getCountryProperty() {
        return country;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Address Property">
    public String getAddress() {
        return address.get();
    }
    
    public void setAddress(String value) {
        address.set(value);
    }
    
    public StringProperty getAddressProperty() {
        return address;
    }
    // </editor-fold>
   
    @Override
    public int hashCode() {
        int hash = 1;
        hash = 53 * hash + Objects.hashCode(this.id);
        hash = 53 * hash + Objects.hashCode(this.streetname);
        hash = 53 * hash + Objects.hashCode(this.housenumber);
        hash = 53 * hash + Objects.hashCode(this.unitname);
        hash = 53 * hash + Objects.hashCode(this.unitnumber);
        hash = 53 * hash + Objects.hashCode(this.unitlocation);
        hash = 53 * hash + Objects.hashCode(this.city);
        hash = 53 * hash + Objects.hashCode(this.state);
        hash = 53 * hash + Objects.hashCode(this.zipcode);
        hash = 53 * hash + Objects.hashCode(this.country);
        hash = 53 * hash + Objects.hashCode(this.address);
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
        final Address other = (Address) obj;
        if (!this.id.getValue().equals(other.id.getValue())) {
            return false;
        }
        if (!this.streetname.getValue().equals(other.streetname.getValue())) {
            return false;
        }
        if (!this.housenumber.getValue().equals(other.housenumber.getValue())) {
            return false;
        }
        if (!this.unitname.getValue().equals(other.unitname.getValue())) {
            return false;
        }
        if (!this.unitnumber.getValue().equals(other.unitnumber.getValue())) {
            return false;
        }
        if (!this.unitlocation.getValue().equals(other.unitlocation.getValue())) {
            return false;
        }
        if (!this.city.getValue().equals(other.city.getValue())) {
            return false;
        }
        if (!this.state.getValue().equals(other.state.getValue())) {
            return false;
        }
        if (!this.zipcode.getValue().equals(other.zipcode.getValue())) {
            return false;
        }
        return this.country.getValue().equals(other.country.getValue());
    }
   
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(streetname.getValue()).append(", ");
        sb.append(housenumber.getValue()).append(", ");
        sb.append(unitname.getValue()).append(", ");
        sb.append(unitnumber.getValue()).append(", ");
        sb.append(unitlocation.getValue()).append(", ");
        sb.append(city.getValue()).append(", ");
        sb.append(state.getValue()).append(", ");
        sb.append(zipcode.getValue()).append(", ");
        sb.append(country.getValue()).append("");
        return sb.toString();
    }
    
}
