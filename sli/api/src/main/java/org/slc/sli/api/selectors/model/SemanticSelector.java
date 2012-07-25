package org.slc.sli.api.selectors.model;

import org.slc.sli.api.selectors.doc.QueryPlan;
import org.slc.sli.api.selectors.doc.QueryVisitable;
import org.slc.sli.api.selectors.doc.QueryVisitor;
import org.slc.sli.modeling.uml.Type;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author jstokes
 */
public class SemanticSelector extends HashMap<Type, List<SelectorElement>> implements QueryVisitable {

    public void addSelector(final Type type, final SelectorElement se) {
        if (this.containsKey(type)) {
            this.get(type).add(se);
        } else {
            this.put(type, new ArrayList<SelectorElement>(Arrays.asList(se)));
        }
    }

    @Override
    public Map<Type, QueryPlan> accept(QueryVisitor queryVisitor) {
        return queryVisitor.visit(this);
    }
}
