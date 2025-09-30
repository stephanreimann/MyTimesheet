/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package exceptions;

/**
 *
 * @author adrest18
 */
public class InvalidDataException extends Exception {
    
    public InvalidDataException () {

    }

    public InvalidDataException (String message) {
        super (message);
    }

    public InvalidDataException (Throwable cause) {
        super (cause);
    }

    public InvalidDataException (String message, Throwable cause) {
        super (message, cause);
    }
    
}
