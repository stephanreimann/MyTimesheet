/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.util.*;

public class ControllerRepository {

    private final Map<String, IViewController> map;
    private static ControllerRepository instance;

    private ControllerRepository() {
        this.map = new HashMap<>();
    }

    public static ControllerRepository getInstance() {
        synchronized (ControllerRepository.class) {
            if (ControllerRepository.instance == null) {
                ControllerRepository.instance = new ControllerRepository();
            }
        }
        return ControllerRepository.instance;
    }

    public Map<String, IViewController> getAll() {
        return map;
    }

    public IViewController get(String key) {
        if (map.containsKey(key)) {
            return map.get(key);
        }
        return null;
    }

    public void put(String key, IViewController value) {
        map.put(key, value);
    }

    public void remove(String key) {
        if (map.containsKey(key)) {
            map.remove(key);
        }
    }

    public Boolean contains(String key) {
        return map.containsKey(key);
    }

}
