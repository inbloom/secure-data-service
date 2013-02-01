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

package org.slc.sli.api.selectors.doc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.codehaus.plexus.util.StringUtils;
import org.slc.sli.common.domain.EmbeddedDocumentRelations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.constants.PathConstants;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.model.ModelProvider;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.Type;


/**
 * Default implementation of a selector document
 *
 * @author srupasinghe
 *
 */
@Component
public class DefaultSelectorDocument implements SelectorDocument {

    public static final int EMBEDDED_DOCUMENT_LIMIT = 1000;

    @Autowired
    private ModelProvider modelProvider;

    @Autowired
    private EntityDefinitionStore entityDefs;

    private List<String> defaults = Arrays.asList("id", "entityType", "metaData");

    private interface Counter {
        public void add(int i);
        public int getTotal();
    }

    @Override
    public List<EntityBody> aggregate(SelectorQuery selectorQuery, final NeutralQuery constraint) {

        return executeQueryPlan(selectorQuery, constraint, new ArrayList<EntityBody>(), new Stack<Type>(), true, new MyCounter());

    }

    protected EntityDefinition getEntityDefinition(Type type) {
        return entityDefs.lookupByEntityType(StringUtils.uncapitalise(type.getName()));
    }


    protected List<EntityBody> executeQueryPlan(SelectorQuery selectorQuery, NeutralQuery constraint,
                                          List<EntityBody> previousEntities, Stack<Type> types, boolean first,
                                          Counter counter) {
        NeutralQuery localConstraint = constraint;
        boolean localFirst = first;
        
        List<EntityBody> results = new ArrayList<EntityBody>();
        Map<Type, List<EntityBody>> entityCache = new HashMap<Type, List<EntityBody>>();

        for (Map.Entry<Type, SelectorQueryPlan> entry : selectorQuery.entrySet()) {
            List<EntityBody> connectingEntities = new ArrayList<EntityBody>();
            Type connectingType = null;
            Type currentType = entry.getKey();
            SelectorQueryPlan plan = entry.getValue();
            Type previousType = !types.isEmpty() ? types.peek() : null;
            List<EntityBody> entities = null;

            if (!previousEntities.isEmpty() && previousType != null) {
                if (isEmbedded(previousType, currentType)) {
                    entities = getEmbeddedEntities(previousEntities, currentType);
                } else {
                    String key = getKey(currentType, previousType);
                    plan.getParseFields().add(key);

                    String extractKey = getExtractionKey(currentType, previousType);
                    List<String> ids = extractIds(previousEntities, extractKey);

                    connectingType = modelProvider.getConnectingEntityType(currentType, previousType);
                    if ((connectingType != null) && !currentType.equals(previousType)) {
                        types.push(connectingType);

                        if (isEmbedded(previousType, connectingType)) {
                            connectingEntities = getEmbeddedEntities(previousEntities, connectingType);
                        } else {
                            //construct a new constraint using the new connecting key and ids
                            localConstraint = constructConstrainQuery(getKey(connectingType, previousType), ids);

                            connectingEntities = getConnectingEntities(plan, connectingType, previousType, localConstraint, entityCache);
                        }
                        entityCache.put(connectingType, connectingEntities);
                        ids = getConnectingIds(connectingEntities, currentType, connectingType);
                    }

                    localConstraint = constructConstrainQuery(key, ids);
                }
            }

            //add the current type
            types.push(currentType);
            if (entities == null) {
                entities = (List<EntityBody>) executeQuery(plan, currentType, localConstraint, localFirst, entityCache);
            }
            entityCache.put(currentType, entities);
            results.addAll(entities);

            List<Object> childQueries = plan.getChildQueryPlans();

            for (Object obj : childQueries) {
                List<EntityBody> list = executeQueryPlan((SelectorQuery) obj, localConstraint, entities, types, false, counter);

                //update the entity results
                results = updateEntityList(plan, results, list, types, currentType, counter);
            }

            results = filterFields(results, plan);
            results = updateConnectingEntities(results, connectingEntities, connectingType, currentType, previousType);
            localFirst = false;
        }

        return results;
    }

    private boolean isEmbedded(Type previousType, Type currentType) {
        return (previousType.getName().equalsIgnoreCase(EmbeddedDocumentRelations.getParentEntityType(StringUtils.lowercaseFirstLetter(currentType.getName()))));
    }

