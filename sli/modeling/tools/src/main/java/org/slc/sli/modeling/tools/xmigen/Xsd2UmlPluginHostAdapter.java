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
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.ws.commons.schema.XmlSchemaAppInfo;
import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.uml.TaggedValue;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.index.ModelIndex;

/**
 * Intentionally package protected.
 */
final class Xsd2UmlPluginHostAdapter implements Xsd2UmlPluginHost, Xsd2UmlHostedPlugin {
    
    private final ModelIndex mapper;
    
    public Xsd2UmlPluginHostAdapter(final ModelIndex mapper) {
        if (mapper == null) {
            throw new IllegalArgumentException("mapper");
        }
        this.mapper = mapper;
    }

    @Override
    public Xsd2UmlHostedPlugin getPlugin() {
        return this;
    }
    
    @Override
    public Collection<TagDefinition> declareTagDefinitions(final Xsd2UmlPluginHost host) {
        return Collections.emptyList();
    }
    
    @Override
    public Identifier ensureTagDefinitionId(final String name) {
        final TagDefinition tagDefinition = mapper.getTagDefinition(new QName(name));
        if (null != tagDefinition) {
            return tagDefinition.getId();
        }
        throw new IllegalArgumentException(name);
    }
    
    @Override
    public String getAssociationEndTypeName(final ClassType classType, final Attribute attribute,
            final Xsd2UmlPluginHost host) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public TagDefinition getTagDefinition(final Identifier id) {
        return mapper.getTagDefinition(id);
    }
    
    @Override
    public Type getType(final Identifier typeId) {
        return mapper.getType(typeId);
    }
    
    @Override
    public boolean isAssociationEnd(final ClassType classType, final Attribute attribute, final Xsd2UmlPluginHost host) {
        return false;
    }
    
    @Override
    public String nameAssociation(final AssociationEnd lhs, final AssociationEnd rhs, final Xsd2UmlHostedPlugin host) {
        // Associations don't have to be named.
        return lhs.getName() + "<=>" + rhs.getName();
    }
    
    @Override
    public String nameFromComplexTypeExtension(final QName complexType, final QName base) {
        return nameFromSchemaTypeName(complexType).concat(" extends ").concat(nameFromSchemaTypeName(base));
    }
    
    @Override
    public String nameFromSchemaElementName(final QName name) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String nameFromSchemaAttributeName(final QName name) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String nameFromSimpleTypeRestriction(final QName simpleType, final QName base) {
        return nameFromSchemaTypeName(simpleType).concat(" restricts ").concat(nameFromSchemaTypeName(base));
    }
    
    @Override
    public String nameFromSchemaTypeName(final QName name) {
        return name.getLocalPart();
    }
    
    @Override
    public List<TaggedValue> tagsFromAppInfo(final XmlSchemaAppInfo appInfo, final Xsd2UmlPluginHost host) {
        return Collections.emptyList();
    }
}
