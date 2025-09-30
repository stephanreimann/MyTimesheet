/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commands;

import controller.*;
import java.util.*;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.mockito.Mockito;
import properties.TranslationStringProperty;
import service.LanguageService;

/**
 *
 * @author adrest18
 */
public class ChangeLanguageCommandTest {
  
    @Test(expected=NullPointerException.class)
    public void T00_Ctor_Called_OldLocale_IsNull_Throws_NullPointerException() {
        //Arrange
        Locale newLocale = new Locale("en", "EN");
        LanguageService languageServiceMock = Mockito.mock(LanguageService.class);
        ControllerRepository controllerRepositoryMock = Mockito.mock(ControllerRepository.class);

        //Act
        //Assert
        ChangeLanguageCommand cmd = new ChangeLanguageCommand(null, newLocale, languageServiceMock, controllerRepositoryMock);        
    }
    
    @Test(expected=NullPointerException.class)
    public void T01_Ctor_Called_NewLocale_IsNull_Throws_NullPointerException() {
        //Arrange
        Locale oldLocale = new Locale("de", "DE");
        LanguageService languageServiceMock = Mockito.mock(LanguageService.class);
        ControllerRepository controllerRepositoryMock = Mockito.mock(ControllerRepository.class);

        //Act
        //Assert
        ChangeLanguageCommand cmd = new ChangeLanguageCommand(oldLocale, null, languageServiceMock, controllerRepositoryMock);        
    }

    @Test(expected=NullPointerException.class)
    public void T02_Ctor_Called_LanguageService_IsNull_Throws_NullPointerException() {
        //Arrange
        Locale oldLocale = new Locale("de", "DE");
        Locale newLocale = new Locale("en", "EN");
        ControllerRepository controllerRepositoryMock = Mockito.mock(ControllerRepository.class);

        //Act
        //Assert
        ChangeLanguageCommand cmd = new ChangeLanguageCommand(oldLocale, newLocale, null, controllerRepositoryMock);        
    }

    @Test(expected=NullPointerException.class)
    public void T03_Ctor_Called_ControllerRepository_IsNull_Throws_NullPointerException() {
        //Arrange
        Locale oldLocale = new Locale("de", "DE");
        Locale newLocale = new Locale("en", "EN");
        LanguageService languageServiceMock = Mockito.mock(LanguageService.class);

        //Act
        //Assert
        ChangeLanguageCommand cmd = new ChangeLanguageCommand(oldLocale, newLocale, languageServiceMock, null);        
    }

    @Test
    public void T04_Ctor_Called_Returns_Instance() {
        //Arrange
        Locale oldLocale = new Locale("de", "DE");
        Locale newLocale = new Locale("en", "EN");
        LanguageService languageServiceMock = Mockito.mock(LanguageService.class);
        ControllerRepository controllerRepositoryMock = Mockito.mock(ControllerRepository.class);

        //Act
        ChangeLanguageCommand cmd = new ChangeLanguageCommand(oldLocale, newLocale, languageServiceMock, controllerRepositoryMock);
        
        //Assert
        assertNotNull(cmd);
    }

    @Test
    public void T05_Execute_Called_Changes_The_Language() {
        //Arrange
        Locale oldLocale = new Locale("de", "DE");
        Locale newLocale = new Locale("en", "EN");

        ResourceBundle newResourceBundle = ResourceBundle.getBundle("languages.bundle", newLocale);
        
        LanguageService languageServiceMock = Mockito.mock(LanguageService.class);
        
        TranslationStringProperty translationStringPropertyMock = Mockito.mock(TranslationStringProperty.class);
        
        MainStatusBarViewController mainStatusBarViewControllerMock = Mockito.mock(MainStatusBarViewController.class);
        mainStatusBarViewControllerMock.stateMessage = translationStringPropertyMock;     
        
        MainToolBarViewController mainToolBarViewControllerMock = Mockito.mock(MainToolBarViewController.class);
        MainMenuBarViewController mainMenuBarViewControllerMock = Mockito.mock(MainMenuBarViewController.class);
        
        ControllerRepository controllerRepositoryMock = Mockito.mock(ControllerRepository.class);
        Mockito.when(controllerRepositoryMock.get(MainStatusBarViewController.class.getName())).thenReturn(mainStatusBarViewControllerMock);
        Mockito.when(controllerRepositoryMock.get(MainToolBarViewController.class.getName())).thenReturn(mainToolBarViewControllerMock);
        Mockito.when(controllerRepositoryMock.get(MainMenuBarViewController.class.getName())).thenReturn(mainMenuBarViewControllerMock);
        
        ChangeLanguageCommand cmd = new ChangeLanguageCommand(oldLocale, newLocale, languageServiceMock, controllerRepositoryMock);

        //Act
        cmd.execute();
        
        //Assert
        Mockito.verify(translationStringPropertyMock).translate(Mockito.anyString(), Mockito.any(), Mockito.any());
        Mockito.verify(languageServiceMock).setResourceBundle(newResourceBundle);
        Mockito.verify(languageServiceMock).updateGuiItems();
        Mockito.verify(mainToolBarViewControllerMock).toggleUndoRedoButtons();
    }
    
