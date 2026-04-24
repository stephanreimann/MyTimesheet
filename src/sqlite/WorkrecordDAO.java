package sqlite;

import model.*;
import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap; // For thread-safe caching
import org.apache.logging.log4j.*;

/**
 * Data Access Object (DAO) for managing Workrecord objects in a SQLite database,
 * now with added caching for improved performance on frequent read operations.
 *
 * @author adrest18
 */
public class WorkrecordDAO {

    private final Logger log;
    private final Connection connection;
    private final UserDAO userDao;
    private final ProjectDAO projectDao;
    private final WorklocationDAO worklocationDAO;

    // Caching mechanism
    // Cache for individual Workrecord objects by their ID
    private final Map<Long, Workrecord> workrecordCacheById;
    // Cache for lists of Workrecord objects by User ID (for selectAll(User user))
    private final Map<Long, List<Workrecord>> workrecordCacheByUser;

    public WorkrecordDAO(Connection connection) {
        if (connection == null) {
            throw new NullPointerException("connection");
        }

        this.log = LogManager.getLogger(WorkrecordDAO.class.getName());
        this.connection = connection;
        this.userDao = new UserDAO(connection);
        this.projectDao = new ProjectDAO(connection);
        this.worklocationDAO = new WorklocationDAO(connection);

        // Initialize caches
        this.workrecordCacheById = new ConcurrentHashMap<>();
        this.workrecordCacheByUser = new ConcurrentHashMap<>();
        log.debug("WorkrecordDAO initialized with caching enabled.");
    }

    public synchronized List<Workrecord> selectAll() throws SQLException {
        List<Workrecord> resultList = new ArrayList<>();

        StringBuilder statement = new StringBuilder();
        statement.append("SELECT id, userid, projectid, date, starttime, endtime, worktime, overtime, overtimecorrection, vacationcorrection, worklocationid, description ");
        statement.append("FROM workrecord;");

        Instant start = Instant.now();
        try (PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            ResultSet rs = dbStatement.executeQuery();
            while (rs.next()) {
                Workrecord record = createWorkrecordFromResultSetEntry(rs);
                resultList.add(record);
                // Add to cache for individual records
                workrecordCacheById.put(record.getId(), record);
            }
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.debug(String.format("WorkrecordDAO.selectAll() returns %d workrecords. (DB hit)", resultList.size()));
            log.debug("Elapsed time: " + timeElapsed + "ms");
        }
        return resultList;
    }

    public synchronized List<Workrecord> selectAll(User user, LocalDate startDate, LocalDate endDate) throws SQLException {
        List<Workrecord> resultList = new ArrayList<>();

        StringBuilder statement = new StringBuilder();
        statement.append("SELECT id, userid, projectid, date, starttime, endtime, worktime, overtime, overtimecorrection, vacationcorrection, worklocationid, description ");
        statement.append("FROM workrecord ");
        statement.append("WHERE userid = ? ");
        statement.append("AND date BETWEEN ? AND ?;");

        try (PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            dbStatement.setLong(1, user.getId());
            dbStatement.setString(2, startDate.toString());
            dbStatement.setString(3, endDate.toString());
            Instant start = Instant.now();
            ResultSet rs = dbStatement.executeQuery();
            while (rs.next()) {
                Workrecord record = createWorkrecordFromResultSetEntry(rs);
                resultList.add(record);
                // Add to cache for individual records
                workrecordCacheById.put(record.getId(), record);
            }
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();

            log.debug(String.format("WorkrecordDAO.selectAll(%s, %s, %s) returns %d workrecords. (DB hit)", user.getLastname(), startDate, endDate, resultList.size()));
            log.debug("Elapsed time: " + timeElapsed + "ms");
        }
        return resultList;
    }

