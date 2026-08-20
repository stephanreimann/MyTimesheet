/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package command.workitemtracking;

import commands.ICommand;
import controller.ControllerRepository;
import javafx.scene.control.TableView;
import model.WorkItem;
import sqlite.WorkItemDAO;
import utils.EventManager;

/**
 *
 * @author adrest18
 */
public class EditWorkItemCommand implements ICommand {

    public EditWorkItemCommand(ControllerRepository controllerRepository, EventManager events, TableView<WorkItem> trackingItemTableView, WorkItem newWorkItem, WorkItemDAO workItemDao) {

    }
    
    @Override
    public boolean execute() {
        return false;
    }

    @Override
    public boolean undo() {
        return false;
    }

    @Override
    public boolean redo() {
        return false;
    }

    @Override
    public String getText() {
        return "";
    }
    
}
