/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commands;

import adapter.Log4jAdapter;
import controller.*;
import javafx.collections.*;
import static org.junit.Assert.*;
import org.junit.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;

/**
 *
 * @author adrest18
 */
public class ClearLogListCommandTest {
    
    @Test(expected=NullPointerException.class)
    public void T00_Ctor_Called_ControllerRepository_IsNull_Throws_NullPointerException() {
        //Arrange
        ObservableList<String> infoViewEntriesMock = Mockito.mock(ObservableList.class);
        Log4jAdapter log4jAdapterMock = Mockito.mock(Log4jAdapter.class);
        
        //Act
        //Assert
        ClearLogListCommand clearLogListCommand = new ClearLogListCommand(null, infoViewEntriesMock, log4jAdapterMock);
    }

    @Test(expected=NullPointerException.class)
    public void T01_Ctor_Called_InfoViewEntries_IsNull_Throws_NullPointerException() {
        //Arrange
        ControllerRepository controllerRepositoryMock = Mockito.mock(ControllerRepository.class);
        Log4jAdapter log4jAdapterMock = Mockito.mock(Log4jAdapter.class);
        
        //Act
        //Assert
        ClearLogListCommand clearLogListCommand = new ClearLogListCommand(controllerRepositoryMock, null, log4jAdapterMock);
    }
    
    @Test(expected=NullPointerException.class)
    public void T02_Ctor_Called_Log4jAdapter_IsNull_Throws_NullPointerException() {
        //Arrange
        ControllerRepository controllerRepositoryMock = Mockito.mock(ControllerRepository.class);
        ObservableList<String> infoViewEntriesMock = Mockito.mock(ObservableList.class);
        
        //Act
        //Assert
        ClearLogListCommand clearLogListCommand = new ClearLogListCommand(controllerRepositoryMock, infoViewEntriesMock, null);
    }

    @Test
    public void T03_Ctor_Called_Returns_Instance() {
        MainToolBarViewController mainToolBarViewControllerMock = Mockito.mock(MainToolBarViewController.class);
        MainInfoViewController mainInfoViewControllerMock = Mockito.mock(MainInfoViewController.class);
        Log4jAdapter log4jAdapterMock = Mockito.mock(Log4jAdapter.class);
        
        ControllerRepository controllerRepositoryMock = Mockito.mock(ControllerRepository.class);
        when(controllerRepositoryMock.get(MainToolBarViewController.class.getName())).thenReturn(mainToolBarViewControllerMock);
        when(controllerRepositoryMock.get(MainInfoViewController.class.getName())).thenReturn(mainInfoViewControllerMock);
        
        ObservableList<String> infoViewEntries = FXCollections.observableArrayList();

        //Act
        ClearLogListCommand clearLogListCommand = new ClearLogListCommand(controllerRepositoryMock, infoViewEntries, log4jAdapterMock);
        
        //Assert
        assertNotNull(clearLogListCommand);
    }
    
    @Test
    public void T04_Execute_Called_Clears_LogListEntries() {
        //Arrange
        MainToolBarViewController mainToolBarViewControllerMock = Mockito.mock(MainToolBarViewController.class);        
        MainMenuBarViewController mainMenuBarViewControllerMock = Mockito.mock(MainMenuBarViewController.class);
        MainInfoViewController mainInfoViewControllerMock = Mockito.mock(MainInfoViewController.class);
        Log4jAdapter log4jAdapterMock = Mockito.mock(Log4jAdapter.class);
        
        ControllerRepository controllerRepositoryMock = Mockito.mock(ControllerRepository.class);
        when(controllerRepositoryMock.get(MainToolBarViewController.class.getName())).thenReturn(mainToolBarViewControllerMock);
        when(controllerRepositoryMock.get(MainInfoViewController.class.getName())).thenReturn(mainInfoViewControllerMock);
        when(controllerRepositoryMock.get(MainMenuBarViewController.class.getName())).thenReturn(mainMenuBarViewControllerMock);
        
        ObservableList<String> infoViewEntries = FXCollections.observableArrayList();
        infoViewEntries.add("First_Entry");
        
        ClearLogListCommand clearLogListCommand = new ClearLogListCommand(controllerRepositoryMock, infoViewEntries, log4jAdapterMock);

        //Act
        clearLogListCommand.execute();
        
        //Assert
        assertTrue(infoViewEntries.isEmpty());
        Mockito.verify(mainToolBarViewControllerMock).toggleUndoRedoButtons();
    }
    
