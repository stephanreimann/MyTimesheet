package sqlite;

import model.Worklocation;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap; // For thread-safe caching
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Data Access Object (DAO) for managing Worklocation objects in a SQLite database,
 * now with added caching for improved performance on frequent read operations.
 *
 * @author adrest18
 */
public class WorklocationDAO {

    private final Logger log;
    private final Connection connection;

    // Caching mechanism
    // Cache for individual Worklocation objects by their ID
    private final Map<Long, Worklocation> worklocationCacheById;
    // Cache for all worklocations (for selectAll())
    private List<Worklocation> allWorklocationsCache;

    public WorklocationDAO(Connection connection) {
        if (connection == null) {
            throw new NullPointerException("connection");
        }

        this.log = LogManager.getLogger(WorklocationDAO.class.getName());
        this.connection = connection;

        // Initialize caches
        this.worklocationCacheById = new ConcurrentHashMap<>();
        this.allWorklocationsCache = null; // Initialize as null, will be populated on first selectAll()
        log.debug("WorklocationDAO initialized with caching enabled.");
    }

    /**
     * Retrieves all work locations, utilizing a cache.
     * If the data is in the cache, it's returned immediately. Otherwise,
     * it fetches from the database and populates the cache.
     *
     * @return A list of all Worklocation objects.
     * @throws SQLException If a database access error occurs.
     */
    public synchronized List<Worklocation> selectAll() throws SQLException {
        // Try to retrieve from cache first
        if (allWorklocationsCache != null) {
            log.debug(String.format("WorklocationDAO.selectAll() returns %d worklocations. (Cache hit)", allWorklocationsCache.size()));
            return new ArrayList<>(allWorklocationsCache); // Return a copy to prevent external modification
        }

        List<Worklocation> resultList = new ArrayList<>();

        StringBuilder statement = new StringBuilder();
        statement.append("SELECT id, name, description ");
        statement.append("FROM worklocation;");

        Instant start = Instant.now();
        // Use try-with-resources for PreparedStatement and ResultSet
        try (PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
             ResultSet resultSet = dbStatement.executeQuery()) {

            while (resultSet.next()) {
                Worklocation worklocation = createWorklocationFromResultSetEntry(resultSet);
                resultList.add(worklocation);
                // Also cache individual records when fetching a list
                worklocationCacheById.put(worklocation.getId(), worklocation);
            }
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.debug(String.format("WorklocationDAO.selectAll() returns %d worklocations. (DB hit)", resultList.size()));
            log.debug("Elapsed time: " + timeElapsed + "ms");

            // Populate the cache for all worklocations
            allWorklocationsCache = new ArrayList<>(resultList); // Store a copy
        }
        return resultList;
    }

    /**
     * Retrieves a Worklocation by its ID, utilizing a cache.
     * If the record is in the cache, it's returned immediately. Otherwise,
     * it fetches from the database and populates the cache.
     *
     * @param id The ID of the work location to retrieve.
     * @return The Worklocation object if found, or null if not.
     * @throws SQLException If a database access error occurs.
     */
    public synchronized Worklocation selectWorklocationFromId(long id) throws SQLException {
        // Try to retrieve from cache first
        if (worklocationCacheById.containsKey(id)) {
            log.debug(String.format("WorklocationDAO.selectWorklocationFromId(%d) returns a worklocation. (Cache hit)", id));
            return worklocationCacheById.get(id);
        }

        StringBuilder statement = new StringBuilder();
        statement.append("SELECT id, name, description ");
        statement.append("FROM worklocation ");
        statement.append("WHERE id = ?;");

        Instant start = Instant.now();
        // Use try-with-resources for PreparedStatement and ResultSet
        try (PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            dbStatement.setLong(1, id); // Use setLong for long type
            try (ResultSet resultSet = dbStatement.executeQuery()) {
                if (resultSet.next()) { // Changed from while to if, as we expect only one result for an ID
                    Worklocation worklocation = createWorklocationFromResultSetEntry(resultSet);
                    Instant finish = Instant.now();
                    long timeElapsed = Duration.between(start, finish).toMillis();
                    log.debug(String.format("WorklocationDAO.selectWorklocationFromId(%d) returns a worklocation. (DB hit)", id));
                    log.debug("Elapsed time: " + timeElapsed + "ms");

                    // Add to cache
                    worklocationCacheById.put(id, worklocation);
                    return worklocation;
                }
            }
        }
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        log.debug(String.format("WorklocationDAO.selectWorklocationFromId(%d) returns null. (DB hit)", id));
        log.debug("Elapsed time: " + timeElapsed + "ms");
        log.warn(String.format("Select: Worklocation with Id %d not found.", id));
        return null;
    }

