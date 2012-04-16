package org.slc.sli.modeling.xsd2xmi.core;

import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.ws.commons.schema.XmlSchemaAppInfo;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.uml.TaggedValue;

/**
 * Plug-in used to customize the W3C XML Schema to UML conversion.
 */
public interface Xsd2UmlPlugin {
    
    List<TaggedValue> convertAppInfo(XmlSchemaAppInfo appInfo, Xsd2UmlPluginHost host);
    
    String convertAttributeName(QName name);
    
    String convertTypeName(QName name);
    
    Collection<TagDefinition> declareTagDefinitions(Xsd2UmlPluginHost host);
    
    String getGeneralizationNameForComplexTypeExtension(QName complexType, QName base);
    
    String getGeneralizationNameForSimpleTypeRestriction(QName simpleType, QName base);
    
    boolean isAssociationEnd(ClassType classType, Attribute attribute, Xsd2UmlPluginHost host);
    
    String getAssociationEndTypeName(ClassType classType, Attribute attribute, Xsd2UmlPluginHost host);
}
