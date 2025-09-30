/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import java.time.Duration;
import javafx.scene.control.Spinner;

/**
 *
 * @author adrest18
 */
public class DurationStyler {
    
    private static final String COLOR_LIGHT_RED = "Red";
    private static final String COLOR_LIGHT_GREEN = "Green";

    public static void styleSpinner(Spinner spinner, Duration value) {
        if(value.isNegative()) {
            spinner.getEditor().setStyle("-fx-text-fill: " + COLOR_LIGHT_RED + ";");
            return;
        }
        if(value.isPositive()) {
            spinner.getEditor().setStyle("-fx-text-fill:" + COLOR_LIGHT_GREEN + ";");
            return;
        }
        if(value.isZero()) {
            spinner.getEditor().setStyle("");
        }
    }

}
