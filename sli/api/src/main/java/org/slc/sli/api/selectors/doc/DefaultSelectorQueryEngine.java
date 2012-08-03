package org.slc.sli.api.selectors.doc;

import org.slc.sli.api.selectors.model.BooleanSelectorElement;
import org.slc.sli.api.selectors.model.ComplexSelectorElement;
import org.slc.sli.api.selectors.model.IncludeAllSelectorElement;
import org.slc.sli.api.selectors.model.ModelProvider;
import org.slc.sli.api.selectors.model.SelectorElement;
import org.slc.sli.api.selectors.model.SemanticSelector;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.Type;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private ModelProvider modelProvider;

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
                queries.get(type).getIncludeFields().addAll(selectorQuery.getIncludeFields());
                queries.get(type).getExcludeFields().addAll(selectorQuery.getExcludeFields());
            } else {
                SelectorQueryPlan plan = new SelectorQueryPlan();
                plan.setQuery(neutralQuery);
                plan.getIncludeFields().addAll(selectorQuery.getIncludeFields());
                plan.getExcludeFields().addAll(selectorQuery.getExcludeFields());

                queries.put(type, plan);
            }
        }

        return queries;

    }

    protected NeutralQuery buildQuery(SelectorQuery selectorQuery) {
        NeutralQuery query = new NeutralQuery();

        return query;
    }

    protected SelectorQuery handleAttributes(List<SelectorElement> attributes) {
        SelectorQuery selectorQuery = new SelectorQuery();

        for (SelectorQueryVisitable visitableSelector : attributes) {
            SelectorQuery newSelectorQuery = visitableSelector.accept(this);

            if (newSelectorQuery != null) {
                selectorQuery.getQueries().addAll(newSelectorQuery.getQueries());
                selectorQuery.getIncludeFields().addAll(newSelectorQuery.getIncludeFields());
                selectorQuery.getExcludeFields().addAll(newSelectorQuery.getExcludeFields());
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
        String attr = booleanSelectorElement.getElementName();

        if (booleanSelectorElement.isAttribute()) {
            if (booleanSelectorElement.getQualifier()) {
                selectorQuery.getIncludeFields().add(attr);
            } else {
                selectorQuery.getExcludeFields().add(attr);
            }
        } else {
            ClassType type = (ClassType) booleanSelectorElement.getLHS();

            Map<Type, SelectorQueryPlan> queries = new HashMap<Type, SelectorQueryPlan>();
            SelectorQueryPlan plan = new SelectorQueryPlan();

            plan.setQuery(new NeutralQuery());
            queries.put(type, plan);

            selectorQuery.getQueries().add(queries);
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
