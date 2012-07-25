package org.slc.sli.api.selectors.doc;

import org.slc.sli.modeling.uml.Type;

import java.util.Map;

/**
 * Visitable interface for building queries
 *
 * @author srupasinghe
 *
 */
public interface QueryVisitable {

    public Map<Type, QueryPlan> accept(QueryVisitor queryVisitor);

}
