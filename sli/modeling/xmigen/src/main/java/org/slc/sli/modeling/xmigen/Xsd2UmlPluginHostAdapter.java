package org.slc.sli.modeling.xmigen;

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
final class Xsd2UmlPluginHostAdapter implements Xsd2UmlPluginHost {

    private final ModelIndex mapper;

    public Xsd2UmlPluginHostAdapter(final ModelIndex mapper) {
        if (mapper == null) {
            throw new NullPointerException("mapper");
        }
        this.mapper = mapper;
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
    public String nameAssociation(final AssociationEnd lhs, final AssociationEnd rhs, final Xsd2UmlPluginHost host) {
        // Associations don't have to be named.
        return lhs.getName() + "<=>" + rhs.getName();
    }

    @Override
    public String nameFromComplexTypeExtension(final QName complexType, final QName base) {
        return nameFromTypeName(complexType).concat(" extends ").concat(nameFromTypeName(base));
    }

    @Override
    public String nameFromElementName(final QName name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String nameFromSimpleTypeRestriction(final QName simpleType, final QName base) {
        return nameFromTypeName(simpleType).concat(" restricts ").concat(nameFromTypeName(base));
    }

    @Override
    public String nameFromTypeName(final QName name) {
        return name.getLocalPart();
    }

    @Override
    public List<TaggedValue> tagsFromAppInfo(final XmlSchemaAppInfo appInfo, final Xsd2UmlPluginHost host) {
        return Collections.emptyList();
    }
}
