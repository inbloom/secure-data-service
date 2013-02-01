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


package org.slc.sli.modeling.tools.xsdgen;

import static org.slc.sli.modeling.xml.XmlTools.collapseWhitespace;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.io.IOUtils;
import org.slc.sli.modeling.psm.PsmDocument;
import org.slc.sli.modeling.psm.helpers.TagName;
import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.DataType;
import org.slc.sli.modeling.uml.EnumLiteral;
import org.slc.sli.modeling.uml.EnumType;
import org.slc.sli.modeling.uml.Generalization;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.ModelElement;
import org.slc.sli.modeling.uml.Multiplicity;
import org.slc.sli.modeling.uml.Occurs;
import org.slc.sli.modeling.uml.Range;
import org.slc.sli.modeling.uml.SimpleType;
import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.uml.TaggedValue;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.UmlPackage;
import org.slc.sli.modeling.uml.index.ModelIndex;
import org.slc.sli.modeling.xml.IndentingXMLStreamWriter;
import org.slc.sli.modeling.xsd.WxsNamespace;
import org.slc.sli.modeling.xsd.XsdAttributeName;
import org.slc.sli.modeling.xsd.XsdElementName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Writes a UML {@link Model} to a file (by name) or {@link OutputStream}.
 */
final class Uml2XsdWriter {
    
    private static final Logger LOG = LoggerFactory.getLogger(Uml2XsdWriter.class);

    private static final String NAMESPACE_XS = WxsNamespace.URI;
    private static final String PREFIX_XS = "xs";

    private static final String QUALIFIED = "qualified";
    private static final String UNQUALIFIED = "unqualified";

    private static final void closeQuiet(final Closeable closeable) {
        IOUtils.closeQuietly(closeable);
    }
    
    public Uml2XsdWriter() {
        throw new UnsupportedOperationException();
    }

    private static final Iterable<SimpleType> combine(final Iterable<DataType> dataTypes,
            final Iterable<EnumType> enumTypes) {
        final List<SimpleType> simpleTypes = new LinkedList<SimpleType>();
        for (final DataType dataType : dataTypes) {
            simpleTypes.add(dataType);
        }
        for (final EnumType enumType : enumTypes) {
            simpleTypes.add(enumType);
        }
        return Collections.unmodifiableList(simpleTypes);
    }

    private static final Identifier getBase(final Identifier type, final ModelIndex lookup) {
        final List<Generalization> generalizationBase = lookup.getGeneralizationBase(type);
        if (generalizationBase.isEmpty()) {
            return null;
        } else {
            if (generalizationBase.size() == 1) {
                return generalizationBase.get(0).getParent();
            } else {
                throw new AssertionError(type);
            }
        }
    }

    private static final QName getQName(final SimpleType simpleType, final ModelIndex lookup) {
        final Identifier id = simpleType.getId();
        for (final ModelElement whereUsed : lookup.whereUsed(id)) {
            if (whereUsed instanceof UmlPackage) {
                final UmlPackage pkg = (UmlPackage) whereUsed;
                return new QName(pkg.getName(), simpleType.getName());
            }
        }
        return new QName(simpleType.getName());
    }

    /**
     * This is a hack while we don't have a ready way to get the package name.
     */
    private static final boolean isW3cXmlSchemaDatatype(final String localName) {
        if (localName.equals(WxsNamespace.BOOLEAN.getLocalPart())) {
            return true;
        } else if (localName.equals(WxsNamespace.DATE.getLocalPart())) {
            return true;
        } else if (localName.equals(WxsNamespace.DOUBLE.getLocalPart())) {
            return true;
        } else if (localName.equals(WxsNamespace.INT.getLocalPart())) {
            return true;
        } else if (localName.equals(WxsNamespace.INTEGER.getLocalPart())) {
            return true;
        } else if (localName.equals(WxsNamespace.STRING.getLocalPart())) {
            return true;
        } else {
            return localName.equals(WxsNamespace.TIME.getLocalPart());
        }
    }

    private static final boolean isW3cXmlSchemaType(final QName name) {
        final String namespace = name.getNamespaceURI();
        return namespace.equals(NAMESPACE_XS);
    }

    private static final void maxOccurs(final Occurs value, final XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeAttribute(XsdAttributeName.MAX_OCCURS.getLocalName(), toString(value));
    }

