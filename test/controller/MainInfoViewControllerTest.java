/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import adapter.Log4jAdapter;
import java.net.*;
import java.sql.Connection;
import java.util.ResourceBundle;
import org.mockito.Mockito;
import service.*;
import org.junit.Test;
/**
 *
 * @author adrest18
 */
public class MainInfoViewControllerTest {
    
    @Test(expected=NullPointerException.class)
    public void T00_Ctor_Called_LanguageService_IsNull_ThrowsNullPointerException() {
        //Arrange
        Connection connectionMock = Mockito.mock(Connection.class);
        UndoService undoServiceMock = Mockito.mock(UndoService.class);
        Log4jAdapter log4jAdapterMock = Mockito.mock(Log4jAdapter.class);
        
        //Act
        //Assert
        MainInfoViewController mainInfoViewController = new MainInfoViewController(null, connectionMock, undoServiceMock, log4jAdapterMock); 
    }
    
    @Test(expected=NullPointerException.class)
    public void T01_Ctor_Called_Connection_IsNull_ThrowsNullPointerException() {
        //Arrange
        LanguageService languageServiceMock = Mockito.mock(LanguageService.class);
        UndoService undoServiceMock = Mockito.mock(UndoService.class);
        Log4jAdapter log4jAdapterMock = Mockito.mock(Log4jAdapter.class);
        
        //Act
        //Assert
        MainInfoViewController mainInfoViewController = new MainInfoViewController(languageServiceMock, null, undoServiceMock, log4jAdapterMock);
    }

    @Test(expected=NullPointerException.class)
    public void T02_Ctor_Called_UndoService_IsNull_ThrowsNullPointerException() {
        //Arrange
        LanguageService languageServiceMock = Mockito.mock(LanguageService.class);
        Connection connectionMock = Mockito.mock(Connection.class);
        Log4jAdapter log4jAdapterMock = Mockito.mock(Log4jAdapter.class);
        
        //Act
        //Assert
        MainInfoViewController mainInfoViewController = new MainInfoViewController(languageServiceMock, connectionMock, null, log4jAdapterMock);
    }
    
    @Test(expected=NullPointerException.class)
    public void T03_Ctor_Called_Log4jAdapter_IsNull_ThrowsNullPointerException() {
        //Arrange
        LanguageService languageServiceMock = Mockito.mock(LanguageService.class);
        Connection connectionMock = Mockito.mock(Connection.class);
        UndoService undoServiceMock = Mockito.mock(UndoService.class);
        
        //Act
        //Assert
        MainInfoViewController mainInfoViewController = new MainInfoViewController(languageServiceMock, connectionMock, undoServiceMock, null);
    }

    @Test(expected=NullPointerException.class)
    public void T10_Initialize_Called_Url_IsNull_Throws_NullPointerException() {
        //Arrange
        LanguageService languageServiceMock = Mockito.mock(LanguageService.class);
        Connection connectionMock = Mockito.mock(Connection.class);
        UndoService undoServiceMock = Mockito.mock(UndoService.class);
        Log4jAdapter log4jAdapterMock = Mockito.mock(Log4jAdapter.class);

        MainInfoViewController mainInfoViewController = new MainInfoViewController(languageServiceMock, connectionMock, undoServiceMock, log4jAdapterMock);
        
        ResourceBundle resourceBundleMock = Mockito.mock(ResourceBundle.class);
        
        //Act
        //Assert
        mainInfoViewController.initialize(null, resourceBundleMock);
    }

    @Test(expected=NullPointerException.class)
    public void T11_Initialize_Called_ResourceBundle_IsNull_Throws_NullPointerException() throws MalformedURLException {
        //Arrange
        LanguageService languageServiceMock = Mockito.mock(LanguageService.class);
        Connection connectionMock = Mockito.mock(Connection.class);
        UndoService undoServiceMock = Mockito.mock(UndoService.class);
        Log4jAdapter log4jAdapterMock = Mockito.mock(Log4jAdapter.class);

        MainInfoViewController mainInfoViewController = new MainInfoViewController(languageServiceMock, connectionMock, undoServiceMock, log4jAdapterMock);
        
        URL url = new URL("file:/E:/Develop/Java/JavaFXDefaultApplication/dist/run1909416252/JavaFxDefaultApplication.jar!/view/MainInfoView.fxml");

        //Act
        //Assert
        mainInfoViewController.initialize(url, null);
    }
    
}
