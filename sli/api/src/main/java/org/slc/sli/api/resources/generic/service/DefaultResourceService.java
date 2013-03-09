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
package org.slc.sli.api.resources.generic.service;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slc.sli.api.config.AssociationDefinition;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.constants.ResourceConstants;
import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.criteriaGenerator.GranularAccessFilter;
import org.slc.sli.api.criteriaGenerator.GranularAccessFilterProvider;
import org.slc.sli.api.model.ModelProvider;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.generic.PreConditionFailedException;
import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.resources.generic.representation.ServiceResponse;
import org.slc.sli.api.resources.generic.util.ResourceHelper;
import org.slc.sli.api.resources.util.InProcessDateQueryEvaluator;
import org.slc.sli.api.selectors.LogicalEntity;
import org.slc.sli.api.selectors.UnsupportedSelectorException;
import org.slc.sli.api.service.EntityNotFoundException;
import org.slc.sli.api.service.query.ApiQuery;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.aspect.ApiMigrationAspect.MigratePostedEntity;
import org.slc.sli.aspect.ApiMigrationAspect.MigrateResponse;
import org.slc.sli.common.domain.EmbeddedDocumentRelations;
import org.slc.sli.domain.CalculatedData;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.modeling.uml.ClassType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Default implementation of the resource service.
 *
 * @author srupasinghe
 * @author jstokes
 * @author pghosh
 */

@Component("defaultResourceService")
public class DefaultResourceService implements ResourceService {

    @Autowired
    private LogicalEntity logicalEntity;

    @Autowired
    private ResourceHelper resourceHelper;

    @Autowired
    private ModelProvider provider;

    @Autowired
    private ResourceServiceHelper resourceServiceHelper;

    @Autowired
    private GranularAccessFilterProvider granularAccessFilterProvider;

    @Autowired
    private InProcessDateQueryEvaluator inProcessDateQueryEvaluator;

    public static final int MAX_MULTIPLE_UUIDS = 100;

    /**
     * @author jstokes
     */
    protected static interface ServiceLogic {
        public ServiceResponse run(final Resource resource, EntityDefinition definition);
    }

    protected ServiceResponse handle(final Resource resource, ServiceLogic logic) {
        EntityDefinition definition = resourceHelper.getEntityDefinition(resource);

        ServiceResponse serviceResponse  = logic.run(resource, definition);

        return serviceResponse;
    }

    @Override
    @MigrateResponse
    public ServiceResponse getEntitiesByIds(final Resource resource, final String idList, final URI requestURI) {

        return handle(resource, new ServiceLogic() {
            @Override
            public ServiceResponse run(final Resource resource, EntityDefinition definition) {
                final int idLength = idList.split(",").length;

                if (idLength > MAX_MULTIPLE_UUIDS) {
                    String errorMessage = "Too many GUIDs: " + idLength + " (input) vs "
                            + MAX_MULTIPLE_UUIDS + " (allowed)";
                    throw new PreConditionFailedException(errorMessage);
                }

                final List<String> ids = Arrays.asList(idList.split(","));

                ApiQuery apiQuery = resourceServiceHelper.getApiQuery(definition, requestURI);

                apiQuery.addCriteria(new NeutralCriteria("_id", "in", ids));
                apiQuery.setLimit(0);
                apiQuery.setOffset(0);

                // final/resulting information
                List<EntityBody> finalResults = null;
                try {
                    finalResults = logicalEntity.getEntities(apiQuery, resource.getResourceType());
                } catch (UnsupportedSelectorException e) {
                    finalResults = (List<EntityBody>) definition.getService().list(apiQuery);
                }

                if (idLength == 1 && finalResults.isEmpty()) {
                    throw new EntityNotFoundException(ids.get(0));
                }

                //inject error entities if needed
                finalResults = injectErrors(definition, ids, finalResults);

                return new ServiceResponse(finalResults, idLength);
            }
        });
    }

