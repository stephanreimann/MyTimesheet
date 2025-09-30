/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.util.Map;
import static org.junit.Assert.*;
import org.junit.Test;
import org.mockito.Mockito;

/**
 *
 * @author adrest18
 */
public class ControllerRepositoryTest {

    @Test    
    public void T00_GetInstance_Called_Once_Returns_TheControllerRepositoryInstance() {
        //Arrange
        //Act
        ControllerRepository controllerRepository = ControllerRepository.getInstance();
        
        //Assert
        assertNotNull(controllerRepository);
    }
    
    @Test    
    public void T01_GetInstance_Called_Twice_Returns_TheSameControllerRepositoryInstance() {
        //Arrange
        ControllerRepository controllerRepositoryFirstCall = ControllerRepository.getInstance();

        //Act
        ControllerRepository controllerRepositorySecondCall = ControllerRepository.getInstance();
        
        //Assert
        assertSame(controllerRepositoryFirstCall, controllerRepositorySecondCall);
    }
    
    @Test    
    public void T02_GetAll_Called_Returns_AllRegisteredViewController() {
        //Arrange
        String expectedViewController_1 = "TestViewController_1";
        String expectedViewController_2 = "TestViewController_2";
        
        IViewController viewControllerMock = Mockito.mock(IViewController.class);
        
        ControllerRepository controllerRepository = ControllerRepository.getInstance();
        controllerRepository.put(expectedViewController_1, viewControllerMock);
        controllerRepository.put(expectedViewController_2, viewControllerMock);
        
        //Act
        Map<String, IViewController> mapOfViewController = controllerRepository.getAll();
        
        //Assert
        assertEquals(2, mapOfViewController.size());
        assertTrue(mapOfViewController.containsKey(expectedViewController_1));
        assertTrue(mapOfViewController.containsKey(expectedViewController_2));
        
        controllerRepository.remove(expectedViewController_1);
        controllerRepository.remove(expectedViewController_2);
    }

    @Test    
    public void T03_GetAll_Called_NoRegisteredViewController_Returns_EmptyMay() {
        //Arrange
        ControllerRepository controllerRepository = ControllerRepository.getInstance();
        
        //Act
        Map<String, IViewController> mapOfViewController = controllerRepository.getAll();
        
        //Assert
        assertTrue(mapOfViewController.isEmpty());
    }

    @Test    
    public void T04_Get_Called_Returns_TheRequestedViewController() {
        //Arrange
        String expectedViewController = "TestViewController";

        IViewController viewControllerMock = Mockito.mock(IViewController.class);
        
        ControllerRepository controllerRepository = ControllerRepository.getInstance();
        controllerRepository.put(expectedViewController, viewControllerMock);
        
        //Act
        IViewController viewController = controllerRepository.get(expectedViewController);
        
        //Assert
        assertNotNull(viewController);

        controllerRepository.remove(expectedViewController);
    }

    @Test    
    public void T05_Get_Called_Returns_Null_If_TheRequestedViewController_IsNotRegistered() {
        //Arrange
        String expectedViewController = "TestViewController";
        String anotherViewController = "AnotherViewCOntroller";
        IViewController viewControllerMock = Mockito.mock(IViewController.class);
        
        ControllerRepository controllerRepository = ControllerRepository.getInstance();
        controllerRepository.put(anotherViewController, viewControllerMock);
        
        //Act
        IViewController viewController = controllerRepository.get(expectedViewController);
        
        //Assert
        assertNull(viewController);

        controllerRepository.remove(anotherViewController);
    }

    @Test    
    public void T05_Put_Called_Registeres_TheNamedViewController() {
        //Arrange
        String expectedViewController = "TestViewController";
        IViewController viewControllerMock = Mockito.mock(IViewController.class);

        ControllerRepository controllerRepository = ControllerRepository.getInstance();

        //Act
        controllerRepository.put(expectedViewController, viewControllerMock);
        
        //Assert
        assertTrue(controllerRepository.contains(expectedViewController));
        
        controllerRepository.remove(expectedViewController);
    }

    @Test    
    public void T06_Remove_Called_Removes_TheRegistererdViewController() {
        //Arrange
        String expectedViewController = "TestViewController";
        IViewController viewControllerMock = Mockito.mock(IViewController.class);

        ControllerRepository controllerRepository = ControllerRepository.getInstance();
        controllerRepository.put(expectedViewController, viewControllerMock);

        //Act
        controllerRepository.remove(expectedViewController);
        
        //Assert
        assertTrue(controllerRepository.getAll().isEmpty());
    }

    @Test    
    public void T07_Remove_Called_OnNotRegisteredViewController_DoesNothing() {
        //Arrange
        String registeredViewController = "RegisteredViewController";
        String notRegisteredViewController = "NotRegisteredViewController";
        IViewController viewControllerMock = Mockito.mock(IViewController.class);

        ControllerRepository controllerRepository = ControllerRepository.getInstance();
        controllerRepository.put(registeredViewController, viewControllerMock);

        //Act
        controllerRepository.remove(notRegisteredViewController);
        
        //Assert
        assertEquals(1, controllerRepository.getAll().size());

        controllerRepository.remove(registeredViewController);
    }

    @Test    
    public void T08_Contains_Called_Return_True_If_TheViewController_IsRegistered() {
        //Arrange
        String expectedViewController = "TestViewController";
        IViewController viewControllerMock = Mockito.mock(IViewController.class);

        ControllerRepository controllerRepository = ControllerRepository.getInstance();
        controllerRepository.put(expectedViewController, viewControllerMock);

        //Act
        //Assert
        assertTrue(controllerRepository.contains(expectedViewController));
        
        controllerRepository.remove(expectedViewController);
    }
    
    @Test    
    public void T09_Contains_Called_Return_False_If_TheNamedViewController_IsNotRegistered() {
        //Arrange
        String expectedViewController = "ExpectedViewController";
        String registeredViewController = "RegisteredViewController";
        IViewController viewControllerMock = Mockito.mock(IViewController.class);

        ControllerRepository controllerRepository = ControllerRepository.getInstance();
        controllerRepository.put(registeredViewController, viewControllerMock);

        //Act
        //Assert
        assertFalse(controllerRepository.contains(expectedViewController));
        
        controllerRepository.remove(registeredViewController);
    }
}
