/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sqlite;

import model.Project;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author adrest18
 */
public class ProjectDAO {

    private final Logger log;
    private final Connection connection;

    public ProjectDAO(Connection connection) {
        if(connection == null) throw new NullPointerException("connection");
        
        this.log = LogManager.getLogger(ProjectDAO.class.getName());
        this.connection = connection;
    }

    public synchronized List<Project> selectAll() throws SQLException {
        List<Project> resultList = new ArrayList<>();
        
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT id, name, costunit, isworktimerelevant, isvacationrelevant, iscomptimerelevant, description ");
        statement.append("FROM project;");
        
        Instant start = Instant.now();
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        ResultSet resultSet = dbStatement.executeQuery();
        while(resultSet.next()) {
            resultList.add(createProjectFromResultSetEntry(resultSet));
        }
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        log.debug("ProjectDAO.selectAll() returns " + resultList.size() + " projects.");
        log.debug("Elapsed time: " + timeElapsed + "ms");
        return resultList;
    }
    
    public Project selectProjectFromId(long id) throws SQLException {
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT id, name, costunit, isworktimerelevant, isvacationrelevant, iscomptimerelevant, description ");
        statement.append("FROM project ");
        statement.append("WHERE id = ?;");
        
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        dbStatement.setString(1, Long.toString(id));
        ResultSet resultSet = dbStatement.executeQuery();
        while(resultSet.next()) {
            Project project = createProjectFromResultSetEntry(resultSet);
            return project;
        }
        log.warn("Select: Project with Id " + id + " not found");
        return null;
    }
    
    private Project createProjectFromResultSetEntry(ResultSet resultSet) throws SQLException {
        Long rsId = resultSet.getLong("id");
        String rsName = resultSet.getString("name");
        String rsCostunit = resultSet.getString("costunit");
        String rsIsWorktimeRelevant = resultSet.getString("isworktimerelevant");
        String rsIsVacationRelevant = resultSet.getString("isvacationrelevant");
        String rsIsComptimeRelevant = resultSet.getString("iscomptimerelevant");
        String rsDescription = resultSet.getString("description");

        return new Project(rsId, rsName, rsCostunit, rsIsWorktimeRelevant, rsIsVacationRelevant, rsIsComptimeRelevant, rsDescription);
    }
    
    public synchronized boolean create(Project project) throws SQLException {
        if(project == null) throw new NullPointerException("contract");

        StringBuilder statement = new StringBuilder();
        statement.append("INSERT INTO project (id, name, costunit, isworktimerelevant, isvacationrelevant, iscomptimerelevant, description) ");
        statement.append("VALUES (?, ?, ?, ?, ?, ?, ?)");
        
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        dbStatement.setLong(1, project.getId());
        dbStatement.setString(2, project.getName());
        dbStatement.setString(3, project.getCostunit());
        dbStatement.setString(4, project.getIsWorktimeRelevant());
        dbStatement.setString(5, project.getIsVacationRelevant());
        dbStatement.setString(6, project.getIsComptimeRelevant());
        dbStatement.setString(7, project.getDescription());

        return (dbStatement.executeUpdate() > 0);
    }
    
    public synchronized boolean update(Project original, Project modified) throws SQLException {
        if(original == null) throw new NullPointerException("original");
        if(modified == null) throw new NullPointerException("modified");

        if(!Objects.equals(original.getId(), modified.getId())) {
            log.warn("Update: " + original.toString() + " with " + modified.toString() + "not possible, as Id different");
            return false;
        }
        
        StringBuilder statement = new StringBuilder();
        statement.append("UPDATE project ");
        statement.append("SET id = ?, name = ?, costunit = ?, isworktimerelevant = ?, isvacationrelevant = ?, iscomptimerelevant = ?, description = ? ");
        statement.append("WHERE id = ?;");
        
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        dbStatement.setLong(1, modified.getId());
        dbStatement.setString(2, modified.getName());
        dbStatement.setString(3, modified.getCostunit());
        dbStatement.setString(4, modified.getIsWorktimeRelevant());
        dbStatement.setString(5, modified.getIsVacationRelevant());
        dbStatement.setString(6, modified.getIsComptimeRelevant());
        dbStatement.setString(7, modified.getDescription());
        dbStatement.setLong(8, original.getId());

        boolean result = dbStatement.executeUpdate() > 0;
        if(!result) {
            log.warn("Update: not possible, as " + original.toString() + " does not exist");
        }
        return result;
    }
    
    public synchronized boolean delete(Project project) throws SQLException {
        if(project == null) throw new NullPointerException("contract");

        StringBuilder statement = new StringBuilder();
        statement.append("DELETE FROM project ");
        statement.append("WHERE id = ?");
        
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        dbStatement.setLong(1, project.getId());

        boolean result = dbStatement.executeUpdate() > 0;
        if(!result) {
            log.warn("Delete: not possible, as " + project.toString() + " does not exist");
        }
        return result;
    }
    
    public synchronized Long getNextId() throws SQLException{
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT seq ");
        statement.append("FROM SQLITE_SEQUENCE ");
        statement.append("WHERE name = 'Project';");

        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        ResultSet rs = dbStatement.executeQuery();
        if (rs.next()) {
            return rs.getLong(1) + 1;
        }
        return 0L;
    }    
    
}
