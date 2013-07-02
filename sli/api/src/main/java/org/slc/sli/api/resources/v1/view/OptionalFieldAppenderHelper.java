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


package org.slc.sli.api.resources.v1.view;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

/**
 * Helper class for all the strategy implementations for the custom
 * views returned by the api
 * @author srupasinghe
 *
 */
@Component
public class OptionalFieldAppenderHelper {

    @Autowired
    protected EntityDefinitionStore entityDefs;

    /**
     * Queries the database with a given collection for a key value pair
     * @param resourceName The collection
     * @param key The key to search on
     * @param values The value to search on
     * @return
     */
    public List<EntityBody> queryEntities(String resourceName, String key, List<String> values) {

        EntityDefinition entityDef = entityDefs.lookupByResourceName(resourceName);
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.setLimit(0);
        neutralQuery.addCriteria(new NeutralCriteria(key, NeutralCriteria.CRITERIA_IN, values));

        return (List<EntityBody>) entityDef.getService().list(neutralQuery);
    }

    /**
     * Queries the database with a given collection with the query
     * @param resourceName The collection
     * @return
     */
    public List<EntityBody> queryEntities(String resourceName, NeutralQuery query) {

        EntityDefinition entityDef = entityDefs.lookupByResourceName(resourceName);

        return (List<EntityBody>) entityDef.getService().list(query);
    }

    /**
     * Returns a single entity from a list for a given key value pair
     * @param list List of entities
     * @param field The field to search for
     * @param value The value to search for
     * @return
     */
    public EntityBody getEntityFromList(List<EntityBody> list, String field, String value) {

        if (list == null || field == null || value == null) {
            return null;
        }

        for (EntityBody e : list) {
            if (value.equals(e.get(field))) {
                return e;
            }
        }

        return null;
    }

    /**
     * Returns a list of entities for a given key value pair
     * @param list List of entities
     * @param field The field to search for
     * @param value The value to search for
     * @return
     */
    public List<EntityBody> getEntitySubList(List<EntityBody> list, String field, String value) {
        List<EntityBody> results = new ArrayList<EntityBody>();

        if (list == null || field == null || value == null) {
            return results;
        }

        for (EntityBody e : list) {
            if (value.equals(e.get(field))) {
                results.add(e);
            }
        }

        return results;
    }

    /**
     * Returns a list of ids for a given key
     * @param list
     * @param field
     * @return
     */
    public List<String> getIdList(List<EntityBody> list, String field) {
        List<String> ids = new ArrayList<String>();

        if (list == null || field == null) {
            return ids;
        }

        for (EntityBody e : list) {
            if (e.get(field) != null) {
                ids.add((String) e.get(field));
            }
        }

        return ids;
    }

    /**
     * Returns a list of section ids by examining a list of students
     * @param entities
     * @return
     */
    public Set<String> getSectionIds(List<EntityBody> entities) {
        Set<String> sectionIds = new HashSet<String>();
        for (EntityBody e : entities) {
            List<EntityBody> associations = (List<EntityBody>) e.get("studentSectionAssociation");

            if (associations == null) {
                continue;
            }

            for (EntityBody association : associations) {
                sectionIds.add((String) association.get(ParameterConstants.SECTION_ID));
            }
        }

        return sectionIds;
    }
}
