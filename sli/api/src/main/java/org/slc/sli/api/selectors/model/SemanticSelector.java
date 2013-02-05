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

package org.slc.sli.api.selectors.model;

import org.apache.commons.lang.StringUtils;
import org.slc.sli.api.selectors.doc.SelectorQueryPlan;
import org.slc.sli.api.selectors.doc.SelectorQueryVisitable;
import org.slc.sli.api.selectors.doc.SelectorQueryVisitor;
import org.slc.sli.api.selectors.model.elem.SelectorElement;
import org.slc.sli.modeling.uml.Type;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * Convenience wrapper for Selectors that contain type information
 * @author jstokes
 */
public class SemanticSelector extends HashMap<Type, List<SelectorElement>> implements SelectorQueryVisitable {

    private static final long serialVersionUID = -4671870720839644128L;

    public void addSelector(final Type type, final SelectorElement se) {
        if (this.containsKey(type)) {
            this.get(type).add(se);
        } else {
            this.put(type, new ArrayList<SelectorElement>(Arrays.asList(se)));
        }
    }

    @Override
    public SelectorQueryPlan accept(SelectorQueryVisitor selectorQueryVisitor) {
        return selectorQueryVisitor.visit(this);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();

        for (final Map.Entry<Type, List<SelectorElement>> item : this.entrySet()) {
            final List<SelectorElement> elements = item.getValue();
            builder.append("[");
            builder.append(StringUtils.join(elements, ','));
            builder.append("]");
        }

        return builder.toString();
    }
}
