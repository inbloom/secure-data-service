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
 *
 * @author ccheng
 *
 */
public class Encryptor {

    private String keyStorePass;
    private String keyLocation;

    KeyStore ks;

	public Encryptor(String keyStoreLocation, String keyStorePassword)  throws Exception {

		setKeyLocation(keyStoreLocation);
        setKeyStorePass(keyStorePassword);
        
        File keyfile = new File(getKeyLocation());

        if (keyfile.exists()) {
        	// load keystore
            FileInputStream fis = new FileInputStream(getKeyLocation());
            ks = KeyStore.getInstance("JCEKS");
            ks.load(fis, this.getKeyStorePass().toCharArray());
            fis.close();
        } else {
            throw new FileNotFoundException("Please specify a valid keystore file.");
        }
    }

    public String encrypt(String alias, String password, String value) throws GeneralSecurityException,
            IOException {

        Key k = ks.getKey(alias, password.toCharArray());
        // create a cipher object and use the generated key to initialize it
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, k);
        byte[] encrypted = cipher.doFinal(value.getBytes());

        return byteArrayToHexString(encrypted);
    }

    public String decrypt(String alias, String password, String message) throws GeneralSecurityException,
            IOException {

        Key k = ks.getKey(alias, password.toCharArray());
        // create a cipher object and use the generated key to initialize it
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, k);
        byte[] decrypted = cipher.doFinal(hexStringToByteArray(message));
        return new String(decrypted);
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

    public void setKeyStorePass(String keyStorePass) {
        this.keyStorePass = keyStorePass;
    }

    public String getKeyLocation() {
        return keyLocation;
    }

    public void setKeyLocation(String keyLocation) {
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
        
        out.println("\n");
        out.println("Encrypted String for " + args[4] + " is: " + encryptedString);
        out.println("\n");

    }
}
