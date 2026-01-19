/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sqlite;

import model.Role;
import java.sql.*;
import java.util.*;
import org.apache.logging.log4j.*;

/**
 *
 * @author adrest18
 */
public class RoleDAO {
    
    private final Logger log;
    private final Connection connection;

    public RoleDAO(Connection connection) {
        if(connection == null) throw new NullPointerException("connection");
        
        this.log = LogManager.getLogger(RoleDAO.class.getName());
        this.connection = connection;
    }

    public synchronized List<Role> selectAll() throws SQLException {
        List<Role> resultList = new ArrayList<>();
        
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT id, name, description ");
        statement.append("FROM role;");
        
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        ResultSet rs = dbStatement.executeQuery();
        while(rs.next()) {
            resultList.add(createRoleFromResultSetEntry(rs));
        }
        log.debug("RoleDAO.selectAll returns " + resultList.size() + " roles");        
        return resultList;
    }

    public Role selectRoleFromId(long id) throws SQLException {
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT id, name, description ");
        statement.append("FROM role ");
        statement.append("WHERE id = ?;");
        
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        dbStatement.setString(1, Long.toString(id));
        ResultSet resultSet = dbStatement.executeQuery();
        while(resultSet.next()) {
            Role role = createRoleFromResultSetEntry(resultSet);
            return role;
        }
        log.warn("Select: Address with Id " + id + " not found");
        return null;
    }
    
    private Role createRoleFromResultSetEntry(ResultSet rs) throws SQLException {
        Long rsId = rs.getLong("id");
        String rsName = rs.getString("name");
        String rsDescription = rs.getString("description");

        return new Role(rsId, rsName, rsDescription);
    }
    
    public synchronized boolean create(Role role) throws SQLException {
        if(role == null) throw new NullPointerException("role");

        StringBuilder statement = new StringBuilder();
        statement.append("INSERT INTO role (id, name, description) ");
        statement.append("VALUES (?, ?, ?)");
        
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        dbStatement.setLong(1, role.getId());
        dbStatement.setString(2, role.getName());
        dbStatement.setString(3, role.getDescription());

        return (dbStatement.executeUpdate() > 0);
    }

    public synchronized boolean update(Role original, Role modified) throws SQLException {
        if(original == null) throw new NullPointerException("original");
        if(modified == null) throw new NullPointerException("modified");

        if(!Objects.equals(original.getId(), modified.getId())) {
            log.warn("Update: " + original.toString() + " with " + modified.toString() + "not possible, as Id different");
            return false;
        }
        
        StringBuilder statement = new StringBuilder();
        statement.append("UPDATE role ");
        statement.append("SET id = ?, name = ?, description = ? ");
        statement.append("WHERE id = ?;");
        
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        dbStatement.setLong(1, modified.getId());
        dbStatement.setString(2, modified.getName());
        dbStatement.setString(3, modified.getDescription());
        dbStatement.setLong(4, original.getId());

        boolean result = dbStatement.executeUpdate() > 0;
        if(!result) {
            log.warn("Update: not possible, as " + original.toString() + " does not exist");
        }
        return result;
    }
    
    public synchronized boolean delete(Role role) throws SQLException {
        if(role == null) throw new NullPointerException("role");

        StringBuilder statement = new StringBuilder();
        statement.append("DELETE FROM role ");
        statement.append("WHERE id = ?");
        
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        dbStatement.setLong(1, role.getId());

        boolean result = dbStatement.executeUpdate() > 0;
        if(!result) {
            log.warn("Delete: not possible, as " + role.toString() + " does not exist");
        }
        return result;
    }
    
    public synchronized Long getNextId() throws SQLException{
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT seq ");
        statement.append("FROM SQLITE_SEQUENCE ");
        statement.append("WHERE name = 'Role';");

        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        ResultSet rs = dbStatement.executeQuery();
        if (rs.next()) {
            return rs.getLong(1) + 1;
        }
        return 0L;
    }    

}
