package org.slc.sli.modeling.uml2Xsd;

import static org.slc.sli.modeling.xml.XmlTools.collapseWhitespace;

import javax.xml.namespace.QName;

import org.slc.sli.modeling.psm.PsmClassType;
import org.slc.sli.modeling.psm.PsmCollection;
import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.Occurs;
import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.uml.TaggedValue;
import org.slc.sli.modeling.uml.Type;

final class PluginForMongo implements Uml2XsdPlugin {
    
    @Override
    public QName getElementName(final PsmClassType<Type> classType) {
        final PsmCollection collection = classType.getCollection();
        return new QName(collection.getName());
    }
    
    @Override
    public QName getElementName(final QName name, final boolean isReference) {
        // camel case is the convention for JSON.
        return Uml2XsdTools.camelCase(name);
    }
    
    @Override
    public QName getElementType(final QName name, final boolean isAssociation) {
        return getTypeName(name);
    }
    
    @Override
    public QName getTypeName(final QName name) {
        return name;
    }
    
    @Override
    public boolean isEnabled(final QName name) {
        return false;
    }
    
    @Override
    public void writeAssociationElement(final ClassType complexType, final AssociationEnd element,
            final Uml2XsdPluginWriter xsw) {
        if (element.isNavigable()) {
            final QName name = getName(element);
            final Type type = element.getType();
            final Occurs minOccurs = element.getMultiplicity().getRange().getLower();
            final Occurs maxOccurs = element.getMultiplicity().getRange().getUpper();
            xsw.choice();
            {
                // The first choice is for an embedded type.
                xsw.element();
                xsw.name(name);
                xsw.type(type.getName());
                xsw.minOccurs(minOccurs);
                xsw.maxOccurs(maxOccurs);
                {
                    xsw.annotation();
                    for (final TaggedValue taggedValue : element.getTaggedValues()) {
                        final TagDefinition tagDefinition = taggedValue.getTagDefinition();
                        if (TagDefinition.NAME_DOCUMENTATION.equals(tagDefinition.getName())) {
                            xsw.documentation();
                            xsw.characters(collapseWhitespace(taggedValue.getValue()));
                            xsw.end();
                        }
                    }
                    
                    xsw.end();
                }
                xsw.end();
                // The second choice is for UUIDs.
                xsw.element();
                xsw.name(withId(camelCase(type.getName()), maxOccurs));
                xsw.type(new QName("string"));
                xsw.minOccurs(minOccurs);
                xsw.maxOccurs(maxOccurs);
                {
                    xsw.annotation();
                    for (final TaggedValue taggedValue : element.getTaggedValues()) {
                        final TagDefinition tagDefinition = taggedValue.getTagDefinition();
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
            xsw.end();
        }
    }
    
    /**
     * Not all association ends have names so we synthesize a name based upon the type.
     */
    private static final QName getName(final AssociationEnd element) {
        if (!element.getName().getLocalPart().trim().isEmpty()) {
            return element.getName();
        } else {
            // Name using the element type. Could be more sophisticated here.
            return new Uml2XsdSyntheticHasName(element).getName();
        }
    }
    
    private static final QName camelCase(final QName name) {
        final String text = name.getLocalPart();
        return new QName(text.substring(0, 1).toLowerCase().concat(text.substring(1)));
    }
    
    private static final QName withId(final QName name, final Occurs maxOccurs) {
        return new QName(name.getLocalPart().concat(isUnbounded(maxOccurs) ? "Ids" : "Id"));
    }
    
    private static final boolean isUnbounded(final Occurs occurs) {
        return occurs.equals(Occurs.UNBOUNDED);
    }
}
