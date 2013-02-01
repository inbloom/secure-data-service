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
    private NeutralQuery query = new NeutralQuery();
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
