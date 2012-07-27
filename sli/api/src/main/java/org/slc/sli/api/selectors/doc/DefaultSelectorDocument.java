package org.slc.sli.api.selectors.doc;

import org.codehaus.plexus.util.StringUtils;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.selectors.model.*;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.modeling.uml.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

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

    @Override
    public List<EntityBody> aggregate(Map<Type, SelectorQueryPlan> queryMap, final Constraint constraint) {

        return executeQueryPlan(queryMap, constraint, new ArrayList<EntityBody>(), new Stack<Type>());

    }

    protected EntityDefinition getEntityDefinition(Type type) {
        return entityDefs.lookupByEntityType(StringUtils.uncapitalise(type.getName()));
    }

    protected List<EntityBody> executeQueryPlan(Map<Type, SelectorQueryPlan> queryPlan, Constraint constraint,
                                          List<EntityBody> previousEntities, Stack<Type> types) {
        List<EntityBody> results = new ArrayList<EntityBody>();

        for (Map.Entry<Type, SelectorQueryPlan> entry : queryPlan.entrySet()) {
            Type currentType = entry.getKey();
            SelectorQueryPlan plan = entry.getValue();
            Type previousType = !types.isEmpty()? types.peek() : null;

            //add the current type
            types.push(currentType);

            if (!previousEntities.isEmpty() && previousType != null) {
                String key = getKey(currentType, previousType);
                constraint.setKey(key);

                String extractKey = getExtractionKey(currentType, previousType);
                List<String> ids = extractIds(previousEntities, extractKey);
                constraint.setValue(ids);
            }

            Iterable<EntityBody> entities = executeQuery(currentType, plan.getQuery(), constraint, true);
            results.addAll((List<EntityBody>) entities);

            List<Object> childQueries = plan.getChildQueryPlans();

            for (Object obj : childQueries) {
                List<EntityBody> list = executeQueryPlan((Map<Type, SelectorQueryPlan>) obj, constraint, (List<EntityBody>) entities, types);

                //update the entity results
                results = updateEntityList(results, list, types, currentType);
            }
        }

        return results;
    }

    protected List<EntityBody> updateEntityList(List<EntityBody> results, List<EntityBody> entityList,
                                                Stack<Type> types, Type currentType) {
        Type nextType = types.pop();
        String extractionKey = getExtractionKey(nextType, currentType);
        String key = getKey(nextType, currentType);
        key = key.equals("_id") ? "id" : key;

        for (EntityBody body : results) {
            String id = (String) body.get(extractionKey);

            List<EntityBody> subList = getEntitySubList(entityList, key, id);
            body.put(nextType.getName(), subList);
        }

        return results;
    }

    protected String getExtractionKey(Type currentType, Type previousType) {
        String key = "id";
        ClassType currentClassType = modelProvider.getClassType(currentType.getName());
        ClassType previousClassType = modelProvider.getClassType(previousType.getName());

        if (previousClassType.isClassType() && currentClassType.isAssociation()) {
            key = "id";
        } else if (previousClassType.isAssociation() && currentClassType.isClassType()) {
            key = StringUtils.uncapitalise(currentClassType.getName()) + "Id";
        }

        return key;
    }

    protected String getKey(Type currentType, Type previousType) {
        String key = "id";
        ClassType currentClassType = modelProvider.getClassType(currentType.getName());
        ClassType previousClassType = modelProvider.getClassType(previousType.getName());

        if (previousClassType.isClassType() && currentClassType.isAssociation()) {
            key = StringUtils.uncapitalise(previousClassType.getName()) + "Id";
        } else if (previousClassType.isAssociation() && currentClassType.isClassType()) {
            key = "_id";
        }

        return key;
    }


    protected Iterable<EntityBody> executeQuery(Type type, NeutralQuery query, final Constraint constraint, final boolean inLine) {
        query.addCriteria(new NeutralCriteria(constraint.getKey(),
                NeutralCriteria.CRITERIA_IN, constraint.getValue(), inLine));

        Iterable<EntityBody> results = getEntityDefinition(type).getService().list(query);

        return results;
    }


    protected List<String> extractIds(Iterable<EntityBody> entities, String key) {
        List<String> ids = new ArrayList<String>();

        key = key.equals("_id") ? "id" : key;

        for (EntityBody body : entities) {
            ids.add((String) body.get(key));
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
