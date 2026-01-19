/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sqlite;

import model.Worklocation;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author adrest18
 */
public class WorklocationDAO {

    private final Logger log;
    private final Connection connection;

    public WorklocationDAO(Connection connection) {
        if(connection == null) throw new NullPointerException("connection");
        
        this.log = LogManager.getLogger(WorklocationDAO.class.getName());
        this.connection = connection;
    }

    public List<Worklocation> selectAll() throws SQLException {
        List<Worklocation> resultList = new ArrayList<>();
        
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT id, name, description ");
        statement.append("FROM worklocation;");
        
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        ResultSet resultSet = dbStatement.executeQuery();
        while(resultSet.next()) {
            resultList.add(createWorklocationFromResultSetEntry(resultSet));
        }
        log.debug("WorklocationDAO.selectAll returns " + resultList.size() + " worklocations");        
        return resultList;
    }
    
    public Worklocation selectWorklocationFromId(long id) throws SQLException {
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT id, name, description ");
        statement.append("FROM worklocation ");
        statement.append("WHERE id = ?;");
        
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        dbStatement.setString(1, Long.toString(id));
        ResultSet resultSet = dbStatement.executeQuery();
        while(resultSet.next()) {
            Worklocation worklocation = createWorklocationFromResultSetEntry(resultSet);
            return worklocation;
        }
        log.warn("Select: Address with Id " + id + " not found");
        return null;
    }
    
    private synchronized Worklocation createWorklocationFromResultSetEntry(ResultSet resultSet) throws SQLException {
        Long rsId = resultSet.getLong("id");
        String rsName = resultSet.getString("name");
        String rsDescription = resultSet.getString("description");

        return new Worklocation(rsId, rsName, rsDescription);
    }    

    public synchronized boolean create(Worklocation worklocation) throws SQLException {
        if(worklocation == null) throw new NullPointerException("worklocation");
        
        StringBuilder statement = new StringBuilder();
        statement.append("INSERT INTO worklocation (id, name, description) ");
        statement.append("VALUES (?, ?, ?)");
        
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        dbStatement.setLong(1, worklocation.getId());
        dbStatement.setString(2, worklocation.getName());
        dbStatement.setString(3, worklocation.getDescription());

        return (dbStatement.executeUpdate() > 0);
    }

    public synchronized boolean update(Worklocation original, Worklocation modified) throws SQLException {
        if(original == null) throw new NullPointerException("original");
        if(modified == null) throw new NullPointerException("modified");
        
        if(!Objects.equals(original.getId(), modified.getId())) {
            log.warn("Update: " + original.toString() + " with " + modified.toString() + "not possible, as Id different");
            return false;
        }
        
        StringBuilder statement = new StringBuilder();
        statement.append("UPDATE worklocation ");
        statement.append("SET name = ?, description = ? ");
        statement.append("WHERE id = ?;");
        
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        dbStatement.setString(1, modified.getName());
        dbStatement.setString(2, modified.getDescription());
        dbStatement.setLong(3, original.getId());

        boolean result = dbStatement.executeUpdate() > 0;
        if(!result) {
            log.warn("Update: not possible, as " + original.toString() + " does not exist");
        }
        return result;
    }
    
    public synchronized boolean delete(Worklocation worklocation) throws SQLException {
        if(worklocation == null) throw new NullPointerException("worklocation");
        
        StringBuilder statement = new StringBuilder();
        statement.append("DELETE FROM worklocation ");
        statement.append("WHERE id = ?");
        
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        dbStatement.setLong(1, worklocation.getId());
        
        boolean result = dbStatement.executeUpdate() > 0;
        if(!result) {
            log.warn("Delete: not possible, as " + worklocation.toString() + " does not exist");
        }
        return result;
    }
    
    public synchronized Long getNextId() throws SQLException{
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT seq ");
        statement.append("FROM SQLITE_SEQUENCE ");
        statement.append("WHERE name = 'Worklocation';");

        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        ResultSet rs = dbStatement.executeQuery();
        if (rs.next()) {
            return rs.getLong(1) + 1;
        }
        return 0L;
    }    
    
}
