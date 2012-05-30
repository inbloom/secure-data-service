package org.slc.sli.modeling.xsdgen;

import java.util.Map;

import javax.xml.namespace.QName;

import org.slc.sli.modeling.psm.PsmDocument;
import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.TaggedValue;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.index.ModelIndex;

public interface Uml2XsdPlugin {
    /**
     * Returns the prefix mappings that the plug-in will use to write custom content.
     */
    Map<String, String> declarePrefixMappings();

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
     * Returns the W3C XML Schema name for the UML class, in the plural form.
     */
    QName getPluralTopLevelElementName(final PsmDocument<Type> classType);

    /**
     * Returns the W3C XML Schema name for the UML class, in the singular form.
     */
    QName getSingularTopLevelElementName(final PsmDocument<Type> classType);

    String getTargetNamespace();

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
     * Determines the setting of the <code>attributeFormDefault</code> attribute.
     */
    boolean isAttributeFormDefaultQualified();

    /**
     * Determines the setting of the <code>elementFormDefault</code> attribute.
     */
    boolean isElementFormDefaultQualified();

    /**
     * Determines whether the specified property is enabled.
     */
    boolean isEnabled(final QName name);

    void writeAppInfo(final TaggedValue taggedValue, final ModelIndex lookup, final Uml2XsdPluginWriter xsw);

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
    void writeAssociation(final ClassType complexType, final AssociationEnd element, final ModelIndex lookup,
            final Uml2XsdPluginWriter xsw);

    /**
     * Writes the top-level element appropriate for the schema usage.
     *
     * @param classType
     *            The complex type of a single resource.
     * @param model
     *            The UML model index.
     * @param xsw
     *            The writer callback.
     *
     *            FIXME: The platform-specific aspects should come from the plug-in implementation.
     *            This means that the interface should only know about the UML model and elements.
     */
    void writeTopLevelElement(final PsmDocument<Type> classType, final ModelIndex model, final Uml2XsdPluginWriter xsw);
}
