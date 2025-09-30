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
public class DeleteRoleCommand implements ICommand {

    private final String deleteRoleEvent = "DeleteRole";
    
    private final MainToolBarViewController mainToolBarViewController;
    private final MainMenuBarViewController mainMenuBarViewController;
    private final EventManager events;
    private TableView<Role> roleTableView;
    private Role selectedRole;
    private RoleDAO roleDao;
    private final Logger log = LogManager.getLogger(DeleteRoleCommand.class.getName());
    
    public DeleteRoleCommand(ControllerRepository controllerRepository, EventManager events, TableView<Role> roleTableView, Role selectedRole, RoleDAO roleDao) {
        if(controllerRepository == null) throw new NullPointerException("controllerRepository");
        if(events == null) throw new NullPointerException("events");
        if(roleTableView == null) throw new NullPointerException("roleTableView");
        if(selectedRole == null) throw new NullPointerException("selectedRole");
        if(roleDao == null) throw new NullPointerException("roleDao");

        this.mainToolBarViewController = (MainToolBarViewController) controllerRepository.get(MainToolBarViewController.class.getName());
        this.mainMenuBarViewController = (MainMenuBarViewController) controllerRepository.get(MainMenuBarViewController.class.getName());
        
        this.events = events;
        this.roleTableView = roleTableView;
        this.selectedRole = selectedRole;
        this.roleDao = roleDao;
    }
    
    @Override
    public boolean execute() {
        roleTableView.getItems().remove(selectedRole);
        try {
            if(!roleDao.delete(selectedRole)) {
                log.error("Deletion of role failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                events.notifyListenerOfEvent(deleteRoleEvent, this);                
                return true;
            }
        } catch (SQLException ex) {
            log.fatal(ex);
            return false;
        }
    }

    @Override
    public boolean undo() {
        roleTableView.getItems().add(selectedRole);
        try {
            if(!roleDao.create(selectedRole)) {
                log.error("Undo deletion of role failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                roleTableView.getSelectionModel().select(selectedRole);
                events.notifyListenerOfEvent(deleteRoleEvent, this);                
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
        return "Delete Role[" + selectedRole.toString() + "]";        
    }
    
}
