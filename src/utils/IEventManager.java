/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package utils;

/**
 *
 * @author adrest18
 */
public interface IEventManager {
    
    public void registerEventType(String eventType);
    public void subscribeEventToListener(String eventType, IEventListener listener);
    public void unsubscribeEventFromListener(String eventType, IEventListener listener);
    public void notifyListenerOfEvent(String eventType, Object source);
    
}
