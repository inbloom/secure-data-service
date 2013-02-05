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

package org.slc.sli.dashboard.entity;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.Link;
import org.slc.sli.dashboard.util.Constants;

/**
 * Simple application entity
 * 
 * @author David Wu
 * @author Robert Bloh
 * 
 */
public class GenericEntity extends LinkedHashMap<String, Object> implements Entity {
    
    private static final long serialVersionUID = 2295343600615753471L;
    
    private final List<Link> links;
    private final String entityType;
    
    public GenericEntity() {
        super();
        links = new ArrayList<Link>();
        entityType = null;
    }
    
    @SuppressWarnings("unchecked")
    public GenericEntity(Map<String, Object> map) {
        super(map);
        links = (List<Link>) map.get("links");
        entityType = null;
    }
    
    @SuppressWarnings("unchecked")
    public GenericEntity(Map<String, Object> map, String entityType) {
        super(map);
        this.entityType = entityType;
        links = (List<Link>) map.get("links");
    }
    
    public GenericEntity(Entity copyFrom) {
        super(copyFrom.getData());
        this.entityType = copyFrom.getEntityType();
        links = copyFrom.getLinks();
    }
    
    @Override
    public String getId() {
        return getString(Constants.ATTR_ID);
    }
    
    public String getString(String key) {
        return (String) (get(key));
    }
    
    @SuppressWarnings("unchecked")
    /**
     * Get object for dot notation based key
     * @param key
     * @return
     */
    public Object getNode(String key) {
        if (key == null) {
            return null;
        }
        String[] fieldArray = key.split("\\.");
        Object node = this;
        for (String field : fieldArray) {
            if (node == null || !(node instanceof Map)) {
                return null;
            }
            node = ((Map<String, Object>) node).get(field);
        }
        return node;
    }
    
    @SuppressWarnings("rawtypes")
    public List getList(String key) {
        @SuppressWarnings("unchecked")
        List<Object> list = (List<Object>) (get(key));
        return list == null ? Collections.emptyList() : list;
    }
    
    public void appendToList(String key, GenericEntity obj) {
        if (!containsKey(key)) {
            put(key, new ArrayList<GenericEntity>());
        }
        @SuppressWarnings("unchecked")
        List<GenericEntity> list = (List<GenericEntity>) get(key);
        list.add(obj);
        put(key, list);
    }
    
    // Entity Interface for SDK Integration
    @Override
    public String getEntityType() {
        return entityType;
    }
    
    @Override
    public Map<String, Object> getData() {
        return this;
    }
    
    @Override
    public List<Link> getLinks() {
        return links;
    }
    
    public Link getLink(String linkName) {
        if (links == null) {
            return null;
        }
        
        for (Link link : links) {
            if (link.getLinkName().equals(linkName)) {
                return link;
            }
        }
        
        return null;
    }
    
    @Override
    public Map<String, URL> getLinkMap() {
        List<Link> links = getLinks();
        if (links != null) {
            Map<String, URL> linkMap = new HashMap<String, URL>();
            for (Link link : links) {
                linkMap.put(link.getLinkName(), link.getResourceURL());
            }
            return linkMap;
        }
        return null;
    }
}
