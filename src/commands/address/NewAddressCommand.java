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
public class NewAddressCommand implements ICommand {

    private final String newAddressEvent = "NewAddress";
    
    private final MainToolBarViewController mainToolBarViewController;
    private final MainMenuBarViewController mainMenuBarViewController;
    private final EventManager events;
    private TableView<Address> addressTableView;
    private AddressDAO addressDao;
    private final Address newAddress;
    private final Logger log = LogManager.getLogger(NewAddressCommand.class.getName());
    
    public NewAddressCommand(ControllerRepository controllerRepository, EventManager events, TableView<Address> addressTableView, Address newAddress, AddressDAO addressDao) {
        if(controllerRepository == null) throw new NullPointerException("controllerRepository");
        if(events == null) throw new NullPointerException("events");
        if(addressTableView == null) throw new NullPointerException("addressTableView");
        if(newAddress == null) throw new NullPointerException("newAddress");
        if(addressDao == null) throw new NullPointerException("addressDao");
        
        this.mainToolBarViewController = (MainToolBarViewController) controllerRepository.get(MainToolBarViewController.class.getName());
        this.mainMenuBarViewController = (MainMenuBarViewController) controllerRepository.get(MainMenuBarViewController.class.getName());
        
        this.events = events;
        this.addressTableView = addressTableView;
        this.newAddress = newAddress;
        this.addressDao = addressDao;
    }
    
    @Override
    public boolean execute() {
        try {
            addressTableView.getItems().add(newAddress);
            if(!addressDao.create(newAddress)) {
                log.error("Adding address failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                addressTableView.getSelectionModel().select(newAddress);
                events.notifyListenerOfEvent(newAddressEvent, this);
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
            addressTableView.getItems().remove(newAddress);
            if(!addressDao.delete(newAddress)) {
                log.error("Undo adding of address failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                events.notifyListenerOfEvent(newAddressEvent, this);
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
        return "Create Address[" + newAddress.toString() + "]";
    }
    
}
