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
public class EditHolydayCommand implements ICommand {

    private final MainToolBarViewController mainToolBarViewController;
    private final MainMenuBarViewController mainMenuBarViewController;
    private TableView<Holyday> holydayTableView;
    private final Holyday originalHolyday;
    private final Holyday modifiedHolyday;
    private final HolydayDAO holydayDao;
    private final Logger log = LogManager.getLogger(EditHolydayCommand.class.getName());
    
    public EditHolydayCommand(ControllerRepository controllerRepository, TableView<Holyday> holydayTableView, Holyday originalHolyday, Holyday modifiedHolyday, HolydayDAO holydayDao) {
        if(controllerRepository == null) throw new NullPointerException("controllerRepository");
        if(holydayTableView == null) throw new NullPointerException("holydayTableView");
        if(originalHolyday == null) throw new NullPointerException("originalHolyday");
        if(modifiedHolyday == null) throw new NullPointerException("modifiedHolyday");
        if(holydayDao == null) throw new NullPointerException("holydayDao");
        
        this.mainToolBarViewController = (MainToolBarViewController) controllerRepository.get(MainToolBarViewController.class.getName());
        this.mainMenuBarViewController = (MainMenuBarViewController) controllerRepository.get(MainMenuBarViewController.class.getName());
        
        this.holydayTableView = holydayTableView;
        this.originalHolyday = originalHolyday;
        this.modifiedHolyday = modifiedHolyday;
        this.holydayDao = holydayDao;                
    }
    
    @Override
    public boolean execute() {
        try {
            holydayTableView.getItems().remove(modifiedHolyday);
            holydayTableView.getItems().add(modifiedHolyday);
            if(!holydayDao.update(originalHolyday, modifiedHolyday)) {
                log.error("Editing holyday failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                holydayTableView.getSelectionModel().select(modifiedHolyday);
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
            holydayTableView.getItems().remove(modifiedHolyday);
            holydayTableView.getItems().add(originalHolyday);
            if(!holydayDao.update(modifiedHolyday, originalHolyday)) {
                log.error("Undo editing holyday failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                holydayTableView.getSelectionModel().select(originalHolyday);
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
            holydayTableView.getItems().remove(originalHolyday);
            holydayTableView.getItems().add(modifiedHolyday);
            if(!holydayDao.update(originalHolyday, modifiedHolyday)) {
                log.error("Undo editing holyday failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                holydayTableView.getSelectionModel().select(modifiedHolyday);
                return true;
            }
        } catch (SQLException ex) {
            log.fatal(ex);
            return false;
        }
    }

    @Override
    public String getText() {
        return "Edit Holyday";
    }
    
}
