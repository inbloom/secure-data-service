package org.slc.sli.dal.encrypt;

import java.io.FileOutputStream;
import java.security.KeyStore;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * Utility class to generate a secret key and stores in a keystore
 * 
 * @author Ryan Farris <rfarris@wgen.net>
 * 
 */
public class KeyStoreCreator {
    
    /**
     * @param args
     *            file, keyStorePass, keyAlias, keyPass
     */
    public static void main(String[] args) throws Exception {
        
        String file = "/tmp/localKeyStore.jks";
        String ksPass = "changeit";
        String alias = "apiDatabaseKeyLocal";
        String keyPass = "changeit";
        
        if (args.length == 4) {
            file = args[0];
            ksPass = args[1];
            alias = args[2];
            keyPass = args[3];
        }
        
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        SecretKey key = keyGen.generateKey();
        
        KeyStore ks = KeyStore.getInstance("JCEKS");
        ks.load(null, ksPass.toCharArray());
        ks.setKeyEntry(alias, key, keyPass.toCharArray(), null);
        ks.store(new FileOutputStream(file), ksPass.toCharArray());
    }
    
}