    @Test
    public void T05_Undo_Called_Reverts_Old_LogListEntries() {
        //Arrange
        MainToolBarViewController mainToolBarViewControllerMock = Mockito.mock(MainToolBarViewController.class);
        MainMenuBarViewController mainMenuBarViewControllerMock = Mockito.mock(MainMenuBarViewController.class);
        MainInfoViewController mainInfoViewControllerMock = Mockito.mock(MainInfoViewController.class);
        Log4jAdapter log4jAdapterMock = Mockito.mock(Log4jAdapter.class);
        
        ControllerRepository controllerRepositoryMock = Mockito.mock(ControllerRepository.class);
        when(controllerRepositoryMock.get(MainToolBarViewController.class.getName())).thenReturn(mainToolBarViewControllerMock);
        when(controllerRepositoryMock.get(MainInfoViewController.class.getName())).thenReturn(mainInfoViewControllerMock);
        when(controllerRepositoryMock.get(MainMenuBarViewController.class.getName())).thenReturn(mainMenuBarViewControllerMock);
        
        ObservableList<String> infoViewEntries = FXCollections.observableArrayList();
        infoViewEntries.add("First_Entry");
        
        ClearLogListCommand clearLogListCommand = new ClearLogListCommand(controllerRepositoryMock, infoViewEntries, log4jAdapterMock);
        clearLogListCommand.execute();

        //Act
        clearLogListCommand.undo();
        
        //Assert
        assertFalse(infoViewEntries.isEmpty());
        assertEquals("First_Entry", infoViewEntries.get(0));
        Mockito.verify(mainToolBarViewControllerMock, times(2)).toggleUndoRedoButtons();
    }

    @Test
    public void T06_Redo_Called_Clears_LogListEntries() {
        //Arrange
        MainToolBarViewController mainToolBarViewControllerMock = Mockito.mock(MainToolBarViewController.class);
        MainMenuBarViewController mainMenuBarViewControllerMock = Mockito.mock(MainMenuBarViewController.class);
        MainInfoViewController mainInfoViewControllerMock = Mockito.mock(MainInfoViewController.class);
        Log4jAdapter log4jAdapterMock = Mockito.mock(Log4jAdapter.class);
        
        ControllerRepository controllerRepositoryMock = Mockito.mock(ControllerRepository.class);
        when(controllerRepositoryMock.get(MainToolBarViewController.class.getName())).thenReturn(mainToolBarViewControllerMock);
        when(controllerRepositoryMock.get(MainInfoViewController.class.getName())).thenReturn(mainInfoViewControllerMock);
        when(controllerRepositoryMock.get(MainMenuBarViewController.class.getName())).thenReturn(mainMenuBarViewControllerMock);
        
        ObservableList<String> infoViewEntries = FXCollections.observableArrayList();
        infoViewEntries.add("First_Entry");
        
        ClearLogListCommand clearLogListCommand = new ClearLogListCommand(controllerRepositoryMock, infoViewEntries, log4jAdapterMock);
        clearLogListCommand.execute();
        clearLogListCommand.undo();

        //Act
        clearLogListCommand.redo();
        
        //Assert
        assertTrue(infoViewEntries.isEmpty());
        Mockito.verify(mainToolBarViewControllerMock, times(3)).toggleUndoRedoButtons();
    }
    
}
