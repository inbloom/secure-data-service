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

package org.slc.sli.common.encrypt.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Helper class to deal with stored trusted certificates
 * @author dkornishev
 *
 */
@Component
public class CertificateValidationHelper {

	@Value("${sli.security.truststore.path}")
    private String pathToTruststore;

    @Value("${sli.security.truststore.password}")
    private String trustStorePassword;

    private KeyStore trusted;

    @SuppressWarnings("unused")
    @PostConstruct
    private void init() throws Exception {
        trusted = KeyStore.getInstance("JKS");
        InputStream fis = new FileInputStream(new File(this.pathToTruststore));
        trusted.load(fis, this.trustStorePassword.toCharArray());
    }

    /**
     * Validates that client certificate provided during TLS handshake
     * Matches what we have stored for the application
     * @param request
     */
    public void validateCertificate(X509Certificate requestCert, String clientId) {
        Certificate storedCert = locateCertificateForApp(clientId);
        if (!storedCert.equals(requestCert)) {
            throw new IllegalArgumentException("App must provide trusted X509 Certificate");
        }
    }

    /**
     * Provides public key from the certificate stored for current application
     * @return
     * @throws KeyStoreException
     */
    public PublicKey getPublicKeyForApp(String clientId) {
        return locateCertificateForApp(clientId).getPublicKey();
    }

    private Certificate locateCertificateForApp(String clientId) {

        Certificate storedCert;
        try {
            storedCert = trusted.getCertificate(clientId);
        } catch (KeyStoreException e) {
            throw new IllegalStateException("Failed to retrieve stored cert for application", e);
        }

        if (null == storedCert) {
            throw new IllegalStateException("No certificate for app " + clientId + " found");
        }
        
        return storedCert;
    }

}
