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

package org.slc.sli.api.selectors.model.elem;

import org.slc.sli.api.selectors.doc.SelectorQueryPlan;
import org.slc.sli.api.selectors.doc.SelectorQueryVisitor;
import org.slc.sli.api.selectors.model.SemanticSelector;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.ModelElement;


/**
 * Implementation of a selector element that has a ModelElement => SemanticSelector structure
 *
 * @author jstokes
 */
public class ComplexSelectorElement extends AbstractSelectorElement implements SelectorElement {
    private final SemanticSelector selector;

    public ComplexSelectorElement(final ModelElement modelElement, final SemanticSelector selector) {
        super.setElement(modelElement);
        super.setTyped(modelElement instanceof ClassType);
        this.selector = selector;
    }

    @Override
    public Object getRHS() {
        return selector;
    }

    @Override
    public SelectorQueryPlan accept(final SelectorQueryVisitor selectorQueryVisitor) {
        return selectorQueryVisitor.visit(this);
    }

    public SemanticSelector getSelector() {
        return selector;
    }

    @Override
    public String toString() {
        return "{" + getElementName() + " : " + getSelector() + "}";
    }
}
