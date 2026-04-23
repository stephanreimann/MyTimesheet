/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sqlite;

import model.*;
import java.sql.*;
import java.time.Duration;
import java.time.Instant;
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
    private final UserDAO userDao;
    private final ProjectDAO projectDao;
    private final WorklocationDAO worklocationDAO;
    
    public WorkrecordDAO(Connection connection) {
        if(connection == null) throw new NullPointerException("connection");
        
        this.log = LogManager.getLogger(WorkrecordDAO.class.getName());
        this.connection = connection;
        this.userDao = new UserDAO(connection);
        this.projectDao = new ProjectDAO(connection);
        this.worklocationDAO = new WorklocationDAO(connection);
    }
    
    public synchronized List<Workrecord> selectAll() throws SQLException {
        List<Workrecord> resultList = new ArrayList<>();
        
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT id, userid, projectid, date, starttime, endtime, worktime, overtime, overtimecorrection, vacationcorrection, worklocationid, description ");
        statement.append("FROM workrecord;");
        
        Instant start = Instant.now();
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        ResultSet rs = dbStatement.executeQuery();
        while(rs.next()) {
            resultList.add(createWorkrecordFromResultSetEntry(rs));
        }
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        log.debug("WorkrecordDAO.selectAll() returns " + resultList.size() + " workrecords.");
        log.debug("Elapsed time: " + timeElapsed + "ms");        
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

        Instant start = Instant.now();
        ResultSet rs = dbStatement.executeQuery();
        while(rs.next()) {
            resultList.add(createWorkrecordFromResultSetEntry(rs));
        }
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        
        log.debug(String.format("WorkrecordDAO.selectAll(%s, %s, %s) returns %d workrecords.",user.getLastname(), startDate, endDate, resultList.size()));        
        log.debug("Elapsed time: " + timeElapsed + "ms");
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

        Instant start = Instant.now();
        ResultSet rs = dbStatement.executeQuery();
        while(rs.next()) {
            resultList.add(createWorkrecordFromResultSetEntry(rs));
        }
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        log.debug(String.format("WorkrecordDAO.selectAll(%s, %s, %s, %s) returns %d workrecords.",user.getLastname(),date.toString(), startTime.toString(), endTime.toString(), resultList.size()));        
        log.debug("Elapsed time: " + timeElapsed + "ms");
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

        Instant start = Instant.now();
        ResultSet rs = dbStatement.executeQuery();
        while(rs.next()) {
            resultList.add(createWorkrecordFromResultSetEntry(rs));
        }
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        log.debug(String.format("WorkrecordDAO.selectAll(%s, %s) returns %d workrecords.",user.getLastname(),date.toString(), resultList.size()));        
        log.debug("WorkrecordDAO.selectAll("+ user.getLastname() + ", " + date.toString() + " returns " + resultList.size() + " workrecords.");
        log.debug("Elapsed time: " + timeElapsed + "ms");
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

        Instant start = Instant.now();
        ResultSet rs = dbStatement.executeQuery();
        while(rs.next()) {
            resultList.add(createWorkrecordFromResultSetEntry(rs));
        }
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        log.debug(String.format("WorkrecordDAO.selectAll(%s) returns %d workrecords.",user.getLastname(), resultList.size()));        
        log.debug("Elapsed time: " + timeElapsed + "ms");
        return resultList;
    }

    public Optional<Workrecord> selectWorkrecordFromId(long id) throws SQLException {
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT id, userid, projectid, date, starttime, endtime, worktime, overtime, overtimecorrection, vacationcorrection, worklocationid, description ");
        statement.append("FROM workrecord ");
        statement.append("WHERE id = ?;");
        
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        dbStatement.setLong(1, id);
        
        Instant start = Instant.now();
        ResultSet resultSet = dbStatement.executeQuery();
        while(resultSet.next()) {
            Workrecord workrecord = createWorkrecordFromResultSetEntry(resultSet);
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.debug("WorkrecordDAO.selectWorkrecordFromId returns 1 workrecords.");
            log.debug("Elapsed time: " + timeElapsed + "ms");
            return Optional.of(workrecord);
        }
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        log.debug("WorkrecordDAO.selectWorkrecordFromId returns 0 workrecords.");
        log.debug("Elapsed time: " + timeElapsed + "ms");
        log.warn("Select: Workrecord with Id " + id + " not found");
        
        return Optional.empty();
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

        User user = userDao.selectUserFromId(rsUserId);           
        Project project = projectDao.selectProjectFromId(rsProjectId);            
        Worklocation worklocation = worklocationDAO.selectWorklocationFromId(rsWorklocationId);
            
        Workrecord workrecord = new Workrecord(rsId, user, project, rsDate, rsStartTime, rsEndTime, rsWorkTime, rsOverTime, rsOverTimeCorrection, rsVacationCorrection, worklocation, rsDescription);
        return workrecord;
    }
    
    public synchronized boolean create(Workrecord workrecord) throws SQLException {
        if(workrecord == null) throw new NullPointerException("user");

        StringBuilder statement = new StringBuilder();
        statement.append("INSERT INTO workrecord (id, userid, projectid, date, starttime, endtime, worktime, overtime, overtimecorrection, vacationcorrection, worklocationid, description) ");
        statement.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        
        Instant start = Instant.now();
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

        boolean result = dbStatement.executeUpdate() > 0;
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        log.debug("WorkrecordDAO.create returns " + result + " workrecords.");
        log.debug("Elapsed time: " + timeElapsed + "ms");
        return result;
    }

    public synchronized boolean update(Workrecord original, Workrecord modified) throws SQLException {
        if(original == null) throw new NullPointerException("original");
        if(modified == null) throw new NullPointerException("modified");

        if (!Objects.equals(original.getId(), modified.getId())) {
            log.warn("Update: " + original.toString() + " with " + modified.toString() + " not possible, as Id different");
            return false;
        }
        
        StringBuilder statement = new StringBuilder();
        statement.append("UPDATE workrecord ");
        statement.append("SET id = ?, userid = ?, projectid = ?, date = ?, starttime = ?, endtime = ?, worktime = ?, overtime = ?, overtimecorrection = ?, vacationcorrection = ?, worklocationid = ?, description = ? ");
        statement.append("WHERE id = ?;");
        
        Instant start = Instant.now();
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
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        log.debug("WorkrecordDAO.update returns " + result + " worrkrecords.");
        log.debug("Elapsed time: " + timeElapsed + "ms");
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
        
        Instant start = Instant.now();
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        dbStatement.setLong(1, workrecord.getId());

        boolean result = dbStatement.executeUpdate() > 0;
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        log.debug("WorkrecordDAO.delete returns " + result + " workrecords.");
        log.debug("Elapsed time: " + timeElapsed + "ms");
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
