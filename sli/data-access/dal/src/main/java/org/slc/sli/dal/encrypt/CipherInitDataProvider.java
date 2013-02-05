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
import java.util.Properties;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Looks up SecretKey and initializationVector from file storage.
 */
@Component
public class CipherInitDataProvider {

    @Value("${sli.encryption.keyStore}")
    String keyStore;
    @Value("${sli.conf}")
    String propertiesFile;

    public CipherInitData getInitData() {

        try {
            String keyStorePass, keyAlias, keyPass, initializationVector;

            FileInputStream propStream = null;
            try {
                propStream = new FileInputStream(new File(propertiesFile));
                Properties props = new Properties();
                props.load(propStream);
                keyStorePass = props.getProperty("sli.encryption.keyStorePass");
                keyAlias = props.getProperty("sli.encryption.dalKeyAlias");
                keyPass = props.getProperty("sli.encryption.dalKeyPass");
                initializationVector = props.getProperty("sli.encryption.dalInitializationVector");
            } finally {
                if (propStream != null) {
                    propStream.close();
                }
            }

            if (keyStorePass == null) {
                throw new RuntimeException("No key store password found in properties file.");
            }
            if (keyAlias == null) {
                throw new RuntimeException("No key alias found in properties file");
            }
            if (keyPass == null) {
                throw new RuntimeException("No key password found in properties file");
            }
            if (initializationVector == null) {
                throw new RuntimeException("No initialization vector provided");
            }

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
            Key key = ks.getKey(keyAlias, keyPass.toCharArray());
            if (!(key instanceof SecretKey)) {
                throw new RuntimeException("Expected key of type SecretKey, got " + key.getClass());
            }
            return new CipherInitData((SecretKey) key, initializationVector);

        } catch (UnrecoverableKeyException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