    private static final void minOccurs(final Occurs value, final XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeAttribute(XsdAttributeName.MIN_OCCURS.getLocalName(), toString(value));
    }

    private static final void occurrences(final Multiplicity multiplicity, final XMLStreamWriter xsw)
            throws XMLStreamException {
        final Range range = multiplicity.getRange();
        if (!range.getLower().equals(Occurs.ONE)) {
            minOccurs(range.getLower(), xsw);
        }
        if (!range.getUpper().equals(Occurs.ONE)) {
            maxOccurs(range.getUpper(), xsw);
        }
    }

    private static final <T> List<T> sort(final Iterable<T> elements, final Comparator<? super T> comparator) {
        final List<T> copy = new LinkedList<T>();
        for (final T element : elements) {
            copy.add(element);
        }
        Collections.sort(copy, comparator);
        return Collections.unmodifiableList(copy);
    }

    private static final String toString(final Occurs value) {
        if (value == null) {
            throw new IllegalArgumentException("value");
        }
        switch (value) {
        case ZERO: {
            return "0";
        }
        case ONE: {
            return "1";
        }
        case UNBOUNDED: {
            return "unbounded";
        }
        default: {
            throw new AssertionError(value);
        }
        }
    }

    private static final String typeLexicalName(final QName name, final XMLStreamWriter context) {
        final NamespaceContext namespaceContext = context.getNamespaceContext();
        final String namespace = name.getNamespaceURI();
        if (namespace.length() > 0) {
            final String prefix = namespaceContext.getPrefix(namespace);
            if (prefix == null || prefix.length() == 0) {
                return name.getLocalPart();
            } else {
                return prefix.concat(":").concat(name.getLocalPart());
            }
        } else {
            return name.getLocalPart();
        }
    }

    private static final void writeAssociationElements(final ClassType complexType, final ModelIndex lookup,
            final Uml2XsdPlugin plugin, final XMLStreamWriter xsw) throws XMLStreamException {
        for (final AssociationEnd element : lookup.getAssociationEnds(complexType.getId())) {
            plugin.writeAssociation(complexType, element, lookup, new Uml2XsdPluginWriterAdapter(xsw, PREFIX_XS));
        }
    }

    private static final void writeAttribute(final XsdAttributeName attributeName, final QName name,
            final XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeAttribute(attributeName.getLocalName(), typeLexicalName(name, xsw));
    }

    private static final void writeBaseAttribute(final Type type, final Uml2XsdPlugin plugin, final XMLStreamWriter xsw)
            throws XMLStreamException {
        // The problem with the UML structure is that it's hard to get the
        // package.
        // For now we'll just hack the case of W3C XML Schema data-types.
        final String localName = type.getName();
        if (isW3cXmlSchemaDatatype(localName)) {
            writeAttribute(XsdAttributeName.BASE, new QName(WxsNamespace.URI, localName), xsw);
        } else {
            final QName name = plugin.getTypeName(localName);
            writeAttribute(XsdAttributeName.BASE, name, xsw);
        }
    }

    private static final void writeAttributeElements(final ClassType complexType, final ModelIndex lookup,
            final Uml2XsdPlugin plugin, final XMLStreamWriter xsw) throws XMLStreamException {
        for (final Attribute element : complexType.getAttributes()) {
            writeElement(complexType, element, lookup, plugin, xsw);
        }
    }

    private static final void writeBaseAttribute(final QName name, final XMLStreamWriter xsw) throws XMLStreamException {
        writeAttribute(XsdAttributeName.BASE, name, xsw);
    }

    private static final void writeComplexType(final ClassType classType, final ModelIndex lookup,
            final Uml2XsdPlugin plugin, final XMLStreamWriter xsw) throws XMLStreamException {
        writeStartElement(XsdElementName.COMPLEX_TYPE, xsw);
        try {
            writeNameOfSchemaType(classType.getName(), xsw);
            final Identifier baseId = getBase(classType.getId(), lookup);
            if (baseId != null) {
                final Type base = lookup.getType(baseId);
                writeStartElement(XsdElementName.COMPLEX_CONTENT, xsw);
                try {
                    writeStartElement(XsdElementName.EXTENSION, xsw);
                    writeBaseAttribute(base, plugin, xsw);
                    try {
                        writeElements(classType, lookup, plugin, xsw);
                    } finally {
                        writeEndElement(xsw);
                    }
                } finally {
                    writeEndElement(xsw);
                }
            } else {
                writeElements(classType, lookup, plugin, xsw);
            }
        } finally {
            writeEndElement(xsw);
        }
    }

