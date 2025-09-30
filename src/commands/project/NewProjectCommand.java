/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package commands.project;

import java.sql.SQLException;
import commands.ICommand;
import controller.*;
import javafx.scene.control.TableView;
import model.Project;
import org.apache.logging.log4j.*;
import sqlite.ProjectDAO;
import utils.EventManager;

/**
 *
 * @author adrest18
 */
public class NewProjectCommand implements ICommand {

    private final String newProjectEvent = "NewProject";
    
    private final MainToolBarViewController mainToolBarViewController;
    private final MainMenuBarViewController mainMenuBarViewController;
    private final EventManager events;
    private TableView<Project> projectTableView;
    private ProjectDAO projectDao;
    private final Project newProject;
    private final Logger log = LogManager.getLogger(NewProjectCommand.class.getName());

    public NewProjectCommand(ControllerRepository controllerRepository, EventManager events, TableView<Project> projectTableView, Project newProject, ProjectDAO projectDao) {
        if(controllerRepository == null) throw new NullPointerException("controllerRepository");
        if(events == null) throw new NullPointerException("events");
        if(projectTableView == null) throw new NullPointerException("projectTableView");
        if(newProject == null) throw new NullPointerException("newProject");
        if(projectDao == null) throw new NullPointerException("projectDao");
        
        this.mainToolBarViewController = (MainToolBarViewController) controllerRepository.get(MainToolBarViewController.class.getName());
        this.mainMenuBarViewController = (MainMenuBarViewController) controllerRepository.get(MainMenuBarViewController.class.getName());
        
        this.events = events;
        this.projectTableView = projectTableView;
        this.newProject = newProject;
        this.projectDao = projectDao;
    }
    
    @Override
    public boolean execute() {
        try {
            projectTableView.getItems().add(newProject);
            if(!projectDao.create(newProject)) {
                log.error("Adding project failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                projectTableView.getSelectionModel().select(newProject);
                events.notifyListenerOfEvent(newProjectEvent, this);                
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
            projectTableView.getItems().remove(newProject);
            if(!projectDao.delete(newProject)) {
                log.error("Undo adding of project failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                events.notifyListenerOfEvent(newProjectEvent, this);                
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
        return "New Project[" + newProject.toString() + "]";
    }
    
}
