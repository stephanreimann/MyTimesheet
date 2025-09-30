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
public class NewWorkrecordCommand implements ICommand {

    private final String newWorkrecordEvent = "NewWorkrecord";
    
    private final MainToolBarViewController mainToolBarViewController;
    private final MainMenuBarViewController mainMenuBarViewController;
    private final MainInfoViewController mainInfoViewController;
    private final WorkRecordViewController workRecordViewController;
    private final EventManager events;
    private TableView<Workrecord> workrecordTableView;
    private final Workrecord newWorkrecord;
    private final WorkrecordDAO workrecordDao;
    private final Logger log = LogManager.getLogger(NewWorkrecordCommand.class.getName());
    
    public NewWorkrecordCommand(ControllerRepository controllerRepository, EventManager events, TableView<Workrecord> workrecordTableView, Workrecord newWorkrecord, WorkrecordDAO workrecordDao) {
        if(controllerRepository == null) throw new NullPointerException("controllerRepository");
        if(events == null) throw new NullPointerException("events");
        if(workrecordTableView == null) throw new NullPointerException("workrecordTableView");
        if(newWorkrecord == null) throw new NullPointerException("acceptedWorkrecord");
        if(workrecordDao == null) throw new NullPointerException("workrecordDao");
        
        this.mainToolBarViewController = (MainToolBarViewController) controllerRepository.get(MainToolBarViewController.class.getName());
        this.mainMenuBarViewController = (MainMenuBarViewController) controllerRepository.get(MainMenuBarViewController.class.getName());
        this.mainInfoViewController = (MainInfoViewController) controllerRepository.get(MainInfoViewController.class.getName());
        this.workRecordViewController = (WorkRecordViewController)controllerRepository.get(WorkRecordViewController.class.getName());
        
        this.events = events;
        this.workrecordTableView = workrecordTableView;
        this.newWorkrecord = newWorkrecord;
        this.workrecordDao = workrecordDao;                
    }
    
    @Override
    public boolean execute() {
        try {
            workrecordTableView.getItems().add(newWorkrecord);
            if(!workrecordDao.create(newWorkrecord)) {
                log.error("Adding workrecord failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                mainInfoViewController.toggleUndoRedoButtons();
                workRecordViewController.selectWorkrecordOf(newWorkrecord.getDate());
                events.notifyListenerOfEvent(newWorkrecordEvent, newWorkrecord);                
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
            workrecordTableView.getItems().remove(newWorkrecord);
            if(!workrecordDao.delete(newWorkrecord)) {
                log.error("Undo adding of workrecord failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                workRecordViewController.selectWorkrecordOf(newWorkrecord.getDate());
                events.notifyListenerOfEvent(newWorkrecordEvent, newWorkrecord);                
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
        return "New Workrecord[" + newWorkrecord.toString() + "]";
    }
    
}
