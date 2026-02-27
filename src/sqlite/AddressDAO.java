/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sqlite;

import model.Address;
import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import org.apache.logging.log4j.*;

/**
 *
 * @author adrest18
 */
public class AddressDAO {
    
    private final Logger log;
    private final Connection connection;

    public AddressDAO(Connection connection) {
        if(connection == null) throw new NullPointerException("connection");
        
        this.log = LogManager.getLogger(AddressDAO.class.getName());
        this.connection = connection;
    }

    public List<Address> selectAll() throws SQLException {
        List<Address> resultList = new ArrayList<>();
         
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT id, streetname, housenumber, unitname, unitnumber, unitlocation, city, state, zipcode, country ");
        statement.append("FROM address;");
        
        Instant start = Instant.now();
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        ResultSet resultSet = dbStatement.executeQuery();
        while(resultSet.next()) {
            resultList.add(createAddressFromResultSetEntry(resultSet));
        }
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        log.debug("AddressDAO.selectAll returns " + resultList.size() + " addresses.");
        log.debug("Elapsed time: " + timeElapsed + "ms");
        return resultList;
    }
    
    public Address selectAddressFromId(long id) throws SQLException {
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT id, streetname, housenumber, unitname, unitnumber, unitlocation, city, state, zipcode, country ");
        statement.append("FROM address ");
        statement.append("WHERE id = ?;");
        
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        dbStatement.setString(1, Long.toString(id));
        ResultSet resultSet = dbStatement.executeQuery();
        while(resultSet.next()) {
            Address address = createAddressFromResultSetEntry(resultSet);
            return address;
        }
        return null;
    }
    
    private synchronized Address createAddressFromResultSetEntry(ResultSet resultSet) throws SQLException {
        Long rsId = resultSet.getLong("id");
        String rsStreetname = resultSet.getString("streetname");
        Long rsHousenumber = resultSet.getLong("housenumber");
        String rsUnitname = resultSet.getString("unitname");
        Long rsUnitnumber = resultSet.getLong("unitnumber");
        String rsUnitlocation = resultSet.getString("unitlocation");
        String rsCity = resultSet.getString("city");
        String rsState = resultSet.getString("state");
        Long rsZipcode = resultSet.getLong("zipcode");
        String rsCountry = resultSet.getString("country");

        return new Address(rsId, rsStreetname, rsHousenumber, rsUnitname, rsUnitnumber, rsUnitlocation, rsCity, rsState, rsZipcode, rsCountry);
    }    

    public synchronized boolean create(Address address) throws SQLException {
        if(address == null) throw new NullPointerException("address");
        
        StringBuilder statement = new StringBuilder();
        statement.append("INSERT INTO address (id, streetname, housenumber, unitname, unitnumber, unitlocation, city, state, zipcode, country) ");
        statement.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        dbStatement.setLong(1, address.getId());
        dbStatement.setString(2, address.getStreetname());
        dbStatement.setLong(3, address.getHousenumber());
        dbStatement.setString(4, address.getUnitname());
        dbStatement.setLong(5, address.getUnitnumber());
        dbStatement.setString(6, address.getUnitlocation());
        dbStatement.setString(7, address.getCity());
        dbStatement.setString(8, address.getState());
        dbStatement.setLong(9, address.getZipcode());
        dbStatement.setString(10, address.getCountry());

        return (dbStatement.executeUpdate() > 0);
    }

    public synchronized boolean update(Address original, Address modified) throws SQLException {
        if(original == null) throw new NullPointerException("original");
        if(modified == null) throw new NullPointerException("modified");
        
        if(!Objects.equals(original.getId(), modified.getId())) {
            log.warn("Update: " + original.toString() + " with " + modified.toString() + "not possible, as Id different");
            return false;
        }
        
        StringBuilder statement = new StringBuilder();
        statement.append("UPDATE address ");
        statement.append("SET streetname = ?, housenumber = ?, unitname = ?, unitnumber = ?, unitlocation = ?, city = ?, state = ?, zipcode = ?, country = ? ");
        statement.append("WHERE id = ?;");
        
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        dbStatement.setString(1, modified.getStreetname());
        dbStatement.setLong(2, modified.getHousenumber());
        dbStatement.setString(3, modified.getUnitname());
        dbStatement.setLong(4, modified.getUnitnumber());
        dbStatement.setString(5, modified.getUnitlocation());
        dbStatement.setString(6, modified.getCity());
        dbStatement.setString(7, modified.getState());
        dbStatement.setLong(8, modified.getZipcode());
        dbStatement.setString(9, modified.getCountry());
        dbStatement.setLong(10, original.getId());

        boolean result = dbStatement.executeUpdate() > 0;
        if(!result) {
            log.warn("Update: not possible, as " + original.toString() + " does not exist");
        }
        return result;
    }
    
    public synchronized boolean delete(Address address) throws SQLException {
        if(address == null) throw new NullPointerException("address");
        
        StringBuilder statement = new StringBuilder();
        statement.append("DELETE FROM address ");
        statement.append("WHERE id = ?");
        
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        dbStatement.setLong(1, address.getId());
        
        boolean result = dbStatement.executeUpdate() > 0;
        if(!result) {
            log.warn("Delete: not possible, as " + address.toString() + " does not exist");
        }
        return result;
    }
    
    public synchronized Long getNextId() throws SQLException{
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT seq ");
        statement.append("FROM SQLITE_SEQUENCE ");
        statement.append("WHERE name = 'Address';");

        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        ResultSet rs = dbStatement.executeQuery();
        if (rs.next()) {
            return rs.getLong(1) + 1;
        }
        return 0L;
    }    

}