    protected List<EntityBody> getEmbeddedEntities(List<EntityBody> previousEntities, Type currentType) {
        List<EntityBody> embeddedBodyList = new ArrayList<EntityBody>();
        String currType = StringUtils.lowercaseFirstLetter(currentType.getName());
        for (EntityBody body : previousEntities) {
            if (body.containsKey(currType)) {
                embeddedBodyList.addAll((Collection<? extends EntityBody>) body.get(currType));
            }
        }
        return embeddedBodyList;
    }

    protected NeutralQuery constructConstrainQuery(String key, List<String> ids) {
        NeutralQuery constraint = new NeutralQuery();
        constraint.addCriteria(new NeutralCriteria(key, NeutralCriteria.CRITERIA_IN, ids));

        return constraint;
    }

    protected List<EntityBody> updateConnectingEntities(List<EntityBody> results, List<EntityBody> connectingEntities, Type connectingType, Type currentType, Type previousType) {
        if (!connectingEntities.isEmpty()) {
            String key = getKey(currentType, connectingType);
            String extractionKey = getExtractionKey(currentType, connectingType);
            String connectingKey = getKey(connectingType, previousType);

            key = key.equals("_id") ? "id" : key;

            for (EntityBody body : connectingEntities) {
                String id = (String) body.get(extractionKey);
                String connectingValue = (String) body.get(connectingKey);

                List<EntityBody> subList = getEntitySubList(results, key, id);

                for (EntityBody sub : subList) {
                    sub.put(connectingKey, connectingValue);
                }
            }
        }

        return results;
    }

    protected List<String> getConnectingIds(List<EntityBody> entities, Type currentType, Type previousType) {
        String extractKey = getExtractionKey(currentType, previousType);

        return extractIds(entities, extractKey);
    }

    protected List<EntityBody> getConnectingEntities(SelectorQueryPlan plan, Type currentType, Type previousType, NeutralQuery constraint, Map<Type, List<EntityBody>> entityCache) {
        String key = getKey(currentType, previousType);

        for (NeutralCriteria criteria : constraint.getCriteria()) {
            criteria.setKey(key);
        }

        return (List<EntityBody>) executeQuery(plan, currentType, constraint, false, entityCache);
    }

    protected boolean isDefaultOrParse(String key, SelectorQueryPlan plan) {
        return defaults.contains(key) || plan.getParseFields().contains(key);
    }

    protected List<EntityBody> filterExcludeFields(List<EntityBody> results, SelectorQueryPlan plan) {

        for (EntityBody body : results) {
            for (String exclude : plan.getExcludeFields()) {
                if (!isDefaultOrParse(exclude, plan)) {
                    body.remove(exclude);
                    body.remove(getExposeName(modelProvider.getClassType(exclude)));
                }
            }
        }

        return results;
    }

    protected List<EntityBody> filterIncludeFields(List<EntityBody> results, SelectorQueryPlan plan) {
        List<EntityBody> filteredList = new ArrayList<EntityBody>();

        for (EntityBody body : results) {
            EntityBody filtered = new EntityBody();
            for (String include : plan.getIncludeFields()) {
                if (body.containsKey(include)) {
                    filtered.put(include, body.get(include));
                }
            }
            filteredList.add(addDefaultsAndParseFields(plan, body, filtered));
        }


        return  filteredList;
    }

    protected List<EntityBody> filterFields(List<EntityBody> results, SelectorQueryPlan plan) {
        List<EntityBody> filteredList = filterIncludeFields(results, plan);
        filteredList = filterExcludeFields(filteredList, plan);

        return filteredList;
    }

    protected EntityBody addDefaultsAndParseFields(SelectorQueryPlan plan, EntityBody body, EntityBody filteredBody) {

        for (String defaultString : defaults) {
            if (body.containsKey(defaultString)) {
                filteredBody.put(defaultString, body.get(defaultString));
            }
        }

        for (String parseField : plan.getParseFields()) {
            if (body.containsKey(parseField)) {
                filteredBody.put(parseField, body.get(parseField));
            }
        }

        return filteredBody;
    }

