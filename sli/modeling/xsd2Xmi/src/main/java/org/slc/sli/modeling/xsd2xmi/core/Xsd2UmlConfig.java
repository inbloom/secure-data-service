package org.slc.sli.modeling.xsd2xmi.core;

import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.ws.commons.schema.XmlSchemaAppInfo;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.uml.TaggedValue;

/**
 * Keeps track of all the things we might need while we are making the transformation.
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
            throw new NullPointerException("plugin");
        }
        this.plugin = plugin;
    }
    
    @Override
    public List<TaggedValue> convertAppInfo(XmlSchemaAppInfo appInfo, final Xsd2UmlPluginHost host) {
        if (appInfo == null) {
            throw new NullPointerException("appInfo");
        }
        if (host == null) {
            throw new NullPointerException("host");
        }
        return plugin.convertAppInfo(appInfo, host);
    }
    
    @Override
    public String convertAttributeName(final QName name) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        return plugin.convertAttributeName(name);
    }
    
    @Override
    public String convertTypeName(final QName name) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        return plugin.convertTypeName(name);
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
    
    public Identifier ensureTagDefinitionId(final String name) {
        return tagDefinition.from(name);
    }
    
    @Override
    public String getAssociationEndTypeName(final ClassType classType, final Attribute attribute,
            final Xsd2UmlPluginHost host) {
        return plugin.getAssociationEndTypeName(classType, attribute, host);
    }
    
    @Override
    public String getGeneralizationNameForComplexTypeExtension(QName complexType, QName base) {
        if (complexType == null) {
            throw new NullPointerException("complexType");
        }
        if (base == null) {
            throw new NullPointerException("base");
        }
        return plugin.getGeneralizationNameForComplexTypeExtension(complexType, base);
    }
    
    @Override
    public String getGeneralizationNameForSimpleTypeRestriction(final QName simpleType, final QName base) {
        if (simpleType == null) {
            throw new NullPointerException("simpleType");
        }
        if (base == null) {
            throw new NullPointerException("base");
        }
        return plugin.getGeneralizationNameForSimpleTypeRestriction(simpleType, base);
    }
    
    @Override
    public TagDefinition getTagDefinition(final Identifier id) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public boolean isAssociationEnd(final ClassType classType, final Attribute attribute, final Xsd2UmlPluginHost host) {
        return plugin.isAssociationEnd(classType, attribute, host);
    }
}
