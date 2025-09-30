/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sqlite;

import model.Project;
import adapter.Log4jAdapter;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import service.PropertiesService;

/**
 *
 * @author stephan
 */
public class ProjectDAOTest {
    
    private final static String APP_LANGUAGE_RESOURCE_KEY = "Language";
    private final static String APP_LANGUAGE_DEFAULT_VALUE = "en";
    private final static String LANGUAGE_RESOURCE = "languages.bundle";
    private final static String DATABASE_PATH_AND_FULL_NAME = System.getProperty("user.dir") + "/sqlite/testdb.sqlite";
    private final static String LOG4J2_PATH_AND_FULL_NAME = System.getProperty("user.dir") + "/log4j2.xml";
    
    private static ResourceBundle bundle;
    private static ConnectionFactory connectionFactory;
    private static Connection connection;
    
    public ProjectDAOTest() throws FileNotFoundException { 
        bundle = ResourceBundle.getBundle(LANGUAGE_RESOURCE, new Locale(PropertiesService.getInstance().getProperty(APP_LANGUAGE_RESOURCE_KEY, APP_LANGUAGE_DEFAULT_VALUE)));    
        connectionFactory = new ConnectionFactory(bundle, new Log4jAdapter(LOG4J2_PATH_AND_FULL_NAME));
        connection = connectionFactory.getConnection(DATABASE_PATH_AND_FULL_NAME);   
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
        truncateTable();
    }

    @Test(expected = NullPointerException.class)
    public void T00_Calling_Ctor_With_ConnectionIsNull_Throws_NullPointerException() {
        //Arrange
        //Act
        //Assert
        ProjectDAO projectDAO = new ProjectDAO(null);
    }
    
    @Test()
    public void T01_Calling_Ctor_With_Valid_Connection_Returns_Initialized_Instance() {
        //Arrange
        //Act
        ProjectDAO projectDAO = new ProjectDAO(connection);
        
        //Assert
        Assert.assertNotNull(projectDAO);
    }
    
    @Test()
    public void T10_Calling_SelectAll_Returns_All_Found_Projects() throws SQLException {
        //Arrange
        Project project1 = new Project(1L, "TestProject1", "TestCostUnit1", "True", "True", "True", "TestDescription1");
        Project project2 = new Project(2L, "TestProject2", "TestCostUnit2", "True", "True", "True", "TestDescription2");

        ProjectDAO projectDAO = new ProjectDAO(connection);
        projectDAO.create(project1);
        projectDAO.create(project2);
        
        //Act
        List<Project> projectList = projectDAO.selectAll();

        //Assert
        Assert.assertEquals(2, projectList.size());
        Assert.assertEquals(project1, projectList.get(0));
        Assert.assertEquals(project2, projectList.get(1));
    }
    
    //@Test()
    public void T11_Calling_SelectAll_Returns_EmptyList_If_No_Projects_Found() throws SQLException {
        //Arrange
        ProjectDAO projectDAO = new ProjectDAO(connection);
        
        //Act
        List<Project> projectList = projectDAO.selectAll();

        //Assert
        Assert.assertTrue(projectList.isEmpty());
    }
   
    @Test()
    public void T20_Calling_SelectProjectFromId_Returns_The_Stored_Project() throws SQLException {
        //Arrange
        Project project1 = new Project(1L, "TestProject1", "TestCostUnit1", "True", "True", "True", "TestDescription1");
        Project project2 = new Project(2L, "TestProject2", "TestCostUnit2", "True", "True", "True", "TestDescription2");

        ProjectDAO projectDAO = new ProjectDAO(connection);
        projectDAO.create(project1);
        projectDAO.create(project2);
        
        //Act
        Project result = projectDAO.selectProjectFromId(1L);

        //Assert
        Assert.assertEquals(project1, result);
    }
    
    @Test()
    public void T21_Calling_SelectProjectFromId_Returns_Null_If_Project_NotFound() throws SQLException {
        //Arrange
        Project project = new Project(1L, "TestProject", "TestCostUnit", "True", "True", "True", "TestDescription");

        ProjectDAO projectDAO = new ProjectDAO(connection);
        projectDAO.create(project);
        
        //Act
        Project result = projectDAO.selectProjectFromId(2L);

        //Assert
        Assert.assertNull(result);
    }
    
