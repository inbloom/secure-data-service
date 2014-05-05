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

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Class used to encrypt/decrypt properties
 * 
 * @author ccheng
 *
 */
public class Encryptor {


    private static final String KEYSTOREPASS = "keyStorePass";
    private static final String DALKEYALIAS = "dalKeyAlias";
    private static final String DALKEYPASS = "dalKeyPass";
    private static final String DALINITVEC = "dalInitializationVector";
    private static final String DALALGORITHM = "dalAlgorithm";

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

    public String encrypt(String alias, String password, String algorithm, String initVec, String message) throws GeneralSecurityException {
        Key key = keystore.getKey(alias, password.toCharArray());
        Cipher cipher = Cipher.getInstance(algorithm);

        IvParameterSpec ivspec = null;
        if(initVec != null) {
            byte[] ivBytes;
            try {
                ivBytes = Hex.decodeHex(initVec.toCharArray());
            } catch (DecoderException e) {
                throw new RuntimeException(e);
            }
            ivspec = new IvParameterSpec(ivBytes);

            cipher.init(Cipher.ENCRYPT_MODE, (SecretKey)key, ivspec);
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, (SecretKey)key);
        }

        byte[] encryptedBytes = cipher.doFinal(message.getBytes());
        String encoded = Base64.encodeBase64String(encryptedBytes);

        return encoded;
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

    public String decrypt(String alias, String password, String algorithm, String initVec, String message) throws GeneralSecurityException {
        Key key = keystore.getKey(alias, password.toCharArray());
        Cipher cipher = Cipher.getInstance(algorithm);

        IvParameterSpec ivspec = null;
        if(initVec != null) {
            byte[] ivBytes;
            try {
                ivBytes = Hex.decodeHex(initVec.toCharArray());
            } catch (DecoderException e) {
                throw new RuntimeException(e);
            }
            ivspec = new IvParameterSpec(ivBytes);

            cipher.init(Cipher.DECRYPT_MODE, (SecretKey)key, ivspec);
        } else {
            cipher.init(Cipher.DECRYPT_MODE, (SecretKey)key);
        }

        byte[] decoded = Base64.decodeBase64(message.getBytes());
        String decrypted = new String(cipher.doFinal(decoded));

        return decrypted;
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

    private static Map<String, String> parsePropertiesFile(String propertiesFile) throws IOException {
        Map<String, String> properties = new HashMap<String, String>();

        Properties props = new Properties();
        props.load(new FileInputStream(new File(propertiesFile)));

        for(Object propObj : props.keySet()) {
            String prop = propObj.toString();
            if(prop.endsWith(KEYSTOREPASS)) {
                properties.put(KEYSTOREPASS, props.get(prop).toString());
            } else if(prop.endsWith(DALKEYALIAS)) {
                properties.put(DALKEYALIAS, props.get(prop).toString());
            } else if(prop.endsWith(DALKEYPASS)) {
                properties.put(DALKEYPASS, props.get(prop).toString());
            } else if(prop.endsWith(DALINITVEC)) {
                properties.put(DALINITVEC, props.get(prop).toString());
            } else if(prop.endsWith(DALALGORITHM)) {
                properties.put(DALALGORITHM, props.get(prop).toString());
            }
        }

        return properties;
    }


    /**
     * New functionality allows encrypting using the same algorithm
     * the api uses for encrypting PII fields in MongoDB.
     *
     * for example:
          java -jar encryption-tool.java --decrypt
          ../data-access/dal/keyStore/localKeyStore.jks
          ../data-access/dal/keyStore/localEncryption.properties
          aDvgicSt81j/YdPUhvr4Ig==

     * or to encrypt:
          java -jar encryption-tool.java
          ../data-access/dal/keyStore/localKeyStore.jks
          ../data-access/dal/keyStore/localEncryption.properties
          Lauretta

     * @param args
     * @throws Exception
     */

    public static void main(String[] args) throws Exception {
        PrintStream out = System.out;
        if (args.length < 3 || args.length > 6) {
            out.println("For encryption:");
            out.println("Usage: java -jar encryption-tool.jar <keystore_location> <keystore_password> <key_alias> <key_password> <string>");
            out.println("For decryption");
            out.println("Usage: java -jar encryption-tool.jar --decrypt <keystore_location> <keystore_password> <key_alias> <key_password> <string>");
            out.println();
            out.println("Using a properties file: \n  Property names must end with the following values. ");
            out.println("  " + KEYSTOREPASS + " (required)");
            out.println("  " + DALKEYALIAS + " (required)");
            out.println("  " + DALKEYPASS + " (required) ");
            out.println("  " + DALINITVEC + " (optional)");
            out.println("  " + DALALGORITHM + "(optional, defaults to \"AES/CBC/PKCS5Padding\")");

            out.println("Encryption:");
            out.println("  Usage: java jar encyption-tool-1.0-jar <keystore_location> <properties_file> <string>");
            out.println("Decryption:");
            out.println("  Usage: java jar encyption-tool-1.0-jar -decrypt <keystore_location> <properties_file> <string>");

            return;
        }

        boolean decrypt = false;
        String [] effectiveArgs = args;
        if (args[0].equals("--decrypt")) {
            decrypt = true;
            effectiveArgs = new String[args.length - 1];
            System.arraycopy(args, 1, effectiveArgs, 0, args.length - 1);
        }

        String propertiesLocation = null;
        String keystoreLocation = null;
        String keystorePassword = null;
        String keyAlias = null;
        String keyPassword = null;
        String message = null;
        String initVector = null;
        String algorithm = "AES/CBC/PKCS5Padding";

        if(args.length == 5 || args.length == 6) {
            //OLDER FUNCTIONALITY
            keystoreLocation = effectiveArgs[0];
            keystorePassword = effectiveArgs[1];
            keyAlias = effectiveArgs[2];
            keyPassword = effectiveArgs[3];
            message = effectiveArgs[4];

            Encryptor encryptor = new Encryptor(keystoreLocation, keystorePassword);

            if (!decrypt) {
                String encryptedString = encryptor.encrypt(keyAlias, keyPassword, message);
                out.println("Encrypted string for " + message + " is: " + encryptedString + "\n");
            } else {
                String decryptedString = encryptor.decrypt(keyAlias, keyPassword, message);
                out.println("Descrypted string for " + message + " is: " + decryptedString + "\n");
            }

        } else {
            //NEWER FUNCTIONALITY
            keystoreLocation = effectiveArgs[0];
            propertiesLocation = effectiveArgs[1];
            message = effectiveArgs[2];

            Map<String, String> properties = parsePropertiesFile(propertiesLocation);

            keystorePassword = properties.get(properties.get(KEYSTOREPASS));
            keyPassword = properties.get(DALKEYPASS);
            keyAlias = properties.get(DALKEYALIAS);

            if(properties.containsKey(DALINITVEC)) {
                initVector = properties.get(DALINITVEC);
            }
            if(properties.containsKey(DALALGORITHM)) {
                algorithm = properties.get(DALALGORITHM);
            }

            Encryptor encryptor = new Encryptor(keystoreLocation, keyPassword);

            if(!decrypt) {
                String encrypted = encryptor.encrypt(keyAlias, keyPassword, algorithm, initVector, message);
                System.out.println(encrypted);
            } else {
                String decrypted = encryptor.decrypt(keyAlias, keyPassword, algorithm, initVector, message);
                System.out.println(decrypted);
            }




        }

    }
}
