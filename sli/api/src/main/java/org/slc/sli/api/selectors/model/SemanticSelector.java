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
public class SemanticSelector extends HashMap<Type, List<Object>> implements QueryVisitable {

    public void addSelector(final Type type, final Object obj) {
        if (this.containsKey(type)) {
            this.get(type).add(obj);
        } else {
            this.put(type, new ArrayList<Object>(Arrays.asList(obj)));
        }
    }

    @Override
    public Map<Type, QueryPlan> accept(QueryVisitor queryVisitor) {
        return queryVisitor.visit(this);
    }
}
