/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tablecelltemplates;

import java.time.LocalTime;
import javafx.scene.control.TableCell;
import javafx.scene.paint.Color;

/**
 *
 * @author ADREST18
 * @param <T>
 */
public class ReadOnlyRedGreenColoredStringCellByEvaluationOfMinusSign<T> extends TableCell<T, String> {
    
    public ReadOnlyRedGreenColoredStringCellByEvaluationOfMinusSign() {
        
    }
    
    @Override
    public void updateItem(String localTime, boolean isEmpty) {
        super.updateItem(localTime, isEmpty);

        if (localTime != null && !isEmpty) {
            
            setText(localTime);
            
            if (localTime.contains("-")) {
                setTextFill(Color.RED);
            } else if(localTime.equals(LocalTime.MIN.toString())) {
                setTextFill(Color.BLACK);
            } else {
                setTextFill(Color.GREEN);
            }
        }   
    }
    
}
