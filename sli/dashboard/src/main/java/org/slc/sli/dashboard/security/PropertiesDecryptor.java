/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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

import org.slc.sli.encryption.tool.Encryptor;

/**
 * 
 * @author svankina
 *
 */
public class PropertiesDecryptor {

    private String decryptedClientId, decryptedClientSecret;
    private Encryptor encryptor;
    
    public PropertiesDecryptor(String keyStore, String clientId, String clientSecret, String alias,
            String keyStorePassword, String aliasPassword) throws Exception {
        
        encryptor = new Encryptor(keyStore, keyStorePassword);
        decryptedClientId = encryptor.decrypt(alias, aliasPassword, clientId);
        decryptedClientSecret = encryptor.decrypt(alias, aliasPassword, clientSecret);
    }

    /**
     * Decodes csv byteString to byte array and decrypts to string
     *
     * @param csString
     * @return
     * @throws Exception
     */
    public String getDecryptedStringFromCSByteString(String csString) throws Exception {
        List<String> items = Arrays.asList(csString.split("\\s*,\\s*"));
        byte[] encodedBytes = new byte[items.size()];
        int i = 0;

        for (String item : items) {
            encodedBytes[i++] = Byte.parseByte(item);
        }
        byte[] decodedBytes = decryptCipher.doFinal(encodedBytes);
        return new String(decodedBytes);
    }

    /**
     * Convert string to Encrypted CVS byte string
     * @param toEncode
     * @return Encrypted comma delimited byte string. 
     * @throws Exception
     */
    public String getEncryptedByteCSString(String toEncode) throws Exception {
        byte[] encoded = encryptCipher.doFinal(toEncode.getBytes());

        String s = "";
        String delim = "";

        for (byte byteElement : encoded) {
            s += delim + byteElement;
            delim = ",";
        }

        return s;
    }

    public String getDecryptedClientSecret() {
        return decryptedClientSecret;
    }
    
}
