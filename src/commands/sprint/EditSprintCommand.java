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
public class EditSprintCommand implements ICommand {

    private final String editSprintEvent = "EditSprint";
    
    private final MainToolBarViewController mainToolBarViewController;
    private final MainMenuBarViewController mainMenuBarViewController;
    private final EventManager events;
    private TableView<Sprint> sprintTableView;
    private final Sprint originalSprint;
    private final Sprint modifiedSprint;
    private final SprintDAO sprintDao;
    private final Logger log = LogManager.getLogger(EditSprintCommand.class.getName());
    
    public EditSprintCommand(ControllerRepository controllerRepository, EventManager events, TableView<Sprint> sprintTableView, Sprint originalSprint, Sprint modifiedSprint, SprintDAO sprintDao) {
        if(controllerRepository == null) throw new NullPointerException("controllerRepository");
        if(events == null) throw new NullPointerException("events");
        if(sprintTableView == null) throw new NullPointerException("sprintTableView");
        if(originalSprint == null) throw new NullPointerException("originalSprint");
        if(modifiedSprint == null) throw new NullPointerException("modifiedSprint");
        if(sprintDao == null) throw new NullPointerException("sprintDao");
        
        this.mainToolBarViewController = (MainToolBarViewController) controllerRepository.get(MainToolBarViewController.class.getName());
        this.mainMenuBarViewController = (MainMenuBarViewController) controllerRepository.get(MainMenuBarViewController.class.getName());
        
        this.events = events;        
        this.sprintTableView = sprintTableView;
        this.originalSprint = originalSprint;
        this.modifiedSprint = modifiedSprint;
        this.sprintDao = sprintDao;                
    }
    
    @Override
    public boolean execute() {
        try {
            sprintTableView.getItems().remove(modifiedSprint);
            sprintTableView.getItems().add(modifiedSprint);
            if(!sprintDao.update(originalSprint, modifiedSprint)) {
                log.error("Editing sprint failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                sprintTableView.getSelectionModel().select(modifiedSprint);
                events.notifyListenerOfEvent(editSprintEvent, this);                
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
            sprintTableView.getItems().remove(modifiedSprint);
            sprintTableView.getItems().add(originalSprint);
            if(!sprintDao.update(modifiedSprint, originalSprint)) {
                log.error("Undo editing sprint failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                sprintTableView.getSelectionModel().select(originalSprint);
                events.notifyListenerOfEvent(editSprintEvent, this);                
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
            sprintTableView.getItems().remove(originalSprint);
            sprintTableView.getItems().add(modifiedSprint);
            if(!sprintDao.update(originalSprint, modifiedSprint)) {
                log.error("Undo editing sprint failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                sprintTableView.getSelectionModel().select(modifiedSprint);
                events.notifyListenerOfEvent(editSprintEvent, this);                
                return true;
            }
        } catch (SQLException ex) {
            log.fatal(ex);
            return false;
        }
    }

    @Override
    public String getText() {
        return "Change Sprint from [" + originalSprint.toString() +"] to [" + modifiedSprint.toString() + "]";
    }
    
}
