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
package org.slc.sli.api.search.service;

import com.google.common.base.Predicate;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Table;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.collect.Lists;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.criteriaGenerator.GranularAccessFilter;
import org.slc.sli.api.criteriaGenerator.GranularAccessFilterProvider;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.generic.PreConditionFailedException;
import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.resources.generic.representation.ServiceResponse;
import org.slc.sli.api.resources.generic.service.DefaultResourceService;
import org.slc.sli.api.resources.generic.util.ResourceHelper;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.context.ContextValidator;
import org.slc.sli.api.security.context.resolver.EdOrgHelper;
import org.slc.sli.api.security.context.validator.IContextValidator;
import org.slc.sli.api.service.EntityNotFoundException;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.api.service.query.ApiQuery;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Service class to handle all API search requests. Retrieves results using data
 * access classes. Queries and filters results based on the user's security
 * context (role, ed-org, school, section assocs, etc.)
 */

@Component
public class SearchResourceService {

    private static final String CONTEXT_SCHOOL_ID = "context.schoolId";

    // Minimum limit on results to retrieve from Elasticsearch each trip
    private static final int MINIMUM_ES_LIMIT_PER_QUERY = 10;

    private static final int SEARCH_RESULT_LIMIT = 250;

    @Autowired
    DefaultResourceService defaultResourceService;

    @Autowired
    private ResourceHelper resourceHelper;

    @Autowired
    private EdOrgHelper edOrgHelper;

    @Value("${sli.search.maxUnfilteredResults:15000}")
    private long maxUnfilteredSearchResultCount;

    @Value("${sli.search.maxFilteredResults:250}")
    private long maxFilteredSearchResultCount;

    @Autowired
    private ContextValidator contextValidator;

    @Autowired
    GranularAccessFilterProvider granularAccessFilterProvider;

    private EntityDefinition searchEntityDefinition;

    // keep parameters for ElasticSearch
    // "q" is the query parameter in the url (i.e. /api/rest/v1/search?q=Matt)
    private static final List<String> WHITE_LIST_PARAMETERS = Arrays.asList(new String[] { "q" });

    @PostConstruct
    public void init() {
        searchEntityDefinition = resourceHelper.getEntityDefinition(EntityNames.SEARCH);
    }

    protected EntityService getService() {
        return searchEntityDefinition.getService();
    }

    /**
     * Main entry point for retrieving search results
     * 
     * @param resource
     * @param resourcesToSearch
     * @param queryUri
     * @param routeToDefaultApp
     *            - get ids via search app and route the request to the default
     *            app, attaching the ids
     * @return
     */
    public ServiceResponse list(Resource resource, String resourcesToSearch, URI queryUri, boolean routeToDefaultApp) {
        List<EntityBody> finalEntities = null;
        Boolean moreEntities;
        // set up query criteria, make query
        try {
            Pair<? extends List<EntityBody>, Boolean> resultPair = retrieveResults(prepareQuery(resource,
                    resourcesToSearch, queryUri));
            finalEntities = resultPair.getLeft();
            moreEntities = resultPair.getRight();
            if (routeToDefaultApp) {
                finalEntities = routeToDefaultApp(finalEntities, new ApiQuery(queryUri));
            } else {
                setRealEntityTypes(finalEntities);
            }
        } catch (HttpStatusCodeException hsce) { // TODO: create some sli
            // exception for this
            // warn("Error retrieving results from ES: " + hsce.getMessage());
            // if item not indexed, throw Illegal
            if (hsce.getStatusCode() == HttpStatus.NOT_FOUND || hsce.getStatusCode().value() >= 500) {
                throw (IllegalArgumentException) new IllegalArgumentException(
                        "Search is not available for the user at this moment.").initCause(hsce);
            }
            throw hsce;
        }

        if (moreEntities) {
            ApiQuery query = new ApiQuery(queryUri);
            return new ServiceResponse(finalEntities, finalEntities.size() + query.getLimit() + query.getOffset());
        } else {
            return new ServiceResponse(finalEntities, finalEntities.size());
        }
    }

