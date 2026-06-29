/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package commands.sprint;

import commands.ICommand;
import controller.*;
import java.sql.SQLException;
import javafx.scene.control.TableView;
import model.Sprint;
import org.apache.logging.log4j.*;
import sqlite.SprintDAO;
import utils.EventManager;

/**
 *
 * @author stephan
 */
public class NewSprintCommand implements ICommand {

    private final String newSprintEvent = "NewSprint";
    
    private final MainToolBarViewController mainToolBarViewController;
    private final MainMenuBarViewController mainMenuBarViewController;
    private final EventManager events;
    private TableView<Sprint> sprintTableView;
    private SprintDAO sprintDao;
    private final Sprint newSprint;
    private final Logger log = LogManager.getLogger(NewSprintCommand.class.getName());
    
    public NewSprintCommand(ControllerRepository controllerRepository, EventManager events, TableView<Sprint> sprintTableView, Sprint newSprint, SprintDAO sprintDao) {
        if(controllerRepository == null) throw new NullPointerException("controllerRepository");
        if(events == null) throw new NullPointerException("events");
        if(sprintTableView == null) throw new NullPointerException("sprintTableView");
        if(newSprint == null) throw new NullPointerException("newSprint");
        if(sprintDao == null) throw new NullPointerException("sprintDao");
        
        this.mainToolBarViewController = (MainToolBarViewController) controllerRepository.get(MainToolBarViewController.class.getName());
        this.mainMenuBarViewController = (MainMenuBarViewController) controllerRepository.get(MainMenuBarViewController.class.getName());
        
        this.events = events;        
        this.sprintTableView = sprintTableView;
        this.newSprint = newSprint;
        this.sprintDao = sprintDao;
    }
    
    @Override
    public boolean execute() {
        try {
            sprintTableView.getItems().add(newSprint);
            if(!sprintDao.create(newSprint)) {
                log.error("Adding sprint failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                sprintTableView.getSelectionModel().select(newSprint);
                events.notifyListenerOfEvent(newSprintEvent, this);                
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
            sprintTableView.getItems().remove(newSprint);
            if(!sprintDao.delete(newSprint)) {
                log.error("Undo adding of sprint failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                events.notifyListenerOfEvent(newSprintEvent, this);                
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
        return "New Sprint[" + newSprint.toString() + "]";
    }
    
}
