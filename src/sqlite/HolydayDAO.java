package sqlite;

import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap; // For thread-safe caching
import model.Holyday;
import org.apache.logging.log4j.*;

/**
 * Data Access Object (DAO) for managing Holyday objects in a SQLite database,
 * now with added caching for improved performance on frequent read operations.
 *
 * @author adrest18
 */
public class HolydayDAO {

    private final Logger log;
    private final Connection connection;

    // Caching mechanism
    // Cache for individual Holyday objects by their ID
    private final Map<Long, Holyday> holydayCacheById;
    // Cache for all holydays (for selectAll())
    private List<Holyday> allHolydaysCache;

    public HolydayDAO(Connection connection) {
        if (connection == null) {
            throw new NullPointerException("connection");
        }

        this.log = LogManager.getLogger(HolydayDAO.class.getName()); // Corrected logger name
        this.connection = connection;

        // Initialize caches
        this.holydayCacheById = new ConcurrentHashMap<>();
        this.allHolydaysCache = null; // Initialize as null, will be populated on first selectAll()
        log.debug("HolydayDAO initialized with caching enabled.");
    }

    /**
     * Retrieves all holydays, utilizing a cache.
     * If the data is in the cache, it's returned immediately. Otherwise,
     * it fetches from the database and populates the cache.
     *
     * @return A list of all Holyday objects.
     * @throws SQLException If a database access error occurs.
     */
    public synchronized List<Holyday> selectAll() throws SQLException {
        // Try to retrieve from cache first
        if (allHolydaysCache != null) {
            log.debug(String.format("HolydayDAO.selectAll() returns %d holydays. (Cache hit)", allHolydaysCache.size()));
            return new ArrayList<>(allHolydaysCache); // Return a copy to prevent external modification
        }

        List<Holyday> resultList = new ArrayList<>();

        StringBuilder statement = new StringBuilder();
        statement.append("SELECT id, date, name, state ");
        statement.append("FROM holyday;");

        Instant start = Instant.now();
        // Use try-with-resources for PreparedStatement and ResultSet
        try (PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
             ResultSet rs = dbStatement.executeQuery()) {

            while (rs.next()) {
                Holyday holyday = createHolydayFromResultSetEntry(rs);
                resultList.add(holyday);
                // Also cache individual records when fetching a list
                holydayCacheById.put(holyday.getId(), holyday);
            }
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.debug(String.format("HolydayDAO.selectAll() returns %d holydays. (DB hit)", resultList.size()));
            log.debug("Elapsed time: " + timeElapsed + "ms");

            // Populate the cache for all holydays
            allHolydaysCache = new ArrayList<>(resultList); // Store a copy
        }
        return resultList;
    }

