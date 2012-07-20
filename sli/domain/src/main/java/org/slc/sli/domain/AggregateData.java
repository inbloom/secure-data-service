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
    private final Map<String, Map<String, Object>> aggregates;

    public AggregateData() {
        this(new HashMap<String, Map<String, Object>>());
    }

    public AggregateData(Map<String, Map<String, Object>> aggregates) {
        super();
        this.aggregates = aggregates;
    }

    public Collection<String> getAggregatedTypes() {
        return aggregates.keySet();
    }

    public Map<String, Object> getAggregatesForType(String type) {
        return aggregates.get(type);
    }

    @Override
    public String toString() {
        return "AggregateData [aggregates=" + aggregates + ", getAggregatedTypes()=" + getAggregatedTypes() + "]";
    }


}
