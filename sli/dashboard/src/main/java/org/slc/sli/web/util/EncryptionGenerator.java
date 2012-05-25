package org.slc.sli.web.util;

import org.slc.sli.security.PropertiesDecryptor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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
        if (args.length < 2) {
            System.out.println("Not enough arguments.");
            System.out.println("Usage: java EncryptionGenerator <clientId> <clientSecret>");
            return;
        }
        
        EncryptionGenerator encGen = new EncryptionGenerator();
        String encryptedClientId = encGen.getEncryptedString(args[0]);
        String encryptedClientSecret = encGen.getEncryptedString(args[1]);
        System.out.println("\n\n\n\n########## Encrypted Properties");
        System.out
                .println("If this client_id and secret is for a single environment, copy and paste this to sli.properties.");
        System.out
                .println("If this client_id and secret is for the overall dashboard app, copy and paste this to canonical_config.yml.");
        System.out.println("Encrypted Client ID: " + encryptedClientId);
        System.out.println("Encrypted Client Secret: " + encryptedClientSecret);
        System.out.println("##########\n\n\n\n\n");
        
    }
    
}
