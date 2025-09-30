/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package commands.project;

import commands.ICommand;
import controller.*;
import javafx.scene.control.TableView;
import model.Project;
import sqlite.ProjectDAO;
import java.sql.SQLException;
import org.apache.logging.log4j.*;
import utils.EventManager;

/**
 *
 * @author adrest18
 */
public class EditProjectCommand implements ICommand {

    private final String editProjectEvent = "EditProject";
    
    private final MainToolBarViewController mainToolBarViewController;
    private final MainMenuBarViewController mainMenuBarViewController;
    private final EventManager events;
    private TableView<Project> projectTableView;
    private final Project originalProject;
    private final Project modifiedProject;
    private final ProjectDAO projectDao;
    private final Logger log = LogManager.getLogger(EditProjectCommand.class.getName());
    
    public EditProjectCommand(ControllerRepository controllerRepository, EventManager events, TableView<Project> projectTableView, Project originalProject, Project modifiedProject, ProjectDAO projectDao) {
        if(controllerRepository == null) throw new NullPointerException("controllerRepository");
        if(events == null) throw new NullPointerException("events");
        if(projectTableView == null) throw new NullPointerException("projectTableView");
        if(originalProject == null) throw new NullPointerException("originalProject");
        if(modifiedProject == null) throw new NullPointerException("modifiedProject");
        if(projectDao == null) throw new NullPointerException("roleDao");
        
        this.mainToolBarViewController = (MainToolBarViewController) controllerRepository.get(MainToolBarViewController.class.getName());
        this.mainMenuBarViewController = (MainMenuBarViewController) controllerRepository.get(MainMenuBarViewController.class.getName());
        
        this.events = events;
        this.projectTableView = projectTableView;
        this.originalProject = originalProject;
        this.modifiedProject = modifiedProject;
        this.projectDao = projectDao;                
    }

    @Override
    public boolean execute() {
        try {
            projectTableView.getItems().remove(modifiedProject);
            projectTableView.getItems().add(modifiedProject);
            if(!projectDao.update(originalProject, modifiedProject)) {
                log.error("Editing project failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                projectTableView.getSelectionModel().select(modifiedProject);
                events.notifyListenerOfEvent(editProjectEvent, this);                
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
            projectTableView.getItems().remove(modifiedProject);
            projectTableView.getItems().add(originalProject);
            if(!projectDao.update(modifiedProject, originalProject)) {
                log.error("Undo editing project failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                projectTableView.getSelectionModel().select(originalProject);
                events.notifyListenerOfEvent(editProjectEvent, this);                
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
            projectTableView.getItems().remove(originalProject);
            projectTableView.getItems().add(modifiedProject);
            if(!projectDao.update(originalProject, modifiedProject)) {
                log.error("Undo editing project failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                projectTableView.getSelectionModel().select(modifiedProject);
                events.notifyListenerOfEvent(editProjectEvent, this);                
                return true;
            }
        } catch (SQLException ex) {
            log.fatal(ex);
            return false;
        }
    }

    @Override
    public String getText() {
        return "Change Project from [" + originalProject.toString() +"] to [" + modifiedProject.toString() + "]";
    }
    
}
