/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sqlite;

import model.*;
import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.*;
import org.apache.logging.log4j.*;

/**
 *
 * @author adrest18
 */
public class UserDAO {
    
    private final Logger log;
    private final Connection connection;

    public UserDAO(Connection connection) {
        if(connection == null) throw new NullPointerException("connection");
        
        this.log = LogManager.getLogger(UserDAO.class.getName());
        this.connection = connection;
    }

    private StringBuilder getBaseSelectStatement() {
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT ");
        statement.append("u.id AS u_id, u.roleid AS u_roleid, u.addressid AS u_addressid, u.contractid AS u_contractid, u.firstname AS u_firstname, u.lastname AS u_lastname, u.login AS u_login, u.password AS u_password, u.vacationleft AS u_vacationleft, ");
        statement.append("r.id AS r_id, r.name AS r_name, r.Description AS r_Description, ");
        statement.append("a.id AS a_id, a.streetname AS a_streetname, a.housenumber AS a_housenumber, a.unitname AS a_unitname, a.unitnumber AS a_unitnumber, a.unitLocation AS a_unitLocation, a.city As a_city, a.state AS a_state, a.zipcode AS a_zipcode, a.country AS a_country, ");
        statement.append("c.id AS c_id, c.name AS c_name, c.workhours AS c_workhours, c.maxworkhours AS c_maxworkhours, c.vacationdays AS c_vacationdays, c.vacationreconciliationdate AS c_vacationreconciliationdate, c.breakfastofftimeend AS c_breakfastofftimeend, c.breakfastofftimestart AS c_breakfastofftimestart, c.lunchofftimeend AS c_lunchofftimeend, c.lunchofftimestart AS c_lunchofftimestart, c.earliestworktimestart AS c_earliestworktimestart, c.latestworktimeend AS c_latestworktimeend ");
        statement.append("FROM user u ");
        statement.append("JOIN role r ON u.roleid = r.id ");
        statement.append("JOIN address a ON u.addressid = a.id ");
        statement.append("JOIN contract c ON u.contractid = c.id ");        
        return statement;
    }
    
    public synchronized List<User> selectAll() throws SQLException {
        List<User> resultList = new ArrayList<>();
        
        StringBuilder statement = getBaseSelectStatement();
        statement.append(";");
        
        Instant start = Instant.now();
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        ResultSet rs = dbStatement.executeQuery();
        while(rs.next()) {
            resultList.add(createUserFromResultSetEntry(rs));
        }
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        log.debug("UserDAO.selectAll() returns " + resultList.size() + " users.");
        log.debug("Elapsed time: " + timeElapsed + "ms");
        return resultList;
    }

    public User selectUserFromId(long id) throws SQLException {
        StringBuilder statement = getBaseSelectStatement();
        statement.append("WHERE u.id = ?;");
        
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        dbStatement.setString(1, Long.toString(id));
        ResultSet resultSet = dbStatement.executeQuery();
        while(resultSet.next()) {
            User user = createUserFromResultSetEntry(resultSet);
            return user;
        }
        log.warn("Select: User with Id " + id + " not found");
        
        return null;
    }
    
