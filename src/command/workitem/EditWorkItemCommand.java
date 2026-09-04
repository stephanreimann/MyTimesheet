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
public class EditWorkItemCommand implements ICommand {

    private final String editWorkItemEvent = "EditWorkItem";

    private final MainToolBarViewController mainToolBarViewController;
    private final MainMenuBarViewController mainMenuBarViewController;
    private final WorkItemViewController workItemViewController;    
    private final EventManager events;
    private TableView<WorkItem> trackingItemTableView;
    private final WorkItem originalWorkItem;
    private final WorkItem modifiedWorkItem;
    private final WorkItemDAO workItemDao;
    private final Logger log = LogManager.getLogger(EditWorkItemCommand.class.getName());
    
    public EditWorkItemCommand(ControllerRepository controllerRepository, EventManager events, TableView<WorkItem> trackingItemTableView, WorkItem originalWorkItem, WorkItem modifiedWorkItem, WorkItemDAO workItemDao) {
        if(controllerRepository == null) throw new NullPointerException("controllerRepository");
        if(events == null) throw new NullPointerException("events");
        if(trackingItemTableView == null) throw new NullPointerException("trackingItemTableView");
        if(originalWorkItem == null) throw new NullPointerException("originalWorkItem");
        if(modifiedWorkItem == null) throw new NullPointerException("modifiedWorkItem");
        if(workItemDao == null) throw new NullPointerException("workItemDao");
        
        this.mainToolBarViewController = (MainToolBarViewController) controllerRepository.get(MainToolBarViewController.class.getName());
        this.mainMenuBarViewController = (MainMenuBarViewController) controllerRepository.get(MainMenuBarViewController.class.getName());
        this.workItemViewController = (WorkItemViewController) controllerRepository.get(WorkItemViewController.class.getName());
        
        this.events = events;
        this.trackingItemTableView = trackingItemTableView;
        this.originalWorkItem = originalWorkItem;
        this.modifiedWorkItem = modifiedWorkItem;
        this.workItemDao = workItemDao;
    }
    
    @Override
    public boolean execute() {
        try {
            trackingItemTableView.getItems().remove(originalWorkItem);
            trackingItemTableView.getItems().add(modifiedWorkItem);
            if(!workItemDao.update(originalWorkItem, modifiedWorkItem)) {
                log.error("Editing trackingitem failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                trackingItemTableView.getSelectionModel().select(modifiedWorkItem);
                workItemViewController.refreshButtonState();
                events.notifyListenerOfEvent(editWorkItemEvent, this);
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
            trackingItemTableView.getItems().remove(modifiedWorkItem);
            trackingItemTableView.getItems().add(originalWorkItem);
            if(!workItemDao.update(modifiedWorkItem, originalWorkItem)) {
                log.error("Undo editing trackingitem failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                trackingItemTableView.getSelectionModel().select(originalWorkItem);
                workItemViewController.refreshButtonState();
                events.notifyListenerOfEvent(editWorkItemEvent, this);
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
            trackingItemTableView.getItems().remove(originalWorkItem);
            trackingItemTableView.getItems().add(modifiedWorkItem);
            if(!workItemDao.update(originalWorkItem, modifiedWorkItem)) {
                log.error("Undo editing trackingitem failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                trackingItemTableView.getSelectionModel().select(modifiedWorkItem);
                workItemViewController.refreshButtonState();
                events.notifyListenerOfEvent(editWorkItemEvent, this);
                return true;
            }
        } catch (SQLException ex) {
            log.fatal(ex);
            return false;
        }
    }

    @Override
    public String getText() {
        return "Change Trackingitem from [" + originalWorkItem.toString() +"] to [" + modifiedWorkItem.toString() + "]";
    }
    
}
