package sqlite;

import model.*;
import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap; // For thread-safe caching
import org.apache.logging.log4j.*;

/**
 * Data Access Object (DAO) for managing User objects in a SQLite database,
 * now with added caching for improved performance on frequent read operations.
 *
 * @author adrest18
 */
public class UserDAO {

    private final Logger log;
    private final Connection connection;
    private final RoleDAO roleDao;
    private final AddressDAO addressDao;

    // Caching mechanism
    // Cache for individual User objects by their ID
    private final Map<Long, User> userCacheById;
    // Cache for all users (for selectAll())
    private List<User> allUsersCache;

    public UserDAO(Connection connection) {
        if (connection == null) {
            throw new NullPointerException("connection");
        }

        this.log = LogManager.getLogger(UserDAO.class.getName());
        this.connection = connection;
        this.roleDao = new RoleDAO(connection);
        this.addressDao = new AddressDAO(connection);

        // Initialize caches
        this.userCacheById = new ConcurrentHashMap<>();
        this.allUsersCache = null; // Initialize as null, will be populated on first selectAll()
        log.debug("UserDAO initialized with caching enabled.");
    }

    /**
     * Retrieves all users, utilizing a cache.
     * If the data is in the cache, it's returned immediately. Otherwise,
     * it fetches from the database and populates the cache.
     *
     * @return A list of all User objects.
     * @throws SQLException If a database access error occurs.
     */
    public synchronized List<User> selectAll() throws SQLException {
        // Try to retrieve from cache first
        if (allUsersCache != null) {
            log.debug(String.format("UserDAO.selectAll() returns %d users. (Cache hit)", allUsersCache.size()));
            return new ArrayList<>(allUsersCache); // Return a copy to prevent external modification
        }

        List<User> resultList = new ArrayList<>();

        StringBuilder statement = new StringBuilder();
        statement.append("SELECT id, roleid, addressid, contractid, firstname, lastname, login, password, vacationleft ");
        statement.append("FROM user;");

        Instant start = Instant.now();
        // Use try-with-resources for PreparedStatement and ResultSet
        try (PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
             ResultSet rs = dbStatement.executeQuery()) {

            while (rs.next()) {
                User user = createUserFromResultSetEntry(rs);
                resultList.add(user);
                // Also cache individual records when fetching a list
                userCacheById.put(user.getId(), user);
            }
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.debug(String.format("UserDAO.selectAll() returns %d users. (DB hit)", resultList.size()));
            log.debug("Elapsed time: " + timeElapsed + "ms");

            // Populate the cache for all users
            allUsersCache = new ArrayList<>(resultList); // Store a copy
        }
        return resultList;
    }

    /**
     * Retrieves a User by their ID, utilizing a cache.
     * If the record is in the cache, it's returned immediately. Otherwise,
     * it fetches from the database and populates the cache.
     *
     * @param id The ID of the user to retrieve.
     * @return The User object if found, or null if not.
     * @throws SQLException If a database access error occurs.
     */
    public synchronized User selectUserFromId(long id) throws SQLException {
        // Try to retrieve from cache first
        if (userCacheById.containsKey(id)) {
            log.debug(String.format("UserDAO.selectUserFromId(%d) returns a user. (Cache hit)", id));
            return userCacheById.get(id);
        }

        StringBuilder statement = new StringBuilder();
        statement.append("SELECT id, roleid, addressid, contractid, firstname, lastname, login, password, vacationleft ");
        statement.append("FROM user ");
        statement.append("WHERE id = ?;");

        Instant start = Instant.now();
        // Use try-with-resources for PreparedStatement and ResultSet
        try (PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            dbStatement.setLong(1, id); // Use setLong for long type
            try (ResultSet resultSet = dbStatement.executeQuery()) {
                if (resultSet.next()) { // Changed from while to if, as we expect only one result for an ID
                    User user = createUserFromResultSetEntry(resultSet);
                    Instant finish = Instant.now();
                    long timeElapsed = Duration.between(start, finish).toMillis();
                    log.debug(String.format("UserDAO.selectUserFromId(%d) returns a user. (DB hit)", id));
                    log.debug("Elapsed time: " + timeElapsed + "ms");

                    // Add to cache
                    userCacheById.put(id, user);
                    return user;
                }
            }
        }
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        log.debug(String.format("UserDAO.selectUserFromId(%d) returns null. (DB hit)", id));
        log.debug("Elapsed time: " + timeElapsed + "ms");
        log.warn(String.format("Select: User with Id %d not found.", id));
        return null;
    }

