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

package org.slc.sli.modeling.xmi.writer;

import org.apache.commons.io.IOUtils;
import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.DataType;
import org.slc.sli.modeling.uml.EnumLiteral;
import org.slc.sli.modeling.uml.EnumType;
import org.slc.sli.modeling.uml.Generalization;
import org.slc.sli.modeling.uml.HasIdentity;
import org.slc.sli.modeling.uml.HasName;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.Model;
import org.slc.sli.modeling.uml.ModelElement;
import org.slc.sli.modeling.uml.Multiplicity;
import org.slc.sli.modeling.uml.NamespaceOwnedElement;
import org.slc.sli.modeling.uml.Occurs;
import org.slc.sli.modeling.uml.Range;
import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.uml.TaggedValue;
import org.slc.sli.modeling.uml.UmlPackage;
import org.slc.sli.modeling.uml.Visitor;
import org.slc.sli.modeling.uml.index.ModelIndex;
import org.slc.sli.modeling.xmi.XmiAttributeName;
import org.slc.sli.modeling.xmi.XmiElementName;
import org.slc.sli.modeling.xmi.XmiRuntimeException;
import org.slc.sli.modeling.xml.IndentingXMLStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Writes a UML {@link Model} to a file (by name) or {@link OutputStream}.
 */
public final class XmiWriter {

    private static final Logger LOG = LoggerFactory.getLogger(XmiWriter.class);

    private static final String NAMESPACE_UML = "org.omg.xmi.namespace.UML";
    private static final String PREFIX_UML = "UML";

    private static final void closeQuiet(final Closeable closeable) {
        IOUtils.closeQuietly(closeable);
    }

    private static final int range(final Occurs value) {
        if (value == null) {
            throw new IllegalArgumentException("value");
        }
        switch (value) {
            case ZERO: {
                return 0;
            }
            case ONE: {
                return 1;
            }
            case UNBOUNDED: {
                return -1;
            }
            default: {
                throw new AssertionError(value);
            }
        }
    }

    private static final void writeGeneralization(final Generalization generalization, final ModelIndex mapper,
                                                  final XMLStreamWriter xsw) throws XMLStreamException {

        xsw.writeStartElement(PREFIX_UML, XmiElementName.GENERALIZATION.getLocalName(), NAMESPACE_UML);
        writeId(generalization, xsw);
        xsw.writeAttribute(XmiAttributeName.NAME.getLocalName(), generalization.getName());
        writeModelElementTaggedValues(generalization, mapper, xsw);

        xsw.writeStartElement(PREFIX_UML, XmiElementName.GENERALIZATION_DOT_CHILD.getLocalName(), NAMESPACE_UML);
        writeReference(generalization.getChild(), mapper, xsw);
        xsw.writeEndElement();

        xsw.writeStartElement(PREFIX_UML, XmiElementName.GENERALIZATION_DOT_PARENT.getLocalName(), NAMESPACE_UML);
        writeReference(generalization.getParent(), mapper, xsw);
        xsw.writeEndElement();

        xsw.writeEndElement();
    }

    private static final void writeAssociationEnd(final AssociationEnd end, final ModelIndex mapper,
                                                  final XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeStartElement(PREFIX_UML, XmiElementName.ASSOCIATION_END.getLocalName(), NAMESPACE_UML);
        writeId(end, xsw);
        xsw.writeAttribute(XmiAttributeName.NAME.getLocalName(), end.getName());
        xsw.writeAttribute(XmiAttributeName.IS_NAVIGABLE.getLocalName(), Boolean.toString(end.isNavigable()));
        xsw.writeAttribute(XmiAttributeName.ASSOCIATED_ATTRIBUTE_NAME.getLocalName(), end.getAssociatedAttributeName());
        writeModelElementTaggedValues(end, mapper, xsw);
        xsw.writeStartElement(PREFIX_UML, XmiElementName.ASSOCIATION_END_DOT_MULTIPLICITY.getLocalName(), NAMESPACE_UML);
        writeMultiplicity(end.getMultiplicity(), xsw);
        xsw.writeEndElement();
        writeAssociationEndParticipant(end.getType(), mapper, xsw);
        xsw.writeEndElement();
    }

