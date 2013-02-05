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


package org.slc.sli.modeling.docgen;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.slc.sli.modeling.psm.helpers.SliUmlConstants;
import org.slc.sli.modeling.psm.helpers.TagName;
import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.DataType;
import org.slc.sli.modeling.uml.EnumLiteral;
import org.slc.sli.modeling.uml.EnumType;
import org.slc.sli.modeling.uml.Feature;
import org.slc.sli.modeling.uml.Generalization;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.Occurs;
import org.slc.sli.modeling.uml.Range;
import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.uml.Taggable;
import org.slc.sli.modeling.uml.TaggedValue;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.helpers.NamespaceHelper;
import org.slc.sli.modeling.uml.helpers.TaggedValueHelper;
import org.slc.sli.modeling.uml.index.ModelIndex;
import org.slc.sli.modeling.xmi.XmiAttributeName;
import org.slc.sli.modeling.xml.IndentingXMLStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Writes XMI describing domain model.
 */
public final class DocumentationWriter {
    private static final Logger LOG = LoggerFactory.getLogger(DocumentationWriter.class);

    public DocumentationWriter() {
        throw new UnsupportedOperationException();
    }

    private static final void closeQuiet(final Closeable closeable) {
        try {
            closeable.close();
        } catch (final IOException e) {
            LOG.warn(e.getMessage());
        }
    }

    private static boolean isEmbedded(final Feature feature, final ModelIndex model) {
        // To really determine whether the feature is embedded we should look at the
        // aggregation property on the other end of an association end.
        return feature.isAttribute();
    }

    private static boolean isNaturalKey(final Feature feature, final ModelIndex model) {
        for (final TaggedValue taggedValue : feature.getTaggedValues()) {
            final Identifier tagDefinitionId = taggedValue.getTagDefinition();
            final TagDefinition tagDefinition = model.getTagDefinition(tagDefinitionId);
            if (tagDefinition.getName().equals(SliUmlConstants.TAGDEF_NATURAL_KEY)) {
                return true;
            }
        }
        return false;
    }

