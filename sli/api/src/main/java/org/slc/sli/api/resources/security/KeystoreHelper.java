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
package org.slc.sli.api.resources.security;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;



/**
 * @author: npandey
 */
@Component
public class KeystoreHelper {

    @Value("${sli.api.keyStore:../data-access/dal/keyStore/ciKeyStore.jks}")
    private String keystoreFileName;

    @Value("${sli.api.keystore.password:changeit}")
    private String keystorePassword;

    @Value("${sli.api.digital.signature.alias:apids}")
    private String keystoreAlias;

    private static final String KEYSTORE_TYPE = "JCEKS";
    /**
     *
     * @return
     * @throws KeyStoreException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws UnrecoverableEntryException
     */
    public KeyStore.PrivateKeyEntry initializeKeystoreEntry() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableEntryException {
        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
        FileInputStream fis = new FileInputStream(keystoreFileName);
        char[] password = keystorePassword.toCharArray();

        keyStore.load(fis, password);
        IOUtils.closeQuietly(fis);

        return (KeyStore.PrivateKeyEntry) keyStore.getEntry(keystoreAlias, new KeyStore.PasswordProtection(password));
    }
}
