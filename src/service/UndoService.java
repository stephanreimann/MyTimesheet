/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import commands.ICommand;
import java.util.Stack;
import javafx.collections.ObservableList;
import utils.ObservableStack;

/**
 * Undo/Redo implementation.
 *
 * @author stephan
 */
public class UndoService {

    private final Stack<ICommand> undoStack;
    private final ObservableStack<String> undoStackComments;
    private final Stack<ICommand> redoStack;
    private final ObservableStack<String> redoStackComments;

    public UndoService() {
        this.undoStack = new Stack<>();
        this.undoStackComments = new ObservableStack();
        this.redoStack = new Stack<>();
        this.redoStackComments = new ObservableStack();
    }

    public boolean execute(final ICommand cmd) {
        if (!redoStack.isEmpty()) {
            redoStack.clear();
            redoStackComments.clear();
        }
        undoStack.push(cmd);
        undoStackComments.push(cmd.getText());
        return cmd.execute();
    }

    public boolean undo() {
        if (!undoStack.isEmpty()) {
            ICommand cmd = undoStack.pop();
            undoStackComments.pop();
            redoStack.push(cmd);
            redoStackComments.push(cmd.getText());
            return cmd.undo();
        }
        return false;
    }

    public boolean redo() {
        if (!redoStack.isEmpty()) {
            ICommand cmd = redoStack.pop();
            redoStackComments.pop();
            undoStack.push(cmd);
            undoStackComments.push(cmd.getText());
            return cmd.redo();
        }
        return false;
    }

    public void clearUndoRedoStacks() {
        undoStack.clear();
        undoStackComments.clear();
        redoStack.clear();
        redoStackComments.clear();
    }

    public Boolean isUndoStackEmpty() {
        return undoStack.isEmpty();
    }

    public Boolean isRedoStackEmpty() {
        return redoStack.isEmpty();
    }

    public ObservableList<String> getUndoStackComments() {
        return undoStackComments;
    }
    
    public ObservableList<String> getRedoStackComments() {
        return redoStackComments;
    }

}
