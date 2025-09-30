/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package commands.worklocation;

import commands.ICommand;
import controller.*;
import java.sql.SQLException;
import javafx.scene.control.TableView;
import model.Worklocation;
import org.apache.logging.log4j.*;
import sqlite.WorklocationDAO;
import utils.EventManager;

/**
 *
 * @author stephan
 */
public class EditWorklocationCommand implements ICommand {

    private final String editWorkLocationEvent = "EditWorkLocation";
    
    private final MainToolBarViewController mainToolBarViewController;
    private final MainMenuBarViewController mainMenuBarViewController;
    private final EventManager events;
    private TableView<Worklocation> worklocationTableView;
    private final Worklocation originalWorklocation;
    private final Worklocation modifiedWorklocation;
    private final WorklocationDAO worklocationDao;
    private final Logger log = LogManager.getLogger(EditWorklocationCommand.class.getName());
    
    public EditWorklocationCommand(ControllerRepository controllerRepository, EventManager events, TableView<Worklocation> worklocationTableView, Worklocation originalWorklocation, Worklocation modifiedWorklocation, WorklocationDAO worklocationDao) {
        if(controllerRepository == null) throw new NullPointerException("controllerRepository");
        if(events == null) throw new NullPointerException("events");
        if(worklocationTableView == null) throw new NullPointerException("worklocationTableView");
        if(originalWorklocation == null) throw new NullPointerException("originalWorklocation");
        if(modifiedWorklocation == null) throw new NullPointerException("modifiedWorklocation");
        if(worklocationDao == null) throw new NullPointerException("worklocationDao");
        
        this.mainToolBarViewController = (MainToolBarViewController) controllerRepository.get(MainToolBarViewController.class.getName());
        this.mainMenuBarViewController = (MainMenuBarViewController) controllerRepository.get(MainMenuBarViewController.class.getName());
        
        this.events = events;
        this.worklocationTableView = worklocationTableView;
        this.originalWorklocation = originalWorklocation;
        this.modifiedWorklocation = modifiedWorklocation;
        this.worklocationDao = worklocationDao;
    }
    
    @Override
    public boolean execute() {
        try {
            worklocationTableView.getItems().remove(modifiedWorklocation);
            worklocationTableView.getItems().add(modifiedWorklocation);
            if(!worklocationDao.update(originalWorklocation, modifiedWorklocation)) {
                log.error("Editing worklocation failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                worklocationTableView.getSelectionModel().select(modifiedWorklocation);
                events.notifyListenerOfEvent(editWorkLocationEvent, this);                
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
            worklocationTableView.getItems().remove(modifiedWorklocation);
            worklocationTableView.getItems().add(originalWorklocation);
            if(!worklocationDao.update(modifiedWorklocation, originalWorklocation)) {
                log.error("Undo worklocation role failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                worklocationTableView.getSelectionModel().select(originalWorklocation);
                events.notifyListenerOfEvent(editWorkLocationEvent, this);                
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
            worklocationTableView.getItems().remove(originalWorklocation);
            worklocationTableView.getItems().add(modifiedWorklocation);
            if(!worklocationDao.update(originalWorklocation, modifiedWorklocation)) {
                log.error("Undo editing worklocation failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                worklocationTableView.getSelectionModel().select(modifiedWorklocation);
                events.notifyListenerOfEvent(editWorkLocationEvent, this);                
                return true;
            }
        } catch (SQLException ex) {
            log.fatal(ex);
            return false;
        }
    }

    @Override
    public String getText() {
        return "Change Worklocation from [" + originalWorklocation.toString() +"] to [" + modifiedWorklocation.toString() + "]";
    }
    
}
