/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package testhelper;

import controller.IViewController;
import controller.MainToolBarViewController;
import java.lang.reflect.Field;
import javafx.event.ActionEvent;

/**
 *
 * @author adrest18
 */
public class ReflectionHelper {
    
    public void setField(IViewController controller, String fieldName, Object value) throws Exception {
        Field field = MainToolBarViewController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(controller, value);
    }

    public void invokePrivateMethod(IViewController controller, String methodName, ActionEvent event) throws Exception {
        var method = MainToolBarViewController.class.getDeclaredMethod(methodName, ActionEvent.class);
        method.setAccessible(true);
        method.invoke(controller, event);
    }

}
