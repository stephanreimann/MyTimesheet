/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package utils;

import adapter.Log4jAdapter;
import org.apache.logging.log4j.Logger;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

/**
 *
 * @author adrest18
 */
public class EncryptorTest {
    
    @Test
    public void T00_Ctor_Called_DoesNotThrow_Exception() {
        //Arrange
        //Act
        //Assert
        Encryptor instance = new Encryptor();
    }

    @Test
    public void T10_Called_Encrypt_ReturnsTheEncryptedString() throws Exception {
        //Arrange
        String value = "Password";
        Encryptor instance = new Encryptor();
        String expResult = "YpHHW40Zs4YhoUObkjKukA==";

        //Act
        String result = instance.encrypt(value);

        //Assert
        assertEquals(expResult, result);
    }

    @Test
    public void T20_Called_Decrypt_ReturnsTheEncryptedString() throws Exception {
        //Arrange
        String value = "YpHHW40Zs4YhoUObkjKukA==";
        Encryptor instance = new Encryptor();
        String expResult = "Password";

        //Act
        String result = instance.decrypt(value);

        //Assert
        assertEquals(expResult, result);
    }
    
}
