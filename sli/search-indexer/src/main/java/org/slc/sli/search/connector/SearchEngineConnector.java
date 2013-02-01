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
package org.slc.sli.search.connector;

import java.util.List;

import org.slc.sli.search.entity.IndexEntity;
import org.slc.sli.search.entity.IndexEntity.Action;
import org.springframework.http.HttpStatus;

/**
 * Interface for communicating wiht the search engine via REST
 *
 */
public interface SearchEngineConnector {
    public String executeGet(String url, Object... uriParams);
    public String executePost(String url, String body, Object... uriParams);
    public HttpStatus executePut(String url, String body, Object... uriParams);
    public HttpStatus executeDelete(String url, Object... uriParams);
    public HttpStatus executeHead(String url, Object... uriParams);
    
    public void createIndex(String index);
    public void deleteIndex(String index);
    
    public void putMapping(String index, String type, String mapping);
    
    public String getBaseUrl();
    
    public void execute(Action a, List<IndexEntity> docs);
}
