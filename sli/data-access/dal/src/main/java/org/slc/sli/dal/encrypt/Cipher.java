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
