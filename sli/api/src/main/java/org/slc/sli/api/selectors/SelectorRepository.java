package org.slc.sli.api.selectors;

import org.slc.sli.api.selectors.model.SemanticSelector;


public interface SelectorRepository {
    
    /**
     * Returns the associated default selector, or null if not available.
     */
    public SemanticSelector getSelector(String type);
}