    @Test(expected = NullPointerException.class)
    public void T30_Calling_Create_Project_IsNull_Throws_NullPointerException() throws SQLException {
        //Arrange
        ProjectDAO projectDAO = new ProjectDAO(connection);

        //Act
        boolean result = projectDAO.create(null);

        //Assert
        Assert.assertFalse(result);
    }

    @Test()
    public void T31_Calling_Create_Stores_Project() throws SQLException {
        //Arrange
        Project project = new Project(1L, "TestProject", "TestCostUnit", "True", "True", "True", "TestDescription");

        ProjectDAO projectDAO = new ProjectDAO(connection);
        
        //Act
        boolean result = projectDAO.create(project);
        
        //Assert
        Assert.assertTrue(result);
    }
    
    @Test(expected = SQLException.class)
    public void T32_Calling_Create_Project_AllreadyExists_Throws_SQLException() throws SQLException {
        //Arrange
        Project project = new Project(1L, "TestProject", "TestCostUnit", "True", "True", "True", "TestDescription");

        ProjectDAO projectDAO = new ProjectDAO(connection);
        projectDAO.create(project);

        //Act
        boolean result = projectDAO.create(project);     

        //Assert
        Assert.assertFalse(result);
    }
    
    @Test(expected = NullPointerException.class)
    public void T40_Calling_Update_OriginalProject_IsNull_Throws_NullPointerException() throws SQLException {
        //Arrange
        Project modifiedProject = new Project(1L, "TestProject", "TestCostUnit", "True", "True", "True", "TestDescription");

        ProjectDAO projectDAO = new ProjectDAO(connection);

        //Act
        boolean result = projectDAO.update(null, modifiedProject);

        //Assert
        Assert.assertFalse(result);
    }

    @Test(expected = NullPointerException.class)
    public void T41_Calling_Update_ModifiedProject_IsNull_Throws_NullPointerException() throws SQLException {
        //Arrange
        Project originalProject = new Project(1L, "TestProject", "TestCostUnit", "True", "True", "True", "TestDescription");

        ProjectDAO projectDAO = new ProjectDAO(connection);

        //Act
        boolean result = projectDAO.update(originalProject, null);

        //Assert
        Assert.assertFalse(result);
    }

    @Test()
    public void T42_Calling_Update_Updates_OriginalProject() throws SQLException {
        //Arrange
        Project originalProject = new Project(1L, "TestProject1", "TestCostUnit1", "True", "True", "True", "TestDescription1");
        Project modifiedProject = new Project(1L, "TestProject1", "TestCostUnit1", "True", "True", "True", "TestDescription1");

        ProjectDAO projectDAO = new ProjectDAO(connection);
        projectDAO.create(originalProject);
        
        //Act
        boolean result = projectDAO.update(originalProject, modifiedProject);
        Project projectResult = projectDAO.selectProjectFromId(modifiedProject.getId());
        
        //Assert
        Assert.assertTrue(result);
        Assert.assertEquals(modifiedProject, projectResult);
    }

    @Test()
    public void T43_Calling_Update_OriginalProject_DoesNotExists_Returns_False() throws SQLException {
        //Arrange
        Project originalProject = new Project(1L, "TestProject1", "TestCostUnit1", "True", "True", "True", "TestDescription1");
        Project modifiedProject = new Project(1L, "TestProject1", "TestCostUnit1", "True", "True", "True", "TestDescription1");

        ProjectDAO projectDAO = new ProjectDAO(connection);
        
        //Act
        boolean result = projectDAO.update(originalProject, modifiedProject);
        
        //Assert
        Assert.assertFalse(result);
    }
    
