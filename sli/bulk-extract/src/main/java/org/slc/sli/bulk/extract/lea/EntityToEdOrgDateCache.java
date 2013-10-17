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
package org.slc.sli.bulk.extract.lea;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;

/**
 * @author: npandey
 */
public class EntityToEdOrgDateCache {
    private Map<String, Map<String, DateTime>> cache;

    //F316: Inverse cache needs to be fixed for the new pipeline
    //private HashMultimap<String, String> inverse;

    /**
     * Simple constructor that creates the internal cache.
     */
    public EntityToEdOrgDateCache() {
        cache = new HashMap<String, Map<String, DateTime>>();
        //inverse = HashMultimap.create();
    }

    /**
     * Adds or updates the entity's edOrg entries
     *
     * @param entityId the entity id.
     * @param edOrgId the id of the edOrg.
     * @param expirationDate the expiration date of the association of the entity and edOrg
     */
    public void addEntry(String entityId, String edOrgId, DateTime expirationDate) {
        Map<String, DateTime> edOrgTime = cache.get(entityId);
        if (edOrgTime == null) {
            edOrgTime = new HashMap<String, DateTime>();
        }

        DateTime finalExpirationDate = expirationDate;
        DateTime existingExpirationDate = edOrgTime.get(edOrgId);
        if ((expirationDate == null) ||
                ((existingExpirationDate != null) && existingExpirationDate.isAfter(expirationDate))) {
            finalExpirationDate = existingExpirationDate;
        }

        edOrgTime.put(edOrgId, finalExpirationDate);
        cache.put(entityId, edOrgTime);

        //inverse.put(edOrgId, entityId);
    }

    /*
    public Set<String> ancestorEdorgs(String edorgId) {
        return inverse.get(edorgId);
    } */

    /**
     * Gets the map of EdOrgs with their expiration date associated with the entity.
     *
     * @param entityId the id of the entity in the cache.
     * @return The map of edOrgs and their expiration date for the specified entity id
     */
    public Map<String, DateTime> getEntriesById(String entityId) {
        if (!cache.containsKey(entityId)) {
            return new HashMap<String, DateTime>();
        }
        return cache.get(entityId);
    }

    /**
     * Returns all of the keys in the cache
     *
     * @return
     */
    public Set<String> getEntityIds() {
        return cache.keySet();
    }

}
