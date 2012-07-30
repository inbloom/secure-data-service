package org.slc.sli.api.selectors.doc;

import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.modeling.uml.ClassType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: srupasinghe
 * Date: 7/24/12
 * Time: 1:32 PM
 * To change this template use File | Settings | File Templates.
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
