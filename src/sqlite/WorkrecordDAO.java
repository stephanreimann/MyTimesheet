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
    
    public WorkrecordDAO(Connection connection) {
        if(connection == null) throw new NullPointerException("connection");
        
        this.log = LogManager.getLogger(WorkrecordDAO.class.getName());
        this.connection = connection;
    }
    
    private StringBuilder getBaseSelectStatement() {
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT ");
        statement.append("wr.id AS wr_id, wr.userid AS wr_userid, wr.projectid AS wr_projectid, wr.date AS wr_date, wr.starttime AS wr_starttime, wr.endtime AS wr_endtime, wr.worktime AS wr_worktime, wr.overtime AS wr_overtime, wr.overtimecorrection AS wr_overtimecorrection, wr.vacationcorrection AS wr_vacationcorrection, wr.worklocationid AS wr_worklocationid, wr.description AS wr_description, ");
        statement.append("u.id AS u_id, u.roleid AS u_roleid, u.addressid AS u_addressid, u.contractid AS u_contractid, u.firstname AS u_firstname, u.lastname AS u_lastname, u.login AS u_login, u.password AS u_password, u.vacationleft AS u_vacationleft, ");
        statement.append("r.id AS r_id, r.name AS r_name, r.Description AS r_Description, ");
        statement.append("a.id AS a_id, a.streetname AS a_streetname, a.housenumber AS a_housenumber, a.unitname AS a_unitname, a.unitnumber AS a_unitnumber, a.unitLocation AS a_unitLocation, a.city As a_city, a.state AS a_state, a.zipcode AS a_zipcode, a.country AS a_country, ");
        statement.append("c.id AS c_id, c.name AS c_name, c.workhours AS c_workhours, c.maxworkhours AS c_maxworkhours, c.vacationdays AS c_vacationdays, c.vacationreconciliationdate AS c_vacationreconciliationdate, c.breakfastofftimeend AS c_breakfastofftimeend, c.breakfastofftimestart AS c_breakfastofftimestart, c.lunchofftimeend AS c_lunchofftimeend, c.lunchofftimestart AS c_lunchofftimestart, c.earliestworktimestart AS c_earliestworktimestart, c.latestworktimeend AS c_latestworktimeend, ");
        statement.append("p.id AS p_id, p.name AS p_name, p.costunit AS p_costunit, p.isworktimerelevant AS p_isworktimerelevant, p.isvacationrelevant AS p_isvacationrelevant, p.iscomptimerelevant AS p_iscomptimerelevant, p.description AS p_description, ");
        statement.append("wl.id AS wl_id, wl.name AS wl_name, wl.description AS wl_description ");
        statement.append("FROM workrecord wr ");
        statement.append("JOIN user u ON wr.userid = u.id ");
        statement.append("JOIN project p ON wr.projectid = p.id ");
        statement.append("JOIN worklocation wl ON wr.worklocationid = wl.id ");
        statement.append("JOIN role r ON u.roleid = r.id ");
        statement.append("JOIN address a ON u.addressid = a.id ");
        statement.append("JOIN contract c ON u.contractid = c.id ");        
        return statement;
    }
    
    public List<Workrecord> selectAll() throws SQLException {
        List<Workrecord> resultList = new ArrayList<>();
        
        StringBuilder statement = getBaseSelectStatement();
        statement.append(";");
        
        try(PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            Instant start = Instant.now();
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
    
    public List<Workrecord> selectAll(User user, LocalDate startDate, LocalDate endDate) throws SQLException {
        List<Workrecord> resultList = new ArrayList<>();
        
        StringBuilder statement = getBaseSelectStatement();
        statement.append("WHERE wr.userid = ? ");
        statement.append("AND wr.date BETWEEN ? AND ?;");
        
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
    
    public List<Workrecord> selectAll(User user, LocalDate date, LocalTime startTime, LocalTime endTime) throws SQLException {
        List<Workrecord> resultList = new ArrayList<>();
        
        StringBuilder statement = getBaseSelectStatement();
        statement.append("WHERE wr.userid = ? ");
        statement.append("AND wr.date = ? ");
        statement.append("AND wr.starttime = ? ");
        statement.append("AND wr.endtime = ?;");

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
  
    public List<Workrecord> selectAll(User user, LocalDate date) throws SQLException {
        List<Workrecord> resultList = new ArrayList<>();

        if(user == null) {
            return resultList;
        }
        
        StringBuilder statement = getBaseSelectStatement();
        statement.append("WHERE wr.userid = ? ");
        statement.append("AND wr.date = ?;");
        
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

    public List<Workrecord> selectAll(User user) throws SQLException {
        List<Workrecord> resultList = new ArrayList<>();
        
        StringBuilder statement = getBaseSelectStatement();
        statement.append("WHERE wr.userid = ?;");

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
        StringBuilder statement = getBaseSelectStatement();
        statement.append("WHERE wr.id = ?;");
        
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
        long rsId = resultSet.getLong("wr_id");
        long rsUserId = resultSet.getLong("wr_userid");
        long rsProjectId = resultSet.getLong("wr_projectid");
        LocalDate rsDate = LocalDate.parse(resultSet.getString("wr_date"));
        LocalTime rsStartTime = LocalTime.parse(resultSet.getString("wr_starttime"));
        LocalTime rsEndTime = LocalTime.parse(resultSet.getString("wr_endtime"));
        LocalTime rsWorkTime = LocalTime.parse(resultSet.getString("wr_worktime"));
        String rsOverTime = resultSet.getString("wr_overtime");
        String rsOverTimeCorrection = resultSet.getString("wr_overtimecorrection");
        int rsVacationCorrection = resultSet.getInt("wr_vacationcorrection");
        long rsWorklocationId = resultSet.getLong("wr_worklocationid");
        String rsDescription = resultSet.getString("wr_description");

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
        
        Project project = new Project(resultSet.getLong("p_id"));         
        project.setName(resultSet.getString("p_name"));
        project.setCostunit(resultSet.getString("p_costunit"));
        project.setIsWorktimeRelevant(resultSet.getString("p_isworktimerelevant"));
        project.setIsVacationRelevant(resultSet.getString("p_isvacationrelevant"));
        project.setIsComptimeRelevant(resultSet.getString("p_iscomptimerelevant"));
        project.setDescription(resultSet.getString("p_description"));
        
        Worklocation worklocation = new Worklocation(resultSet.getLong("wl_id"));
        worklocation.setName(resultSet.getString("wl_name"));
        worklocation.setDescription(resultSet.getString("wl_description"));
            
        return new Workrecord(rsId, user, project, rsDate, rsStartTime, rsEndTime, rsWorkTime, rsOverTime, rsOverTimeCorrection, rsVacationCorrection, worklocation, rsDescription);
    }
    
    public boolean create(Workrecord workrecord) throws SQLException {
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

    public boolean update(Workrecord original, Workrecord modified) throws SQLException {
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
    
    public boolean delete(Workrecord workrecord) throws SQLException {
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
    
    public Long getNextId() throws SQLException{
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT seq ");
        statement.append("FROM SQLITE_SEQUENCE ");
        statement.append("WHERE name = 'Workrecord';");

        try(PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            ResultSet rs = dbStatement.executeQuery();
            if (rs.next()) {
                return rs.getLong(1) + 1;
            }
        }
        return 0L;
    }    
    
}
