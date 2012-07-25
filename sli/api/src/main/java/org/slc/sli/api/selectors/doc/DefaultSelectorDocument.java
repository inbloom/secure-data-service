package org.slc.sli.api.selectors.doc;

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
public class DefaultSelectorDocument implements SelectorDocument, QueryVisitor {

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
            Type type = entry.getKey();
            List<Object> attributes = entry.getValue();

            Container container = handleAttributes(attributes);

            if (!container.getQueries().isEmpty()) {
                if (queries.containsKey(type)) {
                    queries.get(type).getChildQueryPlans().addAll(container.getQueries());
                } else {
                    QueryPlan plan = new QueryPlan();
                    plan.getChildQueryPlans().addAll(container.getQueries());

                    queries.put(type, plan);
                }
            }

            NeutralQuery query = buildQuery(container.getIncludeFields());

            if (queries.containsKey(type)) {
                queries.get(type).setQuery(query);
            } else {
                QueryPlan plan = new QueryPlan();
                plan.setQuery(query);

                queries.put(type, plan);
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

    protected Container handleAttributes(List<Object> attributes) {
        Container container = new Container();

        for (Object obj : attributes) {
            if (obj instanceof QueryVisitable) {
                Map<Type, QueryPlan> queries = ((QueryVisitable) obj).accept(this);
                container.getQueries().add(queries);
            } else if (String.class.isInstance(obj)) {
                container.getIncludeFields().add((String) obj);
            }
        }

        return container;
    }

    class Container {
        private List<String> includeFields = new ArrayList<String>();
        private List<Map<Type, QueryPlan>> queries = new ArrayList<Map<Type, QueryPlan>>();

        public List<String> getIncludeFields() {
            return includeFields;
        }

        public void setIncludeFields(List<String> includeFields) {
            this.includeFields = includeFields;
        }

        public List<Map<Type, QueryPlan>> getQueries() {
            return queries;
        }

        public void setQueries(List<Map<Type, QueryPlan>> queries) {
            this.queries = queries;
        }
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

    @Override
    public Map<Type, QueryPlan> visit(SemanticSelector semanticSelector) {
        return buildQueryPlan(semanticSelector);
    }

}
