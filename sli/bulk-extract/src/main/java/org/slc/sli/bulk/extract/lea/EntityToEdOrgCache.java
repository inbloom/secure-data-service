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

package org.slc.sli.bulk.extract.lea;

import com.google.common.collect.HashMultimap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EntityToEdOrgCache {
    
    private Map<String, Set<String>> cache;
    private HashMultimap<String, String> inverse;

    /**
     * Simple constructor that creates the internal cache.
     */
    public EntityToEdOrgCache() {
        cache = new HashMap<String, Set<String>>();
        inverse = HashMultimap.create();
    }

    /**
     * Adds or updates the entity's lea entries
     * 
     * @param entityId
     * @param leaId
     */
    public void addEntry(String entityId, String leaId) {
        Set<String> leas = new HashSet<String>();
        if (cache.containsKey(entityId)) {
            leas = cache.get(entityId);
        }
        leas.add(leaId);
        cache.put(entityId, leas);
        inverse.put(leaId, entityId);
    }

    public Set<String> ancestorEdorgs(String edorgId) {
        return inverse.get(edorgId);
    }

    /**
     * Gets the set of LEAs associated with the entity.
     * 
     * @param entityId
     * @return The set of leas for the specified entity id
     */
    public Set<String> getEntriesById(String entityId) {
        if (!cache.containsKey(entityId)) {
            return new HashSet<String>();
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
