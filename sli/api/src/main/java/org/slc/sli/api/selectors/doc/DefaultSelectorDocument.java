package org.slc.sli.api.selectors.doc;

import org.aspectj.weaver.World;
import org.codehaus.plexus.util.StringUtils;
import org.slc.sli.api.selectors.model.ModelProvider;
import org.slc.sli.api.selectors.model.SemanticSelector;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    public void aggregate(SemanticSelector queryMap, Constraint constraint) {

        if (queryMap == null) return;

        Map<Type, QueryPlan> queries = buildQueryPlan(queryMap);

        executeQueryPlan(queries, constraint);
    }

    private Map<Type, QueryPlan> buildQueryPlan(SemanticSelector queryMap) {
        Map<Type, QueryPlan> queries = new HashMap<Type, QueryPlan>();

        for (Map.Entry<Type, List<Object>> entry : queryMap.entrySet()) {

            if (List.class.isInstance(entry.getValue())) {
                Type type = entry.getKey();
                List<Object> attributes = (List<Object>) entry.getValue();
                List<String> includeFields = handleAttributes(type, attributes, queries);

                NeutralQuery query = buildQuery(includeFields);

                if (queries.containsKey(type)) {
                    queries.get(type).setQuery(query);
                } else {
                    QueryPlan plan = new QueryPlan();
                    plan.setQuery(query);

                    queries.put(type, plan);
                }
            }
        }

        return queries;

    }

    protected void executeQueryPlan(Map<Type, QueryPlan> queryMap, final Constraint constraint) {
        for (Map.Entry<Type, QueryPlan> entry : queryMap.entrySet()) {
            Type type = entry.getKey();
            QueryPlan plan = entry.getValue();

            List<String> ids;

            Constraint previousConstraint = new Constraint();
            previousConstraint.setKey(getKeyName(type));
            previousConstraint.setValue(constraint.getValue());
            ids = executeQuery(type, plan.getQuery(), previousConstraint);
            previousConstraint.setValue(ids);

            List<Object> childQueryPlans = plan.getChildQueryPlans();

            for (Object obj : childQueryPlans) {
                executeQueryPlan((Map<Type, QueryPlan>) obj, previousConstraint);
            }
        }
    }

    protected NeutralQuery buildQuery(List<String> includeFields) {
        NeutralQuery query = new NeutralQuery();
        query.setIncludeFields(StringUtils.join(includeFields.iterator(), ","));

        return query;
    }

    protected List<String> handleAttributes(Type type, List<Object> attributes, Map<Type, QueryPlan> parentQueries) {
        List<String> includeFields = new ArrayList<String>();
        for (Object obj : attributes) {
            if (String.class.isInstance(obj)) {
                includeFields.add((String) obj);
            } else if (Map.class.isInstance(obj)) {
                Map<Type, QueryPlan> queries = buildQueryPlan((SemanticSelector) obj);

                if (parentQueries.containsKey(type)) {
                    parentQueries.get(type).getChildQueryPlans().add(queries);
                } else {
                    QueryPlan plan = new QueryPlan();
                    plan.getChildQueryPlans().add(queries);

                    parentQueries.put(type, plan);
                }
            }
        }

        return includeFields;
    }

    protected List<String> executeQuery(Type type, NeutralQuery query, final Constraint constraint) {
        query.addCriteria(new NeutralCriteria(constraint.getKey(),
                NeutralCriteria.CRITERIA_IN, constraint.getValue()));

        System.out.println("Running Query : [" + type.getName() + "], " + query);

        //TODO stubbing for now
        List<String> result = new ArrayList<String>();
        result.add(type.getName() + "_34");
        result.add(type.getName() + "_56");

        return result;
    }

    protected String getKeyName(Type type) {
        String key = "id";

        ClassType classType = modelProvider.getClassType(type.getName());

        if (classType.isAssociation()) {
            AssociationEnd end = classType.getRHS();

            key = StringUtils.uncapitalise(end.getName()) + "Id";
        }

        return key;
    }
}
