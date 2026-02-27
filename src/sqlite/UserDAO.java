/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sqlite;

import model.*;
import java.sql.*;
import java.time.Duration;
import java.time.Instant;
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

    public synchronized List<User> selectAll() throws SQLException {
        List<User> resultList = new ArrayList<>();
        
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT id, roleid, addressid, contractid, firstname, lastname, login, password, vacationleft ");
        statement.append("FROM user;");
        
        Instant start = Instant.now();
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        ResultSet rs = dbStatement.executeQuery();
        while(rs.next()) {
            resultList.add(createUserFromResultSetEntry(rs));
        }
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        log.debug("UserDAO.selectAll returns " + resultList.size() + " users.");
        log.debug("Elapsed time: " + timeElapsed + "ms");
        return resultList;
    }

    public User selectUserFromId(long id) throws SQLException {
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT id, roleid, addressid, contractid, firstname, lastname, login, password, vacationleft ");
        statement.append("FROM user ");
        statement.append("WHERE id = ?;");
        
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
        long rsId = resultSet.getLong("id");
        long rsRoleId = resultSet.getLong("roleid");
        long rsAddressId = resultSet.getLong("addressid");
        long rsContractId = resultSet.getLong("contractid");
        String rsFirstName = resultSet.getString("firstname");
        String rsLastName = resultSet.getString("lastname");
        String rsLogin = resultSet.getString("login");
        String rsPassword = resultSet.getString("password");
        long rsVacationLeft = resultSet.getLong("vacationleft");

        RoleDAO roleDao = new RoleDAO(connection);
        Role role = roleDao.selectRoleFromId(rsRoleId);
            
        AddressDAO addressDao = new AddressDAO(connection);
        Address address = addressDao.selectAddressFromId(rsAddressId);
            
        ContractDAO contractDao = new ContractDAO(connection);
        Contract contract = contractDao.selectContractFromId(rsContractId);
            
        return new User(rsId, role, address, contract, rsFirstName, rsLastName, rsLogin, rsPassword, rsVacationLeft);
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