    private static final void writeDocumentation(final TaggedValue taggedValue, final XMLStreamWriter xsw)
            throws XMLStreamException {
        writeStartElement(XsdElementName.DOCUMENTATION, xsw);
        try {
            xsw.writeCharacters(collapseWhitespace(taggedValue.getValue()));
        } finally {
            writeEndElement(xsw);
        }
    }

    private static final void writeElement(final ClassType complexType, final Attribute element,
            final ModelIndex model, final Uml2XsdPlugin plugin, final XMLStreamWriter xsw) throws XMLStreamException {
        writeStartElement(XsdElementName.ELEMENT, xsw);
        try {
            final QName name = plugin.getElementName(element.getName(), false);
            writeElementName(name, xsw);
            final Identifier elementTypeId = element.getType();
            final Type elementType = model.getType(elementTypeId);
            final String localName = elementType.getName();
            if (isW3cXmlSchemaDatatype(localName)) {
                writeTypeAttribute(new QName(WxsNamespace.URI, localName), xsw);
            } else {
                final QName type = plugin.getElementType(localName, false);
                writeTypeAttribute(type, xsw);
            }
            occurrences(element.getMultiplicity(), xsw);
            writeStartElement(XsdElementName.ANNOTATION, xsw);
            try {
                for (final TaggedValue taggedValue : element.getTaggedValues()) {
                    final TagDefinition tagDefinition = model.getTagDefinition(taggedValue.getTagDefinition());
                    if (TagName.DOCUMENTATION.equals(tagDefinition.getName())) {
                        writeDocumentation(taggedValue, xsw);
                    } else {
                        plugin.writeAppInfo(taggedValue, model, new Uml2XsdPluginWriterAdapter(xsw, PREFIX_XS));
                    }
                }
            } finally {
                writeEndElement(xsw);
            }
        } finally {
            writeEndElement(xsw);
        }
    }

    private static final void writeElementName(final QName name, final XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeAttribute(XsdAttributeName.NAME.getLocalName(), name.getLocalPart());
    }

    private static final void writeElements(final ClassType complexType, final ModelIndex lookup,
            final Uml2XsdPlugin plugin, final XMLStreamWriter xsw) throws XMLStreamException {
        /**
         * We are virtually forced to write all the elements inside the
         * xs:sequence compositor because of the restrictions on xs:all: 1)
         * xs:all is not a particle so it cannot be embedded inside a
         * compositor. 2) xs:all does not accept elements whose occurences are
         * greater than one.
         */
        writeStartElement(XsdElementName.SEQUENCE, xsw);
        try {
            writeAttributeElements(complexType, lookup, plugin, xsw);
            writeAssociationElements(complexType, lookup, plugin, xsw);
        } finally {
            writeEndElement(xsw);
        }
    }

    private static final void writeEndElement(final XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeEndElement();
    }

    private static final void writeFacet(final XsdElementName name, TaggedValue taggedValue, final XMLStreamWriter xsw)
            throws XMLStreamException {
        writeStartElement(name, xsw);
        try {
            writeValueAttribute(collapseWhitespace(taggedValue.getValue()), xsw);
        } finally {
            writeEndElement(xsw);
        }
    }

    private static final void writeFormDefaultForAttributes(final boolean qualified, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeAttribute(XsdAttributeName.ATTRIBUTE_FORM_DEFAULT.getLocalName(), qualified ? QUALIFIED : UNQUALIFIED);
    }

    private static final void writeFormDefaultForElements(final boolean qualified, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeAttribute(XsdAttributeName.ELEMENT_FORM_DEFAULT.getLocalName(), qualified ? QUALIFIED : UNQUALIFIED);
    }

    private static final void writeNameOfSchemaType(final String name, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeAttribute(XsdAttributeName.NAME.getLocalName(), name);
    }

