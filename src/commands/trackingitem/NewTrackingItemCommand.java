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
public class NewTrackingItemCommand implements ICommand {

    private final String newTrackingItemEvent = "NewTrackingItem";
    
    private final MainToolBarViewController mainToolBarViewController;
    private final MainMenuBarViewController mainMenuBarViewController;
    private final EventManager events;
    private TableView<TrackingItem> trackingItemTableView;
    private TrackingItemDAO trackingItemDao;
    private final TrackingItem newTrackingItem;
    private final Logger log = LogManager.getLogger(NewTrackingItemCommand.class.getName());
    
    public NewTrackingItemCommand(ControllerRepository controllerRepository, EventManager events, TableView<TrackingItem> trackingItemTableView, TrackingItem newTrackingItem, TrackingItemDAO trackingItemDao) {
        if(controllerRepository == null) throw new NullPointerException("controllerRepository");
        if(events == null) throw new NullPointerException("events");
        if(trackingItemTableView == null) throw new NullPointerException("trackingItemTableView");
        if(newTrackingItem == null) throw new NullPointerException("newTrackingItem");
        if(trackingItemDao == null) throw new NullPointerException("trackingItemDao");
        
        this.mainToolBarViewController = (MainToolBarViewController) controllerRepository.get(MainToolBarViewController.class.getName());
        this.mainMenuBarViewController = (MainMenuBarViewController) controllerRepository.get(MainMenuBarViewController.class.getName());
        
        this.events = events;        
        this.trackingItemTableView = trackingItemTableView;
        this.newTrackingItem = newTrackingItem;
        this.trackingItemDao = trackingItemDao;
    }
    
    @Override
    public boolean execute() {
        try {
            trackingItemTableView.getItems().add(newTrackingItem);
            if(!trackingItemDao.create(newTrackingItem)) {
                log.error("Adding trackingItem failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                trackingItemTableView.getSelectionModel().select(newTrackingItem);
                events.notifyListenerOfEvent(newTrackingItemEvent, this);                
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
            trackingItemTableView.getItems().remove(newTrackingItem);
            if(!trackingItemDao.delete(newTrackingItem)) {
                log.error("Undo adding of trackingItem failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                events.notifyListenerOfEvent(newTrackingItemEvent, this);                
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
        return "New TrackingItem[" + newTrackingItem.toString() + "]";
    }
    
}
