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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.resources.generic.representation.ServiceResponse;
import org.slc.sli.api.resources.generic.service.DefaultResourceService;
import org.slc.sli.api.resources.generic.util.ResourceHelper;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.context.ContextValidator;
import org.slc.sli.api.security.context.ResponseTooLargeException;
import org.slc.sli.api.security.context.resolver.EdOrgHelper;
import org.slc.sli.api.security.context.validator.IContextValidator;
import org.slc.sli.api.service.ResourceNotFoundException;
import org.slc.sli.api.service.query.ApiQuery;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;

/**
 * Service class to handle all API search requests.
 * Retrieves results using data access classes. Queries and filters results based on the
 * user's security context (role, ed-org, school, section assocs, etc.)
 *
 */

@Component
public class SearchResourceService {

    private static final String CONTEXT_SCHOOL_ID = "context.schoolId";

    // Minimum limit on results to retrieve from Elasticsearch each trip
    private static final int MINIMUM_ES_LIMIT_PER_QUERY = 10;

    private static final int SEARCH_RESULT_LIMIT = 500;

    @Autowired
    DefaultResourceService defaultResourceService;

    @Autowired
    private ResourceHelper resourceHelper;

    @Autowired
    private EdOrgHelper edOrgHelper;

    @Value("${sli.search.maxUnfilteredResults:1000}")
    private long maxUnfilteredSearchResultCount;

    @Autowired
    private ContextValidator contextValidator;

    // keep parameters for ElasticSearch
    // "q" is the query parameter in the url (i.e. /api/rest/v1/search?q=Matt)
    private static final List<String> whiteListParameters = Arrays.asList(new String[] { "q" });

    /**
     * Main entry point for retrieving search results
     * @param resource
     * @param resourcesToSearch
     * @param queryUri
     * @return
     */
    public ServiceResponse list(Resource resource, String resourcesToSearch, URI queryUri) {

        final EntityDefinition definition = resourceHelper.getEntityDefinition(resource);
        List<EntityBody> finalEntities = Collections.emptyList();
        // set up query criteria, make query
        ApiQuery apiQuery = prepareQuery(resource, resourcesToSearch, queryUri);
        long count = definition.getService().count(apiQuery);
        if (count >= maxUnfilteredSearchResultCount) {
            throw new ResponseTooLargeException();
        }
        if (count == 0) {
            return new ServiceResponse(finalEntities, 0);
        }
        finalEntities = retrieveResults(definition, apiQuery);
        return new ServiceResponse(finalEntities, finalEntities.size());
    }

    /**
     * Takes an ApiQuery and retrieve results. Includes logic for pagination and calls
     * methods to filter by security context.
     * @param definition
     * @param apiQuery
     * @return
     */
    public List<EntityBody> retrieveResults(EntityDefinition definition, ApiQuery apiQuery) {

        // get the offset and limit requested
        int limit = apiQuery.getLimit();
        if (limit == 0) {
            limit = SEARCH_RESULT_LIMIT;
        }
        int offset = apiQuery.getOffset();
        int totalLimit = limit + offset;

        // now, based on the requested offset and limit, calculate
        // new offset and limit for retrieving data in batches from Elasticsearch.
        // this is necessary because some Elasticsearch results will be
        // filtered out based on security context.
        int limitPerQuery = totalLimit * 2;
        if (limitPerQuery < MINIMUM_ES_LIMIT_PER_QUERY) {
            limitPerQuery = MINIMUM_ES_LIMIT_PER_QUERY;
        }
        apiQuery.setLimit(limitPerQuery);
        apiQuery.setOffset(0);

        List<EntityBody> entityBodies = null;
        ArrayList<EntityBody> finalEntities = new ArrayList<EntityBody>();

        while (finalEntities.size() < totalLimit) {

            // call BasicService to query the elastic search repo
            entityBodies = (List<EntityBody>) definition.getService().list(apiQuery);

            // filter results through security context

            finalEntities.addAll(filterResultsBySecurity(entityBodies));

            // if no more results to grab, then we're done
            if (entityBodies.size() < limitPerQuery) {
                break;
            }

            apiQuery.setOffset(apiQuery.getOffset() + limitPerQuery);
        }

        debug("finalEntities " + finalEntities.size() + " totalLimit " + totalLimit + " offset " + offset);
        if (finalEntities.size() < offset) {
            return Collections.emptyList();
        }
        finalEntities.subList(0, offset).clear();
        return (finalEntities.size() <= limit) ? finalEntities : finalEntities.subList(0, limit);
    }

    /**
     * Prepare an ApiQuery to send to the search repository.
     * Creates the ApiQuery from the query URI, sets query criteria and security context criteria.
     * @param resourcesToSearch
     * @param queryUri
     * @return
     */
    public ApiQuery prepareQuery(Resource resource, String resourcesToSearch, URI queryUri) {
        ApiQuery apiQuery = new ApiQuery(queryUri);
        filterCriteria(apiQuery);
        addSecurityContext(apiQuery);
        if (resourcesToSearch != null) {
            apiQuery.addCriteria(new NeutralCriteria("_type", NeutralCriteria.CRITERIA_IN, getEntityTypes(resource,resourcesToSearch)));
        }
        return apiQuery;
    }

