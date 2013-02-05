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

package org.slc.sli.modeling.xmi.comp;

/**
 * Model an XMI mapping.
 */
public final class XmiMapping implements Comparable<XmiMapping> {
    private final XmiFeature lhs;
    private final XmiFeature rhs;
    private final XmiMappingStatus status;
    private final String tracking;
    private final String comment;
    
    public XmiMapping(final XmiFeature lhs, final XmiFeature rhs, final XmiMappingStatus status, final String tracking, final String comment) {
        if (null == status) {
            throw new IllegalArgumentException("status");
        }
        if (null == comment) {
            throw new IllegalArgumentException("comment");
        }
        if (null == tracking) {
            throw new IllegalArgumentException("tracking");
        }
        this.lhs = lhs;
        this.rhs = rhs;
        this.status = status;
        this.tracking = tracking;
        this.comment = comment;
    }
    
    @Override
    public int compareTo(final XmiMapping other) {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof XmiMapping && compareTo((XmiMapping) o) == 0;
    }

    @Override
    public int hashCode() {
        int result = lhs != null ? lhs.hashCode() : 0;
        result = 31 * result + (rhs != null ? rhs.hashCode() : 0);
        result = 31 * result + status.hashCode();
        result = 31 * result + tracking.hashCode();
        result = 31 * result + comment.hashCode();
        return result;
    }

    public XmiFeature getLhsFeature() {
        return lhs;
    }
    
    public XmiFeature getRhsFeature() {
        return rhs;
    }
    
    public XmiMappingStatus getStatus() {
        return status;
    }
    
    public String getComment() {
        return comment;
    }
    
    public String getTracking() {
        return tracking;
    }
    
    @Override
    public String toString() {
        return String.format("{lhs : %s, rhs : %s, status : %s, comment : %s}", lhs, rhs, status, comment);
    }
}
