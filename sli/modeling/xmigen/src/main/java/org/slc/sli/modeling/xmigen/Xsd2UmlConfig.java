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

import java.util.Collection;
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

/**
 * Keeps track of all the things we might need while we are making the transformation.
 * 
 * This class delegates all implementation to the plug-in.
 * 
 * Intentionally package protected.
 */
final class Xsd2UmlConfig implements Xsd2UmlPlugin, Xsd2UmlPluginHost {
    private final Xsd2UmlPlugin plugin;
    /**
     * Provides tag definition identifier from the tag definition name.
     */
    private final Xsd2UmlLookup<String> tagDefinition = new Xsd2UmlLookup<String>();
    
    private final Xsd2UmlLookup<QName> typeId = new Xsd2UmlLookup<QName>();
    
    public Xsd2UmlConfig() {
        this(new Xsd2UmlPluginGeneric());
    }
    
    public Xsd2UmlConfig(final Xsd2UmlPlugin plugin) {
        if (plugin == null) {
            throw new IllegalArgumentException("plugin");
        }
        this.plugin = plugin;
    }
    
    @Override
    public Collection<TagDefinition> declareTagDefinitions(final Xsd2UmlPluginHost host) {
        return plugin.declareTagDefinitions(host);
    }
    
    /**
     * Provides type identifier from schema type qualified name.
     */
    public Identifier ensureId(final QName name) {
        return typeId.from(name);
    }
    
    @Override
    public Identifier ensureTagDefinitionId(final String name) {
        return tagDefinition.from(name);
    }
    
    @Override
    public String getAssociationEndTypeName(final ClassType classType, final Attribute attribute,
            final Xsd2UmlPluginHost host) {
        return plugin.getAssociationEndTypeName(classType, attribute, host);
    }
    
    @Override
    public TagDefinition getTagDefinition(final Identifier id) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Type getType(final Identifier typeId) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean isAssociationEnd(final ClassType classType, final Attribute attribute, final Xsd2UmlPluginHost host) {
        return plugin.isAssociationEnd(classType, attribute, host);
    }
    
    @Override
    public String nameAssociation(final AssociationEnd lhs, final AssociationEnd rhs, final Xsd2UmlPluginHost host) {
        return plugin.nameAssociation(lhs, rhs, host);
    }
    
    @Override
    public String nameFromComplexTypeExtension(final QName complexType, final QName base) {
        if (complexType == null) {
            throw new IllegalArgumentException("complexType");
        }
        if (base == null) {
            throw new IllegalArgumentException("base");
        }
        return plugin.nameFromComplexTypeExtension(complexType, base);
    }
    
    @Override
    public String nameFromSchemaElementName(final QName name) {
        if (name == null) {
            throw new IllegalArgumentException("name");
        }
        return plugin.nameFromSchemaElementName(name);
    }
    
    @Override
    public String nameFromSchemaAttributeName(final QName name) {
        if (name == null) {
            throw new IllegalArgumentException("name");
        }
        return plugin.nameFromSchemaAttributeName(name);
    }
    
    @Override
    public String nameFromSimpleTypeRestriction(final QName simpleType, final QName base) {
        if (simpleType == null) {
            throw new IllegalArgumentException("simpleType");
        }
        if (base == null) {
            throw new IllegalArgumentException("base");
        }
        return plugin.nameFromSimpleTypeRestriction(simpleType, base);
    }
    
    @Override
    public String nameFromSchemaTypeName(final QName name) {
        if (name == null) {
            throw new IllegalArgumentException("name");
        }
        return plugin.nameFromSchemaTypeName(name);
    }
    
    @Override
    public List<TaggedValue> tagsFromAppInfo(XmlSchemaAppInfo appInfo, final Xsd2UmlPluginHost host) {
        if (appInfo == null) {
            throw new IllegalArgumentException("appInfo");
        }
        if (host == null) {
            throw new IllegalArgumentException("host");
        }
        return plugin.tagsFromAppInfo(appInfo, host);
    }
}
