package org.slc.sli.modeling.uml2Xsd;

import static org.slc.sli.modeling.xml.XmlTools.collapseWhitespace;

import javax.xml.namespace.QName;

import org.slc.sli.modeling.psm.PsmClassType;
import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.Occurs;
import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.uml.TaggedValue;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.index.Mapper;
import org.slc.sli.modeling.xsd.WxsNamespace;

final class PluginForJson implements Uml2XsdPlugin {
    
    @Override
    public QName getElementName(final PsmClassType<Type> classType) {
        return new QName(classType.getResource().getName());
    }
    
    @Override
    public QName getElementName(final String name, final boolean isReference) {
        // camel case is the convention for JSON.
        return new QName(Uml2XsdTools.camelCase(name));
    }
    
    @Override
    public QName getElementType(final String name, final boolean isAssociation) {
        return getTypeName(name);
    }
    
    @Override
    public QName getTypeName(final String name) {
        return new QName(name);
    }
    
    @Override
    public boolean isEnabled(final QName name) {
        return false;
    }
    
    @Override
    public void writeAssociationElement(final ClassType complexType, final AssociationEnd element, final Mapper lookup,
            final Uml2XsdPluginWriter xsw) {
        // if (element.isNavigable()) {
        // xsw.choice();
        // {
        // The first choice is for an embedded type.
        writeEmbedded(complexType, element, lookup, xsw);
        // The second choice is for UUIDs.
        // writeReference(complexType, element, lookup, xsw);
        // }
        // xsw.end();
        // }
    }
    
    private void writeEmbedded(final ClassType complexType, final AssociationEnd element, final Mapper lookup,
            final Uml2XsdPluginWriter xsw) {
        final String name = getName(element, lookup);
        final Type type = lookup.getType(element.getType());
        final Occurs minOccurs = element.getMultiplicity().getRange().getLower();
        final Occurs maxOccurs = element.getMultiplicity().getRange().getUpper();
        xsw.element();
        xsw.name(new QName(name));
        xsw.type(getTypeName(type.getName()));
        xsw.minOccurs(minOccurs);
        xsw.maxOccurs(maxOccurs);
        {
            xsw.annotation();
            for (final TaggedValue taggedValue : element.getTaggedValues()) {
                final TagDefinition tagDefinition = lookup.getTagDefinition(taggedValue.getTagDefinition());
                if (TagDefinition.NAME_DOCUMENTATION.equals(tagDefinition.getName())) {
                    xsw.documentation();
                    xsw.characters(collapseWhitespace(taggedValue.getValue()));
                    xsw.end();
                }
            }
            
            xsw.end();
        }
        xsw.end();
    }
    
    @SuppressWarnings("unused")
    private void writeReference(final ClassType complexType, final AssociationEnd element, final Mapper lookup,
            final Uml2XsdPluginWriter xsw) {
        final String name = getName(element, lookup);
        final Occurs minOccurs = element.getMultiplicity().getRange().getLower();
        final Occurs maxOccurs = element.getMultiplicity().getRange().getUpper();
        
        xsw.element();
        xsw.name(new QName(withId(camelCase(name), maxOccurs)));
        xsw.type(WxsNamespace.STRING);
        xsw.minOccurs(minOccurs);
        xsw.maxOccurs(maxOccurs);
        {
            xsw.annotation();
            for (final TaggedValue taggedValue : element.getTaggedValues()) {
                final TagDefinition tagDefinition = lookup.getTagDefinition(taggedValue.getTagDefinition());
                if (TagDefinition.NAME_DOCUMENTATION.equals(tagDefinition.getName())) {
                    xsw.documentation();
                    xsw.characters(collapseWhitespace(taggedValue.getValue()));
                    xsw.end();
                }
            }
            
            xsw.end();
        }
        xsw.end();
    }
    
    /**
     * Not all association ends have names so we synthesize a name based upon the type.
     */
    private static final String getName(final AssociationEnd element, final Mapper lookup) {
        if (!element.getName().trim().isEmpty()) {
            return element.getName();
        } else {
            // Name using the element type. Could be more sophisticated here.
            return new Uml2XsdSyntheticHasName(element, lookup).getName();
        }
    }
    
    private static final String camelCase(final String text) {
        return text.substring(0, 1).toLowerCase().concat(text.substring(1));
    }
    
    private static final String withId(final String name, final Occurs maxOccurs) {
        return name.concat(isUnbounded(maxOccurs) ? "Ids" : "Id");
    }
    
    private static final boolean isUnbounded(final Occurs occurs) {
        return occurs.equals(Occurs.UNBOUNDED);
    }
}
