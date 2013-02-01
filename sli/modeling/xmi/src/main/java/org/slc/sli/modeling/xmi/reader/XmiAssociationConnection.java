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


package org.slc.sli.modeling.xmi.reader;

import org.slc.sli.modeling.uml.AssociationEnd;

/**
 * This class only exists temporarily during parsing.
 */
final class XmiAssociationConnection {
    
    private final AssociationEnd lhs;
    private final AssociationEnd rhs;
    
    public XmiAssociationConnection(final AssociationEnd lhs, final AssociationEnd rhs) {
        if (lhs == null) {
            throw new IllegalArgumentException("lhs");
        }
        if (rhs == null) {
            throw new IllegalArgumentException("rhs");
        }
        this.lhs = lhs;
        this.rhs = rhs;
    }
    
    public AssociationEnd getLHS() {
        return lhs;
    }
    
    public AssociationEnd getRHS() {
        return rhs;
    }
}
