package org.slc.sli.dal.encrypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

/**
 * Looks up a SecretKey. Isolates the dependency on the file system / key store
 * from the rest of the AesCipher process.
 * 
 * @author sashton
 * 
 */

@Component
public class SecretKeyProvider {

    public SecretKey lookupSecretKey(String keyStore, String keyStorePass,
            String keyAlias, String keyPass) {

        Key key;
        try {
            KeyStore ks = KeyStore.getInstance("JCEKS");

            FileInputStream fis = null;
            try {
                fis = new FileInputStream(new File(keyStore));
                ks.load(fis, keyStorePass.toCharArray());
            } finally {
                if (fis != null) {
                    fis.close();
                }
            }
            key = ks.getKey(keyAlias, keyPass.toCharArray());
            if (!(key instanceof SecretKey)) {
                throw new RuntimeException(
                        "Expected key of type SecretKey, got " + key.getClass());
            }
        } catch (UnrecoverableKeyException e) {
            throw new RuntimeException(e);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return (SecretKey) key;
    }

}
