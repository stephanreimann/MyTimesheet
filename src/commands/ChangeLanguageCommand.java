/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commands;

import controller.*;
import java.util.*;
import service.*;

public class ChangeLanguageCommand implements ICommand {

    private final Locale oldValue;
    private final Locale newValue;
    
    private final LanguageService languageService;
    
    private final ControllerRepository controllerRepository;
    private final MainStatusBarViewController mainStatusBarViewController;
    private final MainToolBarViewController mainToolBarViewController;
    private final MainMenuBarViewController mainMenuBarViewController;
    
    public ChangeLanguageCommand(Locale oldValue, Locale newValue, LanguageService languageService, ControllerRepository controllerRepository) {        
        if(oldValue == null) throw new NullPointerException("oldValue");
        if(newValue == null) throw new NullPointerException("newValue");
        if(languageService == null) throw new NullPointerException("languageService");
        if(controllerRepository == null) throw new NullPointerException("controllerRepository");

        this.oldValue = oldValue;
        this.newValue = newValue;
        this.languageService = languageService;
        this.controllerRepository = controllerRepository;
        
        this.mainStatusBarViewController = (MainStatusBarViewController)this.controllerRepository.get(MainStatusBarViewController.class.getName());
        this.mainToolBarViewController = (MainToolBarViewController)this.controllerRepository.get(MainToolBarViewController.class.getName());
        this.mainMenuBarViewController = (MainMenuBarViewController)this.controllerRepository.get(MainMenuBarViewController.class.getName());
    }

    @Override
    public boolean execute() {
        return changeLanguage(this.newValue, this.oldValue);
    }

    @Override
    public boolean undo() {
        return changeLanguage(this.oldValue, this.newValue);
    }

    @Override
    public boolean redo() {
        return execute();
    }

    private boolean changeLanguage(Locale newValue, Locale oldValue) {
        Object[] args = new Object[]{
            oldValue.getLanguage(),
            newValue.getLanguage()
        };
        
        ResourceBundle bundle = ResourceBundle.getBundle("languages.bundle", newValue);
        mainStatusBarViewController.stateMessage.translate("LanguageChanged", args, bundle);
        Locale.setDefault(newValue);
        languageService.setResourceBundle(bundle);
        languageService.updateGuiItems();
        mainToolBarViewController.toggleUndoRedoButtons();
        mainMenuBarViewController.toggleUndoRedoMenuItems();
        return true;
    }

    @Override
    public String getText() {
        return "Change Language " + oldValue.getDisplayLanguage() + " -> " + newValue.getDisplayLanguage();
    }

}
