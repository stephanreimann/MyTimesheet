package sqlite;

import model.Contract;
import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap; // For thread-safe caching
import org.apache.logging.log4j.*;

/**
 * Data Access Object (DAO) for managing Contract objects in a SQLite database,
 * now with added caching for improved performance on frequent read operations.
 *
 * @author adrest18
 */
public class ContractDAO {

    private final Logger log;
    private final Connection connection;

    // Caching mechanism
    // Cache for individual Contract objects by their ID
    private final Map<Long, Contract> contractCacheById;
    // Cache for all contracts (for selectAll())
    private List<Contract> allContractsCache;

    public ContractDAO(Connection connection) {
        if (connection == null) {
            throw new NullPointerException("connection");
        }

        this.log = LogManager.getLogger(ContractDAO.class.getName()); // Corrected logger name
        this.connection = connection;

        // Initialize caches
        this.contractCacheById = new ConcurrentHashMap<>();
        this.allContractsCache = null; // Initialize as null, will be populated on first selectAll()
        log.debug("ContractDAO initialized with caching enabled.");
    }

    /**
     * Retrieves all contracts, utilizing a cache.
     * If the data is in the cache, it's returned immediately. Otherwise,
     * it fetches from the database and populates the cache.
     *
     * @return A list of all Contract objects.
     * @throws SQLException If a database access error occurs.
     */
    public synchronized List<Contract> selectAll() throws SQLException {
        // Try to retrieve from cache first
        if (allContractsCache != null) {
            log.debug(String.format("ContractDAO.selectAll() returns %d contracts. (Cache hit)", allContractsCache.size()));
            return new ArrayList<>(allContractsCache); // Return a copy to prevent external modification
        }

        List<Contract> resultList = new ArrayList<>();

        StringBuilder statement = new StringBuilder();
        statement.append("SELECT id, name, workhours, maxworkhours, vacationdays, vacationreconciliationdate, breakfastofftimeend, breakfastofftimestart, lunchofftimeend, lunchofftimestart, earliestworktimestart, latestworktimeend ");
        statement.append("FROM contract;");

        Instant start = Instant.now();
        // Use try-with-resources for PreparedStatement and ResultSet
        try (PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
             ResultSet resultSet = dbStatement.executeQuery()) {

            while (resultSet.next()) {
                Contract contract = createContractFromResultSetEntry(resultSet);
                resultList.add(contract);
                // Also cache individual records when fetching a list
                contractCacheById.put(contract.getId(), contract);
            }
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.debug(String.format("ContractDAO.selectAll() returns %d contracts. (DB hit)", resultList.size()));
            log.debug("Elapsed time: " + timeElapsed + "ms");

            // Populate the cache for all contracts
            allContractsCache = new ArrayList<>(resultList); // Store a copy
        }
        return resultList;
    }

    /**
     * Retrieves a Contract by its ID, utilizing a cache.
     * If the record is in the cache, it's returned immediately. Otherwise,
     * it fetches from the database and populates the cache.
     *
     * @param id The ID of the contract to retrieve.
     * @return The Contract object if found, or null if not.
     * @throws SQLException If a database access error occurs.
     */
    public synchronized Contract selectContractFromId(long id) throws SQLException {
        // Try to retrieve from cache first
        if (contractCacheById.containsKey(id)) {
            log.debug(String.format("ContractDAO.selectContractFromId(%d) returns a contract. (Cache hit)", id));
            return contractCacheById.get(id);
        }

        StringBuilder statement = new StringBuilder();
        statement.append("SELECT id, name, workhours, maxworkhours, vacationdays, vacationreconciliationdate, breakfastofftimeend, breakfastofftimestart, lunchofftimeend, lunchofftimestart, earliestworktimestart, latestworktimeend ");
        statement.append("FROM contract ");
        statement.append("WHERE id = ?;");

        Instant start = Instant.now();
        // Use try-with-resources for PreparedStatement and ResultSet
        try (PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            dbStatement.setLong(1, id); // Use setLong for long type
            try (ResultSet resultSet = dbStatement.executeQuery()) {
                if (resultSet.next()) { // Changed from while to if, as we expect only one result for an ID
                    Contract contract = createContractFromResultSetEntry(resultSet);
                    Instant finish = Instant.now();
                    long timeElapsed = Duration.between(start, finish).toMillis();
                    log.debug(String.format("ContractDAO.selectContractFromId(%d) returns a contract. (DB hit)", id));
                    log.debug("Elapsed time: " + timeElapsed + "ms");

                    // Add to cache
                    contractCacheById.put(id, contract);
                    return contract;
                }
            }
        }
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        log.debug(String.format("ContractDAO.selectContractFromId(%d) returns null. (DB hit)", id));
        log.debug("Elapsed time: " + timeElapsed + "ms");
        log.warn(String.format("Select: Contract with Id %d not found.", id));
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
        if (contract == null) {
            throw new NullPointerException("contract");
        }

        boolean result;
        StringBuilder statement = new StringBuilder();
        statement.append("INSERT INTO contract (id, name, workhours, maxworkhours, vacationdays, vacationreconciliationdate, breakfastofftimeend, breakfastofftimestart, lunchofftimeend, lunchofftimestart, earliestworktimestart, latestworktimeend) ");
        statement.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        Instant start = Instant.now();
        try (PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
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

            result = dbStatement.executeUpdate() > 0;
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.debug(String.format("ContractDAO.create returns %b.", result));
            log.debug("Elapsed time: " + timeElapsed + "ms");

            if (result) {
                // Invalidate cache after creation
                invalidateCache(contract);
            }
        }
        return result;
    }