    private static final void writeAssociationEndParticipant(final Identifier participant, final ModelIndex mapper,
                                                             final XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeStartElement(PREFIX_UML, XmiElementName.ASSOCIATION_END_DOT_PARTICIPANT.getLocalName(), NAMESPACE_UML);
        try {
            writeReference(participant, mapper, xsw);
        } finally {
            xsw.writeEndElement();
        }
    }

    private static final void writeAttribute(final Attribute attribute, final ModelIndex mapper,
                                             final XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeStartElement(PREFIX_UML, XmiElementName.ATTRIBUTE.getLocalName(), NAMESPACE_UML);
        try {
            writeId(attribute, xsw);
            xsw.writeAttribute(XmiAttributeName.NAME.getLocalName(), attribute.getName());
            writeModelElementTaggedValues(attribute, mapper, xsw);
            xsw.writeStartElement(PREFIX_UML, XmiElementName.STRUCTURAL_FEATURE_DOT_MULTIPLICITY.getLocalName(),
                    NAMESPACE_UML);
            try {
                writeMultiplicity(attribute.getMultiplicity(), xsw);
            } finally {
                xsw.writeEndElement();
            }
            xsw.writeStartElement(PREFIX_UML, XmiElementName.STRUCTURAL_FEATURE_DOT_TYPE.getLocalName(), NAMESPACE_UML);
            try {
                writeReference(attribute.getType(), mapper, xsw);
            } finally {
                xsw.writeEndElement();
            }
        } finally {
            xsw.writeEndElement();
        }
    }

    private static final void writeClassType(final ClassType classType, final ModelIndex mapper,
                                             final XMLStreamWriter xsw) throws XMLStreamException {
        if (classType.isClassType()) {
            if (classType.isAssociation()) {
                xsw.writeStartElement(PREFIX_UML, XmiElementName.ASSOCIATION_CLASS.getLocalName(), NAMESPACE_UML);
            } else {
                xsw.writeStartElement(PREFIX_UML, XmiElementName.CLASS.getLocalName(), NAMESPACE_UML);
            }
        } else {
            if (classType.isAssociation()) {
                xsw.writeStartElement(PREFIX_UML, XmiElementName.ASSOCIATION.getLocalName(), NAMESPACE_UML);
            } else {
                throw new AssertionError();
            }
        }
        try {
            writeId(classType, xsw);
            xsw.writeAttribute(XmiAttributeName.NAME.getLocalName(), classType.getName());
            writeModelElementTaggedValues(classType, mapper, xsw);
            if (classType.isAssociation()) {
                xsw.writeStartElement(PREFIX_UML, XmiElementName.ASSOCIATION_DOT_CONNECTION.getLocalName(),
                        NAMESPACE_UML);
                try {
                    writeAssociationEnd(classType.getLHS(), mapper, xsw);
                    writeAssociationEnd(classType.getRHS(), mapper, xsw);
                } finally {
                    xsw.writeEndElement();
                }
            }
            xsw.writeStartElement(PREFIX_UML, XmiElementName.CLASSIFIER_DOT_FEATURE.getLocalName(), NAMESPACE_UML);
            try {
                for (final Attribute attribute : classType.getAttributes()) {
                    writeAttribute(attribute, mapper, xsw);
                }
            } finally {
                xsw.writeEndElement();
            }
        } finally {
            xsw.writeEndElement();
        }
    }

    private static final void writeContent(final Model model, final ModelIndex mapper, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement("XMI.content");
        try {
            writeModel(model, mapper, xsw);
        } finally {
            xsw.writeEndElement();
        }
    }

    private static final void writeDataType(final DataType dataType, final ModelIndex mapper, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(PREFIX_UML, XmiElementName.DATA_TYPE.getLocalName(), NAMESPACE_UML);
        try {
            writeId(dataType, xsw);
            xsw.writeAttribute(XmiAttributeName.NAME.getLocalName(), dataType.getName());
            xsw.writeAttribute("isAbstract", Boolean.toString(dataType.isAbstract()));
            writeModelElementTaggedValues(dataType, mapper, xsw);
        } finally {
            xsw.writeEndElement();
        }
    }

