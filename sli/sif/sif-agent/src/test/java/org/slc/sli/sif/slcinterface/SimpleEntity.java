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
package org.slc.sli.sif.slcinterface;

import java.net.URL;

import java.util.List;
import java.util.Map;

import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.Link;

/**
 * Simple default implementation for Entity
 *
 */
public class SimpleEntity implements Entity {

    private String type;
    private String id;

    public SimpleEntity(String type) {
        this(type, "id");
    }

    public SimpleEntity(String type, String id) {
        this.type = type;
        this.id = id;
    }

    @Override
    public Map<String, Object> getData() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getEntityType() {
        return type;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public List<Link> getLinks() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, URL> getLinkMap() {
        // TODO Auto-generated method stub
        return null;
    }

}
