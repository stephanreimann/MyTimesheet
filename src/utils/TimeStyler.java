/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;

/**
 *
 * @author adrest18
 */
public class TimeStyler {

    private static final String COLOR_LIGHT_RED = "Red";
    private static final String COLOR_LIGHT_GREEN = "Green";
    private static final String COLOR_LIGHT_ORANGE = "Orange";

    public static void styleSpinner(Spinner spinner, String value) {
        switch(Long.signum(Long.parseLong(value))) {
            case -1 -> spinner.getEditor().setStyle("-fx-text-fill: " + COLOR_LIGHT_RED + ";");
            case 0 -> spinner.getEditor().setStyle("");
            case 1 -> spinner.getEditor().setStyle("-fx-text-fill:" + COLOR_LIGHT_GREEN + ";");
        }
    }

    public static void styleTimeLabel(Label label, Long value) {
        switch(Long.signum(value)) {
            case -1 -> label.setStyle("-fx-text-fill: " + COLOR_LIGHT_RED + ";");
            case 0 -> label.setStyle("");
            case 1 -> label.setStyle("-fx-text-fill:" + COLOR_LIGHT_GREEN + ";");
        }
    }

    public static void styleTimeLabel(Label label, Long value, Long threshold) {
        if(value > threshold) {
            label.setStyle("-fx-text-fill: " + COLOR_LIGHT_RED + ";");
        } else {
            switch(Long.signum(value)) {
                case -1 -> label.setStyle("-fx-text-fill: " + COLOR_LIGHT_RED + ";");
                case 0 -> label.setStyle("");
                case 1 -> label.setStyle("-fx-text-fill:" + COLOR_LIGHT_GREEN + ";");
            }
        }
    }

    public static void styleTimeLabel(Label label, Long value, Long upperThreshold, Long lowerThreshold) {
        if(value < lowerThreshold | value > upperThreshold) {
            label.setStyle("-fx-text-fill: " + COLOR_LIGHT_RED + ";");
        } else if(value >= lowerThreshold && value <= upperThreshold) {
            label.setStyle("-fx-text-fill: " + COLOR_LIGHT_GREEN + ";");
        } else {
            label.setStyle("");
        }
    }
    
    public static void styleTimeLabel(Label label, Duration duration) {
        if(duration.isNegative() && !duration.equals(Duration.ofHours(-24))) {
            label.setStyle("-fx-text-fill: " + COLOR_LIGHT_RED + ";");
        } else if(duration.isPositive()) {
            label.setStyle("-fx-text-fill:" + COLOR_LIGHT_GREEN + ";");
        } else {
            label.setStyle("");
        }
    }

    public static void styleTimeLabel(Label label, String duration) {
        if (duration.contains("-")) {
            label.setStyle("-fx-text-fill: " + COLOR_LIGHT_RED + ";");
        } else if(duration.equals("00:00")) {
            label.setStyle("");
        } else {
            label.setStyle("-fx-text-fill:" + COLOR_LIGHT_GREEN + ";");
        }
    }
    
    public static void styleTimeLabel(TextField textField, Duration duration, Pattern syntaxPattern) {
        boolean matchResult = true;
        if(syntaxPattern != null) {
            Matcher matcher = syntaxPattern.matcher(duration.toString());
            matchResult = matcher.matches();
        }

        if(duration.isNegative() && !duration.equals(Duration.ofHours(-24))) {
            textField.setStyle("-fx-text-fill: " + COLOR_LIGHT_RED + ";");
        } else if(duration.isPositive()) {
            textField.setStyle("-fx-text-fill:" + COLOR_LIGHT_GREEN + ";");
        }  else if(!matchResult) {
            textField.setStyle("-fx-text-fill: " + COLOR_LIGHT_ORANGE + ";");
        } else { 
            textField.setStyle("");
        }
    }

    public static void styleTimeLabel(TextField textField, String duration, Pattern syntaxPattern) {
        boolean matchResult = true;
        if(syntaxPattern != null) {
            Matcher matcher = syntaxPattern.matcher(duration);
            matchResult = matcher.matches();
        }
        
        if (duration.contains("-")) {
            textField.setStyle("-fx-text-fill: " + COLOR_LIGHT_RED + ";");
        } else if(duration.equals("00:00")) {
            textField.setStyle("");
        } else if(!matchResult) {
            textField.setStyle("-fx-text-fill: " + COLOR_LIGHT_ORANGE + ";");
        } else {
            textField.setStyle("-fx-text-fill:" + COLOR_LIGHT_GREEN + ";");
        }
    }

}
