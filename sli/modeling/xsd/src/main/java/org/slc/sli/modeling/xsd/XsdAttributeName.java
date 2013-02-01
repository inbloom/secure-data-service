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


package org.slc.sli.modeling.xsd;

/**
 * Symbolic constants used for attribute names in W3C XML Schema.
 */
public enum XsdAttributeName {
    /**
     * attributeFormDefault
     */
    ATTRIBUTE_FORM_DEFAULT("attributeFormDefault"),
    /**
     * base
     */
    BASE("base"),
    /**
     * elementFormDefault
     */
    ELEMENT_FORM_DEFAULT("elementFormDefault"),
    /**
     * maxOccurs
     */
    MAX_OCCURS("maxOccurs"),
    /**
     * minOccurs.
     */
    MIN_OCCURS("minOccurs"),
    /**
     * name
     */
    NAME("name"),
    /**
     * type
     */
    TYPE("type"),
    /**
     * value
     */
    VALUE("value");
    
    private final String localName;
    
    XsdAttributeName(final String localName) {
        if (localName == null) {
            throw new IllegalArgumentException("localName");
        }
        this.localName = localName;
    }
    
    public String getLocalName() {
        return localName;
    }
}
