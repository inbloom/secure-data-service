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


package org.slc.sli.dashboard.security;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.slc.sli.encryption.tool.Encryptor;

/**
 *
 * @author svankina
 *
 */
public class PropertiesDecryptor {

    private String decryptedClientId, decryptedClientSecret;
    private Encryptor encryptor;
    private String alias, aliasPassword;

    public PropertiesDecryptor(String keyStore, String clientId, String clientSecret, String alias,
            String keyStorePassword, String aliasPassword) throws Exception {

        this.alias = alias;
        this.aliasPassword = aliasPassword;
        encryptor = new Encryptor(keyStore, keyStorePassword);
        decryptedClientId = encryptor.decrypt(alias, aliasPassword, clientId);
        decryptedClientSecret = encryptor.decrypt(alias, aliasPassword, clientSecret);
    }

    public String getDecryptedClientId() {
        return decryptedClientId;
    }

    public String getDecryptedClientSecret() {
        return decryptedClientSecret;
    }

    public String encrypt(String toEncrypt) throws GeneralSecurityException, IOException {
        return encryptor.encrypt(alias, aliasPassword, toEncrypt);
    }

    public String decrypt(String toDecrypt) throws GeneralSecurityException, IOException {
        return encryptor.decrypt(alias, aliasPassword, toDecrypt);
    }

}
