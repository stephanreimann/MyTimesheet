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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sqlite.WorkItemDAO;
import utils.EventManager;

/**
 *
 * @author adrest18
 */
public class DeleteWorkItemCommand implements ICommand {

    private final String deleteWorkItemEvent = "DeleteWorkItem";
 
    private final MainToolBarViewController mainToolBarViewController;
    private final MainMenuBarViewController mainMenuBarViewController;
    private final WorkItemViewController workItemViewController;
    private final EventManager events;
    private TableView<WorkItem> trackingItemTableView;
    private WorkItem selectedWorkItem;
    private WorkItemDAO workItemDao;
    private final Logger log = LogManager.getLogger(DeleteWorkItemCommand.class.getName());

    public DeleteWorkItemCommand(ControllerRepository controllerRepository, EventManager events, TableView<WorkItem> trackingItemTableView, WorkItem selectedWorkItem, WorkItemDAO workItemDao) {
        if(controllerRepository == null) throw new NullPointerException("controllerRepository");
        if(events == null) throw new NullPointerException("events");
        if(trackingItemTableView == null) throw new NullPointerException("trackingItemTableView");
        if(selectedWorkItem == null) throw new NullPointerException("selectedWorkItem");
        if(workItemDao == null) throw new NullPointerException("workItemDao");

        this.mainToolBarViewController = (MainToolBarViewController) controllerRepository.get(MainToolBarViewController.class.getName());
        this.mainMenuBarViewController = (MainMenuBarViewController) controllerRepository.get(MainMenuBarViewController.class.getName());
        this.workItemViewController = (WorkItemViewController) controllerRepository.get(WorkItemViewController.class.getName());
        
        this.events = events;
        this.trackingItemTableView = trackingItemTableView;
        this.selectedWorkItem = selectedWorkItem;
        this.workItemDao = workItemDao;
    }
    
    @Override
    public boolean execute() {
        trackingItemTableView.getItems().remove(selectedWorkItem);
        try {
            if(!workItemDao.delete(selectedWorkItem)) {
                log.error("Deletion of trackingitem failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                trackingItemTableView.getSelectionModel().select(0);
                workItemViewController.refreshButtonState();
                events.notifyListenerOfEvent(deleteWorkItemEvent, this);
                return true;
            }
        } catch (SQLException ex) {
            log.fatal(ex);
            return false;
        }
    }

    @Override
    public boolean undo() {
        trackingItemTableView.getItems().add(selectedWorkItem);
        try {
            if(!workItemDao.create(selectedWorkItem)) {
                log.error("Undo deletion of trackingitem failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                trackingItemTableView.getSelectionModel().select(selectedWorkItem);
                workItemViewController.refreshButtonState();
                events.notifyListenerOfEvent(deleteWorkItemEvent, this);
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
        return "Delete Trackingitem[" + selectedWorkItem.toString() + "]";
    }
    
}
