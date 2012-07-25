package org.slc.sli.api.selectors.model;

import org.slc.sli.modeling.uml.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author jstokes
 */
public class SemanticSelector extends HashMap<Type, List<SelectorElement>> {

    public void addSelector(final Type type, final SelectorElement se) {
        if (this.containsKey(type)) {
            this.get(type).add(se);
        } else {
            this.put(type, new ArrayList<SelectorElement>(Arrays.asList(se)));
        }
    }
}
