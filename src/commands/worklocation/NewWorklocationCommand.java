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
public class NewWorklocationCommand implements ICommand {

    private final String newWorkLocationEvent = "NewWorkLocation";
    
    private final MainToolBarViewController mainToolBarViewController;
    private final MainMenuBarViewController mainMenuBarViewController;
    private final EventManager events;
    private TableView<Worklocation> worklocationTableView;
    private WorklocationDAO worklocationDao;
    private final Worklocation newWorklocation;
    private final Logger log = LogManager.getLogger(NewWorklocationCommand.class.getName());
    
    public NewWorklocationCommand(ControllerRepository controllerRepository, EventManager events, TableView<Worklocation> worklocationTableView, Worklocation newWorklocation, WorklocationDAO worklocationDao) {
        if(controllerRepository == null) throw new NullPointerException("controllerRepository");
        if(events == null) throw new NullPointerException("events");
        if(worklocationTableView == null) throw new NullPointerException("worklocationTableView");
        if(newWorklocation == null) throw new NullPointerException("newWorklocation");
        if(worklocationDao == null) throw new NullPointerException("worklocationDao");
        
        this.mainToolBarViewController = (MainToolBarViewController) controllerRepository.get(MainToolBarViewController.class.getName());
        this.mainMenuBarViewController = (MainMenuBarViewController) controllerRepository.get(MainMenuBarViewController.class.getName());
        
        this.events = events;
        this.worklocationTableView = worklocationTableView;
        this.newWorklocation = newWorklocation;
        this.worklocationDao = worklocationDao;
    }
    
    @Override
    public boolean execute() {
        try {
            worklocationTableView.getItems().add(newWorklocation);
            if(!worklocationDao.create(newWorklocation)) {
                log.error("Adding worklocation failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                worklocationTableView.getSelectionModel().select(newWorklocation);
                events.notifyListenerOfEvent(newWorkLocationEvent, this);                
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
            worklocationTableView.getItems().remove(newWorklocation);
            if(!worklocationDao.delete(newWorklocation)) {
                log.error("Undo adding of worklocation failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                events.notifyListenerOfEvent(newWorkLocationEvent, this);                
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
        return "New Worklocation[" + newWorklocation.toString() + "]";
    }
    
}
