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

package org.slc.sli.encryption.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;

import javax.crypto.Cipher;

/**
 * Class used to encrypt/decrypt properties
 * 
 * @author ccheng
 *
 */
public class Encryptor {

    private static final String KEYSTORE_TYPE = "JCEKS";
    private static final String ALGORITHM = "AES";

    private String keyStorePass;
    private String keyLocation;

    KeyStore keystore;

    public Encryptor(String keyStoreLocation, String keyStorePassword)  throws Exception {

        setKeyLocation(keyStoreLocation);
        setKeyStorePass(keyStorePassword);
        
        String keyLocation = getKeyLocation();
        File keyfile = new File(keyLocation);

//        System.out.println("\nUsing keystore: " + getKeyLocation());
        
        if (keyfile.exists()) {
            // load keystore
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(getKeyLocation());
                keystore = KeyStore.getInstance(KEYSTORE_TYPE);
                keystore.load(fis, this.getKeyStorePass().toCharArray());
                fis.close();
            } finally {
                if (fis != null) {
                    fis.close();
                }
            }
        } else {
            throw new FileNotFoundException("Unable to load file '" + keyLocation + "' -- Please specify a valid keystore file.");
        }
    }

    public String encrypt(String alias, String password, String value) throws GeneralSecurityException,
            IOException {

        Key key = keystore.getKey(alias, password.toCharArray());
        // create a cipher object and use the generated key to initialize it
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(value.getBytes("UTF8"));

        return byteArrayToHexString(encrypted);
    }

    public String decrypt(String alias, String password, String message) throws GeneralSecurityException,
            IOException {

        Key key = keystore.getKey(alias, password.toCharArray());
        // create a cipher object and use the generated key to initialize it
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decrypted = cipher.doFinal(hexStringToByteArray(message));
        return new String(decrypted, "UTF8");
    }

    private static String byteArrayToHexString(byte[] b) {
        StringBuffer sb = new StringBuffer(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            int v = b[i] & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase();
    }

    private static byte[] hexStringToByteArray(String s) {
        byte[] b = new byte[s.length() / 2];
        for (int i = 0; i < b.length; i++) {
            int index = i * 2;
            int v = Integer.parseInt(s.substring(index, index + 2), 16);
            b[i] = (byte) v;
        }
        return b;
    }

    public String getKeyStorePass() {
        return keyStorePass;
    }

    final public void setKeyStorePass(String keyStorePass) {
        this.keyStorePass = keyStorePass;
    }

    final public String getKeyLocation() {
        return keyLocation;
    }

    final public void setKeyLocation(String keyLocation) {
        this.keyLocation = keyLocation;
    }


    public static void main(String[] args) throws Exception {
        PrintStream out = System.out;
        if (args.length < 4) {
            out.println("Please specify the keystore location, alias, password, and the string to be encrypted.");
            out.println("Usage: java -jar encryption-tool.jar <keystore_location> <keystore_password> <key_alias> <key_password> <string>");
            return;
        }

        Encryptor encryptor = new Encryptor(args[0], args[1]);

        String encryptedString = encryptor.encrypt(args[2], args[3], args[4]);
        
        out.println("\nEncrypted String for " + args[4] + " is: " + encryptedString + "\n");

    }
}
