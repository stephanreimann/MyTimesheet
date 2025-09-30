/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import controller.*;
import java.util.*;

/**
 *
 * @author ADREST18
 */
public class LanguageService {

    public void updateGuiItems() {
        ControllerRepository.getInstance().getAll().entrySet().stream().map(
                (Map.Entry<String, IViewController> pair)
                -> (IViewController) pair.getValue()).forEach((IViewController controller) -> {
                    controller.updateGuiItems();
                });
    }

    public void setResourceBundle(ResourceBundle rb) {
        ControllerRepository.getInstance().getAll().entrySet().stream().map(
                (Map.Entry<String, IViewController> pair)
                -> (IViewController) pair.getValue()).forEach((IViewController controller) -> {
                    controller.setResourceBundle(rb);
                });
    }
    
}
