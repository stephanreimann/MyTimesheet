package sqlite;

import model.Project;
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
 * Data Access Object (DAO) for managing Project objects in a SQLite database,
 * now with added caching for improved performance on frequent read operations.
 *
 * @author adrest18
 */
public class ProjectDAO {

    private final Logger log;
    private final Connection connection;

    // Caching mechanism
    // Cache for individual Project objects by their ID
    private final Map<Long, Project> projectCacheById;
    // Cache for all projects (for selectAll())
    private List<Project> allProjectsCache;

    public ProjectDAO(Connection connection) {
        if (connection == null) {
            throw new NullPointerException("connection");
        }

        this.log = LogManager.getLogger(ProjectDAO.class.getName());
        this.connection = connection;

        // Initialize caches
        this.projectCacheById = new ConcurrentHashMap<>();
        this.allProjectsCache = null; // Initialize as null, will be populated on first selectAll()
        log.debug("ProjectDAO initialized with caching enabled.");
    }

    /**
     * Retrieves all projects, utilizing a cache.
     * If the data is in the cache, it's returned immediately. Otherwise,
     * it fetches from the database and populates the cache.
     *
     * @return A list of all Project objects.
     * @throws SQLException If a database access error occurs.
     */
    public synchronized List<Project> selectAll() throws SQLException {
        // Try to retrieve from cache first
        if (allProjectsCache != null) {
            log.debug(String.format("ProjectDAO.selectAll() returns %d projects. (Cache hit)", allProjectsCache.size()));
            return new ArrayList<>(allProjectsCache); // Return a copy to prevent external modification
        }

        List<Project> resultList = new ArrayList<>();

        StringBuilder statement = new StringBuilder();
        statement.append("SELECT id, name, costunit, isworktimerelevant, isvacationrelevant, iscomptimerelevant, description ");
        statement.append("FROM project;");

        Instant start = Instant.now();
        // Use try-with-resources for PreparedStatement and ResultSet
        try (PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
             ResultSet resultSet = dbStatement.executeQuery()) {

            while (resultSet.next()) {
                Project project = createProjectFromResultSetEntry(resultSet);
                resultList.add(project);
                // Also cache individual records when fetching a list
                projectCacheById.put(project.getId(), project);
            }
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.debug(String.format("ProjectDAO.selectAll() returns %d projects. (DB hit)", resultList.size()));
            log.debug("Elapsed time: " + timeElapsed + "ms");

            // Populate the cache for all projects
            allProjectsCache = new ArrayList<>(resultList); // Store a copy
        }
        return resultList;
    }

    /**
     * Retrieves a Project by its ID, utilizing a cache.
     * If the record is in the cache, it's returned immediately. Otherwise,
     * it fetches from the database and populates the cache.
     *
     * @param id The ID of the project to retrieve.
     * @return The Project object if found, or null if not.
     * @throws SQLException If a database access error occurs.
     */
    public synchronized Project selectProjectFromId(long id) throws SQLException {
        // Try to retrieve from cache first
        if (projectCacheById.containsKey(id)) {
            log.debug(String.format("ProjectDAO.selectProjectFromId(%d) returns a project. (Cache hit)", id));
            return projectCacheById.get(id);
        }

        StringBuilder statement = new StringBuilder();
        statement.append("SELECT id, name, costunit, isworktimerelevant, isvacationrelevant, iscomptimerelevant, description ");
        statement.append("FROM project ");
        statement.append("WHERE id = ?;");

        Instant start = Instant.now();
        // Use try-with-resources for PreparedStatement and ResultSet
        try (PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            dbStatement.setLong(1, id); // Use setLong for long type
            try (ResultSet resultSet = dbStatement.executeQuery()) {
                if (resultSet.next()) { // Changed from while to if, as we expect only one result for an ID
                    Project project = createProjectFromResultSetEntry(resultSet);
                    Instant finish = Instant.now();
                    long timeElapsed = Duration.between(start, finish).toMillis();
                    log.debug(String.format("ProjectDAO.selectProjectFromId(%d) returns a project. (DB hit)", id));
                    log.debug("Elapsed time: " + timeElapsed + "ms");

                    // Add to cache
                    projectCacheById.put(id, project);
                    return project;
                }
            }
        }
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        log.debug(String.format("ProjectDAO.selectProjectFromId(%d) returns null. (DB hit)", id));
        log.debug("Elapsed time: " + timeElapsed + "ms");
        log.warn(String.format("Select: Project with Id %d not found.", id));
        return null;
    }

