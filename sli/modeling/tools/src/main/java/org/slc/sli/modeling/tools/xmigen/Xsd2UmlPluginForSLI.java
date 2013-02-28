/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slc.sli.modeling.tools.xmigen;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.ws.commons.schema.XmlSchemaAppInfo;
import org.slc.sli.modeling.psm.helpers.SliMongoConstants;
import org.slc.sli.modeling.psm.helpers.SliUmlConstants;
import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.Multiplicity;
import org.slc.sli.modeling.uml.Occurs;
import org.slc.sli.modeling.uml.Range;
import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.uml.TaggedValue;
import org.slc.sli.modeling.xml.XmlTools;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Provides additional configuration information on how to transform an XSD to a UML.
 * 
 * 
 * @author kmyers
 *
 */
public final class Xsd2UmlPluginForSLI extends Xsd2UmlPluginDefault {

    /**
     * Control the massaging of the W3C XML Schema. The less we do the better.
     */
    private static final boolean TITLE_CASE_SCHEMA_TYPE_NAMES = true;

    private static final TagDefinition makeTagDefinition(final String name, final Occurs lower, final Occurs upper,
            final Xsd2UmlPluginHost host) {
        final Range range = new Range(lower, upper);
        final Multiplicity multiplicity = new Multiplicity(range);
        final Identifier id = host.ensureTagDefinitionId(name);
        return new TagDefinition(id, name, multiplicity);
    }

    private static final String stringValue(final NodeList markup) {
        final StringBuilder sb = new StringBuilder();
        final int length = markup.getLength();
        for (int i = 0; i < length; i++) {
            final Node node = markup.item(i);
            sb.append(node.getTextContent());
        }
        return sb.toString();
    }

    private static final String titleCase(final String text) {
        return text.substring(0, 1).toUpperCase().concat(text.substring(1));
    }

    public Xsd2UmlPluginForSLI() {
        //No Op
    }

    @Override
    public List<TagDefinition> declareTagDefinitions(final Xsd2UmlPluginHost host) {
        final List<TagDefinition> tagDefs = new LinkedList<TagDefinition>();
        tagDefs.add(makeTagDefinition(SliUmlConstants.TAGDEF_COLLECTION_NAME, Occurs.ZERO, Occurs.ONE, host));
        tagDefs.add(makeTagDefinition(SliUmlConstants.TAGDEF_NATURAL_KEY, Occurs.ZERO, Occurs.ONE, host));
        tagDefs.add(makeTagDefinition(SliUmlConstants.TAGDEF_SELF_REFERENCE, Occurs.ZERO, Occurs.ONE, host));
        tagDefs.add(makeTagDefinition(SliUmlConstants.TAGDEF_APPLY_NATURAL_KEYS, Occurs.ZERO, Occurs.ONE, host));
        tagDefs.add(makeTagDefinition(SliUmlConstants.TAGDEF_PII, Occurs.ZERO, Occurs.ONE, host));
        tagDefs.add(makeTagDefinition(SliUmlConstants.TAGDEF_ENFORCE_READ, Occurs.ZERO, Occurs.ONE, host));
        tagDefs.add(makeTagDefinition(SliUmlConstants.TAGDEF_ENFORCE_WRITE, Occurs.ZERO, Occurs.ONE, host));
        tagDefs.add(makeTagDefinition(SliUmlConstants.TAGDEF_REFERENCE, Occurs.ZERO, Occurs.ONE, host));
        tagDefs.add(makeTagDefinition(SliUmlConstants.TAGDEF_RELAXED_BLACKLIST, Occurs.ZERO, Occurs.UNBOUNDED, host));
        tagDefs.add(makeTagDefinition(SliUmlConstants.TAGDEF_REST_RESOURCE, Occurs.ZERO, Occurs.UNBOUNDED, host));
        tagDefs.add(makeTagDefinition(SliUmlConstants.TAGDEF_RESTRICTED_FOR_LOGGING, Occurs.ZERO, Occurs.ONE, host));
        tagDefs.add(makeTagDefinition(SliUmlConstants.TAGDEF_SECURITY_SPHERE, Occurs.ZERO, Occurs.ONE, host));
        tagDefs.add(makeTagDefinition(SliUmlConstants.TAGDEF_ASSOCIATION_KEY, Occurs.ZERO, Occurs.ONE, host));
        tagDefs.add(makeTagDefinition(SliUmlConstants.TAGDEF_BEGIN_DATE, Occurs.ZERO, Occurs.ONE, host));
         tagDefs.add(makeTagDefinition(SliUmlConstants.TAGDEF_END_DATE, Occurs.ZERO, Occurs.ONE, host));
        tagDefs.add(makeTagDefinition(SliUmlConstants.TAGDEF_ASSOCIATED_DATED_COLLECTION, Occurs.ZERO, Occurs.ONE, host));
        tagDefs.add(makeTagDefinition(SliUmlConstants.TAGDEF_FILTER_BEGIN_DATE_ON, Occurs.ZERO, Occurs.ONE, host));
        tagDefs.add(makeTagDefinition(SliUmlConstants.TAGDEF_FILTER_END_DATE_ON, Occurs.ZERO, Occurs.ONE, host));
        return Collections.unmodifiableList(tagDefs);
    }

