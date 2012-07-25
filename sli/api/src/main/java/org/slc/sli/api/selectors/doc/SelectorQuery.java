package org.slc.sli.api.selectors.doc;

import org.slc.sli.modeling.uml.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Container class to hold information about a Selector query
 *
 * @author srupasinghe
 *
 */
public class SelectorQuery {
    private List<String> includeFields = new ArrayList<String>();
    private List<Map<Type, SelectorQueryPlan>> queries = new ArrayList<Map<Type, SelectorQueryPlan>>();

    public List<String> getIncludeFields() {
        return includeFields;
    }

    public void setIncludeFields(List<String> includeFields) {
        this.includeFields = includeFields;
    }

    public List<Map<Type, SelectorQueryPlan>> getQueries() {
        return queries;
    }

    public void setQueries(List<Map<Type, SelectorQueryPlan>> queries) {
        this.queries = queries;
    }
}
