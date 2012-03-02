package org.slc.sli.dal.encrypt;

/**
 * Interface for encrypting/decrypting data
 * 
 * @author Ryan Farris <rfarris@wgen.net>
 * 
 */
public interface Cipher {
    /**
     * Encrypt the data the data into an encoded string
     * throws a runtimeException if the data is not Integer, Long, Boolean, String, or Double
     * 
     * @param data
     *            data to be encrypted
     * @return encrypted data
     */
    public String encrypt(Object data);
    
    /**
     * Decrypts the encrypted parameter back into it's original type (Integer, Long, etc)
     * 
     * @param data
     *            data previously encrypted with encrypt()
     * @return decrypted data, or null if the data was not encrypted with encrypt()
     */
    public Object decrypt(String data);
}
