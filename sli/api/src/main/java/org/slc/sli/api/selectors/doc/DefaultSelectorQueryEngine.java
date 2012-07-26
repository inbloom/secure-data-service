package org.slc.sli.api.selectors.doc;

import org.codehaus.plexus.util.StringUtils;
import org.slc.sli.api.selectors.model.*;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.modeling.uml.Type;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Default query engine
 *
 * @author srupasinghe
 *
 */
@Component
public class DefaultSelectorQueryEngine implements SelectorQueryEngine, SelectorQueryVisitor {

    @Override
    public Map<Type, SelectorQueryPlan> assembleQueryPlan(SemanticSelector semanticSelector) {
        return buildQueryPlan(semanticSelector);
    }

    protected Map<Type, SelectorQueryPlan> buildQueryPlan(SemanticSelector semanticSelector) {
        Map<Type, SelectorQueryPlan> queries = new HashMap<Type, SelectorQueryPlan>();

        for (Map.Entry<Type, List<SelectorElement>> entry : semanticSelector.entrySet()) {
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

            NeutralQuery neutralQuery = buildQuery(selectorQuery);

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

    protected NeutralQuery buildQuery(SelectorQuery selectorQuery) {
        NeutralQuery query = new NeutralQuery();

        if (!selectorQuery.getIncludeFields().isEmpty()) {
            query.setIncludeFields(StringUtils.join(selectorQuery.getIncludeFields().iterator(), ","));
        }
        if (!selectorQuery.getExcludeFields().isEmpty()) {
            query.setExcludeFields(StringUtils.join(selectorQuery.getExcludeFields().iterator(), ","));
        }

        return query;
    }

    protected SelectorQuery handleAttributes(List<SelectorElement> attributes) {
        SelectorQuery selectorQuery = new SelectorQuery();

        for (SelectorQueryVisitable visitableSelector : attributes) {
            SelectorQuery newSelectorQuery = visitableSelector.accept(this);

            if (newSelectorQuery != null) {
                selectorQuery.getQueries().addAll(newSelectorQuery.getQueries());
                selectorQuery.getIncludeFields().addAll(newSelectorQuery.getIncludeFields());
            }
        }

        return selectorQuery;
    }

    @Override
    public SelectorQuery visit(SemanticSelector semanticSelector) {
        //TODO
        return null;
    }

    @Override
    public SelectorQuery visit(BooleanSelectorElement booleanSelectorElement) {
        SelectorQuery selectorQuery = new SelectorQuery();

        if (booleanSelectorElement.isAttribute()) {
            String attr = booleanSelectorElement.getElementName();

            if (booleanSelectorElement.getQualifier()) {
                selectorQuery.getIncludeFields().add(attr);
            } else {
                selectorQuery.getExcludeFields().add(attr);
            }
        }

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
        Type type = (Type) includeAllSelectorElement.getLHS();
        Map<Type, SelectorQueryPlan> queries = new HashMap<Type, SelectorQueryPlan>();
        SelectorQuery selectorQuery = new SelectorQuery();
        SelectorQueryPlan plan = new SelectorQueryPlan();

        plan.setQuery(new NeutralQuery());
        queries.put(type, plan);

        selectorQuery.getQueries().add(queries);

        return selectorQuery;
    }


}
