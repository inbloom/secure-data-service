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


package org.slc.sli.modeling.docgen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @param <TYPE> Feature type.
 */
public final class Documentation<TYPE> {
    
    private final List<Domain<TYPE>> domains;
    
    public Documentation(final List<Domain<TYPE>> domains) {
        if (domains == null) {
            throw new IllegalArgumentException("domains");
        }
        this.domains = Collections.unmodifiableList(new ArrayList<Domain<TYPE>>(domains));
    }
    
    public List<Domain<TYPE>> getDomains() {
        return domains;
    }
}