    public synchronized List<Workrecord> selectAll(User user, LocalDate date, LocalTime startTime, LocalTime endTime) throws SQLException {
        List<Workrecord> resultList = new ArrayList<>();

        StringBuilder statement = new StringBuilder();
        statement.append("SELECT id, userid, projectid, date, starttime, endtime, worktime, overtime, overtimecorrection, vacationcorrection, worklocationid, description ");
        statement.append("FROM workrecord ");
        statement.append("WHERE userid = ? ");
        statement.append("AND date = ? ");
        statement.append("AND starttime = ? ");
        statement.append("AND endtime = ?;");

        try (PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            dbStatement.setLong(1, user.getId());
            dbStatement.setString(2, date.toString());
            dbStatement.setString(3, startTime.toString());
            dbStatement.setString(4, endTime.toString());

            Instant start = Instant.now();
            ResultSet rs = dbStatement.executeQuery();
            while (rs.next()) {
                Workrecord record = createWorkrecordFromResultSetEntry(rs);
                resultList.add(record);
                // Add to cache for individual records
                workrecordCacheById.put(record.getId(), record);
            }
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.debug(String.format("WorkrecordDAO.selectAll(%s, %s, %s, %s) returns %d workrecords. (DB hit)", user.getLastname(), date, startTime, endTime, resultList.size()));
            log.debug("Elapsed time: " + timeElapsed + "ms");
        }
        return resultList;
    }

    public synchronized List<Workrecord> selectAll(User user, LocalDate date) throws SQLException {
        List<Workrecord> resultList = new ArrayList<>();

        StringBuilder statement = new StringBuilder();
        statement.append("SELECT id, userid, projectid, date, starttime, endtime, worktime, overtime, overtimecorrection, vacationcorrection, worklocationid, description ");
        statement.append("FROM workrecord ");
        statement.append("WHERE userid = ? ");
        statement.append("AND date = ?;");

        try (PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            dbStatement.setLong(1, user.getId());
            dbStatement.setString(2, date.toString());

            Instant start = Instant.now();
            ResultSet rs = dbStatement.executeQuery();
            while (rs.next()) {
                Workrecord record = createWorkrecordFromResultSetEntry(rs);
                resultList.add(record);
                // Add to cache for individual records
                workrecordCacheById.put(record.getId(), record);
            }
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.debug(String.format("WorkrecordDAO.selectAll(%s, %s) returns %d workrecords. (DB hit)", user.getLastname(), date, resultList.size()));
            log.debug("Elapsed time: " + timeElapsed + "ms");
        }
        return resultList;
    }

    /**
     * Retrieves all work records for a specific user, utilizing a cache.
     * If the data is in the cache, it's returned immediately. Otherwise,
     * it fetches from the database and populates the cache.
     *
     * @param user The user whose work records are to be retrieved.
     * @return A list of Workrecord objects for the given user.
     * @throws SQLException If a database access error occurs.
     */
    public synchronized List<Workrecord> selectAll(User user) throws SQLException {
        // Try to retrieve from cache first
        if (workrecordCacheByUser.containsKey(user.getId())) {
            log.debug(String.format("WorkrecordDAO.selectAll(%s) returns %d workrecords. (Cache hit)", user.getLastname(), workrecordCacheByUser.get(user.getId()).size()));
            return new ArrayList<>(workrecordCacheByUser.get(user.getId())); // Return a copy to prevent external modification of cached list
        }

        List<Workrecord> resultList = new ArrayList<>();

        StringBuilder statement = new StringBuilder();
        statement.append("SELECT id, userid, projectid, date, starttime, endtime, worktime, overtime, overtimecorrection, vacationcorrection, worklocationid, description ");
        statement.append("FROM workrecord ");
        statement.append("WHERE userid = ?;");

        try (PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            dbStatement.setLong(1, user.getId());

            Instant start = Instant.now();
            ResultSet rs = dbStatement.executeQuery();
            while (rs.next()) {
                Workrecord record = createWorkrecordFromResultSetEntry(rs);
                resultList.add(record);
                // Also cache individual records when fetching a list
                workrecordCacheById.put(record.getId(), record);
            }
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.debug(String.format("WorkrecordDAO.selectAll(%s) returns %d workrecords. (DB hit)", user.getLastname(), resultList.size()));
            log.debug("Elapsed time: " + timeElapsed + "ms");

            // Populate the cache for this user
            workrecordCacheByUser.put(user.getId(), new ArrayList<>(resultList)); // Store a copy
        }
        return resultList;
    }

