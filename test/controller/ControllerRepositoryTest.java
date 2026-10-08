/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import org.junit.Before;
import org.junit.Test;
import java.util.Map;
import java.util.ResourceBundle;

import static org.junit.Assert.*;

public class ControllerRepositoryTest {

    private ControllerRepository repository;
    private IViewController dummyController;

    @Before
    public void setUp() {
        repository = ControllerRepository.getInstance();
        // Clear repository state before each test to ensure isolation
        repository.getAll().clear();

        // Create a simple anonymous stub implementation of IViewController for testing
        dummyController = new IViewController() {
            @Override
            public void updateGuiItems() {}
            @Override
            public ResourceBundle getResourceBundle() { return null; }
            @Override
            public void setResourceBundle(ResourceBundle rb) {}
            @Override
            public void setPrimaryStage(javafx.stage.Stage stage) {}
            @Override
            public void preCloseAction() {}
        };
    }

    @Test
    public void getInstance_ReturnsNonNullInstance() {
        ControllerRepository instance = ControllerRepository.getInstance();
        assertNotNull(instance);
    }

    @Test
    public void getInstance_ReturnsSameSingletonInstance() {
        ControllerRepository instance1 = ControllerRepository.getInstance();
        ControllerRepository instance2 = ControllerRepository.getInstance();
        assertSame(instance1, instance2);
    }

    @Test
    public void putAndGet_ValidKeyAndValue_StoresAndRetrieves() {
        String key = "TestController";
        repository.put(key, dummyController);

        IViewController retrieved = repository.get(key);
        assertNotNull(retrieved);
        assertEquals(dummyController, retrieved);
    }

    @Test
    public void get_NonExistentKey_ReturnsNull() {
        IViewController retrieved = repository.get("NonExistentKey");
        assertNull(retrieved);
    }

    @Test
    public void contains_ExistingKey_ReturnsTrue() {
        String key = "ExistingController";
        repository.put(key, dummyController);

        assertTrue(repository.contains(key));
    }

    @Test
    public void contains_NonExistentKey_ReturnsFalse() {
        assertFalse(repository.contains("MissingController"));
    }

    @Test
    public void remove_ExistingKey_RemovesController() {
        String key = "ControllerToRemove";
        repository.put(key, dummyController);
        assertTrue(repository.contains(key));

        repository.remove(key);
        assertFalse(repository.contains(key));
        assertNull(repository.get(key));
    }

    @Test
    public void getAll_ReturnsUnderlyingMap() {
        String key1 = "Controller1";
        String key2 = "Controller2";
        repository.put(key1, dummyController);
        repository.put(key2, dummyController);

        Map<String, IViewController> allControllers = repository.getAll();
        assertNotNull(allControllers);
        assertEquals(2, allControllers.size());
        assertTrue(allControllers.containsKey(key1));
        assertTrue(allControllers.containsKey(key2));
    }
}