    private User createUserFromResultSetEntry(ResultSet resultSet) throws SQLException {            
        Role role = new Role(resultSet.getLong("r_id"));
        role.setName(resultSet.getString("r_name"));
        role.setDescription(resultSet.getString("r_description"));

        Address address = new Address(resultSet.getLong("a_id"));
        address.setStreetname(resultSet.getString("a_streetname"));
        address.setHousenumber(resultSet.getLong("a_housenumber"));
        address.setUnitname(resultSet.getString("a_unitname"));
        address.setUnitnumber(resultSet.getLong("a_unitnumber"));
        address.setUnitlocation(resultSet.getString("a_unitLocation"));
        address.setCity(resultSet.getString("a_city"));
        address.setState(resultSet.getString("a_state"));
        address.setZipcode(resultSet.getLong("a_zipcode"));
        address.setCountry(resultSet.getString("a_country"));

        Contract contract = new Contract(resultSet.getLong("c_id"));
        contract.setName(resultSet.getString("c_name"));
        contract.setWorkhours(resultSet.getLong("c_workhours"));
        contract.setMaxworkhours(resultSet.getLong("c_maxworkhours"));
        contract.setVacationdays(resultSet.getLong("c_vacationdays"));
        contract.setVacationreconciliationdate(resultSet.getString("c_vacationreconciliationdate"));
        contract.setBreakfastofftimeend(LocalTime.parse(resultSet.getString("c_breakfastofftimeend")));
        contract.setBreakfastofftimestart(LocalTime.parse(resultSet.getString("c_breakfastofftimestart")));
        contract.setLunchofftimeend(LocalTime.parse(resultSet.getString("c_lunchofftimeend")));
        contract.setLunchofftimestart(LocalTime.parse(resultSet.getString("c_lunchofftimestart")));
        contract.setEarliestworktimestart(LocalTime.parse(resultSet.getString("c_earliestworktimestart")));
        contract.setLatestworktimeend(LocalTime.parse(resultSet.getString("c_latestworktimeend")));

        User user = new User(resultSet.getLong("u_id"));
        user.setRole(role);
        user.setAddress(address);
        user.setContract(contract);
        user.setFirstname(resultSet.getString("u_firstname"));
        user.setLastname(resultSet.getString("u_lastname"));
        user.setLogin(resultSet.getString("u_login"));
        user.setPassword(resultSet.getString("u_password"));
        user.setVacationleft(resultSet.getLong("u_vacationleft"));
        
        return user;
    }
    
    public synchronized boolean create(User user) throws SQLException {
        if(user == null) throw new NullPointerException("user");

        StringBuilder statement = new StringBuilder();
        statement.append("INSERT INTO user (id, roleId, addressid, contractid, firstname, lastname, login, password, vacationleft) ");
        statement.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
        
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        dbStatement.setLong(1, user.getId());
        dbStatement.setLong(2, user.getRole().getId());
        dbStatement.setLong(3, user.getAddress().getId());
        dbStatement.setLong(4, user.getContract().getId());
        dbStatement.setString(5, user.getFirstname());
        dbStatement.setString(6, user.getLastname());
        dbStatement.setString(7, user.getLogin());
        dbStatement.setString(8, user.getPassword());
        dbStatement.setLong(9, user.getVacationleft());

        return (dbStatement.executeUpdate() > 0);
    }

    public synchronized boolean update(User original, User modified) throws SQLException {
        if(original == null) throw new NullPointerException("original");
        if(modified == null) throw new NullPointerException("modified");

        if(!Objects.equals(original.getId(), modified.getId())) {
            log.warn("Update: " + original.toString() + " with " + modified.toString() + "not possible, as Id different");
            return false;
        }
        
        StringBuilder statement = new StringBuilder();
        statement.append("UPDATE user ");
        statement.append("SET id = ?, roleId = ?, addressid = ?, contractid = ?, firstname = ?, lastname = ?, login = ?, password = ?, vacationleft = ? ");
        statement.append("WHERE id = ?;");
        
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        dbStatement.setLong(1, modified.getId());
        dbStatement.setLong(2, modified.getRole().getId());
        dbStatement.setLong(3, modified.getAddress().getId());
        dbStatement.setLong(4, modified.getContract().getId());
        dbStatement.setString(5, modified.getFirstname());
        dbStatement.setString(6, modified.getLastname());
        dbStatement.setString(7, modified.getLogin());
        dbStatement.setString(8, modified.getPassword());
        dbStatement.setLong(9, modified.getVacationleft());
        dbStatement.setLong(10, original.getId());

        boolean result = dbStatement.executeUpdate() > 0;
        if(!result) {
            log.warn("Update: not possible, as " + original.toString() + " does not exist");
        }
        return result;
    }
    
    public synchronized boolean delete(User user) throws SQLException {
        if(user == null) throw new NullPointerException("user");

        StringBuilder statement = new StringBuilder();
        statement.append("DELETE FROM user ");
        statement.append("WHERE id = ?");
        
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        dbStatement.setLong(1, user.getId());

        boolean result = dbStatement.executeUpdate() > 0;
        if(!result) {
            log.warn("Delete: not possible, as " + user.toString() + " does not exist");
        }
        return result;
    }
    
    public synchronized Long getNextId() throws SQLException{
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT seq ");
        statement.append("FROM SQLITE_SEQUENCE ");
        statement.append("WHERE name = 'User';");

        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        ResultSet rs = dbStatement.executeQuery();
        if (rs.next()) {
            return rs.getLong(1) + 1;
        }
        return 0L;
    }    

}
