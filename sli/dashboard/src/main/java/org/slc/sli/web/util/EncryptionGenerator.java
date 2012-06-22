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


package org.slc.sli.web.util;

import java.io.PrintStream;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.slc.sli.security.PropertiesDecryptor;

/**
 *
 * @author svankina
 *
 */

public class EncryptionGenerator {

    ApplicationContext applicationContext;

    PropertiesDecryptor propertiesDecryptor;

    public EncryptionGenerator() {

        applicationContext = new ClassPathXmlApplicationContext("classpath:/application-context.xml");

        propertiesDecryptor = (PropertiesDecryptor) applicationContext.getBean("propertiesDecryptor");
    }

    public void setPropertiesDecryptor(PropertiesDecryptor propertiesDecryptor) {
        this.propertiesDecryptor = propertiesDecryptor;
    }

    public String getEncryptedString(String toEncrypt) throws Exception {
        return propertiesDecryptor.getEncryptedByteCSString(toEncrypt);
    }

    public static void main(String[] args) throws Exception {
        PrintStream out = System.out;
        if (args.length < 2) {
            out.println("Not enough arguments.");
            out.println("Usage: java EncryptionGenerator <clientId> <clientSecret>");
            return;
        }

        EncryptionGenerator encGen = new EncryptionGenerator();
        String encryptedClientId = encGen.getEncryptedString(args[0]);
        String encryptedClientSecret = encGen.getEncryptedString(args[1]);
        out.println("\n\n\n\n########## Encrypted Properties");
        out.println("If this client_id and secret is for a single environment, copy and paste this to sli.properties.");
        out.println("If this client_id and secret is for the overall dashboard app, copy and paste this to canonical_config.yml.");
        out.println("Encrypted Client ID: " + encryptedClientId);
        out.println("Encrypted Client Secret: " + encryptedClientSecret);
        out.println("##########\n\n\n\n\n");

    }

}
