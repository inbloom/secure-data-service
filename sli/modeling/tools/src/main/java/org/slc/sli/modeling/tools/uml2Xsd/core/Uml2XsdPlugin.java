package org.slc.sli.modeling.tools.uml2Xsd.core;

import javax.xml.namespace.QName;

import org.slc.sli.modeling.psm.PsmClassType;
import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.index.Mapper;

public interface Uml2XsdPlugin {
    QName getElementName(final PsmClassType<Type> classType);
    
    /**
     * Returns the name of an element to be used in the schema based upon the logical model name.
     * 
     * @param name
     *            The logical model name.
     * @param isAssociation
     *            Determines whether the name resulted from an association.
     * @return The name to be used in the schema.
     */
    QName getElementName(final String name, final boolean isAssociation);
    
    /**
     * Returns the name of an element type to be used in the schema based upon the logical model
     * name.
     * 
     * @param name
     *            The logical model name.
     * @param isAssociation
     *            Determines whether the name resulted from an association.
     * @return The name to be used in the schema for the element type.
     */
    QName getElementType(final String name, final boolean isAssociation);
    
    /**
     * Returns the name of a type to be used in the schema based upon the logical model
     * name.
     * 
     * @param name
     *            The logical model name.
     * @return The name to be used in the schema for a type.
     */
    QName getTypeName(final String name);
    
    /**
     * Determines whether the specified property is enabled.
     */
    boolean isEnabled(final QName name);
    
    /**
     * The plug-in gets to choose how to handle an association.
     * 
     * @param complexType
     *            The complex type that is the context for the element.
     * @param element
     *            The {@link AssociationEnd} that becomes the element.
     * @param xsw
     *            The writer.
     */
    void writeAssociationElement(final ClassType complexType, final AssociationEnd element, final Mapper lookup,
            final Uml2XsdPluginWriter xsw);
}
