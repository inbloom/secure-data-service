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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Model a comparision between Xmi's
 */
public final class XmiComparison {
    
    private final XmiDefinition lhsRef;
    private final XmiDefinition rhsRef;
    private final List<XmiMapping> mappings;
    
    public XmiComparison(final XmiDefinition lhsRef, final XmiDefinition rhsRef, final List<XmiMapping> mappings) {
        if (lhsRef == null) {
            throw new IllegalArgumentException("lhsRef");
        }
        if (rhsRef == null) {
            throw new IllegalArgumentException("rhsRef");
        }
        this.lhsRef = lhsRef;
        this.rhsRef = rhsRef;
        this.mappings = Collections.unmodifiableList(new ArrayList<XmiMapping>(mappings));
    }
    
    public XmiDefinition getLhsDef() {
        return lhsRef;
    }
    
    public XmiDefinition getRhsDef() {
        return rhsRef;
    }
    
    public List<XmiMapping> getMappings() {
        return mappings;
    }
}
