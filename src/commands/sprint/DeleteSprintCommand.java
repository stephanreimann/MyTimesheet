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
public class DeleteSprintCommand implements ICommand {

    private final String deleteSprintEvent = "DeleteSprint";
    
    private final MainToolBarViewController mainToolBarViewController;
    private final MainMenuBarViewController mainMenuBarViewController;
    private final EventManager events;
    private TableView<Sprint> sprintTableView;
    private Sprint selectedSprint;
    private SprintDAO sprintDao;
    private final Logger log = LogManager.getLogger(DeleteSprintCommand.class.getName());
    
    public DeleteSprintCommand(ControllerRepository controllerRepository, EventManager events, TableView<Sprint> sprintTableView, Sprint selectedSprint, SprintDAO sprintDao) {
        if(controllerRepository == null) throw new NullPointerException("controllerRepository");
        if(events == null) throw new NullPointerException("events");
        if(sprintTableView == null) throw new NullPointerException("sprintTableView");
        if(selectedSprint == null) throw new NullPointerException("selectedSprint");
        if(sprintDao == null) throw new NullPointerException("sprintDao");

        this.mainToolBarViewController = (MainToolBarViewController) controllerRepository.get(MainToolBarViewController.class.getName());
        this.mainMenuBarViewController = (MainMenuBarViewController) controllerRepository.get(MainMenuBarViewController.class.getName());
        
        this.events = events;
        this.sprintTableView = sprintTableView;
        this.selectedSprint = selectedSprint;
        this.sprintDao = sprintDao;
    }
    
    @Override
    public boolean execute() {
        sprintTableView.getItems().remove(selectedSprint);
        try {
            if(!sprintDao.delete(selectedSprint)) {
                log.error("Deletion of sprint failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                events.notifyListenerOfEvent(deleteSprintEvent, this);                
                return true;
            }
        } catch (SQLException ex) {
            log.fatal(ex);
            return false;
        }
    }

    @Override
    public boolean undo() {
        sprintTableView.getItems().add(selectedSprint);
        try {
            if(!sprintDao.create(selectedSprint)) {
                log.error("Undo deletion of sprint failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                sprintTableView.getSelectionModel().select(selectedSprint);
                events.notifyListenerOfEvent(deleteSprintEvent, this);                
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
        return "Delete Sprint[" + selectedSprint.toString() + "]";        
    }
    
}
