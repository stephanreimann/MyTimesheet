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
public class DeleteProjectCommand implements ICommand {

    private final String deleteProjectEvent = "DeleteProject";
    
    private final MainToolBarViewController mainToolBarViewController;
    private final MainMenuBarViewController mainMenuBarViewController;
    private final EventManager events;
    private TableView<Project> projectTableView;
    private Project selectedProject;
    private ProjectDAO projectDao;
    private final Logger log = LogManager.getLogger(DeleteProjectCommand.class.getName());
    
    public DeleteProjectCommand(ControllerRepository controllerRepository, EventManager events, TableView<Project> projectTableView, Project selectedProject, ProjectDAO projectDao) {
        if(controllerRepository == null) throw new NullPointerException("controllerRepository");
        if(events == null) throw new NullPointerException("events");
        if(projectTableView == null) throw new NullPointerException("roleTableView");
        if(selectedProject == null) throw new NullPointerException("selectedRole");
        if(projectDao == null) throw new NullPointerException("roleDao");

        this.mainToolBarViewController = (MainToolBarViewController) controllerRepository.get(MainToolBarViewController.class.getName());
        this.mainMenuBarViewController = (MainMenuBarViewController) controllerRepository.get(MainMenuBarViewController.class.getName());
        
        this.events = events;
        this.projectTableView = projectTableView;
        this.selectedProject = selectedProject;
        this.projectDao = projectDao;
    }

    @Override
    public boolean execute() {
        projectTableView.getItems().remove(selectedProject);
        try {
            if(!projectDao.delete(selectedProject)) {
                log.error("Deletion of project failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                events.notifyListenerOfEvent(deleteProjectEvent, this);                
                return true;
            }
        } catch (SQLException ex) {
            log.fatal(ex);
            return false;
        }
    }

    @Override
    public boolean undo() {
        projectTableView.getItems().add(selectedProject);
        try {
            if(!projectDao.create(selectedProject)) {
                log.error("Undo deletion of project failed");
                return false;
            } else {
                mainToolBarViewController.toggleUndoRedoButtons();
                mainMenuBarViewController.toggleUndoRedoMenuItems();
                projectTableView.getSelectionModel().select(selectedProject);
                events.notifyListenerOfEvent(deleteProjectEvent, this);                
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
        return "Delete Project[" + selectedProject.toString() + "]";        
    }
    
}
