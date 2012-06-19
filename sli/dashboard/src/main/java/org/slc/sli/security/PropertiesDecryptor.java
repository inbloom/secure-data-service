package org.slc.sli.security;

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

    public String getDecryptedClientId() {
        return decryptedClientId;
    }

    public String getDecryptedClientSecret() {
        return decryptedClientSecret;
    }
    
}
