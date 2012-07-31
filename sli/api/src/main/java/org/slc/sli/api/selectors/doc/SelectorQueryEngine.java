package org.slc.sli.api.selectors.doc;

import org.slc.sli.api.selectors.model.SemanticSelector;
import org.slc.sli.modeling.uml.Type;

import java.util.Map;

/**
 * Selector query engine
 *
 * @author srupasinghe
 *
 */
public interface SelectorQueryEngine {

    public Map<Type, SelectorQueryPlan> assembleQueryPlan(SemanticSelector semanticSelector);
}
