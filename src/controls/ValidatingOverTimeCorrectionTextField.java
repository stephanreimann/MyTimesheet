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
public class ValidatingOverTimeCorrectionTextField extends TextField {
    //                             0123456
    //-------------------------------------
    //                             -999:59
    //                             999:59
    //-------------------------------------
    private static Boolean isNegative = false;
    private static String overtimeCorrection = "";
    private static String hourPart = "";
    private static String minutePart = "";
     
    public ValidatingOverTimeCorrectionTextField(String promptText) {
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
        overtimeCorrection = stringBuilder.toString();

        isNegative = overtimeCorrection.contains("-");

        String[] tokens = overtimeCorrection.split(":");
        switch(tokens.length) {
            case 1 -> {
                hourPart = tokens[0];
                if(isNegative && hourPart.length() >= 5) {
                    return;
                }
                if(!isNegative && hourPart.length() >= 4) {
                    return;
                }
            }
            case 2 -> {
                hourPart = tokens[0];
                if(isNegative && hourPart.length() >= 5) {
                    return;
                }
                if(!isNegative && hourPart.length() >= 4) {
                    return;
                }
                minutePart = tokens[1];
                if(minutePart.length() >= 3) {
                    return;
                }
            }
            default -> {
                return;
            }
        }

        switch(start) {
            case 0 -> {
                if(text.matches("[-]")) {
                    super.replaceText(start, end, text);
                } else if(text.matches("[0-9]")) {
                    super.replaceText(start, end, text);
                }
            }
            case 1, 2 -> {
                if(text.matches("[0-9]")) {
                    super.replaceText(start, end, text);
                }
            }
            case 3 -> {
                if(isNegative) {
                    if(text.matches("[0-9]")) {
                        super.replaceText(start, end, text);
                    }
                } else {
                    if(text.matches("[:]")) {
                        super.replaceText(start, end, text);
                    }
                }
            }
            case 4 -> {
                if(isNegative) {
                    if(text.matches("[:]")) {
                        super.replaceText(start, end, text);
                    }
                } else {
                    if(text.matches("[0-5]")) {
                        super.replaceText(start, end, text);
                    }
                }
            }
            case 5 -> {
                if(isNegative) {
                    if(text.matches("[0-5]")) {
                        super.replaceText(start, end, text);
                    }
                } else {
                    if(text.matches("[0-9]")) {
                        super.replaceText(start, end, text);
                    }
                }
            }
            case 6 -> {
                if(isNegative) {
                    if(text.matches("[0-9]")) {
                        super.replaceText(start, end, text);
                    }
                }
            }
        }
    }

}
