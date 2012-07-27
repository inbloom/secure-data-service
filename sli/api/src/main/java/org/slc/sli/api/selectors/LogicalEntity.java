package org.slc.sli.api.selectors;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.selectors.doc.Constraint;

import java.util.List;
import java.util.Map;

/**
 * @author jstokes
 */
public interface LogicalEntity {
    public List<EntityBody> createEntities(Map<String, Object> selector, Constraint constraint,
                                           String classType);
}
