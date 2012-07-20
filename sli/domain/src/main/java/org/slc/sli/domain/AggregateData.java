package org.slc.sli.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Data for aggregates
 *
 * @author nbrown
 *
 */
public class AggregateData {
    private final List<AggregateDatum> aggregates;

    public AggregateData() {
        this(new HashMap<String, Map<String, Map<String, Map<String, Object>>>>());
    }

    //types are fun!
    public AggregateData(Map<String, Map<String, Map<String, Map<String, Object>>>> aggregateData) {
        super();
        List<AggregateDatum> datums = new ArrayList<AggregateDatum>();
        if (aggregateData != null) {
            for (Entry<String, Map<String, Map<String, Map<String, Object>>>> typeEntry : aggregateData.entrySet()) {
                String type = typeEntry.getKey();
                for (Entry<String, Map<String, Map<String, Object>>> nameEntry : typeEntry.getValue().entrySet()) {
                    String name = nameEntry.getKey();
                    for (Entry<String, Map<String, Object>> windowEntry : nameEntry.getValue().entrySet()) {
                        String window = windowEntry.getKey();
                        for (Entry<String, Object> valueEntry : windowEntry.getValue().entrySet()) {
                            String method = valueEntry.getKey();
                            Object value = valueEntry.getValue();
                            datums.add(new AggregateDatum(type, window, name, method, value));
                        }
                    }
                }
            }
        }
        this.aggregates = Collections.unmodifiableList(datums);
    }

    public List<AggregateDatum> getAggregates() {
        return aggregates;
    }

}
