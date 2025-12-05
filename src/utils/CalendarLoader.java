/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Holyday;
import net.fortuna.ical4j.data.*;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;

/**
 * !!! IMPORTATNT !!!
 * The loading mechanism ONLY works for files of
 * //www.feiertage-kalender.de
 * 1. Select the year
 * 2. Download file => Feiertage2026.ics
 * @author adrest18
 */
public class CalendarLoader {
    
    private final String parseFormat = "yyyyMMdd";
    private final String dateFormat = "dd.MM.yyyy";
    private final String wordDelimiter = " ";
    private final String enumerationDelimiter = ",";
    private final String token_0 = "Gesetzlicher";
    private final String token_1 = "Feiertag";
    private final String token_2 = "in";
    
    private final ObservableList<Holyday> holydays;
    
    public CalendarLoader() {
        this.holydays = FXCollections.observableArrayList();
    }
    
    public ObservableList<Holyday> getHolydays() {
        return holydays;
    }
    
    public void loadHolydays(String filePathAndName, String state) throws IOException, ParserException {
        if(ControllerUtilities.isNullOrEmpty(state)) {
            throw new IllegalArgumentException("Parameter state must not be null or empty!");
        }
        
        File file = new File(filePathAndName);
        if(!file.exists()) {
            throw new FileNotFoundException(filePathAndName + " does not exist!");
        }
        if(!filePathAndName.contains(".ics")) {
            throw new FileNotFoundException(filePathAndName + " does not ends with *.ics!");
        }
        
        FileInputStream fis = new FileInputStream(file);
        CalendarBuilder builder = new CalendarBuilder();
        Calendar calendar = builder.build(fis);
        loadHolydaysIntoList(calendar, state);
    }
    
    private void loadHolydaysIntoList(Calendar calendar, String state) {
        for (Component component : calendar.getComponents()) {
            String holydayName = null;
            String holydayDate = null;
            String[] holydayDescriptionParts = null;
            
            for (Property property : component.getProperties()) {
                switch(property.getName()) {
                    case "SUMMARY" -> holydayName = property.getValue();
                    case "DTSTART" -> holydayDate = property.getValue();
                    case "DESCRIPTION" -> holydayDescriptionParts = splitHolydayDescriptionToParts(property.getValue());
                }

                if(isPublicHolyday(holydayDescriptionParts) || 
                   isPublicHolydayInState(holydayDescriptionParts, state)) {
                    if(holydayName != null && holydayDate != null) {
                        LocalDate parsedDate = DateConverter.parse(holydayDate, DateTimeFormatter.ofPattern(parseFormat));
                        Holyday holyday = new Holyday(parsedDate, holydayName, state);
                        if(!holydays.contains(holyday)) {
                            holydays.add(holyday);
                        }
                    }
                }
            }
        }
    }
    
    private String[] splitHolydayDescriptionToParts(String holydayDescription) {
        String[] holydayDescriptionParts = holydayDescription.split(wordDelimiter);
        for(int idx = 0; idx < holydayDescriptionParts.length; idx++) {
            if(holydayDescriptionParts[idx].endsWith(enumerationDelimiter)) {
                holydayDescriptionParts[idx] = holydayDescriptionParts[idx].substring(0, holydayDescriptionParts[idx].indexOf(","));
            }
        }
        return holydayDescriptionParts;
    }
    
    private boolean isPublicHolyday(String[] holydayDescriptionParts) {
        //Check if we have only "Gesetzlicher" and "Feiertag" parsed.
        boolean result = false;
        
        if(holydayDescriptionParts != null) {
            boolean r1 = Arrays.stream(holydayDescriptionParts).anyMatch(s -> s.contains(token_0));
            boolean r2 = Arrays.stream(holydayDescriptionParts).anyMatch(s -> s.contains(token_1));
            
            result = r1 && r2;
        }
        
        return result;                
    }

    private boolean isPublicHolydayInState(String[] holydayDescriptionParts, String state) {
        //Check if we have only "Gesetzlicher" and "Feiertag" and "in" and "state" parsed.
        boolean result = false;
        
        if(holydayDescriptionParts != null) {
            boolean r1 = Arrays.stream(holydayDescriptionParts).anyMatch(s -> s.contains(token_0));
            boolean r2 = Arrays.stream(holydayDescriptionParts).anyMatch(s -> s.contains(token_1));
            boolean r3 = Arrays.stream(holydayDescriptionParts).anyMatch(s -> s.contains(token_2));
            boolean r4 = Arrays.stream(holydayDescriptionParts).anyMatch(s -> s.contains(state));

            result = r1 && r2 && r3 && r4;
        }
        
        return result;
    }

}
