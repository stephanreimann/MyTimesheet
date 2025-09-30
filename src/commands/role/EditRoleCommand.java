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
public class EditRoleCommand implements ICommand {

    private final String editRoleEvent = "EditRole";
    
    private final MainToolBarViewController mainToolBarViewController;
    private final MainMenuBarViewController mainMenuBarViewController;
    private final EventManager events;
    private TableView<Role> roleTableView;
    private final Role originalRole;
    private final Role modifiedRole;
    private final RoleDAO roleDao;
    private final Logger log = LogManager.getLogger(EditRoleCommand.class.getName());
    
    public EditRoleCommand(ControllerRepository controllerRepository, EventManager events, TableView<Role> roleTableView, Role originalRole, Role modifiedRole, RoleDAO roleDao) {
        if(controllerRepository == null) throw new NullPointerException("controllerRepository");
        if(events == null) throw new NullPointerException("events");
        if(roleTableView == null) throw new NullPointerException("roleTableView");
        if(originalRole == null) throw new NullPointerException("originalRole");
        if(modifiedRole == null) throw new NullPointerException("modifiedRole");
        if(roleDao == null) throw new NullPointerException("roleDao");
        
        this.mainToolBarViewController = (MainToolBarViewController) controllerRepository.get(MainToolBarViewController.class.getName());
        this.mainMenuBarViewController = (MainMenuBarViewController) controllerRepository.get(MainMenuBarViewController.class.getName());
        
        this.events = events;        
        this.roleTableView = roleTableView;
        this.originalRole = originalRole;
        this.modifiedRole = modifiedRole;
        this.roleDao = roleDao;                
    }
    
    @Override
    public boolean execute() {
        try {
            roleTableView.getItems().remove(modifiedRole);
            roleTableView.getItems().add(modifiedRole);
            if(!roleDao.update(originalRole, modifiedRole)) {
                log.error("Editing role failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                roleTableView.getSelectionModel().select(modifiedRole);
                events.notifyListenerOfEvent(editRoleEvent, this);                
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
            roleTableView.getItems().remove(modifiedRole);
            roleTableView.getItems().add(originalRole);
            if(!roleDao.update(modifiedRole, originalRole)) {
                log.error("Undo editing role failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                roleTableView.getSelectionModel().select(originalRole);
                events.notifyListenerOfEvent(editRoleEvent, this);                
                return true;
            }
        } catch (SQLException ex) {
            log.fatal(ex);
            return false;
        }
    }

    @Override
    public boolean redo() {
        try {
            roleTableView.getItems().remove(originalRole);
            roleTableView.getItems().add(modifiedRole);
            if(!roleDao.update(originalRole, modifiedRole)) {
                log.error("Undo editing role failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                roleTableView.getSelectionModel().select(modifiedRole);
                events.notifyListenerOfEvent(editRoleEvent, this);                
                return true;
            }
        } catch (SQLException ex) {
            log.fatal(ex);
            return false;
        }
    }

    @Override
    public String getText() {
        return "Change Role from [" + originalRole.toString() +"] to [" + modifiedRole.toString() + "]";
    }
    
}