    /**
     * Retrieves a Workrecord by its ID, utilizing a cache.
     * If the record is in the cache, it's returned immediately. Otherwise,
     * it fetches from the database and populates the cache.
     *
     * @param id The ID of the work record to retrieve.
     * @return An Optional containing the Workrecord if found, or empty if not.
     * @throws SQLException If a database access error occurs.
     */
    public Optional<Workrecord> selectWorkrecordFromId(long id) throws SQLException {
        // Try to retrieve from cache first
        if (workrecordCacheById.containsKey(id)) {
            log.debug(String.format("WorkrecordDAO.selectWorkrecordFromId(%d) returns 1 workrecord. (Cache hit)", id));
            return Optional.of(workrecordCacheById.get(id));
        }

        StringBuilder statement = new StringBuilder();
        statement.append("SELECT id, userid, projectid, date, starttime, endtime, worktime, overtime, overtimecorrection, vacationcorrection, worklocationid, description ");
        statement.append("FROM workrecord ");
        statement.append("WHERE id = ?;");

        try (PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            dbStatement.setLong(1, id);

            Instant start = Instant.now();
            ResultSet resultSet = dbStatement.executeQuery();
            if (resultSet.next()) { // Changed from while to if, as we expect only one result for an ID
                Workrecord workrecord = createWorkrecordFromResultSetEntry(resultSet);
                Instant finish = Instant.now();
                long timeElapsed = Duration.between(start, finish).toMillis();
                log.debug(String.format("WorkrecordDAO.selectWorkrecordFromId(%d) returns 1 workrecord. (DB hit)", id));
                log.debug("Elapsed time: " + timeElapsed + "ms");

                // Add to cache
                workrecordCacheById.put(id, workrecord);
                return Optional.of(workrecord);
            }
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.debug(String.format("WorkrecordDAO.selectWorkrecordFromId(%d) returns 0 workrecords. (DB hit)", id));
            log.debug("Elapsed time: " + timeElapsed + "ms");
            log.warn(String.format("Select: Workrecord with Id %d not found.", id));
        }
        return Optional.empty();
    }

    private Workrecord createWorkrecordFromResultSetEntry(ResultSet resultSet) throws SQLException {
        long rsId = resultSet.getLong("id");
        long rsUserId = resultSet.getLong("userid");
        long rsProjectId = resultSet.getLong("projectid");
        LocalDate rsDate = LocalDate.parse(resultSet.getString("date"));
        LocalTime rsStartTime = LocalTime.parse(resultSet.getString("starttime"));
        LocalTime rsEndTime = LocalTime.parse(resultSet.getString("endtime"));
        LocalTime rsWorkTime = LocalTime.parse(resultSet.getString("worktime"));
        String rsOverTime = resultSet.getString("overtime");
        String rsOverTimeCorrection = resultSet.getString("overtimecorrection");
        int rsVacationCorrection = resultSet.getInt("vacationcorrection");
        long rsWorklocationId = resultSet.getLong("worklocationid");
        String rsDescription = resultSet.getString("description");

        User user = userDao.selectUserFromId(rsUserId);
        Project project = projectDao.selectProjectFromId(rsProjectId);
        Worklocation worklocation = worklocationDAO.selectWorklocationFromId(rsWorklocationId);

        return new Workrecord(rsId, user, project, rsDate, rsStartTime, rsEndTime, rsWorkTime, rsOverTime, rsOverTimeCorrection, rsVacationCorrection, worklocation, rsDescription);
    }

    public synchronized boolean create(Workrecord workrecord) throws SQLException {
        if (workrecord == null) {
            throw new NullPointerException("workrecord");
        }

        boolean result;
        StringBuilder statement = new StringBuilder();
        statement.append("INSERT INTO workrecord (id, userid, projectid, date, starttime, endtime, worktime, overtime, overtimecorrection, vacationcorrection, worklocationid, description) ");
        statement.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        try (PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            dbStatement.setLong(1, workrecord.getId());
            dbStatement.setLong(2, workrecord.getUser().getId());
            dbStatement.setLong(3, workrecord.getProject().getId());
            dbStatement.setString(4, workrecord.getDate().toString());
            dbStatement.setString(5, workrecord.getStarttime().toString());
            dbStatement.setString(6, workrecord.getEndtime().toString());
            dbStatement.setString(7, workrecord.getWorktime().toString());
            dbStatement.setString(8, workrecord.getOvertime());
            dbStatement.setString(9, workrecord.getOvertimecorrection());
            dbStatement.setLong(10, workrecord.getVacationcorrection());
            dbStatement.setLong(11, workrecord.getWorklocation().getId());
            dbStatement.setString(12, workrecord.getDescription());

            Instant start = Instant.now();
            result = dbStatement.executeUpdate() > 0;
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.debug(String.format("WorkrecordDAO.create returns %b.", result));
            log.debug("Elapsed time: " + timeElapsed + "ms");

            if (result) {
                // Invalidate or update cache after creation
                invalidateCache(workrecord);
            }
        }
        return result;
    }

