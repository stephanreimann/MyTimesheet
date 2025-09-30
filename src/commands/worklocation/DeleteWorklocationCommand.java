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
public class DeleteWorklocationCommand implements ICommand {

    private final String deleteWorkLocationEvent = "DeleteWorkLocation";
    
    private final MainToolBarViewController mainToolBarViewController;
    private final MainMenuBarViewController mainMenuBarViewController;
    private final EventManager events;    
    private TableView<Worklocation> worklocationTableView;
    private Worklocation selectedWorklocation;
    private WorklocationDAO worklocationDao;
    private final Logger log = LogManager.getLogger(DeleteWorklocationCommand.class.getName());
    
    public DeleteWorklocationCommand(ControllerRepository controllerRepository, EventManager events, TableView<Worklocation> roleTableView, Worklocation selectedWorklocation, WorklocationDAO worklocationDao) {
        if(controllerRepository == null) throw new NullPointerException("controllerRepository");
        if(events == null) throw new NullPointerException("events");
        if(roleTableView == null) throw new NullPointerException("roleTableView");
        if(selectedWorklocation == null) throw new NullPointerException("selectedWorklocation");
        if(worklocationDao == null) throw new NullPointerException("worklocationDao");

        this.mainToolBarViewController = (MainToolBarViewController) controllerRepository.get(MainToolBarViewController.class.getName());
        this.mainMenuBarViewController = (MainMenuBarViewController) controllerRepository.get(MainMenuBarViewController.class.getName());
        
        this.events = events;
        this.worklocationTableView = roleTableView;
        this.selectedWorklocation = selectedWorklocation;
        this.worklocationDao = worklocationDao;
    }
    
    @Override
    public boolean execute() {
        worklocationTableView.getItems().remove(selectedWorklocation);
        try {
            if(!worklocationDao.delete(selectedWorklocation)) {
                log.error("Deletion of wolklocation failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                events.notifyListenerOfEvent(deleteWorkLocationEvent, this);                
                return true;
            }
        } catch (SQLException ex) {
            log.fatal(ex);
            return false;
        }
    }

    @Override
    public boolean undo() {
        worklocationTableView.getItems().add(selectedWorklocation);
        try {
            if(!worklocationDao.create(selectedWorklocation)) {
                log.error("Undo deletion of wolklocation failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                worklocationTableView.getSelectionModel().select(selectedWorklocation);
                events.notifyListenerOfEvent(deleteWorkLocationEvent, this);                
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
        return "Delete Worklocation[" + selectedWorklocation.toString() + "]";        
    }
    
}
