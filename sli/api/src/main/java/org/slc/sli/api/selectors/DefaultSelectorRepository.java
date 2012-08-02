package org.slc.sli.api.selectors;

import java.util.Map;

public interface DefaultSelectorRepository {
    
    /**
     * Returns the associated default selector, or null if not available.
     */
    public Map<String, Object> getDefaultSelector(String type);
}
