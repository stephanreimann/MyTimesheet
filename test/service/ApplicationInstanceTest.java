/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.io.File;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author adrest18
 */
public class ApplicationInstanceTest {

    private final String instanceLockName = "instanceLock";

    @After
    public void tearDown() {
        File lockFile = new File(instanceLockName);
        lockFile.delete();
    }
    
    @Test(expected=NullPointerException.class)
    public void T00_Ctor_Called_InstanceLockName_IsNull_Throws_NullPointerException() {
        //Arrange
        //Act
        //Assert
        ApplicationInstance instance = new ApplicationInstance(null);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void T01_Ctor_Called_InstanceLockName_IsEmpty_Throws_IllegalArgumentException() {
        //Arrange
        //Act
        //Assert
        ApplicationInstance instance = new ApplicationInstance("");
    }

    @Test(expected=IllegalArgumentException.class)
    public void T02_Ctor_Called_InstanceLockName_HasWhitespaces_Throws_IllegalArgumentException() {
        //Arrange
        //Act
        //Assert
        ApplicationInstance instance = new ApplicationInstance("   ");
    }

    @Test
    public void T03_IsRunning_Called_Application_Running_Returns_True() {
        //Arrange
        ApplicationInstance instance = new ApplicationInstance(instanceLockName);
        instance.isRunning();
        
        //Act
        Boolean isRunning = instance.isRunning();
        
        //Assert
        assertTrue(isRunning);
    }
    
    @Test
    public void T04_IsRunning_Called_Application_IsNotRunning_Returns_False() {
        //Arrange
        ApplicationInstance instance = new ApplicationInstance(instanceLockName);
        
        //Act
        Boolean isRunning = instance.isRunning();
        
        //Assert
        assertFalse(isRunning);
    }
}
