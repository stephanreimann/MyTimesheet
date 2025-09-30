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
public class DeleteAddressCommand implements ICommand {

    private final String deleteAddressEvent = "DeleteAddress";
    
    private final MainToolBarViewController mainToolBarViewController;
    private final MainMenuBarViewController mainMenuBarViewController;
    private final EventManager events;
    private TableView<Address> addressTableView;
    private Address selectedAddress;
    private AddressDAO addressDao;
    private final Logger log = LogManager.getLogger(DeleteAddressCommand.class.getName());
    
    public DeleteAddressCommand(ControllerRepository controllerRepository, EventManager events, TableView<Address> addressTableView, Address selectedAddress, AddressDAO addressDao) {
        if(controllerRepository == null) throw new NullPointerException("controllerRepository");
        if(events == null) throw new NullPointerException("events");
        if(addressTableView == null) throw new NullPointerException("addressTableView");
        if(selectedAddress == null) throw new NullPointerException("selectedAddress");
        if(addressDao == null) throw new NullPointerException("addressDao");

        this.mainToolBarViewController = (MainToolBarViewController) controllerRepository.get(MainToolBarViewController.class.getName());
        this.mainMenuBarViewController = (MainMenuBarViewController) controllerRepository.get(MainMenuBarViewController.class.getName());
        
        this.events = events;
        this.addressTableView = addressTableView;
        this.selectedAddress = selectedAddress;
        this.addressDao = addressDao;
    }
    
    @Override
    public boolean execute() {
        addressTableView.getItems().remove(selectedAddress);
        try {
            if(!addressDao.delete(selectedAddress)) {
                log.error("Deletion of address failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                events.notifyListenerOfEvent(deleteAddressEvent, this);
                return true;
            }
        } catch (SQLException ex) {
            log.fatal(ex);
            return false;
        }
    }

    @Override
    public boolean undo() {
        addressTableView.getItems().add(selectedAddress);
        try {
            if(!addressDao.create(selectedAddress)) {
                log.error("Undo deletion of address failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                addressTableView.getSelectionModel().select(selectedAddress);
                events.notifyListenerOfEvent(deleteAddressEvent, this);
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
        return "Delete Address[" + selectedAddress.toString() + "]";
    }
    
}