    @Test
    public void T06_Undo_Called_Reverts_The_Language_Change() {
        Locale oldLocale = new Locale("de", "DE");
        Locale newLocale = new Locale("en", "EN");

        ResourceBundle newResourceBundle = ResourceBundle.getBundle("languages.bundle", oldLocale);

        LanguageService languageServiceMock = Mockito.mock(LanguageService.class);
        
        TranslationStringProperty translationStringPropertyMock = Mockito.mock(TranslationStringProperty.class);
        
        MainStatusBarViewController mainStatusBarViewControllerMock = Mockito.mock(MainStatusBarViewController.class);
        mainStatusBarViewControllerMock.stateMessage = translationStringPropertyMock;     
        
        MainToolBarViewController mainToolBarViewControllerMock = Mockito.mock(MainToolBarViewController.class);
        MainMenuBarViewController mainMenuBarViewControllerMock = Mockito.mock(MainMenuBarViewController.class);
        
        ControllerRepository controllerRepositoryMock = Mockito.mock(ControllerRepository.class);
        Mockito.when(controllerRepositoryMock.get(MainStatusBarViewController.class.getName())).thenReturn(mainStatusBarViewControllerMock);
        Mockito.when(controllerRepositoryMock.get(MainToolBarViewController.class.getName())).thenReturn(mainToolBarViewControllerMock);
        Mockito.when(controllerRepositoryMock.get(MainMenuBarViewController.class.getName())).thenReturn(mainMenuBarViewControllerMock);
        
        ChangeLanguageCommand cmd = new ChangeLanguageCommand(oldLocale, newLocale, languageServiceMock, controllerRepositoryMock);

        //Act
        cmd.undo();
        
        //Assert
        Mockito.verify(translationStringPropertyMock).translate(Mockito.anyString(), Mockito.any(), Mockito.any());
        Mockito.verify(languageServiceMock).setResourceBundle(newResourceBundle);
        Mockito.verify(languageServiceMock).updateGuiItems();
        Mockito.verify(mainToolBarViewControllerMock).toggleUndoRedoButtons();
    }

    @Test
    public void T07_Redo_Called_Changes_The_Language() {
        //Arrange
        Locale oldLocale = new Locale("de", "DE");
        Locale newLocale = new Locale("en", "EN");

        ResourceBundle newResourceBundle = ResourceBundle.getBundle("languages.bundle", newLocale);
        
        LanguageService languageServiceMock = Mockito.mock(LanguageService.class);
        
        TranslationStringProperty translationStringPropertyMock = Mockito.mock(TranslationStringProperty.class);
        
        MainStatusBarViewController mainStatusBarViewControllerMock = Mockito.mock(MainStatusBarViewController.class);
        mainStatusBarViewControllerMock.stateMessage = translationStringPropertyMock;     
        
        MainToolBarViewController mainToolBarViewControllerMock = Mockito.mock(MainToolBarViewController.class);
        MainMenuBarViewController mainMenuBarViewControllerMock = Mockito.mock(MainMenuBarViewController.class);
        
        ControllerRepository controllerRepositoryMock = Mockito.mock(ControllerRepository.class);
        Mockito.when(controllerRepositoryMock.get(MainStatusBarViewController.class.getName())).thenReturn(mainStatusBarViewControllerMock);
        Mockito.when(controllerRepositoryMock.get(MainToolBarViewController.class.getName())).thenReturn(mainToolBarViewControllerMock);
        Mockito.when(controllerRepositoryMock.get(MainMenuBarViewController.class.getName())).thenReturn(mainMenuBarViewControllerMock);
        
        ChangeLanguageCommand cmd = new ChangeLanguageCommand(oldLocale, newLocale, languageServiceMock, controllerRepositoryMock);

        //Act
        cmd.redo();
        
        //Assert
        Mockito.verify(translationStringPropertyMock).translate(Mockito.anyString(), Mockito.any(), Mockito.any());
        Mockito.verify(languageServiceMock).setResourceBundle(newResourceBundle);
        Mockito.verify(languageServiceMock).updateGuiItems();
        Mockito.verify(mainToolBarViewControllerMock).toggleUndoRedoButtons();
    }

}
