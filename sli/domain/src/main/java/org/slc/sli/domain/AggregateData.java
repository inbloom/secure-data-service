package org.slc.sli.domain;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Data for aggregates
 *
 * @author nbrown
 *
 */
public class AggregateData {
    private final Map<String, Map<String, String>> aggregates;

    public AggregateData() {
        this(new HashMap<String, Map<String, String>>());
    }

    public AggregateData(Map<String, Map<String, String>> aggregates) {
        super();
        this.aggregates = aggregates;
    }

    public Collection<String> getAggregatedTypes() {
        return aggregates.keySet();
    }

    public Map<String, String> getAggregatesForType(String type) {
        return aggregates.get(type);
    }
}
