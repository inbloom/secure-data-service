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
 * This class is intended for unit tests and development. It should not be configured by default,
 * and should never be used in production.
 * 
 * Instead of encrypting data, it just pre-appends a tag to it to indicate it would have been
 * encrypted.
 * 
 * @author Ryan Farris <rfarris@wgen.net>
 * 
 */
public class InsecureCipher implements Cipher {
    
    @Override
    public String encrypt(Object data) {
        if (data instanceof String) {
            return "ENCRYPTED_STRING:" + data.toString();
        } else if (data instanceof Boolean) {
            return "ENCRYPTED_BOOL:" + data.toString();
        } else if (data instanceof Integer) {
            return "ENCRYPTED_INT:" + data.toString();
        } else if (data instanceof Long) {
            return "ENCRYPTED_LONG:" + data.toString();
        } else if (data instanceof Double) {
            return "ENCRYPTED_DOUBLE:" + data.toString();
        } else {
            throw new RuntimeException("Unsupported type: " + data.getClass().getCanonicalName());
        }
    }
    
    @Override
    public Object decrypt(String data) {
        String[] splitData = data.split(":");
        if (splitData.length < 2) {
            return null;
        } else {
            if (splitData[0].equals("ENCRYPTED_STRING")) {
                return splitData[1];
            } else if (splitData[0].equals("ENCRYPTED_BOOL")) {
                return Boolean.valueOf(splitData[1]);
            } else if (splitData[0].equals("ENCRYPTED_INT")) {
                return Integer.valueOf(splitData[1]);
            } else if (splitData[0].equals("ENCRYPTED_LONG")) {
                return Long.valueOf(splitData[1]);
            } else if (splitData[0].equals("ENCRYPTED_DOUBLE")) {
                return Double.valueOf(splitData[1]);
            } else {
                return null;
            }
        }
    }
    
}
