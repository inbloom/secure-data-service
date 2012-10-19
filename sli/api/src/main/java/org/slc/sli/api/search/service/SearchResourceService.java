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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
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
import org.slc.sli.api.security.context.resolver.EdOrgHelper;
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

    // keep parameters for ElasticSearch
    // q, size, offset
    // offset is filtered ApiQuery, but accessible by getOffset()
    private static final List<String> whilteListParameters = Arrays.asList(new String[] { "q" });

    public ServiceResponse list(Resource resource, URI queryUri) {

        List<EntityBody> entityBodies = null;
        SLIPrincipal principal = (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Entity prinipalEntity = principal.getEntity();
        // Temporary until teacher security is in place
        // If teacher, return unauthorized error
        if (isTeacher(prinipalEntity)) {
            throw new AccessDeniedException("Search currently available only for staff.");
        }

        // Call BasicService to query the elastic search repo
        final EntityDefinition definition = resourceHelper.getEntityDefinition(resource);
        ApiQuery apiQuery = new ApiQuery(queryUri);

        doFilter(apiQuery);

        // get allSchools for staff
        List<String> schoolIds = this.edOrgHelper.getUserSchools(prinipalEntity);

        apiQuery.addCriteria(new NeutralCriteria("context.schoolId", NeutralCriteria.CRITERIA_IN, schoolIds));

        entityBodies = (List<EntityBody>) definition.getService().list(apiQuery);

        // return results
        return new ServiceResponse(entityBodies, entityBodies.size());
    }
    
    /**
     * NeutralCriteria filter.  Keep NeutralCriteria only on the White List
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
                if (!whilteListParameters.contains(criteria.getKey())) {
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
     * find current user role is a teacher or not.
     *
     * @param prinipalEntity
     * @return
     */
    private static boolean isTeacher(Entity prinipalEntity) {
        return (prinipalEntity != null) && EntityNames.TEACHER.equals(prinipalEntity.getType());
    }

    /**
     * apply default query for ElasticSearch
     *
     * @param criterias
     * @throws LimitExceededException
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
        StringBuilder sb = new StringBuilder("");
        int totalCharacters = 0;
        int maxCharacters = 0;
        for (String token : tokens) {
            if (totalCharacters > 0) {
                sb.append(" ");
            }
            sb.append(token.toLowerCase());
            int length = token.length();
            totalCharacters += length;
            if (maxCharacters < length) {
                maxCharacters = length;
            }
            // if query token is at least 3 characters, do partial match. else do exact match.
            if (length >= 3) {
                sb.append("*");
            }
        }
        if (maxCharacters < 2) {
            throw new HttpClientErrorException(HttpStatus.REQUEST_ENTITY_TOO_LARGE);
        }
        if (totalCharacters < 3) {
            throw new HttpClientErrorException(HttpStatus.REQUEST_ENTITY_TOO_LARGE);
        }
        criteria.setValue(sb.toString());
    }
}
