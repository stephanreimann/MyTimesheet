/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sqlite;

import model.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import org.apache.logging.log4j.*;

/**
 *
 * @author adrest18
 */
public class WorkrecordDAO {

    private final Logger log;
    private final Connection connection;

    public WorkrecordDAO(Connection connection) {
        if(connection == null) throw new NullPointerException("connection");
        
        this.log = LogManager.getLogger(WorkrecordDAO.class.getName());
        this.connection = connection;
    }
    
    public synchronized List<Workrecord> selectAll() throws SQLException {
        List<Workrecord> resultList = new ArrayList<>();
        
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT id, userid, projectid, date, starttime, endtime, worktime, overtime, overtimecorrection, vacationcorrection, worklocationid, description ");
        statement.append("FROM workrecord;");
        
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        ResultSet rs = dbStatement.executeQuery();
        while(rs.next()) {
            resultList.add(createWorkrecordFromResultSetEntry(rs));
        }
        return resultList;
    }
    
    public synchronized List<Workrecord> selectAll(User user, LocalDate startDate, LocalDate endDate) throws SQLException {
        List<Workrecord> resultList = new ArrayList<>();
        
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT id, userid, projectid, date, starttime, endtime, worktime, overtime, overtimecorrection, vacationcorrection, worklocationid, description ");
        statement.append("FROM workrecord ");
        statement.append("WHERE userid = ? ");
        statement.append("AND date BETWEEN ? AND ?;");

        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        dbStatement.setLong(1, user.getId());
        dbStatement.setString(2, startDate.toString());
        dbStatement.setString(3, endDate.toString());

        ResultSet rs = dbStatement.executeQuery();
        while(rs.next()) {
            resultList.add(createWorkrecordFromResultSetEntry(rs));
        }
        return resultList;
    }
    
    public synchronized List<Workrecord> selectAll(User user, LocalDate date, LocalTime startTime, LocalTime endTime) throws SQLException {
        List<Workrecord> resultList = new ArrayList<>();
        
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT id, userid, projectid, date, starttime, endtime, worktime, overtime, overtimecorrection, vacationcorrection, worklocationid, description ");
        statement.append("FROM workrecord ");
        statement.append("WHERE userid = ? ");
        statement.append("AND date = ? ");
        statement.append("AND starttime = ? ");
        statement.append("AND endtime = ?;");

        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        dbStatement.setLong(1, user.getId());
        dbStatement.setString(2, date.toString());
        dbStatement.setString(3, startTime.toString());
        dbStatement.setString(4, endTime.toString());

        ResultSet rs = dbStatement.executeQuery();
        while(rs.next()) {
            resultList.add(createWorkrecordFromResultSetEntry(rs));
        }
        return resultList;
    }
  
    public synchronized List<Workrecord> selectAll(User user, LocalDate date) throws SQLException {
        List<Workrecord> resultList = new ArrayList<>();
        
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT id, userid, projectid, date, starttime, endtime, worktime, overtime, overtimecorrection, vacationcorrection, worklocationid, description ");
        statement.append("FROM workrecord ");
        statement.append("WHERE userid = ? ");
        statement.append("AND date = ?;");

        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        dbStatement.setLong(1, user.getId());
        dbStatement.setString(2, date.toString());

        ResultSet rs = dbStatement.executeQuery();
        while(rs.next()) {
            resultList.add(createWorkrecordFromResultSetEntry(rs));
        }
        return resultList;
    }

    public synchronized List<Workrecord> selectAll(User user) throws SQLException {
        List<Workrecord> resultList = new ArrayList<>();
        
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT id, userid, projectid, date, starttime, endtime, worktime, overtime, overtimecorrection, vacationcorrection, worklocationid, description ");
        statement.append("FROM workrecord ");
        statement.append("WHERE userid = ?;");

        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        dbStatement.setLong(1, user.getId());

        ResultSet rs = dbStatement.executeQuery();
        while(rs.next()) {
            resultList.add(createWorkrecordFromResultSetEntry(rs));
        }
        return resultList;
    }

    public Workrecord selectWorkrecordFromId(long id) throws SQLException {
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT id, userid, projectid, date, starttime, endtime, worktime, overtime, overtimecorrection, vacationcorrection, worklocationid, description ");
        statement.append("FROM workrecord ");
        statement.append("WHERE id = ?;");
        
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        dbStatement.setString(1, Long.toString(id));
        ResultSet resultSet = dbStatement.executeQuery();
        while(resultSet.next()) {
            Workrecord workrecord = createWorkrecordFromResultSetEntry(resultSet);
            return workrecord;
        }
        log.warn("Select: Workrecord with Id " + id + " not found");
        
        return null;
    }
    