    private static final void writeFeature(final Feature feature, final ModelIndex modelIndex, final XMLStreamWriter xsw)
            throws XMLStreamException {
        final Type type = modelIndex.getType(feature.getType());
        xsw.writeStartElement(DocumentationElements.FEATURE.getLocalPart());
        try {
            if (isEmbedded(feature, modelIndex)) {
                xsw.writeAttribute(DocumentationAttributes.EMBEDDED, Boolean.toString(true));
            }
            if (isNaturalKey(feature, modelIndex)) {
                xsw.writeAttribute(DocumentationAttributes.NATURAL_KEY, Boolean.toString(true));
            }
            xsw.writeStartElement(DocumentationElements.NAME.getLocalPart());
            try {
                xsw.writeCharacters(feature.getName());
            } finally {
                xsw.writeEndElement();
            }
            writeDescription(feature, modelIndex, xsw);
            final Range range = feature.getMultiplicity().getRange();
            xsw.writeStartElement(DocumentationElements.LOWER.getLocalPart());
            try {
                xsw.writeCharacters(toString(range.getLower()));
            } finally {
                xsw.writeEndElement();
            }
            xsw.writeStartElement(DocumentationElements.UPPER.getLocalPart());
            try {
                xsw.writeCharacters(toString(range.getUpper()));
            } finally {
                xsw.writeEndElement();
            }
            xsw.writeStartElement(DocumentationElements.TYPE.getLocalPart());
            try {
                xsw.writeStartElement(DocumentationElements.NAMESPACE.getLocalPart());
                try {
                    xsw.writeCharacters(modelIndex.getNamespaceURI(type));
                } finally {
                    xsw.writeEndElement();
                }
                xsw.writeStartElement(DocumentationElements.NAME.getLocalPart());
                try {
                    xsw.writeCharacters(type.getName());
                } finally {
                    xsw.writeEndElement();
                }
            } finally {
                xsw.writeEndElement();
            }
        } finally {
            xsw.writeEndElement();
        }
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
            return "*";
        }
        default: {
            throw new AssertionError(value);
        }
        }
    }

    private static final void writeClassType(final ClassType classType, final ModelIndex model,
            final XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeStartElement(DocumentationElements.CLASS.getLocalPart());
        try {
            xsw.writeStartElement(DocumentationElements.NAME.getLocalPart());
            try {
                xsw.writeCharacters(classType.getName());
            } finally {
                xsw.writeEndElement();
            }
            writeDescription(classType, model, xsw);
            writeGeneralizations(classType, model, xsw);
            for (final Attribute attribute : classType.getAttributes()) {
                writeFeature(attribute, model, xsw);
            }
            for (final AssociationEnd associationEnd : model.getAssociationEnds(classType.getId())) {
                if (associationEnd.isNavigable()) {
                    writeFeature(associationEnd, model, xsw);
                }
            }
        } finally {
            xsw.writeEndElement();
        }
    }

    /**
     * Writes the {@link DataType} to the {@link XMLStreamWriter}.
     *
     * @param dataType
     *            The data-type from which we obtain the description and facets.
     * @param name
     *            The qualified name of the data-type, which takes into account the namespace it
     *            belongs to.
     * @param model
     *            The indexed UML model.
     * @param xsw
     *            The {@link XMLStreamWriter}.
     * @throws XMLStreamException
     *             if an exception occurs when writing to the stream.
     */
    private static final void writeDataType(final DataType dataType, final QName name, final ModelIndex model,
            final XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeStartElement(DocumentationElements.DATA_TYPE.getLocalPart());
        try {
            xsw.writeStartElement(DocumentationElements.NAMESPACE.getLocalPart());
            try {
                xsw.writeCharacters(name.getNamespaceURI());
            } finally {
                xsw.writeEndElement();
            }
            xsw.writeStartElement(DocumentationElements.NAME.getLocalPart());
            try {
                xsw.writeCharacters(name.getLocalPart());
            } finally {
                xsw.writeEndElement();
            }
            writeDescription(dataType, model, xsw);
            writeGeneralizations(dataType, model, xsw);
            writeFacets(dataType, model, xsw);
        } finally {
            xsw.writeEndElement();
        }
    }

    private static final void writeGeneralizations(final Type type, final ModelIndex model, final XMLStreamWriter xsw)
            throws XMLStreamException {
        for (final Generalization generalization : model.getGeneralizationBase(type.getId())) {
            final Identifier parentId = generalization.getParent();
            final Type parent = model.getType(parentId);
            xsw.writeStartElement(DocumentationElements.GENERALIZATION.getLocalPart());
            try {
                xsw.writeStartElement(DocumentationElements.NAMESPACE.getLocalPart());
                try {
                    xsw.writeCharacters(NamespaceHelper.getNamespace(parent, model));
                } finally {
                    xsw.writeEndElement();
                }
                xsw.writeStartElement(DocumentationElements.NAME.getLocalPart());
                try {
                    xsw.writeCharacters(parent.getName());
                } finally {
                    xsw.writeEndElement();
                }
            } finally {
                xsw.writeEndElement();
            }
        }
    }

    private static final void writeEnumType(final EnumType enumType, final ModelIndex model, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(DocumentationElements.ENUM_TYPE.getLocalPart());
        try {
            xsw.writeStartElement(DocumentationElements.NAME.getLocalPart());
            try {
                xsw.writeCharacters(enumType.getName());
            } finally {
                xsw.writeEndElement();
            }
            writeDescription(enumType, model, xsw);
            writeGeneralizations(enumType, model, xsw);
            for (final EnumLiteral literal : enumType.getLiterals()) {
                xsw.writeStartElement(DocumentationElements.LITERAL.getLocalPart());
                try {
                    xsw.writeAttribute("value", literal.getName());
                } finally {
                    xsw.writeEndElement();
                }
            }
            writeFacets(enumType, model, xsw);
        } finally {
            xsw.writeEndElement();
        }
    }

    private static final void writeDescription(final Taggable taggable, final ModelIndex model,
            final XMLStreamWriter xsw) throws XMLStreamException {
        final String documentation = TaggedValueHelper.getStringTag(TagName.DOCUMENTATION, taggable, model, null);
        if (documentation != null) {
            writeDescription(documentation, xsw);
        }
    }

    private static final void writeDescription(final String description, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(DocumentationElements.DESCRIPTION.getLocalPart());
        try {
            xsw.writeCharacters(description);
        } finally {
            xsw.writeEndElement();
        }

    }

    private static final void writeFacets(final Taggable type, final ModelIndex model, final XMLStreamWriter xsw)
            throws XMLStreamException {
        for (final TaggedValue taggedValue : type.getTaggedValues()) {
            final String localName = model.getTagDefinition(taggedValue.getTagDefinition()).getName();
            if (!TagName.DOCUMENTATION.equals(localName)) {
                xsw.writeStartElement(localName);
                try {
                    xsw.writeCharacters(taggedValue.getValue());
                } finally {
                    xsw.writeEndElement();
                }
            }
        }
    }

    private static final void writeDiagram(final Diagram diagram, final XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeStartElement(DocumentationElements.DIAGRAM.getLocalPart());
        try {
            xsw.writeStartElement(DocumentationElements.TITLE.getLocalPart());
            try {
                xsw.writeCharacters(diagram.getTitle());
            } finally {
                xsw.writeEndElement();
            }
            xsw.writeStartElement(DocumentationElements.SOURCE.getLocalPart());
            try {
                xsw.writeCharacters(diagram.getSource());
            } finally {
                xsw.writeEndElement();
            }
            xsw.writeStartElement(DocumentationElements.PROLOG.getLocalPart());
            try {
                xsw.writeCharacters(diagram.getProlog());
            } finally {
                xsw.writeEndElement();
            }
            xsw.writeStartElement(DocumentationElements.EPILOG.getLocalPart());
            try {
                xsw.writeCharacters(diagram.getEpilog());
            } finally {
                xsw.writeEndElement();
            }
        } finally {
            xsw.writeEndElement();
        }
    }

    public static final void writeDocument(final Documentation<Type> documentation, final ModelIndex model,
            final OutputStream outstream) {
        if (outstream == null) {
            throw new IllegalArgumentException("outstream");
        }
        final XMLOutputFactory xof = XMLOutputFactory.newInstance();
        try {
            final XMLStreamWriter xsw = new IndentingXMLStreamWriter(xof.createXMLStreamWriter(outstream, "UTF-8"));
            xsw.writeStartDocument("UTF-8", "1.0");
            try {
                writeRoot(documentation, model, xsw);
            } finally {
                xsw.writeEndDocument();
            }
            xsw.flush();
            xsw.close();
        } catch (final XMLStreamException e) {
            throw new DocumentGeneratorRuntimeException(e);
        }
    }

    public static final void writeDocument(final Documentation<Type> documentation, final ModelIndex model,
            final String fileName) {
        if (fileName == null) {
            throw new IllegalArgumentException("fileName");
        }
        try {
            final OutputStream outstream = new BufferedOutputStream(new FileOutputStream(fileName));
            try {
                writeDocument(documentation, model, outstream);
            } finally {
                closeQuiet(outstream);
            }
        } catch (final FileNotFoundException e) {
            LOG.warn(e.getMessage());
        }
    }

    public static final void writeDocument(final Documentation<Type> documentation, final ModelIndex model,
            final File file) {
        if (file == null) {
            throw new IllegalArgumentException("file");
        }
        try {
            final OutputStream outstream = new BufferedOutputStream(new FileOutputStream(file));
            try {
                writeDocument(documentation, model, outstream);
            } finally {
                closeQuiet(outstream);
            }
        } catch (final FileNotFoundException e) {
            LOG.warn(e.getMessage());
        }
    }

    private static final void writeDomain(final Domain<Type> domain, final ModelIndex model, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(DocumentationElements.DOMAIN.getLocalPart());
        try {
            xsw.writeStartElement(DocumentationElements.TITLE.getLocalPart());
            try {
                xsw.writeCharacters(domain.getTitle());
            } finally {
                xsw.writeEndElement();
            }
            xsw.writeStartElement(DocumentationElements.DESCRIPTION.getLocalPart());
            try {
                xsw.writeCharacters(domain.getDescription());
            } finally {
                xsw.writeEndElement();
            }
            for (final Diagram diagram : domain.getDiagrams()) {
                writeDiagram(diagram, xsw);
            }
            for (final Entity<Type> entity : domain.getEntities()) {
                writeEntity(entity, model, xsw);
            }
        } finally {
            xsw.writeEndElement();
        }
    }

    private static final void writeEntity(final Entity<Type> entity, final ModelIndex model, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(DocumentationElements.ENTITY.getLocalPart());
        try {
            xsw.writeStartElement(DocumentationElements.TITLE.getLocalPart());
            try {
                xsw.writeCharacters(entity.getTitle());
            } finally {
                xsw.writeEndElement();
            }
            final Type type = entity.getType();
            xsw.writeStartElement(DocumentationElements.CLASS.getLocalPart());
            try {
                final Identifier id = type.getId();
                xsw.writeAttribute(XmiAttributeName.IDREF.getLocalName(), id.toString());
            } finally {
                xsw.writeEndElement();
            }
            xsw.writeStartElement(DocumentationElements.NAME.getLocalPart());
            try {
                xsw.writeCharacters(type.getName());
            } finally {
                xsw.writeEndElement();
            }
            writeDescription(type, model, xsw);
        } finally {
            xsw.writeEndElement();
        }
    }

    private static final void writeRoot(final Documentation<Type> documentation, final ModelIndex modelIndex,
            final XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeStartElement(DocumentationElements.DOMAINS.getLocalPart());
        try {
            for (final Domain<Type> domain : documentation.getDomains()) {
                writeDomain(domain, modelIndex, xsw);
            }
            for (final ClassType classType : modelIndex.getClassTypes().values()) {
                writeClassType(classType, modelIndex, xsw);
            }
            for (final EnumType enumType : modelIndex.getEnumTypes()) {
                writeEnumType(enumType, modelIndex, xsw);
            }
            final Map<QName, DataType> dataTypes = modelIndex.getDataTypes();
            for (Map.Entry<QName, DataType> entry : dataTypes.entrySet()) {
//            for (final QName name : dataTypes.keySet()) {
                final DataType dataType = dataTypes.get(entry.getKey());
                writeDataType(entry.getValue(), entry.getKey(), modelIndex, xsw);
            }
        } finally {
            xsw.writeEndElement();
        }
    }
}
