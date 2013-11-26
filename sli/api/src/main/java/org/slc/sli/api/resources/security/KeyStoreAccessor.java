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

import javax.annotation.PostConstruct;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;



/**
 * @author: npandey
 */
public class KeyStoreAccessor {

    private KeyStore keyStore;

    private String keyStoreFileName;

    private String keyStorePassword;

    private String keyStoreType ;
    /**
     * @return
     * @throws KeyStoreException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws UnrecoverableEntryException
     */
    @PostConstruct
    private void initializeKeystoreEntry() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableEntryException {
        keyStore = KeyStore.getInstance(keyStoreType);
        InputStream fis = null;
        try {
            fis = new BufferedInputStream(new FileInputStream(keyStoreFileName));
            keyStore.load(fis, keyStorePassword.toCharArray());
        } finally {

            IOUtils.closeQuietly(fis);
        }
    }

    public KeyStore.PrivateKeyEntry getPrivateKeyEntry(String keyStoreAlias, String keyStoreEntryPassword) throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException {
        return (KeyStore.PrivateKeyEntry) keyStore.getEntry(keyStoreAlias, new KeyStore.PasswordProtection(keyStoreEntryPassword.toCharArray()));
    }

    public void setKeyStoreFileName(String keyStoreFileName) {
        this.keyStoreFileName = keyStoreFileName;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    public void setKeyStoreType(String keyStoreType) {
        this.keyStoreType = keyStoreType;
    }
}
