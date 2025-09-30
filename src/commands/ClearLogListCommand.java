/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commands;

import adapter.Log4jAdapter;
import controller.*;
import java.util.ResourceBundle;
import javafx.collections.*;
import properties.TranslationStringProperty;

/**
 *
 * @author adrest18
 */
public class ClearLogListCommand implements ICommand {

    private final String infoViewClearedResourceKey = "InfoViewCleared";
    private final String infoViewRestoredResourceKey ="InfoViewRestored";

    private final ObservableList<String> oldInfoViewEntries = FXCollections.observableArrayList(); 
    private final ObservableList<String> infoViewEntries;
    private final TranslationStringProperty translationStringProperty;
    private final ResourceBundle resourceBundle;
    private final MainToolBarViewController mainToolBarViewController;
    private final MainMenuBarViewController mainMenuBarViewController;
    private final MainInfoViewController mainInfoViewController;
    
    public ClearLogListCommand(ControllerRepository controllerRepository, ObservableList<String> infoViewEntries, Log4jAdapter log4jAdapter) {
        if(controllerRepository == null) throw new NullPointerException("controllerRepository");
        if(infoViewEntries == null) throw new NullPointerException("infoViewEntries");
        if(log4jAdapter == null) throw new NullPointerException("log4jAdapter");
        
        this.mainToolBarViewController = (MainToolBarViewController) controllerRepository.get(MainToolBarViewController.class.getName());
        this.mainInfoViewController = (MainInfoViewController) controllerRepository.get(MainInfoViewController.class.getName());
        this.mainMenuBarViewController = (MainMenuBarViewController) controllerRepository.get(MainMenuBarViewController.class.getName());
        
        this.infoViewEntries = infoViewEntries;
        this.translationStringProperty = new TranslationStringProperty(log4jAdapter);
        this.resourceBundle = this.mainInfoViewController.getResourceBundle();
    }
    
    @Override
    public boolean execute() {
        oldInfoViewEntries.clear();
        oldInfoViewEntries.addAll(this.infoViewEntries);
        infoViewEntries.clear();
        translationStringProperty.translate(this.infoViewClearedResourceKey, this.resourceBundle);
        mainToolBarViewController.toggleUndoRedoButtons();
        mainMenuBarViewController.toggleUndoRedoMenuItems();
        return true;
    }

    @Override
    public boolean undo() {
        infoViewEntries.clear();
        infoViewEntries.addAll(this.oldInfoViewEntries);
        translationStringProperty.translate(this.infoViewRestoredResourceKey, this.resourceBundle);
        oldInfoViewEntries.clear();
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
        return "Clear Loglist";
    }
    
}