    /**
     * Given string of resource names, get corresponding string of entity types
     * @param resourceNames
     * @return
     */
    private String getEntityTypes(Resource resource, String resourceNames) {
        List<String> entityTypes = new ArrayList<String>();
        EntityDefinition def;
        for (String resourceName : resourceNames.split(",")) {
            def = resourceHelper.getEntityDefinition(resourceName);
            if (def == null) {
                throw new ResourceNotFoundException(resource.getNamespace(), resourceName);
            }
            entityTypes.add(def.getType());
        }
        return StringUtils.join(entityTypes, ',');
    }

    /**
     * Return list of accessible entities, filtered through the security context.
     * Original list may by cross-collection.
     * Retains the original order of entities.
     *
     * @param entities
     * @return
     */
    public List<EntityBody> filterResultsBySecurity(List<EntityBody> entities) {

        List<EntityBody> accessible = new ArrayList<EntityBody>();
        String toType, entityId;
        // loop through entities. if accessible, add to list
        for (EntityBody entity : entities) {
            toType = (String) entity.get("type");
            entityId = (String) entity.get("id");
            if (isAccessible(toType, entityId)) {
                accessible.add(entity);
            }
        }
        return accessible;
    }

    public boolean isAccessible(String toType, String id) {

        // get and save validator
        IContextValidator validator = contextValidator.findValidator(toType, false);
        // validate. if accessible, add to list
        if (validator != null) {
            Set<String> entityIds = new HashSet<String>();
            entityIds.add(id);
            return validator.validate(toType, entityIds);
        }
        return false;
    }

    /**
     * NeutralCriteria filter. Keep NeutralCriteria only on the White List
     *
     * @param apiQuery
     */
    public void filterCriteria(ApiQuery apiQuery) {

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
     * Apply default query pattern for ElasticSearch.
     * Query strategy - start-of-word match on each query token
     * @param criterias
     */
    private static void applyDefaultPattern(NeutralCriteria criteria) {
        String queryString = ((String) criteria.getValue()).trim().toLowerCase();

        // filter rule:
        // first, token must be at least 1 tokens
        String[] tokens = queryString.split("\\s+");
        if (tokens == null || tokens.length < 2) {
            throw new HttpClientErrorException(HttpStatus.REQUEST_ENTITY_TOO_LARGE);
        }
        // append wildcard '*' to each token
        criteria.setValue(StringUtils.join(tokens, "* ") + "*");
    }

    /**
     * Add security context criteria to query.
     * The security context is determined by the user's accessible schools. The list of
     * accessible school ids is added to the query, and records in Elasticsearch must
     * match an id in order to be returned.
     * @param apiQuery
     */
    private void addSecurityContext(ApiQuery apiQuery) {
        SLIPrincipal principal = (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Entity principalEntity = principal.getEntity();
        // get allSchools for staff
        List<String> schoolIds = new ArrayList<String>();
        schoolIds.addAll(edOrgHelper.getUserSchools(principalEntity));
        schoolIds.addAll(edOrgHelper.getDirectSchools(principalEntity));
        schoolIds.add("ALL");
        apiQuery.addCriteria(new NeutralCriteria(CONTEXT_SCHOOL_ID, NeutralCriteria.CRITERIA_IN, new ArrayList<String>(schoolIds)));
    }

    public void setMaxUnfilteredSearchResultCount(long maxUnfilteredSearchResultCount) {
        this.maxUnfilteredSearchResultCount = maxUnfilteredSearchResultCount;
    }

    /**
     * Run an embedded ElasticSearch instance, if enabled by configuration.
     * @author dwu
     *
     */
    @Component
    static final class Embedded {
        final Logger logger = LoggerFactory.getLogger(Embedded.class);

        private static final String EMBEDDED_DATA = "data";
        private Node node;

        @Value(value="${sli.search.embedded:false}")
        private boolean embeddedEnabled;

        @PostConstruct
        public void init() {
            if (embeddedEnabled) {
                logger.info("Starting embedded ElasticSearch node");
                try {
                    FileUtils.deleteDirectory(new File(EMBEDDED_DATA));
                    node = NodeBuilder.nodeBuilder().local(true).node();
                } catch (IOException ioe) {
                    logger.info("Unable to delete data directory for embedded elasticsearch");
                }
                node = NodeBuilder.nodeBuilder().local(true).node();
            }
        }

        @PreDestroy
        public void destroy(){
            if (embeddedEnabled && node != null) {
                logger.info("Destroying embedded ElasticSearch node");
                node.close();
            }
        }
    }
}
