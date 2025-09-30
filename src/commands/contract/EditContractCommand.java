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
public class EditContractCommand implements ICommand {

    private final String editContractEvent = "EditContract";
    
    private final MainToolBarViewController mainToolBarViewController;
    private final MainMenuBarViewController mainMenuBarViewController;
    private final EventManager events;
    private TableView<Contract> contractTableView;
    private final Contract originalContract;
    private final Contract modifiedContract;
    private final ContractDAO contractDao;
    private final Logger log = LogManager.getLogger(EditContractCommand.class.getName());
    
    public EditContractCommand(ControllerRepository controllerRepository, EventManager events, TableView<Contract> contractTableView, Contract originalContract, Contract modifiedContract, ContractDAO contractDao) {
        if(controllerRepository == null) throw new NullPointerException("controllerRepository");
        if(events == null) throw new NullPointerException("events");
        if(contractTableView == null) throw new NullPointerException("contractTableView");
        if(originalContract == null) throw new NullPointerException("originalContract");
        if(modifiedContract == null) throw new NullPointerException("modifiedContract");
        if(contractDao == null) throw new NullPointerException("contractDao");
        
        this.mainToolBarViewController = (MainToolBarViewController) controllerRepository.get(MainToolBarViewController.class.getName());
        this.mainMenuBarViewController = (MainMenuBarViewController) controllerRepository.get(MainMenuBarViewController.class.getName());
        
        this.events = events;
        this.contractTableView = contractTableView;
        this.originalContract = originalContract;
        this.modifiedContract = modifiedContract;
        this.contractDao = contractDao;                
    }
    
    @Override
    public boolean execute() {
        try {
            contractTableView.getItems().remove(modifiedContract);
            contractTableView.getItems().add(modifiedContract);
            if(!contractDao.update(originalContract, modifiedContract)) {
                log.error("Editing contract failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                contractTableView.getSelectionModel().select(modifiedContract);
                events.notifyListenerOfEvent(editContractEvent, modifiedContract);                
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
            contractTableView.getItems().remove(modifiedContract);
            contractTableView.getItems().add(originalContract);
            if(!contractDao.update(modifiedContract, originalContract)) {
                log.error("Undo editing contract failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                contractTableView.getSelectionModel().select(originalContract);
                events.notifyListenerOfEvent(editContractEvent, modifiedContract);                
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
            contractTableView.getItems().remove(originalContract);
            contractTableView.getItems().add(modifiedContract);
            if(!contractDao.update(originalContract, modifiedContract)) {
                log.error("Undo editing contract failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                contractTableView.getSelectionModel().select(modifiedContract);
                events.notifyListenerOfEvent(editContractEvent, modifiedContract);                
                return true;
            }
        } catch (SQLException ex) {
            log.fatal(ex);
            return false;
        }
    }

    @Override
    public String getText() {
        return "Change Contract from [" + originalContract.toString() +"] to [" + modifiedContract.toString() + "]";
    }
    
}
