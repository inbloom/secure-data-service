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
package org.slc.sli.search.connector.impl;

import org.apache.commons.codec.binary.Base64;
import org.slc.sli.encryption.tool.Encryptor;
import org.slc.sli.search.util.RecoverableIndexerException;
import org.slc.sli.search.util.SearchEngineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestOperations;

import java.util.Arrays;

/**
 * Indexer is responsible for building elastic search index requests and
 * sending them to the elastic search server for processing.
 *
 * @author dwu
 */
public abstract class ESConnector {

    private static final Logger LOG = LoggerFactory.getLogger(ESConnector.class);

    private RestOperations searchTemplate;

    private String esUsername;

    private String esPassword;

    private Encryptor encryptor;

    private String dalKeyAlias;

    private String keyStorePass;

    private boolean enableEncryption;

    /**
     * Send REST query to elasticsearch server
     *
     * @param query
     * @return
     */
    private ResponseEntity<String> sendRESTCall(HttpMethod method, String url, String query, Object... uriParams) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("content-type", "application/json;charset=utf-8");

        // Basic Authentication when username and password are provided
        if (esUsername != null && esPassword != null) {
            try {
                headers.set(
                        "Authorization",
                        "Basic "
                                + Base64.encodeBase64String(((this.enableEncryption ? this.encryptor.decrypt(
                                this.dalKeyAlias, this.keyStorePass, this.esUsername) : this.esUsername) + ":" + (this.enableEncryption ? this.encryptor
                                .decrypt(this.dalKeyAlias, this.keyStorePass, this.esPassword) : this.esPassword))
                                .getBytes()));
            } catch (Exception e) {
                LOG.error("Error decrypting", e);
            }
        }
        HttpEntity<String> entity = new HttpEntity<String>(query, headers);
        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("%s Request: %s, [%s]", method.name(), url, Arrays.asList(uriParams)));
        }

        // make the REST call
        try {
            return searchTemplate.exchange(url, method, entity, String.class, uriParams);
        } catch (ResourceAccessException ste) {
            LOG.error("rest call failed: " + ste);
            throw new RecoverableIndexerException(ste);
        } catch (HttpClientErrorException rce) {
            return new ResponseEntity<String>(rce.getMessage(), rce.getStatusCode());
        }
    }

    public String executeGet(String url, Object... uriParams) {
        return sendRESTCall(HttpMethod.GET, url, null, uriParams).getBody();
    }

    public HttpStatus executeDelete(String url, Object... uriParams) {
        return sendRESTCall(HttpMethod.DELETE, url, null, uriParams).getStatusCode();
    }

    public HttpStatus executeHead(String url, Object... uriParams) {
        return sendRESTCall(HttpMethod.HEAD, url, null, uriParams).getStatusCode();
    }

    public String executePost(String url, String body, Object... uriParams) {
        ResponseEntity<String> response = sendRESTCall(HttpMethod.POST, url, body, uriParams);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new SearchEngineException(response.getStatusCode().name(), response.getBody());
        }
        return response.getBody();
    }

    public HttpStatus executePut(String url, String body, Object... uriParams) {
        return sendRESTCall(HttpMethod.PUT, url, body, uriParams).getStatusCode();
    }

    public void setSearchUsername(String esUsername) {
        this.esUsername = esUsername;
    }

    public void setSearchPassword(String esPassword) {
        this.esPassword = esPassword;
    }

    public void setSearchTemplate(RestOperations searchTemplate) {
        this.searchTemplate = searchTemplate;
    }

    public void setEncryptor(Encryptor encryptor) {
        this.encryptor = encryptor;
    }

    public void setDalKeyAlias(String dalKeyAlias) {
        this.dalKeyAlias = dalKeyAlias;
    }

    public void setKeyStorePass(String keyStorePass) {
        this.keyStorePass = keyStorePass;
    }

    public void setEnableEncryption(boolean enableEncryption) {
        this.enableEncryption = enableEncryption;
    }
}
