/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controls;

import javafx.scene.control.TextField;

/**
 *
 * @author adrest18
 */
public class ValidatingVacationCorrectionTextField extends TextField {
    //                             012
    //---------------------------------
    //                             -30
    //                             30
    //---------------------------------
    // [-]?\[0-3][0-9] like -30
    private static Boolean isNegative = false;
    private static String vacationCorrection = "";
     
    public ValidatingVacationCorrectionTextField(String promptText) {
        super();
        this.setPromptText(promptText);
    }

    @Override
    public void replaceText(int start, int end, String text) {
        if(text.isEmpty()) {
            super.replaceText(start, end, text);
            return;
        }

        StringBuilder stringBuilder = new StringBuilder(super.getText());
        stringBuilder.insert(end, text);
        vacationCorrection = stringBuilder.toString();

        isNegative = vacationCorrection.contains("-");

        switch(start) {
            case 0 -> {
                if(text.matches("[-]")) {
                    super.replaceText(start, end, text);
                } else if(text.matches("[0-9]")) {
                    super.replaceText(start, end, text);
                }
            }
            case 1 -> {
                if(text.matches("[0-9]")) {
                    super.replaceText(start, end, text);
                }
            }
            case 2 -> {
                if(isNegative && text.matches("[0-9]")) {
                    super.replaceText(start, end, text);
                }
            }
        }
    }

}
