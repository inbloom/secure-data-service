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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import org.slc.sli.api.security.context.ContextResolverStore;
import org.slc.sli.api.security.context.ContextValidator;
import org.slc.sli.api.security.context.ResponseTooLargeException;
import org.slc.sli.api.security.context.resolver.EdOrgHelper;
import org.slc.sli.api.security.context.resolver.EntityContextResolver;
import org.slc.sli.api.security.context.validator.IContextValidator;
import org.slc.sli.api.service.query.ApiQuery;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;

/**
 * Search service
 *
 */

@Component
public class SearchResourceService {

    private static final int DEFAULT_ES_LIMIT_PER_QUERY = 10;

    private static final int SEARCH_RESULT_LIMIT = 500;

    @Autowired
    DefaultResourceService defaultResourceService;

    @Autowired
    private ResourceHelper resourceHelper;

    @Autowired
    private EdOrgHelper edOrgHelper;

    @Autowired
    private ContextResolverStore contextResolverStore;

    @Value("${sli.search.maxUnfilteredResults:1000}")
    private long maxUnfilteredSearchResultCount;

    @Autowired
    private ContextValidator contextValidator;

    Map<String, IContextValidator> validators = new HashMap<String, IContextValidator>();

    // keep parameters for ElasticSearch
    // q,
    private static final List<String> whiteListParameters = Arrays.asList(new String[] { "q" });

    public ServiceResponse list(Resource resource, String entity, URI queryUri) {

        final EntityDefinition definition = resourceHelper.getEntityDefinition(resource);

        // set up query criteria, make query
        ApiQuery apiQuery = prepareQuery(entity, queryUri);
        if (definition.getService().count(apiQuery) >= maxUnfilteredSearchResultCount) {
            throw new ResponseTooLargeException();
        }
        List<EntityBody> finalEntities = retrieveResults(definition, apiQuery);
        return new ServiceResponse(finalEntities, finalEntities.size());
    }

    public List<EntityBody> retrieveResults(EntityDefinition definition, ApiQuery apiQuery) {

        int limit = apiQuery.getLimit();
        if (limit == 0) {
            limit = SEARCH_RESULT_LIMIT;
        }
        int offset = apiQuery.getOffset();
        int totalLimit = limit + offset;
        int total = 0, newTotal = 0;

        int limitPerQuery = totalLimit * 2;
        if (limitPerQuery < DEFAULT_ES_LIMIT_PER_QUERY) {
            limitPerQuery = DEFAULT_ES_LIMIT_PER_QUERY;
        }
        apiQuery.setLimit(limitPerQuery);
        apiQuery.setOffset(0);

        List<EntityBody> entityBodies = null;
        List<EntityBody> accessible = null;
        ArrayList<EntityBody> finalEntities = new ArrayList<EntityBody>();

        while (total < totalLimit) {

            // call BasicService to query the elastic search repo
            entityBodies = retrieve(apiQuery, definition);

            // filter results through security context
            accessible = checkAccessible(entityBodies);

            // if past offset, add accessible results to final list
            newTotal = total + accessible.size();
            if (newTotal > offset) {
                for (int i=0; i<accessible.size(); i++) {
                    if ((total+i >= offset) && (total+i < totalLimit)) {
                        finalEntities.add(accessible.get(i));
                    }
                }
            }
            total = newTotal;

            // if no more results to grab, then we're done
            if (entityBodies.size() < limitPerQuery) {
                break;
            }

            apiQuery.setOffset(apiQuery.getOffset() + limitPerQuery);
        }

        debug("finalEntities " + finalEntities.size() + " totalLimit " + totalLimit + " offset " + offset);
        return finalEntities;
    }

    public List<EntityBody> retrieve(ApiQuery apiQuery, final EntityDefinition definition) {
        return (List<EntityBody>) definition.getService().list(apiQuery);
    }

    public ApiQuery prepareQuery(String entity, URI queryUri) {
        ApiQuery apiQuery = new ApiQuery(queryUri);
        doFilter(apiQuery);
        addContext(apiQuery);
        if (entity != null) {
            apiQuery.addCriteria(new NeutralCriteria("_type", NeutralCriteria.CRITERIA_IN, entity));
        }
        return apiQuery;
    }

    /**
     * Return list of accessible entities, filtered through the security context.
     * Original list may by cross-collection.
     * Retains the original order of entities.
     *
     * @param entities
     * @return
     */
    public List<EntityBody> checkAccessible(List<EntityBody> entities) {

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


    /**
     * Checks if an entity is accessible
     * @param toType
     * @param id
     * @return
     */
    public boolean isAccessible(String toType, String id) {

        // get and save validator
        IContextValidator validator;

        if (validators.containsKey(toType)) {
            validator = validators.get(toType);
        } else {
            validator = contextValidator.findValidator(toType, false);
            validators.put(toType, validator);
        }

        // validate. if accessible, add to list
        if (validator != null) {
            Set<String> entityIds = new HashSet<String>();
            entityIds.add(id);
            return validator.validate(toType, entityIds);
        }
        return false;
    }

    /**
     * Returns list of accessible ids for one entity type
     * @param toType
     * @return
     */
    public List<String> findAccessible(String toType) {
        Entity user = ((SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEntity();
        EntityContextResolver resolver = contextResolverStore.findResolver(user.getType(), toType);
        if (resolver == null) {
            return new ArrayList<String>();
        } else {
            return resolver.findAccessible(user);
        }
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
        String queryString = ((String) criteria.getValue()).trim().toLowerCase();

        // filter rule:
        // first, token must be at least 1 tokens
        String[] tokens = queryString.split("\\s+");
        if (tokens == null || tokens.length < 1) {
            throw new HttpClientErrorException(HttpStatus.REQUEST_ENTITY_TOO_LARGE);
        }
        criteria.setValue(StringUtils.join(tokens, "* ") + "*");
    }

    private void addContext(ApiQuery apiQuery) {
        SLIPrincipal principal = (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Entity principalEntity = principal.getEntity();
        // get allSchools for staff
        List<String> schoolIds = new ArrayList<String>();
        schoolIds.addAll(edOrgHelper.getUserSchools(principalEntity));
        schoolIds.addAll(edOrgHelper.getDirectSchools(principalEntity));
        schoolIds.add("ALL");
        apiQuery.addCriteria(new NeutralCriteria("context.schoolId", NeutralCriteria.CRITERIA_IN, new ArrayList<String>(schoolIds)));
    }

    public void setMaxUnfilteredSearchResultCount(long maxUnfilteredSearchResultCount) {
        this.maxUnfilteredSearchResultCount = maxUnfilteredSearchResultCount;
    }

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
