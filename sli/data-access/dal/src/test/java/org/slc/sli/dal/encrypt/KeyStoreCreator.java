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
