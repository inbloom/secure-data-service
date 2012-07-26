package org.slc.sli.api.selectors.model;

import org.apache.commons.lang.StringUtils;
import org.slc.sli.api.selectors.doc.SelectorQuery;
import org.slc.sli.api.selectors.doc.SelectorQueryVisitable;
import org.slc.sli.api.selectors.doc.SelectorQueryVisitor;
import org.slc.sli.modeling.uml.Type;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * Convenience wrapper for Selectors that contain type information
 * @author jstokes
 */
public class SemanticSelector extends HashMap<Type, List<SelectorElement>> implements SelectorQueryVisitable {

    public void addSelector(final Type type, final SelectorElement se) {
        if (this.containsKey(type)) {
            this.get(type).add(se);
        } else {
            this.put(type, new ArrayList<SelectorElement>(Arrays.asList(se)));
        }
    }

    @Override
    public SelectorQuery accept(SelectorQueryVisitor selectorQueryVisitor) {
        return selectorQueryVisitor.visit(this);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();

        for (final Map.Entry<Type, List<SelectorElement>> item : this.entrySet()) {
            final List<SelectorElement> elements = item.getValue();
            builder.append("[");
            builder.append(StringUtils.join(elements, ','));
            builder.append("]");
        }

        return builder.toString();
    }
}