    private Workrecord createWorkrecordFromResultSetEntry(ResultSet resultSet) throws SQLException {
        long rsId = resultSet.getLong("id");
        long rsUserId = resultSet.getLong("userid");
        long rsProjectId = resultSet.getLong("projectid");
        LocalDate rsDate = LocalDate.parse(resultSet.getString("date"));
        LocalTime rsStartTime = LocalTime.parse(resultSet.getString("starttime"));
        LocalTime rsEndTime = LocalTime.parse(resultSet.getString("endtime"));
        LocalTime rsWorkTime = LocalTime.parse(resultSet.getString("worktime"));
        String rsOverTime = resultSet.getString("overtime");
        String rsOverTimeCorrection = resultSet.getString("overtimecorrection");
        int rsVacationCorrection = resultSet.getInt("vacationcorrection");
        long rsWorklocationId = resultSet.getLong("worklocationid");
        String rsDescription = resultSet.getString("description");

        UserDAO userDao = new UserDAO(connection);
        User user = userDao.selectUserFromId(rsUserId);
            
        ProjectDAO projectDao = new ProjectDAO(connection);
        Project project = projectDao.selectProjectFromId(rsProjectId);
            
        WorklocationDAO worklocationDAO = new WorklocationDAO(connection);
        Worklocation worklocation = worklocationDAO.selectWorklocationFromId(rsWorklocationId);
            
        return new Workrecord(rsId, user, project, rsDate, rsStartTime, rsEndTime, rsWorkTime, rsOverTime, rsOverTimeCorrection, rsVacationCorrection, worklocation, rsDescription);
    }
    
    public synchronized boolean create(Workrecord workrecord) throws SQLException {
        if(workrecord == null) throw new NullPointerException("user");

        StringBuilder statement = new StringBuilder();
        statement.append("INSERT INTO workrecord (id, userid, projectid, date, starttime, endtime, worktime, overtime, overtimecorrection, vacationcorrection, worklocationid, description) ");
        statement.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        dbStatement.setLong(1, workrecord.getId());
        dbStatement.setLong(2, workrecord.getUser().getId());
        dbStatement.setLong(3, workrecord.getProject().getId());
        dbStatement.setString(4, workrecord.getDate().toString());
        dbStatement.setString(5, workrecord.getStarttime().toString());
        dbStatement.setString(6, workrecord.getEndtime().toString());
        dbStatement.setString(7, workrecord.getWorktime().toString());
        dbStatement.setString(8, workrecord.getOvertime());
        dbStatement.setString(9, workrecord.getOvertimecorrection());
        dbStatement.setLong(10, workrecord.getVacationcorrection());
        dbStatement.setLong(11, workrecord.getWorklocation().getId());
        dbStatement.setString(12, workrecord.getDescription());

        return (dbStatement.executeUpdate() > 0);
    }

    public synchronized boolean update(Workrecord original, Workrecord modified) throws SQLException {
        if(original == null) throw new NullPointerException("original");
        if(modified == null) throw new NullPointerException("modified");

        if(!Objects.equals(original.getId(), modified.getId())) {
            log.warn("Update: " + original.toString() + " with " + modified.toString() + "not possible, as Id different");
            return false;
        }
        
        StringBuilder statement = new StringBuilder();
        statement.append("UPDATE workrecord ");
        statement.append("SET id = ?, userid = ?, projectid = ?, date = ?, starttime = ?, endtime = ?, worktime = ?, overtime = ?, overtimecorrection = ?, vacationcorrection = ?, worklocationid = ?, description = ? ");
        statement.append("WHERE id = ?;");
        
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        dbStatement.setLong(1, modified.getId());
        dbStatement.setLong(2, modified.getUser().getId());
        dbStatement.setLong(3, modified.getProject().getId());
        dbStatement.setString(4, modified.getDate().toString());
        dbStatement.setString(5, modified.getStarttime().toString());
        dbStatement.setString(6, modified.getEndtime().toString());
        dbStatement.setString(7, modified.getWorktime().toString());
        dbStatement.setString(8, modified.getOvertime());
        dbStatement.setString(9, modified.getOvertimecorrection());
        dbStatement.setLong(10, modified.getVacationcorrection());
        dbStatement.setLong(11, modified.getWorklocation().getId());
        dbStatement.setString(12, modified.getDescription());
        dbStatement.setLong(13, original.getId());

        boolean result = dbStatement.executeUpdate() > 0;
        if(!result) {
            log.warn("Update: not possible, as " + original.toString() + " does not exist");
        }
        return result;
    }
    
    public synchronized boolean delete(Workrecord workrecord) throws SQLException {
        if(workrecord == null) throw new NullPointerException("user");

        StringBuilder statement = new StringBuilder();
        statement.append("DELETE FROM workrecord ");
        statement.append("WHERE id = ?");
        
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        dbStatement.setLong(1, workrecord.getId());

        boolean result = dbStatement.executeUpdate() > 0;
        if(!result) {
            log.warn("Delete: not possible, as " + workrecord.toString() + " does not exist");
        }
        return result;
    }
    
    public synchronized Long getNextId() throws SQLException{
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT seq ");
        statement.append("FROM SQLITE_SEQUENCE ");
        statement.append("WHERE name = 'Workrecord';");

        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        ResultSet rs = dbStatement.executeQuery();
        if (rs.next()) {
            return rs.getLong(1) + 1;
        }
        return 0L;
    }    
    
}