    private Project createProjectFromResultSetEntry(ResultSet resultSet) throws SQLException {
        Long rsId = resultSet.getLong("id");
        String rsName = resultSet.getString("name");
        String rsCostunit = resultSet.getString("costunit");
        String rsIsWorktimeRelevant = resultSet.getString("isworktimerelevant");
        String rsIsVacationRelevant = resultSet.getString("isvacationrelevant");
        String rsIsComptimeRelevant = resultSet.getString("iscomptimerelevant");
        String rsDescription = resultSet.getString("description");

        return new Project(rsId, rsName, rsCostunit, rsIsWorktimeRelevant, rsIsVacationRelevant, rsIsComptimeRelevant, rsDescription);
    }

    public synchronized boolean create(Project project) throws SQLException {
        if (project == null) {
            throw new NullPointerException("project"); // Corrected from "contract"
        }

        boolean result;
        StringBuilder statement = new StringBuilder();
        statement.append("INSERT INTO project (id, name, costunit, isworktimerelevant, isvacationrelevant, iscomptimerelevant, description) ");
        statement.append("VALUES (?, ?, ?, ?, ?, ?, ?)");

        Instant start = Instant.now();
        try (PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            dbStatement.setLong(1, project.getId());
            dbStatement.setString(2, project.getName());
            dbStatement.setString(3, project.getCostunit());
            dbStatement.setString(4, project.getIsWorktimeRelevant());
            dbStatement.setString(5, project.getIsVacationRelevant());
            dbStatement.setString(6, project.getIsComptimeRelevant());
            dbStatement.setString(7, project.getDescription());

            result = dbStatement.executeUpdate() > 0;
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.debug(String.format("ProjectDAO.create returns %b.", result));
            log.debug("Elapsed time: " + timeElapsed + "ms");

            if (result) {
                // Invalidate cache after creation
                invalidateCache(project);
            }
        }
        return result;
    }

    public synchronized boolean update(Project original, Project modified) throws SQLException {
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
        statement.append("UPDATE project ");
        statement.append("SET name = ?, costunit = ?, isworktimerelevant = ?, isvacationrelevant = ?, iscomptimerelevant = ?, description = ? "); // Removed 'id = ?' from SET clause as it's typically in WHERE
        statement.append("WHERE id = ?;");

        Instant start = Instant.now();
        try (PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            // Parameters shifted due to removal of 'id = ?' from SET
            dbStatement.setString(1, modified.getName());
            dbStatement.setString(2, modified.getCostunit());
            dbStatement.setString(3, modified.getIsWorktimeRelevant());
            dbStatement.setString(4, modified.getIsVacationRelevant());
            dbStatement.setString(5, modified.getIsComptimeRelevant());
            dbStatement.setString(6, modified.getDescription());
            dbStatement.setLong(7, original.getId()); // WHERE clause parameter

            result = dbStatement.executeUpdate() > 0;
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.debug(String.format("ProjectDAO.update returns %b.", result));
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

    public synchronized boolean delete(Project project) throws SQLException {
        if (project == null) {
            throw new NullPointerException("project"); // Corrected from "contract"
        }

        boolean result;
        StringBuilder statement = new StringBuilder();
        statement.append("DELETE FROM project ");
        statement.append("WHERE id = ?");

        Instant start = Instant.now();
        try (PreparedStatement dbStatement = connection.prepareStatement(statement.toString())) {
            dbStatement.setLong(1, project.getId());

            result = dbStatement.executeUpdate() > 0;
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.debug(String.format("ProjectDAO.delete returns %b.", result));
            log.debug("Elapsed time: " + timeElapsed + "ms");
            if (!result) {
                log.warn(String.format("Delete: not possible, as %s does not exist.", project));
            } else {
                // Invalidate cache after deletion
                invalidateCache(project);
            }
        }
        return result;
    }

    public synchronized Long getNextId() throws SQLException {
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT seq ");
        statement.append("FROM SQLITE_SEQUENCE ");
        statement.append("WHERE name = 'Project';");

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
     * Helper method to invalidate cache entries related to a Project.
     * This is crucial for maintaining cache consistency after CUD operations.
     *
     * @param project The Project that was created, updated, or deleted.
     */
    private void invalidateCache(Project project) {
        // Invalidate individual record cache
        projectCacheById.remove(project.getId());
        log.debug(String.format("Cache invalidated for Project ID: %d", project.getId()));

        // Invalidate the allProjectsCache, as its contents are now stale
        allProjectsCache = null;
        log.debug("Cache for all projects invalidated.");
    }
}