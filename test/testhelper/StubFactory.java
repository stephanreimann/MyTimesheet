/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package testhelper;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;
import service.LanguageService;

/**
 *
 * @author adrest18
 */
public class StubFactory {
    
    public LanguageService createLanguageServiceStub() {
        return new LanguageService() {
            @Override
            public void updateGuiItems() {
                // No-op stub for testing
            }
        };
    }
    
    public Connection createConnectionProxyStub() {
        return (Connection) Proxy.newProxyInstance(
                Connection.class.getClassLoader(),
                new Class<?>[]{Connection.class},
                (proxy, method, args) -> null
        );
    }
    
    public ResourceBundle createResourceBundleStub() {
        return new ResourceBundle() {
            @Override
            protected Object handleGetObject(String key) {
                return switch (key) {
                    case "languageSelectorButton" -> "Sprache";
                    case "LanguageDE" -> "Deutsch";
                    case "LanguageEN" -> "Englisch";
                    case "LanguageES" -> "Spanisch";
                    case "LanguageFR" -> "Französisch";
                    case "LanguageIT" -> "Italienisch";
                    case "Undo" -> "Rückgängig";
                    case "Redo" -> "Wiederholen";
                    case "WorkItemToolTip" -> "Arbeitselement";
                    case "WorkItemViewTitle" -> "Arbeitselement-Ansicht";
                    default -> key;
                };
            }

            @Override
            public Enumeration<String> getKeys() {
                return Collections.emptyEnumeration();
            }

            @Override
            public Locale getLocale() {
                return Locale.GERMAN;
            }
        };
    }
    
}
