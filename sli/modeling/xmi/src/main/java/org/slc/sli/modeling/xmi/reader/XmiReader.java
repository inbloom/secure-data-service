package org.slc.sli.modeling.xmi.reader;

import static org.slc.sli.modeling.xml.XmlTools.collapseWhitespace;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.slc.sli.modeling.uml.Association;
import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.DataType;
import org.slc.sli.modeling.uml.EnumLiteral;
import org.slc.sli.modeling.uml.EnumType;
import org.slc.sli.modeling.uml.Generalization;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.Model;
import org.slc.sli.modeling.uml.Multiplicity;
import org.slc.sli.modeling.uml.NamespaceOwnedElement;
import org.slc.sli.modeling.uml.Occurs;
import org.slc.sli.modeling.uml.Range;
import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.uml.TaggedValue;
import org.slc.sli.modeling.uml.UmlPackage;
import org.slc.sli.modeling.xmi.XmiAttributeName;
import org.slc.sli.modeling.xmi.XmiElementName;

/**
 * Reads from a file (by name) or {@link InputStream} to produce a UML {@link Model}.
 */
public final class XmiReader {
    private static final List<TaggedValue> EMPTY_TAGGED_VALUE_LIST = Collections.emptyList();
    private static final List<NamespaceOwnedElement> EMPTY_NAMESPACE_OWNED_ELEMENTS = Collections.emptyList();
    private static final String GLOBAL_NAMESPACE = "";
    /**
     * If an empty name is acceptable then use this symbolic constant.
     */
    private static final String DEFAULT_EMPTY_NAME = "";

    /**
     * A programmatic assertion that we have the reader positioned on the correct element.
     *
     * @param expectLocalName
     *            The local name that we expect.
     * @param reader
     *            The reader.
     */
    private static final void assertName(final XmiElementName name, final XMLStreamReader reader) {
        if (!match(name, reader)) {
            throw new AssertionError(reader.getLocalName());
        }
    }

    private static final <T> T assertNotNull(final T obj) {
        if (obj != null) {
            return obj;
        } else {
            throw new AssertionError();
        }
    }

    private static final void closeQuiet(final Closeable closeable) {
        try {
            closeable.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private static final Identifier getId(final XMLStreamReader reader) {
        return Identifier.fromString(reader.getAttributeValue(GLOBAL_NAMESPACE, XmiAttributeName.ID.getLocalName()));
    }

    private static final boolean getBoolean(final XmiAttributeName name, final boolean defaultValue,
            final XMLStreamReader reader) {
        final String value = reader.getAttributeValue(GLOBAL_NAMESPACE, name.getLocalName());
        if (value != null) {
            return Boolean.valueOf(value);
        } else {
            return defaultValue;
        }
    }

    private static final Identifier getIdRef(final XMLStreamReader reader) throws XmiMissingAttributeException {
        final String value = reader.getAttributeValue(GLOBAL_NAMESPACE, XmiAttributeName.IDREF.getLocalName());
        if (value != null) {
            return Identifier.fromString(value);
        } else {
            throw new XmiMissingAttributeException(XmiAttributeName.IDREF.getLocalName());
        }
    }

    private static final String getName(final XMLStreamReader reader, final String defaultName) {
        final String name = reader.getAttributeValue(GLOBAL_NAMESPACE, XmiAttributeName.NAME.getLocalName());
        if (name != null) {
            return name;
        } else {
            return defaultName;
        }
    }

    private static final Occurs getOccurs(final XMLStreamReader reader, final XmiAttributeName name) {
        final int value = Integer.valueOf(reader.getAttributeValue(GLOBAL_NAMESPACE, name.getLocalName()));
        switch (value) {
            case 0: {
                return Occurs.ZERO;
            }
            case 1: {
                return Occurs.ONE;
            }
            case -1: {
                return Occurs.UNBOUNDED;
            }
            default: {
                throw new AssertionError(value);
            }
        }
    }

    private static final boolean match(final XmiElementName name, final XMLStreamReader reader) {
        return name.getLocalName().equals(reader.getLocalName());
    }

    private static final Association readAssociation(final XMLStreamReader reader) throws XMLStreamException {
        assertName(XmiElementName.ASSOCIATION, reader);
        final List<TaggedValue> taggedValues = new LinkedList<TaggedValue>();
        final Identifier id = getId(reader);
        final String name = getName(reader, DEFAULT_EMPTY_NAME);
        XmiAssociationConnection connection = null;
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    if (match(XmiElementName.MODEL_ELEMENT_DOT_TAGGED_VALUE, reader)) {
                        taggedValues.addAll(readTaggedValueGroup(reader));
                    } else if (match(XmiElementName.ASSOCIATION_DOT_CONNECTION, reader)) {
                        connection = assertNotNull(readAssociationConnection(reader));
                    } else {
                        skipElement(reader);
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(XmiElementName.ASSOCIATION, reader);
                    return new Association(id, name, connection.getLHS(), connection.getRHS(), taggedValues);
                }
                case XMLStreamConstants.CHARACTERS: {
                    // Ignore.
                    break;
                }
                default: {
                    throw new AssertionError(reader.getEventType());
                }
            }
        }
        throw new AssertionError();
    }

