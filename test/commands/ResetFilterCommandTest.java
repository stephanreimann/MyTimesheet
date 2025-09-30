/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commands;

import adapter.Log4jAdapter;
import controller.*;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;

/**
 *
 * @author adrest18
 */
public class ResetFilterCommandTest {
    
    @Test(expected=NullPointerException.class)
    public void T00_Ctor_Called_ControllerRepository_IsNull_Throws_NullPointerException() {
        //Arrange
        Log4jAdapter log4jAdapter = Mockito.mock(Log4jAdapter.class);

        //Act
        //Assert
        ResetFilterCommand resetFilterCommand = new ResetFilterCommand(null, log4jAdapter);
    }

    @Test(expected=NullPointerException.class)
    public void T01_Ctor_Called_Log4jAdapter_IsNull_Throws_NullPointerException() {
        //Arrange
        ControllerRepository controllerRepositoryMock = Mockito.mock(ControllerRepository.class);

        //Act
        //Assert
        ResetFilterCommand resetFilterCommand = new ResetFilterCommand(controllerRepositoryMock, null);
    }

    @Test
    public void T02_Ctor_Called_Returns_Instance() {
        //Arrange
        MainToolBarViewController mainToolBarViewControllerMock = Mockito.mock(MainToolBarViewController.class);
        MainMenuBarViewController mainMenuBarViewControllerMock = Mockito.mock(MainMenuBarViewController.class);
        MainInfoViewController mainInfoViewControllerMock = Mockito.mock(MainInfoViewController.class);

        ControllerRepository controllerRepositoryMock = Mockito.mock(ControllerRepository.class);
        when(controllerRepositoryMock.get(MainToolBarViewController.class.getName())).thenReturn(mainToolBarViewControllerMock);
        when(controllerRepositoryMock.get(MainInfoViewController.class.getName())).thenReturn(mainInfoViewControllerMock);
        when(controllerRepositoryMock.get(MainMenuBarViewController.class.getName())).thenReturn(mainMenuBarViewControllerMock);

        Log4jAdapter log4jAdapter = Mockito.mock(Log4jAdapter.class);

        //Act
        ResetFilterCommand resetFilterCommand = new ResetFilterCommand(controllerRepositoryMock, log4jAdapter);
        
        //Assert
        assertNotNull(resetFilterCommand);
    }
    
    @Test
    public void T02_Execute_Called_Resets_Filter_Settings() {
        //Arrange
        MainToolBarViewController mainToolBarViewControllerMock = Mockito.mock(MainToolBarViewController.class);
        MainMenuBarViewController mainMenuBarViewControllerMock = Mockito.mock(MainMenuBarViewController.class);
        MainInfoViewController mainInfoViewControllerMock = Mockito.mock(MainInfoViewController.class);

        ControllerRepository controllerRepositoryMock = Mockito.mock(ControllerRepository.class);
        when(controllerRepositoryMock.get(MainToolBarViewController.class.getName())).thenReturn(mainToolBarViewControllerMock);
        when(controllerRepositoryMock.get(MainInfoViewController.class.getName())).thenReturn(mainInfoViewControllerMock);
        when(controllerRepositoryMock.get(MainMenuBarViewController.class.getName())).thenReturn(mainMenuBarViewControllerMock);
        
        Log4jAdapter log4jAdapter = Mockito.mock(Log4jAdapter.class);

        ResetFilterCommand resetFilterCommand = new ResetFilterCommand(controllerRepositoryMock, log4jAdapter);

        //Act
        resetFilterCommand.execute();
        
        //Assert
        Mockito.verify(mainInfoViewControllerMock).setInfoToggleButtonState(Boolean.FALSE);
        Mockito.verify(mainInfoViewControllerMock).setDebugToggleButtonState(Boolean.FALSE);
        Mockito.verify(mainInfoViewControllerMock).setWarningToggleButtonState(Boolean.FALSE);
        Mockito.verify(mainInfoViewControllerMock).setErrorToggleButtonState(Boolean.FALSE);
        Mockito.verify(mainInfoViewControllerMock).setFatalToggleButtonState(Boolean.FALSE);
        Mockito.verify(mainToolBarViewControllerMock).toggleUndoRedoButtons();
    }
    