    @Override
    @MigrateResponse
    public ServiceResponse getEntities(final Resource resource, final URI requestURI,
                                       final boolean getAllEntities) {

        return handle(resource, new ServiceLogic() {
            @Override
            public ServiceResponse run(final Resource resource, final EntityDefinition definition) {
                try {
                    Iterable<EntityBody> entityBodies = null;
                    final ApiQuery apiQuery = resourceServiceHelper.getApiQuery(definition, requestURI);
                    addAdditionalCriteria(apiQuery);
                    addGranularAccessCriteria(definition, apiQuery);

                    if (getAllEntities) {
                        entityBodies = SecurityUtil.sudoRun(new SecurityUtil.SecurityTask<Iterable<EntityBody>>() {

                            @Override
                            public Iterable<EntityBody> execute() {
                                Iterable<EntityBody> entityBodies = null;
                                try {
                                    entityBodies = logicalEntity.getEntities(apiQuery, resource.getResourceType());
                                } catch (UnsupportedSelectorException e) {
                                    entityBodies = definition.getService().list(apiQuery);
                                }

                                return entityBodies;
                            }
                        });
                    } else {
                        try {
                            entityBodies = logicalEntity.getEntities(apiQuery, resource.getResourceType());
                        } catch (UnsupportedSelectorException e) {
                            entityBodies = definition.getService().list(apiQuery);
                        }
                    }
                    long count = getEntityCount(definition, apiQuery);

                    return new ServiceResponse((List<EntityBody>) entityBodies, count);
                } catch (NoGranularAccessDatesException e) {
                    List<EntityBody> entityBodyList = Collections.emptyList();
                    return new ServiceResponse(entityBodyList, 0);
                }
            }
        });
    }

    protected void addAdditionalCriteria(final NeutralQuery apiQuery) {
        // no-op
    }

    protected long getEntityCount(EntityDefinition definition, ApiQuery apiQuery) {
        long count = 0;

        if (definition.getService() == null) {
            return count;
        }

        if (apiQuery == null) {
            return definition.getService().count(new NeutralQuery());
        }

        int originalLimit = apiQuery.getLimit();
        int originalOffset = apiQuery.getOffset();
        apiQuery.setLimit(0);
        apiQuery.setOffset(0);

        count = definition.getService().count(apiQuery);
        apiQuery.setLimit(originalLimit);
        apiQuery.setOffset(originalOffset);

        return count;
    }

    @Override
    @MigratePostedEntity
    public String postEntity(final Resource resource, EntityBody entity) {
        EntityDefinition definition = resourceHelper.getEntityDefinition(resource);

        return definition.getService().create(entity);
    }

    @Override
    @MigratePostedEntity
    public void putEntity(Resource resource, String id, EntityBody entity) {
        EntityDefinition definition = resourceHelper.getEntityDefinition(resource);

        EntityBody copy = new EntityBody(entity);
        copy.remove(ResourceConstants.LINKS);

        definition.getService().update(id, copy);
    }

    @Override
    @MigratePostedEntity
    public void patchEntity(Resource resource, String id, EntityBody entity) {
        EntityDefinition definition = resourceHelper.getEntityDefinition(resource);

        EntityBody copy = new EntityBody(entity);
        copy.remove(ResourceConstants.LINKS);

        definition.getService().patch(id, copy);
    }

    @Override
    public void deleteEntity(Resource resource, String id) {
        EntityDefinition definition = resourceHelper.getEntityDefinition(resource);

        definition.getService().delete(id);
    }

    @Override
    public String getEntityType(Resource resource) {
        return resourceHelper.getEntityDefinition(resource).getType();
    }

    @Override
    public CalculatedData<String> getCalculatedData(Resource resource, String id) {
        EntityDefinition definition = resourceHelper.getEntityDefinition(resource);

        return definition.getService().getCalculatedValues(id);
    }

