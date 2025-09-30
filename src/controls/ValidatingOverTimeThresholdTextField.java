/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controls;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.control.TextField;

/**
 *
 * @author adrest18
 */
public class ValidatingOverTimeThresholdTextField extends TextField {
    
    private final Pattern pattern1 = Pattern.compile("[0-9]");
    private final Pattern pattern2 = Pattern.compile("[0-9][:]");
    private final Pattern pattern3 = Pattern.compile("[0-9][:][0-5]");
    private final Pattern pattern4 = Pattern.compile("[0-9][:][0-5][0-9]");
    private final Pattern pattern5 = Pattern.compile("[0-9][0-9]");
    private final Pattern pattern6 = Pattern.compile("[0-9][0-9][:]");
    private final Pattern pattern7 = Pattern.compile("[0-9][0-9][:][0-5]");
    private final Pattern pattern8 = Pattern.compile("[0-9][0-9][:][0-5][0-9]");
    
    public ValidatingOverTimeThresholdTextField(String promptText) {
        super();
        this.setPromptText(promptText);
    }
    
    @Override
    public void replaceText(int start, int end, String text) {
        String text2 = this.getText()+text;
        if( compare(text2) || start != end) {
            super.replaceText( start, end, text );
        }
    }
    
    public boolean compare(String text) {
        Matcher match = pattern1.matcher(text);
        if(match.matches()) return true;
        match = pattern2.matcher(text);
        if(match.matches()) return true;
        match = pattern3.matcher(text);
        if(match.matches()) return true;
        match = pattern4.matcher(text);
        if(match.matches()) return true;
        match = pattern5.matcher(text);
        if(match.matches()) return true;
        match = pattern6.matcher(text);
        if(match.matches()) return true;
        match = pattern7.matcher(text);
        if(match.matches()) return true;
        match = pattern8.matcher(text);
        return match.matches();
    }
    
}