    public static final void writeDocument(final Model model, final ModelIndex mapper, final OutputStream outstream) {
        final XMLOutputFactory xof = XMLOutputFactory.newInstance();
        try {
            final XMLStreamWriter xsw = new IndentingXMLStreamWriter(xof.createXMLStreamWriter(outstream, "UTF-8"));
            xsw.writeStartDocument("UTF-8", "1.0");
            try {
                XmiWriter.writeXMI(model, mapper, xsw);
            } finally {
                xsw.writeEndDocument();
            }
            xsw.flush();
        } catch (final XMLStreamException e) {
            throw new XmiRuntimeException(e);
        }
    }

    public static final void writeDocument(final Model model, final ModelIndex mapper, final String fileName) {
        try {
            final OutputStream outstream = new BufferedOutputStream(new FileOutputStream(fileName));
            try {
                writeDocument(model, mapper, outstream);
            } finally {
                closeQuiet(outstream);
            }
        } catch (final FileNotFoundException e) {
            LOG.warn(e.getMessage());
        }
    }

    public static final void writeDocument(final Model model, final ModelIndex mapper, final File file) {
        try {
            final OutputStream outstream = new BufferedOutputStream(new FileOutputStream(file));
            try {
                writeDocument(model, mapper, outstream);
            } finally {
                closeQuiet(outstream);
            }
        } catch (final FileNotFoundException e) {
            LOG.warn(e.getMessage());
        }
    }

    private static final void writeStartElement(final XmiElementName name, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(PREFIX_UML, name.getLocalName(), NAMESPACE_UML);
    }

    private static final void writeEnumType(final EnumType enumType, final ModelIndex mapper, final XMLStreamWriter xsw)
            throws XMLStreamException {
        writeStartElement(XmiElementName.ENUMERATION, xsw);
        try {
            writeId(enumType, xsw);
            writeName(enumType, mapper, xsw);
            writeModelElementTaggedValues(enumType, mapper, xsw);
            writeStartElement(XmiElementName.ENUMERATION_LITERAL_GROUP, xsw);
            try {
                for (final EnumLiteral literal : enumType.getLiterals()) {
                    writeStartElement(XmiElementName.ENUMERATION_LITERAL, xsw);
                    writeId(literal, xsw);
                    writeName(literal, mapper, xsw);
                    xsw.writeAttribute("isSpecification", Boolean.toString(false));
                    xsw.writeEndElement();
                }
            } finally {
                xsw.writeEndElement();
            }
        } finally {
            xsw.writeEndElement();
        }
    }

    private static final void writeId(final HasIdentity hasIdentity, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeAttribute(XmiAttributeName.ID.getLocalName(), hasIdentity.getId().toString());
    }

    private static final void writeName(final HasName hasName, final ModelIndex mapper, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeAttribute(XmiAttributeName.NAME.getLocalName(), hasName.getName());
    }

    private static final void writeModel(final Model model, final ModelIndex mapper, final XMLStreamWriter xsw)
            throws XMLStreamException {

        xsw.writeStartElement(PREFIX_UML, XmiElementName.MODEL.getLocalName(), NAMESPACE_UML);
        try {
            xsw.writeAttribute(XmiAttributeName.ID.getLocalName(), model.getId().toString());
            xsw.writeAttribute(XmiAttributeName.NAME.getLocalName(), model.getName());
            writeModelElementTaggedValues(model, mapper, xsw);
            writeNamespaceOwnedElements(model.getOwnedElements(), mapper, xsw);
        } finally {
            xsw.writeEndElement();
        }
    }

