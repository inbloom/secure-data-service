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


package org.slc.sli.modeling.tools.xmigen;

import java.util.HashMap;
import java.util.Map;

import org.slc.sli.modeling.uml.Identifier;

/**
 * W3C XML Schema identifies types by qualified name.
 * XMI wants us to use synthetic identifiers.
 * This class provides the Just-In-Time Lookup.
 * 
 * Intentionally package protected.
 * 
 * @author kmyers
 *
 * @param <T> key type
 */
final class Xsd2UmlLookup<T> {
    
    private final Map<T, Identifier> data = new HashMap<T, Identifier>();
    
    /**
     * Looks up the <code>Identifier</code> from the <code>QName</code>
     * 
     * @param key
     *            The type qualified name. Cannot be <code>null</code>.
     * @return The XMI identifier.
     */
    public Identifier from(final T key) {
        if (key == null) {
            throw new IllegalArgumentException("name");
        }
        if (data.containsKey(key)) {
            return data.get(key);
        } else {
            final Identifier reference = Identifier.random();
            data.put(key, reference);
            return reference;
        }
    }
}