    private List<EntityBody> routeToDefaultApp(List<EntityBody> entities, ApiQuery query) {
        List<EntityBody> fullEntities = new ArrayList<EntityBody>();
        Table<String, String, EntityBody> entityMap = getEntityTable(entities);
        NeutralCriteria criteria = null;
        // got through each type and execute list() for the list of ids provided
        // by search
        for (String type : entityMap.rowKeySet()) {
            if (criteria != null) {
                query.removeCriteria(criteria);
            }
            criteria = new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, entityMap.row(type).keySet());
            query.addCriteria(criteria);
            query.setOffset(0);
            Iterables.addAll(fullEntities, resourceHelper.getEntityDefinitionByType(type).getService().list(query));
        }
        return fullEntities;
    }

    /**
     * Takes an ApiQuery and retrieve results. Includes logic for pagination and
     * calls methods to filter by security context.
     * 
     * @param definition
     * @param apiQuery
     * @return
     */
    public Pair<? extends List<EntityBody>, Boolean> retrieveResults(ApiQuery apiQuery) {

        // get the offset and limit requested
        int limit = apiQuery.getLimit();
        if ((limit == 0) || (limit == 1000)) {
            limit = SEARCH_RESULT_LIMIT;
        }
        if (limit > maxFilteredSearchResultCount) {
            throw new PreConditionFailedException("Invalid condition, limit [" + limit
                    + "] cannot be greater than maxFilteredResults [" + maxFilteredSearchResultCount + "] on search");
        }

        int offset = apiQuery.getOffset();
        int totalLimit = limit + offset + 1;

        // now, based on the requested offset and limit, calculate
        // new offset and limit for retrieving data in batches from
        // Elasticsearch.
        // this is necessary because some Elasticsearch results will be
        // filtered out based on security context.
        int limitPerQuery = totalLimit * 2;
        if (limitPerQuery < MINIMUM_ES_LIMIT_PER_QUERY) {
            limitPerQuery = MINIMUM_ES_LIMIT_PER_QUERY;
        }
        apiQuery.setLimit(limitPerQuery);
        apiQuery.setOffset(0);

        List<EntityBody> entityBodies = null;
        List<EntityBody> finalEntities = new ArrayList<EntityBody>();

        while (finalEntities.size() <= totalLimit
                && apiQuery.getOffset() + limitPerQuery < this.maxUnfilteredSearchResultCount) {

            // call BasicService to query the elastic search repo
            entityBodies = (List<EntityBody>) getService().list(apiQuery);
            debug("Got {} entities back", entityBodies.size());
            int lastSize = entityBodies.size();
            finalEntities.addAll(filterResultsBySecurity(entityBodies, offset, limit));

            // if no more results to grab, then we're done
            if (lastSize < limitPerQuery && lastSize < limit) {
                break;
            }

            apiQuery.setOffset(apiQuery.getOffset() + apiQuery.getLimit());
        }

        // debug("finalEntities " + finalEntities.size() + " totalLimit " +
        // totalLimit + " offset " + offset);
        if (finalEntities.size() < offset) {
            return Pair.of(new ArrayList<EntityBody>(), false);
        }

        finalEntities.subList(0, offset).clear();
        if (finalEntities.size() <= limit) {
            return Pair.of(finalEntities, false);
        } else {
            int upperBound = limit > finalEntities.size() ? finalEntities.size() : limit;
            return Pair.of(finalEntities.subList(0, upperBound), true);
        }
    }

    /**
     * Replace entity type 'search' with the real entity types
     * 
     * @param entities
     */
    private void setRealEntityTypes(List<EntityBody> entities) {
        for (EntityBody entity : entities) {
            entity.put("entityType", entity.get("type"));
            entity.remove("type");
        }
    }

    /**
     * Prepare an ApiQuery to send to the search repository. Creates the
     * ApiQuery from the query URI, sets query criteria and security context
     * criteria.
     * 
     * @param resourcesToSearch
     * @param queryUri
     * @return
     */
    public ApiQuery prepareQuery(Resource resource, String entities, URI queryUri) {
        ApiQuery apiQuery = new ApiQuery(queryUri);
        addGranularAccessCriteria(entities,apiQuery);
        filterCriteria(apiQuery);
        addSecurityContext(apiQuery);
        if (entities != null) {
            apiQuery.addCriteria(new NeutralCriteria("_type", NeutralCriteria.CRITERIA_IN, getEntityTypes(resource,
                    entities)));
        }
        return apiQuery;
    }

    /**
     * Given string of resource names, get corresponding string of entity types
     * 
     * @param resourceNames
     * @return
     */
    private Collection<String> getEntityTypes(Resource resource, String resourceNames) {
        List<String> entityTypes = new ArrayList<String>();
        EntityDefinition def;
        for (String resourceName : resourceNames.split(",")) {
            def = resourceHelper.getEntityDefinition(resourceName);
            if (def == null || !searchEntityDefinition.getService().collectionExists(def.getType())) {
                throw new EntityNotFoundException(resourceName);
            }
            entityTypes.add(def.getType());
        }
        return entityTypes;
    }

    /**
     * Return list of accessible entities, filtered through the security
     * context. Original list may by cross-collection. Retains the original
     * order of entities.
     * 
     * @param entities
     * @param offset
     *            -
     * @param limit
     *            - total requested
     * @return
     */
    public Collection<EntityBody> filterResultsBySecurity(List<EntityBody> entityBodies, int offset, int limit) {
        if (entityBodies == null || entityBodies.isEmpty()) {
            return entityBodies;
        }
        int total = offset + limit + 1;
        List<EntityBody> sublist;
        // this collection will be filtered out based on security context but
        // the original order will be preserved
        List<EntityBody> finalEntities = new ArrayList<EntityBody>(entityBodies);
        final HashBasedTable<String, String, EntityBody> filterMap = HashBasedTable.create();
        Table<String, String, EntityBody> entitiesByType = HashBasedTable.create();
        // filter results through security context
        // security checks are expensive, so do min checks necessary at a time
        while (!entityBodies.isEmpty() && filterMap.size() < total) {
            sublist = new ArrayList<EntityBody>(entityBodies.subList(0, Math.min(entityBodies.size(), limit)));
            entitiesByType = getEntityTable(sublist);

            // get accessible entities by type, add to filter map
            Set<String> accessible;
            Map<String, EntityBody> row;
            for (String type : entitiesByType.rowKeySet()) {
                row = entitiesByType.row(type);
                accessible = filterOutInaccessibleIds(type, row.keySet());
                for (String id : accessible) {
                    if (row.containsKey(id)) {
                        filterMap.put(id, type, row.get(id));
                    }
                }
            }
            entityBodies.removeAll(sublist);
            entitiesByType.clear();
        }

        // use filter map to return final entity list
        return Lists.newArrayList(Iterables.filter(finalEntities, new Predicate<EntityBody>() {
            @Override
            public boolean apply(EntityBody input) {
                return (filterMap.contains(input.get("id"), input.get("type")));
            }
        }));
    }

    /**
     * Get entities table by type, by ids
     * 
     * @param entityList
     * @return
     */
    private Table<String, String, EntityBody> getEntityTable(List<EntityBody> entityList) {
        HashBasedTable<String, String, EntityBody> entitiesByType = HashBasedTable.create();
        for (EntityBody entity : entityList) {
            entitiesByType.put((String) entity.get("type"), (String) entity.get("id"), entity);
        }
        return entitiesByType;
    }

    /**
     * Filter id set to get accessible ids
     * 
     * @param toType
     * @param ids
     * @return
     */
    public Set<String> filterOutInaccessibleIds(String toType, Set<String> ids) {

        // get validator
        IContextValidator validator = contextValidator.findValidator(toType, false);
        // validate. if accessible, add to list
        if (validator != null) {
            return validator.getValid(toType, ids);
        }
        return Collections.emptySet();
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
                if (!WHITE_LIST_PARAMETERS.contains(criteria.getKey())) {
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
     * Apply default query pattern for ElasticSearch. Query strategy -
     * start-of-word match on each query token
     * 
     * @param criterias
     */
    private static void applyDefaultPattern(NeutralCriteria criteria) {
        String queryString = ((String) criteria.getValue()).trim().toLowerCase();

        // filter rule:
        // first, token must be at least 1 tokens
        String[] tokens = queryString.split("\\s+");
        if (tokens == null || tokens.length < 1 || queryString.length() < 1) {
            throw new HttpClientErrorException(HttpStatus.REQUEST_ENTITY_TOO_LARGE);
        }
        // append wildcard '*' to each token
        criteria.setValue(StringUtils.join(tokens, "* ") + "*");
    }

    /**
     * Add security context criteria to query. The security context is
     * determined by the user's accessible schools. The list of accessible
     * school ids is added to the query, and records in Elasticsearch must match
     * an id in order to be returned.
     * 
     * @param apiQuery
     */
    private void addSecurityContext(ApiQuery apiQuery) {
        SLIPrincipal principal = (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Entity principalEntity = principal.getEntity();
        // get schools for user
        List<String> schoolIds = new ArrayList<String>();
        schoolIds.addAll(edOrgHelper.getUserEdOrgs(principalEntity));
        // a special marker for global entities
        schoolIds.add("ALL");
        apiQuery.addCriteria(new NeutralCriteria(CONTEXT_SCHOOL_ID, NeutralCriteria.CRITERIA_IN, new ArrayList<String>(
                schoolIds)));
    }

    public void setMaxUnfilteredSearchResultCount(long maxUnfilteredSearchResultCount) {
        this.maxUnfilteredSearchResultCount = maxUnfilteredSearchResultCount;
    }

    /**
     * Run an embedded ElasticSearch instance, if enabled by configuration.
     * 
     * @author dwu
     */
    @Component
    static final class Embedded {
        private static final String ES_DIR = "es";
        private Node node;

        @Value(value = "${sli.search.embedded:false}")
        private boolean embeddedEnabled;

        @Value("${sli.search.embedded.http.port:9200}")
        private int elasticSearchHttpPort;

        @PostConstruct
        public void init() {
            if (embeddedEnabled) {
                info("Starting embedded ElasticSearch node");

                String tmpDir = System.getProperty("java.io.tmpdir");
                File elasticsearchDir = new File(tmpDir, ES_DIR);
                debug(String.format("ES data tmp dir is %s", elasticsearchDir.getAbsolutePath()));

                if (elasticsearchDir.exists()) {
                    deleteDirectory(elasticsearchDir);
                }

                Settings settings = ImmutableSettings.settingsBuilder().put("node.http.enabled", true)
                        .put("http.port", this.elasticSearchHttpPort)
                        .put("path.data", elasticsearchDir.getAbsolutePath() + "/data").put("gateway.type", "none")
                        // .put("index.store.type", "memory")
                        .put("index.number_of_shards", 1).put("index.number_of_replicas", 1).build();

                node = NodeBuilder.nodeBuilder().local(true).settings(settings).node();
            }
        }

        public Client getClient() {
            return node.client();
        }

        @PreDestroy
        public void destroy() {
            if (embeddedEnabled && node != null) {
                node.close();
            }
        }

        private void deleteDirectory(File folder) {
            try {
                FileUtils.forceDelete(folder);
                if (folder.exists()) {
                    warn("Unable to delete data directory for embedded elasticsearch");
                }
            } catch (Exception e) {
                error("Unable to delete data directory for embedded elasticsearch", e);
            }
        }
    }
    private void addGranularAccessCriteria(String entities, ApiQuery apiQuery) {

        if (granularAccessFilterProvider.hasFilter()) {
            GranularAccessFilter filter = granularAccessFilterProvider.getFilter();

            if (entities.contains(filter.getEntityName())) {

                if (filter.isNoSessionsFoundForSchoolYear()) {
                    //throw new NoGranularAccessDatesException();
                }

                NeutralQuery dateQuery = filter.getNeutralQuery();
                for (NeutralCriteria criteria : dateQuery.getCriteria()) {
                    apiQuery.addCriteria(criteria);
                }
                for (NeutralQuery dateOrQuery : dateQuery.getOrQueries()) {
                   for( NeutralCriteria dateOrCriteria : dateOrQuery.getCriteria()) {
                       if(ParameterConstants.SCHOOL_YEARS.equals(dateOrCriteria.getKey())) {
                           continue;
                       } else {
                        apiQuery.addCriteria(dateOrCriteria);
                       }
                   }
                }
            }
        }
    }
}
