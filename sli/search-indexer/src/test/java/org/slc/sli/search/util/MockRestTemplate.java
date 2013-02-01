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
package org.slc.sli.search.util;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

@SuppressWarnings("unchecked")
public class MockRestTemplate implements RestOperations {
    
    private List<HttpEntity<?>> callsCollector = new ArrayList<HttpEntity<?>>();
    
    ResponseEntity<?> mockResponse = new ResponseEntity<String>("{'status':'ok'}", HttpStatus.OK);
    
    public void reset() {
        callsCollector = new ArrayList<HttpEntity<?>>();
    }
    
    public List<HttpEntity<?>> getCalls() {
        return callsCollector;
    }
    
    public <T> T getForObject(String url, Class<T> responseType, Object... uriVariables) throws RestClientException {
        // TODO Auto-generated method stub
        return null;
    }
    
    public <T> T getForObject(String url, Class<T> responseType, Map<String, ?> uriVariables)
            throws RestClientException {
        // TODO Auto-generated method stub
        return null;
    }
    
    public <T> T getForObject(URI url, Class<T> responseType) throws RestClientException {
        // TODO Auto-generated method stub
        return null;
    }
    
    public <T> ResponseEntity<T> getForEntity(String url, Class<T> responseType, Object... uriVariables)
            throws RestClientException {
        // TODO Auto-generated method stub
        return null;
    }
    
    public <T> ResponseEntity<T> getForEntity(String url, Class<T> responseType, Map<String, ?> uriVariables)
            throws RestClientException {
        // TODO Auto-generated method stub
        return null;
    }
    
    public <T> ResponseEntity<T> getForEntity(URI url, Class<T> responseType) throws RestClientException {
        // TODO Auto-generated method stub
        return null;
    }
    
    public HttpHeaders headForHeaders(String url, Object... uriVariables) throws RestClientException {
        // TODO Auto-generated method stub
        return null;
    }
    
    public HttpHeaders headForHeaders(String url, Map<String, ?> uriVariables) throws RestClientException {
        // TODO Auto-generated method stub
        return null;
    }
    
    public HttpHeaders headForHeaders(URI url) throws RestClientException {
        // TODO Auto-generated method stub
        return null;
    }
    
    public URI postForLocation(String url, Object request, Object... uriVariables) throws RestClientException {
        // TODO Auto-generated method stub
        return null;
    }
    
    public URI postForLocation(String url, Object request, Map<String, ?> uriVariables) throws RestClientException {
        // TODO Auto-generated method stub
        return null;
    }
    
    public URI postForLocation(URI url, Object request) throws RestClientException {
        // TODO Auto-generated method stub
        return null;
    }
    
    public <T> T postForObject(String url, Object request, Class<T> responseType, Object... uriVariables)
            throws RestClientException {
        // TODO Auto-generated method stub
        return null;
    }
    
    public <T> T postForObject(String url, Object request, Class<T> responseType, Map<String, ?> uriVariables)
            throws RestClientException {
        // TODO Auto-generated method stub
        return null;
    }
    
    public <T> T postForObject(URI url, Object request, Class<T> responseType) throws RestClientException {
        // TODO Auto-generated method stub
        return null;
    }
    
    public <T> ResponseEntity<T> postForEntity(String url, Object request, Class<T> responseType,
            Object... uriVariables) throws RestClientException {
        // TODO Auto-generated method stub
        return null;
    }
    
    public <T> ResponseEntity<T> postForEntity(String url, Object request, Class<T> responseType,
            Map<String, ?> uriVariables) throws RestClientException {
        // TODO Auto-generated method stub
        return null;
    }
    
    public <T> ResponseEntity<T> postForEntity(URI url, Object request, Class<T> responseType)
            throws RestClientException {
        // TODO Auto-generated method stub
        return null;
    }
    
    public void put(String url, Object request, Object... uriVariables) throws RestClientException {
        // TODO Auto-generated method stub
        
    }
    
    public void put(String url, Object request, Map<String, ?> uriVariables) throws RestClientException {
        // TODO Auto-generated method stub
        
    }
    
    public void put(URI url, Object request) throws RestClientException {
        // TODO Auto-generated method stub
        
    }
    
    public void delete(String url, Object... uriVariables) throws RestClientException {
        // TODO Auto-generated method stub
        
    }
    
    public void delete(String url, Map<String, ?> uriVariables) throws RestClientException {
        // TODO Auto-generated method stub
        
    }
    
    public void delete(URI url) throws RestClientException {
        // TODO Auto-generated method stub
        
    }
    
    public Set<HttpMethod> optionsForAllow(String url, Object... uriVariables) throws RestClientException {
        // TODO Auto-generated method stub
        return null;
    }
    
    public Set<HttpMethod> optionsForAllow(String url, Map<String, ?> uriVariables) throws RestClientException {
        // TODO Auto-generated method stub
        return null;
    }
    
    public Set<HttpMethod> optionsForAllow(URI url) throws RestClientException {
        // TODO Auto-generated method stub
        return null;
    }
    
   
    public <T> ResponseEntity<T> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity,
            Class<T> responseType, Object... uriVariables) throws RestClientException {
        callsCollector.add(requestEntity);
        return (ResponseEntity<T>) ((url.contains("_mget")) ? (ResponseEntity<T>) getMockResponse(requestEntity) : mockResponse);
    }
    
    public <T> ResponseEntity<T> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity,
            Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        callsCollector.add(requestEntity);
        return (ResponseEntity<T>) ((url.contains("_mget")) ? (ResponseEntity<T>) getMockResponse(requestEntity) : mockResponse);
    }
    
    public <T> ResponseEntity<T> exchange(URI url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType)
            throws RestClientException {
        callsCollector.add(requestEntity);
        return (ResponseEntity<T>) ((url.getPath().contains("_mget")) ? (ResponseEntity<T>) getMockResponse(requestEntity) : mockResponse);
    }
    
    public <T> T execute(String url, HttpMethod method, RequestCallback requestCallback,
            ResponseExtractor<T> responseExtractor, Object... uriVariables) throws RestClientException {
        // TODO Auto-generated method stub
        return null;
    }
    
    public <T> T execute(String url, HttpMethod method, RequestCallback requestCallback,
            ResponseExtractor<T> responseExtractor, Map<String, ?> uriVariables) throws RestClientException {
        // TODO Auto-generated method stub
        return null;
    }
    
    public <T> T execute(URI url, HttpMethod method, RequestCallback requestCallback,
            ResponseExtractor<T> responseExtractor) throws RestClientException {
        // TODO Auto-generated method stub
        return null;
    }
    
    private ResponseEntity<String> getMockResponse(HttpEntity<?> requestEntity) {
        String body = (String)requestEntity.getBody();
        Map<String, Object> entity = IndexEntityUtil.getEntity(body);
        
        List<Map<String, Object>> array = new ArrayList<Map<String, Object>>();
        Map<String, Object> tmp = new HashMap<String, Object>();
        for (Map<String, Object> entry : (List<Map<String, Object>>)entity.get("docs")) {
            tmp.putAll(entry);
            tmp.put("exists", true);
            tmp.put("_source", new HashMap<String, Object>());
            array.add(tmp);
        }
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("docs", array);
        return new ResponseEntity<String>(IndexEntityUtil.getBodyForIndex(response), HttpStatus.OK);
    }
    
}
