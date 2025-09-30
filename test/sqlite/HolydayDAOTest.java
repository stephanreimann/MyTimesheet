/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package sqlite;

import adapter.Log4jAdapter;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import model.Holyday;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import service.PropertiesService;

/**
 *
 * @author adrest18
 */
public class HolydayDAOTest {
    
    private final static String APP_LANGUAGE_RESOURCE_KEY = "Language";
    private final static String APP_LANGUAGE_DEFAULT_VALUE = "en";
    private final static String LANGUAGE_RESOURCE = "languages.bundle";
    private final static String DATABASE_PATH_AND_FULL_NAME = System.getProperty("user.dir") + "/sqlite/testdb.sqlite";
    private final static String LOG4J2_PATH_AND_FULL_NAME = System.getProperty("user.dir") + "/log4j2.xml";
    
    private static ResourceBundle bundle;
    private static ConnectionFactory connectionFactory;
    private static Connection connection;

    public HolydayDAOTest() throws FileNotFoundException {
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
        HolydayDAO holydayDAO = new HolydayDAO(null);
    }
    
    @Test()
    public void T01_Calling_Ctor_With_Valid_Connection_Returns_Initialized_Instance() {
        //Arrange
        //Act
        HolydayDAO holydayDAO = new HolydayDAO(connection);
        
        //Assert
        Assert.assertNotNull(holydayDAO);
    }

    @Test()
    public void T10_Calling_SelectAll_Returns_All_Found_Holydays() throws SQLException {
        //Arrange
        Holyday holyday1 = new Holyday(1L, LocalDate.now(), "SomeHolyday1", "Bayern");
        Holyday holyday2 = new Holyday(2L, LocalDate.now().plusDays(1), "SomeHolyday2", "Bayern");

        HolydayDAO holydayDAO = new HolydayDAO(connection);
        holydayDAO.create(holyday1);
        holydayDAO.create(holyday2);
        
        //Act
        List<Holyday> holydayList = holydayDAO.selectAll();

        //Assert
        Assert.assertEquals(2, holydayList.size());
        Assert.assertEquals(holyday1, holydayList.get(0));
        Assert.assertEquals(holyday2, holydayList.get(1));
    }
    
    //@Test()
    public void T11_Calling_SelectAll_Returns_EmptyList_If_No_Holydays_Found() throws SQLException {
        //Arrange
        HolydayDAO holydayDAO = new HolydayDAO(connection);
        
        //Act
        List<Holyday> holydayList = holydayDAO.selectAll();

        //Assert
        Assert.assertTrue(holydayList.isEmpty());
    }

    @Test()
    public void T20_Calling_SelectHolydayFromId_Returns_The_Stored_Holyday() throws SQLException {
        //Arrange
        Holyday holyday1 = new Holyday(1L, LocalDate.now(), "SomeHolyday1", "Bayern");
        Holyday holyday2 = new Holyday(2L, LocalDate.now().plusDays(1), "SomeHolyday2", "Bayern");

        HolydayDAO holydayDAO = new HolydayDAO(connection);
        holydayDAO.create(holyday1);
        holydayDAO.create(holyday2);
        
        //Act
        Holyday holyday = holydayDAO.selectHolydayFromId(1L);

        //Assert
        Assert.assertEquals(holyday1, holyday);
    }

    @Test()
    public void T21_Calling_SelectHolydayFromId_Returns_Null_If_Holyday_NotFound() throws SQLException {
        //Arrange
        Holyday holyday = new Holyday(1L, LocalDate.now(), "SomeHolyday", "Bayern");

        HolydayDAO holydayDAO = new HolydayDAO(connection);
        holydayDAO.create(holyday);
        
        //Act
        Holyday result = holydayDAO.selectHolydayFromId(2L);

        //Assert
        Assert.assertNull(result);
    }

    @Test(expected = NullPointerException.class)
    public void T30_Calling_Create_Holyday_IsNull_Throws_NullPointerException() throws SQLException {
        //Arrange
        HolydayDAO holydayDAO = new HolydayDAO(connection);

        //Act
        boolean result = holydayDAO.create(null);

        //Assert
        Assert.assertFalse(result);
    }

