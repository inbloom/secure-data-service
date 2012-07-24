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

    public ClassType getType(final String typeName) {
        return modelIndex.getClassTypes().get(typeName);
    }

    public boolean isAttribute(final ClassType type, final String attributeName) {
        final List<Attribute> attributes = type.getAttributes();
        for (Attribute attribute : attributes) {
            if (attribute.getName().equals(attributeName)) {
                return true;
            }
        }
        return false;
    }

    public boolean isAssociation(final ClassType type, final String attribute) {
        final List<AssociationEnd> associationEnds = getAssociationEnds(type.getId());

        for (final AssociationEnd end : associationEnds) {
            if (end.getName().equals(attribute)) {
                return true;
            }
        }
        return false;
    }

    public Type getType(final ClassType type, final String attr) {
        if (isAssociation(type, attr)) {
            return getAssociationType(type, attr);
        } else if (isAttribute(type, attr)) {
            return getAttributeType(type, attr);
        }
        return null;
    }

    private Type getAttributeType(final ClassType type, final String attr) {
        final List<Attribute> attributes = type.getAttributes();
        for (Attribute attribute : attributes) {
            if (attribute.getName().equals(attr)) {
                return getType(attribute.getType());
            }
        }
        return null;
    }

    private Type getAssociationType(final ClassType type, final String attr) {
        final List<AssociationEnd> associationEnds = getAssociationEnds(type.getId());
        for (final AssociationEnd end : associationEnds) {
            if (end.getName().equals(attr)) {
                return getType(end.getType());
            }
        }
        return null;
    }
}
