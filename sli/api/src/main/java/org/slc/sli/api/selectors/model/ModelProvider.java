package org.slc.sli.api.selectors.model;

import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.Model;
import org.slc.sli.modeling.uml.ModelElement;
import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.index.DefaultModelIndex;
import org.slc.sli.modeling.uml.index.ModelIndex;
import org.slc.sli.modeling.xmi.reader.XmiReader;
import org.springframework.stereotype.Component;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.Set;

/**
 * Provides information from the model about Associations and Attributes
 * @author jstokes
 */

@Component
public final class ModelProvider {
    private final ModelIndex modelIndex;
    private final static String DEFAULT_XMI_LOC = "/sliModel/SLI.xmi";

    public ModelProvider(final String xmiLoc) {
        final Model model = XmiReader.readModel(getClass().getResourceAsStream(xmiLoc));
        modelIndex = new DefaultModelIndex(model);
    }

    public ModelProvider() {
        this(DEFAULT_XMI_LOC);
    }

    public ModelProvider(final ModelIndex modelIndex) {
        this.modelIndex = modelIndex;
    }

    public List<AssociationEnd> getAssociationEnds(final Identifier type) {
        return modelIndex.getAssociationEnds(type);
    }

    public Set<ModelElement> lookupByName(final QName qName) {
        return modelIndex.lookupByName(qName);
    }

    public TagDefinition getTagDefinition(final Identifier id) {
        return modelIndex.getTagDefinition(id);
    }

    public Type getType(final Identifier id) {
        return modelIndex.getType(id);
    }

    public ClassType getClassType(final String typeName) {
        return modelIndex.getClassTypes().get(typeName);
    }

    public ClassType getClassType(final ClassType type, final String attr) {
        if (isAssociation(type, attr)) {
            return getAssociationType(type, attr);
        } else if (isAttribute(type, attr)) {
            return getEmbeddedClassType(type, attr);
        }
        return null;
    }

    public boolean isAttribute(final ClassType type, final String attributeName) {
        if (type == null) throw new NullPointerException("type");
        if (attributeName == null) throw new NullPointerException("attributeName");

        final List<Attribute> attributes = type.getAttributes();
        for (final Attribute attribute : attributes) {
            if (attribute.getName().equals(attributeName)) {
                return true;
            }
        }
        return false;
    }

    public boolean isAssociation(final ClassType type, final String attribute) {
        if (type == null) throw new NullPointerException("type");
        if (attribute == null) throw new NullPointerException("attribute");

        final List<AssociationEnd> associationEnds = getAssociationEnds(type.getId());
        for (final AssociationEnd end : associationEnds) {
            if (end.getName().equals(attribute)) {
                return true;
            }
        }
        return false;
    }

    public Set<ModelElement> whereUsed(Identifier identifier) {
        return modelIndex.whereUsed(identifier);
    }

    public ModelIndex getModelIndex() {
        return modelIndex;
    }

    public Attribute getAttributeType(final ClassType type, final String attr) {
        final List<Attribute> attributes = type.getAttributes();
        for (final Attribute attribute : attributes) {
            if (attribute.getName().equals(attr)) {
                return attribute;
            }
        }
        return null;
    }

    private ClassType getEmbeddedClassType(final ClassType type, final String attr) {
        final List<Attribute> attributes = type.getAttributes();
        for (final Attribute attribute : attributes) {
            if (attribute.getName().equals(attr)) {
                final Type embeddedType = getType(attribute.getType());
                if (embeddedType.isClassType()) return (ClassType) embeddedType;
                else break;
            }
        }
        return null;
    }

    private ClassType getAssociationType(final ClassType type, final String attr) {
        final List<AssociationEnd> associationEnds = getAssociationEnds(type.getId());
        for (final AssociationEnd end : associationEnds) {
            if (end.getName().equals(attr)) {
                final Type matchType = getType(end.getType());
                if (matchType.isClassType()) return (ClassType) matchType;
                else break;
            }
        }
        return null;
    }

    public ModelElement getModelElement(final ClassType type, final String key) {
        if (isAssociation(type, key)) {
            return getAssociationType(type, key);
        } else if (isAttribute(type, key)) {
            return getAttributeType(type, key);
        }
        return null;
    }
}

