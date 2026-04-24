package sqlite;

import model.Address;
import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap; // For thread-safe caching
import org.apache.logging.log4j.*;

/**
 * Data Access Object (DAO) for managing Address objects in a SQLite database,
 * now with added caching for improved performance on frequent read operations.
 *
 * @author adrest18
 */
public class AddressDAO {

    private final Logger log;
    private final Connection connection;

    // Caching mechanism
    // Cache for individual Address objects by their ID
    private final Map<Long, Address> addressCacheById;
    // Cache for all addresses (for selectAll())
    private List<Address> allAddressesCache;

    public AddressDAO(Connection connection) {
        if (connection == null) {
            throw new NullPointerException("connection");
        }

        this.log = LogManager.getLogger(AddressDAO.class.getName());
        this.connection = connection;

        // Initialize caches
        this.addressCacheById = new ConcurrentHashMap<>();
        this.allAddressesCache = null; // Initialize as null, will be populated on first selectAll()
        log.debug("AddressDAO initialized with caching enabled.");
    }

    /**
     * Retrieves all addresses, utilizing a cache.
     * If the data is in the cache, it's returned immediately. Otherwise,
     * it fetches from the database and populates the cache.
     *
     * @return A list of all Address objects.
     * @throws SQLException If a database access error occurs.
     */
    public synchronized List<Address> selectAll() throws SQLException {
        // Try to retrieve from cache first
        if (allAddressesCache != null) {
            log.debug(String.format("AddressDAO.selectAll() returns %d addresses. (Cache hit)", allAddressesCache.size()));
            return new ArrayList<>(allAddressesCache); // Return a copy to prevent external modification
        }

        List<Address> resultList = new ArrayList<>();

        StringBuilder statement = new StringBuilder();
        statement.append("SELECT id, streetname, housenumber, unitname, unitnumber, unitlocation, city, state, zipcode, country ");
        statement.append("FROM address;");

        Instant start = Instant.now();
        // Use try-with-resources for PreparedStatement and ResultSet
        try (PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
             ResultSet resultSet = dbStatement.executeQuery()) {

            while (resultSet.next()) {
                Address address = createAddressFromResultSetEntry(resultSet);
                resultList.add(address);
                // Also cache individual records when fetching a list
                addressCacheById.put(address.getId(), address);
            }
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.debug(String.format("AddressDAO.selectAll() returns %d addresses. (DB hit)", resultList.size()));
            log.debug("Elapsed time: " + timeElapsed + "ms");

            // Populate the cache for all addresses
            allAddressesCache = new ArrayList<>(resultList); // Store a copy
        }
        return resultList;
    }

    /**
     * Retrieves an Address by its ID, utilizing a cache.
     * If the record is in the cache, it's returned immediately. Otherwise,
     * it fetches from the database and populates the cache.
     *
     * @param id The ID of the address to retrieve.
     * @return The Address object if found, or null if not.
     * @throws SQLException If a database access error occurs.
     */
    public synchronized Address selectAddressFromId(long id) throws SQLException {
        // Try to retrieve from cache first
        if (addressCacheById.containsKey(id)) {
            log.debug(String.format("AddressDAO.selectAddressFromId(%d) returns an address. (Cache hit)", id));
            return addressCacheById.get(id);
        }

        StringBuilder statement = new StringBuilder();
        statement.append("SELECT id, streetname, housenumber, unitname, unitnumber, unitlocation, city, state, zipcode, country ");
        statement.append("FROM address ");
        statement.append("WHERE id = ?;");

        Instant start = Instant.now();
        // Use try-with-resources for PreparedStatement and ResultSet
        try (PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            dbStatement.setLong(1, id); // Use setLong for long type
            try (ResultSet resultSet = dbStatement.executeQuery()) {
                if (resultSet.next()) { // Changed from while to if, as we expect only one result for an ID
                    Address address = createAddressFromResultSetEntry(resultSet);
                    Instant finish = Instant.now();
                    long timeElapsed = Duration.between(start, finish).toMillis();
                    log.debug(String.format("AddressDAO.selectAddressFromId(%d) returns an address. (DB hit)", id));
                    log.debug("Elapsed time: " + timeElapsed + "ms");

                    // Add to cache
                    addressCacheById.put(id, address);
                    return address;
                }
            }
        }
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        log.debug(String.format("AddressDAO.selectAddressFromId(%d) returns null. (DB hit)", id));
        log.debug("Elapsed time: " + timeElapsed + "ms");
        log.warn(String.format("Select: Address with Id %d not found.", id));
        return null;
    }