    @Test()
    public void T31_Calling_Create_Stores_Holyday() throws SQLException {
        //Arrange
        Holyday holyday = new Holyday(1L, LocalDate.now(), "SomeHolyday", "Bayern");

        HolydayDAO holydayDAO = new HolydayDAO(connection);
        
        //Act
        boolean result = holydayDAO.create(holyday);
        
        //Assert
        Assert.assertTrue(result);
    }
    
    @Test(expected = SQLException.class)
    public void T32_Calling_Create_Holyday_AllreadyExists_Throws_SQLException() throws SQLException {
        //Arrange
        Holyday holyday = new Holyday(1L, LocalDate.now(), "SomeHolyday", "Bayern");

        HolydayDAO holydayDAO = new HolydayDAO(connection);
        
        holydayDAO.create(holyday);

        //Act
        boolean result = holydayDAO.create(holyday);        

        //Assert
        Assert.assertFalse(result);
    }

    @Test(expected = NullPointerException.class)
    public void T40_Calling_Update_OriginalHolyday_IsNull_Throws_NullPointerException() throws SQLException {
        //Arrange
        Holyday holyday = new Holyday(1L, LocalDate.now(), "SomeHolyday", "Bayern");
        
        HolydayDAO holydayDAO = new HolydayDAO(connection);

        //Act
        boolean result = holydayDAO.update(null, holyday);

        //Assert
        Assert.assertFalse(result);
    }

    @Test(expected = NullPointerException.class)
    public void T41_Calling_Update_ModifiedHolyday_IsNull_Throws_NullPointerException() throws SQLException {
        //Arrange
        Holyday holyday = new Holyday(1L, LocalDate.now(), "SomeHolyday", "Bayern");
        
        HolydayDAO holydayDAO = new HolydayDAO(connection);

        //Act
        boolean result = holydayDAO.update(holyday, null);

        //Assert
        Assert.assertFalse(result);
    }

    @Test()
    public void T42_Calling_Update_Updates_OriginalHolyday() throws SQLException {
        //Arrange
        Holyday originalHolyday = new Holyday(1L, LocalDate.now(), "SomeHolyday", "Bayern");
        Holyday modifiedHolyday = new Holyday(1L, LocalDate.now().plusDays(1), "SomeHolyday", "Bayern");
        
        HolydayDAO holydayDAO = new HolydayDAO(connection);
        holydayDAO.create(originalHolyday);
        
        //Act
        boolean result = holydayDAO.update(originalHolyday, modifiedHolyday);
        Holyday holydayResult = holydayDAO.selectHolydayFromId(modifiedHolyday.getId());
        
        //Assert
        Assert.assertTrue(result);
        Assert.assertEquals(modifiedHolyday, holydayResult);
    }

    @Test()
    public void T43_Calling_Update_OriginalHolyday_DoesNotExists_Returns_False() throws SQLException {
        //Arrange
        Holyday originalHolyday = new Holyday(1L, LocalDate.now(), "SomeHolyday1", "Bayern");
        Holyday modifiedHolyday = new Holyday(2L, LocalDate.now().plusYears(1), "SomeHolyday1", "Bayern");
        
        HolydayDAO holydayDAO = new HolydayDAO(connection);
        
        //Act
        boolean result = holydayDAO.update(originalHolyday, modifiedHolyday);
        
        //Assert
        Assert.assertFalse(result);
    }
    
    @Test()
    public void T44_Calling_Update_On_Different_Holyday_DoenNotChange_Holyday() throws SQLException {
        //Arrange
        Holyday originalHolyday = new Holyday(1L, LocalDate.now(), "SomeHolyday1", "Bayern");
        Holyday modifiedHolyday = new Holyday(2L, LocalDate.now().plusYears(1), "SomeHolyday2", "Bayern");
        
        HolydayDAO holydayDAO = new HolydayDAO(connection);
        holydayDAO.create(originalHolyday);

        //Act
        boolean result = holydayDAO.update(originalHolyday, modifiedHolyday);
        Holyday holydayResult = holydayDAO.selectHolydayFromId(originalHolyday.getId());

        //Assert
        Assert.assertFalse(result);
        Assert.assertEquals(originalHolyday, holydayResult);
    }