    private User createUserFromResultSetEntry(ResultSet resultSet) throws SQLException {
        long rsId = resultSet.getLong("id");
        long rsRoleId = resultSet.getLong("roleid");
        long rsAddressId = resultSet.getLong("addressid");
        long rsContractId = resultSet.getLong("contractid");
        String rsFirstName = resultSet.getString("firstname");
        String rsLastName = resultSet.getString("lastname");
        String rsLogin = resultSet.getString("login");
        String rsPassword = resultSet.getString("password");
        long rsVacationLeft = resultSet.getLong("vacationleft");

        // Note: The DAOs for Role, Address, and Contract should ideally also have caching
        // to maximize the benefit here. For this example, we'll assume they fetch from DB.
        Role role = roleDao.selectRoleFromId(rsRoleId);
        Address address = addressDao.selectAddressFromId(rsAddressId);

        // Instantiating ContractDAO inside this method is not ideal for performance
        // if many users are being loaded. Consider injecting it like RoleDAO and AddressDAO.
        // For now, I'll keep it as is, but it's a point for optimization.
        ContractDAO contractDao = new ContractDAO(connection);
        Contract contract = contractDao.selectContractFromId(rsContractId);

        return new User(rsId, role, address, contract, rsFirstName, rsLastName, rsLogin, rsPassword, rsVacationLeft);
    }

    public synchronized boolean create(User user) throws SQLException {
        if (user == null) {
            throw new NullPointerException("user");
        }

        boolean result;
        StringBuilder statement = new StringBuilder();
        statement.append("INSERT INTO user (id, roleId, addressid, contractid, firstname, lastname, login, password, vacationleft) ");
        statement.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");

        Instant start = Instant.now();
        try (PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            dbStatement.setLong(1, user.getId());
            dbStatement.setLong(2, user.getRole().getId());
            dbStatement.setLong(3, user.getAddress().getId());
            dbStatement.setLong(4, user.getContract().getId());
            dbStatement.setString(5, user.getFirstname());
            dbStatement.setString(6, user.getLastname());
            dbStatement.setString(7, user.getLogin());
            dbStatement.setString(8, user.getPassword());
            dbStatement.setLong(9, user.getVacationleft());

            result = dbStatement.executeUpdate() > 0;
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.debug(String.format("UserDAO.create returns %b.", result));
            log.debug("Elapsed time: " + timeElapsed + "ms");

            if (result) {
                // Invalidate cache after creation
                invalidateCache(user);
            }
        }
        return result;
    }

    public synchronized boolean update(User original, User modified) throws SQLException {
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
        statement.append("UPDATE user ");
        statement.append("SET id = ?, roleId = ?, addressid = ?, contractid = ?, firstname = ?, lastname = ?, login = ?, password = ?, vacationleft = ? ");
        statement.append("WHERE id = ?;");

        Instant start = Instant.now();
        try (PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            dbStatement.setLong(1, modified.getId());
            dbStatement.setLong(2, modified.getRole().getId());
            dbStatement.setLong(3, modified.getAddress().getId());
            dbStatement.setLong(4, modified.getContract().getId());
            dbStatement.setString(5, modified.getFirstname());
            dbStatement.setString(6, modified.getLastname());
            dbStatement.setString(7, modified.getLogin());
            dbStatement.setString(8, modified.getPassword());
            dbStatement.setLong(9, modified.getVacationleft());
            dbStatement.setLong(10, original.getId());

            result = dbStatement.executeUpdate() > 0;
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.debug(String.format("UserDAO.update returns %b.", result));
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

    public synchronized boolean delete(User user) throws SQLException {
        if (user == null) {
            throw new NullPointerException("user");
        }

        boolean result;
        StringBuilder statement = new StringBuilder();
        statement.append("DELETE FROM user ");
        statement.append("WHERE id = ?");

        Instant start = Instant.now();
        try (PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            dbStatement.setLong(1, user.getId());

            result = dbStatement.executeUpdate() > 0;
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.debug(String.format("UserDAO.delete returns %b.", result));
            log.debug("Elapsed time: " + timeElapsed + "ms");
            if (!result) {
                log.warn(String.format("Delete: not possible, as %s does not exist.", user));
            } else {
                // Invalidate cache after deletion
                invalidateCache(user);
            }
        }
        return result;
    }

    public synchronized Long getNextId() throws SQLException {
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT seq ");
        statement.append("FROM SQLITE_SEQUENCE ");
        statement.append("WHERE name = 'User';");

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
     * Helper method to invalidate cache entries related to a User.
     * This is crucial for maintaining cache consistency after CUD operations.
     *
     * @param user The User that was created, updated, or deleted.
     */
    private void invalidateCache(User user) {
        // Invalidate individual record cache
        userCacheById.remove(user.getId());
        log.debug(String.format("Cache invalidated for User ID: %d", user.getId()));

        // Invalidate the allUsersCache, as its contents are now stale
        allUsersCache = null;
        log.debug("Cache for all users invalidated.");
    }
}