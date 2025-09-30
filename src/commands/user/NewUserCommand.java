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
public class NewUserCommand implements ICommand {

    private final String newUserEvent = "NewUser";

    private final MainToolBarViewController mainToolBarViewController;
    private final MainMenuBarViewController mainMenuBarViewController;
    private final EventManager events;
    private TableView<User> userTableView;
    private UserDAO userDao;
    private final User newUser;
    private final Logger log = LogManager.getLogger(NewUserCommand.class.getName());
    
    public NewUserCommand(ControllerRepository controllerRepository, EventManager events, TableView<User> userTableView, User newUser, UserDAO userDao) {
        if(controllerRepository == null) throw new NullPointerException("controllerRepository");
        if(events == null) throw new NullPointerException("events");
        if(userTableView == null) throw new NullPointerException("userTableView");
        if(newUser == null) throw new NullPointerException("newUser");
        if(userDao == null) throw new NullPointerException("userDao");
        
        this.mainToolBarViewController = (MainToolBarViewController) controllerRepository.get(MainToolBarViewController.class.getName());
        this.mainMenuBarViewController = (MainMenuBarViewController) controllerRepository.get(MainMenuBarViewController.class.getName());
        
        this.events = events;
        this.userTableView = userTableView;
        this.newUser = newUser;
        this.userDao = userDao;
    }
    
    @Override
    public boolean execute() {
        try {
            userTableView.getItems().add(newUser);
            if(!userDao.create(newUser)) {
                log.error("Adding user failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                userTableView.getSelectionModel().select(newUser);
                events.notifyListenerOfEvent(newUserEvent, this);
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
            userTableView.getItems().remove(newUser);
            if(!userDao.delete(newUser)) {
                log.error("Undo adding of user failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                events.notifyListenerOfEvent(newUserEvent, this);
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
        return "New User[" + newUser.toString() + "]";
    }
    
}
