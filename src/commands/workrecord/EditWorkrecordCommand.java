/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package commands.workrecord;

import commands.ICommand;
import commands.user.EditUserCommand;
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
public class EditWorkrecordCommand implements ICommand {

    private final String editWorkrecordEvent = "EditWorkrecord";
    
    private final MainToolBarViewController mainToolBarViewController;
    private final MainMenuBarViewController mainMenuBarViewController;
    private final WorkRecordViewController workRecordViewController;    
    private final EventManager events;
    private TableView<Workrecord> workrecordTableView;
    private final Workrecord originalWorkrecord;
    private final Workrecord modifiedWorkrecord;
    private final WorkrecordDAO workrecordDao;
    private final org.apache.logging.log4j.Logger log = LogManager.getLogger(EditUserCommand.class.getName());
    
    public EditWorkrecordCommand(ControllerRepository controllerRepository, EventManager events, TableView<Workrecord> workrecordTableView, Workrecord originalWorkrecord, Workrecord modifiedWorkrecord, WorkrecordDAO workrecordDao) {
        if(controllerRepository == null) throw new NullPointerException("controllerRepository");
        if(events == null) throw new NullPointerException("events");
        if(workrecordTableView == null) throw new NullPointerException("workrecordTableView");
        if(originalWorkrecord == null) throw new NullPointerException("originalWorkrecord");
        if(modifiedWorkrecord == null) throw new NullPointerException("modifiedWorkrecord");
        if(workrecordDao == null) throw new NullPointerException("workrecordDao");
        
        this.mainToolBarViewController = (MainToolBarViewController) controllerRepository.get(MainToolBarViewController.class.getName());
        this.mainMenuBarViewController = (MainMenuBarViewController) controllerRepository.get(MainMenuBarViewController.class.getName());
        this.workRecordViewController = (WorkRecordViewController)controllerRepository.get(WorkRecordViewController.class.getName());
        
        this.events = events;
        this.workrecordTableView = workrecordTableView;
        this.originalWorkrecord = originalWorkrecord;
        this.modifiedWorkrecord = modifiedWorkrecord;
        this.workrecordDao = workrecordDao;                
    }
    
    @Override
    public boolean execute() {
        try {
            workrecordTableView.getItems().remove(modifiedWorkrecord);
            workrecordTableView.getItems().add(modifiedWorkrecord);
            if(!workrecordDao.update(originalWorkrecord, modifiedWorkrecord)) {
                log.error("Editing workrecord failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                workRecordViewController.selectWorkrecordOf(modifiedWorkrecord.getDate());
                events.notifyListenerOfEvent(editWorkrecordEvent, modifiedWorkrecord);
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
            workrecordTableView.getItems().remove(modifiedWorkrecord);
            workrecordTableView.getItems().add(originalWorkrecord);
            if(!workrecordDao.update(modifiedWorkrecord, originalWorkrecord)) {
                log.error("Undo editing user failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                workRecordViewController.selectWorkrecordOf(originalWorkrecord.getDate());
                events.notifyListenerOfEvent(editWorkrecordEvent, originalWorkrecord);
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
            workrecordTableView.getItems().remove(originalWorkrecord);
            workrecordTableView.getItems().add(modifiedWorkrecord);
            if(!workrecordDao.update(originalWorkrecord, modifiedWorkrecord)) {
                log.error("Undo editing user failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                workRecordViewController.selectWorkrecordOf(modifiedWorkrecord.getDate());
                events.notifyListenerOfEvent(editWorkrecordEvent, modifiedWorkrecord);
                return true;
            }
        } catch (SQLException ex) {
            log.fatal(ex);
            return false;
        }
    }

    @Override
    public String getText() {
        return "Change Workrecord from [" + originalWorkrecord.toString() +"] to [" + modifiedWorkrecord.toString() + "]";
    }
    
}
