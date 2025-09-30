/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sqlite;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import model.Holyday;
import org.apache.logging.log4j.*;

/**
 *
 * @author adrest18
 */
public class HolydayDAO {

    private final Logger log;
    private final Connection connection;
    
    public HolydayDAO(Connection connection) {
        if(connection == null) throw new NullPointerException("connection");
        
        this.log = LogManager.getLogger(RoleDAO.class.getName());
        this.connection = connection;
    }

    public synchronized List<Holyday> selectAll() throws SQLException {
        List<Holyday> resultList = new ArrayList<>();
        
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT id, date, name, state ");
        statement.append("FROM holyday;");
        
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        ResultSet rs = dbStatement.executeQuery();
        while(rs.next()) {
            resultList.add(createHolydayFromResultSetEntry(rs));
        }
        return resultList;
    }

    public Holyday selectHolydayFromId(long id) throws SQLException {
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT id, date, name, state ");
        statement.append("FROM holyday ");
        statement.append("WHERE id = ?;");
        
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        dbStatement.setString(1, Long.toString(id));
        ResultSet resultSet = dbStatement.executeQuery();
        while(resultSet.next()) {
            Holyday holyday = createHolydayFromResultSetEntry(resultSet);
            return holyday;
        }
        log.warn("Select: Holyday with Id " + id + " not found");
        return null;
    }
    
    private Holyday createHolydayFromResultSetEntry(ResultSet rs) throws SQLException {
        Long rsId = rs.getLong("id");
        LocalDate rsDate = LocalDate.parse(rs.getString("date"));
        String rsName = rs.getString("name");
        String rsState = rs.getString("state");

        return new Holyday(rsId, rsDate, rsName, rsState);
    }
    
    public synchronized boolean create(Holyday holyday) throws SQLException {
        if(holyday == null) throw new NullPointerException("holyday");

        StringBuilder statement = new StringBuilder();
        statement.append("INSERT INTO holyday (id, date, name, state) ");
        statement.append("VALUES (?, ?, ?, ?)");
        
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        dbStatement.setLong(1, holyday.getId());
        dbStatement.setString(2, holyday.getDate().toString());
        dbStatement.setString(3, holyday.getName());
        dbStatement.setString(4, holyday.getState());

        return (dbStatement.executeUpdate() > 0);
    }

    public synchronized boolean update(Holyday original, Holyday modified) throws SQLException {
        if(original == null) throw new NullPointerException("original");
        if(modified == null) throw new NullPointerException("modified");

        if(!Objects.equals(original.getId(), modified.getId())) {
            log.warn("Update: " + original.toString() + " with " + modified.toString() + "not possible, as Id different");
            return false;
        }
        
        StringBuilder statement = new StringBuilder();
        statement.append("UPDATE holyday ");
        statement.append("SET id = ?, date = ?, name = ?, state = ? ");
        statement.append("WHERE id = ?;");
        
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        dbStatement.setLong(1, modified.getId());
        dbStatement.setString(2, modified.getDate().toString());
        dbStatement.setString(3, modified.getName());
        dbStatement.setString(4, modified.getState());
        dbStatement.setLong(5, original.getId());

        boolean result = dbStatement.executeUpdate() > 0;
        if(!result) {
            log.warn("Update: not possible, as " + original.toString() + " does not exist");
        }
        return result;
    }
    
    public synchronized boolean delete(Holyday holyday) throws SQLException {
        if(holyday == null) throw new NullPointerException("holyday");

        StringBuilder statement = new StringBuilder();
        statement.append("DELETE FROM holyday ");
        statement.append("WHERE id = ?");
        
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        dbStatement.setLong(1, holyday.getId());

        boolean result = dbStatement.executeUpdate() > 0;
        if(!result) {
            log.warn("Delete: not possible, as " + holyday.toString() + " does not exist");
        }
        return result;
    }
    
    public synchronized Long getNextId() throws SQLException{
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT seq ");
        statement.append("FROM SQLITE_SEQUENCE ");
        statement.append("WHERE name = 'Holyday';");

        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        ResultSet rs = dbStatement.executeQuery();
        if (rs.next()) {
            return rs.getLong(1) + 1;
        }
        return 0L;
    }    
    
}
