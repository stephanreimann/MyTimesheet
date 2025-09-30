/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 *
 * @author adrest18
 * @param <T>
 */
public class ObservableStack<T> extends SimpleListProperty<T> {
    private final LinkedList<T> stack;

    public ObservableStack() {
        this.stack = new LinkedList<>();
        this.set(FXCollections.observableList(this.stack));
    }

    public T push(T item) {
        stack.push(item);
        fireValueChangedEvent(new StackChange(this.get(),
                ChangeType.PUSH.setChangedObj(Collections.singletonList(item))));
        return item;
    }

    public T pop() throws NoSuchElementException {
        T temp = stack.pop();
        fireValueChangedEvent(new StackChange(this.get(),
                ChangeType.POP.setChangedObj(Collections.singletonList(temp))));
        return temp;
    }

    @Override
    public boolean add(T element) {
        push(element);
        return true;
    }

    @Override
    public T remove(int i) throws IllegalArgumentException {
        if (0 == i) {
            return pop();
        }
        throw new IllegalArgumentException("Can only modify the top of the stack " + i);
    }

    public boolean removeAll() throws NoSuchElementException {
        this.get().remove(0, getSize());
        return true;
    }

    @Override
    public void add(int i, T element) throws IllegalArgumentException {
        if (0 == i) {
            push(element);
        }
        throw new IllegalArgumentException("Can only modify the top of the stack " + i);
    }

    @Override
    public boolean addAll(Collection<? extends T> elements) throws NullPointerException {
        elements.forEach(stack::push);
        fireValueChangedEvent(new StackChange(this.get(),
                ChangeType.PUSH.setChangedObj(new ArrayList<>(elements))));
        return true;
    }

    @Override
    public boolean addAll(T... elements) {
        return addAll(Arrays.asList(elements));
    }

    @Override
    public boolean addAll(int i, Collection<? extends T> elements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object obj) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Operation not allowed, use pop");
    }

    @Override
    public void remove(int from, int to) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Operation not allowed, use pop");
    }

    @Override
    public boolean removeAll(T... elements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> objects) {
        throw new UnsupportedOperationException();
    }

    private enum ChangeType {
        PUSH, POP;

        private List changedObj;

        public List getChangedObj() {
            return changedObj;
        }

        public ChangeType setChangedObj(List obj) {
            this.changedObj = obj;
            return this;
        }
    }

    private final class StackChange extends ListChangeListener.Change<T> {

        private final ChangeType type;
        private boolean onChange;

        public StackChange(ObservableList<T> list, ChangeType type) {
            super(list);
            this.type = type;
            onChange = false;
        }

        @Override
        public boolean wasAdded() {
            return type == ChangeType.PUSH;
        }

        @Override
        public boolean wasRemoved() {
            return type == ChangeType.POP;
        }

        @Override
        public boolean next() {
            if (onChange) {
                return false;
            }
            onChange = true;
            return true;
        }

        @Override
        public void reset() {
            onChange = false;
        }

        @Override
        public int getFrom() {
            if (!onChange) {
                throw new IllegalStateException(
                        "Invalid Change state: next() must be called before inspecting the Change.");
            }
            return 0;
        }

        @Override
        public int getTo() {
            if (!onChange) {
                throw new IllegalStateException(
                        "Invalid Change state: next() must be called before inspecting the Change.");
            }
            return type.getChangedObj().size();
        }

        @Override
        public List<T> getRemoved() {
            return wasRemoved() ? type.getChangedObj() :
                    Collections.emptyList();
        }

        @Override
        protected int[] getPermutation() {
            return new int[0];
        }
    }

}