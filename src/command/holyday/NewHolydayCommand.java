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

/**
 *
 * @author adrest18
 */
public class NewHolydayCommand implements ICommand {

    private final MainToolBarViewController mainToolBarViewController;
    private final MainMenuBarViewController mainMenuBarViewController;
    private TableView<Holyday> holydayTableView;
    private HolydayDAO holydayDao;
    private final Holyday newHolyday;
    private final Logger log = LogManager.getLogger(NewHolydayCommand.class.getName());

    public NewHolydayCommand(ControllerRepository controllerRepository, TableView<Holyday> holydayTableView, Holyday newHolyday, HolydayDAO holydayDao) {
        if(controllerRepository == null) throw new NullPointerException("controllerRepository");
        if(holydayTableView == null) throw new NullPointerException("holydayTableView");
        if(newHolyday == null) throw new NullPointerException("newHolyday");
        if(holydayDao == null) throw new NullPointerException("holydayDao");
        
        this.mainToolBarViewController = (MainToolBarViewController) controllerRepository.get(MainToolBarViewController.class.getName());
        this.mainMenuBarViewController = (MainMenuBarViewController) controllerRepository.get(MainMenuBarViewController.class.getName());
        
        this.holydayTableView = holydayTableView;
        this.newHolyday = newHolyday;
        this.holydayDao = holydayDao;
    }
    
    @Override
    public boolean execute() {
        try {
            holydayTableView.getItems().add(newHolyday);
            if(!holydayDao.create(newHolyday)) {
                log.error("Adding holyday failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                holydayTableView.getSelectionModel().select(newHolyday);
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
            holydayTableView.getItems().remove(newHolyday);
            if(!holydayDao.delete(newHolyday)) {
                log.error("Undo adding of holyday failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
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
        return "New Holyday";
    }
    
}
