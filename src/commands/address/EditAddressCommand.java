/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package commands.address;

import commands.ICommand;
import controller.*;
import java.sql.SQLException;
import javafx.scene.control.TableView;
import model.Address;
import org.apache.logging.log4j.*;
import sqlite.AddressDAO;
import utils.EventManager;

/**
 *
 * @author stephan
 */
public class EditAddressCommand implements ICommand {

    private final String editAddressEvent = "EditAddress";
    
    private final MainToolBarViewController mainToolBarViewController;
    private final MainMenuBarViewController mainMenuBarViewController;
    private final EventManager events;
    private TableView<Address> addressTableView;
    private final Address originalAddress;
    private final Address modifiedAddress;
    private final AddressDAO addressDao;
    private final Logger log = LogManager.getLogger(EditAddressCommand.class.getName());
    
    public EditAddressCommand(ControllerRepository controllerRepository, EventManager events, TableView<Address> addressTableView, Address originalAddress, Address modifiedAddress, AddressDAO addressDao) {
        if(controllerRepository == null) throw new NullPointerException("controllerRepository");
        if(events == null) throw new NullPointerException("events");
        if(addressTableView == null) throw new NullPointerException("addressTableView");
        if(originalAddress == null) throw new NullPointerException("originalAddress");
        if(modifiedAddress == null) throw new NullPointerException("modifiedAddress");
        if(addressDao == null) throw new NullPointerException("addressDao");
        
        this.mainToolBarViewController = (MainToolBarViewController) controllerRepository.get(MainToolBarViewController.class.getName());
        this.mainMenuBarViewController = (MainMenuBarViewController) controllerRepository.get(MainMenuBarViewController.class.getName());
        
        this.events = events;
        this.addressTableView = addressTableView;
        this.originalAddress = originalAddress;
        this.modifiedAddress = modifiedAddress;
        this.addressDao = addressDao;                
    }
    
    @Override
    public boolean execute() {
        try {
            addressTableView.getItems().remove(modifiedAddress);
            addressTableView.getItems().add(modifiedAddress);
            if(!addressDao.update(originalAddress, modifiedAddress)) {
                log.error("Editing address failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                addressTableView.getSelectionModel().select(modifiedAddress);
                events.notifyListenerOfEvent(editAddressEvent, this);
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
            addressTableView.getItems().remove(modifiedAddress);
            addressTableView.getItems().add(originalAddress);
            if(!addressDao.update(modifiedAddress, originalAddress)) {
                log.error("Undo editing address failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                addressTableView.getSelectionModel().select(originalAddress);
                events.notifyListenerOfEvent(editAddressEvent, this);
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
            addressTableView.getItems().remove(originalAddress);
            addressTableView.getItems().add(modifiedAddress);
            if(!addressDao.update(originalAddress, modifiedAddress)) {
                log.error("Undo editing address failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                addressTableView.getSelectionModel().select(modifiedAddress);
                events.notifyListenerOfEvent(editAddressEvent, this);
                return true;
            }
        } catch (SQLException ex) {
            log.fatal(ex);
            return false;
        }
    }

    @Override
    public String getText() {
        return "Change Address from [" + originalAddress.toString() +"] to [" + modifiedAddress.toString() + "]";
    }
    
}
