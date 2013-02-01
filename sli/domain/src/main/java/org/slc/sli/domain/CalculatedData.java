/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slc.sli.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Data for Caclulated values
 *
 * @author nbrown
 * @param <T>
 *
 */
public class CalculatedData<T> implements Serializable {
    private static final long serialVersionUID = 1127693471079120104L;
    private final List<CalculatedDatum<T>> calculatedValues;

    public CalculatedData() {
        this(new HashMap<String, Map<String, Map<String, Map<String, T>>>>());
    }

    // types are fun!
    public CalculatedData(Map<String, Map<String, Map<String, Map<String, T>>>> calculatedValues) {
        super();
        List<CalculatedDatum<T>> datums = new ArrayList<CalculatedDatum<T>>();
        if (calculatedValues != null) {
            for (Entry<String, Map<String, Map<String, Map<String, T>>>> typeEntry : calculatedValues.entrySet()) {
                String type = typeEntry.getKey();
                for (Entry<String, Map<String, Map<String, T>>> nameEntry : typeEntry.getValue().entrySet()) {
                    String name = nameEntry.getKey();
                    for (Entry<String, Map<String, T>> windowEntry : nameEntry.getValue().entrySet()) {
                        String window = windowEntry.getKey();
                        for (Entry<String, T> valueEntry : windowEntry.getValue().entrySet()) {
                            String method = valueEntry.getKey();
                            T value = valueEntry.getValue();
                            datums.add(new CalculatedDatum<T>(type, window, name, method, value));
                        }
                    }
                }
            }
        }
        this.calculatedValues = Collections.unmodifiableList(datums);
    }

    // types are fun!
    public CalculatedData(Map<String, Map<String, Map<String, T>>> calculatedValues, String method) {
        super();
        List<CalculatedDatum<T>> datums = new ArrayList<CalculatedDatum<T>>();
        if (calculatedValues != null) {
            for (Entry<String, Map<String, Map<String, T>>> typeEntry : calculatedValues.entrySet()) {
                String type = typeEntry.getKey();
                for (Entry<String, Map<String, T>> nameEntry : typeEntry.getValue().entrySet()) {
                    String name = nameEntry.getKey();
                    for (Entry<String, T> windowEntry : nameEntry.getValue().entrySet()) {
                        String window = windowEntry.getKey();
                        T value = windowEntry.getValue();
                        datums.add(new CalculatedDatum<T>(type, window, name, method, value));
                    }
                }
            }
        }
        this.calculatedValues = Collections.unmodifiableList(datums);
    }

    public List<CalculatedDatum<T>> getCalculatedValues() {
        return calculatedValues;
    }

    public List<CalculatedDatum<T>> getCalculatedValues(String type, String window, String methodology, String name) {
        List<CalculatedDatum<T>> aggs = new ArrayList<CalculatedDatum<T>>();
        for (CalculatedDatum<T> datum : calculatedValues) {
            if ((type == null || type.equals(datum.getType())) && (window == null || window.equals(datum.getWindow()))
                    && (methodology == null || methodology.equals(datum.getMethod()))
                    && (name == null || name.equals(datum.getName()))) {
                aggs.add(datum);
            }
        }
        return aggs;
    }

}
