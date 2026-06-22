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
        statement.append("SELECT ");
        statement.append("wi.id AS wi_id, wi.userid AS wi_userid, wi.workrecordid AS wi_workrecordid, wi.sprintid AS wi_sprintid, wi.trackingitemid AS wi_trackingitemid, wi.starttime AS wi_starttime, wi.endtime AS wi_endtime, wi.description AS wi_description, ");
        statement.append("u.id AS u_id, u.roleid AS u_roleid, u.addressid AS u_addressid, u.contractid AS u_contractid, u.firstname AS u_firstname, u.lastname AS u_lastname, u.login AS u_login, u.password AS u_password, u.vacationleft AS u_vacationleft, ");
        statement.append("r.id AS r_id, r.name AS r_name, r.Description AS r_Description, ");
        statement.append("a.id AS a_id, a.streetname AS a_streetname, a.housenumber AS a_housenumber, a.unitname AS a_unitname, a.unitnumber AS a_unitnumber, a.unitLocation AS a_unitLocation, a.city As a_city, a.state AS a_state, a.zipcode AS a_zipcode, a.country AS a_country, ");
        statement.append("c.id AS c_id, c.name AS c_name, c.workhours AS c_workhours, c.maxworkhours AS c_maxworkhours, c.vacationdays AS c_vacationdays, c.vacationreconciliationdate AS c_vacationreconciliationdate, c.breakfastofftimeend AS c_breakfastofftimeend, c.breakfastofftimestart AS c_breakfastofftimestart, c.lunchofftimeend AS c_lunchofftimeend, c.lunchofftimestart AS c_lunchofftimestart, c.earliestworktimestart AS c_earliestworktimestart, c.latestworktimeend AS c_latestworktimeend, ");
        statement.append("p.id AS p_id, p.name AS p_name, p.costunit AS p_costunit, p.isworktimerelevant AS p_isworktimerelevant, p.isvacationrelevant AS p_isvacationrelevant, p.iscomptimerelevant AS p_iscomptimerelevant, p.description AS p_description, ");
        statement.append("wr.id AS wr_id, wr.userid AS wr_userid, wr.projectid AS wr_projectid, wr.date AS wr_date, wr.starttime AS wr_starttime, wr.endtime AS wr_endtime, wr.worktime AS wr_worktime, wr.overtime AS wr_overtime, wr.overtimecorrection AS wr_overtimecorrection, wr.vacationcorrection AS wr_vacationcorrection, wr.worklocationid AS wr_worklocationid, wr.description AS wr_description, ");
        statement.append("wl.id AS wl_id, wl.name AS wl_name, wl.description AS wl_description, ");
        statement.append("s.id AS s_id, s.startdate AS s_startdate, s.enddate AS s_enddate, s.numberofsprintdays AS s_numberofsprintdays, ");
        statement.append("ti.id AS ti_id, ti.name AS ti_name, ti.shortcut AS ti_shortcut, ti.description AS ti_description ");
        statement.append("FROM workitem wi ");
        statement.append("JOIN user u ON wi.userid = u.id ");
        statement.append("JOIN role r ON u.roleid = r.id ");
        statement.append("JOIN address a ON u.addressid = a.id ");
        statement.append("JOIN contract c ON u.contractid = c.id ");        
        statement.append("JOIN project p ON wr.projectid = p.id ");
        statement.append("JOIN workrecord wr ON wi.workrecordid = wr.id ");
        statement.append("JOIN worklocation wl ON wr.worklocationid = wl.id ");
        statement.append("JOIN sprint s ON wi.sprintid = s.id ");
        statement.append("JOIN trackingitem ti ON wi.trackingitemid = ti.id ");
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

    public List<WorkItem> selectAll(User user, Sprint sprint) throws SQLException {
        List<WorkItem> resultList = new ArrayList<>();
        
        StringBuilder statement = getBaseSelectStatement();
        statement.append("WHERE u.id = ? ");
        statement.append("AND s.id = ?;");
        
        try(PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            dbStatement.setLong(1, user.getId());
            dbStatement.setLong(2, sprint.getId());

            Instant start = Instant.now();
            ResultSet rs = dbStatement.executeQuery();
            while(rs.next()) {
                resultList.add(createWorkItemFromResultSetEntry(rs));
            }
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.debug(String.format("WorkItemDAO.selectAll(%s, %s) returns %d workItems.",user.getLastname(),sprint.getId(), resultList.size()));        
            log.debug("Elapsed time: " + timeElapsed + "ms");
        }
        return resultList;
    }
    
    private WorkItem createWorkItemFromResultSetEntry(ResultSet resultSet) throws SQLException {
        long rsId = resultSet.getLong("wi_id");
        long rsUserId = resultSet.getLong("wi_userid");
        long rsWorkrecordId = resultSet.getLong("wi_workrecordid");
        long rsSprintId = resultSet.getLong("wi_sprintid");
        long rsTrackingItemId = resultSet.getLong("wi_trackingitemid");
        LocalTime rsStartTime = LocalTime.parse(resultSet.getString("wi_starttime"));
        LocalTime rsEndTime = LocalTime.parse(resultSet.getString("wi_endtime"));
        String rsDescription = resultSet.getString("wi_description");
      
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
        
        Workrecord workrecord = new Workrecord(resultSet.getLong("wr_id"));
        workrecord.setUser(user);
        workrecord.setProject(project);
        workrecord.setDate(LocalDate.parse(resultSet.getString("wr_date")));
        workrecord.setStarttime(LocalTime.parse(resultSet.getString("wr_starttime")));
        workrecord.setEndtime(LocalTime.parse(resultSet.getString("wr_endtime")));
        workrecord.setWorktime(LocalTime.parse(resultSet.getString("wr_worktime")));
        workrecord.setOvertime(resultSet.getString("wr_overtime"));
        workrecord.setOvertimecorrection(resultSet.getString("wr_overtimecorrection"));
        workrecord.setVacationcorrection(resultSet.getInt("wr_vacationcorrection"));
        workrecord.setWorklocation(worklocation);
        workrecord.setDescription(resultSet.getString("wr_description"));
        
        Sprint sprint = new Sprint(resultSet.getLong("s_id"));
        sprint.setStartDate(LocalDate.parse(resultSet.getString("s_startdate")));
        sprint.setEndDate(LocalDate.parse(resultSet.getString("s_enddate")));
        sprint.setNumberOfSprintDays(resultSet.getInt("s_numberofsprintdays"));
        
        TrackingItem trackingItem = new TrackingItem(resultSet.getLong("ti_id"));
        trackingItem.setName(resultSet.getString("ti_name"));
        trackingItem.setShortcut(resultSet.getString("ti_shortcut"));
        trackingItem.setDescription(resultSet.getString("ti_description"));
                
        return new WorkItem(rsId, user, workrecord, sprint, trackingItem, rsStartTime, rsEndTime, rsDescription);
    }
    
    public boolean create(WorkItem workItem) throws SQLException {
        if(workItem == null) throw new NullPointerException("workItem");

        boolean result;
        StringBuilder statement = new StringBuilder();
        statement.append("INSERT INTO workitem (id, userid, workrecordid, sprintid, trackingitemid, starttime, endtime, description) ");
        statement.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        
        try(PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            dbStatement.setLong(1, workItem.getId());
            dbStatement.setLong(2, workItem.getUser().getId());
            dbStatement.setLong(3, workItem.getWorkrecord().getId());
            dbStatement.setLong(4, workItem.getSprint().getId());
            dbStatement.setLong(5, workItem.getTrackingItem().getId());
            dbStatement.setString(6, workItem.getStartTime().toString());
            dbStatement.setString(7, workItem.getEndTime().toString());
            dbStatement.setString(8, workItem.getDescription());

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
        statement.append("SET id = ?, userid = ?, workrecordid = ?, sprintid = ?, trackingitemid = ?, starttime = ?, endtime = ?, description = ? ");
        statement.append("WHERE id = ?;");
        
        try(PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            dbStatement.setLong(1, modified.getId());
            dbStatement.setLong(2, modified.getUser().getId());
            dbStatement.setLong(3, modified.getWorkrecord().getId());
            dbStatement.setLong(4, modified.getSprint().getId());
            dbStatement.setLong(5, modified.getTrackingItem().getId());
            dbStatement.setString(6, modified.getStartTime().toString());
            dbStatement.setString(7, modified.getEndTime().toString());
            dbStatement.setString(8, modified.getDescription());

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
