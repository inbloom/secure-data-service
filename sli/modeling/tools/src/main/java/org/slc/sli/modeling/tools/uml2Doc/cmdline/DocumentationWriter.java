package org.slc.sli.modeling.tools.uml2Doc.cmdline;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.DataType;
import org.slc.sli.modeling.uml.EnumLiteral;
import org.slc.sli.modeling.uml.EnumType;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.Occurs;
import org.slc.sli.modeling.uml.Range;
import org.slc.sli.modeling.uml.Taggable;
import org.slc.sli.modeling.uml.TaggedValue;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.index.Mapper;
import org.slc.sli.modeling.xmi.XmiAttributeName;
import org.slc.sli.modeling.xml.IndentingXMLStreamWriter;

public final class DocumentationWriter {

    private static final void closeQuiet(final Closeable closeable) {
        try {
            closeable.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private static final void writeAttribute(final Attribute attribute, final Mapper model, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(DocumentationElements.ATTRIBUTE.getLocalPart());
        try {
            xsw.writeStartElement(DocumentationElements.NAME.getLocalPart());
            try {
                xsw.writeCharacters(attribute.getName());
            } finally {
                xsw.writeEndElement();
            }
            writeDescription(attribute, model, xsw);
            final Range range = attribute.getMultiplicity().getRange();
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
                xsw.writeStartElement(DocumentationElements.NAME.getLocalPart());
                try {
                    xsw.writeCharacters(model.getType(attribute.getType()).getName());
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
            throw new NullPointerException("value");
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

    private static final void writeClassType(final ClassType classType, final Mapper model, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(DocumentationElements.CLASS.getLocalPart());
        try {
            xsw.writeStartElement(DocumentationElements.NAME.getLocalPart());
            try {
                xsw.writeCharacters(classType.getName());
            } finally {
                xsw.writeEndElement();
            }
            writeDescription(classType, model, xsw);
            for (final Attribute attribute : classType.getAttributes()) {
                writeAttribute(attribute, model, xsw);
            }
        } finally {
            xsw.writeEndElement();
        }
    }

    private static final void writeDataType(final DataType dataType, final Mapper model, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(DocumentationElements.DATA_TYPE.getLocalPart());
        try {
            xsw.writeStartElement(DocumentationElements.NAME.getLocalPart());
            try {
                xsw.writeCharacters(dataType.getName());
            } finally {
                xsw.writeEndElement();
            }
            writeDescription(dataType, model, xsw);
            writeFacets(dataType, model, xsw);
        } finally {
            xsw.writeEndElement();
        }
    }

    private static final void writeEnumType(final EnumType enumType, final Mapper model, final XMLStreamWriter xsw)
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

    private static final void writeDescription(final Taggable type, final Mapper model, final XMLStreamWriter xsw)
            throws XMLStreamException {
        for (final TaggedValue taggedValue : type.getTaggedValues()) {
            final String name = model.getTagDefinition(taggedValue.getTagDefinition()).getName();
            if ("documentation".equals(name)) {
                xsw.writeStartElement(DocumentationElements.DESCRIPTION.getLocalPart());
                try {
                    xsw.writeCharacters(taggedValue.getValue());
                } finally {
                    xsw.writeEndElement();
                }
            }
        }
    }

    private static final void writeFacets(final Taggable type, final Mapper model, final XMLStreamWriter xsw)
            throws XMLStreamException {
        for (final TaggedValue taggedValue : type.getTaggedValues()) {
            final String localName = model.getTagDefinition(taggedValue.getTagDefinition()).getName();
            if (!"documentation".equals(localName)) {
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

    public static final void writeDocument(final Documentation<Type> documentation, final Mapper model,
            final OutputStream outstream) {
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
            throw new RuntimeException(e);
        }
    }

    public static final void writeDocument(final Documentation<Type> documentation, final Mapper model,
            final String fileName) {
        try {
            final OutputStream outstream = new BufferedOutputStream(new FileOutputStream(fileName));
            try {
                writeDocument(documentation, model, outstream);
            } finally {
                closeQuiet(outstream);
            }
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static final void writeDomain(final Domain<Type> domain, final Mapper model, final XMLStreamWriter xsw)
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

    private static final void writeEntity(final Entity<Type> entity, final Mapper model, final XMLStreamWriter xsw)
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

    private static final void writeRoot(final Documentation<Type> documentation, final Mapper model,
            final XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeStartElement(DocumentationElements.DOMAINS.getLocalPart());
        try {
            for (final Domain<Type> domain : documentation.getDomains()) {
                writeDomain(domain, model, xsw);
            }
            for (final ClassType classType : model.getClassTypes()) {
                writeClassType(classType, model, xsw);
            }
            for (final EnumType enumType : model.getEnumTypes()) {
                writeEnumType(enumType, model, xsw);
            }
            for (final DataType dataType : model.getDataTypes()) {
                writeDataType(dataType, model, xsw);
            }
        } finally {
            xsw.writeEndElement();
        }
    }
}