    private static final void writeNamespaceOwnedElements(final List<NamespaceOwnedElement> ownedElements,
                                                          final ModelIndex mapper, final XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeStartElement(PREFIX_UML, XmiElementName.NAMESPACE_DOT_OWNED_ELEMENT.getLocalName(), NAMESPACE_UML);
        try {
            for (final NamespaceOwnedElement ownedElement : ownedElements) {
                if (ownedElement instanceof DataType) {
                    final DataType type = (DataType) ownedElement;
                    writeDataType(type, mapper, xsw);
                } else if (ownedElement instanceof EnumType) {
                    final EnumType type = (EnumType) ownedElement;
                    writeEnumType(type, mapper, xsw);
                } else if (ownedElement instanceof ClassType) {
                    final ClassType classType = (ClassType) ownedElement;
                    writeClassType(classType, mapper, xsw);
                } else if (ownedElement instanceof Generalization) {
                    final Generalization generalization = (Generalization) ownedElement;
                    writeGeneralization(generalization, mapper, xsw);
                } else if (ownedElement instanceof TagDefinition) {
                    final TagDefinition tagDefinition = (TagDefinition) ownedElement;
                    writeTagDefinition(tagDefinition, mapper, xsw);
                } else if (ownedElement instanceof UmlPackage) {
                    final UmlPackage pkg = (UmlPackage) ownedElement;
                    writePackage(pkg, mapper, xsw);
                }
            }
        } finally {
            xsw.writeEndElement();
        }
    }

    private static final void writeModelElementTaggedValues(final ModelElement element, final ModelIndex mapper,
                                                            final XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeStartElement(PREFIX_UML, XmiElementName.MODEL_ELEMENT_DOT_TAGGED_VALUE.getLocalName(), NAMESPACE_UML);
        try {
            for (final TaggedValue taggedValue : element.getTaggedValues()) {
                writeTaggedValue(taggedValue, mapper, xsw);
            }
        } catch (final XMLStreamException e) {
            LOG.warn("writeModelElementTaggedValues(" + element + ")");
        } finally {
            xsw.writeEndElement();
        }
    }

    private static final void writeMultiplicity(final Multiplicity multiplicity, final XMLStreamWriter xsw)
            throws XMLStreamException {

        xsw.writeStartElement(PREFIX_UML, XmiElementName.MULTIPLICITY.getLocalName(), NAMESPACE_UML);
        writeId(multiplicity, xsw);
        writeMultiplicityRange(multiplicity.getRange(), xsw);
        xsw.writeEndElement();
    }

    private static final void writeMultiplicityRange(final Range range, final XMLStreamWriter xsw)
            throws XMLStreamException {

        xsw.writeStartElement(PREFIX_UML, XmiElementName.MULTIPLICITY_DOT_RANGE.getLocalName(), NAMESPACE_UML);
        xsw.writeStartElement(PREFIX_UML, XmiElementName.MULTIPLICITY_RANGE.getLocalName(), NAMESPACE_UML);
        writeId(range, xsw);
        xsw.writeAttribute("lower", Integer.toString(range(range.getLower())));
        xsw.writeAttribute("upper", Integer.toString(range(range.getUpper())));
        xsw.writeEndElement();
        xsw.writeEndElement();
    }

    private static final void writeReference(final Identifier reference, final ModelIndex mapper,
                                             final XMLStreamWriter xsw) throws XMLStreamException {
        if (reference == null) {
            throw new IllegalArgumentException("reference");
        }

        mapper.lookup(reference, new Visitor() {

            @Override
            public void visit(final AssociationEnd associationEnd) {
                throw new AssertionError();
            }

            @Override
            public void visit(final Attribute attribute) {
                throw new AssertionError();
            }

            @Override
            public void visit(final ClassType classType) {
                try {
                    if (classType.isClassType()) {
                        if (classType.isAssociation()) {
                            writeStartElement(XmiElementName.ASSOCIATION_CLASS, xsw);
                        } else {
                            writeStartElement(XmiElementName.CLASS, xsw);
                        }
                    } else {
                        if (classType.isAssociation()) {
                            writeStartElement(XmiElementName.ASSOCIATION, xsw);
                        } else {
                            throw new AssertionError();
                        }
                    }
                } catch (final XMLStreamException e) {
                    throw new XmiRuntimeException(e);
                }
            }

            @Override
            public void visit(final DataType dataType) {
                try {
                    writeStartElement(XmiElementName.DATA_TYPE, xsw);
                } catch (final XMLStreamException e) {
                    throw new XmiRuntimeException(e);
                }
            }

            @Override
            public void visit(final EnumLiteral enumLiteral) {
                throw new AssertionError();
            }

            @Override
            public void visit(final EnumType enumType) {
                try {
                    writeStartElement(XmiElementName.ENUMERATION, xsw);
                } catch (final XMLStreamException e) {
                    throw new XmiRuntimeException(e);
                }
            }

            @Override
            public void visit(final Generalization generalization) {
                throw new AssertionError();
            }

            @Override
            public void visit(final Model model) {
                throw new AssertionError();
            }

            @Override
            public void visit(final Multiplicity multiplicity) {
                throw new AssertionError();
            }

            @Override
            public void visit(final Range range) {
                throw new AssertionError();
            }

            @Override
            public void visit(final TaggedValue taggedValue) {
                throw new AssertionError();
            }

            @Override
            public void beginPackage(final UmlPackage pkg) {
                throw new AssertionError();
            }

            @Override
            public void endPackage(final UmlPackage pkg) {
                throw new AssertionError();
            }

            @Override
            public void visit(final TagDefinition tagDefinition) {
                try {
                    writeStartElement(XmiElementName.TAG_DEFINITION, xsw);
                } catch (final XMLStreamException e) {
                    throw new XmiRuntimeException(e);
                }
            }
        });
        try {
            xsw.writeAttribute(XmiAttributeName.IDREF.getLocalName(), reference.toString());
        } finally {
            xsw.writeEndElement();
        }
    }

