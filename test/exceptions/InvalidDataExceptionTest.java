package exceptions;

import org.junit.Test;

import static org.junit.Assert.*;

public class InvalidDataExceptionTest {

    @Test
    public void testDefaultConstructor() {
        InvalidDataException ex = new InvalidDataException();
        assertNotNull(ex);
        assertNull(ex.getMessage());
        assertNull(ex.getCause());
        assertTrue(ex instanceof Exception);
    }

    @Test
    public void testMessageConstructor() {
        String message = "Invalid data provided";
        InvalidDataException ex = new InvalidDataException(message);
        
        assertNotNull(ex);
        assertEquals(message, ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    public void testCauseConstructor() {
        Throwable cause = new IllegalArgumentException("Root cause");
        InvalidDataException ex = new InvalidDataException(cause);
        
        assertNotNull(ex);
        assertEquals(cause, ex.getCause());
        // The message usually incorporates the cause's toString() or is null depending on super implementation
    }

    @Test
    public void testMessageAndCauseConstructor() {
        String message = "Complex failure";
        Throwable cause = new NullPointerException("Null reference");
        InvalidDataException ex = new InvalidDataException(message, cause);
        
        assertNotNull(ex);
        assertEquals(message, ex.getMessage());
        assertEquals(cause, ex.getCause());
    }
}