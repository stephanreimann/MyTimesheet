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
        try(PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            ResultSet rs = dbStatement.executeQuery();
            while(rs.next()) {
                resultList.add(createWorkrecordFromResultSetEntry(rs));
            }
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.debug(String.format("WorkrecordDAO.selectAll() returns %d workrecords.", resultList.size()));
            log.debug("Elapsed time: " + timeElapsed + "ms");        
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

        try(PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
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

        try(PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
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
            log.debug(String.format("WorkrecordDAO.selectAll(%s, %s, %s, %s) returns %d workrecords.",user.getLastname(),date, startTime, endTime, resultList.size()));        
            log.debug("Elapsed time: " + timeElapsed + "ms");
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

        try(PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            dbStatement.setLong(1, user.getId());
            dbStatement.setString(2, date.toString());

            Instant start = Instant.now();
            ResultSet rs = dbStatement.executeQuery();
            while(rs.next()) {
                resultList.add(createWorkrecordFromResultSetEntry(rs));
            }
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.debug(String.format("WorkrecordDAO.selectAll(%s, %s) returns %d workrecords.",user.getLastname(),date, resultList.size()));        
            log.debug("Elapsed time: " + timeElapsed + "ms");
        }
        return resultList;
    }

    public synchronized List<Workrecord> selectAll(User user) throws SQLException {
        List<Workrecord> resultList = new ArrayList<>();
        
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT id, userid, projectid, date, starttime, endtime, worktime, overtime, overtimecorrection, vacationcorrection, worklocationid, description ");
        statement.append("FROM workrecord ");
        statement.append("WHERE userid = ?;");

        try(PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
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
        }
        return resultList;
    }

    public Optional<Workrecord> selectWorkrecordFromId(long id) throws SQLException {
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT id, userid, projectid, date, starttime, endtime, worktime, overtime, overtimecorrection, vacationcorrection, worklocationid, description ");
        statement.append("FROM workrecord ");
        statement.append("WHERE id = ?;");
        
        try(PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            dbStatement.setLong(1, id);

            Instant start = Instant.now();
            ResultSet resultSet = dbStatement.executeQuery();
            while(resultSet.next()) {
                Workrecord workrecord = createWorkrecordFromResultSetEntry(resultSet);
                Instant finish = Instant.now();
                long timeElapsed = Duration.between(start, finish).toMillis();
                log.debug(String.format("WorkrecordDAO.selectWorkrecordFromId(%d) returns 1 workrecord.", id));
                log.debug("Elapsed time: " + timeElapsed + "ms");
                return Optional.of(workrecord);
            }
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.debug(String.format("WorkrecordDAO.selectWorkrecordFromId(%d) returns 0 workrecords.", id));
            log.debug("Elapsed time: " + timeElapsed + "ms");
            log.warn(String.format("Select: Workrecord with Id %d not found.", id));
        }
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
            
        return new Workrecord(rsId, user, project, rsDate, rsStartTime, rsEndTime, rsWorkTime, rsOverTime, rsOverTimeCorrection, rsVacationCorrection, worklocation, rsDescription);
    }
    
    public synchronized boolean create(Workrecord workrecord) throws SQLException {
        if(workrecord == null) throw new NullPointerException("workrecord");

        boolean result;
        StringBuilder statement = new StringBuilder();
        statement.append("INSERT INTO workrecord (id, userid, projectid, date, starttime, endtime, worktime, overtime, overtimecorrection, vacationcorrection, worklocationid, description) ");
        statement.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        
        try(PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
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

            Instant start = Instant.now();
            result = dbStatement.executeUpdate() > 0;
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.debug(String.format("WorkrecordDAO.create returns %b.", result));
            log.debug("Elapsed time: " + timeElapsed + "ms");
        }
        return result;
    }

    public synchronized boolean update(Workrecord original, Workrecord modified) throws SQLException {
        if(original == null) throw new NullPointerException("original");
        if(modified == null) throw new NullPointerException("modified");

        if (!Objects.equals(original.getId(), modified.getId())) {
            log.warn("Update: " + original.toString() + " with " + modified.toString() + " not possible, as Id different");
            return false;
        }
        
        boolean result;
        StringBuilder statement = new StringBuilder();
        statement.append("UPDATE workrecord ");
        statement.append("SET id = ?, userid = ?, projectid = ?, date = ?, starttime = ?, endtime = ?, worktime = ?, overtime = ?, overtimecorrection = ?, vacationcorrection = ?, worklocationid = ?, description = ? ");
        statement.append("WHERE id = ?;");
        
        try(PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
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

            Instant start = Instant.now();
            result = dbStatement.executeUpdate() > 0;
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.debug(String.format("WorkrecordDAO.update returns %b.", result));
            log.debug("Elapsed time: " + timeElapsed + "ms");
            if(!result) {
                log.warn(String.format("Update: not possible, as %s does not exist.", original));
            }
        }
        return result;
    }
    
    public synchronized boolean delete(Workrecord workrecord) throws SQLException {
        if(workrecord == null) throw new NullPointerException("workrecord");

        boolean result;
        StringBuilder statement = new StringBuilder();
        statement.append("DELETE FROM workrecord ");
        statement.append("WHERE id = ?");
        
        try(PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            dbStatement.setLong(1, workrecord.getId());

            Instant start = Instant.now();
            result = dbStatement.executeUpdate() > 0;
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.debug(String.format("WorkrecordDAO.delete returns %b.", result));
            log.debug("Elapsed time: " + timeElapsed + "ms");
            if(!result) {
                log.warn(String.format("Delete: not possible, as %s does not exist.", workrecord));
            }
        }
        return result;
    }
    
    public synchronized Long getNextId() throws SQLException{
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT seq ");
        statement.append("FROM SQLITE_SEQUENCE ");
        statement.append("WHERE name = 'workrecord';");

        try(PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            ResultSet rs = dbStatement.executeQuery();
            if (rs.next()) {
                return rs.getLong(1) + 1;
            }
        }
        return 0L;
    }    
    
}
