/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import java.time.Duration;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;

/**
 *
 * @author adrest18
 */
public class ValueStyler {

    private static final String COLOR_LIGHT_RED = "Red";
    private static final String COLOR_LIGHT_GREEN = "Green";

    public static void styleSpinner(Spinner spinner, String value) {
        Integer intValue = Integer.valueOf(value);
        switch(Long.signum(intValue)) {
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
    
    public static void styleTimeLabel(TextField textField, Duration duration) {
        if(duration.isNegative() && !duration.equals(Duration.ofHours(-24))) {
            textField.setStyle("-fx-text-fill: " + COLOR_LIGHT_RED + ";");
        } else if(duration.isPositive()) {
            textField.setStyle("-fx-text-fill:" + COLOR_LIGHT_GREEN + ";");
        } else {
            textField.setStyle("");
        }
    }

    public static void styleTimeLabel(TextField textField, String duration) {
        if (duration.contains("-")) {
            textField.setStyle("-fx-text-fill: " + COLOR_LIGHT_RED + ";");
        } else if(duration.equals("00:00")) {
            textField.setStyle("");
        } else {
            textField.setStyle("-fx-text-fill:" + COLOR_LIGHT_GREEN + ";");
        }
    }

}
