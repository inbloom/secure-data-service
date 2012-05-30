package org.slc.sli.modeling.xmigen;

import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.ws.commons.schema.XmlSchemaAppInfo;

import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.uml.TaggedValue;

/**
 * Plug-in used to customize the W3C XML Schema to UML conversion.
 */
public interface Xsd2UmlPlugin {

    Collection<TagDefinition> declareTagDefinitions(Xsd2UmlPluginHost host);

    String getAssociationEndTypeName(ClassType classType, Attribute attribute, Xsd2UmlPluginHost host);

    boolean isAssociationEnd(ClassType classType, Attribute attribute, Xsd2UmlPluginHost host);

    String nameAssociation(AssociationEnd lhs, AssociationEnd rhs, Xsd2UmlPluginHost host);

    String nameFromComplexTypeExtension(QName complexType, QName base);

    String nameFromElementName(QName name);

    String nameFromSimpleTypeRestriction(QName simpleType, QName base);

    String nameFromTypeName(QName name);

    List<TaggedValue> tagsFromAppInfo(XmlSchemaAppInfo appInfo, Xsd2UmlPluginHost host);
}
