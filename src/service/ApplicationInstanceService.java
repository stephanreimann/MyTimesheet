/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.io.*;

/**
 * This class gives information if a instance of the application is 
 * already running.
 * @author adrest18
 */
public class ApplicationInstanceService {
    
    private String instanceLockName;
    
    public ApplicationInstanceService(String instanceLockName) {
        if(instanceLockName == null) throw new NullPointerException("instanceLockName");
        if(instanceLockName.trim().isEmpty()) throw new IllegalArgumentException("trimmed instanceLockName is empty");
        
        this.instanceLockName = instanceLockName;
    }
    
    public boolean isRunning() {
        try {
            return createInstanceLockIfNotExist();
        } catch (IOException e) {
            return true;
        }
    }
    
    public void forceRemoveOfInstanceLock() throws Exception {
        final File file = new File(instanceLockName);
        if(file.exists()) {
            file.deleteOnExit();
            if(file.exists()) {
                throw new Exception("InstanceLock "+ instanceLockName + " could not be removed, please try to remove it manually!");
            }
        }
    }
    
    private boolean createInstanceLockIfNotExist() throws IOException {
        final File file = new File(instanceLockName);
        if (file.createNewFile())
        {
            file.deleteOnExit();
            return false;
        }
        return true;
    }
    
}
