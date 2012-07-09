/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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


package org.slc.sli.modeling.xmigen;

import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.ws.commons.schema.XmlSchemaAppInfo;

import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.uml.TaggedValue;

/**
 * Used to configure the reverse-engineering of UML from W3C XML Schemas.
 *
 * The conversion of EdFi TitleCase attribute names to camelCase is configurable.
 *
 */
public final class Xsd2UmlPluginForEdFi implements Xsd2UmlPlugin {

    private static final String camelCase(final String text) {
        return text.substring(0, 1).toLowerCase().concat(text.substring(1));
    }

    private final boolean camelCase;

    public Xsd2UmlPluginForEdFi(final boolean camelCase) {
        this.camelCase = camelCase;
    }

    @Override
    public List<TagDefinition> declareTagDefinitions(final Xsd2UmlPluginHost host) {
        return Collections.emptyList();
    }

    @Override
    public String getAssociationEndTypeName(final ClassType classType, final Attribute attribute,
            final Xsd2UmlPluginHost host) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAssociationEnd(final ClassType classType, final Attribute attribute, final Xsd2UmlPluginHost host) {
        // We aren't yet attempting to turn attributes into associations, but we may need to soon in
        // order to be able to compare with the Shared Learning Infrastructure.
        return false;
    }

    @Override
    public String nameAssociation(final AssociationEnd lhs, final AssociationEnd rhs, final Xsd2UmlPluginHost host) {
        return "";
    }

    @Override
    public String nameFromComplexTypeExtension(final QName complexType, final QName base) {
        return complexType.getLocalPart().concat(" extends ").concat(base.getLocalPart());
    }

    @Override
    public String nameFromElementName(final QName name) {
        final String localName = name.getLocalPart();
        if (camelCase) {
            return camelCase(localName);
        } else {
            return localName;
        }
    }

    @Override
    public String nameFromSimpleTypeRestriction(final QName simpleType, final QName base) {
        return simpleType.getLocalPart().concat(" restricts ").concat(base.getLocalPart());
    }

    @Override
    public String nameFromTypeName(final QName name) {
        return name.getLocalPart();
    }

    @Override
    public List<TaggedValue> tagsFromAppInfo(final XmlSchemaAppInfo appInfo, final Xsd2UmlPluginHost host) {
        throw new UnsupportedOperationException();
    }
}
