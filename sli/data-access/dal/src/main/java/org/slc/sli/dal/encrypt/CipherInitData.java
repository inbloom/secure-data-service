package org.slc.sli.dal.encrypt;

import javax.crypto.SecretKey;

public class CipherInitData {
    
    private SecretKey secretKey;
    private String initializationVector;
    
    public CipherInitData(SecretKey secretKey, String initializationVector) {
        super();
        this.secretKey = secretKey;
        this.initializationVector = initializationVector;
    }
    
    public SecretKey getSecretKey() {
        return secretKey;
    }
    
    public String getInitializationVector() {
        return initializationVector;
    }
    
}