    private Worklocation createWorklocationFromResultSetEntry(ResultSet resultSet) throws SQLException {
        Long rsId = resultSet.getLong("id");
        String rsName = resultSet.getString("name");
        String rsDescription = resultSet.getString("description");

        return new Worklocation(rsId, rsName, rsDescription);
    }

    public synchronized boolean create(Worklocation worklocation) throws SQLException {
        if (worklocation == null) {
            throw new NullPointerException("worklocation");
        }

        boolean result;
        StringBuilder statement = new StringBuilder();
        statement.append("INSERT INTO worklocation (id, name, description) ");
        statement.append("VALUES (?, ?, ?)");

        Instant start = Instant.now();
        try (PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            dbStatement.setLong(1, worklocation.getId());
            dbStatement.setString(2, worklocation.getName());
            dbStatement.setString(3, worklocation.getDescription());

            result = dbStatement.executeUpdate() > 0;
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.debug(String.format("WorklocationDAO.create returns %b.", result));
            log.debug("Elapsed time: " + timeElapsed + "ms");

            if (result) {
                // Invalidate cache after creation
                invalidateCache(worklocation);
            }
        }
        return result;
    }

    public synchronized boolean update(Worklocation original, Worklocation modified) throws SQLException {
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
        statement.append("UPDATE worklocation ");
        statement.append("SET name = ?, description = ? ");
        statement.append("WHERE id = ?;");

        Instant start = Instant.now();
        try (PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            dbStatement.setString(1, modified.getName());
            dbStatement.setString(2, modified.getDescription());
            dbStatement.setLong(3, original.getId());

            result = dbStatement.executeUpdate() > 0;
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.debug(String.format("WorklocationDAO.update returns %b.", result));
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

    public synchronized boolean delete(Worklocation worklocation) throws SQLException {
        if (worklocation == null) {
            throw new NullPointerException("worklocation");
        }

        boolean result;
        StringBuilder statement = new StringBuilder();
        statement.append("DELETE FROM worklocation ");
        statement.append("WHERE id = ?");

        Instant start = Instant.now();
        try (PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            dbStatement.setLong(1, worklocation.getId());

            result = dbStatement.executeUpdate() > 0;
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.debug(String.format("WorklocationDAO.delete returns %b.", result));
            log.debug("Elapsed time: " + timeElapsed + "ms");
            if (!result) {
                log.warn(String.format("Delete: not possible, as %s does not exist.", worklocation));
            } else {
                // Invalidate cache after deletion
                invalidateCache(worklocation);
            }
        }
        return result;
    }

    public synchronized Long getNextId() throws SQLException {
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT seq ");
        statement.append("FROM SQLITE_SEQUENCE ");
        statement.append("WHERE name = 'Worklocation';");

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
     * Helper method to invalidate cache entries related to a Worklocation.
     * This is crucial for maintaining cache consistency after CUD operations.
     *
     * @param worklocation The Worklocation that was created, updated, or deleted.
     */
    private void invalidateCache(Worklocation worklocation) {
        // Invalidate individual record cache
        worklocationCacheById.remove(worklocation.getId());
        log.debug(String.format("Cache invalidated for Worklocation ID: %d", worklocation.getId()));

        // Invalidate the allWorklocationsCache, as its contents are now stale
        allWorklocationsCache = null;
        log.debug("Cache for all worklocations invalidated.");
    }
}