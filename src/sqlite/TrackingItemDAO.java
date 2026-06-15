/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sqlite;

import model.TrackingItem;
import java.sql.*;
import java.time.*;
import java.util.*;
import org.apache.logging.log4j.*;

/**
 *
 * @author adrest18
 */
public class TrackingItemDAO {
    
    private final Logger log;
    private final Connection connection;

    public TrackingItemDAO(Connection connection) {
        if(connection == null) throw new NullPointerException("connection");
        
        this.log = LogManager.getLogger(RoleDAO.class.getName());
        this.connection = connection;
    }

    public synchronized List<TrackingItem> selectAll() throws SQLException {
        List<TrackingItem> resultList = new ArrayList<>();
        
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT id, name, shortcut, description ");
        statement.append("FROM trackingitem;");
        
        Instant start = Instant.now();
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        ResultSet rs = dbStatement.executeQuery();
        while(rs.next()) {
            resultList.add(createTrackingItemFromResultSetEntry(rs));
        }
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        log.debug("TrackingItemDAO.selectAll() returns " + resultList.size() + " trackingitems.");
        log.debug("Elapsed time: " + timeElapsed + "ms");
        return resultList;
    }

    public TrackingItem selectTrackingItemFromId(long id) throws SQLException {
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT id, name, shortcut, description ");
        statement.append("FROM trackingitem ");
        statement.append("WHERE id = ?;");
        
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        dbStatement.setString(1, Long.toString(id));
        ResultSet resultSet = dbStatement.executeQuery();
        while(resultSet.next()) {
            TrackingItem trackingItem = createTrackingItemFromResultSetEntry(resultSet);
            return trackingItem;
        }
        log.warn("Select: TrackingItem with Id " + id + " not found");
        return null;
    }
    
    private TrackingItem createTrackingItemFromResultSetEntry(ResultSet rs) throws SQLException {
        Long rsId = rs.getLong("id");
        String rsName = rs.getString("name");
        String rsShortcut = rs.getString("shortcut");
        String rsDescription = rs.getString("description");

        return new TrackingItem(rsId, rsName, rsShortcut, rsDescription);
    }
    
    public synchronized boolean create(TrackingItem trackingItem) throws SQLException {
        if(trackingItem == null) throw new NullPointerException("trackingItem");

        StringBuilder statement = new StringBuilder();
        statement.append("INSERT INTO trackingitem (id, name, shortcut, description) ");
        statement.append("VALUES (?, ?, ?, ?)");
        
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        dbStatement.setLong(1, trackingItem.getId());
        dbStatement.setString(2, trackingItem.getName());
        dbStatement.setString(3, trackingItem.getShortcut());
        dbStatement.setString(4, trackingItem.getDescription());

        return (dbStatement.executeUpdate() > 0);
    }
    
    public synchronized boolean update(TrackingItem original, TrackingItem modified) throws SQLException {
        if(original == null) throw new NullPointerException("original");
        if(modified == null) throw new NullPointerException("modified");

        if(!Objects.equals(original.getId(), modified.getId())) {
            log.warn("Update: " + original.toString() + " with " + modified.toString() + "not possible, as Id different");
            return false;
        }
        
        StringBuilder statement = new StringBuilder();
        statement.append("UPDATE trackingitem ");
        statement.append("SET id = ?, name = ?, shortcut = ?, description = ? ");
        statement.append("WHERE id = ?;");
        
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        dbStatement.setLong(1, modified.getId());
        dbStatement.setString(2, modified.getName());
        dbStatement.setString(3, modified.getShortcut());
        dbStatement.setString(4, modified.getDescription());
        dbStatement.setLong(5, original.getId());

        boolean result = dbStatement.executeUpdate() > 0;
        if(!result) {
            log.warn("Update: not possible, as " + original.toString() + " does not exist");
        }
        return result;
    }
    
    public synchronized boolean delete(TrackingItem trackingItem) throws SQLException {
        if(trackingItem == null) throw new NullPointerException("role");

        StringBuilder statement = new StringBuilder();
        statement.append("DELETE FROM trackingitem ");
        statement.append("WHERE id = ?");
        
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        dbStatement.setLong(1, trackingItem.getId());

        boolean result = dbStatement.executeUpdate() > 0;
        if(!result) {
            log.warn("Delete: not possible, as " + trackingItem.toString() + " does not exist");
        }
        return result;
    }

    public synchronized Long getNextId() throws SQLException{
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT seq ");
        statement.append("FROM SQLITE_SEQUENCE ");
        statement.append("WHERE name = 'TrackingItem';");

        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        ResultSet rs = dbStatement.executeQuery();
        if (rs.next()) {
            return rs.getLong(1) + 1;
        }
        return 0L;
    }    
    
}
