/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sqlite;

import model.Contract;
import java.sql.*;
import java.time.LocalTime;
import java.util.*;
import org.apache.logging.log4j.*;

/**
 *
 * @author adrest18
 */
public class ContractDAO {

    private final Logger log;
    private final Connection connection;

    public ContractDAO(Connection connection) {
        if(connection == null) throw new NullPointerException("connection");
        
        this.log = LogManager.getLogger(UserDAO.class.getName());
        this.connection = connection;
    }
    
    public synchronized List<Contract> selectAll() throws SQLException {
        List<Contract> resultList = new ArrayList<>();
        
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT id, name, workhours, maxworkhours, vacationdays, vacationreconciliationdate, breakfastofftimeend, breakfastofftimestart, lunchofftimeend, lunchofftimestart, earliestworktimestart, latestworktimeend ");
        statement.append("FROM contract;");
        
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        ResultSet resultSet = dbStatement.executeQuery();
        while(resultSet.next()) {
            resultList.add(createContractFromResultSetEntry(resultSet));
        }
        return resultList;
    }

    public Contract selectContractFromId(long id) throws SQLException {
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT id, name, workhours, maxworkhours, vacationdays, vacationreconciliationdate, breakfastofftimeend, breakfastofftimestart, lunchofftimeend, lunchofftimestart, earliestworktimestart, latestworktimeend ");
        statement.append("FROM contract ");
        statement.append("WHERE id = ?;");
        
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        dbStatement.setString(1, Long.toString(id));
        ResultSet resultSet = dbStatement.executeQuery();
        while(resultSet.next()) {
            Contract contract = createContractFromResultSetEntry(resultSet);
            return contract;
        }
        log.warn("Select: Contract with Id " + id + " not found");
        return null;
    }
    
    private Contract createContractFromResultSetEntry(ResultSet resultSet) throws SQLException {
        Long rsId = resultSet.getLong("id");
        String rsName = resultSet.getString("name");
        Long rsWorkhours = resultSet.getLong("workhours");
        Long rsMaxworkhours = resultSet.getLong("maxworkhours");
        Long rsVacationdays = resultSet.getLong("vacationdays");
        String rsVacationreconciliationdate = resultSet.getString("vacationreconciliationdate");
        LocalTime rsBreakfastofftimeend = LocalTime.parse(resultSet.getString("breakfastofftimeend"));
        LocalTime rsBreakfastofftimestart = LocalTime.parse(resultSet.getString("breakfastofftimestart"));
        LocalTime rsLunchofftimeend = LocalTime.parse(resultSet.getString("lunchofftimeend"));
        LocalTime rsLunchofftimestart = LocalTime.parse(resultSet.getString("lunchofftimestart"));
        LocalTime rsEarliestworktimestart = LocalTime.parse(resultSet.getString("earliestworktimestart"));
        LocalTime rsLatestworktimeend = LocalTime.parse(resultSet.getString("latestworktimeend"));

        return new Contract(rsId, rsName, rsWorkhours, rsMaxworkhours, rsVacationdays, rsVacationreconciliationdate, rsBreakfastofftimeend, rsBreakfastofftimestart, rsLunchofftimeend, rsLunchofftimestart, rsEarliestworktimestart, rsLatestworktimeend);
    }

    public synchronized boolean create(Contract contract) throws SQLException {
        if(contract == null) throw new NullPointerException("contract");

        StringBuilder statement = new StringBuilder();
        statement.append("INSERT INTO contract (id, name, workhours, maxworkhours, vacationdays, vacationreconciliationdate, breakfastofftimeend, breakfastofftimestart, lunchofftimeend, lunchofftimestart, earliestworktimestart, latestworktimeend) ");
        statement.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        dbStatement.setLong(1, contract.getId());
        dbStatement.setString(2, contract.getName());
        dbStatement.setLong(3, contract.getWorkhours());
        dbStatement.setLong(4, contract.getMaxworkhours());
        dbStatement.setLong(5, contract.getVacationdays());
        dbStatement.setString(6, contract.getVacationreconciliationdate());
        dbStatement.setString(7, contract.getBreakfastofftimeend().toString());
        dbStatement.setString(8, contract.getBreakfastofftimestart().toString());
        dbStatement.setString(9, contract.getLunchofftimeend().toString());
        dbStatement.setString(10, contract.getLunchofftimestart().toString());
        dbStatement.setString(11, contract.getEarliestworktimestart().toString());
        dbStatement.setString(12, contract.getLatestworktimeend().toString());

        return (dbStatement.executeUpdate() > 0);
    }

    public synchronized boolean update(Contract original, Contract modified) throws SQLException {
        if(original == null) throw new NullPointerException("original");
        if(modified == null) throw new NullPointerException("modified");

        if(!Objects.equals(original.getId(), modified.getId())) {
            log.warn("Update: " + original.toString() + " with " + modified.toString() + "not possible, as Id different");
            return false;
        }
        
        StringBuilder statement = new StringBuilder();
        statement.append("UPDATE contract ");
        statement.append("SET id = ?, name = ?, workhours = ?, maxworkhours = ?, vacationdays = ?, vacationreconciliationdate = ?, breakfastofftimeend = ?, breakfastofftimestart = ?, lunchofftimeend = ?, lunchofftimestart = ?, earliestworktimestart = ?, latestworktimeend = ? ");
        statement.append("WHERE id = ?;");
        
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        dbStatement.setLong(1, modified.getId());
        dbStatement.setString(2, modified.getName());
        dbStatement.setLong(3, modified.getWorkhours());
        dbStatement.setLong(4, modified.getMaxworkhours());
        dbStatement.setLong(5, modified.getVacationdays());
        dbStatement.setString(6, modified.getVacationreconciliationdate());
        dbStatement.setString(7, modified.getBreakfastofftimeend().toString());
        dbStatement.setString(8, modified.getBreakfastofftimestart().toString());
        dbStatement.setString(9, modified.getLunchofftimeend().toString());
        dbStatement.setString(10, modified.getLunchofftimestart().toString());
        dbStatement.setString(11, modified.getEarliestworktimestart().toString());
        dbStatement.setString(12, modified.getLatestworktimeend().toString());
        dbStatement.setLong(13, original.getId());

        boolean result = dbStatement.executeUpdate() > 0;
        if(!result) {
            log.warn("Update: not possible, as " + original.toString() + " does not exist");
        }
        return result;
    }
    
    public synchronized boolean delete(Contract contract) throws SQLException {
        if(contract == null) throw new NullPointerException("contract");

        StringBuilder statement = new StringBuilder();
        statement.append("DELETE FROM contract ");
        statement.append("WHERE id = ?");
        
        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        dbStatement.setLong(1, contract.getId());

        boolean result = dbStatement.executeUpdate() > 0;
        if(!result) {
            log.warn("Delete: not possible, as " + contract.toString() + " does not exist");
        }
        return result;
    }
    
    public synchronized Long getNextId() throws SQLException{
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT seq ");
        statement.append("FROM SQLITE_SEQUENCE ");
        statement.append("WHERE name = 'Contract';");

        PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
        ResultSet rs = dbStatement.executeQuery();
        if (rs.next()) {
            return rs.getLong(1) + 1;
        }
        return 0L;
    }    

}
