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

import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.ws.commons.schema.XmlSchemaAppInfo;
import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.uml.TaggedValue;

/**
 * Plug-in used to customize the W3C XML Schema to UML conversion.
 */
public interface Xsd2UmlPlugin {

    String nameFromComplexTypeExtension(QName complexType, QName base);
    
    /**
     * Converts a WXS schema attribute name into a name in the logical model.
     */
    String nameFromSchemaAttributeName(QName name);
    
    /**
     * Converts a WXS schema element name into a name in the logical model.
     */
    String nameFromSchemaElementName(QName name);
    
    /**
     * Converts a WXS schema type name into a name in the logical model.
     */
    String nameFromSchemaTypeName(QName name);
    
    String nameFromSimpleTypeRestriction(QName simpleType, QName base);
}
