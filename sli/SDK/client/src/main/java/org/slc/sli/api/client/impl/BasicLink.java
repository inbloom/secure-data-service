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


package org.slc.sli.api.client.impl;

import java.net.URL;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.slc.sli.api.client.Link;
import org.slc.sli.api.client.impl.transform.LinkDeserializer;
import org.slc.sli.api.client.impl.transform.LinkSerializer;

/**
 * A basic Link resource associated with an Entity.
 * 
 * @author asaarela
 */
@JsonSerialize(using = LinkSerializer.class)
@JsonDeserialize(using = LinkDeserializer.class)
public class BasicLink implements Link {
    
    private final String linkName;
    private final URL resource;
    
    /**
     * Construct a new link
     * 
     * @param linkName
     *            Name of the link.
     * @param resource
     *            Resource for the link.
     */
    @JsonCreator
    public BasicLink(final String linkName, final URL resource) {
        this.linkName = linkName;
        this.resource = resource;
    }
    
    @Override
    public String getLinkName() {
        return linkName;
    }
    
    @Override
    public URL getResourceURL() {
        return resource;
    }
    
    @Override
    public String toString() {
        return "rel=" + linkName + ",href=" + resource;
    }
    
}
