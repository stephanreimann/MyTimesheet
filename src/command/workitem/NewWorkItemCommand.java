/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package command.workitem;

import commands.ICommand;
import controller.*;
import java.sql.SQLException;
import javafx.scene.control.TableView;
import model.WorkItem;
import org.apache.logging.log4j.*;
import sqlite.WorkItemDAO;
import utils.EventManager;

/**
 *
 * @author adrest18
 */
public class NewWorkItemCommand implements ICommand {

    private final String newWorkItemEvent = "NewWorkItem";
    
    private final MainToolBarViewController mainToolBarViewController;
    private final MainMenuBarViewController mainMenuBarViewController;
    private final EventManager events;
    private TableView<WorkItem> trackingItemTableView;
    private WorkItemDAO workItemDao;
    private final WorkItem newWorkItem;
    private final Logger log = LogManager.getLogger(NewWorkItemCommand.class.getName());
    
    public NewWorkItemCommand(ControllerRepository controllerRepository, EventManager events, TableView<WorkItem> trackingItemTableView, WorkItem newWorkItem, WorkItemDAO workItemDao) {
        if(controllerRepository == null) throw new NullPointerException("controllerRepository");
        if(events == null) throw new NullPointerException("events");
        if(trackingItemTableView == null) throw new NullPointerException("trackingItemTableView");
        if(newWorkItem == null) throw new NullPointerException("newWorkItem");
        if(workItemDao == null) throw new NullPointerException("workItemDao");
        
        this.mainToolBarViewController = (MainToolBarViewController) controllerRepository.get(MainToolBarViewController.class.getName());
        this.mainMenuBarViewController = (MainMenuBarViewController) controllerRepository.get(MainMenuBarViewController.class.getName());
        
        this.events = events;
        this.trackingItemTableView = trackingItemTableView;
        this.newWorkItem = newWorkItem;
        this.workItemDao = workItemDao;
    }
    
    @Override
    public boolean execute() {
        try {
            trackingItemTableView.getItems().add(newWorkItem);
            if(!workItemDao.create(newWorkItem)) {
                log.error("Adding trackingitem failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                events.notifyListenerOfEvent(newWorkItemEvent, this);
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
            trackingItemTableView.getItems().remove(newWorkItem);
            if(!workItemDao.delete(newWorkItem)) {
                log.error("Undo adding of trackingitem failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                events.notifyListenerOfEvent(newWorkItemEvent, this);
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
        return "Create Trackingitem[" + newWorkItem.toString() + "]";
    }
    
}
