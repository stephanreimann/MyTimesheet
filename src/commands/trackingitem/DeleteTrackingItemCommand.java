/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package commands.trackingitem;

import commands.ICommand;
import controller.*;
import java.sql.SQLException;
import javafx.scene.control.TableView;
import model.TrackingItem;
import org.apache.logging.log4j.*;
import sqlite.TrackingItemDAO;
import utils.EventManager;

/**
 *
 * @author stephan
 */
public class DeleteTrackingItemCommand implements ICommand {

    private final String deleteTrackingItemEvent = "DeleteTrackingItem";
    
    private final MainToolBarViewController mainToolBarViewController;
    private final MainMenuBarViewController mainMenuBarViewController;
    private final EventManager events;
    private TableView<TrackingItem> trackingItemTableView;
    private TrackingItem selectedTrackingItem;
    private TrackingItemDAO trackingItemDao;
    private final Logger log = LogManager.getLogger(DeleteTrackingItemCommand.class.getName());
    
    public DeleteTrackingItemCommand(ControllerRepository controllerRepository, EventManager events, TableView<TrackingItem> trackingItemTableView, TrackingItem selectedTrackingItem, TrackingItemDAO trackingItemDao) {
        if(controllerRepository == null) throw new NullPointerException("controllerRepository");
        if(events == null) throw new NullPointerException("events");
        if(trackingItemTableView == null) throw new NullPointerException("trackingItemTableView");
        if(selectedTrackingItem == null) throw new NullPointerException("selectedTrackingItem");
        if(trackingItemDao == null) throw new NullPointerException("trackingItemDao");

        this.mainToolBarViewController = (MainToolBarViewController) controllerRepository.get(MainToolBarViewController.class.getName());
        this.mainMenuBarViewController = (MainMenuBarViewController) controllerRepository.get(MainMenuBarViewController.class.getName());
        
        this.events = events;
        this.trackingItemTableView = trackingItemTableView;
        this.selectedTrackingItem = selectedTrackingItem;
        this.trackingItemDao = trackingItemDao;
    }
    
    @Override
    public boolean execute() {
        trackingItemTableView.getItems().remove(selectedTrackingItem);
        try {
            if(!trackingItemDao.delete(selectedTrackingItem)) {
                log.error("Deletion of trackingItem failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                events.notifyListenerOfEvent(deleteTrackingItemEvent, this);                
                return true;
            }
        } catch (SQLException ex) {
            log.fatal(ex);
            return false;
        }
    }

    @Override
    public boolean undo() {
        trackingItemTableView.getItems().add(selectedTrackingItem);
        try {
            if(!trackingItemDao.create(selectedTrackingItem)) {
                log.error("Undo deletion of trackingItem failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                trackingItemTableView.getSelectionModel().select(selectedTrackingItem);
                events.notifyListenerOfEvent(deleteTrackingItemEvent, this);                
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
        return "Delete TrackingItem[" + selectedTrackingItem.toString() + "]";        
    }
    
}
