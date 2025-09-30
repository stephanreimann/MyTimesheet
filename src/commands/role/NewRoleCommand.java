/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package commands.role;

import commands.ICommand;
import controller.*;
import java.sql.SQLException;
import javafx.scene.control.TableView;
import model.Role;
import org.apache.logging.log4j.*;
import sqlite.RoleDAO;
import utils.EventManager;

/**
 *
 * @author stephan
 */
public class NewRoleCommand implements ICommand {

    private final String newRoleEvent = "NewRole";
    
    private final MainToolBarViewController mainToolBarViewController;
    private final MainMenuBarViewController mainMenuBarViewController;
    private final EventManager events;
    private TableView<Role> roleTableView;
    private RoleDAO roleDao;
    private final Role newRole;
    private final Logger log = LogManager.getLogger(NewRoleCommand.class.getName());
    
    public NewRoleCommand(ControllerRepository controllerRepository, EventManager events, TableView<Role> roleTableView, Role newRole, RoleDAO roleDao) {
        if(controllerRepository == null) throw new NullPointerException("controllerRepository");
        if(events == null) throw new NullPointerException("events");
        if(roleTableView == null) throw new NullPointerException("roleTableView");
        if(newRole == null) throw new NullPointerException("newRole");
        if(roleDao == null) throw new NullPointerException("roleDao");
        
        this.mainToolBarViewController = (MainToolBarViewController) controllerRepository.get(MainToolBarViewController.class.getName());
        this.mainMenuBarViewController = (MainMenuBarViewController) controllerRepository.get(MainMenuBarViewController.class.getName());
        
        this.events = events;        
        this.roleTableView = roleTableView;
        this.newRole = newRole;
        this.roleDao = roleDao;
    }
    
    @Override
    public boolean execute() {
        try {
            roleTableView.getItems().add(newRole);
            if(!roleDao.create(newRole)) {
                log.error("Adding role failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                roleTableView.getSelectionModel().select(newRole);
                events.notifyListenerOfEvent(newRoleEvent, this);                
                return true;
            }
        } catch (SQLException ex) {
            log.fatal(ex);
            return false;
        }
    }

    @Override
    public boolean undo() {
        try {
            roleTableView.getItems().remove(newRole);
            if(!roleDao.delete(newRole)) {
                log.error("Undo adding of role failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                events.notifyListenerOfEvent(newRoleEvent, this);                
                return true;
            }
        } catch (SQLException ex) {
            log.fatal(ex);
            return false;
        }
    }

    @Override
    public boolean redo() {
        return execute();
    }

    @Override
    public String getText() {
        return "New Role[" + newRole.toString() + "]";
    }
    
}
