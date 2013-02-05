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

import org.slc.sli.api.selectors.doc.SelectorQueryVisitable;
import org.slc.sli.modeling.uml.ModelElement;

/**
 * Represents a Selector element that has a left-hand side and a right-hand side
 *
 * @author jstokes
 */
public interface SelectorElement extends SelectorQueryVisitable {
    /**
     * Sentinel value for including all attributes
     */
    public static final String INCLUDE_ALL = "*";

    public static final String INCLUDE_XSD = "$";

    public static final String INCLUDE_DEFAULT = ".";

    public static final String EMPTY = "";

    /**
     *
     * @return true if <code>SelectorElement</code> is typed, false if otherwise
     */
    public boolean isTyped();

    /**
     * @return true is <code>SelectorElement</code> is an attribute, false if otherwise
     */
    public boolean isAttribute();

    /**
     * @return The model element identifier on the left-hand side of the selector element
     */
    public ModelElement getLHS();

    /**
     * @return The value on the right-hand side of the selector element.
     * This could be a <code>boolean</code>, include all element or a <code>SemanticSelector</code>
     */
    public Object getRHS();

    /**
     * @return The string representation of the left hand side of the <code>SelectorElement</code>
     */
    public String getElementName();
}
