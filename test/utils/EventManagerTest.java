/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class EventManagerTest {

    private EventManager eventManager;
    private TestEventListener listener1;
    private TestEventListener listener2;

    @Before
    public void setUp() {
        eventManager = new EventManager();
        listener1 = new TestEventListener();
        listener2 = new TestEventListener();
    }

    // --- Helper class to record update calls without Mockito ---
    private static class TestEventListener implements IEventListener {
        private String lastEventType;
        private Object lastSource;
        private int updateCount = 0;

        @Override
        public void update(String eventType, Object source) {
            this.lastEventType = eventType;
            this.lastSource = source;
            this.updateCount++;
        }

        public int getUpdateCount() {
            return updateCount;
        }

        public String getLastEventType() {
            return lastEventType;
        }

        public Object getLastSource() {
            return lastSource;
        }
    }

    @Test
    public void testRegisterEventType_NewType_Success() {
        eventManager.registerEventType("USER_LOGIN");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterEventType_DuplicateType_ThrowsIllegalArgumentException() {
        eventManager.registerEventType("USER_LOGIN");
        eventManager.registerEventType("USER_LOGIN");
    }

    @Test
    public void testSubscribeEventToListener_ValidEventType_Success() {
        String eventType = "DATA_UPDATED";
        eventManager.registerEventType(eventType);

        eventManager.subscribeEventToListener(eventType, listener1);
        
        eventManager.notifyListenerOfEvent(eventType, "test-source");
        
        assertEquals(1, listener1.getUpdateCount());
        assertEquals(eventType, listener1.getLastEventType());
        assertEquals("test-source", listener1.getLastSource());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSubscribeEventToListener_UnregisteredEventType_ThrowsIllegalArgumentException() {
        eventManager.subscribeEventToListener("UNKNOWN_EVENT", listener1);
    }

    @Test
    public void testUnsubscribeEventFromListener_SubscribedListener_RemovesSuccessfully() {
        String eventType = "FILE_SAVED";
        eventManager.registerEventType(eventType);

        eventManager.subscribeEventToListener(eventType, listener1);
        eventManager.unsubscribeEventFromListener(eventType, listener1);

        eventManager.notifyListenerOfEvent(eventType, "source");
        assertEquals(0, listener1.getUpdateCount());
    }

    @Test
    public void testNotifyListenerOfEvent_MultipleListeners_NotifiesAll() {
        String eventType = "CONFIG_CHANGED";
        eventManager.registerEventType(eventType);

        eventManager.subscribeEventToListener(eventType, listener1);
        eventManager.subscribeEventToListener(eventType, listener2);

        Object source = new Object();
        eventManager.notifyListenerOfEvent(eventType, source);

        assertEquals(1, listener1.getUpdateCount());
        assertEquals(1, listener2.getUpdateCount());
    }

    @Test
    public void testNotifyListenerOfEvent_UnregisteredEventType_DoesNothing() {
        eventManager.notifyListenerOfEvent("NON_EXISTENT_EVENT", "source");
        assertEquals(0, listener1.getUpdateCount());
    }
}