package org.slc.sli.api.selectors.doc;

import org.codehaus.plexus.util.StringUtils;
import org.slc.sli.api.selectors.model.ModelProvider;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.ClassType;
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
    public void aggregate(Map<ClassType, Object> queryMap, Constraint constraint) {

        if (queryMap == null) return;

        Map<ClassType, QueryPlan> queries = buildQueryPlan(queryMap);

        executeQueryPlan(queries, constraint);
    }

    private Map<ClassType, QueryPlan> buildQueryPlan(Map<ClassType, Object> queryMap) {
        Map<ClassType, QueryPlan> queries = new HashMap<ClassType, QueryPlan>();

        for (Map.Entry<ClassType, Object> entry : queryMap.entrySet()) {

            if (List.class.isInstance(entry.getValue())) {
                ClassType type = entry.getKey();
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

    protected void executeQueryPlan(Map<ClassType, QueryPlan> queryMap, final Constraint constraint) {
        for (Map.Entry<ClassType, QueryPlan> entry : queryMap.entrySet()) {
            ClassType type = entry.getKey();
            QueryPlan plan = entry.getValue();

            List<String> ids;

            Constraint previousConstraint = new Constraint();
            previousConstraint.setKey(getKeyName(type));
            previousConstraint.setValue(constraint.getValue());
            ids = executeQuery(type, plan.getQuery(), previousConstraint);
            previousConstraint.setValue(ids);

            List<Object> childQueryPlans = plan.getChildQueryPlans();

            for (Object obj : childQueryPlans) {
                executeQueryPlan((Map<ClassType, QueryPlan>) obj, previousConstraint);
            }
        }
    }

    protected NeutralQuery buildQuery(List<String> includeFields) {
        NeutralQuery query = new NeutralQuery();
        query.setIncludeFields(StringUtils.join(includeFields.iterator(), ","));

        return query;
    }

    protected List<String> handleAttributes(ClassType type, List<Object> attributes, Map<ClassType, QueryPlan> parentQueries) {
        List<String> includeFields = new ArrayList<String>();
        for (Object obj : attributes) {
            if (String.class.isInstance(obj)) {
                includeFields.add((String) obj);
            } else if (Map.class.isInstance(obj)) {
                Map<ClassType, QueryPlan> queries = buildQueryPlan((Map<ClassType, Object>) obj);

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

    protected List<String> executeQuery(ClassType type, NeutralQuery query, final Constraint constraint) {
        query.addCriteria(new NeutralCriteria(constraint.getKey(),
                NeutralCriteria.CRITERIA_IN, constraint.getValue()));

        //TODO stubbing for now
        List<String> result = new ArrayList<String>();
        result.add(type.getName() + "_34");
        result.add(type.getName() + "_56");

        return result;
    }

    protected String getKeyName(ClassType type) {
        String key = "id";

        if (type.isAssociation()) {
            AssociationEnd end = type.getRHS();

            key = StringUtils.uncapitalise(end.getName()) + "Id";
        }

        return key;
    }
}