    private static final void writePackage(final UmlPackage pkg, final ModelIndex mapper, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(PREFIX_UML, XmiElementName.PACKAGE.getLocalName(), NAMESPACE_UML);
        try {
            writeId(pkg, xsw);
            writeName(pkg, mapper, xsw);
            writeModelElementTaggedValues(pkg, mapper, xsw);
            writeNamespaceOwnedElements(pkg.getOwnedElements(), mapper, xsw);
        } finally {
            xsw.writeEndElement();
        }
    }

    private static final void writeTagDefinition(final TagDefinition tagdef, final ModelIndex mapper,
                                                 final XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeStartElement(PREFIX_UML, XmiElementName.TAG_DEFINITION.getLocalName(), NAMESPACE_UML);
        try {
            writeId(tagdef, xsw);
            writeName(tagdef, mapper, xsw);
            writeModelElementTaggedValues(tagdef, mapper, xsw);
            xsw.writeStartElement(PREFIX_UML, XmiElementName.TAG_DEFINITION_DOT_MULTIPLICITY.getLocalName(),
                    NAMESPACE_UML);
            try {
                writeMultiplicity(tagdef.getMultiplicity(), xsw);
            } finally {
                xsw.writeEndElement();
            }
        } finally {
            xsw.writeEndElement();
        }
    }

    private static final void writeTaggedValue(final TaggedValue taggedValue, final ModelIndex mapper,
                                               final XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeStartElement(PREFIX_UML, XmiElementName.TAGGED_VALUE.getLocalName(), NAMESPACE_UML);
        try {
            writeId(taggedValue, xsw);
            xsw.writeStartElement(PREFIX_UML, XmiElementName.TAGGED_VALUE_DOT_DATA_VALUE.getLocalName(), NAMESPACE_UML);
            try {
                xsw.writeCharacters(taggedValue.getValue());
            } finally {
                xsw.writeEndElement();
            }
            xsw.writeStartElement(PREFIX_UML, XmiElementName.TAGGED_VALUE_DOT_TYPE.getLocalName(), NAMESPACE_UML);
            try {
                writeReference(taggedValue.getTagDefinition(), mapper, xsw);
            } finally {
                xsw.writeEndElement();
            }
        } finally {
            xsw.writeEndElement();
        }
    }

    /**
     * Writes the UML model to the XML stream in XMI format.
     *
     * @param model The UML model.
     * @param xsw   The XML stream.
     * @throws XMLStreamException if anything bad happens.
     */
    private static final void writeXMI(final Model model, final ModelIndex mapper, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement("XMI");
        try {
            xsw.writeNamespace("UML", NAMESPACE_UML);
            xsw.writeAttribute("xmi.version", "1.2");
            writeContent(model, mapper, xsw);
        } finally {
            xsw.writeEndElement();
        }
    }
}
