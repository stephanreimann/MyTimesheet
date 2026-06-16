/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sqlite;

import java.sql.*;
import java.time.*;
import java.util.*;
import model.Sprint;
import org.apache.logging.log4j.*;

/**
 *
 * @author adrest18
 */
public class SprintDAO {
    
    private final Logger log;
    private final Connection connection;

    public SprintDAO(Connection connection) {
        if(connection == null) throw new NullPointerException("connection");
        
        this.log = LogManager.getLogger(RoleDAO.class.getName());
        this.connection = connection;
    }

    public synchronized List<Sprint> selectAll() throws SQLException {
        List<Sprint> resultList = new ArrayList<>();
        
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT id, startdate, enddate, numberofsprintdays ");
        statement.append("FROM sprint;");
        
        Instant start = Instant.now();
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        ResultSet resultSet = dbStatement.executeQuery();
        while(resultSet.next()) {
            resultList.add(createSprintFromResultSetEntry(resultSet));
        }
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        log.debug("SprintDAO.selectAll() returns " + resultList.size() + " sprints.");
        log.debug("Elapsed time: " + timeElapsed + "ms");
        return resultList;
    }

    public Sprint selectSprintFromId(long id) throws SQLException {
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT id, startdate, enddate, numberofsprintdays ");
        statement.append("FROM sprint ");
        statement.append("WHERE id = ?;");
        
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        dbStatement.setString(1, Long.toString(id));
        ResultSet resultSet = dbStatement.executeQuery();
        while(resultSet.next()) {
            Sprint sprint = createSprintFromResultSetEntry(resultSet);
            return sprint;
        }
        log.warn("Select: Sprint with Id " + id + " not found");
        return null;
    }
    
    private Sprint createSprintFromResultSetEntry(ResultSet rs) throws SQLException {
        Long rsId = rs.getLong("id");
        LocalDate rsStartDate = LocalDate.parse(rs.getString("startdate"));
        LocalDate rsEndDate = LocalDate.parse(rs.getString("enddate"));
        int rsNumberOfSprintDays = rs.getInt("numberofsprintdays");

        return new Sprint(rsId, rsStartDate, rsEndDate, rsNumberOfSprintDays);
    }
    
    public synchronized boolean create(Sprint sprint) throws SQLException {
        if(sprint == null) throw new NullPointerException("sprint");

        StringBuilder statement = new StringBuilder();
        statement.append("INSERT INTO sprint (id, startdate, enddate, numberofsprintdays) ");
        statement.append("VALUES (?, ?, ?, ?)");
        
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        dbStatement.setLong(1, sprint.getId());
        dbStatement.setString(2, sprint.getStartDate().toString());
        dbStatement.setString(3, sprint.getEndDate().toString());
        dbStatement.setLong(4, sprint.getNumberOfSprintDays());

        return (dbStatement.executeUpdate() > 0);
    }

    public synchronized boolean update(Sprint original, Sprint modified) throws SQLException {
        if(original == null) throw new NullPointerException("original");
        if(modified == null) throw new NullPointerException("modified");

        if(!Objects.equals(original.getId(), modified.getId())) {
            log.warn("Update: " + original.toString() + " with " + modified.toString() + "not possible, as Id different");
            return false;
        }
        
        StringBuilder statement = new StringBuilder();
        statement.append("UPDATE sprint ");
        statement.append("SET id = ?, startdate = ?, enddate = ?, numberofsprintdays = ? ");
        statement.append("WHERE id = ?;");
        
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        dbStatement.setLong(1, modified.getId());
        dbStatement.setString(2, modified.getStartDate().toString());
        dbStatement.setString(3, modified.getEndDate().toString());
        dbStatement.setLong(4, modified.getNumberOfSprintDays());
        dbStatement.setLong(5, original.getId());

        boolean result = dbStatement.executeUpdate() > 0;
        if(!result) {
            log.warn("Update: not possible, as " + original.toString() + " does not exist");
        }
        return result;
    }
    
    public synchronized boolean delete(Sprint sprint) throws SQLException {
        if(sprint == null) throw new NullPointerException("sprint");

        StringBuilder statement = new StringBuilder();
        statement.append("DELETE FROM sprint ");
        statement.append("WHERE id = ?");
        
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        dbStatement.setLong(1, sprint.getId());

        boolean result = dbStatement.executeUpdate() > 0;
        if(!result) {
            log.warn("Delete: not possible, as " + sprint.toString() + " does not exist");
        }
        return result;
    }
    
    public synchronized Long getNextId() throws SQLException{
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT seq ");
        statement.append("FROM SQLITE_SEQUENCE ");
        statement.append("WHERE name = 'Sprint';");

        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        ResultSet rs = dbStatement.executeQuery();
        if (rs.next()) {
            return rs.getLong(1) + 1;
        }
        return 0L;
    }    
    
}