    public static final void writeSchema(final List<PsmDocument<Type>> elements, final ModelIndex model,
            final Uml2XsdPlugin plugin, final File file) {
        try {
            final OutputStream outstream = new BufferedOutputStream(new FileOutputStream(file));
            try {
                writeSchema(elements, model, plugin, outstream);
            } finally {
                closeQuiet(outstream);
            }
        } catch (final FileNotFoundException e) {
            LOG.warn(e.getMessage());
        }
    }

    public static final void writeSchema(final List<PsmDocument<Type>> documents, final ModelIndex model,
            final Uml2XsdPlugin plugin, final OutputStream outstream) {
        final XMLOutputFactory xof = XMLOutputFactory.newInstance();
        try {
            final XMLStreamWriter xsw = new IndentingXMLStreamWriter(xof.createXMLStreamWriter(outstream, "UTF-8"));
            xsw.writeStartDocument("UTF-8", "1.0");
            try {
                writeSchema(documents, model, plugin, xsw);
            } finally {
                xsw.writeEndDocument();
            }
            xsw.flush();
            // We close the stream writer knowing (from the documentation) that it will not close
            // the underlying output stream.
            xsw.close();
        } catch (final XMLStreamException e) {
            throw new XsdGenRuntimeException(e);
        }
    }

    public static final void writeSchema(final List<PsmDocument<Type>> elements, final ModelIndex model,
            final Uml2XsdPlugin plugin, final String fileName) {
        try {
            final OutputStream outstream = new BufferedOutputStream(new FileOutputStream(fileName));
            try {
                writeSchema(elements, model, plugin, outstream);
            } finally {
                closeQuiet(outstream);
            }
        } catch (final FileNotFoundException e) {
            LOG.warn(e.getMessage());
        }
    }

    /**
     * Writes the UML model to the XML stream in XSD format.
     *
     * @param model
     *            The UML model.
     * @param xsw
     *            The XML stream.
     * @throws XMLStreamException
     *             if anything bad happens.
     */
    private static final void writeSchema(final List<PsmDocument<Type>> elements, final ModelIndex lookup,
            final Uml2XsdPlugin plugin, final XMLStreamWriter xsw) throws XMLStreamException {
        writeStartElement(XsdElementName.SCHEMA, xsw);
        try {
            xsw.writeNamespace(PREFIX_XS, NAMESPACE_XS);
            final Map<String, String> prefixMappings = plugin.declarePrefixMappings();
            for (final Map.Entry<String, String> entry : prefixMappings.entrySet()) {
                final String namespace = entry.getValue();
                if (namespace == null) {
                    throw new IllegalArgumentException("namespace declared by plugin is null.");
                }
                if (namespace.trim().length() == 0) {
                    throw new IllegalArgumentException("namespace declared by plugin is the empty string.");
                }
                xsw.writeNamespace(entry.getKey(), namespace);
            }

            final String targetNamespace = plugin.getTargetNamespace();
            if (targetNamespace == null) {
                throw new IllegalArgumentException("targetNamespace declared by plugin is null.");
            }
            if (targetNamespace.trim().length() > 0) {
                xsw.writeAttribute("targetNamespace", targetNamespace);
                writeFormDefaultForElements(plugin.isElementFormDefaultQualified(), xsw);
                writeFormDefaultForAttributes(plugin.isAttributeFormDefaultQualified(), xsw);
            }

            for (final PsmDocument<Type> element : elements) {
                writeTopLevelElement(element, lookup, plugin, xsw);
            }
            for (final SimpleType simpleType : sort(combine(lookup.getDataTypes().values(), lookup.getEnumTypes()),
                    TypeComparator.SINGLETON)) {
                writeSimpleType(simpleType, lookup, plugin, xsw);
            }
            for (final ClassType enumType : sort(lookup.getClassTypes().values(), TypeComparator.SINGLETON)) {
                // DE2424 exclude creating a ComplexType with no name
                if ("".equals(enumType.getName()) == false) {
                    writeComplexType(enumType, lookup, plugin, xsw);
                }
            }
        } finally {
            xsw.writeEndElement();
        }
    }

