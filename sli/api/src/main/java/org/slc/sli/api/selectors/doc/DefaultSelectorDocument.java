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

package org.slc.sli.api.selectors.doc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.constants.PathConstants;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.selectors.model.ModelProvider;
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

    @Autowired
    private ModelProvider modelProvider;

    @Autowired
    private EntityDefinitionStore entityDefs;

    private List<String> defaults = Arrays.asList("id", "entityType", "metaData");

    private int totalEmbeddedEntities;

    @Override
    public List<EntityBody> aggregate(SelectorQuery selectorQuery, final NeutralQuery constraint) {

        this.totalEmbeddedEntities = 0;

        return executeQueryPlan(selectorQuery, constraint, new ArrayList<EntityBody>(), new Stack<Type>(), true);

    }

    protected EntityDefinition getEntityDefinition(Type type) {
        return entityDefs.lookupByEntityType(StringUtils.uncapitalise(type.getName()));
    }


    protected List<EntityBody> executeQueryPlan(SelectorQuery selectorQuery, NeutralQuery constraint,
                                          List<EntityBody> previousEntities, Stack<Type> types, boolean first) {
        List<EntityBody> results = new ArrayList<EntityBody>();

        for (Map.Entry<Type, SelectorQueryPlan> entry : selectorQuery.entrySet()) {
            List<EntityBody> connectingEntities = new ArrayList<EntityBody>();
            Type connectingType = null;
            Type currentType = entry.getKey();
            SelectorQueryPlan plan = entry.getValue();
            Type previousType = !types.isEmpty() ? types.peek() : null;

            if (!previousEntities.isEmpty() && previousType != null) {
                String key = getKey(currentType, previousType);
                plan.getParseFields().add(key);

                String extractKey = getExtractionKey(currentType, previousType);
                List<String> ids = extractIds(previousEntities, extractKey);

                if (ids.isEmpty() && !currentType.equals(previousType)) {
                    connectingType = modelProvider.getConnectingEntityType(currentType, previousType);

                    if (connectingType != null) {
                        types.push(connectingType);

                        connectingEntities = getConnectingEntities(connectingType, previousType, constraint);
                        ids = getConnectingIds(connectingEntities, currentType, connectingType);
                    }
                }

                constraint = constructConstrainQuery(key, ids);
            }

            //add the current type
            types.push(currentType);

            Iterable<EntityBody> entities = executeQuery(currentType, constraint, first);
            results.addAll((List<EntityBody>) entities);

            List<Object> childQueries = plan.getChildQueryPlans();

            for (Object obj : childQueries) {
                Type type = types.peek();

                List<EntityBody> list = executeQueryPlan((SelectorQuery) obj, constraint, (List<EntityBody>) entities, types, false);

                //update the entity results
                results = updateEntityList(plan, results, list, types, currentType);



                if (currentType.isClassType()) {
                    this.totalEmbeddedEntities += list.size();
                    System.out.println("Found " + list.size() + getEntityDefinition(currentType).getResourceName() + " entities.");
                    System.out.println ("count = " + this.totalEmbeddedEntities);
                }
            }

            results = filterFields(results, plan);
            results = updateConnectingEntities(results, connectingEntities, connectingType, currentType, previousType);

            first = false;
        }

        return results;
    }

    protected NeutralQuery constructConstrainQuery(String key, List<String> ids) {
        NeutralQuery constraint = new NeutralQuery();
        constraint.addCriteria(new NeutralCriteria(key, NeutralCriteria.CRITERIA_IN, ids));

        return constraint;
    }

    protected List<EntityBody> updateConnectingEntities(List<EntityBody> results, List<EntityBody> connectingEntities, Type connectingType, Type currentType, Type previousType) {
        if (!connectingEntities.isEmpty()) {
            String key = getKey(currentType, previousType);
            String extractionKey = getExtractionKey(currentType, previousType);
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

    protected List<EntityBody> getConnectingEntities(Type currentType, Type previousType, NeutralQuery constraint) {
        String key = getKey(currentType, previousType);

        for (NeutralCriteria criteria : constraint.getCriteria()) {
            criteria.setKey(key);
        }

        return (List<EntityBody>) executeQuery(currentType, constraint, false);
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
        results = filterIncludeFields(results, plan);
        results = filterExcludeFields(results, plan);

        return results;
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
                                                Stack<Type> types, Type currentType) {
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
                String id = (String) body.get(extractionKey);

                List<EntityBody> subList = getEntitySubList(entityList, key, id);

                body.put(exposeName, subList);
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
        String key = "id";

        if (currentType != null && previousType != null) {
            ClassType currentClassType = modelProvider.getClassType(currentType.getName());
            ClassType previousClassType = modelProvider.getClassType(previousType.getName());

            if (previousClassType.isClassType() && currentClassType.isAssociation()) {
                key = "id";
            } else if (previousClassType.isAssociation() && currentClassType.isClassType()) {
                key = StringUtils.uncapitalise(currentClassType.getName()) + "Id";
            } else if (previousClassType.isClassType() && currentClassType.isClassType()) {
                key = StringUtils.uncapitalise(currentClassType.getName()) + "Id";
            }
        }

        return key;
    }

    protected String getKey(Type currentType, Type previousType) {
        String key = "_id";
        ClassType currentClassType = modelProvider.getClassType(currentType.getName());
        ClassType previousClassType = modelProvider.getClassType(previousType.getName());

        if (previousClassType.isClassType() && currentClassType.isAssociation()) {
            key = StringUtils.uncapitalise(previousClassType.getName()) + "Id";
        } else if (previousClassType.isAssociation() && currentClassType.isClassType()) {
            key = "_id";
        }

        return key;
    }


    protected Iterable<EntityBody> executeQuery(Type type, final NeutralQuery constraint, boolean first) {

        if (first) {
            Iterable<EntityBody> results = getEntityDefinition(type).getService().list(constraint);

            constraint.setLimit(0);
            constraint.setOffset(0);

            return results;
        } else {
            try {
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


    protected List<String> extractIds(Iterable<EntityBody> entities, String key) {
        List<String> ids = new ArrayList<String>();

        key = key.equals("_id") ? "id" : key;

        for (EntityBody body : entities) {
            if (body.containsKey(key)) {
                ids.add((String) body.get(key));
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
}
