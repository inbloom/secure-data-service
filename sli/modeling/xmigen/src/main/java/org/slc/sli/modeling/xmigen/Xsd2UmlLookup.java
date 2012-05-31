package org.slc.sli.modeling.xmigen;

import java.util.HashMap;
import java.util.Map;

import org.slc.sli.modeling.uml.Identifier;

/**
 * W3C XML Schema identifies types by qualified name.
 * XMI wants us to use synthetic identifiers.
 * This class provides the Just-In-Time Lookup.
 * 
 * Intentionally package protected.
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
            throw new NullPointerException("name");
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
