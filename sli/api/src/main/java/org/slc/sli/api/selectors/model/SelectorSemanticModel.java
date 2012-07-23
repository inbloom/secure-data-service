package org.slc.sli.api.selectors.model;

import org.slc.sli.modeling.uml.ClassType;

import java.util.Map;

/**
 * Represents a semantic model of the selectors
 *
 * @author srupasinghe
 *
 */
public interface SelectorSemanticModel {

    public Map<ClassType, Object> parse(Map<String, Object> selectors, ClassType type);

}
