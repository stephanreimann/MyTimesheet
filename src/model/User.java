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
public class User {

    private final LongProperty id = new SimpleLongProperty();
    private final ObjectProperty<Role> role = new SimpleObjectProperty<>();
    private final ObjectProperty<Address> address = new SimpleObjectProperty<>();
    private final ObjectProperty<Contract> contract = new SimpleObjectProperty<>();
    private final StringProperty firstname = new SimpleStringProperty();
    private final StringProperty lastname = new SimpleStringProperty();
    private final StringProperty login = new SimpleStringProperty();
    private final StringProperty password = new SimpleStringProperty();
    private final LongProperty vacationleft = new SimpleLongProperty();
 
    public User(Long id) {
        this.id.set(id);
    }
    
    public User(Long id, Role role, Address addresse, Contract contract, String firstname,
            String lastname, String login, String password, Long vacationleft) {
        this.id.set(id);
        this.role.set(role);
        this.address.set(addresse);
        this.contract.set(contract);
        this.firstname.set(firstname);
        this.lastname.set(lastname);
        this.login.set(login);
        this.password.set(password);
        this.vacationleft.set(vacationleft);
    }
    
    public User(User user) {
        this.id.set(user.getId());
        this.role.set(user.getRole());
        this.address.set(user.getAddress());
        this.contract.set(user.getContract());
        this.firstname.set(user.getFirstname());
        this.lastname.set(user.getLastname());
        this.login.set(user.getLogin());
        this.password.set(user.getPassword());
        this.vacationleft.set(user.getVacationleft());
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
    // <editor-fold defaultstate="collapsed" desc="Role Property">
    public Role getRole() {
        return role.get();
    }
    
    public void setRole(Role value) {
        role.set(value);
    }
    
    public ObjectProperty<Role> getRoleProperty() {
        return role;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Address Property">
    public Address getAddress() {
        return address.get();
    }
    
    public void setAddress(Address value) {
        address.set(value);
    }
    
    public ObjectProperty<Address> getAddressProperty() {
        return address;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Contract Property">
    public Contract getContract() {
        return contract.get();
    }
    
    public void setContract(Contract value) {
        contract.set(value);
    }
    
    public ObjectProperty<Contract> getContractProperty() {
        return contract;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Firstname Property">
    public String getFirstname() {
        return firstname.get();
    }
    
    public void setFirstname(String value) {
        firstname.set(value);
    }
    
    public StringProperty getFirstnameProperty() {
        return firstname;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Lastname Property">
    public String getLastname() {
        return lastname.get();
    }
    
    public void setLastname(String value) {
        lastname.set(value);
    }
    
    public StringProperty getLastnameProperty() {
        return lastname;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Login Property">
    public String getLogin() {
        return login.get();
    }
    
    public void setLogin(String value) {
        login.set(value);
    }
    
    public StringProperty getLoginProperty() {
        return login;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Password Property">
    public String getPassword() {
        return password.get();
    }
    
    public void setPassword(String value) {
        password.set(value);
    }
    
    public StringProperty getPasswordProperty() {
        return password;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Vacationleft Property">
    public Long getVacationleft() {
        return vacationleft.get();
    }
    
    public void setVacationleft(Long value) {
        vacationleft.set(value);
    }
    
    public LongProperty getVacationleftProperty() {
        return vacationleft;
    }
    // </editor-fold>
    
    @Override
    public int hashCode() {
        int hash = 4;
        hash = 53 * hash + Objects.hashCode(this.id);
        hash = 53 * hash + Objects.hashCode(this.role);
        hash = 53 * hash + Objects.hashCode(this.address);
        hash = 53 * hash + Objects.hashCode(this.contract);
        hash = 53 * hash + Objects.hashCode(this.firstname);
        hash = 53 * hash + Objects.hashCode(this.lastname);
        hash = 53 * hash + Objects.hashCode(this.login);
        hash = 53 * hash + Objects.hashCode(this.password);
        hash = 53 * hash + Objects.hashCode(this.vacationleft);
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
        final User other = (User) obj;
        if (!this.id.getValue().equals(other.id.getValue())) {
            return false;
        }
        if(this.role.getValue() == null || 
           this.address.getValue() == null ||
           this.contract.getValue() == null) {
            return false;
        }
        
        if (!this.role.getValue().equals(other.role.getValue())) {
            return false;
        }
        if (!this.address.getValue().equals(other.address.getValue())) {
            return false;
        }
        if (!this.contract.getValue().equals(other.contract.getValue())) {
            return false;
        }
        if (!this.firstname.getValue().equals(other.firstname.getValue())) {
            return false;
        }
        if (!this.lastname.getValue().equals(other.lastname.getValue())) {
            return false;
        }
        if (!this.login.getValue().equals(other.login.getValue())) {
            return false;
        }
        if (!this.password.getValue().equals(other.password.getValue())) {
            return false;
        }
        return this.vacationleft.getValue().equals(other.vacationleft.getValue());
    }
  
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(lastname.getValue()).append("");
        return sb.toString();
    }
    
}
