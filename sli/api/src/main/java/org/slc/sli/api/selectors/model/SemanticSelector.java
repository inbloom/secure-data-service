package org.slc.sli.api.selectors.model;

import org.slc.sli.modeling.uml.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author jstokes
 */
public class SemanticSelector extends HashMap<Type, List<Object>> {

    public void addSelector(final Type type, final Object obj) {
        if (this.containsKey(type)) {
            this.get(type).add(obj);
        } else {
            this.put(type, new ArrayList<Object>(Arrays.asList(obj)));
        }
    }
}
