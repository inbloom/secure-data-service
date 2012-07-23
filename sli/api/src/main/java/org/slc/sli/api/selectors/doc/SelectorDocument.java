package org.slc.sli.api.selectors.doc;

import org.slc.sli.modeling.uml.ClassType;

import java.util.Map;

/**
 * Represents a selector document to be returned
 *
 * @author srupasinghe
 *
 */
public interface SelectorDocument {

    public void aggregate(Map<ClassType, Object> queryMap, Constraint constraint);

}
