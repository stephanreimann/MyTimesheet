/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package commands.user;

import commands.ICommand;
import controller.*;
import java.sql.SQLException;
import javafx.scene.control.TableView;
import model.User;
import org.apache.logging.log4j.LogManager;
import sqlite.UserDAO;
import utils.EventManager;

/**
 *
 * @author stephan
 */
public class EditUserCommand implements ICommand {

    private final String editUserEvent = "EditUser";
    
    private final MainToolBarViewController mainToolBarViewController;
    private final MainMenuBarViewController mainMenuBarViewController;
    private final EventManager events;
    private TableView<User> userTableView;
    private final User originalUser;
    private final User modifiedUser;
    private final UserDAO userDao;
    private final org.apache.logging.log4j.Logger log = LogManager.getLogger(EditUserCommand.class.getName());
    
    public EditUserCommand(ControllerRepository controllerRepository, EventManager events, TableView<User> userTableView, User originalUser, User modifiedUser, UserDAO userDao) {
        if(controllerRepository == null) throw new NullPointerException("controllerRepository");
        if(events == null) throw new NullPointerException("events");
        if(userTableView == null) throw new NullPointerException("userTableView");
        if(originalUser == null) throw new NullPointerException("originalUser");
        if(modifiedUser == null) throw new NullPointerException("modifiedUser");
        if(userDao == null) throw new NullPointerException("userDao");
        
        this.mainToolBarViewController = (MainToolBarViewController) controllerRepository.get(MainToolBarViewController.class.getName());
        this.mainMenuBarViewController = (MainMenuBarViewController) controllerRepository.get(MainMenuBarViewController.class.getName());
        
        this.events = events;
        this.userTableView = userTableView;
        this.originalUser = originalUser;
        this.modifiedUser = modifiedUser;
        this.userDao = userDao;                
    }
    
    @Override
    public boolean execute() {
        try {
            userTableView.getItems().remove(modifiedUser);
            userTableView.getItems().add(modifiedUser);
            if(!userDao.update(originalUser, modifiedUser)) {
                log.error("Editing user failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                userTableView.getSelectionModel().select(modifiedUser);
                events.notifyListenerOfEvent(editUserEvent, this);
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
            userTableView.getItems().remove(modifiedUser);
            userTableView.getItems().add(originalUser);
            if(!userDao.update(modifiedUser, originalUser)) {
                log.error("Undo editing user failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                userTableView.getSelectionModel().select(originalUser);
                events.notifyListenerOfEvent(editUserEvent, this);
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
            userTableView.getItems().remove(originalUser);
            userTableView.getItems().add(modifiedUser);
            if(!userDao.update(originalUser, modifiedUser)) {
                log.error("Undo editing user failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                userTableView.getSelectionModel().select(modifiedUser);
                events.notifyListenerOfEvent(editUserEvent, this);
                return true;
            }
        } catch (SQLException ex) {
            log.fatal(ex);
            return false;
        }
    }

    @Override
    public String getText() {
        return "Change User from [" + originalUser.toString() +"] to [" + modifiedUser.toString() + "]";
    }
    
}
