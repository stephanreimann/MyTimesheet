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
import org.apache.logging.log4j.*;
import sqlite.UserDAO;
import utils.EventManager;

/**
 *
 * @author stephan
 */
public class DeleteUserCommand implements ICommand {

    private final String deleteUserEvent = "DeleteUser";
    
    private final MainToolBarViewController mainToolBarViewController;
    private final MainMenuBarViewController mainMenuBarViewController;
    private final EventManager events;    
    private TableView<User> userTableView;
    private User selectedUser;
    private UserDAO userDao;
    private final Logger log = LogManager.getLogger(DeleteUserCommand.class.getName());
    
    public DeleteUserCommand(ControllerRepository controllerRepository, EventManager events, TableView<User> userTableView, User selectedUser, UserDAO userDao) {
        if(controllerRepository == null) throw new NullPointerException("controllerRepository");
        if(events == null) throw new NullPointerException("events");
        if(userTableView == null) throw new NullPointerException("userTableView");
        if(selectedUser == null) throw new NullPointerException("selectedUser");
        if(userDao == null) throw new NullPointerException("userDao");

        this.mainToolBarViewController = (MainToolBarViewController) controllerRepository.get(MainToolBarViewController.class.getName());
        this.mainMenuBarViewController = (MainMenuBarViewController) controllerRepository.get(MainMenuBarViewController.class.getName());
        
        this.events = events;
        this.userTableView = userTableView;
        this.selectedUser = selectedUser;
        this.userDao = userDao;
    }
    
    @Override
    public boolean execute() {
        userTableView.getItems().remove(selectedUser);
        try {
            if(!userDao.delete(selectedUser)) {
                log.error("Deletion of user failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                events.notifyListenerOfEvent(deleteUserEvent, this);
                return true;
            }
        } catch (SQLException ex) {
            log.fatal(ex);
            return false;
        }
    }

    @Override
    public boolean undo() {
        userTableView.getItems().add(selectedUser);
        try {
            if(!userDao.create(selectedUser)) {
                log.error("Undo deletion of user failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                userTableView.getSelectionModel().select(selectedUser);
                events.notifyListenerOfEvent(deleteUserEvent, this);
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
        return "Delete User[" + selectedUser.toString() + "]";        
    }
    
}
