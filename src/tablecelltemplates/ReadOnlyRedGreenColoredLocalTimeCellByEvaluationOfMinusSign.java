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
public class ReadOnlyRedGreenColoredLocalTimeCellByEvaluationOfMinusSign<T> extends TableCell<T, LocalTime> {
    
    public ReadOnlyRedGreenColoredLocalTimeCellByEvaluationOfMinusSign() {
        
    }
    
    @Override
    public void updateItem(LocalTime localTime, boolean isEmpty) {
        super.updateItem(localTime, isEmpty);

        if (localTime != null && !isEmpty) {
            String localTimeAsString = localTime.toString();
            
            setText(localTimeAsString);
            
            if (localTimeAsString.contains("-")) {
                setTextFill(Color.RED);
            } else if(localTime.equals(LocalTime.MIN)) {
                setTextFill(Color.BLACK);
            } else {
                setTextFill(Color.GREEN);
            }
        }   
    }
    
}
