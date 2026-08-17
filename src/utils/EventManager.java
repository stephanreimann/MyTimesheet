/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author stephan
 */
public class EventManager implements IEventManager {
    
    private final Map<String, List<IEventListener>> listeners;

    public EventManager() {
        listeners = new HashMap<>();
    }

    @Override
    public void registerEventType(String eventType) {
        if(this.listeners.containsKey(eventType)) {
            throw new IllegalArgumentException("EventManager has allready a registration for Event: " + eventType);
        }
        this.listeners.put(eventType, new ArrayList<>());
    }
    
    @Override
    public void subscribeEventToListener(String eventType, IEventListener listener) {
        List<IEventListener> eventListeners = listeners.get(eventType);
        if(eventListeners == null) {
            throw new IllegalArgumentException("No listener registered for Event: " + eventType);
        }
        if(!eventListeners.contains(listener)) {
            eventListeners.add(listener);
        }
    }

    @Override
    public void unsubscribeEventFromListener(String eventType, IEventListener listener) {
        List<IEventListener> eventListeners = listeners.get(eventType);
        if(eventListeners == null) {
            throw new IllegalArgumentException("No listener registered for Event: " + eventType);
        }
        if(eventListeners.contains(listener)) {
            eventListeners.remove(listener);
        }
    }

    @Override
    public void notifyListenerOfEvent(String eventType, Object source) {
        List<IEventListener> eventListeners = listeners.get(eventType);
        for (IEventListener listener : eventListeners) {
            if(listener != null) {
                listener.update(eventType, source);
            }
        }
    }
    
}