    protected List<EntityBody> updateEntityList(SelectorQueryPlan plan, List<EntityBody> results, List<EntityBody> entityList,
                                                Stack<Type> types, Type currentType, Counter counter) {
        Type nextType = types.pop();
        String extractionKey, key;

        if (!types.peek().equals(currentType)) {
            Type connectingType = types.pop();
            extractionKey = getExtractionKey(connectingType, currentType);
            key = getKey(connectingType, currentType);

        } else {
            extractionKey = getExtractionKey(nextType, currentType);
            key = getKey(nextType, currentType);
        }

        String exposeName = getExposeName(nextType);
        key = key.equals("_id") ? "id" : key;

        if (exposeName != null) {
            //make sure we save the field we just added
            plan.getParseFields().add(exposeName);

            for (EntityBody body : results) {
                if (body.containsKey(extractionKey)) {
                    for (String id : body.getValues(extractionKey)) {

                        List<EntityBody> subList = getEntitySubList(entityList, key, id);

                        if (!body.containsKey(exposeName)) {
                            body.put(exposeName, subList);
                            counter.add(subList.size());
                            if (counter.getTotal() > DefaultSelectorDocument.EMBEDDED_DOCUMENT_LIMIT) {
                                throw new EmbeddedDocumentLimitException("Exceeded embedded document limit of " + EMBEDDED_DOCUMENT_LIMIT);
                            }
                        }
                    }
                } else {
                    body.put(exposeName, new ArrayList<EntityBody>());
                }
            }
        }

        return results;
    }

    protected String getExposeName(Type type) {
        if (type == null) {
            return null;
        }

        EntityDefinition definition = getEntityDefinition(type);

        return (definition != null) ? PathConstants.TEMP_MAP.get(definition.getResourceName()) : null;
    }

    protected String getExtractionKey(Type currentType, Type previousType) {
        String key = getConnectingKey(currentType, previousType, previousType);

        if (key.contains(StringUtils.uncapitalise(previousType.getName()))) {
            key = "id";
        }

        return key;
    }

    protected String getKey(Type currentType, Type previousType) {
        String key = getConnectingKey(currentType, previousType, currentType);

        if (key.contains(StringUtils.uncapitalise(currentType.getName()))) {
            key = "_id";
        }

        return key;
    }

    protected String getConnectingKey(Type currentType, Type previousType, Type typeToConnect) {
        ClassType currentClassType = modelProvider.getClassType(currentType.getName());
        ClassType previousClassType = modelProvider.getClassType(previousType.getName());

        Type connectingEntity = modelProvider.getConnectingEntityType(currentType, previousType);
        if (connectingEntity == null) {
            return modelProvider.getConnectionPath(previousClassType, currentClassType);
        } else {
            return modelProvider.getConnectionPath((ClassType) connectingEntity, (ClassType) typeToConnect);
        }
    }

    protected Iterable<EntityBody> executeQuery(SelectorQueryPlan plan, Type type, final NeutralQuery constraint, boolean first, Map<Type, List<EntityBody>> entityCache) {
        //add the child types to embedded fields as needed
        addChildTypesToQuery(type, plan, constraint);

        if (first) {
            Iterable<EntityBody> results = getEntityDefinition(type).getService().list(constraint);

            constraint.setLimit(0);
            constraint.setOffset(0);

            return results;
        } else {
            try {
                if (entityCache.containsKey(type)) {
                    return entityCache.get(type);
                }
                EntityDefinition entityDefinition = getEntityDefinition(type);
                if (entityDefinition == null) {
                    return new ArrayList<EntityBody>();
                }
                return entityDefinition.getService().list(constraint);
            } catch (AccessDeniedException ade) {
                return new ArrayList<EntityBody>();
            }
        }
    }

    protected void addChildTypesToQuery(Type currentType, SelectorQueryPlan selectorQueryPlan, NeutralQuery neutralQuery) {
        List<Object> childQueries = selectorQueryPlan.getChildQueryPlans();
        List<String> embeddedFields = new ArrayList<String>();

        for (Object plan : childQueries) {
            SelectorQuery query = (SelectorQuery) plan;

            for (Type key : query.keySet()) {
                String childType = StringUtils.uncapitalise(key.getName());

                if (currentType.getName().equalsIgnoreCase(EmbeddedDocumentRelations.getParentEntityType(childType))) {
                    embeddedFields.add(childType);
                }
            }
        }

        neutralQuery.setEmbeddedFields(embeddedFields);
    }


    protected List<String> extractIds(Iterable<EntityBody> entities, String key) {
        List<String> ids = new ArrayList<String>();

        String localKey = key.equals("_id") ? "id" : key;

        for (EntityBody body : entities) {
            if (body.containsKey(localKey)) {
                ids.addAll(body.getValues(localKey));
            }
        }

        return ids;
    }


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

    protected void setModelProvider(final ModelProvider modelProvider) {
        this.modelProvider = modelProvider;
    }

    /**
     * Counter
     * @author kmyers
     */
    private static final class MyCounter implements Counter {

        private int total = 0;

        @Override
        public void add(int i) {
            this.total += i;
        }

        @Override
        public int getTotal() {
            return this.total;
        }
    }
}
