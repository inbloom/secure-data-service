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


package org.slc.sli.sif.slcinterface;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.slc.sli.api.client.impl.BasicClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic authentication example using the SLI SDK.
 */
public class SlcInterface {

    private String clientId;
    private String clientSecret;
    private URL apiUrl;
    private URL callbackUrl; //TODO: this may become a problem
    private String token;

    private BasicClient client;

    public SlcInterface() {
        init();
    }
    
    public void init() {
        InputStream propStream;
        String externalProps = System.getProperty("sli.conf");
        if (externalProps != null) {
            try {
                propStream = new FileInputStream(externalProps);
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Unable to load properties file: " + externalProps);
            }
        } else {
            String env = System.getProperty("sli.env");
            if (env == null) {
                throw new RuntimeException("sli.env system property is not set!");
            }
            propStream = this.getClass().getResourceAsStream("/config/" + env + ".properties");
            if (propStream == null) {
                throw new RuntimeException("no properties file found for sli.env: " + env);
            }
        }
        Properties props = new Properties();
        try {
            props.load(propStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        clientId = props.getProperty("bootstrap.app.sif.client_id");
        clientSecret = props.getProperty("bootstrap.app.sif.client_secret");
        token = props.getProperty("bootstrap.app.sif.token");
        String apiUrlString = props.getProperty("bootstrap.app.sif.apiUrl");
        String callbackUrlString = props.getProperty("bootstrap.app.sif.callbackUrl");
        if (clientId == null || clientSecret == null || apiUrlString == null || callbackUrlString == null) {
            throw new RuntimeException(
                    "Missing property.  All of the following properties must be available: clientId, clientSecret, apiUrl, callbackUrl");
        }
        try {
            apiUrl = new URL(apiUrlString);
            callbackUrl = new URL(callbackUrlString);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        
        initializeClient();
    }

    private void initializeClient() {
        if (null == client) {
            client = new BasicClient(apiUrl, clientId, clientSecret, callbackUrl);
            client.setToken(token);
        }
    }

/*    public String sessionCheck() {
        try {
            return client.sessionCheck(token);
        } catch (Exception e) {
            LOG.error("  " + e.getMessage(), e);
        }
        return "";
    }*/
}
