package org.slc.sli.api.selectors;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.selectors.doc.Constraint;
import org.slc.sli.api.selectors.doc.SelectorDocument;
import org.slc.sli.api.selectors.doc.SelectorQueryEngine;
import org.slc.sli.api.selectors.doc.SelectorQueryPlan;
import org.slc.sli.api.selectors.model.ModelProvider;
import org.slc.sli.api.selectors.model.SelectorSemanticModel;
import org.slc.sli.api.selectors.model.SemanticSelector;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author jstokes
 */
@Component
public class DefaultLogicalEntity implements LogicalEntity {

    @Autowired
    private ModelProvider provider;

    @Autowired
    private SelectorSemanticModel selectorSemanticModel;

    @Autowired
    private SelectorQueryEngine selectorQueryEngine;

    @Autowired
    private SelectorDocument selectorDocument;

    public List<EntityBody> createEntities(final Map<String, Object> selector, final Constraint constraint,
                                                  final String classType) {

        if (selector == null) throw new NullPointerException("selector");
        if (constraint == null) throw new NullPointerException("constraint");

        // TODO FIXME TODO FIXME TODO FIXME TODO FIXME TODO FIXME TODO FIXME
        final ClassType entityType = provider.getClassType("Student");

        final SemanticSelector semanticSelector = selectorSemanticModel.parse(selector, entityType);
        final Map<Type, SelectorQueryPlan> selectorQuery = selectorQueryEngine.assembleQueryPlan(semanticSelector);

        return selectorDocument.aggregate(selectorQuery, constraint);
    }
}

