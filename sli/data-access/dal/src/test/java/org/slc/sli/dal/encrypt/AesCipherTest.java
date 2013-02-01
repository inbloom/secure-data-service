/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


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
import org.mockito.Mockito;

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
        String initVector = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        aes = buildCipher(key, initVector);
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
        assertEquals(Integer.valueOf(1023), aes.decrypt(encrypted));
    }

    @Test
    public void testLong() {
        String encrypted = aes.encrypt(1000001L);
        assertTrue(encrypted.startsWith("ELONG:"));
        assertEquals(Long.valueOf(1000001), aes.decrypt(encrypted));
    }

    @Test
    public void testDouble() {
        String encrypted = aes.encrypt(1.000001D);
        assertTrue(encrypted.startsWith("EDOUBLE:"));
        assertEquals(Double.valueOf(1.000001D), aes.decrypt(encrypted));
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

    @Test
    public void testInitializationVectorUniqueResults() throws NoSuchAlgorithmException {

        SecretKey key = KeyGenerator.getInstance("AES").generateKey();
        String initVector1 = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        String initVector2 = "ffffffffffffffffffffffffffffffff";
        AesCipher aes1 = buildCipher(key, initVector1);
        AesCipher aes2 = buildCipher(key, initVector2);

        String encrypted1 = aes1.encrypt("Hello world!");
        String encrypted2 = aes2.encrypt("Hello world!");

        assertTrue(encrypted1.startsWith("ESTRING:"));
        assertTrue(encrypted2.startsWith("ESTRING:"));
        assertFalse("Initialization vector did not provide unique results", encrypted1.equals(encrypted2));

    }

    private AesCipher buildCipher(SecretKey key, String initVector) {
        AesCipher cipher = new AesCipher();

        CipherInitDataProvider dataProvider = Mockito.mock(CipherInitDataProvider.class);
        Mockito.when(dataProvider.getInitData()).thenReturn(new CipherInitData(key, initVector));
        cipher.initDataProvider = dataProvider;

        return cipher;
    }
}
