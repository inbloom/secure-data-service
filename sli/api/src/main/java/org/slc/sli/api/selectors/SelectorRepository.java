package org.slc.sli.api.selectors;

import org.slc.sli.api.selectors.model.SemanticSelector;

/**
 * Returns a selector appropriate to the logical model for the given type.
 *
 *
 * @author kmyers
 *
 */
public interface SelectorRepository {

    /**
     * Returns the associated default selector, or null if not available.
     */
    public SemanticSelector getSelector(String type);
}
