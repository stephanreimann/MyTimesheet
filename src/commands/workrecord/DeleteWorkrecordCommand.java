/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package commands.workrecord;

import commands.ICommand;
import controller.*;
import java.sql.SQLException;
import javafx.scene.control.TableView;
import model.Workrecord;
import org.apache.logging.log4j.*;
import sqlite.WorkrecordDAO;
import utils.EventManager;

/**
 *
 * @author adrest18
 */
public class DeleteWorkrecordCommand implements ICommand {

    private final String deleteWorkrecordEvent = "DeleteWorkrecord";
    
    private final MainToolBarViewController mainToolBarViewController;
    private final MainMenuBarViewController mainMenuBarViewController;
    private final WorkRecordViewController workRecordViewController;    
    private final EventManager events;    
    private TableView<Workrecord> workrecordTableView;
    private Workrecord selectedWorkrecord;
    private WorkrecordDAO workrecordDao;
    private final Logger log = LogManager.getLogger(DeleteWorkrecordCommand.class.getName());
    
    public DeleteWorkrecordCommand(ControllerRepository controllerRepository, EventManager events, TableView<Workrecord> workrecordTableView, Workrecord selectedWorkrecord, WorkrecordDAO workrecordDao) {
        if(controllerRepository == null) throw new NullPointerException("controllerRepository");
        if(events == null) throw new NullPointerException("events");
        if(workrecordTableView == null) throw new NullPointerException("workrecordTableView");
        if(selectedWorkrecord == null) throw new NullPointerException("selectedWorkrecord");
        if(workrecordDao == null) throw new NullPointerException("workrecordDao");

        this.mainToolBarViewController = (MainToolBarViewController) controllerRepository.get(MainToolBarViewController.class.getName());
        this.mainMenuBarViewController = (MainMenuBarViewController) controllerRepository.get(MainMenuBarViewController.class.getName());
        this.workRecordViewController = (WorkRecordViewController)controllerRepository.get(WorkRecordViewController.class.getName());
        
        this.events = events;
        this.workrecordTableView = workrecordTableView;
        this.selectedWorkrecord = selectedWorkrecord;
        this.workrecordDao = workrecordDao;
    }
    
    @Override
    public boolean execute() {
        workrecordTableView.getItems().remove(selectedWorkrecord);
        try {
            if(!workrecordDao.delete(selectedWorkrecord)) {
                log.error("Deletion of workrecord failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                workRecordViewController.selectWorkrecordOf(selectedWorkrecord.getDate());
                events.notifyListenerOfEvent(deleteWorkrecordEvent, selectedWorkrecord);                
                return true;
            }
        } catch (SQLException ex) {
            log.fatal(ex);
            return false;
        }
    }

    @Override
    public boolean undo() {
        workrecordTableView.getItems().add(selectedWorkrecord);
        try {
            if(!workrecordDao.create(selectedWorkrecord)) {
                log.error("Undo deletion of workrecord failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                workRecordViewController.selectWorkrecordOf(selectedWorkrecord.getDate());
                events.notifyListenerOfEvent(deleteWorkrecordEvent, this);                
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
        return "Delete Workrecord[" + selectedWorkrecord.toString() + "]";        
    }
    
}
