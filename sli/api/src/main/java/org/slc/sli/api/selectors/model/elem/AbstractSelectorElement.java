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


import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.ModelElement;

/**
 * @author jstokes
 */
public abstract class AbstractSelectorElement {

    private ModelElement modelElement;
    private boolean typed;

    public boolean isTyped() {
        return getTyped();
    }

    public boolean isAttribute() {
        return !getTyped();
    }

    public ModelElement getLHS() {
        return getElement();
    }

    public String getElementName() {
        if (modelElement instanceof ClassType) {
            return ((ClassType) modelElement).getName();
        } else if (modelElement instanceof Attribute) {
            return ((Attribute) modelElement).getName();
        }
        return null;
    }

    protected ModelElement getElement() {
        return modelElement;
    }

    protected void setElement(final ModelElement element) {
        this.modelElement = element;
    }

    protected boolean getTyped() {
        return typed;
    }

    protected void setTyped(final boolean typed) {
        this.typed = typed;
    }
}