    @Test(expected = NullPointerException.class)
    public void T50_Calling_Delete_Holyday_IsNull_Throws_NullPointerException() throws SQLException {
        //Arrange
        HolydayDAO holydayDAO = new HolydayDAO(connection);

        //Act
        //Assert
        boolean result = holydayDAO.delete(null);
    }
    
    @Test()
    public void T51_Calling_Delete_Holyday_DoesNotExists_Returns_False() throws SQLException {
        //Arrange
        Holyday holyday = new Holyday(1L, LocalDate.now(), "SomeHolyday", "Bayern");

        HolydayDAO holydayDAO = new HolydayDAO(connection);

        //Act
        boolean result = holydayDAO.delete(holyday);

        //Assert
        Assert.assertFalse(result);
    }

    @Test()
    public void T52_Calling_Delete_Holyday_Exists_Deletes_Holyday() throws SQLException {
        //Arrange
        Holyday holyday = new Holyday(1L, LocalDate.now(), "SomeHolyday", "Bayern");

        HolydayDAO holydayDAO = new HolydayDAO(connection);
        holydayDAO.create(holyday);
        
        //Act
        boolean result = holydayDAO.delete(holyday);
        Holyday holydayResult = holydayDAO.selectHolydayFromId(holyday.getId());

        //Assert
        Assert.assertTrue(result);
        Assert.assertNull(holydayResult);
    }

    @Test()
    public void T60_Calling_GetNextId_On_HolydayTable_Containing_One_Record_Returns_2() throws SQLException {
        //Arrange
        Holyday holyday = new Holyday(1L, LocalDate.now(), "SomeHolyday", "Bayern");

        HolydayDAO holydayDAO = new HolydayDAO(connection);
        holydayDAO.create(holyday);
        
        //Act
        long receivedId = holydayDAO.getNextId();
        
        //Assert
        Assert.assertEquals(2L, receivedId);
    }
    
    @Test()
    public void T61_Calling_GetNextId_Twice_On_HolydayTable_Returns_SameId() throws SQLException {
        //Arrange
        Holyday holyday = new Holyday(1L, LocalDate.now(), "SomeHolyday", "Bayern");

        HolydayDAO holydayDAO = new HolydayDAO(connection);
        holydayDAO.create(holyday);
        
        //Act
        long receivedIdFirstCall = holydayDAO.getNextId();
        long receivedIdSecondCall = holydayDAO.getNextId();
        
        //Assert
        Assert.assertEquals(receivedIdSecondCall, receivedIdFirstCall);
    }

    @Test
    public void T62_Calling_GetNextId_On_Truncated_Sqlite_Sequence_Table_Returns_0() throws SQLException {
        //Arrange
        HolydayDAO holydayDAO = new HolydayDAO(connection);
        
        //Act
        long resultId = holydayDAO.getNextId();

        //Assert
        Assert.assertEquals(0L, resultId);
    }
    
    private synchronized boolean truncateTable() {
        boolean result = false;
        StringBuilder statement = new StringBuilder();
        statement.append("DELETE FROM holyday ");
        
        try {
            PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
            result = dbStatement.execute();
        } catch (SQLException e) {
            return result;
        }
        
        statement = new StringBuilder();        
        //statement.append("UPDATE SQLITE_SEQUENCE SET seq = 0 WHERE name='Holyday'");
        statement.append("DELETE FROM SQLITE_SEQUENCE WHERE name='Holyday'");
        try {
            PreparedStatement dbStatement = connection.prepareStatement(statement.toString());
            result = dbStatement.execute();
        } catch (SQLException e) {
            return result;
        }
        return result;
    }
    
}
