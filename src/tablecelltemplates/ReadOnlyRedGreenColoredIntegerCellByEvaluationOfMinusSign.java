/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tablecelltemplates;

import javafx.scene.control.TableCell;
import javafx.scene.paint.Color;

/**
 *
 * @author ADREST18
 * @param <T>
 */
public class ReadOnlyRedGreenColoredIntegerCellByEvaluationOfMinusSign<T> extends TableCell<T, Integer> {
    
    public ReadOnlyRedGreenColoredIntegerCellByEvaluationOfMinusSign() {
        
    }
    
    @Override
    public void updateItem(Integer value, boolean isEmpty) {
        super.updateItem(value, isEmpty);

        if (value != null) {
            
            setText(value.toString());
            
            if (value < 0L) {
                setTextFill(Color.RED);
            } else if(value == 0L) {
                setTextFill(Color.BLACK);
            } else {
                setTextFill(Color.GREEN);
            }
        }   
    }
    
}
