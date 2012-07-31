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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ws.rs.MessageProcessingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.SLIClientException;
import org.slc.sli.api.client.impl.BasicClient;
import org.slc.sli.api.client.impl.BasicRESTClient;
import org.slc.sli.api.client.util.Query;

/**
 * Basic authentication example using the SLI SDK.
 */
@Component
public class SlcInterface {

    private static final Logger LOG = LoggerFactory.getLogger(SlcInterface.class);

    @Value("${bootstrap.app.sif.client_id}")
    private String clientId;

    @Value("${bootstrap.app.sif.client_secret}")
    private String clientSecret;

    @Value("${bootstrap.app.sif.apiUrl}")
    private URL apiUrl;

    @Value("${bootstrap.app.sif.callbackUrl}")
    private URL callbackUrl; //TODO: this may become a problem because it seems to be required but our application probably won't have one.

    @Value("${bootstrap.app.sif.token}")
    private String token;

    private BasicClient client;

    @PostConstruct
    public void init() {
        if (null == client) {
            client = new BasicClient(new BasicRESTClient(apiUrl, clientId, clientSecret, callbackUrl));
            client.getRESTClient().setSessionToken(token);
        }
    }

    public String sessionCheck() {
        try {
            return client.sessionCheck(token);
        } catch (Exception e) {
            LOG.error("  " + e.getMessage(), e);
        }
        return "";
    }
    
    public String create(final Entity e) {
        try
        {
            return client.create(e);
        } catch (IOException e1)
        {
            LOG.error("  " + e1.getMessage(), e1);
        } catch (URISyntaxException e1)
        {
            LOG.error("  " + e1.getMessage(), e1);
        } catch (SLIClientException e1)
        {
            LOG.error("  " + e1.getMessage(), e1);
        }
        return null;
    }
    
    public boolean delete(final String entityType, final String entityId) {
        boolean ok = false;
        try
        {
            client.delete(entityType, entityId);
            ok = true;
        } catch (MalformedURLException e)
        {
            LOG.error("  " + e.getMessage(), e);
        } catch (URISyntaxException e)
        {
            LOG.error("  " + e.getMessage(), e);
        } catch (SLIClientException e)
        {
            LOG.error("  " + e.getMessage(), e);
        }
        return ok;
    }

    public void update(final Entity e) {
        try
        {
            client.update(e);
        } catch (IOException e1)
        {
            LOG.error("  " + e1.getMessage(), e1);
        } catch (URISyntaxException e1)
        {
            LOG.error("  " + e1.getMessage(), e1);
        } catch (SLIClientException e1)
        {
            LOG.error("  " + e1.getMessage(), e1);
        }
    }
    /**
     * Pass-through interface
     */
    public void read(List<Entity> entities, final String type, final Query query) throws URISyntaxException,
            MessageProcessingException, IOException, SLIClientException {
        client.read(entities, type, null, query);
    }

}
