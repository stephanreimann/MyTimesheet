/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package commands.workrecorddetails;

import commands.ICommand;

/**
 *
 * @author adrest18
 */
public class ChangeEndDateCommand implements ICommand {

    @Override
    public boolean execute() {
        return false;
    }

    @Override
    public boolean undo() {
        return false;
    }

    @Override
    public boolean redo() {
        return false;
    }

    @Override
    public String getText() {
        return "Change Enddate";
    }
    
}
