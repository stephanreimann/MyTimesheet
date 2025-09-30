/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commands;

/**
 *
 * @author stephan
 */
public interface ICommand {

    public abstract boolean execute();
    public abstract boolean undo();
    public abstract boolean redo();
    public abstract String getText();
    
}
