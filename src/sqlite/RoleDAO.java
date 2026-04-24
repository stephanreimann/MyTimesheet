package sqlite;

import model.Role;
import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap; // For thread-safe caching
import org.apache.logging.log4j.*;

/**
 * Data Access Object (DAO) for managing Role objects in a SQLite database,
 * now with added caching for improved performance on frequent read operations.
 *
 * @author adrest18
 */
public class RoleDAO {

    private final Logger log;
    private final Connection connection;

    // Caching mechanism
    // Cache for individual Role objects by their ID
    private final Map<Long, Role> roleCacheById;
    // Cache for all roles (for selectAll())
    private List<Role> allRolesCache;

    public RoleDAO(Connection connection) {
        if (connection == null) {
            throw new NullPointerException("connection");
        }

        this.log = LogManager.getLogger(RoleDAO.class.getName());
        this.connection = connection;

        // Initialize caches
        this.roleCacheById = new ConcurrentHashMap<>();
        this.allRolesCache = null; // Initialize as null, will be populated on first selectAll()
        log.debug("RoleDAO initialized with caching enabled.");
    }

    /**
     * Retrieves all roles, utilizing a cache.
     * If the data is in the cache, it's returned immediately. Otherwise,
     * it fetches from the database and populates the cache.
     *
     * @return A list of all Role objects.
     * @throws SQLException If a database access error occurs.
     */
    public synchronized List<Role> selectAll() throws SQLException {
        // Try to retrieve from cache first
        if (allRolesCache != null) {
            log.debug(String.format("RoleDAO.selectAll() returns %d roles. (Cache hit)", allRolesCache.size()));
            return new ArrayList<>(allRolesCache); // Return a copy to prevent external modification
        }

        List<Role> resultList = new ArrayList<>();

        StringBuilder statement = new StringBuilder();
        statement.append("SELECT id, name, description ");
        statement.append("FROM role;");

        Instant start = Instant.now();
        // Use try-with-resources for PreparedStatement and ResultSet
        try (PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
             ResultSet rs = dbStatement.executeQuery()) {

            while (rs.next()) {
                Role role = createRoleFromResultSetEntry(rs);
                resultList.add(role);
                // Also cache individual records when fetching a list
                roleCacheById.put(role.getId(), role);
            }
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.debug(String.format("RoleDAO.selectAll() returns %d roles. (DB hit)", resultList.size()));
            log.debug("Elapsed time: " + timeElapsed + "ms");

            // Populate the cache for all roles
            allRolesCache = new ArrayList<>(resultList); // Store a copy
        }
        return resultList;
    }

    /**
     * Retrieves a Role by its ID, utilizing a cache.
     * If the record is in the cache, it's returned immediately. Otherwise,
     * it fetches from the database and populates the cache.
     *
     * @param id The ID of the role to retrieve.
     * @return The Role object if found, or null if not.
     * @throws SQLException If a database access error occurs.
     */
    public synchronized Role selectRoleFromId(long id) throws SQLException {
        // Try to retrieve from cache first
        if (roleCacheById.containsKey(id)) {
            log.debug(String.format("RoleDAO.selectRoleFromId(%d) returns a role. (Cache hit)", id));
            return roleCacheById.get(id);
        }

        StringBuilder statement = new StringBuilder();
        statement.append("SELECT id, name, description ");
        statement.append("FROM role ");
        statement.append("WHERE id = ?;");

        Instant start = Instant.now();
        // Use try-with-resources for PreparedStatement and ResultSet
        try (PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            dbStatement.setLong(1, id); // Use setLong for long type
            try (ResultSet resultSet = dbStatement.executeQuery()) {
                if (resultSet.next()) { // Changed from while to if, as we expect only one result for an ID
                    Role role = createRoleFromResultSetEntry(resultSet);
                    Instant finish = Instant.now();
                    long timeElapsed = Duration.between(start, finish).toMillis();
                    log.debug(String.format("RoleDAO.selectRoleFromId(%d) returns a role. (DB hit)", id));
                    log.debug("Elapsed time: " + timeElapsed + "ms");

                    // Add to cache
                    roleCacheById.put(id, role);
                    return role;
                }
            }
        }
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        log.debug(String.format("RoleDAO.selectRoleFromId(%d) returns null. (DB hit)", id));
        log.debug("Elapsed time: " + timeElapsed + "ms");
        log.warn(String.format("Select: Role with Id %d not found.", id));
        return null;
    }

    private Role createRoleFromResultSetEntry(ResultSet rs) throws SQLException {
        Long rsId = rs.getLong("id");
        String rsName = rs.getString("name");
        String rsDescription = rs.getString("description");

        return new Role(rsId, rsName, rsDescription);
    }

    public synchronized boolean create(Role role) throws SQLException {
        if (role == null) {
            throw new NullPointerException("role");
        }

        boolean result;
        StringBuilder statement = new StringBuilder();
        statement.append("INSERT INTO role (id, name, description) ");
        statement.append("VALUES (?, ?, ?)");

        Instant start = Instant.now();
        try (PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            dbStatement.setLong(1, role.getId());
            dbStatement.setString(2, role.getName());
            dbStatement.setString(3, role.getDescription());

            result = dbStatement.executeUpdate() > 0;
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.debug(String.format("RoleDAO.create returns %b.", result));
            log.debug("Elapsed time: " + timeElapsed + "ms");

            if (result) {
                // Invalidate cache after creation
                invalidateCache(role);
            }
        }
        return result;
    }

    public synchronized boolean update(Role original, Role modified) throws SQLException {
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
        statement.append("UPDATE role ");
        statement.append("SET id = ?, name = ?, description = ? ");
        statement.append("WHERE id = ?;");

        Instant start = Instant.now();
        try (PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            dbStatement.setLong(1, modified.getId());
            dbStatement.setString(2, modified.getName());
            dbStatement.setString(3, modified.getDescription());
            dbStatement.setLong(4, original.getId());

            result = dbStatement.executeUpdate() > 0;
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.debug(String.format("RoleDAO.update returns %b.", result));
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

    public synchronized boolean delete(Role role) throws SQLException {
        if (role == null) {
            throw new NullPointerException("role");
        }

        boolean result;
        StringBuilder statement = new StringBuilder();
        statement.append("DELETE FROM role ");
        statement.append("WHERE id = ?");

        Instant start = Instant.now();
        try (PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            dbStatement.setLong(1, role.getId());

            result = dbStatement.executeUpdate() > 0;
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.debug(String.format("RoleDAO.delete returns %b.", result));
            log.debug("Elapsed time: " + timeElapsed + "ms");
            if (!result) {
                log.warn(String.format("Delete: not possible, as %s does not exist.", role));
            } else {
                // Invalidate cache after deletion
                invalidateCache(role);
            }
        }
        return result;
    }

    public synchronized Long getNextId() throws SQLException {
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT seq ");
        statement.append("FROM SQLITE_SEQUENCE ");
        statement.append("WHERE name = 'Role';");

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
     * Helper method to invalidate cache entries related to a Role.
     * This is crucial for maintaining cache consistency after CUD operations.
     *
     * @param role The Role that was created, updated, or deleted.
     */
    private void invalidateCache(Role role) {
        // Invalidate individual record cache
        roleCacheById.remove(role.getId());
        log.debug(String.format("Cache invalidated for Role ID: %d", role.getId()));

        // Invalidate the allRolesCache, as its contents are now stale
        allRolesCache = null;
        log.debug("Cache for all roles invalidated.");
    }
}