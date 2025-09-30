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
public class NewContractCommand implements ICommand {

    private final String newContractEvent = "NewContract";
    
    private final MainToolBarViewController mainToolBarViewController;
    private final MainMenuBarViewController mainMenuBarViewController;
    private final EventManager events;
    private TableView<Contract> contractTableView;
    private ContractDAO contractDao;
    private final Contract newContract;
    private final Logger log = LogManager.getLogger(NewContractCommand.class.getName());
    
    public NewContractCommand(ControllerRepository controllerRepository, EventManager events, TableView<Contract> contractTableView, Contract newContract, ContractDAO contractDao) {
        if(controllerRepository == null) throw new NullPointerException("controllerRepository");
        if(events == null) throw new NullPointerException("events");
        if(contractTableView == null) throw new NullPointerException("contractTableView");
        if(newContract == null) throw new NullPointerException("newContract");
        if(contractDao == null) throw new NullPointerException("contractDao");
        
        this.mainToolBarViewController = (MainToolBarViewController) controllerRepository.get(MainToolBarViewController.class.getName());
        this.mainMenuBarViewController = (MainMenuBarViewController) controllerRepository.get(MainMenuBarViewController.class.getName());
        
        this.events = events;
        this.contractTableView = contractTableView;
        this.newContract = newContract;
        this.contractDao = contractDao;
    }
    
    @Override
    public boolean execute() {
        try {
            contractTableView.getItems().add(newContract);
            if(!contractDao.create(newContract)) {
                log.error("Adding contract failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                contractTableView.getSelectionModel().select(newContract);
                events.notifyListenerOfEvent(newContractEvent, newContract);
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
            contractTableView.getItems().remove(newContract);
            if(!contractDao.delete(newContract)) {
                log.error("Undo adding of contract failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                events.notifyListenerOfEvent(newContractEvent, newContract);
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
        return "New Contract[" + newContract.toString() + "]";
    }
    
}