    @Override
    public CalculatedData<Map<String, Integer>> getAggregateData(Resource resource, String id) {
        EntityDefinition definition = resourceHelper.getEntityDefinition(resource);

        return definition.getService().getAggregates(id);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    @MigrateResponse
    public ServiceResponse getEntities(final Resource base, final String id, final Resource resource, final URI requestURI) {
        final EntityDefinition definition = resourceHelper.getEntityDefinition(resource);

        if (isAssociation(base)) {
            final String key = "_id";
            return getAssociatedEntities(base, base, id, resource, key, requestURI);
        }

        try {
            final String associationKey = getConnectionKey(base, resource);
            List<EntityBody> entityBodyList;
            List<String> valueList = Arrays.asList(id.split(","));

            final ApiQuery apiQuery = resourceServiceHelper.getApiQuery(definition, requestURI);

            addGranularAccessCriteria(definition, apiQuery);

            //Mongo blows up if we have multiple $in or equal criteria for the same key.
            //To avoid that case, if we do have duplicate keys, set the value for that
            //criteria to the intersection of the two critiera values
            boolean skipIn = false;
            for (NeutralCriteria crit : apiQuery.getCriteria()) {
                if (crit.getKey().equals(associationKey)
                        && (crit.getOperator().equals(NeutralCriteria.CRITERIA_IN) || crit.getOperator().equals(NeutralCriteria.OPERATOR_EQUAL))) {
                    skipIn = true;
                    Set valueSet = new HashSet();
                    if (crit.getValue() instanceof Collection) {
                        valueSet.addAll((Collection) crit.getValue());
                    } else {
                        valueSet.add(crit.getValue());
                    }
                    valueSet.retainAll(valueList);
                    crit.setValue(valueSet);
                }
            }

            if (!skipIn) {
                apiQuery.addCriteria(new NeutralCriteria(associationKey, "in", valueList));
            }


            try {
                entityBodyList = logicalEntity.getEntities(apiQuery, definition.getResourceName());
            } catch (final UnsupportedSelectorException e) {
                entityBodyList = (List<EntityBody>) definition.getService().list(apiQuery);
            }

            long count = getEntityCount(definition, apiQuery);
            return new ServiceResponse(entityBodyList, count);
        } catch (NoGranularAccessDatesException e) {
            List<EntityBody> entityBodyList = Collections.emptyList();
            return new ServiceResponse(entityBodyList, 0);
        }
    }

    @Override
    public ServiceResponse getEntities(Resource base, String id, Resource association, Resource resource, URI requestUri) {
        final String associationKey = getConnectionKey(base, association);
        return getAssociatedEntities(base, association, id, resource, associationKey, requestUri);
    }

    private boolean isAssociation(Resource base) {
        boolean isAssociation = false;

        final EntityDefinition baseEntityDef = resourceHelper.getEntityDefinition(base);
        ClassType baseEntityType = provider.getClassType(StringUtils.capitalize(baseEntityDef.getType()));

        if (baseEntityType.isAssociation()) {
            isAssociation = true;
        } else if (baseEntityDef instanceof AssociationDefinition) {
            isAssociation = true;
        }

        return isAssociation;
    }
    private ServiceResponse getAssociatedEntities(final Resource base, final Resource association, final String id,
                                                  final Resource resource, final String associationKey, final URI requestUri) {
        List<String> valueList = Arrays.asList(id.split(","));
        final List<String> filteredIdList = new ArrayList<String>();

        final EntityDefinition finalEntity = resourceHelper.getEntityDefinition(resource);
        final EntityDefinition assocEntity = resourceHelper.getEntityDefinition(association);
        final EntityDefinition baseEntity = resourceHelper.getEntityDefinition(base);

        String resourceKey = getConnectionKey(association, resource);
        String key = "_id";

        try {
            String parentType = EmbeddedDocumentRelations.getParentEntityType(assocEntity.getType());
            if ((parentType != null) && baseEntity.getType().equals(parentType)) {
                final ApiQuery apiQuery = resourceServiceHelper.getApiQuery(baseEntity);

                addGranularAccessCriteria(baseEntity, apiQuery);
                apiQuery.setLimit(0);
                apiQuery.addCriteria(new NeutralCriteria("_id", "in", valueList));
                apiQuery.setEmbeddedFields(Arrays.asList(assocEntity.getType()));

                for (EntityBody entityBody : baseEntity.getService().list(apiQuery)) {
                    @SuppressWarnings("unchecked")
                    List<EntityBody> associations = (List<EntityBody>) entityBody.get(assocEntity.getType());

                    if (associations != null) {
                        if (finalEntityReferencesAssociation(finalEntity, assocEntity, resourceKey)) {
                            //if the finalEntity references the assocEntity
                            for (EntityBody associationEntity : associations) {
                                filteredIdList.add((String) associationEntity.get("id"));
                            }
                            key = resourceKey;
                        } else {
                            //otherwise the assocEntity references the finalEntity

                            List<EntityBody> dateMatchedAssociations = filterAssociationEntitiesForDates(associations, assocEntity);

                            for (EntityBody associationEntity : dateMatchedAssociations) {
                                filteredIdList.add((String) associationEntity.get(resourceKey));
                            }
                        }
                    }
                }
            } else {
                final ApiQuery apiQuery = resourceServiceHelper.getApiQuery(assocEntity);

                addGranularAccessCriteria(assocEntity, apiQuery);

                apiQuery.setLimit(0);
                apiQuery.addCriteria(new NeutralCriteria(associationKey, "in", valueList));
                if (association.getResourceType().equals(ResourceNames.STUDENT_SCHOOL_ASSOCIATIONS)
                        && requestUri.getPath().matches("^/api/rest/[^/]+/schools/[^/]+/studentSchoolAssociations/students")) {
                    apiQuery.addOrQuery(new NeutralQuery(new NeutralCriteria(ParameterConstants.EXIT_WITHDRAW_DATE,
                            NeutralCriteria.CRITERIA_EXISTS, false)));
                    apiQuery.addOrQuery(new NeutralQuery(new NeutralCriteria(ParameterConstants.EXIT_WITHDRAW_DATE,
                            NeutralCriteria.CRITERIA_GTE, DateTime.now().toString(DateTimeFormat.forPattern("yyyy-MM-dd"))
                    )));
                }

                for (EntityBody entityBody : assocEntity.getService().list(apiQuery)) {
                    List<String> filteredIds = entityBody.getValues(resourceKey);
                    if ((filteredIds == null) || (filteredIds.isEmpty())) {
                        key = resourceKey;
                        if (associationKey.equals("_id")) {
                            filteredIdList.addAll(valueList);
                            break;

                        } else {
                            resourceKey = "id";
                            filteredIds = entityBody.getValues(resourceKey);
                        }
                    }
                    filteredIdList.addAll(filteredIds);
                }
            }

            List<EntityBody> entityBodyList;
            final ApiQuery finalApiQuery = resourceServiceHelper.getApiQuery(finalEntity, requestUri);

            addGranularAccessCriteria(finalEntity, finalApiQuery);

            //Mongo blows up if we have multiple $in or equal criteria for the same key.
            //To avoid that case, if we do have duplicate keys, set the value for that
            //criteria to the intersection of the two critiera values
            boolean skipIn = false;
            for (NeutralCriteria crit : finalApiQuery.getCriteria()) {
                if (crit.getKey().equals(key)
                        && (crit.getOperator().equals(NeutralCriteria.CRITERIA_IN) || crit.getOperator().equals(NeutralCriteria.OPERATOR_EQUAL))) {
                    skipIn = true;
                    Set valueSet = new HashSet();
                    if (crit.getValue() instanceof Collection) {
                        valueSet.addAll((Collection) crit.getValue());
                    } else {
                        valueSet.add(crit.getValue());
                    }
                    valueSet.retainAll(filteredIdList);
                    crit.setValue(valueSet);
                }
            }

            if (!skipIn) {
                finalApiQuery.addCriteria(new NeutralCriteria(key, "in", filteredIdList));
            }

            try {
                entityBodyList = logicalEntity.getEntities(finalApiQuery, finalEntity.getResourceName());
            } catch (final UnsupportedSelectorException e) {
                entityBodyList = (List<EntityBody>) finalEntity.getService().list(finalApiQuery);
            }

            long count = getEntityCount(finalEntity, finalApiQuery);

            return new ServiceResponse(entityBodyList, count);
        } catch (NoGranularAccessDatesException e) {
            List<EntityBody> entityBodyList = Collections.emptyList();
            return new ServiceResponse(entityBodyList, 0);
        }
    }

    private boolean finalEntityReferencesAssociation(EntityDefinition finalEntity, EntityDefinition assocEntity, String referenceField) {
        return !assocEntity.getReferenceFields().containsKey(referenceField) && finalEntity.getReferenceFields().containsKey(referenceField);
    }

    private String getConnectionKey(final Resource fromEntity, final Resource toEntity) {
        final EntityDefinition toEntityDef = resourceHelper.getEntityDefinition(toEntity);
        final EntityDefinition fromEntityDef = resourceHelper.getEntityDefinition(fromEntity);

        ClassType fromEntityType = provider.getClassType(StringUtils.capitalize(fromEntityDef.getType()));
        ClassType toEntityType = provider.getClassType(StringUtils.capitalize(toEntityDef.getType()));

        return provider.getConnectionPath(fromEntityType, toEntityType);
    }

    protected List<EntityBody> injectErrors(EntityDefinition definition, final List<String> ids, List<EntityBody> finalResults) {
        int idLength = ids.size();

        if (idLength > 1) {
            Collections.sort(finalResults, new Comparator<EntityBody>() {
                @Override
                public int compare(EntityBody o1, EntityBody o2) {
                    return ids.indexOf(o1.get("id")) - ids.indexOf(o2.get("id"));
                }
            });

            int finalResultsSize = finalResults.size();

            // loop if results quantity does not matched requested quantity
            for (int i = 0; finalResultsSize != idLength && i < idLength; i++) {

                String checkedId = ids.get(i);

                boolean checkedIdMissing = false;

                try {
                    checkedIdMissing = !(finalResults.get(i).get("id").equals(checkedId));
                } catch (IndexOutOfBoundsException ioobe) {
                    checkedIdMissing = true;
                }

                // if a particular input ID is not present in the results at the appropriate
                // spot
                if (checkedIdMissing) {

                    Map<String, Object> errorResult = new HashMap<String, Object>();

                    // try individual lookup to capture specific error message (type)
                    try {
                        definition.getService().get(ids.get(i));
                    } catch (EntityNotFoundException enfe) {
                        errorResult.put("type", "Not Found");
                        errorResult.put("message", "Entity not found: " + checkedId);
                        errorResult.put("code", Response.Status.NOT_FOUND.getStatusCode());
                    } catch (AccessDeniedException ade) {
                        errorResult.put("type", "Forbidden");
                        errorResult.put("message", "Access DENIED: " + ade.getMessage());
                        errorResult.put("code", Response.Status.FORBIDDEN.getStatusCode());
                    } /* catch (Exception e) {
                        errorResult.put("type", "Internal Server Error");
                        errorResult.put("message", "Internal Server Error: " + e.getMessage());
                        errorResult.put("code", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
                    } */

                    finalResults.add(i, new EntityBody(errorResult));
                }
            }
        }

        return finalResults;
    }


    private void addGranularAccessCriteria(EntityDefinition entity, ApiQuery apiQuery) throws NoGranularAccessDatesException {

        if (granularAccessFilterProvider.hasFilter()) {
            GranularAccessFilter filter = granularAccessFilterProvider.getFilter();

            if (entity.getType().equals(filter.getEntityName())) {

                if (filter.isNoSessionsFoundForSchoolYear()) {
                    throw new NoGranularAccessDatesException();
                }

                NeutralQuery dateQuery = filter.getNeutralQuery();
                for (NeutralCriteria criteria : dateQuery.getCriteria()) {
                    apiQuery.addCriteria(criteria);
                }
                for (NeutralQuery dateOrQuery : dateQuery.getOrQueries()) {
                    apiQuery.addOrQuery(dateOrQuery);
                }
            }
        }
    }

    private List<EntityBody> filterAssociationEntitiesForDates(List<EntityBody> originalList, EntityDefinition assocEntity) {

        if (granularAccessFilterProvider.hasFilter()) {
            GranularAccessFilter filter = granularAccessFilterProvider.getFilter();

            if (assocEntity.getType().equals(filter.getEntityName())) {
                // need to filter in java, not using mongo query
                NeutralQuery query = filter.getNeutralQuery();
                List<EntityBody> resultList = new ArrayList<EntityBody>();

                for (EntityBody entity : originalList){

                    if(inProcessDateQueryEvaluator.entitySatisfiesDateQuery(entity,query)){
                        resultList.add(entity);
                    }
                }

                return resultList;
            } else {
                return originalList;
            }
        } else {
            return originalList;
        }
    }


    /**
     * Determines whether a query matches an entity. This does NOT handle all operators,
     * and it assumes that all the fields are strings. This is a minimal filter function
     * that is used to apply the granular access query in Java in the case of embedded
     * docs that are loaded in a batch. This is not intended to be a general purpose query
     * handler.
     *
     * @param entity
     * @param query
     * @return
     */
    private boolean entitySatisfiesDateQuery(EntityBody entity, NeutralQuery query) {
        for (NeutralCriteria andCriteria : query.getCriteria()) {
            String fieldName = andCriteria.getKey();
            String fieldValue = (String) entity.get(fieldName);
            String operator = andCriteria.getOperator();


            if (NeutralCriteria.CRITERIA_EXISTS.equals(operator)) {
                boolean shouldExist = (Boolean) andCriteria.getValue();
                Object actualValue = entity.get(fieldName);
                if (shouldExist && actualValue == null) {
                    return false;
                }
            } else {
                String expectedValue = (String) andCriteria.getValue();
                int comparison = fieldValue.compareTo(expectedValue);
                if(NeutralCriteria.CRITERIA_LT.equals(operator)){

                }
            }

        }
        return true;
    }

    /**
     * Indicates that for a valid granular access query, no sessions and hence
     * no beginDate or endDate were found.
     */
    private class NoGranularAccessDatesException extends Exception {
    }

}
