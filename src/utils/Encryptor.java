/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import adapter.Log4jAdapter;
import controller.UserViewController;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.Base64;
import javax.crypto.*;
import javax.crypto.spec.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author adrest18
 */
public class Encryptor {
    
    private final String key = "Bar12345Bar12345";
    private final String initVector = "RandomInitVector";
    private final String mode = "AES/CBC/PKCS5PADDING";
    private final String type = "AES";
    private final String byteMode = "UTF-8";
    
    private IvParameterSpec iv = null;
    private SecretKeySpec skeySpec = null;
    private Cipher cipher = null;
    private final Logger log = LogManager.getLogger(Encryptor.class.getName());

    public Encryptor() {
        initializeEncryptor();
    }
    
    private void initializeEncryptor() {
        try {
            this.iv = new IvParameterSpec(initVector.getBytes(byteMode));
            this.skeySpec = new SecretKeySpec(key.getBytes(byteMode), type);
            this.cipher = Cipher.getInstance(mode);            
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException  ex) {
            log.fatal(ex.getLocalizedMessage());
        }
    }
    
    public String encrypt(String value) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(value.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (BadPaddingException | IllegalBlockSizeException | InvalidKeyException | InvalidAlgorithmParameterException  ex) {
            log.fatal(ex.getLocalizedMessage());
            return null;
        }
    }

    public String decrypt(String value) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(Base64.getDecoder().decode(value));
            return new String(original);
        } catch (BadPaddingException | IllegalBlockSizeException | InvalidKeyException | InvalidAlgorithmParameterException  ex) {
            log.fatal(ex.getLocalizedMessage());
            return null;
        }
    }
    
}
