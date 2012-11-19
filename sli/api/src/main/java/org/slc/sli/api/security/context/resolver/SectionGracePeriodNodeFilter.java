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


package org.slc.sli.api.security.context.resolver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.security.context.AssociativeContextHelper;
import org.slc.sli.api.security.context.traversal.graph.NodeFilter;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;

/**
 * Filters the sections by a given date (grace period)
 *
 * @author srupasinghe
 *
 */
@Component
public class SectionGracePeriodNodeFilter extends NodeFilter {
    private static final String END_DATE = "endDate";
    private static final String ID = "_id";
    private static final String GREATER_THAN_EQUAL = ">=";

    @Value("${sli.security.gracePeriod}")
    private String gracePeriod;

    @Autowired
    
    private PagingRepositoryDelegate<Entity> repo;

    @Autowired
    private AssociativeContextHelper helper;

    @Override
    public List<Entity> filterEntities(List<Entity> toResolve, String referenceField) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        //get the filter date
        String endDate = helper.getFilterDate(gracePeriod, calendar);

        if (!toResolve.isEmpty() && !endDate.isEmpty()) {
            //get the section entities
            Iterable<Entity> sections = helper.getReferenceEntities(EntityNames.SECTION, ID, getReferencedIds(toResolve, referenceField));
            //get the session Ids
            Set<String> sessionIds = getIds(sections, ParameterConstants.SESSION_ID);

            NeutralQuery query = new NeutralQuery();
            query.addCriteria(new NeutralCriteria(ID, NeutralCriteria.CRITERIA_IN,
                    new ArrayList<String>(sessionIds)));
            query.addCriteria(new NeutralCriteria(END_DATE, GREATER_THAN_EQUAL, endDate));

            //get the session entities
            Iterable<Entity> sessions = repo.findAll(EntityNames.SESSION, query);

            //filter out the relevant ids
            return getFilteredEntities(toResolve, getEntityIds(sections, ParameterConstants.SESSION_ID,
                    getIds(sessions, ID)), referenceField);
        }

        return toResolve;
    }

    /**
     * Returns a list of ids for the id key given
     * @param entities List of entities
     * @param idKey The key to match
     * @return
     */
    protected Set<String> getIds(Iterable<Entity> entities, String idKey) {
        Set<String> ids = new HashSet<String>();

        if (entities == null) {
            return ids;
        }

        for (Entity entity : entities) {
            if (idKey.equals(ID)) {
                ids.add(entity.getEntityId());
            } else {
                if (entity.getBody().containsKey(idKey)) {
                    ids.add((String) entity.getBody().get(idKey));
                }
            }
        }

        return ids;
    }

    private List<Entity> getFilteredEntities(List<Entity> entitiesToResolve, Set<String> keys, String referenceField) {
        List<Entity> filteredEntities = new ArrayList<Entity>();

        if (entitiesToResolve == null) {
            return filteredEntities;
        }

        for (Entity entity : entitiesToResolve) {
            String keyId = entity.getEntityId();
            if (referenceField != null && !referenceField.isEmpty()) {
                keyId = (String) entity.getBody().get(referenceField);
            }
            if (keys.contains(keyId)) {
                filteredEntities.add(entity);
            }
        }

        return filteredEntities;

    }
    /**
     * Returns a list of entity ids that matches the id key and the given
     * list of keys
     * @param entities List of entities
     * @param idKey The key to match
     * @param keys The list of key ids to match
     * @return
     */
    protected Set<String> getEntityIds(Iterable<Entity> entities, String idKey, Set<String> keys) {
        Set<String> ids = new HashSet<String>();

        if (entities == null) {
            return ids;
        }

        for (Entity entity : entities) {
            String keyId = (String) entity.getBody().get(idKey);

            if (keys.contains(keyId)) {
                ids.add(entity.getEntityId());
            }
        }

        return ids;
    }

    private List<String> getReferencedIds(List<Entity> toResolve, String referenceField) {
        List<String> foundIds = new ArrayList<String>();

        if (referenceField != null && !referenceField.isEmpty()) {
            for (Entity e : toResolve) {
                Map<String, Object> body = e.getBody();
                foundIds.add((String) body.get(referenceField));
            }
        } else {
            for (Entity e : toResolve) {
                foundIds.add(e.getEntityId());
            }
        }
        return foundIds;
    }

}