    public synchronized boolean update(Workrecord original, Workrecord modified) throws SQLException {
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
        statement.append("UPDATE workrecord ");
        statement.append("SET id = ?, userid = ?, projectid = ?, date = ?, starttime = ?, endtime = ?, worktime = ?, overtime = ?, overtimecorrection = ?, vacationcorrection = ?, worklocationid = ?, description = ? ");
        statement.append("WHERE id = ?;");

        try (PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            dbStatement.setLong(1, modified.getId());
            dbStatement.setLong(2, modified.getUser().getId());
            dbStatement.setLong(3, modified.getProject().getId());
            dbStatement.setString(4, modified.getDate().toString());
            dbStatement.setString(5, modified.getStarttime().toString());
            dbStatement.setString(6, modified.getEndtime().toString());
            dbStatement.setString(7, modified.getWorktime().toString());
            dbStatement.setString(8, modified.getOvertime());
            dbStatement.setString(9, modified.getOvertimecorrection());
            dbStatement.setLong(10, modified.getVacationcorrection());
            dbStatement.setLong(11, modified.getWorklocation().getId());
            dbStatement.setString(12, modified.getDescription());
            dbStatement.setLong(13, original.getId());

            Instant start = Instant.now();
            result = dbStatement.executeUpdate() > 0;
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.debug(String.format("WorkrecordDAO.update returns %b.", result));
            log.debug("Elapsed time: " + timeElapsed + "ms");
            if (!result) {
                log.warn(String.format("Update: not possible, as %s does not exist.", original));
            } else {
                // Invalidate or update cache after update
                invalidateCache(modified);
            }
        }
        return result;
    }

    public synchronized boolean delete(Workrecord workrecord) throws SQLException {
        if (workrecord == null) {
            throw new NullPointerException("workrecord");
        }

        boolean result;
        StringBuilder statement = new StringBuilder();
        statement.append("DELETE FROM workrecord ");
        statement.append("WHERE id = ?");

        try (PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            dbStatement.setLong(1, workrecord.getId());

            Instant start = Instant.now();
            result = dbStatement.executeUpdate() > 0;
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.debug(String.format("WorkrecordDAO.delete returns %b.", result));
            log.debug("Elapsed time: " + timeElapsed + "ms");
            if (!result) {
                log.warn(String.format("Delete: not possible, as %s does not exist.", workrecord));
            } else {
                // Invalidate cache after deletion
                invalidateCache(workrecord);
            }
        }
        return result;
    }

    public synchronized Long getNextId() throws SQLException {
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT seq ");
        statement.append("FROM SQLITE_SEQUENCE ");
        statement.append("WHERE name = 'Workrecord';");

        try (PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            ResultSet rs = dbStatement.executeQuery();
            if (rs.next()) {
                return rs.getLong(1) + 1;
            }
        }
        return 0L;
    }

    /**
     * Helper method to invalidate cache entries related to a Workrecord.
     * This is crucial for maintaining cache consistency after CUD operations.
     *
     * @param workrecord The Workrecord that was created, updated, or deleted.
     */
    private void invalidateCache(Workrecord workrecord) {
        // Invalidate individual record cache
        workrecordCacheById.remove(workrecord.getId());
        log.debug(String.format("Cache invalidated for Workrecord ID: %d", workrecord.getId()));

        // Invalidate list cache for the associated user
        if (workrecord.getUser() != null) {
            workrecordCacheByUser.remove(workrecord.getUser().getId());
            log.debug(String.format("Cache invalidated for Workrecord list of User ID: %d", workrecord.getUser().getId()));
        }
        // For other selectAll methods (date range, specific date/time),
        // it's harder to invalidate precisely. For simplicity, we might
        // either not cache those or implement a more complex cache key/invalidation strategy.
        // For now, we only invalidate the specific user's list.
    }
}