/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commands;

import adapter.Log4jAdapter;
import controller.*;
import java.util.ResourceBundle;
import properties.TranslationStringProperty;

/**
 *
 * @author adrest18
 */
public class ResetFilterCommand implements ICommand {

    private final String resetFilterExecutedResourceKey = "ResetFilterExecuted";
    private final String resetFilterRestoredResourceKey = "ResetFilterRestored";
    private final ResourceBundle resourceBundle;
    private final MainInfoViewController mainInfoViewController;
    private final MainToolBarViewController mainToolBarViewController;
    private final MainMenuBarViewController mainMenuBarViewController;

    private final TranslationStringProperty translationStringProperty;
    
    private final Boolean infoToggleButtonState;
    private final Boolean debugToggleButtonState;
    private final Boolean warningToggleButtonState;
    private final Boolean errorToggleButtonState;
    private final Boolean fatalToggleButtonState;
    
    public ResetFilterCommand(ControllerRepository controllerRepository, Log4jAdapter log4jAdapter) {
        if(controllerRepository == null) throw new NullPointerException("controllerRepository");
        
        this.mainInfoViewController = (MainInfoViewController) controllerRepository.get(MainInfoViewController.class.getName());
        this.mainToolBarViewController = (MainToolBarViewController) controllerRepository.get(MainToolBarViewController.class.getName());
        this.mainMenuBarViewController = (MainMenuBarViewController) controllerRepository.get(MainMenuBarViewController.class.getName());

        this.translationStringProperty = new TranslationStringProperty(log4jAdapter);
        this.resourceBundle = this.mainInfoViewController.getResourceBundle();
        
        this.infoToggleButtonState = this.mainInfoViewController.getInfoToggleButtonState();
        this.debugToggleButtonState = this.mainInfoViewController.getDebugToggleButtonState();
        this.warningToggleButtonState = this.mainInfoViewController.getWarningToggleButtonState();
        this.errorToggleButtonState = this.mainInfoViewController.getErrorToggleButtonState();
        this.fatalToggleButtonState = this.mainInfoViewController.getFatalToggleButtonState();
    }
    
    @Override
    public boolean execute() {
        mainInfoViewController.setInfoToggleButtonState(Boolean.FALSE);
        mainInfoViewController.setDebugToggleButtonState(Boolean.FALSE);
        mainInfoViewController.setWarningToggleButtonState(Boolean.FALSE);
        mainInfoViewController.setErrorToggleButtonState(Boolean.FALSE);
        mainInfoViewController.setFatalToggleButtonState(Boolean.FALSE);
        translationStringProperty.translate(this.resetFilterExecutedResourceKey, this.resourceBundle);
        mainToolBarViewController.toggleUndoRedoButtons();
        mainMenuBarViewController.toggleUndoRedoMenuItems();
        return true;
    }

    @Override
    public boolean undo() {
        mainInfoViewController.setInfoToggleButtonState(infoToggleButtonState);
        mainInfoViewController.setDebugToggleButtonState(debugToggleButtonState);
        mainInfoViewController.setWarningToggleButtonState(warningToggleButtonState);
        mainInfoViewController.setErrorToggleButtonState(errorToggleButtonState);
        mainInfoViewController.setFatalToggleButtonState(fatalToggleButtonState);
        translationStringProperty.translate(this.resetFilterRestoredResourceKey, this.resourceBundle);
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
        return "Reset Filter";
    }
    
}