    private static final XmiAssociationConnection readAssociationConnection(final XMLStreamReader reader) {
        try {
            assertName(XmiElementName.ASSOCIATION_DOT_CONNECTION, reader);
            final List<AssociationEnd> ends = new ArrayList<AssociationEnd>();
            while (reader.hasNext()) {
                reader.next();
                switch (reader.getEventType()) {
                    case XMLStreamConstants.START_ELEMENT: {
                        if (match(XmiElementName.ASSOCIATION_END, reader)) {
                            ends.add(readAssociationEnd(reader));
                        } else {
                            skipElement(reader);
                        }
                        break;
                    }
                    case XMLStreamConstants.END_ELEMENT: {
                        assertName(XmiElementName.ASSOCIATION_DOT_CONNECTION, reader);
                        return new XmiAssociationConnection(ends.get(0), ends.get(1));
                    }
                    case XMLStreamConstants.CHARACTERS: {
                        // Ignore.
                        break;
                    }
                    default: {
                        throw new AssertionError(reader.getEventType());
                    }
                }
            }
        } catch (final XMLStreamException e) {
            e.printStackTrace();
        }
        throw new AssertionError();
    }

    private static final AssociationEnd readAssociationEnd(final XMLStreamReader reader) {
        try {
            assertName(XmiElementName.ASSOCIATION_END, reader);
            final List<TaggedValue> taggedValues = new LinkedList<TaggedValue>();
            final Identifier id = getId(reader);
            final String name = getName(reader, DEFAULT_EMPTY_NAME);
            final boolean isNavigable = getBoolean(XmiAttributeName.IS_NAVIGABLE, true, reader);
            Identifier participant = null;
            final Range range = new Range(Identifier.random(), Occurs.ONE, Occurs.ONE, EMPTY_TAGGED_VALUE_LIST);
            Multiplicity multiplicity = new Multiplicity(Identifier.random(), EMPTY_TAGGED_VALUE_LIST, range);
            while (reader.hasNext()) {
                reader.next();
                switch (reader.getEventType()) {
                    case XMLStreamConstants.START_ELEMENT: {
                        if (match(XmiElementName.MODEL_ELEMENT_DOT_TAGGED_VALUE, reader)) {
                            taggedValues.addAll(readTaggedValueGroup(reader));
                        } else if (match(XmiElementName.ASSOCIATION_END_DOT_MULTIPLICITY, reader)) {
                            multiplicity = assertNotNull(readAssociationEndMultiplicity(reader));
                        } else if (match(XmiElementName.ASSOCIATION_END_DOT_PARTICIPANT, reader)) {
                            participant = readAssociationEndParticipant(reader);
                        } else {
                            skipElement(reader);
                        }
                        break;
                    }
                    case XMLStreamConstants.END_ELEMENT: {
                        assertName(XmiElementName.ASSOCIATION_END, reader);
                        return new AssociationEnd(multiplicity, name, isNavigable, id, taggedValues, participant);
                    }
                    case XMLStreamConstants.CHARACTERS: {
                        // Ignore.
                        break;
                    }
                    default: {
                        throw new AssertionError(reader.getEventType());
                    }
                }
            }
        } catch (final XMLStreamException e) {
            throw new RuntimeException(e);
        }
        throw new AssertionError();
    }