    @Test()
    public void T44_Calling_Update_On_Different_Projects_DoenNotChange_Contract() throws SQLException {
        //Arrange
        Project originalProject = new Project(1L, "TestProject1", "TestCostUnit1", "True", "True", "True", "TestDescription1");
        Project modifiedProject = new Project(2L, "TestProject1", "TestCostUnit1", "True", "True", "True", "TestDescription1");

        ProjectDAO projectDAO = new ProjectDAO(connection);
        projectDAO.create(originalProject);

        //Act
        boolean result = projectDAO.update(originalProject, modifiedProject);
        Project projectResult = projectDAO.selectProjectFromId(originalProject.getId());

        //Assert
        Assert.assertFalse(result);
        Assert.assertEquals(originalProject, projectResult);
    }
   
    @Test(expected = NullPointerException.class)
    public void T50_Calling_Delete_Project_IsNull_Throws_NullPointerException() throws SQLException {
        //Arrange
        ProjectDAO projectDAO = new ProjectDAO(connection);

        //Act
        //Assert
        boolean result = projectDAO.delete(null);
    }
    
    @Test()
    public void T51_Calling_Delete_Project_DoesNotExists_Returns_False() throws SQLException {
        //Arrange
        Project project = new Project(1L, "TestProject", "TestCostUnit", "True", "True","True", "TestDescription");

        ProjectDAO projectDAO = new ProjectDAO(connection);

        //Act
        boolean result = projectDAO.delete(project);

        //Assert
        Assert.assertFalse(result);
    }

    @Test()
    public void T52_Calling_Delete_Contract_Exists_Deletes_Contract() throws SQLException {
        //Arrange
        Project project = new Project(1L, "TestProject", "TestCostUnit", "True", "True","True", "TestDescription");

        ProjectDAO projectDAO = new ProjectDAO(connection);
        projectDAO.create(project);
        
        //Act
        boolean result = projectDAO.delete(project);
        Project projectResult = projectDAO.selectProjectFromId(project.getId());

        //Assert
        Assert.assertTrue(result);
        Assert.assertNull(projectResult);
    }
    
    @Test()
    public void T60_Calling_GetNextId_On_ProjectTable_Containing_One_Record_Returns_2() throws SQLException {
        //Arrange
        Project project = new Project(1L, "TestProject", "TestCostUnit", "True", "True", "True", "TestDescription");

        ProjectDAO projectDAO = new ProjectDAO(connection);
        projectDAO.create(project);
        
        //Act
        long receivedId = projectDAO.getNextId();
        
        //Assert
        Assert.assertEquals(2L, receivedId);
    }
    
    @Test()
    public void T61_Calling_GetNextId_Twice_On_ProjectTable_Returns_SameId() throws SQLException {
        //Arrange
        Project project = new Project(1L, "TestProject", "TestCostUnit", "True", "True", "True", "TestDescription");

        ProjectDAO projectDAO = new ProjectDAO(connection);
        projectDAO.create(project);
        
        //Act
        long receivedIdFirstCall = projectDAO.getNextId();
        long receivedIdSecondCall = projectDAO.getNextId();
        
        //Assert
        Assert.assertEquals(receivedIdSecondCall, receivedIdFirstCall);
    }

    @Test
    public void T62_Calling_GetNextId_On_Truncated_Sqlite_Sequence_Table_Returns_0() throws SQLException {
        //Arrange
        ProjectDAO projectDAO = new ProjectDAO(connection);
        
        //Act
        long resultId = projectDAO.getNextId();

        //Assert
        Assert.assertEquals(0L, resultId);
    }
    
    private synchronized boolean truncateTable() {
        boolean result = false;
        StringBuilder statement = new StringBuilder();
        statement.append("DELETE FROM project ");
        
        try {
            PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
            result = dbStatement.execute();
        } catch (SQLException e) {
            return result;
        }
        
        statement = new StringBuilder();        
        //statement.append("UPDATE SQLITE_SEQUENCE SET seq = 0 WHERE name='Project'");
        statement.append("DELETE FROM SQLITE_SEQUENCE WHERE name='Project'");
        try {
            PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
            result = dbStatement.execute();
        } catch (SQLException e) {
            return result;
        }
        return result;
    }
    
}
