/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sqlite;

import java.sql.*;
import java.time.*;
import java.util.*;
import model.Address;
import model.Contract;
import model.Project;
import model.Role;
import model.Sprint;
import model.TrackingItem;
import model.User;
import model.WorkItem;
import model.Worklocation;
import model.Workrecord;
import org.apache.logging.log4j.*;

/**
 *
 * @author adrest18
 */
public class WorkItemDAO {
    
    private final Logger log;
    private final Connection connection;

    public WorkItemDAO(Connection connection) {
        if(connection == null) throw new NullPointerException("connection");
        
        this.log = LogManager.getLogger(UserDAO.class.getName());
        this.connection = connection;
    }

    private StringBuilder getBaseSelectStatement() {
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT id, workrecordid, sprintid, trackingitemid, starttime, endtime, description ");
        statement.append("FROM workitem ");
        return statement;
    }
    
    public List<WorkItem> selectAll() throws SQLException {
        List<WorkItem> resultList = new ArrayList<>();
        
        StringBuilder statement = getBaseSelectStatement();
        statement.append(";");
        
        try(PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            Instant start = Instant.now();
            ResultSet rs = dbStatement.executeQuery();
            while(rs.next()) {
                resultList.add(createWorkItemFromResultSetEntry(rs));
            }
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.debug(String.format("WorkItemDAO.selectAll() returns %d workItems.", resultList.size()));
            log.debug("Elapsed time: " + timeElapsed + "ms");        
        }
        return resultList;
    }
    
    private WorkItem createWorkItemFromResultSetEntry(ResultSet resultSet) throws SQLException {
        long rsId = resultSet.getLong("id");
        long rsWorkrecordId = resultSet.getLong("workrecordid");
        long rsSprintId = resultSet.getLong("sprintid");
        long rsTrackingItemId = resultSet.getLong("trackingitemid");
        LocalTime rsStartTime = LocalTime.parse(resultSet.getString("starttime"));
        LocalTime rsEndTime = LocalTime.parse(resultSet.getString("endtime"));
        String rsDescription = resultSet.getString("description");

        return new WorkItem(rsId, rsWorkrecordId, rsSprintId, rsTrackingItemId, rsStartTime, rsEndTime, rsDescription);
    }
    
    public boolean create(WorkItem workItem) throws SQLException {
        if(workItem == null) throw new NullPointerException("workItem");

        boolean result;
        StringBuilder statement = new StringBuilder();
        statement.append("INSERT INTO workitem (id, workrecordid, sprintid, trackingitemid, starttime, endtime, description) ");
        statement.append("VALUES (?, ?, ?, ?, ?, ?, ?)");
        
        try(PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            dbStatement.setLong(1, workItem.getId());
            dbStatement.setLong(2, workItem.getWorkrecordId());
            dbStatement.setLong(3, workItem.getSprintId());
            dbStatement.setLong(4, workItem.getTrackingItemId());
            dbStatement.setString(5, workItem.getStartTime().toString());
            dbStatement.setString(6, workItem.getEndTime().toString());
            dbStatement.setString(7, workItem.getDescription());

            Instant start = Instant.now();
            result = dbStatement.executeUpdate() > 0;
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.debug(String.format("WorkItemDAO.create returns %b.", result));
            log.debug("Elapsed time: " + timeElapsed + "ms");
        }
        return result;
    }

    public boolean update(WorkItem original, WorkItem modified) throws SQLException {
        if(original == null) throw new NullPointerException("original");
        if(modified == null) throw new NullPointerException("modified");

        if (!Objects.equals(original.getId(), modified.getId())) {
            log.warn("Update: " + original.toString() + " with " + modified.toString() + " not possible, as Id different");
            return false;
        }
        
        boolean result;
        StringBuilder statement = new StringBuilder();
        statement.append("UPDATE workitem ");
        statement.append("SET id = ?, workrecordid = ?, sprintid = ?, trackingitemid = ?, starttime = ?, endtime = ?, description = ? ");
        statement.append("WHERE id = ?;");
        
        try(PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            dbStatement.setLong(1, modified.getId());
            dbStatement.setLong(2, modified.getWorkrecordId());
            dbStatement.setLong(3, modified.getSprintId());
            dbStatement.setLong(4, modified.getTrackingItemId());
            dbStatement.setString(5, modified.getStartTime().toString());
            dbStatement.setString(6, modified.getEndTime().toString());
            dbStatement.setString(7, modified.getDescription());
            dbStatement.setLong(8, original.getId());
            
            Instant start = Instant.now();
            result = dbStatement.executeUpdate() > 0;
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.debug(String.format("WorkItemDAO.update returns %b.", result));
            log.debug("Elapsed time: " + timeElapsed + "ms");
            if(!result) {
                log.warn(String.format("Update: not possible, as %s does not exist.", original));
            }
        }
        return result;
    }
    
    public boolean delete(WorkItem workItem) throws SQLException {
        if(workItem == null) throw new NullPointerException("workItem");

        boolean result;
        StringBuilder statement = new StringBuilder();
        statement.append("DELETE FROM workitem ");
        statement.append("WHERE id = ?");
        
        try(PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            dbStatement.setLong(1, workItem.getId());

            Instant start = Instant.now();
            result = dbStatement.executeUpdate() > 0;
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.debug(String.format("WorkItemDAO.delete returns %b.", result));
            log.debug("Elapsed time: " + timeElapsed + "ms");
            if(!result) {
                log.warn(String.format("Delete: not possible, as %s does not exist.", workItem));
            }
        }
        return result;
    }
    
    public Long getNextId() throws SQLException{
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT seq ");
        statement.append("FROM SQLITE_SEQUENCE ");
        statement.append("WHERE name = 'WorkItem';");

        try(PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            ResultSet rs = dbStatement.executeQuery();
            if (rs.next()) {
                return rs.getLong(1) + 1;
            }
        }
        return 0L;
    }    
    
}