    /**
     * Retrieves a Holyday by its ID, utilizing a cache.
     * If the record is in the cache, it's returned immediately. Otherwise,
     * it fetches from the database and populates the cache.
     *
     * @param id The ID of the holyday to retrieve.
     * @return The Holyday object if found, or null if not.
     * @throws SQLException If a database access error occurs.
     */
    public synchronized Holyday selectHolydayFromId(long id) throws SQLException {
        // Try to retrieve from cache first
        if (holydayCacheById.containsKey(id)) {
            log.debug(String.format("HolydayDAO.selectHolydayFromId(%d) returns a holyday. (Cache hit)", id));
            return holydayCacheById.get(id);
        }

        StringBuilder statement = new StringBuilder();
        statement.append("SELECT id, date, name, state ");
        statement.append("FROM holyday ");
        statement.append("WHERE id = ?;");

        Instant start = Instant.now();
        // Use try-with-resources for PreparedStatement and ResultSet
        try (PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            dbStatement.setLong(1, id); // Use setLong for long type
            try (ResultSet resultSet = dbStatement.executeQuery()) {
                if (resultSet.next()) { // Changed from while to if, as we expect only one result for an ID
                    Holyday holyday = createHolydayFromResultSetEntry(resultSet);
                    Instant finish = Instant.now();
                    long timeElapsed = Duration.between(start, finish).toMillis();
                    log.debug(String.format("HolydayDAO.selectHolydayFromId(%d) returns a holyday. (DB hit)", id));
                    log.debug("Elapsed time: " + timeElapsed + "ms");

                    // Add to cache
                    holydayCacheById.put(id, holyday);
                    return holyday;
                }
            }
        }
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        log.debug(String.format("HolydayDAO.selectHolydayFromId(%d) returns null. (DB hit)", id));
        log.debug("Elapsed time: " + timeElapsed + "ms");
        log.warn(String.format("Select: Holyday with Id %d not found.", id));
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
        if (holyday == null) {
            throw new NullPointerException("holyday");
        }

        boolean result;
        StringBuilder statement = new StringBuilder();
        statement.append("INSERT INTO holyday (id, date, name, state) ");
        statement.append("VALUES (?, ?, ?, ?)");

        Instant start = Instant.now();
        try (PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            dbStatement.setLong(1, holyday.getId());
            dbStatement.setString(2, holyday.getDate().toString());
            dbStatement.setString(3, holyday.getName());
            dbStatement.setString(4, holyday.getState());

            result = dbStatement.executeUpdate() > 0;
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.debug(String.format("HolydayDAO.create returns %b.", result));
            log.debug("Elapsed time: " + timeElapsed + "ms");

            if (result) {
                // Invalidate cache after creation
                invalidateCache(holyday);
            }
        }
        return result;
    }

    public synchronized boolean update(Holyday original, Holyday modified) throws SQLException {
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
        statement.append("UPDATE holyday ");
        statement.append("SET date = ?, name = ?, state = ? "); // Removed 'id = ?' from SET clause
        statement.append("WHERE id = ?;");

        Instant start = Instant.now();
        try (PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            // Parameters shifted due to removal of 'id = ?' from SET
            dbStatement.setString(1, modified.getDate().toString());
            dbStatement.setString(2, modified.getName());
            dbStatement.setString(3, modified.getState());
            dbStatement.setLong(4, original.getId()); // WHERE clause parameter

            result = dbStatement.executeUpdate() > 0;
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.debug(String.format("HolydayDAO.update returns %b.", result));
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

    public synchronized boolean delete(Holyday holyday) throws SQLException {
        if (holyday == null) {
            throw new NullPointerException("holyday");
        }

        boolean result;
        StringBuilder statement = new StringBuilder();
        statement.append("DELETE FROM holyday ");
        statement.append("WHERE id = ?");

        Instant start = Instant.now();
        try (PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            dbStatement.setLong(1, holyday.getId());

            result = dbStatement.executeUpdate() > 0;
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.debug(String.format("HolydayDAO.delete returns %b.", result));
            log.debug("Elapsed time: " + timeElapsed + "ms");
            if (!result) {
                log.warn(String.format("Delete: not possible, as %s does not exist.", holyday));
            } else {
                // Invalidate cache after deletion
                invalidateCache(holyday);
            }
        }
        return result;
    }

    public synchronized Long getNextId() throws SQLException {
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT seq ");
        statement.append("FROM SQLITE_SEQUENCE ");
        statement.append("WHERE name = 'Holyday';");

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
     * Helper method to invalidate cache entries related to a Holyday.
     * This is crucial for maintaining cache consistency after CUD operations.
     *
     * @param holyday The Holyday that was created, updated, or deleted.
     */
    private void invalidateCache(Holyday holyday) {
        // Invalidate individual record cache
        holydayCacheById.remove(holyday.getId());
        log.debug(String.format("Cache invalidated for Holyday ID: %d", holyday.getId()));

        // Invalidate the allHolydaysCache, as its contents are now stale
        allHolydaysCache = null;
        log.debug("Cache for all holydays invalidated.");
    }
}