    private synchronized Address createAddressFromResultSetEntry(ResultSet resultSet) throws SQLException {
        Long rsId = resultSet.getLong("id");
        String rsStreetname = resultSet.getString("streetname");
        Long rsHousenumber = resultSet.getLong("housenumber");
        String rsUnitname = resultSet.getString("unitname");
        Long rsUnitnumber = resultSet.getLong("unitnumber");
        String rsUnitlocation = resultSet.getString("unitlocation");
        String rsCity = resultSet.getString("city");
        String rsState = resultSet.getString("state");
        Long rsZipcode = resultSet.getLong("zipcode");
        String rsCountry = resultSet.getString("country");

        return new Address(rsId, rsStreetname, rsHousenumber, rsUnitname, rsUnitnumber, rsUnitlocation, rsCity, rsState, rsZipcode, rsCountry);
    }

    public synchronized boolean create(Address address) throws SQLException {
        if (address == null) {
            throw new NullPointerException("address");
        }

        boolean result;
        StringBuilder statement = new StringBuilder();
        statement.append("INSERT INTO address (id, streetname, housenumber, unitname, unitnumber, unitlocation, city, state, zipcode, country) ");
        statement.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        Instant start = Instant.now();
        try (PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            dbStatement.setLong(1, address.getId());
            dbStatement.setString(2, address.getStreetname());
            dbStatement.setLong(3, address.getHousenumber());
            dbStatement.setString(4, address.getUnitname());
            dbStatement.setLong(5, address.getUnitnumber());
            dbStatement.setString(6, address.getUnitlocation());
            dbStatement.setString(7, address.getCity());
            dbStatement.setString(8, address.getState());
            dbStatement.setLong(9, address.getZipcode());
            dbStatement.setString(10, address.getCountry());

            result = dbStatement.executeUpdate() > 0;
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.debug(String.format("AddressDAO.create returns %b.", result));
            log.debug("Elapsed time: " + timeElapsed + "ms");

            if (result) {
                // Invalidate cache after creation
                invalidateCache(address);
            }
        }
        return result;
    }

    public synchronized boolean update(Address original, Address modified) throws SQLException {
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
        statement.append("UPDATE address ");
        statement.append("SET streetname = ?, housenumber = ?, unitname = ?, unitnumber = ?, unitlocation = ?, city = ?, state = ?, zipcode = ?, country = ? ");
        statement.append("WHERE id = ?;");

        Instant start = Instant.now();
        try (PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            dbStatement.setString(1, modified.getStreetname());
            dbStatement.setLong(2, modified.getHousenumber());
            dbStatement.setString(3, modified.getUnitname());
            dbStatement.setLong(4, modified.getUnitnumber());
            dbStatement.setString(5, modified.getUnitlocation());
            dbStatement.setString(6, modified.getCity());
            dbStatement.setString(7, modified.getState());
            dbStatement.setLong(8, modified.getZipcode());
            dbStatement.setString(9, modified.getCountry());
            dbStatement.setLong(10, original.getId());

            result = dbStatement.executeUpdate() > 0;
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.debug(String.format("AddressDAO.update returns %b.", result));
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

    public synchronized boolean delete(Address address) throws SQLException {
        if (address == null) {
            throw new NullPointerException("address");
        }

        boolean result;
        StringBuilder statement = new StringBuilder();
        statement.append("DELETE FROM address ");
        statement.append("WHERE id = ?");

        Instant start = Instant.now();
        try (PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            dbStatement.setLong(1, address.getId());

            result = dbStatement.executeUpdate() > 0;
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.debug(String.format("AddressDAO.delete returns %b.", result));
            log.debug("Elapsed time: " + timeElapsed + "ms");
            if (!result) {
                log.warn(String.format("Delete: not possible, as %s does not exist.", address));
            } else {
                // Invalidate cache after deletion
                invalidateCache(address);
            }
        }
        return result;
    }

    public synchronized Long getNextId() throws SQLException {
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT seq ");
        statement.append("FROM SQLITE_SEQUENCE ");
        statement.append("WHERE name = 'Address';");

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
     * Helper method to invalidate cache entries related to an Address.
     * This is crucial for maintaining cache consistency after CUD operations.
     *
     * @param address The Address that was created, updated, or deleted.
     */
    private void invalidateCache(Address address) {
        // Invalidate individual record cache
        addressCacheById.remove(address.getId());
        log.debug(String.format("Cache invalidated for Address ID: %d", address.getId()));

        // Invalidate the allAddressesCache, as its contents are now stale
        allAddressesCache = null;
        log.debug("Cache for all addresses invalidated.");
    }
}