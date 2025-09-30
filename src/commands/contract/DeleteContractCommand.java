/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package commands.contract;

import commands.ICommand;
import controller.*;
import java.sql.SQLException;
import javafx.scene.control.TableView;
import model.Contract;
import org.apache.logging.log4j.*;
import sqlite.ContractDAO;
import utils.EventManager;

/**
 *
 * @author stephan
 */
public class DeleteContractCommand implements ICommand {

    private final String deleteContractEvent = "DeleteContract";
    
    private final MainToolBarViewController mainToolBarViewController;
    private final MainMenuBarViewController mainMenuBarViewController;
    private final EventManager events;
    private TableView<Contract> contractTableView;
    private Contract selectedContract;
    private ContractDAO contractDao;
    private final Logger log = LogManager.getLogger(DeleteContractCommand.class.getName());
    
    public DeleteContractCommand(ControllerRepository controllerRepository, EventManager events, TableView<Contract> contractTableView, Contract contractAddress, ContractDAO contractDao) {
        if(controllerRepository == null) throw new NullPointerException("controllerRepository");
        if(events == null) throw new NullPointerException("events");
        if(contractTableView == null) throw new NullPointerException("contractTableView");
        if(contractAddress == null) throw new NullPointerException("contractAddress");
        if(contractDao == null) throw new NullPointerException("contractDao");
        
        this.mainToolBarViewController = (MainToolBarViewController) controllerRepository.get(MainToolBarViewController.class.getName());
        this.mainMenuBarViewController = (MainMenuBarViewController) controllerRepository.get(MainMenuBarViewController.class.getName());
        
        this.events = events;
        this.contractTableView = contractTableView;
        this.selectedContract = contractAddress;
        this.contractDao = contractDao;
    }
    
    @Override
    public boolean execute() {
        contractTableView.getItems().remove(selectedContract);
        try {
            if(!contractDao.delete(selectedContract)) {
                log.error("Deletion of contract failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                events.notifyListenerOfEvent(deleteContractEvent, selectedContract);                
                return true;
            }
        } catch (SQLException ex) {
            log.fatal(ex);
            return false;
        }
    }

    @Override
    public boolean undo() {
        contractTableView.getItems().add(selectedContract);
        try {
            if(!contractDao.create(selectedContract)) {
                log.error("Undo contract of address failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                contractTableView.getSelectionModel().select(selectedContract);
                events.notifyListenerOfEvent(deleteContractEvent, selectedContract);                
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
        return "Delete Contract[" + selectedContract.toString() + "]";        
    }
    
}
