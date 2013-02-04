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


package org.slc.sli.modeling.xmi;

/**
 * Symbolic constants used for attribute names in XMI.
 */
public enum XmiAttributeName {
    /**
     * The identifier used by XMI (xmi.id).
     */
    ID("xmi.id"),
    /**
     * isNavigable.
     */
    IS_NAVIGABLE("isNavigable"),
    /**
     *
     *
     */
    ASSOCIATED_ATTRIBUTE_NAME("associatedAttributeName"),
    /**
     * The lower bound attribute used by XMI (lower).
     */
    LOWER("lower"),
    /**
     * The name attribute used by XMI (name).
     */
    NAME("name"),
    /**
     * The identifier reference used by XMI (xmi.idref).
     */
    IDREF("xmi.idref"),
    /**
     * The upper bound attribute used by XMI (upper).
     */
    UPPER("upper");
    
    private final String localName;
    
    XmiAttributeName(final String localName) {
        this.localName = localName;
    }
    
    public String getLocalName() {
        return localName;
    }
}
