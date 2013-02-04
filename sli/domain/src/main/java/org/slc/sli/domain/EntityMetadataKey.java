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


package org.slc.sli.domain;

/**
 * The known entity metadata keys.
 * 
 * @author Sean Melody <smelody@wgen.net>
 * 
 */
public enum EntityMetadataKey {
    
    /**
     * Tenant ID is an SLI assigned identifier that is used to distinguish data coming from a
     * state. Data with matching tenant IDs should be consistent.
     */
    TENANT_ID("tenantId"),
    
    /** The external, client generated identifier for this document. */
    EXTERNAL_ID("externalId"),
    
    /** Timestamp that indicates when this document was created */
    CREATED("created"),
    
    /** Timestamp that indicates when this document was last updated */
    UPDATED("updated");
    
    private String key;
    
    private EntityMetadataKey(String key) {
        this.key = key;
    }
    
    /**
     * Returns the key.
     * 
     * @return
     */
    public String getKey() {
        return key;
    }
    
    /**
     * Returns the key
     */
    public String toString() {
        return key;
    }
}
