package org.slc.sli.api.selectors.doc;


import org.slc.sli.api.selectors.model.SemanticSelector;
import org.slc.sli.modeling.uml.Type;

import java.util.Map;

/**
 * Visitor for building queries
 *
 * @author srupasinghe
 *
 */
public interface QueryVisitor {

    public Map<Type, QueryPlan> visit(SemanticSelector semanticSelector);

}
