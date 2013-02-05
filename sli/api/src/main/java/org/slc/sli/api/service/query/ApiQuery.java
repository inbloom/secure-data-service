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

package org.slc.sli.api.service.query;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;

import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

/**
 * Converts a String into a database independent NeutralQuery object.
 * The API uses URIs to provide a place for query input.
 *
 * @author kmyers
 *
 */
public class ApiQuery extends NeutralQuery {

    private static final UriInfoToApiQueryConverter QUERY_CONVERTER = new UriInfoToApiQueryConverter();

    public static final int API_QUERY_DEFAULT_LIMIT = 50;

    private Map<String, Object> selector = null;

    /* General default. Used when a more specific default selector is not available. */
    public static final Map<String, Object> DEFAULT_SELECTOR = new HashMap<String, Object>();
    static {
        DEFAULT_SELECTOR.put(".", true);
    }

    /**
     * Constructor. Reads the query portion of the URI into a neutral query (this).
     *
     * @param uriInfo
     */
    public ApiQuery(UriInfo uriInfo) {
        this(uriInfo.getRequestUri());
    }

    public ApiQuery(URI requestURI) {
        super(API_QUERY_DEFAULT_LIMIT);
        if (requestURI != null) {
            ApiQuery.QUERY_CONVERTER.convert(this, requestURI);
        }
    }

    public ApiQuery() {
        this((URI) null);
    }

    protected String toSelectorString(Map<?, ?> map) {
        StringBuffer selectorStringBuffer = new StringBuffer();
        selectorStringBuffer.append(":(");

        boolean first = true;
        for (Entry<?, ?> entry : map.entrySet()) {
            if (!first) {
                selectorStringBuffer.append(",");
            }
            first = false;
            selectorStringBuffer.append(entry.getKey().toString());
            if (entry.getValue() instanceof Map) {
                selectorStringBuffer.append(this.toSelectorString((Map<?, ?>) entry.getValue()));
            } else if (entry.getValue() instanceof Boolean) {
                Boolean b = (Boolean) entry.getValue();
                if (!b) {
                    selectorStringBuffer.append(":false");
                }
            }
        }

        selectorStringBuffer.append(")");
        return selectorStringBuffer.toString();
    }

    @Override
    public String toString() {
        StringBuilder stringBuffer = new StringBuilder("offset=" + getOffset() + "&limit=" + getLimit());

        if (getIncludeFields() != null) {
            stringBuffer.append("&includeFields=" + StringUtils.join(getIncludeFields(), ","));
        }

        if (getExcludeFields() != null) {
            stringBuffer.append("&excludeFields=" + StringUtils.join(getExcludeFields(), ","));
        }

        if (getSortBy() != null) {
            stringBuffer.append("&sortBy=" + getSortBy());
        }

        if (getSortOrder() != null) {
            stringBuffer.append("&sortOrder=" + getSortOrder());
        }

        if (this.selector != null) {
            stringBuffer.append("&selector=" + this.toSelectorString(this.selector));
        }

        for (NeutralCriteria neutralCriteria : getCriteria()) {
            stringBuffer.append("&" + neutralCriteria.getKey() + neutralCriteria.getOperator()
                    + neutralCriteria.getValue());
        }

        return stringBuffer.toString();
    }

    public Map<String, Object> getSelector() {
        return selector;
    }

    public void setSelector(Map<String, Object> selector) {
        this.selector = selector;
    }
}
