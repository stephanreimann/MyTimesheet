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
public class ValidatingDeltaTimeTextField extends TextField {
    private final Pattern pattern1 = Pattern.compile("^-");
    private final Pattern pattern2 = Pattern.compile("^-[0-9]");
    private final Pattern pattern3 = Pattern.compile("^-[0-9][:]");
    private final Pattern pattern4 = Pattern.compile("^-[0-9][:][0-5]");
    private final Pattern pattern5 = Pattern.compile("^-[0-9][:][0-5][0-9]");
    private final Pattern pattern6 = Pattern.compile("^-[0-9][0-9]");
    private final Pattern pattern7 = Pattern.compile("^-[0-9][0-9][:]");
    private final Pattern pattern8 = Pattern.compile("^-[0-9][0-9][:][0-5]");
    private final Pattern pattern9 = Pattern.compile("^-[0-9][0-9][:][0-5][0-9]");
    private final Pattern pattern12 = Pattern.compile("[0-9]");
    private final Pattern pattern13 = Pattern.compile("[0-9][:]");
    private final Pattern pattern14 = Pattern.compile("[0-9][:][0-5]");
    private final Pattern pattern15 = Pattern.compile("[0-9][:][0-5][0-9]");
    private final Pattern pattern16 = Pattern.compile("[0-9][0-9]");
    private final Pattern pattern17 = Pattern.compile("[0-9][0-9][:]");
    private final Pattern pattern18 = Pattern.compile("[0-9][0-9][:][0-5]");
    private final Pattern pattern19 = Pattern.compile("[0-9][0-9][:][0-5][0-9]");
    
    public ValidatingDeltaTimeTextField(String promptText) {
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
        if(match.matches()) return true;
        match = pattern9.matcher(text);

        if(match.matches()) return true;
        match = pattern12.matcher(text);
        if(match.matches()) return true;
        match = pattern13.matcher(text);
        if(match.matches()) return true;
        match = pattern14.matcher(text);
        if(match.matches()) return true;
        match = pattern15.matcher(text);
        if(match.matches()) return true;
        match = pattern16.matcher(text);
        if(match.matches()) return true;
        match = pattern17.matcher(text);
        if(match.matches()) return true;
        match = pattern18.matcher(text);
        if(match.matches()) return true;
        match = pattern19.matcher(text);

        return match.matches();
    }
    
}