    @Override
    public String getAssociationEndTypeName(final ClassType classType, final Attribute attribute,
            final Xsd2UmlPluginHost host) {
        // Look for the reference tag.
        String name = null;
        final List<TaggedValue> taggedValues = attribute.getTaggedValues();
        for (final TaggedValue taggedValue : taggedValues) {
            final TagDefinition tagDefinition = host.getTagDefinition(taggedValue.getTagDefinition());
            if (tagDefinition.getName().equals(SliUmlConstants.TAGDEF_REFERENCE)) {
                name = nameFromSchemaTypeName(new QName(taggedValue.getValue()));
                break;
            }
        }
        return name;
    }

    @Override
    /**
     * The most reliable way to determine whether the attribute should be an association is to look for the tag that indicates
     * that the attribute should be treated as a reference. If there is no tag then we look for clues in the name of the attribute.
     */
    public boolean isAssociationEnd(final ClassType classType, final Attribute attribute, final Xsd2UmlPluginHost host) {
        // Look for the reference tag.
        final List<TaggedValue> taggedValues = attribute.getTaggedValues();
        for (final TaggedValue taggedValue : taggedValues) {
            final TagDefinition tagDefinition = host.getTagDefinition(taggedValue.getTagDefinition());
            if (tagDefinition.getName().equals(SliUmlConstants.TAGDEF_REFERENCE)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String nameAssociation(final AssociationEnd lhs, final AssociationEnd rhs, final Xsd2UmlHostedPlugin host) {
        // DE1531: don't return a name for the association as it just adds unwanted <=> to the end documentation
        // return host.nameAssociation(lhs, rhs, host);
        return "";
    }

    @Override
    public String nameFromSchemaTypeName(final QName name) {
        if (TITLE_CASE_SCHEMA_TYPE_NAMES) {
            return titleCase(name.getLocalPart());
        } else {
            return name.getLocalPart();
        }
    }

    @Override
    public List<TaggedValue> tagsFromAppInfo(final XmlSchemaAppInfo appInfo, final Xsd2UmlPluginHost host) {
        final List<TaggedValue> taggedValues = new LinkedList<TaggedValue>();
        final NodeList markup = appInfo.getMarkup();
        for (int i = 0; i < markup.getLength(); i++) {
            final Node node = markup.item(i);
            if (Node.ELEMENT_NODE == node.getNodeType()) {
                final Element element = (Element) node;

                final String namespace = element.getNamespaceURI();
                final String localName = element.getLocalName();
                final QName name = new QName(namespace, localName);
                if (SliMongoConstants.SLI_COLLECTION_NAME.equals(name)) {
                    final Identifier tagDefinition = host.ensureTagDefinitionId(SliUmlConstants.TAGDEF_COLLECTION_NAME);
                    final String collectionName = nameFromSchemaTypeName(new QName(
                            XmlTools.collapseWhitespace(stringValue(element.getChildNodes()))));
                    taggedValues.add(new TaggedValue(collectionName, tagDefinition));
                } else if (SliMongoConstants.SLI_ASSOCIATED_DATED_COLLECTION.equals(name)) {
                    final Identifier tagDefinition = host.ensureTagDefinitionId(SliUmlConstants.TAGDEF_ASSOCIATED_DATED_COLLECTION);
                    final String collectionName = nameFromSchemaTypeName(new QName(
                            XmlTools.collapseWhitespace(stringValue(element.getChildNodes()))));
                    taggedValues.add(new TaggedValue(collectionName, tagDefinition));
                } else if (SliMongoConstants.SLI_NATURAL_KEY.equals(name)) {
                    final Identifier tagDefinition = host.ensureTagDefinitionId(SliUmlConstants.TAGDEF_NATURAL_KEY);
                    taggedValues.add(new TaggedValue("true", tagDefinition));
                } else if (SliMongoConstants.SLI_APPLY_NATURAL_KEYS.equals(name)) {
                    final Identifier tagDefinition = host
                            .ensureTagDefinitionId(SliUmlConstants.TAGDEF_APPLY_NATURAL_KEYS);
                    taggedValues.add(new TaggedValue("true", tagDefinition));                
                } else if (SliMongoConstants.SLI_SELF_REFERENCE.equals(name)) {
                    final Identifier tagDefinition = host.ensureTagDefinitionId(SliUmlConstants.TAGDEF_SELF_REFERENCE);
                    taggedValues.add(new TaggedValue("true", tagDefinition));
                } else if (SliMongoConstants.SLI_REFERENCE_TYPE.equals(name)) {
                    final Identifier tagDefinition = host.ensureTagDefinitionId(SliUmlConstants.TAGDEF_REFERENCE);
                    final String refereceType = nameFromSchemaTypeName(new QName(
                            XmlTools.collapseWhitespace(stringValue(element.getChildNodes()))));
                    taggedValues.add(new TaggedValue(refereceType, tagDefinition));
                } else if (SliMongoConstants.SLI_PII.equals(name)) {
                    final Identifier tagDefinition = host.ensureTagDefinitionId(SliUmlConstants.TAGDEF_PII);
                    final Boolean isPII = Boolean.valueOf(XmlTools.collapseWhitespace(stringValue(element
                            .getChildNodes())));
                    taggedValues.add(new TaggedValue(isPII.toString(), tagDefinition));
                } else if (SliMongoConstants.SLI_READ_ENFORCEMENT.equals(name)) {
                    final Identifier tagDefinition = host.ensureTagDefinitionId(SliUmlConstants.TAGDEF_ENFORCE_READ);
                    final String text = XmlTools.collapseWhitespace(stringValue(element.getChildNodes()));
                    taggedValues.add(new TaggedValue(text, tagDefinition));
                } else if (SliMongoConstants.SLI_SECURITY_SPHERE.equals(name)) {
                    final Identifier tagDefinition = host.ensureTagDefinitionId(SliUmlConstants.TAGDEF_SECURITY_SPHERE);
                    final String text = XmlTools.collapseWhitespace(stringValue(element.getChildNodes()));
                    if ("Public".equals(text)) {
                        taggedValues.add(new TaggedValue(text, tagDefinition));
                    } else {
                        throw new AssertionError("Unexpected value for appinfo: " + name + " => " + text);
                    }
                } else if (SliMongoConstants.SLI_RELAXEDBLACKLIST.equals(name)) {
                    final Identifier tagDefinition = host
                            .ensureTagDefinitionId(SliUmlConstants.TAGDEF_RELAXED_BLACKLIST);
                    final String text = XmlTools.collapseWhitespace(stringValue(element.getChildNodes()));
                    if ("true".equals(text)) {
                        taggedValues.add(new TaggedValue(text, tagDefinition));
                    } else {
                        throw new AssertionError("Unexpected value for appinfo: " + name + " => " + text);
                    }
                } else if (SliMongoConstants.SLI_RESTRICTED_FOR_LOGGING.equals(name)) {
                    final Identifier tagDefinition = host
                            .ensureTagDefinitionId(SliUmlConstants.TAGDEF_RESTRICTED_FOR_LOGGING);
                    final String text = XmlTools.collapseWhitespace(stringValue(element.getChildNodes()));
                    if ("true".equals(text)) {
                        taggedValues.add(new TaggedValue(text, tagDefinition));
                    } else {
                        throw new AssertionError("Unexpected value for appinfo: " + name + " => " + text);
                    }
                } else if (SliMongoConstants.SLI_WRITE_ENFORCEMENT.equals(name)) {
                    final Identifier tagDefinition = host.ensureTagDefinitionId(SliUmlConstants.TAGDEF_ENFORCE_WRITE);
                    final String text = XmlTools.collapseWhitespace(stringValue(element.getChildNodes()));
                    taggedValues.add(new TaggedValue(text, tagDefinition));
                } else if (SliMongoConstants.SLI_ASSOCIATION_KEY.equals(name)) {
                    final Identifier tagDefinition = host.ensureTagDefinitionId(SliUmlConstants.TAGDEF_ASSOCIATION_KEY);
                    taggedValues.add(new TaggedValue("true", tagDefinition));
                } else if (SliMongoConstants.SLI_BEGIN_DATE.equals(name)) {
                    final Identifier tagDefinition = host.ensureTagDefinitionId(SliUmlConstants.TAGDEF_BEGIN_DATE);
                    taggedValues.add(new TaggedValue(SliUmlConstants.TAGDEF_BEGIN_DATE, tagDefinition));
                } else if (SliMongoConstants.SLI_END_DATE.equals(name)) {
                    final Identifier tagDefinition = host.ensureTagDefinitionId(SliUmlConstants.TAGDEF_END_DATE);
                    taggedValues.add(new TaggedValue(SliUmlConstants.TAGDEF_END_DATE, tagDefinition));
                } else if (SliMongoConstants.SLI_FILTER_BEGIN_DATE_ON.equals(name)) {
                    final Identifier tagDefinition = host.ensureTagDefinitionId(SliUmlConstants.TAGDEF_FILTER_BEGIN_DATE_ON);
                    taggedValues.add(new TaggedValue(stringValue(element.getChildNodes()), tagDefinition));
                } else if (SliMongoConstants.SLI_FILTER_END_DATE_ON.equals(name)) {
                    final Identifier tagDefinition = host.ensureTagDefinitionId(SliUmlConstants.TAGDEF_FILTER_END_DATE_ON);
                    taggedValues.add(new TaggedValue(stringValue(element.getChildNodes()), tagDefinition));
                }
            }
        }
        return taggedValues;
    }
}
