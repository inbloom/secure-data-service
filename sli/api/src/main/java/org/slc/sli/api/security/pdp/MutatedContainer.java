/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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

package org.slc.sli.api.security.pdp;

import com.sun.jersey.core.header.InBoundHeaders;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class MutatedContainer {

    private String path;
    private String queryParameters;
    private InBoundHeaders headers;
    boolean usePrincipleId;

    public MutatedContainer(String path, String queryParameters, InBoundHeaders headers) {
        this.path = path;
        this.queryParameters = queryParameters;
        this.headers = headers;
    }

    public MutatedContainer(String path, String queryParameters) {
        this.path = path;
        this.queryParameters = queryParameters;
        this.headers = null;
    }

    public MutatedContainer(String mutatedPathFormat, String mutatedParameter, boolean usePrincipleId) {
        this.path = mutatedPathFormat;
        this.queryParameters = mutatedParameter;
        this.usePrincipleId = usePrincipleId;
    }

    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getQueryParameters() {
        return queryParameters;
    }
    public void setQueryParameters(String queryParameters) {
        this.queryParameters = queryParameters;
    }
    public InBoundHeaders getHeaders() {
        return headers;
    }
    public void setHeaders(InBoundHeaders headers) {
        this.headers = headers;
    }

    public boolean isUsePrincipleId() {
        return usePrincipleId;
    }

    public void setUsePrincipleId(boolean usePrincipleId) {
        this.usePrincipleId = usePrincipleId;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
            append(path).
            append(queryParameters).
            append(headers).
            toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }

        MutatedContainer rhs = (MutatedContainer) obj;
        return new EqualsBuilder().
            append(path, rhs.path).
            append(queryParameters, rhs.queryParameters).
            append(headers, rhs.headers).
            isEquals();
    }
}
