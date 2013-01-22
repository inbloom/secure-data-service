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


package org.slc.sli.api.representation;

import java.util.LinkedList;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Representation of a collection of entity references returned by the API.
 *
 * @author Ryan Farris <rfarris@wgen.net>
 *
 */
public class CollectionResponse extends LinkedList<CollectionResponse.EntityReference> {
    
    private static final long serialVersionUID = -7328415047032909315L;
    
    /**
     * Single reference to an entity.
     *
     * @author Ryan Farris <rfarris@wgen.net>
     *
     */
    public static class EntityReference {
        @JsonProperty("id")
        private final String id;
        @JsonProperty("entityType")
        private final String entityType;
        @JsonProperty("link")
        private final EmbeddedLink link;
        
        public EntityReference(final String id, final String entityType, final EmbeddedLink link) {
            this.id = id;
            this.entityType = entityType;
            this.link = link;
        }
        
        @JsonIgnore
        public String getId() {
            return id;
        }
        
        @JsonIgnore
        public String getEntityType() {
            return entityType;
        }
        
        @JsonIgnore
        public EmbeddedLink getLink() {
            return link;
        }
        
    }
    
    public void add(final String id, final String rel, final String type, final String href) {
        this.add(new EntityReference(id, type, new EmbeddedLink(rel, href)));
    }
    
}
