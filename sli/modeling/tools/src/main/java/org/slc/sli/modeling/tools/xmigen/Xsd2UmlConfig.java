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
final class Xsd2UmlConfig implements Xsd2UmlPluginHost {
    private final Xsd2UmlHostedPlugin plugin;
    /**
     * Provides tag definition identifier from the tag definition name.
     */
    private final Xsd2UmlLookup<String> tagDefinition = new Xsd2UmlLookup<String>();
    
    private final Xsd2UmlLookup<QName> typeId = new Xsd2UmlLookup<QName>();
    
    public Xsd2UmlConfig() {
        this(new Xsd2UmlPluginGeneric());
    }
    
    public Xsd2UmlConfig(final Xsd2UmlHostedPlugin plugin) {
        if (plugin == null) {
            throw new IllegalArgumentException("plugin");
        }
        this.plugin = plugin;
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
    public TagDefinition getTagDefinition(final Identifier id) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Type getType(final Identifier typeId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Xsd2UmlHostedPlugin getPlugin() {
        return plugin;
    }
}
