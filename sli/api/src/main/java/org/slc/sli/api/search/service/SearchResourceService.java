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
package org.slc.sli.api.search.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.resources.generic.representation.ServiceResponse;
import org.slc.sli.api.resources.generic.service.DefaultResourceService;
import org.slc.sli.api.resources.generic.util.ResourceHelper;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.context.ContextResolverStore;
import org.slc.sli.api.security.context.resolver.DenyAllContextResolver;
import org.slc.sli.api.security.context.resolver.EdOrgHelper;
import org.slc.sli.api.security.context.resolver.EntityContextResolver;
import org.slc.sli.api.security.context.traversal.cache.impl.SessionSecurityCache;
import org.slc.sli.api.service.query.ApiQuery;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;

/**
 * Search service
 *
 */

@Component
public class SearchResourceService {

    @Autowired
    DefaultResourceService defaultResourceService;

    @Autowired
    private ResourceHelper resourceHelper;

    @Autowired
    private EdOrgHelper edOrgHelper;

    @Autowired
    private ContextResolverStore contextResolverStore;

    @Autowired
    private SessionSecurityCache securityCachingStrategy;

    // keep parameters for ElasticSearch
    // q,
    private static final List<String> whiteListParameters = Arrays.asList(new String[] { "q" });

    public ServiceResponse list(Resource resource, String entity, URI queryUri) {

        SLIPrincipal principal = (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Entity principalEntity = principal.getEntity();

        // set up query criteria
        final EntityDefinition definition = resourceHelper.getEntityDefinition(resource);
        ApiQuery apiQuery = new ApiQuery(queryUri);
        doFilter(apiQuery);
        addContext(apiQuery);
        if (entity != null) {
            apiQuery.addCriteria(new NeutralCriteria("_type", NeutralCriteria.CRITERIA_IN, entity));
        }

        int maxResults = apiQuery.getLimit();
        int maxPerQuery = maxResults;

        // execute the query
        int queryOffset = 0;
        List<EntityBody> entityBodies = null;
        List<EntityBody> accessibleEntities = null;
        ArrayList<EntityBody> finalEntities = new ArrayList<EntityBody>();

        while (finalEntities.size() < maxResults) {

            // Call BasicService to query the elastic search repo
            entityBodies = (List<EntityBody>) definition.getService().list(apiQuery);

            // filter results through security context
            accessibleEntities = checkAccessible(entityBodies, principalEntity);

            finalEntities.addAll(accessibleEntities);

            // if no more results to grab, then we're done
            if (entityBodies.size() < maxPerQuery) {
                break;
            }

            // adjust api query offset
            queryOffset += maxPerQuery;
            apiQuery.setOffset(queryOffset);
        }

        // return results
        return new ServiceResponse(finalEntities, finalEntities.size());
    }

    /**
     * Return list of accessible entities, filtered through the security context.
     * Original list may by cross-collection.
     * Retains the original order of entities.
     *
     * @param entities
     * @return
     */
    public List<EntityBody> checkAccessible(List<EntityBody> entities, Entity user) {

        // find entity types
        if (EntityNames.STAFF.equals(user.getType())) {
            return entities;
        }

        Map<String, HashSet<String>> accessibleIds = new HashMap<String, HashSet<String>>();
        for (EntityBody entity : entities) {
            if (!accessibleIds.containsKey(entity.get("type"))) {
                accessibleIds.put((String) entity.get("type"), null);
            }
        }

        // get all accessible ids for all entity types
        List<String> allowed = null;
        EntityContextResolver resolver = null;
        for (String toType : accessibleIds.keySet()) {
            resolver = new DenyAllContextResolver();
            if (!securityCachingStrategy.contains(toType)) {
                resolver = contextResolverStore.findResolver(user.getType(), toType);
                allowed = resolver.findAccessible(user);
                accessibleIds.put(toType, new HashSet<String>(allowed));
            }
        }

        // filter out entities that are not accessible
        List<EntityBody> accessible = new ArrayList<EntityBody>();
        for (EntityBody entity : entities) {
            if (accessibleIds.get(entity.get("type")).contains(entity.get("id"))) {
                accessible.add(entity);
            }
        }

        return accessible;
    }

    /**
     * NeutralCriteria filter. Keep NeutralCriteria only on the White List
     *
     * @param apiQuery
     */
    public void doFilter(ApiQuery apiQuery) {

        // keep only whitelist parameters
        List<NeutralCriteria> criterias = apiQuery.getCriteria();
        if (criterias != null) {

            // set doFilter true if "q" is in the list of NetralCriteria
            boolean doFilter = false;
            List<NeutralCriteria> removalList = new LinkedList<NeutralCriteria>();
            for (NeutralCriteria criteria : criterias) {
                if (!whiteListParameters.contains(criteria.getKey())) {
                    removalList.add(criteria);

                } else if ("q".equals(criteria.getKey())) {
                    doFilter = true;
                    applyDefaultPattern(criteria);
                }
            }
            if (doFilter) {
                criterias.removeAll(removalList);
            }
        }
    }

    /**
     * apply default query for ElasticSearch
     *
     * @param criterias
     */
    private static void applyDefaultPattern(NeutralCriteria criteria) {
        String queryString = ((String) criteria.getValue()).trim();

        // filter rule:
        // first, token must be at least 1 tokens
        String[] tokens = queryString.split("\\s+");
        if (tokens == null || tokens.length < 1) {
            throw new HttpClientErrorException(HttpStatus.REQUEST_ENTITY_TOO_LARGE);
        }

        // second, one of tokens must have at least 2 characters
        // third, total number of characters must be at least 3 characters
        StringBuilder sb = new StringBuilder();
        int totalCharacters = 0;
        boolean tokenLengthCriteriaMet = false;
        int length = 0;
        for (String token : tokens) {
            sb.append(" ").append(token.toLowerCase()).append("*");
            length = token.length();
            totalCharacters += length;
            tokenLengthCriteriaMet = tokenLengthCriteriaMet || length >= 2;
        }
        if (!tokenLengthCriteriaMet || totalCharacters < 3) {
            throw new HttpClientErrorException(HttpStatus.REQUEST_ENTITY_TOO_LARGE);
        }
        // first char will be space
        criteria.setValue(sb.substring(1).toString());
    }

    private void addContext(ApiQuery apiQuery) {
        SLIPrincipal principal = (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Entity principalEntity = principal.getEntity();
        // get allSchools for staff
        Set<String> schoolIds = new HashSet<String>();
        schoolIds.addAll(edOrgHelper.getUserSchools(principalEntity));
        schoolIds.addAll(edOrgHelper.getDirectSchools(principalEntity));
        schoolIds.add("ALL");
        apiQuery.addCriteria(new NeutralCriteria("context.schoolId", NeutralCriteria.CRITERIA_IN, new ArrayList<String>(schoolIds)));
    }
}
