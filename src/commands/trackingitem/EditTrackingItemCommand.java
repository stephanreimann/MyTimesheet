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
public class EditTrackingItemCommand implements ICommand {

    private final String editTrackingItemEvent = "EditTrackingItem";
    
    private final MainToolBarViewController mainToolBarViewController;
    private final MainMenuBarViewController mainMenuBarViewController;
    private final EventManager events;
    private TableView<TrackingItem> trackingItemTableView;
    private final TrackingItem originalTrackingItem;
    private final TrackingItem modifiedTrackingItem;
    private final TrackingItemDAO trackingItemDao;
    private final Logger log = LogManager.getLogger(EditTrackingItemCommand.class.getName());
    
    public EditTrackingItemCommand(ControllerRepository controllerRepository, EventManager events, TableView<TrackingItem> trackingItemTableView, TrackingItem originalTrackingItem, TrackingItem modifiedTrackingItem, TrackingItemDAO trackingItemDao) {
        if(controllerRepository == null) throw new NullPointerException("controllerRepository");
        if(events == null) throw new NullPointerException("events");
        if(trackingItemTableView == null) throw new NullPointerException("trackingItemTableView");
        if(originalTrackingItem == null) throw new NullPointerException("originalTrackingItem");
        if(modifiedTrackingItem == null) throw new NullPointerException("modifiedTrackingItem");
        if(trackingItemDao == null) throw new NullPointerException("trackingItemDao");
        
        this.mainToolBarViewController = (MainToolBarViewController) controllerRepository.get(MainToolBarViewController.class.getName());
        this.mainMenuBarViewController = (MainMenuBarViewController) controllerRepository.get(MainMenuBarViewController.class.getName());
        
        this.events = events;        
        this.trackingItemTableView = trackingItemTableView;
        this.originalTrackingItem = originalTrackingItem;
        this.modifiedTrackingItem = modifiedTrackingItem;
        this.trackingItemDao = trackingItemDao;                
    }
    
    @Override
    public boolean execute() {
        try {
            trackingItemTableView.getItems().remove(modifiedTrackingItem);
            trackingItemTableView.getItems().add(modifiedTrackingItem);
            if(!trackingItemDao.update(originalTrackingItem, modifiedTrackingItem)) {
                log.error("Editing trackingItem failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                trackingItemTableView.getSelectionModel().select(modifiedTrackingItem);
                events.notifyListenerOfEvent(editTrackingItemEvent, this);                
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
            trackingItemTableView.getItems().remove(modifiedTrackingItem);
            trackingItemTableView.getItems().add(originalTrackingItem);
            if(!trackingItemDao.update(modifiedTrackingItem, originalTrackingItem)) {
                log.error("Undo editing trackingItem failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                trackingItemTableView.getSelectionModel().select(originalTrackingItem);
                events.notifyListenerOfEvent(editTrackingItemEvent, this);                
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
            trackingItemTableView.getItems().remove(originalTrackingItem);
            trackingItemTableView.getItems().add(modifiedTrackingItem);
            if(!trackingItemDao.update(originalTrackingItem, modifiedTrackingItem)) {
                log.error("Undo editing trackingItem failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                trackingItemTableView.getSelectionModel().select(modifiedTrackingItem);
                events.notifyListenerOfEvent(editTrackingItemEvent, this);                
                return true;
            }
        } catch (SQLException ex) {
            log.fatal(ex);
            return false;
        }
    }

    @Override
    public String getText() {
        return "Change TrackingItem from [" + originalTrackingItem.toString() +"] to [" + modifiedTrackingItem.toString() + "]";
    }
    
}