    @Test
    public void T03_Undo_Called_Reverts_Old_Filter_Settings() {
        MainToolBarViewController mainToolBarViewControllerMock = Mockito.mock(MainToolBarViewController.class);
        MainMenuBarViewController mainMenuBarViewControllerMock = Mockito.mock(MainMenuBarViewController.class);
        MainInfoViewController mainInfoViewControllerMock = Mockito.mock(MainInfoViewController.class);
        when(mainInfoViewControllerMock.getInfoToggleButtonState()).thenReturn(Boolean.FALSE);
        when(mainInfoViewControllerMock.getDebugToggleButtonState()).thenReturn(Boolean.TRUE);
        when(mainInfoViewControllerMock.getWarningToggleButtonState()).thenReturn(Boolean.FALSE);
        when(mainInfoViewControllerMock.getErrorToggleButtonState()).thenReturn(Boolean.TRUE);
        when(mainInfoViewControllerMock.getFatalToggleButtonState()).thenReturn(Boolean.FALSE);
        
        ControllerRepository controllerRepositoryMock = Mockito.mock(ControllerRepository.class);
        when(controllerRepositoryMock.get(MainToolBarViewController.class.getName())).thenReturn(mainToolBarViewControllerMock);
        when(controllerRepositoryMock.get(MainInfoViewController.class.getName())).thenReturn(mainInfoViewControllerMock);
        when(controllerRepositoryMock.get(MainMenuBarViewController.class.getName())).thenReturn(mainMenuBarViewControllerMock);
        
        Log4jAdapter log4jAdapter = Mockito.mock(Log4jAdapter.class);

        ResetFilterCommand resetFilterCommand = new ResetFilterCommand(controllerRepositoryMock, log4jAdapter);
        resetFilterCommand.execute();

        //Act
        resetFilterCommand.undo();
        
        //Assert
        Mockito.verify(mainInfoViewControllerMock).setInfoToggleButtonState(mainInfoViewControllerMock.getInfoToggleButtonState());
        Mockito.verify(mainInfoViewControllerMock).setDebugToggleButtonState(mainInfoViewControllerMock.getDebugToggleButtonState());
        Mockito.verify(mainInfoViewControllerMock).setWarningToggleButtonState(mainInfoViewControllerMock.getWarningToggleButtonState());
        Mockito.verify(mainInfoViewControllerMock).setErrorToggleButtonState(mainInfoViewControllerMock.getErrorToggleButtonState());
        Mockito.verify(mainInfoViewControllerMock).setFatalToggleButtonState(mainInfoViewControllerMock.getFatalToggleButtonState());
        Mockito.verify(mainToolBarViewControllerMock, times(2)).toggleUndoRedoButtons();
    }

    @Test
    public void T04_Redo_Called_Resets_Filter_Settings() {
        MainToolBarViewController mainToolBarViewControllerMock = Mockito.mock(MainToolBarViewController.class);
        MainMenuBarViewController mainMenuBarViewControllerMock = Mockito.mock(MainMenuBarViewController.class);
        MainInfoViewController mainInfoViewControllerMock = Mockito.mock(MainInfoViewController.class);
        when(mainInfoViewControllerMock.getInfoToggleButtonState()).thenReturn(Boolean.FALSE);
        when(mainInfoViewControllerMock.getDebugToggleButtonState()).thenReturn(Boolean.FALSE);
        when(mainInfoViewControllerMock.getWarningToggleButtonState()).thenReturn(Boolean.FALSE);
        when(mainInfoViewControllerMock.getErrorToggleButtonState()).thenReturn(Boolean.FALSE);
        when(mainInfoViewControllerMock.getFatalToggleButtonState()).thenReturn(Boolean.FALSE);

        ControllerRepository controllerRepositoryMock = Mockito.mock(ControllerRepository.class);
        when(controllerRepositoryMock.get(MainToolBarViewController.class.getName())).thenReturn(mainToolBarViewControllerMock);
        when(controllerRepositoryMock.get(MainInfoViewController.class.getName())).thenReturn(mainInfoViewControllerMock);
        when(controllerRepositoryMock.get(MainMenuBarViewController.class.getName())).thenReturn(mainMenuBarViewControllerMock);
        
        Log4jAdapter log4jAdapter = Mockito.mock(Log4jAdapter.class);

        ResetFilterCommand resetFilterCommand = new ResetFilterCommand(controllerRepositoryMock, log4jAdapter);
        resetFilterCommand.execute();
        resetFilterCommand.undo();

        //Act
        resetFilterCommand.redo();
        
        //Assert
        Mockito.verify(mainInfoViewControllerMock).setInfoToggleButtonState(mainInfoViewControllerMock.getInfoToggleButtonState());
        Mockito.verify(mainInfoViewControllerMock).setDebugToggleButtonState(mainInfoViewControllerMock.getDebugToggleButtonState());
        Mockito.verify(mainInfoViewControllerMock).setWarningToggleButtonState(mainInfoViewControllerMock.getWarningToggleButtonState());
        Mockito.verify(mainInfoViewControllerMock).setErrorToggleButtonState(mainInfoViewControllerMock.getErrorToggleButtonState());
        Mockito.verify(mainInfoViewControllerMock).setFatalToggleButtonState(mainInfoViewControllerMock.getFatalToggleButtonState());
        Mockito.verify(mainToolBarViewControllerMock, times(3)).toggleUndoRedoButtons();
    }
    
}
