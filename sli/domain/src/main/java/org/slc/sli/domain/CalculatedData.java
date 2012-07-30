package org.slc.sli.domain;

import java.io.Serializable;
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
public class CalculatedData implements Serializable {
    private static final long serialVersionUID = 1127693471079120104L;
    private final List<CalculatedDatum> calculatedValues;

    public CalculatedData() {
        this(new HashMap<String, Map<String, Map<String, Map<String, Object>>>>());
    }

    //types are fun!
    public CalculatedData(Map<String, Map<String, Map<String, Map<String, Object>>>> calculatedValues) {
        super();
        List<CalculatedDatum> datums = new ArrayList<CalculatedDatum>();
        if (calculatedValues != null) {
            for (Entry<String, Map<String, Map<String, Map<String, Object>>>> typeEntry : calculatedValues.entrySet()) {
                String type = typeEntry.getKey();
                for (Entry<String, Map<String, Map<String, Object>>> nameEntry : typeEntry.getValue().entrySet()) {
                    String name = nameEntry.getKey();
                    for (Entry<String, Map<String, Object>> windowEntry : nameEntry.getValue().entrySet()) {
                        String window = windowEntry.getKey();
                        for (Entry<String, Object> valueEntry : windowEntry.getValue().entrySet()) {
                            String method = valueEntry.getKey();
                            Object value = valueEntry.getValue();
                            datums.add(new CalculatedDatum(type, window, name, method, value));
                        }
                    }
                }
            }
        }
        this.calculatedValues = Collections.unmodifiableList(datums);
    }

    public List<CalculatedDatum> getAggregates() {
        return calculatedValues;
    }

    public List<CalculatedDatum> getCalculatedValues(String type, String window, String methodology, String name) {
        List<CalculatedDatum> aggs = new ArrayList<CalculatedDatum>();
        for (CalculatedDatum datum : calculatedValues) {
            if ((type == null || type.equals(datum.getType())) && (window == null || window.equals(datum.getWindow()))
                    && (methodology == null || methodology.equals(datum.getMethod()))
                    && (name == null || name.equals(datum.getName()))) {
                aggs.add(datum);
            }
        }
        return aggs;
    }


}
