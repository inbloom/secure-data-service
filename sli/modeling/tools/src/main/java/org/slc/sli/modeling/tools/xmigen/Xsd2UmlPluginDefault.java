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

package org.slc.sli.modeling.tools.xmigen;

import javax.xml.namespace.QName;

/**
 * Provides default behavior, though implementation inheritance for a plug-in.
 */
public abstract class Xsd2UmlPluginDefault implements Xsd2UmlHostedPlugin {
    
    @Override
    public final String nameFromComplexTypeExtension(final QName complexType, final QName base) {
        if (complexType == null) {
            throw new IllegalArgumentException("complexType");
        }
        if (base == null) {
            throw new IllegalArgumentException("base");
        }
        return nameFromSchemaTypeName(complexType).concat(" extends ").concat(nameFromSchemaTypeName(base));
    }
    
    /**
     * Retains only the local-name part of the qualified name.
     */
    @Override
    public String nameFromSchemaAttributeName(final QName name) {
        if (name == null) {
            throw new IllegalArgumentException("name");
        }
        return name.getLocalPart();
    }
    
    /**
     * Retains only the local-name part of the qualified name.
     */
    @Override
    public String nameFromSchemaElementName(final QName name) {
        if (name == null) {
            throw new IllegalArgumentException("name");
        }
        return name.getLocalPart();
    }
    
    /**
     * Retains only the local-name part of the qualified name.
     */
    @Override
    public String nameFromSchemaTypeName(final QName name) {
        if (name == null) {
            throw new IllegalArgumentException("name");
        }
        return name.getLocalPart();
    }
    
    @Override
    public final String nameFromSimpleTypeRestriction(final QName simpleType, final QName base) {
        if (simpleType == null) {
            throw new IllegalArgumentException("simpleType");
        }
        if (base == null) {
            throw new IllegalArgumentException("base");
        }
        return nameFromSchemaTypeName(simpleType).concat(" restricts ").concat(nameFromSchemaTypeName(base));
    }
}
