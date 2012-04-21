package org.slc.sli.modeling.xsd2xmi.cmdline;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.ws.commons.schema.XmlSchemaAppInfo;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
import org.slc.sli.modeling.xsd2xmi.core.Xsd2UmlPlugin;
import org.slc.sli.modeling.xsd2xmi.core.Xsd2UmlPluginHost;

public final class Xsd2UmlPluginForSLI implements Xsd2UmlPlugin {

    private static final String NAMESPACE_SLI = "http://slc-sli/ed-org/0.1";

    private static final QName SLI_PII = new QName(NAMESPACE_SLI, "PersonallyIdentifiableInfo");
    private static final QName SLI_READ_ENFORCEMENT = new QName(NAMESPACE_SLI, "ReadEnforcement");
    private static final QName SLI_REFERENCE_TYPE = new QName(NAMESPACE_SLI, "ReferenceType");
    private static final QName SLI_WRITE_ENFORCEMENT = new QName(NAMESPACE_SLI, "WriteEnforcement");
    private static final String TAG_DEFINITION_NAME_COLLECTION = "collection";
    private static final String TAG_DEFINITION_NAME_PII = camelCase(SLI_PII.getLocalPart());
    private static final String TAG_DEFINITION_NAME_READ = camelCase(SLI_READ_ENFORCEMENT.getLocalPart());
    private static final String TAG_DEFINITION_NAME_REFERENCE = camelCase(SLI_REFERENCE_TYPE.getLocalPart());
    private static final String TAG_DEFINITION_NAME_RESOURCE = "resource";
    private static final String TAG_DEFINITION_NAME_WRITE = camelCase(SLI_WRITE_ENFORCEMENT.getLocalPart());

    private static final String camelCase(final String text) {
        return text.substring(0, 1).toLowerCase().concat(text.substring(1));
    }

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

    @Override
    public List<TagDefinition> declareTagDefinitions(final Xsd2UmlPluginHost config) {
        final List<TagDefinition> tagDefinitions = new LinkedList<TagDefinition>();
        tagDefinitions.add(makeTagDefinition(TAG_DEFINITION_NAME_REFERENCE, Occurs.ZERO, Occurs.ONE, config));
        tagDefinitions.add(makeTagDefinition(TAG_DEFINITION_NAME_PII, Occurs.ZERO, Occurs.ONE, config));
        tagDefinitions.add(makeTagDefinition(TAG_DEFINITION_NAME_READ, Occurs.ZERO, Occurs.ONE, config));
        tagDefinitions.add(makeTagDefinition(TAG_DEFINITION_NAME_WRITE, Occurs.ZERO, Occurs.ONE, config));
        tagDefinitions.add(makeTagDefinition(TAG_DEFINITION_NAME_RESOURCE, Occurs.ZERO, Occurs.UNBOUNDED, config));
        tagDefinitions.add(makeTagDefinition(TAG_DEFINITION_NAME_COLLECTION, Occurs.ZERO, Occurs.ONE, config));
        return Collections.unmodifiableList(tagDefinitions);
    }

    @Override
    public String getAssociationEndTypeName(final ClassType classType, final Attribute attribute,
            final Xsd2UmlPluginHost host) {
        // Look for the reference tag.
        final List<TaggedValue> taggedValues = attribute.getTaggedValues();
        for (final TaggedValue taggedValue : taggedValues) {
            final TagDefinition tagDefinition = host.getTagDefinition(taggedValue.getTagDefinition());
            if (tagDefinition.getName().equals(TAG_DEFINITION_NAME_REFERENCE)) {
                return nameFromTypeName(new QName(taggedValue.getValue()));
            }
        }
        return null;
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
            if (tagDefinition.getName().equals(TAG_DEFINITION_NAME_REFERENCE)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String nameAssociation(final AssociationEnd lhs, final AssociationEnd rhs, final Xsd2UmlPluginHost host) {
        return host.nameAssociation(lhs, rhs, host);
    }

    @Override
    public String nameFromComplexTypeExtension(final QName complexType, final QName base) {
        return nameFromTypeName(complexType).concat(" extends ").concat(nameFromTypeName(base));
    }

    @Override
    public String nameFromElementName(final QName name) {
        return name.getLocalPart();
    }

    @Override
    public String nameFromSimpleTypeRestriction(final QName simpleType, final QName base) {
        return nameFromTypeName(simpleType).concat(" restricts ").concat(nameFromTypeName(base));
    }

    @Override
    public String nameFromTypeName(final QName name) {
        return titleCase(name.getLocalPart());
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
                if (SLI_REFERENCE_TYPE.equals(name)) {
                    final Identifier tagDefinition = host.ensureTagDefinitionId(TAG_DEFINITION_NAME_REFERENCE);
                    final String refereceType = nameFromTypeName(new QName(
                            XmlTools.collapseWhitespace(stringValue(element.getChildNodes()))));
                    taggedValues.add(new TaggedValue(refereceType, tagDefinition));
                } else if (SLI_PII.equals(name)) {
                    final Identifier tagDefinition = host.ensureTagDefinitionId(TAG_DEFINITION_NAME_PII);
                    final Boolean isPII = Boolean.valueOf(XmlTools.collapseWhitespace(stringValue(element
                            .getChildNodes())));
                    taggedValues.add(new TaggedValue(isPII.toString(), tagDefinition));
                } else if (SLI_READ_ENFORCEMENT.equals(name)) {
                    final Identifier tagDefinition = host.ensureTagDefinitionId(TAG_DEFINITION_NAME_READ);
                    final String text = XmlTools.collapseWhitespace(stringValue(element.getChildNodes()));
                    if ("READ_RESTRICTED".equals(text)) {
                        taggedValues.add(new TaggedValue(text, tagDefinition));
                    } else {
                        throw new AssertionError("Unexpected value for appinfo: " + name + " => " + text);
                    }
                } else if (SLI_WRITE_ENFORCEMENT.equals(name)) {
                    final Identifier tagDefinition = host.ensureTagDefinitionId(TAG_DEFINITION_NAME_WRITE);
                    final String text = XmlTools.collapseWhitespace(stringValue(element.getChildNodes()));
                    if ("WRITE_RESTRICTED".equals(text)) {
                        taggedValues.add(new TaggedValue(text, tagDefinition));
                    } else {
                        throw new AssertionError("Unexpected value for appinfo: " + name + " => " + text);
                    }
                } else {
                    throw new AssertionError("Unexpected element in appinfo: " + name);
                }
            }
        }
        return taggedValues;
    }

    @Override
    public List<TaggedValue> tagsFromTopLevelElement(final QName name, final Xsd2UmlPluginHost host) {
        final List<TaggedValue> tags = new LinkedList<TaggedValue>();
        // This is a poor-man's pluralization!
        final Identifier resourceTag = host.ensureTagDefinitionId(TAG_DEFINITION_NAME_RESOURCE);
        final TaggedValue resource = new TaggedValue(camelCase(name.getLocalPart()).concat("s"), resourceTag);
        tags.add(resource);
        final Identifier collectionTag = host.ensureTagDefinitionId(TAG_DEFINITION_NAME_COLLECTION);
        tags.add(new TaggedValue(camelCase(name.getLocalPart()), collectionTag));
        return Collections.unmodifiableList(tags);
    }
}
