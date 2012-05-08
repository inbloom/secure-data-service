package org.slc.sli.dal.encrypt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.security.NoSuchAlgorithmException;
import java.util.Date;

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

    AesCipher validAes;

    @Before
    public void init() {
        validAes = buildCipher("aabbccddeeff11223344556677889900");
    }

    private AesCipher buildCipher(String initVector) {
        AesCipher aes = new AesCipher();
        aes.setSecretKeyProvider(new MockSecretKeyProvider());
        aes.setInitializationVector(initVector);
        aes.setKeyAlias("keyAlias");
        aes.setKeyPass("keyPass");
        aes.setKeyStore("keyStore");
        aes.setKeyStorePass("keyStorePass");
        aes.init();
        return aes;
    }

    @Test
    public void testString() {
        String result = validAes.encrypt("Hello world!");
        assertFalse("Hello world!".equals(result));
        assertTrue(result.startsWith("ESTRING:"));
        Object result2 = validAes.decrypt(result);
        assertEquals("Hello world!", result2);
    }

    @Test
    public void testBoolean() {
        String result = validAes.encrypt(true);
        assertFalse(result.contains("true"));
        assertTrue(result.startsWith("EBOOL:"));
        assertEquals(true, validAes.decrypt(result));
    }

    @Test
    public void testInteger() {
        String encrypted = validAes.encrypt(1023);
        assertFalse("1023".equals(encrypted));
        assertTrue(encrypted.startsWith("EINT:"));
        assertEquals(new Integer(1023), validAes.decrypt(encrypted));
    }

    @Test
    public void testLong() {
        String encrypted = validAes.encrypt(1000001L);
        assertTrue(encrypted.startsWith("ELONG:"));
        assertEquals(new Long(1000001), validAes.decrypt(encrypted));
    }

    @Test
    public void testDouble() {
        String encrypted = validAes.encrypt(1.000001D);
        assertTrue(encrypted.startsWith("EDOUBLE:"));
        assertEquals(new Double(1.000001D), validAes.decrypt(encrypted));
    }

    @Test
    public void testDecryptUnsupportedType() {
        validAes.decrypt("UNSUPPORTED:data");
    }

    @Test(expected = RuntimeException.class)
    public void testUnhandled() {
        validAes.encrypt(1.1F);
    }

    @Test()
    public void testUnencryptedString() {
        Object decrypted = validAes.decrypt("Some text");
        assertEquals(null, decrypted);
        Object d2 = validAes.decrypt("SOME DATA WITH : IN IT");
        assertEquals(null, d2);
    }

    @Test(expected = RuntimeException.class)
    public void testKeyPassMissing() {
        AesCipher aes = new AesCipher();
        aes.setSecretKeyProvider(new MockSecretKeyProvider());
        aes.setKeyAlias("keyAlias");
        aes.setKeyPass(null);
        aes.setKeyStore("keyStore");
        aes.setKeyStorePass("keyStorePass");
        aes.init();
    }

    @Test(expected = RuntimeException.class)
    public void testKeyAliasMissing() {
        AesCipher aes = new AesCipher();
        aes.setSecretKeyProvider(new MockSecretKeyProvider());
        aes.setKeyAlias(null);
        aes.setKeyPass("keyPass");
        aes.setKeyStore("keyStore");
        aes.setKeyStorePass("keyStorePass");
        aes.init();
    }

    @Test(expected = RuntimeException.class)
    public void testKeyStoreMissing() {
        AesCipher aes = new AesCipher();
        aes.setSecretKeyProvider(new MockSecretKeyProvider());
        aes.setKeyAlias("keyAlias");
        aes.setKeyPass("keyPass");
        aes.setKeyStore(null);
        aes.setKeyStorePass("keyStorePass");
        aes.init();
    }

    @Test(expected = RuntimeException.class)
    public void testStorePassMissing() {
        AesCipher aes = new AesCipher();
        aes.setSecretKeyProvider(new MockSecretKeyProvider());
        aes.setKeyAlias("keyAlias");
        aes.setKeyPass("keyPass");
        aes.setKeyStore("keyStore");
        aes.setKeyStorePass(null);
        aes.init();
    }

    @Test
    public void testInitializationVectorUniqueResults()
            throws NoSuchAlgorithmException {

        AesCipher aes1 = buildCipher("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        AesCipher aes2 = buildCipher("ffffffffffffffffffffffffffffffff");

        String encrypted1 = aes1.encrypt("Hello world!");
        String encrypted2 = aes2.encrypt("Hello world!");

        assertTrue(encrypted1.startsWith("ESTRING:"));
        assertTrue(encrypted2.startsWith("ESTRING:"));
        assertFalse("Initialization vector did not provide unique results",
                encrypted1.equals(encrypted2));

    }

    private class MockSecretKeyProvider extends SecretKeyProvider {
        @Override
        public SecretKey lookupSecretKey(String keyStore, String keyStorePass,
                String keyAlias, String keyPass) {
            try {
                return KeyGenerator.getInstance("AES").generateKey();
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException();
            }
        }
    }

}