    private static final void writeSimpleType(final SimpleType simpleType, final ModelIndex lookup,
            final Uml2XsdPlugin plugin, final XMLStreamWriter xsw) throws XMLStreamException {
        if (!isW3cXmlSchemaType(getQName(simpleType, lookup))) {
            writeStartElement(XsdElementName.SIMPLE_TYPE, xsw);
            try {
                writeNameOfSchemaType(simpleType.getName(), xsw);
                writeStartElement(XsdElementName.ANNOTATION, xsw);
                try {
                    for (final TaggedValue taggedValue : simpleType.getTaggedValues()) {
                        final TagDefinition tagDefinition = lookup.getTagDefinition(taggedValue.getTagDefinition());
                        final String name = tagDefinition.getName();
                        if (TagName.DOCUMENTATION.equals(name)) {
                            writeDocumentation(taggedValue, xsw);
                        }
                    }
                } finally {
                    writeEndElement(xsw);
                }
                writeStartElement(XsdElementName.RESTRICTION, xsw);
                try {
                    final Identifier baseId = getBase(simpleType.getId(), lookup);
                    if (baseId != null) {
                        final Type base = lookup.getType(baseId);
                        writeBaseAttribute(base, plugin, xsw);
                    } else {
                        if (simpleType instanceof EnumType) {
                            writeBaseAttribute(WxsNamespace.TOKEN, xsw);
                        } else {
                            writeBaseAttribute(WxsNamespace.STRING, xsw);
                        }
                    }
                    for (final TaggedValue taggedValue : simpleType.getTaggedValues()) {
                        final TagDefinition tagDefinition = lookup.getTagDefinition(taggedValue.getTagDefinition());
                        final String name = tagDefinition.getName();
                        if (TagName.MAX_LENGTH.equals(name)) {
                            writeFacet(XsdElementName.MAX_LENGTH, taggedValue, xsw);
                        } else if (TagName.MIN_LENGTH.equals(name)) {
                            writeFacet(XsdElementName.MIN_LENGTH, taggedValue, xsw);
                        } else if (TagName.MAX_INCLUSIVE.equals(name)) {
                            writeFacet(XsdElementName.MAX_INCLUSIVE, taggedValue, xsw);
                        } else if (TagName.MIN_INCLUSIVE.equals(name)) {
                            writeFacet(XsdElementName.MIN_INCLUSIVE, taggedValue, xsw);
                        } else if (TagName.MAX_EXCLUSIVE.equals(name)) {
                            writeFacet(XsdElementName.MAX_EXCLUSIVE, taggedValue, xsw);
                        } else if (TagName.MIN_EXCLUSIVE.equals(name)) {
                            writeFacet(XsdElementName.MIN_EXCLUSIVE, taggedValue, xsw);
                        } else if (TagName.TOTAL_DIGITS.equals(name)) {
                            writeFacet(XsdElementName.TOTAL_DIGITS, taggedValue, xsw);
                        } else if (TagName.FRACTION_DIGITS.equals(name)) {
                            writeFacet(XsdElementName.FRACTION_DIGITS, taggedValue, xsw);
                        } else if (TagName.LENGTH.equals(name)) {
                            writeFacet(XsdElementName.LENGTH, taggedValue, xsw);
                        } else if (TagName.PATTERN.equals(name)) {
                            writeFacet(XsdElementName.PATTERN, taggedValue, xsw);
                        }
                    }
                    for (final EnumLiteral literal : simpleType.getLiterals()) {
                        writeStartElement(XsdElementName.ENUMERATION, xsw);
                        writeValueAttribute(collapseWhitespace(literal.getName()), xsw);
                        xsw.writeEndElement();
                    }

                } finally {
                    writeEndElement(xsw);
                }
            } finally {
                writeEndElement(xsw);
            }
        }
    }

    private static final void writeStartElement(final XsdElementName name, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(PREFIX_XS, name.getLocalName(), NAMESPACE_XS);
    }

    private static final void writeTopLevelElement(final PsmDocument<Type> document, final ModelIndex lookup,
            final Uml2XsdPlugin plugin, final XMLStreamWriter xsw) throws XMLStreamException {
        plugin.writeTopLevelElement(document, lookup, new Uml2XsdPluginWriterAdapter(xsw, PREFIX_XS));
    }

    private static final void writeTypeAttribute(final QName name, final XMLStreamWriter xsw) throws XMLStreamException {
        writeAttribute(XsdAttributeName.TYPE, name, xsw);
    }

    private static final void writeValueAttribute(final String value, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeAttribute(XsdAttributeName.VALUE.getLocalName(), value);
    }
}
