package org.slc.sli.modeling.tools.xmigen;

import org.apache.ws.commons.schema.XmlSchemaAppInfo;
import org.slc.sli.modeling.uml.*;

import java.util.Collection;
import java.util.List;

public interface Xsd2UmlHostedPlugin extends Xsd2UmlPlugin {
    Collection<TagDefinition> declareTagDefinitions(Xsd2UmlPluginHost host);

    String getAssociationEndTypeName(ClassType classType, Attribute attribute, Xsd2UmlPluginHost host);

    boolean isAssociationEnd(ClassType classType, Attribute attribute, Xsd2UmlPluginHost host);

    String nameAssociation(AssociationEnd lhs, AssociationEnd rhs, Xsd2UmlHostedPlugin host);

    List<TaggedValue> tagsFromAppInfo(XmlSchemaAppInfo appInfo, Xsd2UmlPluginHost host);
}
