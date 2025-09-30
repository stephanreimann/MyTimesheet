/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sqlite;

import adapter.Log4jAdapter;
import java.sql.Connection;
import java.util.ResourceBundle;
import org.junit.*;
import static org.junit.Assert.*;
import org.mockito.Mockito;

/**
 *
 * @author adrest18
 */
public class ConnectionFactoryTest {
    
    public ConnectionFactoryTest() {
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
    }

    @Test(expected=NullPointerException.class)
    public void T00_IsRunning_Called_ResourceBundle_IsNull_Throws_NullPointerException() {
        //Arrange
        Log4jAdapter log4jAdapterMock = Mockito.mock(Log4jAdapter.class);
        
        //Act
        //Assert
        ConnectionFactory connectionFactory = new ConnectionFactory(null, log4jAdapterMock);
    }
    
    @Test(expected=NullPointerException.class)
    public void T01_IsRunning_Called_Log4jAdapter_IsNull_Throws_NullPointerException() {
        //Arrange
        ResourceBundle resourceBundleMock = Mockito.mock(ResourceBundle.class);
        
        //Act
        //Assert
        ConnectionFactory connectionFactory = new ConnectionFactory(resourceBundleMock, null);
    }

    @Test
    public void T02_Ctor_Called_Returns_Instance() {
        //Arrange
        ResourceBundle resourceBundleMock = Mockito.mock(ResourceBundle.class);
        Log4jAdapter log4jAdapterMock = Mockito.mock(Log4jAdapter.class);
        
        //Act
        ConnectionFactory connectionFactory = new ConnectionFactory(resourceBundleMock, log4jAdapterMock);
        
        //Assert
        assertNotNull(connectionFactory);
    }
    
    @Test
    public void T10_Call_GetConnection_Database_DoesNotExists_Returns_Null() {
        //Arrange
        ResourceBundle resourceBundleMock = Mockito.mock(ResourceBundle.class);
        Log4jAdapter log4jAdapterMock = Mockito.mock(Log4jAdapter.class);

        ConnectionFactory connectionFactory = new ConnectionFactory(resourceBundleMock, log4jAdapterMock);

        String unknownDatabase = "unknownPathAndFullFile";

        //Act
        Connection connection = connectionFactory.getConnection(unknownDatabase);
        
        //Assert
        assertNull(connection);
    }

    @Test
    public void T11_Call_GetConnection_CalledFirstTime_Returns_Connection() {
        //Arrange
        ResourceBundle resourceBundleMock = Mockito.mock(ResourceBundle.class);
        Log4jAdapter log4jAdapterMock = Mockito.mock(Log4jAdapter.class);

        ConnectionFactory connectionFactory = new ConnectionFactory(resourceBundleMock, log4jAdapterMock);
        
        String databasePathAndFullName = System.getProperty("user.dir").concat("/sqlite/testdb.sqlite");
       
        //Act
        Connection connection = connectionFactory.getConnection(databasePathAndFullName);
        
        //Assert
        assertNotNull(connection);
    }

    @Test
    public void T12_Call_GetConnection_CalledTwice_Returns_Same_Connection_Object() {
        //Arrange
        ResourceBundle resourceBundleMock = Mockito.mock(ResourceBundle.class);
        Log4jAdapter log4jAdapterMock = Mockito.mock(Log4jAdapter.class);

        ConnectionFactory connectionFactory = new ConnectionFactory(resourceBundleMock, log4jAdapterMock);

        String databasePathAndFullName = System.getProperty("user.dir").concat("/sqlite/testdb.sqlite");
       
        Connection connectionFirstCall = connectionFactory.getConnection(databasePathAndFullName);
        
        //Act
        Connection connectionSecondCall = connectionFactory.getConnection(databasePathAndFullName);
        
        //Assert
        assertSame(connectionFirstCall, connectionSecondCall);
    }
    
}
