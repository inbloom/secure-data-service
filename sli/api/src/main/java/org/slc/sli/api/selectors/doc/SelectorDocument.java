package org.slc.sli.api.selectors.doc;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.modeling.uml.Type;

import java.util.List;
import java.util.Map;

/**
 * Represents a selector document to be returned
 *
 * @author srupasinghe
 *
 */
public interface SelectorDocument {

    public List<EntityBody> aggregate(Map<Type, SelectorQueryPlan> queryMap, final Constraint constraint);

}
