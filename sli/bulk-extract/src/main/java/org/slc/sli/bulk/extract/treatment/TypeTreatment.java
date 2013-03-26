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

package org.slc.sli.bulk.extract.treatment;

import java.util.Map;

import org.slc.sli.domain.Entity;

/**
 * Add entity type to the body.
 *
 * @author tke
 *
 */
public class TypeTreatment implements Treatment {

    private static final String TYPE_STRING = "entityType";

    private Map<String, String> combinedEntities;

    @Override
    public Entity apply(Entity entity) {
        String type = null;
        if(combinedEntities.containsKey(entity.getType())){
            type = combinedEntities.get(entity.getType());
        } else {
            type = entity.getType();
        }
        entity.getBody().put(TYPE_STRING, type);
        return entity;
    }

    /**
     * @return the combinedEntities
     */
    public Map<String, String> getCombinedEntities() {
        return combinedEntities;
    }

    /**
     * @param combinedEntities the combinedEntities to set
     */
    public void setCombinedEntities(Map<String, String> combinedEntities) {
        this.combinedEntities = combinedEntities;
    }

}
