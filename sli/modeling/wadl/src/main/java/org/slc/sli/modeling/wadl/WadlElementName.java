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


package org.slc.sli.modeling.wadl;

/**
 * Symbolic constants used for element names in XMI.
 */
public enum WadlElementName {
    /**
     * An <code>application</code> element.
     */
    APPLICATION("application"),
    /**
     * A <code>documentation</code> element.
     */
    DOCUMENTATION("doc"),
    /**
     * A <code>fault</code> element.
     */
    FAULT("fault"),
    /**
     * A <code>grammars</code> element.
     */
    GRAMMARS("grammars"),
    /**
     * An <code>include</code> element.
     */
    INCLUDE("include"),
    /**
     * A <code>method</code> element.
     */
    METHOD("method"),
    /**
     * A <code>option</code> element.
     */
    OPTION("option"),
    /**
     * A <code>param</code> element.
     */
    PARAM("param"),
    /**
     * A <code>representation</code> element.
     */
    REPRESENTATION("representation"),
    /**
     * A <code>request</code> element.
     */
    REQUEST("request"),
    /**
     * A <code>resource</code> element.
     */
    RESOURCE("resource"),
    /**
     * A <code>resources</code> element.
     */
    RESOURCES("resources"),
    /**
     * A <code>resource_type</code> element.
     */
    RESOURCE_TYPE("resource_type"),
    /**
     * A <code>response</code> element.
     */
    RESPONSE("response");

    public static final WadlElementName getElementName(final String localName) {
        for (final WadlElementName name : values()) {
            if (name.localName.equals(localName)) {
                return name;
            }
        }
        return null;
    }

    private final String localName;

    WadlElementName(final String localName) {
        if (localName == null) {
            throw new IllegalArgumentException("localName");
        }
        this.localName = localName;
    }

    public String getLocalName() {
        return localName;
    }
}
