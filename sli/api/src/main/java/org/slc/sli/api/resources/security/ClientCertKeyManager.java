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

import javax.net.ssl.X509KeyManager;
import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 * @author: npandey
 */
class ClientCertKeyManager implements X509KeyManager {

    private static final String CLIENT_ALIAS = "myStaticAlias";

    private PrivateKey privateKey;
    private X509Certificate cert;

    public ClientCertKeyManager(PrivateKey newPrivateKey, X509Certificate newCert) {
        privateKey = newPrivateKey;
        cert = newCert;
    }

    @Override
    public String chooseClientAlias(String[] as, Principal[] aprincipal, Socket socket) {
        return CLIENT_ALIAS;
    }

    @Override
    public String chooseServerAlias(String s, Principal[] aprincipal, Socket socket) {
        return null;
    }

    @Override
    public X509Certificate[] getCertificateChain(String s) {
        return new X509Certificate[] {cert};
    }

    @Override
    public String[] getClientAliases(String s, Principal[] aprincipal) {
        return new String[] {CLIENT_ALIAS};
    }

    @Override
    public PrivateKey getPrivateKey(String s) {
        return privateKey;
    }

    @Override
    public String[] getServerAliases(String s, Principal[] aprincipal) {
        return new String [0];
    }

}

