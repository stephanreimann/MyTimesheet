/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package commands.workrecorddetails;

import commands.ICommand;
import commands.workrecord.NewWorkrecordCommand;
import controller.*;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.ComboBox;
import model.User;
import org.apache.logging.log4j.*;

/**
 *
 * @author adrest18
 */
public class ChangeSelectedUserCommand implements ICommand {

    private final MainToolBarViewController mainToolBarViewController;
    private final MainMenuBarViewController mainMenuBarViewController;
    private final ChangeListener<User> selectedUserChangedListener;
    private final ComboBox<User> selectedUserComboBox;
    private User oldSelectedUser;
    private User newSelectedUser;
    private final Logger log = LogManager.getLogger(NewWorkrecordCommand.class.getName());

    public ChangeSelectedUserCommand(ControllerRepository controllerRepository, ChangeListener<User> selectedUserChangedListener, ComboBox<User> selectedUserComboBox, User oldSelectedUser, User newSelectedUser) {
        if(controllerRepository == null) throw new NullPointerException("controllerRepository");
        if(selectedUserChangedListener == null) throw new NullPointerException("selectedUserChangedListener");
        if(selectedUserComboBox == null) throw new NullPointerException("selectedUserComboBox");
        if(oldSelectedUser == null) throw new NullPointerException("oldSelectedUser");
        if(newSelectedUser == null) throw new NullPointerException("newSelectedUser");

        this.mainToolBarViewController = (MainToolBarViewController) controllerRepository.get(MainToolBarViewController.class.getName());
        this.mainMenuBarViewController = (MainMenuBarViewController) controllerRepository.get(MainMenuBarViewController.class.getName());
        
        this.selectedUserChangedListener = selectedUserChangedListener;
        this.selectedUserComboBox = selectedUserComboBox;
        this.oldSelectedUser = oldSelectedUser;
        this.newSelectedUser = newSelectedUser;
    }
    
    @Override
    public boolean execute() {
        selectedUserComboBox.getSelectionModel().selectedItemProperty().removeListener(selectedUserChangedListener);
        selectedUserComboBox.getSelectionModel().select(newSelectedUser);
        selectedUserComboBox.getSelectionModel().selectedItemProperty().addListener(selectedUserChangedListener);
        mainToolBarViewController.toggleUndoRedoButtons();
        mainMenuBarViewController.toggleUndoRedoMenuItems();
        return true;
    }

    @Override
    public boolean undo() {
        selectedUserComboBox.getSelectionModel().selectedItemProperty().removeListener(selectedUserChangedListener);
        selectedUserComboBox.getSelectionModel().select(oldSelectedUser);
        selectedUserComboBox.getSelectionModel().selectedItemProperty().addListener(selectedUserChangedListener);
        mainToolBarViewController.toggleUndoRedoButtons();
        mainMenuBarViewController.toggleUndoRedoMenuItems();
        return true;
    }

    @Override
    public boolean redo() {
        return execute();
    }

    @Override
    public String getText() {
        log.debug("Selected user changed from " + oldSelectedUser + "to " + newSelectedUser);
        return "Selected user changed from " + oldSelectedUser + " to " + newSelectedUser;
    }
    
}