    private static final Multiplicity readAssociationEndMultiplicity(final XMLStreamReader reader)
            throws XMLStreamException {
        assertName(XmiElementName.ASSOCIATION_END_DOT_MULTIPLICITY, reader);
        Multiplicity multiplicity = null;
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    if (match(XmiElementName.MULTIPLICITY, reader)) {
                        multiplicity = assertNotNull(readMultiplicity(reader));
                    } else {
                        skipElement(reader);
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(XmiElementName.ASSOCIATION_END_DOT_MULTIPLICITY, reader);
                    return assertNotNull(multiplicity);
                }
                case XMLStreamConstants.CHARACTERS: {
                    // Ignore.
                    break;
                }
                default: {
                    throw new AssertionError(reader.getEventType());
                }
            }
        }
        throw new AssertionError();
    }

    private static final Identifier readAssociationEndParticipant(final XMLStreamReader reader)
            throws XMLStreamException {
        assertName(XmiElementName.ASSOCIATION_END_DOT_PARTICIPANT, reader);
        Identifier reference = null;
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    if (match(XmiElementName.CLASS, reader)) {
                        reference = readIdentifier(reader);
                    } else {
                        skipElement(reader);
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(XmiElementName.ASSOCIATION_END_DOT_PARTICIPANT, reader);
                    return reference;
                }
                case XMLStreamConstants.CHARACTERS: {
                    // Ignore.
                    break;
                }
                default: {
                    throw new AssertionError(reader.getEventType());
                }
            }
        }
        throw new AssertionError();
    }

    private static final Attribute readAttribute(final XMLStreamReader reader) throws XMLStreamException {
        assertName(XmiElementName.ATTRIBUTE, reader);
        final Identifier id = getId(reader);
        final String name = getName(reader, null);
        Identifier type = null;
        Multiplicity multiplicity = null;
        final List<TaggedValue> taggedValues = new LinkedList<TaggedValue>();
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    if (match(XmiElementName.STRUCTURAL_FEATURE_DOT_MULTIPLICITY, reader)) {
                        multiplicity = assertNotNull(readStructuralFeatureDotMultiplicity(reader));
                    } else if (match(XmiElementName.STRUCTURAL_FEATURE_DOT_TYPE, reader)) {
                        try {
                            type = assertNotNull(readIdentifierWrapper(XmiElementName.STRUCTURAL_FEATURE_DOT_TYPE,
                                    reader));
                        } catch (final RuntimeException e) {
                            throw new XmiBadAttributeException(name.toString(), e);
                        }
                    } else if (match(XmiElementName.MODEL_ELEMENT_DOT_TAGGED_VALUE, reader)) {
                        taggedValues.addAll(readTaggedValueGroup(reader));
                    } else {
                        throw new AssertionError(reader.getLocalName());
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(XmiElementName.ATTRIBUTE, reader);
                    return new Attribute(id, name, type, multiplicity, taggedValues);
                }
                case XMLStreamConstants.CHARACTERS: {
                    // Ignore.
                    break;
                }
                default: {
                    throw new AssertionError(reader.getEventType());
                }
            }
        }
        throw new AssertionError();
    }

    private static final List<Attribute> readClassifierDotFeature(final XMLStreamReader reader)
            throws XMLStreamException {
        assertName(XmiElementName.CLASSIFIER_DOT_FEATURE, reader);
        final List<Attribute> attributes = new LinkedList<Attribute>();
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    if (match(XmiElementName.ATTRIBUTE, reader)) {
                        attributes.add(assertNotNull(readAttribute(reader)));
                    } else if (match(XmiElementName.OPERATION, reader)) {
                        skipElement(reader);
                    } else {
                        throw new AssertionError(reader.getLocalName());
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(XmiElementName.CLASSIFIER_DOT_FEATURE, reader);
                    return assertNotNull(attributes);
                }
                case XMLStreamConstants.CHARACTERS: {
                    // Ignore.
                    break;
                }
                default: {
                    throw new AssertionError(reader.getEventType());
                }
            }
        }
        throw new AssertionError();
    }

    private static final ClassType readClassType(final XMLStreamReader reader) throws XMLStreamException {
        assertName(XmiElementName.CLASS, reader);
        boolean isAbstract = false;
        final List<Attribute> attributes = new LinkedList<Attribute>();
        final List<TaggedValue> taggedValues = new LinkedList<TaggedValue>();
        final Identifier id = getId(reader);
        final String name = getName(reader, null);
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    if (match(XmiElementName.MODEL_ELEMENT_DOT_TAGGED_VALUE, reader)) {
                        taggedValues.addAll(readTaggedValueGroup(reader));
                        break;
                    } else if (match(XmiElementName.CLASSIFIER_DOT_FEATURE, reader)) {
                        try {
                            attributes.addAll(readClassifierDotFeature(reader));
                        } catch (final RuntimeException e) {
                            throw new XmiBadModelElementException(name.toString(), e);
                        }
                        break;
                    } else if ("GeneralizableElement.generalization".equals(reader.getLocalName())) {
                        skipElement(reader);
                        break;
                    } else if ("ModelElement.comment".equals(reader.getLocalName())) {
                        skipElement(reader);
                        break;
                    } else if ("ModelElement.clientDependency".equals(reader.getLocalName())) {
                        skipElement(reader);
                        break;
                    } else if ("Namespace.ownedElement".equals(reader.getLocalName())) {
                        skipElement(reader);
                        break;
                    } else {
                        throw new RuntimeException("Expecting Foo element, got: " + reader.getLocalName());
                    }
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(XmiElementName.CLASS, reader);
                    return new ClassType(id, name, isAbstract, attributes, taggedValues);
                }
                case XMLStreamConstants.CHARACTERS: {
                    // Ignore.
                    break;
                }
                default: {
                    throw new AssertionError(reader.getEventType());
                }
            }
        }
        throw new AssertionError();
    }

    private static final Model readContent(final XMLStreamReader reader) {
        try {
            if ("XMI.content".equals(reader.getLocalName())) {
                Model model = null;
                while (reader.hasNext()) {
                    reader.next();
                    switch (reader.getEventType()) {
                        case XMLStreamConstants.START_ELEMENT: {
                            if ("Model".equals(reader.getLocalName())) {
                                model = readModel(reader);
                                if (model == null) {
                                    throw new IllegalStateException();
                                }
                                break;
                            } else {
                                throw new RuntimeException("Expecting Foo element, got: " + reader.getLocalName());
                            }
                        }
                        case XMLStreamConstants.END_ELEMENT: {
                            if ("XMI.content".equals(reader.getLocalName())) {
                                if (model == null) {
                                    throw new IllegalStateException();
                                }
                                return model;
                            } else {
                                throw new AssertionError(reader.getLocalName());
                            }
                        }
                        case XMLStreamConstants.CHARACTERS: {
                            // Ignore.
                            break;
                        }
                        default: {
                            throw new AssertionError(reader.getEventType());
                        }
                    }
                }
                throw new AssertionError();
            } else {
                throw new AssertionError(reader.getLocalName());
            }
        } catch (final XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    private static final DataType readDataType(final XMLStreamReader reader) throws XMLStreamException {
        assertName(XmiElementName.DATA_TYPE, reader);
        boolean isAbstract = false;
        final List<TaggedValue> taggedValues = new LinkedList<TaggedValue>();
        final Identifier id = getId(reader);
        final String name = getName(reader, null);
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    if (match(XmiElementName.MODEL_ELEMENT_DOT_TAGGED_VALUE, reader)) {
                        try {
                            taggedValues.addAll(readTaggedValueGroup(reader));
                        } catch (final RuntimeException e) {
                            throw new XmiBadModelElementException(name, e);
                        }
                    } else if (match(XmiElementName.CLASSIFIER_DOT_FEATURE, reader)) {
                        skipElement(reader);
                    } else {
                        skipElement(reader);
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(XmiElementName.DATA_TYPE, reader);
                    return new DataType(id, name, isAbstract, taggedValues);
                }
                case XMLStreamConstants.CHARACTERS: {
                    // Ignore.
                    break;
                }
                default: {
                    throw new AssertionError(reader.getEventType());
                }
            }
        }
        throw new AssertionError();
    }

    private static final Model readDocument(final XMLStreamReader reader) {
        try {
            if (XMLStreamConstants.START_DOCUMENT == reader.getEventType()) {
                Model model = null;
                while (reader.hasNext()) {
                    reader.next();
                    switch (reader.getEventType()) {
                        case XMLStreamConstants.START_ELEMENT: {
                            if ("XMI".equals(reader.getLocalName())) {
                                model = readXMI(reader);
                                if (model == null) {
                                    throw new IllegalStateException();
                                }
                                break;
                            } else {
                                throw new RuntimeException("Expecting Foo element, got: " + reader.getLocalName());
                            }
                        }
                        case XMLStreamConstants.END_DOCUMENT: {
                            if (model == null) {
                                throw new IllegalStateException();
                            }
                            return model;
                        }
                        default: {
                            throw new AssertionError(reader.getEventType());
                        }
                    }
                }
                throw new AssertionError();
            } else {
                throw new AssertionError(reader.getLocalName());
            }
        } catch (final XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    private static final EnumType readEnumeration(final XMLStreamReader reader) throws XMLStreamException {
        assertName(XmiElementName.ENUMERATION, reader);
        final List<EnumLiteral> literals = new LinkedList<EnumLiteral>();
        final List<TaggedValue> taggedValues = new LinkedList<TaggedValue>();
        final Identifier id = Identifier.fromString(reader.getAttributeValue("", "xmi.id"));
        final String name = getName(reader, null);
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    if (match(XmiElementName.MODEL_ELEMENT_DOT_TAGGED_VALUE, reader)) {
                        taggedValues.addAll(readTaggedValueGroup(reader));
                    } else if (match(XmiElementName.ENUMERATION_LITERAL_GROUP, reader)) {
                        literals.addAll(readEnumerationLiteralGroup(reader));
                    } else {
                        skipElement(reader);
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(XmiElementName.ENUMERATION, reader);
                    return new EnumType(id, name, literals, taggedValues);
                }
                case XMLStreamConstants.CHARACTERS: {
                    // Ignore.
                    break;
                }
                default: {
                    throw new AssertionError(reader.getEventType());
                }
            }
        }
        throw new AssertionError();
    }

    private static final EnumLiteral readEnumerationLiteral(final XMLStreamReader reader) throws XMLStreamException {
        assertName(XmiElementName.ENUMERATION_LITERAL, reader);
        final List<TaggedValue> taggedValues = new LinkedList<TaggedValue>();
        final Identifier id = getId(reader);
        final String name = getName(reader, null);
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    if (match(XmiElementName.MODEL_ELEMENT_DOT_TAGGED_VALUE, reader)) {
                        taggedValues.addAll(readTaggedValueGroup(reader));
                        break;
                    } else {
                        throw new AssertionError(reader.getLocalName());
                    }
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(XmiElementName.ENUMERATION_LITERAL, reader);
                    return new EnumLiteral(id, name, taggedValues);
                }
                case XMLStreamConstants.CHARACTERS: {
                    // Ignore.
                    break;
                }
                default: {
                    throw new AssertionError(reader.getEventType());
                }
            }
        }
        throw new AssertionError();
    }

    private static final List<EnumLiteral> readEnumerationLiteralGroup(final XMLStreamReader reader)
            throws XMLStreamException {
        assertName(XmiElementName.ENUMERATION_LITERAL_GROUP, reader);
        final List<EnumLiteral> literals = new LinkedList<EnumLiteral>();
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    if (match(XmiElementName.ENUMERATION_LITERAL, reader)) {
                        literals.add(assertNotNull(readEnumerationLiteral(reader)));
                    } else {
                        skipElement(reader);
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(XmiElementName.ENUMERATION_LITERAL_GROUP, reader);
                    return literals;
                }
                case XMLStreamConstants.CHARACTERS: {
                    // Ignore.
                    break;
                }
                default: {
                    throw new AssertionError(reader.getEventType());
                }
            }
        }
        throw new AssertionError();
    }

    /**
     * Reads XMI from an {@link InputStream}.
     *
     * @param stream
     *            The {@link InputStream}.
     * @return The parsed {@link Model}.
     */
    public static final Model readModel(final InputStream stream) {
        final XMLInputFactory factory = XMLInputFactory.newInstance();
        try {
            final XMLStreamReader reader = factory.createXMLStreamReader(stream);
            try {
                return readDocument(reader);
            } finally {
                reader.close();
            }
        } catch (final XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    public static final Model readModel(final File file) throws FileNotFoundException {
        final InputStream istream = new BufferedInputStream(new FileInputStream(file));
        try {
            return readModel(istream);
        } finally {
            closeQuiet(istream);
        }
    }

    public static final Model readModel(final String fileName) throws FileNotFoundException {
        final InputStream istream = new BufferedInputStream(new FileInputStream(fileName));
        try {
            return readModel(istream);
        } finally {
            closeQuiet(istream);
        }
    }

    private static final Model readModel(final XMLStreamReader reader) throws XMLStreamException {
        assertName(XmiElementName.MODEL, reader);
        final Identifier id = getId(reader);
        final String name = getName(reader, null);
        List<TaggedValue> taggedValues = EMPTY_TAGGED_VALUE_LIST;
        List<NamespaceOwnedElement> ownedElements = EMPTY_NAMESPACE_OWNED_ELEMENTS;
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    if (match(XmiElementName.MODEL_ELEMENT_DOT_TAGGED_VALUE, reader)) {
                        taggedValues = readTaggedValueGroup(reader);
                    } else if (match(XmiElementName.NAMESPACE_DOT_OWNED_ELEMENT, reader)) {
                        ownedElements = readNamespaceOwnedElement(reader);
                    } else {
                        throw new AssertionError(reader.getLocalName());
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(XmiElementName.MODEL, reader);
                    return new Model(id, name, taggedValues, ownedElements);
                }
                case XMLStreamConstants.CHARACTERS: {
                    // Ignore.
                    break;
                }
                default: {
                    throw new AssertionError(reader.getEventType());
                }
            }
        }
        throw new AssertionError();
    }

    private static final Multiplicity readMultiplicity(final XMLStreamReader reader) throws XMLStreamException {
        assertName(XmiElementName.MULTIPLICITY, reader);
        final Identifier id = getId(reader);

        Range range = null;
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    if (match(XmiElementName.MULTIPLICITY_DOT_RANGE, reader)) {
                        range = readMultiplicityDotRange(reader);
                    } else {
                        throw new AssertionError(reader.getLocalName());
                        // skipElement(reader);
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(XmiElementName.MULTIPLICITY, reader);
                    return new Multiplicity(id, EMPTY_TAGGED_VALUE_LIST, range);
                }
                case XMLStreamConstants.CHARACTERS: {
                    // Ignore.
                    break;
                }
                default: {
                    throw new AssertionError(reader.getEventType());
                }
            }
        }
        throw new AssertionError();
    }

    private static final Range readMultiplicityDotRange(final XMLStreamReader reader) throws XMLStreamException {
        assertName(XmiElementName.MULTIPLICITY_DOT_RANGE, reader);
        Range range = null;
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    if (match(XmiElementName.MULTIPLICITY_RANGE, reader)) {
                        range = assertNotNull(readMultiplicityRange(reader));
                    } else {
                        throw new AssertionError(reader.getLocalName());
                        // skipElement(reader);
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(XmiElementName.MULTIPLICITY_DOT_RANGE, reader);
                    return assertNotNull(range);
                }
                case XMLStreamConstants.CHARACTERS: {
                    // Ignore.
                    break;
                }
                default: {
                    throw new AssertionError(reader.getEventType());
                }
            }
        }
        throw new AssertionError();
    }

    private static final Range readMultiplicityRange(final XMLStreamReader reader) throws XMLStreamException {
        assertName(XmiElementName.MULTIPLICITY_RANGE, reader);
        final Identifier id = getId(reader);
        final Occurs lowerBound = getOccurs(reader, XmiAttributeName.LOWER);
        final Occurs upperBound = getOccurs(reader, XmiAttributeName.UPPER);
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    skipElement(reader);
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(XmiElementName.MULTIPLICITY_RANGE, reader);
                    return new Range(id, lowerBound, upperBound, EMPTY_TAGGED_VALUE_LIST);
                }
                case XMLStreamConstants.CHARACTERS: {
                    // Ignore.
                    break;
                }
                default: {
                    throw new AssertionError(reader.getEventType());
                }
            }
        }
        throw new AssertionError();
    }

    private static final Generalization readGeneralization(final XMLStreamReader reader) throws XMLStreamException {
        assertName(XmiElementName.GENERALIZATION, reader);
        final Identifier id = getId(reader);
        final String name = getName(reader, DEFAULT_EMPTY_NAME);
        final List<TaggedValue> taggedValues = new LinkedList<TaggedValue>();
        Identifier child = null;
        Identifier parent = null;
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    if (match(XmiElementName.MODEL_ELEMENT_DOT_TAGGED_VALUE, reader)) {
                        taggedValues.addAll(readTaggedValueGroup(reader));
                        break;
                    } else if (match(XmiElementName.GENERALIZATION_DOT_CHILD, reader)) {
                        child = assertNotNull(readIdentifierWrapper(XmiElementName.GENERALIZATION_DOT_CHILD, reader));
                        break;
                    } else if (match(XmiElementName.GENERALIZATION_DOT_PARENT, reader)) {
                        parent = assertNotNull(readIdentifierWrapper(XmiElementName.GENERALIZATION_DOT_PARENT, reader));
                        break;
                    } else {
                        throw new AssertionError(reader.getLocalName());
                    }
                    // skipElement(reader);
                    // break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(XmiElementName.GENERALIZATION, reader);
                    return new Generalization(name, id, taggedValues, child, parent);
                }
                case XMLStreamConstants.CHARACTERS: {
                    // Ignore.
                    break;
                }
                default: {
                    throw new AssertionError(reader.getEventType());
                }
            }
        }
        throw new AssertionError();
    }

    /**
     * The UML:Namespace.ownedElement is simply a list of model elements that can belong in a
     * name-space.
     *
     * It has no identity of its own and is only present in the serialization format.
     */
    private static final List<NamespaceOwnedElement> readNamespaceOwnedElement(final XMLStreamReader reader)
            throws XMLStreamException {
        assertName(XmiElementName.NAMESPACE_DOT_OWNED_ELEMENT, reader);
        final List<NamespaceOwnedElement> ownedElements = new LinkedList<NamespaceOwnedElement>();
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    if (match(XmiElementName.CLASS, reader)) {
                        ownedElements.add(assertNotNull(readClassType(reader)));
                    } else if (match(XmiElementName.DATA_TYPE, reader)) {
                        ownedElements.add(assertNotNull(readDataType(reader)));
                    } else if (match(XmiElementName.ENUMERATION, reader)) {
                        ownedElements.add(assertNotNull(readEnumeration(reader)));
                    } else if (match(XmiElementName.TAG_DEFINITION, reader)) {
                        ownedElements.add(assertNotNull(readTagDefinition(reader)));
                    } else if (match(XmiElementName.GENERALIZATION, reader)) {
                        ownedElements.add(assertNotNull(readGeneralization(reader)));
                    } else if (match(XmiElementName.ASSOCIATION, reader)) {
                        ownedElements.add(assertNotNull(readAssociation(reader)));
                    } else if (match(XmiElementName.COMMENT, reader)) {
                        skipElement(reader);
                    } else if (match(XmiElementName.PACKAGE, reader)) {
                        ownedElements.add(assertNotNull(readPackage(reader)));
                    } else {
                        skipElement(reader);
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(XmiElementName.NAMESPACE_DOT_OWNED_ELEMENT, reader);
                    return Collections.unmodifiableList(ownedElements);
                }
                case XMLStreamConstants.CHARACTERS: {
                    // Ignore.
                    break;
                }
                default: {
                    throw new AssertionError(reader.getEventType());
                }
            }
        }
        throw new AssertionError();
    }

    private static final Identifier readIdentifier(final XMLStreamReader reader) throws XMLStreamException {
        final XmiElementName name = assertNotNull(XmiElementName.getElementName(reader.getLocalName()));
        final Identifier id = getIdRef(reader);
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    skipElement(reader);
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(name, reader);
                    return id;
                }
                case XMLStreamConstants.CHARACTERS: {
                    // Ignore.
                    break;
                }
                default: {
                    throw new AssertionError(reader.getEventType());
                }
            }
        }
        throw new AssertionError();
    }

    private static final Multiplicity readStructuralFeatureDotMultiplicity(final XMLStreamReader reader)
            throws XMLStreamException {
        assertName(XmiElementName.STRUCTURAL_FEATURE_DOT_MULTIPLICITY, reader);
        Multiplicity multiplicity = null;
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    if (match(XmiElementName.MULTIPLICITY, reader)) {
                        multiplicity = assertNotNull(readMultiplicity(reader));
                    } else {
                        throw new AssertionError(reader.getLocalName());
                        // skipElement(reader);
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(XmiElementName.STRUCTURAL_FEATURE_DOT_MULTIPLICITY, reader);
                    return assertNotNull(multiplicity);
                }
                case XMLStreamConstants.CHARACTERS: {
                    // Ignore.
                    break;
                }
                default: {
                    throw new AssertionError(reader.getEventType());
                }
            }
        }
        throw new AssertionError();
    }

    private static final Identifier readIdentifierWrapper(final XmiElementName name, final XMLStreamReader reader)
            throws XMLStreamException {
        assertName(name, reader);
        Identifier reference = null;
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    if (match(XmiElementName.DATA_TYPE, reader)) {
                        reference = assertNotNull(readIdentifier(reader));
                    } else if (match(XmiElementName.CLASS, reader)) {
                        reference = assertNotNull(readIdentifier(reader));
                    } else if (match(XmiElementName.ENUMERATION, reader)) {
                        reference = assertNotNull(readIdentifier(reader));
                    } else {
                        throw new AssertionError(reader.getLocalName());
                        // skipElement(reader);
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(name, reader);
                    return assertNotNull(reference);
                }
                case XMLStreamConstants.CHARACTERS: {
                    // Ignore.
                    break;
                }
                default: {
                    throw new AssertionError(reader.getEventType());
                }
            }
        }
        throw new AssertionError();
    }

    private static final TagDefinition readTagDefinition(final XMLStreamReader reader) throws XMLStreamException {
        assertName(XmiElementName.TAG_DEFINITION, reader);
        final List<TaggedValue> taggedValues = new LinkedList<TaggedValue>();
        final Identifier id = getId(reader);
        final String name = assertNotNull(getName(reader, null));
        Multiplicity multiplicity = null;
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    if (match(XmiElementName.TAG_DEFINITION_DOT_MULTIPLICITY, reader)) {
                        multiplicity = assertNotNull(readTagDefinitionMultiplicity(reader));
                    } else {
                        skipElement(reader);
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(XmiElementName.TAG_DEFINITION, reader);
                    return new TagDefinition(id, taggedValues, name, multiplicity);
                }
                case XMLStreamConstants.CHARACTERS: {
                    // Ignore.
                    break;
                }
                default: {
                    throw new AssertionError(reader.getEventType());
                }
            }
        }
        throw new AssertionError();
    }

    private static final UmlPackage readPackage(final XMLStreamReader reader) throws XMLStreamException {
        assertName(XmiElementName.PACKAGE, reader);
        final List<TaggedValue> taggedValues = new LinkedList<TaggedValue>();
        final Identifier id = getId(reader);
        final String name = assertNotNull(getName(reader, null));
        List<NamespaceOwnedElement> ownedElements = EMPTY_NAMESPACE_OWNED_ELEMENTS;
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    if (match(XmiElementName.NAMESPACE_DOT_OWNED_ELEMENT, reader)) {
                        ownedElements = readNamespaceOwnedElement(reader);
                    } else if (match(XmiElementName.MODEL_ELEMENT_DOT_TAGGED_VALUE, reader)) {
                        taggedValues.addAll(readTaggedValueGroup(reader));
                    } else {
                        throw new AssertionError(reader.getLocalName());
                        // skipElement(reader);
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(XmiElementName.PACKAGE, reader);
                    return new UmlPackage(name, id, taggedValues, ownedElements);
                }
                case XMLStreamConstants.CHARACTERS: {
                    // Ignore.
                    break;
                }
                default: {
                    throw new AssertionError(reader.getEventType());
                }
            }
        }
        throw new AssertionError();
    }

    private static final Multiplicity readTagDefinitionMultiplicity(final XMLStreamReader reader)
            throws XMLStreamException {
        assertName(XmiElementName.TAG_DEFINITION_DOT_MULTIPLICITY, reader);
        Multiplicity multiplicity = null;
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    if (match(XmiElementName.MULTIPLICITY, reader)) {
                        multiplicity = assertNotNull(readMultiplicity(reader));
                    } else {
                        throw new AssertionError(reader.getLocalName());
                        // skipElement(reader);
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(XmiElementName.TAG_DEFINITION_DOT_MULTIPLICITY, reader);
                    return assertNotNull(multiplicity);
                }
                case XMLStreamConstants.CHARACTERS: {
                    // Ignore.
                    break;
                }
                default: {
                    throw new AssertionError(reader.getEventType());
                }
            }
        }
        throw new AssertionError();
    }

    private static final TaggedValue readTaggedValue(final XMLStreamReader reader) throws XMLStreamException {
        assertName(XmiElementName.TAGGED_VALUE, reader);
        final Identifier id = getId(reader);
        String dataValue = "";
        Identifier tagDefinition = null;
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    if (match(XmiElementName.TAGGED_VALUE_DOT_DATA_VALUE, reader)) {
                        dataValue = assertNotNull(readTaggedValueData(reader));
                    } else if (match(XmiElementName.TAGGED_VALUE_DOT_TYPE, reader)) {
                        tagDefinition = assertNotNull(readTaggedValueType(reader));
                    } else {
                        throw new AssertionError(reader.getLocalName());
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(XmiElementName.TAGGED_VALUE, reader);
                    return new TaggedValue(id, EMPTY_TAGGED_VALUE_LIST, dataValue, tagDefinition);
                }
                case XMLStreamConstants.CHARACTERS: {
                    // Ignore.
                    break;
                }
                default: {
                    throw new AssertionError(reader.getEventType());
                }
            }
        }
        throw new AssertionError();
    }

    private static final String readTaggedValueData(final XMLStreamReader reader) throws XMLStreamException {
        assertName(XmiElementName.TAGGED_VALUE_DOT_DATA_VALUE, reader);
        final StringBuilder sb = new StringBuilder();
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    throw new AssertionError(reader.getLocalName());
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(XmiElementName.TAGGED_VALUE_DOT_DATA_VALUE, reader);
                    return collapseWhitespace(sb.toString());
                }
                case XMLStreamConstants.CHARACTERS: {
                    sb.append(reader.getText());
                    break;
                }
                default: {
                    throw new AssertionError(reader.getEventType());
                }
            }
        }
        throw new AssertionError();
    }

    private static final List<TaggedValue> readTaggedValueGroup(final XMLStreamReader reader) {
        final List<TaggedValue> taggedValues = new LinkedList<TaggedValue>();
        try {
            if (match(XmiElementName.MODEL_ELEMENT_DOT_TAGGED_VALUE, reader)) {
                while (reader.hasNext()) {
                    reader.next();
                    switch (reader.getEventType()) {
                        case XMLStreamConstants.START_ELEMENT: {
                            if ("TaggedValue".equals(reader.getLocalName())) {
                                taggedValues.add(assertNotNull(readTaggedValue(reader)));
                                break;
                            } else {
                                throw new AssertionError(reader.getLocalName());
                            }
                        }
                        case XMLStreamConstants.END_ELEMENT: {
                            if (match(XmiElementName.MODEL_ELEMENT_DOT_TAGGED_VALUE, reader)) {
                                return taggedValues;
                            } else {
                                throw new AssertionError(reader.getLocalName());
                            }
                        }
                        case XMLStreamConstants.CHARACTERS: {
                            // Ignore.
                            break;
                        }
                        default: {
                            throw new AssertionError(reader.getEventType());
                        }
                    }
                }
            } else {
                throw new AssertionError(reader.getLocalName());
            }
        } catch (final XMLStreamException e) {
            e.printStackTrace();
        }
        throw new AssertionError();
    }

    private static final Identifier readTaggedValueType(final XMLStreamReader reader) throws XMLStreamException {
        assertName(XmiElementName.TAGGED_VALUE_DOT_TYPE, reader);
        Identifier reference = null;
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    if (match(XmiElementName.TAG_DEFINITION, reader)) {
                        reference = assertNotNull(readIdentifier(reader));
                    } else {
                        throw new AssertionError(reader.getLocalName());
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(XmiElementName.TAGGED_VALUE_DOT_TYPE, reader);
                    return assertNotNull(reference);
                }
                case XMLStreamConstants.CHARACTERS: {
                    // Ignore.
                    break;
                }
                default: {
                    throw new AssertionError(reader.getEventType());
                }
            }
        }
        throw new AssertionError();
    }

    private static final Model readXMI(final XMLStreamReader reader) {
        try {
            if ("XMI".equals(reader.getLocalName())) {
                Model model = null;
                while (reader.hasNext()) {
                    reader.next();
                    switch (reader.getEventType()) {
                        case XMLStreamConstants.START_ELEMENT: {
                            if ("XMI.header".equals(reader.getLocalName())) {
                                skipElement(reader);
                                break;
                            } else if ("XMI.content".equals(reader.getLocalName())) {
                                model = readContent(reader);
                                if (model == null) {
                                    throw new IllegalStateException();
                                }
                                break;
                            } else {
                                throw new RuntimeException("Expecting Foo element, got: " + reader.getLocalName());
                            }
                        }
                        case XMLStreamConstants.END_ELEMENT: {
                            if ("XMI".equals(reader.getLocalName())) {
                                if (model == null) {
                                    throw new IllegalStateException();
                                }
                                return model;
                            } else {
                                throw new AssertionError(reader.getLocalName());
                            }
                        }
                        case XMLStreamConstants.CHARACTERS: {
                            // Ignore.
                            break;
                        }
                        default: {
                            throw new AssertionError(reader.getEventType());
                        }
                    }
                }
            } else {
                throw new AssertionError(reader.getLocalName());
            }
        } catch (final XMLStreamException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Skips (recursively) over the element in question. Also useful during development.
     *
     * @param reader
     *            The StAX {@link XMLStreamReader}.
     */
    private static final void skipElement(final XMLStreamReader reader) throws XMLStreamException {
        final String localName = reader.getLocalName();
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    skipElement(reader);
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (localName.equals(reader.getLocalName())) {
                        return;
                    } else {
                        throw new AssertionError(reader.getLocalName());
                    }
                }
                case XMLStreamConstants.CHARACTERS: {
                    // Ignore.
                    break;
                }
                default: {
                    throw new AssertionError(reader.getEventType());
                }
            }
        }
        throw new AssertionError();
    }
}
