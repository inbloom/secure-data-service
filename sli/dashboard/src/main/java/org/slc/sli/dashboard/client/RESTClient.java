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


package org.slc.sli.dashboard.client;

import org.slc.sli.dashboard.util.Constants;
import org.slc.sli.dashboard.util.URLBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 
 * @author pwolf
 */
@Component("RESTClient")
public class RESTClient {

    private String securityUrl;

    private static final Logger LOGGER = LoggerFactory.getLogger(RESTClient.class);
    
    private RestTemplate template;
    private RestTemplate templateWTimeout;

    /**
     * Call the session/check API
     * 
     * @param token
     *            the sessionId or null
     * @return JsonOject as described by API documentation
     * @throws NoSessionException
     */
    public JsonObject sessionCheck(String token) {
        LOGGER.info("Session check URL = {}", Constants.SESSION_CHECK_PREFIX);
        // String jsonText = makeJsonRequest(Constants.SESSION_CHECK_PREFIX, token);
        String jsonText = makeJsonRequestWHeaders(Constants.SESSION_CHECK_PREFIX, token);
        LOGGER.info("jsonText = {}", jsonText);
        JsonParser parser = new JsonParser();
        return parser.parse(jsonText).getAsJsonObject();
    }

    /**
     * Make a request to a REST service and convert the result to JSON
     * 
     * @param path
     *            the unique portion of the requested REST service URL
     * @param token
     *            not used yet
     *
     * @param fullEntities 
     *             flag for returning expanded entities from the API
     *            
     * @return a {@link JsonElement} if the request is successful and returns valid JSON, otherwise
     *         null.
     * @throws NoSessionException
     */
     public String makeJsonRequestWHeaders(String path, String token) {

        if (token != null) {

            URLBuilder url = null;
            if (!path.startsWith("http")) {
                url = new URLBuilder(getSecurityUrl());
                url.addPath(path);
            } else {
                url = new URLBuilder(path);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + token);
            HttpEntity entity = new HttpEntity(headers);
            LOGGER.debug("Accessing API at: {}", url);
            HttpEntity<String> response = null;
            try {
                response = exchange(template, url.toString(), HttpMethod.GET, entity, String.class);
            } catch (HttpClientErrorException e) {
                LOGGER.debug("Catch HttpClientException: {}", e.getStatusCode());
            }
            if (response == null) {
                return null;
            }
            return response.getBody();
        }
        LOGGER.debug("Token is null in call to RESTClient for path {}", path);

        return null;
    }

    /**
     * Make a PUT request to a REST service
     * 
     * @param path
     *            the unique portion of the requested REST service URL
     * @param token
     *            not used yet
     * 
     * @param entity
     *            entity used for update
     * 
     * @throws NoSessionException
     */
    public void putJsonRequestWHeaders(String path, String token, Object entity) {
        
        if (token != null) {
            URLBuilder url = null;
            if (!path.startsWith("http")) {
                url = new URLBuilder(getSecurityUrl());
                url.addPath(path);
            } else {
                url = new URLBuilder(path);
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + token);
            headers.add("Content-Type", "application/json");
            HttpEntity requestEntity = new HttpEntity(entity, headers);
            LOGGER.debug("Updating API at: {}", url);
            try {
                template.put(url.toString(), requestEntity);
            } catch (HttpClientErrorException e) {
                LOGGER.debug("Catch HttpClientException: {}", e.getStatusCode());
            }
        }
    }

    
    /**
     * Makes a JSONRequest with the path. Times out, if boolean timeout property is set to true.
     * Timeout value is set in application-context.xml (dashboard.WSCall.timeout)
     * @param path
     * @param timeout
     * @return
     */
    public String getJsonRequest(String path, boolean timeout) {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> response = null;
        
        RestTemplate templateToUse;
        if (timeout) {
            templateToUse = templateWTimeout;
        } else {
            templateToUse = template;
        }
        
        try {
        response = exchange(templateToUse, path, HttpMethod.GET, new HttpEntity(headers), String.class);
        } catch (HttpClientErrorException e) {
            LOGGER.debug("Catch HttpClientException: {}", e.getStatusCode());
        }
        
        if (response == null) {
            return null;
        }
        
        JsonParser parser = new JsonParser();
        String jsonText = parser.parse(response.getBody()).getAsString();
        return jsonText;
        
    }
     
    public String getSecurityUrl() {
        return securityUrl;
    }

    public void setSecurityUrl(String securityUrl) {
        this.securityUrl = securityUrl;
    }

    public RestTemplate getTemplate() {
        return template;
    }

    public void setTemplate(RestTemplate template) {
        this.template = template;
    }
    

    public RestTemplate getTemplateWTimeout() {
        return templateWTimeout;
    }

    public void setTemplateWTimeout(RestTemplate templateWTimeout) {
        this.templateWTimeout = templateWTimeout;
    }
    
    public HttpEntity<String> exchange(RestTemplate templateIn, String url, HttpMethod method, HttpEntity entity, Class cl) {
        return templateIn.exchange(url, method, entity, cl);
    }
}
