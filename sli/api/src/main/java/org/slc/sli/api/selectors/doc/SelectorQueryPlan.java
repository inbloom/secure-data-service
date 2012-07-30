package org.slc.sli.api.selectors.doc;

import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.modeling.uml.ClassType;

import java.util.ArrayList;
import java.util.List;

/**
 * SelectorQueryPlan
 * @author srupasinghe
 */
public class SelectorQueryPlan {
    private ClassType type;
    private NeutralQuery query;
    private List<String> parseFields = new ArrayList<String>();
    private List<String> includeFields = new ArrayList<String>();
    private List<String> excludeFields = new ArrayList<String>();
    private List<Object> childQueryPlans = new ArrayList<Object>();

    public NeutralQuery getQuery() {
        return query;
    }

    public void setQuery(NeutralQuery query) {
        this.query = query;
    }

    public List<Object> getChildQueryPlans() {
        return childQueryPlans;
    }

    public void setChildQueryPlans(List<Object> childQueryPlans) {
        this.childQueryPlans = childQueryPlans;
    }

    public ClassType getType() {
        return type;
    }

    public void setType(ClassType type) {
        this.type = type;
    }

    public List<String> getIncludeFields() {
        return includeFields;
    }

    public void setIncludeFields(List<String> includeFields) {
        this.includeFields = includeFields;
    }

    public List<String> getExcludeFields() {
        return excludeFields;
    }

    public void setExcludeFields(List<String> excludeFields) {
        this.excludeFields = excludeFields;
    }

    public List<String> getParseFields() {
        return parseFields;
    }

    public void setParseFields(List<String> parseFields) {
        this.parseFields = parseFields;
    }
}
