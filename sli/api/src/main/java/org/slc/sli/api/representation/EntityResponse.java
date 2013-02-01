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


package org.slc.sli.api.representation;

import java.util.HashMap;

/**
 * Contents of entities, for use in XML return
 */
public class EntityResponse extends HashMap<String, Object> {
    private String entityCollectionName;
    private static final String ENTITY = "Entity";
    private static final long serialVersionUID = -8766900333518618999L;

    public EntityResponse(String entityCollectionName, Object object) {
        super();

        setEntityCollectionName(entityCollectionName);
        put(this.entityCollectionName, object);
    }

    public Object getEntity() {
        return this.get(entityCollectionName);
    }

    public final void setEntityCollectionName(String entityCollectionName) {
        if (entityCollectionName != null && !entityCollectionName.isEmpty()) {
            this.entityCollectionName = entityCollectionName;
        } else {
            this.entityCollectionName = ENTITY;
        }
    }

    public String getEntityCollectionName() {
        return this.entityCollectionName;
    }
}
