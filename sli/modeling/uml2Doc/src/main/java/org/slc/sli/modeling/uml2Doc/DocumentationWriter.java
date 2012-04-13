package org.slc.sli.modeling.uml2Doc;

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
import org.slc.sli.modeling.uml.HasTaggedValues;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.Model;
import org.slc.sli.modeling.uml.Occurs;
import org.slc.sli.modeling.uml.Range;
import org.slc.sli.modeling.uml.TaggedValue;
import org.slc.sli.modeling.uml.Type;
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
    
    private static final void writeAttribute(final Attribute attribute, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(DocumentationElements.ATTRIBUTE.getLocalPart());
        try {
            xsw.writeStartElement(DocumentationElements.NAME.getLocalPart());
            try {
                xsw.writeCharacters(attribute.getName().getLocalPart());
            } finally {
                xsw.writeEndElement();
            }
            writeDescription(attribute, xsw);
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
                    xsw.writeCharacters(attribute.getType().getName().getLocalPart());
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
    
    private static final void writeClassType(final ClassType classType, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(DocumentationElements.CLASS.getLocalPart());
        try {
            xsw.writeStartElement(DocumentationElements.NAME.getLocalPart());
            try {
                xsw.writeCharacters(classType.getName().getLocalPart());
            } finally {
                xsw.writeEndElement();
            }
            writeDescription(classType, xsw);
            for (final Attribute attribute : classType.getAttributes()) {
                writeAttribute(attribute, xsw);
            }
        } finally {
            xsw.writeEndElement();
        }
    }
    
    private static final void writeDataType(final DataType dataType, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(DocumentationElements.DATA_TYPE.getLocalPart());
        try {
            xsw.writeStartElement(DocumentationElements.NAME.getLocalPart());
            try {
                xsw.writeCharacters(dataType.getName().getLocalPart());
            } finally {
                xsw.writeEndElement();
            }
            writeDescription(dataType, xsw);
            writeFacets(dataType, xsw);
        } finally {
            xsw.writeEndElement();
        }
    }
    
    private static final void writeEnumType(final EnumType enumType, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(DocumentationElements.ENUM_TYPE.getLocalPart());
        try {
            xsw.writeStartElement(DocumentationElements.NAME.getLocalPart());
            try {
                xsw.writeCharacters(enumType.getName().getLocalPart());
            } finally {
                xsw.writeEndElement();
            }
            writeDescription(enumType, xsw);
            for (final EnumLiteral literal : enumType.getLiterals()) {
                xsw.writeStartElement(DocumentationElements.LITERAL.getLocalPart());
                try {
                    xsw.writeAttribute("value", literal.getName().getLocalPart());
                } finally {
                    xsw.writeEndElement();
                }
            }
            writeFacets(enumType, xsw);
        } finally {
            xsw.writeEndElement();
        }
    }
    
    private static final void writeDescription(final HasTaggedValues type, final XMLStreamWriter xsw)
            throws XMLStreamException {
        for (final TaggedValue taggedValue : type.getTaggedValues()) {
            final String localName = taggedValue.getTagDefinition().getName().getLocalPart();
            if ("documentation".equals(localName)) {
                xsw.writeStartElement(DocumentationElements.DESCRIPTION.getLocalPart());
                try {
                    xsw.writeCharacters(taggedValue.getValue());
                } finally {
                    xsw.writeEndElement();
                }
            }
        }
    }
    
    private static final void writeFacets(final HasTaggedValues type, final XMLStreamWriter xsw)
            throws XMLStreamException {
        for (final TaggedValue taggedValue : type.getTaggedValues()) {
            final String localName = taggedValue.getTagDefinition().getName().getLocalPart();
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
            xsw.writeStartElement(DocumentationElements.DESCRIPTION.getLocalPart());
            try {
                xsw.writeCharacters(diagram.getDescription());
            } finally {
                xsw.writeEndElement();
            }
        } finally {
            xsw.writeEndElement();
        }
    }
    
    public static final void writeDocument(final Documentation<Type> documentation, final Model model,
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
    
    public static final void writeDocument(final Documentation<Type> documentation, final Model model,
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
    
    private static final void writeDomain(final Domain<Type> domain, final XMLStreamWriter xsw)
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
                writeEntity(entity, xsw);
            }
        } finally {
            xsw.writeEndElement();
        }
    }
    
    private static final void writeEntity(final Entity<Type> entity, final XMLStreamWriter xsw)
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
                xsw.writeCharacters(type.getName().getLocalPart());
            } finally {
                xsw.writeEndElement();
            }
            writeDescription(type, xsw);
        } finally {
            xsw.writeEndElement();
        }
    }
    
    private static final void writeRoot(final Documentation<Type> documentation, final Model model,
            final XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeStartElement(DocumentationElements.DOMAINS.getLocalPart());
        try {
            for (final Domain<Type> domain : documentation.getDomains()) {
                writeDomain(domain, xsw);
            }
            for (final ClassType classType : model.getClassTypeMap().values()) {
                writeClassType(classType, xsw);
            }
            for (final EnumType enumType : model.getEnumTypeMap().values()) {
                writeEnumType(enumType, xsw);
            }
            for (final DataType dataType : model.getDataTypeMap().values()) {
                writeDataType(dataType, xsw);
            }
        } finally {
            xsw.writeEndElement();
        }
    }
}
