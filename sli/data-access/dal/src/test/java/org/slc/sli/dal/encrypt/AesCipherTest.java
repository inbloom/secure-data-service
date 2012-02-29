package org.slc.sli.dal.encrypt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests the AesCipher class
 * 
 * @author Ryan Farris <rfarris@wgen.net>
 * 
 */
@RunWith(JUnit4.class)
public class AesCipherTest {
    
    AesCipher aes;
    
    @Before
    public void init() throws NoSuchAlgorithmException {
        SecretKey key = KeyGenerator.getInstance("AES").generateKey();
        aes = new AesCipher(key);
    }
    
    @Test
    public void testString() {
        String result = aes.encrypt("Hello world!");
        assertFalse("Hello world!".equals(result));
        assertTrue(result.startsWith("ESTRING:"));
        Object result2 = aes.decrypt(result);
        assertEquals("Hello world!", result2);
    }
    
    @Test
    public void testBoolean() {
        String result = aes.encrypt(true);
        assertFalse(result.contains("true"));
        assertTrue(result.startsWith("EBOOL:"));
        assertEquals(true, aes.decrypt(result));
    }
    
    @Test
    public void testInteger() {
        String encrypted = aes.encrypt(1023);
        assertFalse("1023".equals(encrypted));
        assertTrue(encrypted.startsWith("EINT:"));
        assertEquals(new Integer(1023), aes.decrypt(encrypted));
    }
    
    @Test
    public void testLong() {
        String encrypted = aes.encrypt(1000001L);
        assertTrue(encrypted.startsWith("ELONG:"));
        assertEquals(new Long(1000001), aes.decrypt(encrypted));
    }
    
    @Test
    public void testDouble() {
        String encrypted = aes.encrypt(1.000001D);
        assertTrue(encrypted.startsWith("EDOUBLE:"));
        assertEquals(new Double(1.000001D), aes.decrypt(encrypted));
    }
    
    @Test(expected = RuntimeException.class)
    public void testUnhandled() {
        aes.encrypt(1.1F);
    }
    
    @Test()
    public void testUnencryptedString() {
        Object decrypted = aes.decrypt("Some text");
        assertEquals(null, decrypted);
        Object d2 = aes.decrypt("SOME DATA WITH : IN IT");
        assertEquals(null, d2);
    }
}
