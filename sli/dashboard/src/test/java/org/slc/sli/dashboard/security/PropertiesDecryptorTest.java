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

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Test encryption and decryption of client_secret and clientID
 * @author svankina
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application-context.xml" })
public class PropertiesDecryptorTest {

    @Autowired
    ApplicationContext applicationContext;

    @Test
    public void testEncryptionDecryption() throws Exception {
        String toEncrypt = "Ragnar Danneskjold";
        PropertiesDecryptor propDec = (PropertiesDecryptor) applicationContext.getBean("propertiesDecryptor");
        String encrypted = propDec.encrypt(toEncrypt);
        String decrypted = propDec.decrypt(encrypted);
        org.junit.Assert.assertNotSame(toEncrypt, encrypted);
        assertEquals(toEncrypt, decrypted);
    }



}
