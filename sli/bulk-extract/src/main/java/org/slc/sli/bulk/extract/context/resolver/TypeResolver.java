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
package org.slc.sli.bulk.extract.context.resolver;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;

/**
 * Some collections contain multiple entity types,
 * for example, both teacher/staff is inside staff collection
 * school/educationOrganization is inside educationOrganization
 * collection. This class encapulates the type information
 * 
 * @author ycao
 * 
 */
@Component
public class TypeResolver {
    
    @Resource
    Map<String, String> entitiesToCollections;

    private Map<String, Set<String>> typeMaps;
    
    @PostConstruct
    private void reverseEntitiesToCollectionsMaps() {
        typeMaps = new HashMap<String, Set<String>>();
        for (Map.Entry<String, String> entry : entitiesToCollections.entrySet()) {
            String possibleType = entry.getKey();
            String collection = entry.getValue();
            if (!typeMaps.containsKey(collection)) {
                Set<String> set = new HashSet<String>();
                typeMaps.put(collection, set);
            }
            typeMaps.get(collection).add(possibleType);
        }
        
    }

    /**
     * given a type, return all possible types stored
     * together in the same collection
     * 
     * @param type
     * @return set of types
     */
    public Set<String> resolveType(String type) {
        if (typeMaps.containsKey(type)) {
            return typeMaps.get(type);
        }
        
        return Collections.emptySet();
    }
    
}