    public synchronized boolean update(Contract original, Contract modified) throws SQLException {
        if (original == null) {
            throw new NullPointerException("original");
        }
        if (modified == null) {
            throw new NullPointerException("modified");
        }

        if (!Objects.equals(original.getId(), modified.getId())) {
            log.warn("Update: " + original.toString() + " with " + modified.toString() + " not possible, as Id different");
            return false;
        }

        boolean result;
        StringBuilder statement = new StringBuilder();
        statement.append("UPDATE contract ");
        statement.append("SET name = ?, workhours = ?, maxworkhours = ?, vacationdays = ?, vacationreconciliationdate = ?, breakfastofftimeend = ?, breakfastofftimestart = ?, lunchofftimeend = ?, lunchofftimestart = ?, earliestworktimestart = ?, latestworktimeend = ? "); // Removed 'id = ?' from SET clause
        statement.append("WHERE id = ?;");

        Instant start = Instant.now();
        try (PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            // Parameters shifted due to removal of 'id = ?' from SET
            dbStatement.setString(1, modified.getName());
            dbStatement.setLong(2, modified.getWorkhours());
            dbStatement.setLong(3, modified.getMaxworkhours());
            dbStatement.setLong(4, modified.getVacationdays());
            dbStatement.setString(5, modified.getVacationreconciliationdate());
            dbStatement.setString(6, modified.getBreakfastofftimeend().toString());
            dbStatement.setString(7, modified.getBreakfastofftimestart().toString());
            dbStatement.setString(8, modified.getLunchofftimeend().toString());
            dbStatement.setString(9, modified.getLunchofftimestart().toString());
            dbStatement.setString(10, modified.getEarliestworktimestart().toString());
            dbStatement.setString(11, modified.getLatestworktimeend().toString());
            dbStatement.setLong(12, original.getId()); // WHERE clause parameter

            result = dbStatement.executeUpdate() > 0;
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.debug(String.format("ContractDAO.update returns %b.", result));
            log.debug("Elapsed time: " + timeElapsed + "ms");
            if (!result) {
                log.warn(String.format("Update: not possible, as %s does not exist.", original));
            } else {
                // Invalidate cache after update
                invalidateCache(modified);
            }
        }
        return result;
    }

    public synchronized boolean delete(Contract contract) throws SQLException {
        if (contract == null) {
            throw new NullPointerException("contract");
        }

        boolean result;
        StringBuilder statement = new StringBuilder();
        statement.append("DELETE FROM contract ");
        statement.append("WHERE id = ?");

        Instant start = Instant.now();
        try (PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            dbStatement.setLong(1, contract.getId());

            result = dbStatement.executeUpdate() > 0;
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.debug(String.format("ContractDAO.delete returns %b.", result));
            log.debug("Elapsed time: " + timeElapsed + "ms");
            if (!result) {
                log.warn(String.format("Delete: not possible, as %s does not exist.", contract));
            } else {
                // Invalidate cache after deletion
                invalidateCache(contract);
            }
        }
        return result;
    }

    public synchronized Long getNextId() throws SQLException {
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT seq ");
        statement.append("FROM SQLITE_SEQUENCE ");
        statement.append("WHERE name = 'Contract';");

        // Use try-with-resources for PreparedStatement and ResultSet
        try (PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
             ResultSet rs = dbStatement.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1) + 1;
            }
        }
        return 0L;
    }

    /**
     * Helper method to invalidate cache entries related to a Contract.
     * This is crucial for maintaining cache consistency after CUD operations.
     *
     * @param contract The Contract that was created, updated, or deleted.
     */
    private void invalidateCache(Contract contract) {
        // Invalidate individual record cache
        contractCacheById.remove(contract.getId());
        log.debug(String.format("Cache invalidated for Contract ID: %d", contract.getId()));

        // Invalidate the allContractsCache, as its contents are now stale
        allContractsCache = null;
        log.debug("Cache for all contracts invalidated.");
    }
}