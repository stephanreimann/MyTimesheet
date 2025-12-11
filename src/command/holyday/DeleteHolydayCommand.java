/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package command.holyday;

import commands.ICommand;
import controller.*;
import java.sql.SQLException;
import javafx.scene.control.TableView;
import model.Holyday;
import org.apache.logging.log4j.*;
import sqlite.HolydayDAO;
import utils.EventManager;

/**
 *
 * @author adrest18
 */
public class DeleteHolydayCommand implements ICommand {

    private final String deleteHolydayEvent = "DeleteHolyday";

    private final MainToolBarViewController mainToolBarViewController;
    private final MainMenuBarViewController mainMenuBarViewController;
    private final EventManager eventManager;
    private TableView<Holyday> holydayTableView;
    private Holyday selectedHolyday;
    private HolydayDAO holydayDao;
    private final Logger log = LogManager.getLogger(DeleteHolydayCommand.class.getName());
    
    public DeleteHolydayCommand(ControllerRepository controllerRepository, EventManager eventManager, TableView<Holyday> holydayTableView, Holyday selectedHolyday, HolydayDAO holydayDao) {
        if(controllerRepository == null) throw new NullPointerException("controllerRepository");
        if(eventManager == null) throw new NullPointerException("events");
        if(holydayTableView == null) throw new NullPointerException("holydayTableView");
        if(selectedHolyday == null) throw new NullPointerException("selectedHolyday");
        if(holydayDao == null) throw new NullPointerException("holydayDao");

        this.mainToolBarViewController = (MainToolBarViewController) controllerRepository.get(MainToolBarViewController.class.getName());
        this.mainMenuBarViewController = (MainMenuBarViewController) controllerRepository.get(MainMenuBarViewController.class.getName());
        
        this.eventManager = eventManager;
        this.holydayTableView = holydayTableView;
        this.selectedHolyday = selectedHolyday;
        this.holydayDao = holydayDao;
    }
    
    @Override
    public boolean execute() {
        holydayTableView.getItems().remove(selectedHolyday);
        try {
            if(!holydayDao.delete(selectedHolyday)) {
                log.error("Deletion of holyday failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                eventManager.notifyListenerOfEvent(deleteHolydayEvent, this);                
                return true;
            }
        } catch (SQLException ex) {
            log.fatal(ex);
            return false;
        }
    }

    @Override
    public boolean undo() {
        holydayTableView.getItems().add(selectedHolyday);
        try {
            if(!holydayDao.create(selectedHolyday)) {
                log.error("Undo deletion of holyday failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                holydayTableView.getSelectionModel().select(selectedHolyday);
                eventManager.notifyListenerOfEvent(deleteHolydayEvent, this);                
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
        return "Delete Holyday";
    }
    
}
