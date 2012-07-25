package org.slc.sli.api.selectors.doc;

import org.codehaus.plexus.util.StringUtils;
import org.slc.sli.api.selectors.model.*;
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
public class DefaultSelectorDocument implements SelectorDocument, SelectorQueryVisitor {

    @Autowired
    private ModelProvider modelProvider;

    @Override
    public void aggregate(SemanticSelector queryMap, Constraint constraint) {

        if (queryMap == null) return;

        Map<Type, SelectorQueryPlan> queries = buildQueryPlan(queryMap);

        executeQueryPlan(queries, constraint);
    }

    private Map<Type, SelectorQueryPlan> buildQueryPlan(SemanticSelector queryMap) {
        Map<Type, SelectorQueryPlan> queries = new HashMap<Type, SelectorQueryPlan>();

        for (Map.Entry<Type, List<SelectorElement>> entry : queryMap.entrySet()) {
            Type type = entry.getKey();
            List<SelectorElement> attributes = entry.getValue();

            SelectorQuery selectorQuery = handleAttributes(attributes);

            if (!selectorQuery.getQueries().isEmpty()) {
                if (queries.containsKey(type)) {
                    queries.get(type).getChildQueryPlans().addAll(selectorQuery.getQueries());
                } else {
                    SelectorQueryPlan plan = new SelectorQueryPlan();
                    plan.getChildQueryPlans().addAll(selectorQuery.getQueries());

                    queries.put(type, plan);
                }
            }

            NeutralQuery neutralQuery = buildQuery(selectorQuery.getIncludeFields());

            if (queries.containsKey(type)) {
                queries.get(type).setQuery(neutralQuery);
            } else {
                SelectorQueryPlan plan = new SelectorQueryPlan();
                plan.setQuery(neutralQuery);

                queries.put(type, plan);
            }
        }

        return queries;

    }

    protected void executeQueryPlan(Map<Type, SelectorQueryPlan> queryMap, final Constraint constraint) {
        for (Map.Entry<Type, SelectorQueryPlan> entry : queryMap.entrySet()) {
            Type type = entry.getKey();
            SelectorQueryPlan plan = entry.getValue();

            List<String> ids;

            Constraint previousConstraint = new Constraint();
            previousConstraint.setKey(getKeyName(type));
            previousConstraint.setValue(constraint.getValue());
            ids = executeQuery(type, plan.getQuery(), previousConstraint);
            previousConstraint.setValue(ids);

            List<Object> childQueryPlans = plan.getChildQueryPlans();

            for (Object obj : childQueryPlans) {
                executeQueryPlan((Map<Type, SelectorQueryPlan>) obj, previousConstraint);
            }
        }
    }

    protected NeutralQuery buildQuery(List<String> includeFields) {
        NeutralQuery query = new NeutralQuery();
        query.setIncludeFields(StringUtils.join(includeFields.iterator(), ","));

        return query;
    }

    protected SelectorQuery handleAttributes(List<SelectorElement> attributes) {
        SelectorQuery selectorQuery = new SelectorQuery();

        for (SelectorQueryVisitable visitableSelector : attributes) {
            SelectorQuery newSelectorQuery = visitableSelector.accept(this);
            //Map<Type, QueryPlan> queries = ((QueryVisitable) obj).accept(this);
            selectorQuery.getQueries().addAll(newSelectorQuery.getQueries());
            selectorQuery.getIncludeFields().addAll(newSelectorQuery.getIncludeFields());
        }

        return selectorQuery;
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
    public SelectorQuery visit(SemanticSelector semanticSelector) {
        //return buildQueryPlan(semanticSelector);
        return null;
    }

    @Override
    public SelectorQuery visit(BooleanSelectorElement booleanSelectorElement) {
        String attr = booleanSelectorElement.getElementName();
        SelectorQuery selectorQuery = new SelectorQuery();
        selectorQuery.getIncludeFields().add(attr);
        return selectorQuery;
    }

    @Override
    public SelectorQuery visit(ComplexSelectorElement complexSelectorElement) {
        Map<Type, SelectorQueryPlan> queries = buildQueryPlan(complexSelectorElement.getSelector());
        SelectorQuery selectorQuery = new SelectorQuery();
        selectorQuery.getQueries().add(queries);

        return selectorQuery;
    }

    @Override
    public SelectorQuery visit(IncludeAllSelectorElement includeAllSelectorElement) {
        //TODO
        return null;
    }

}
