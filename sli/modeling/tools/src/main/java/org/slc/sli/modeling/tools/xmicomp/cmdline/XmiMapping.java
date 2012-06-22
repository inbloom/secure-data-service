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


package org.slc.sli.modeling.tools.xmicomp.cmdline;

public final class XmiMapping {
    private final XmiFeature lhs;
    private final XmiFeature rhs;
    private final XmiMappingStatus status;
    private final String comment;
    
    public XmiMapping(final XmiFeature lhs, final XmiFeature rhs, final XmiMappingStatus status, final String comment) {
        if (null == status) {
            throw new NullPointerException("status");
        }
        if (null == comment) {
            throw new NullPointerException("comment");
        }
        this.lhs = lhs;
        this.rhs = rhs;
        this.status = status;
        this.comment = comment;
    }
    
    public XmiFeature getLhs() {
        return lhs;
    }
    
    public XmiFeature getRhs() {
        return rhs;
    }
    
    public XmiMappingStatus getStatus() {
        return status;
    }
    
    public String getComment() {
        return comment;
    }
}