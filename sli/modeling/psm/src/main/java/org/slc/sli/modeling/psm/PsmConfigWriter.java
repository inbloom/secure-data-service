package org.slc.sli.modeling.psm;

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
import org.slc.sli.modeling.uml.Taggable;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.Model;
import org.slc.sli.modeling.uml.Occurs;
import org.slc.sli.modeling.uml.Range;
import org.slc.sli.modeling.uml.TaggedValue;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.index.Mapper;
import org.slc.sli.modeling.xmi.XmiAttributeName;
import org.slc.sli.modeling.xml.IndentingXMLStreamWriter;

public final class PsmConfigWriter {
    
    private static final void closeQuiet(final Closeable closeable) {
        try {
            closeable.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
    
    private static final void writeAttribute(final Attribute attribute, final Mapper model, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(PsmConfigElements.ATTRIBUTE.getLocalPart());
        try {
            xsw.writeStartElement(PsmConfigElements.NAME.getLocalPart());
            try {
                xsw.writeCharacters(attribute.getName());
            } finally {
                xsw.writeEndElement();
            }
            writeDescription(attribute, model, xsw);
            final Range range = attribute.getMultiplicity().getRange();
            xsw.writeStartElement(PsmConfigElements.LOWER.getLocalPart());
            try {
                xsw.writeCharacters(toString(range.getLower()));
            } finally {
                xsw.writeEndElement();
            }
            xsw.writeStartElement(PsmConfigElements.UPPER.getLocalPart());
            try {
                xsw.writeCharacters(toString(range.getUpper()));
            } finally {
                xsw.writeEndElement();
            }
            xsw.writeStartElement(PsmConfigElements.TYPE.getLocalPart());
            try {
                xsw.writeStartElement(PsmConfigElements.NAME.getLocalPart());
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
    
    @SuppressWarnings("unused")
    private static final void writePimClassType(final ClassType classType, final Mapper model, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(PsmConfigElements.PIM_CLASS_REF.getLocalPart());
        try {
            xsw.writeStartElement(PsmConfigElements.NAME.getLocalPart());
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
    
    @SuppressWarnings("unused")
    private static final void writePimDataType(final DataType dataType, final Mapper model, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(PsmConfigElements.DATA_TYPE.getLocalPart());
        try {
            xsw.writeStartElement(PsmConfigElements.NAME.getLocalPart());
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
    
    @SuppressWarnings("unused")
    private static final void writePimEnumType(final EnumType enumType, final Mapper model, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(PsmConfigElements.ENUM_TYPE.getLocalPart());
        try {
            xsw.writeStartElement(PsmConfigElements.NAME.getLocalPart());
            try {
                xsw.writeCharacters(enumType.getName());
            } finally {
                xsw.writeEndElement();
            }
            writeDescription(enumType, model, xsw);
            for (final EnumLiteral literal : enumType.getLiterals()) {
                xsw.writeStartElement(PsmConfigElements.LITERAL.getLocalPart());
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
                xsw.writeStartElement(PsmConfigElements.DESCRIPTION.getLocalPart());
                try {
                    xsw.writeCharacters(taggedValue.getValue());
                } finally {
                    xsw.writeEndElement();
                }
            }
        }
    }
    
    private static final void writeCollection(final PsmCollection collection, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(PsmConfigElements.COLLECTION.getLocalPart());
        try {
            xsw.writeCharacters(collection.getName());
        } finally {
            xsw.writeEndElement();
        }
    }
    
    private static final void writeResource(final PsmResource resource, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(PsmConfigElements.RESOURCE.getLocalPart());
        try {
            xsw.writeCharacters(resource.getName());
        } finally {
            xsw.writeEndElement();
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
    
    public static final void writeConfig(final PsmConfig<Type> documentation, final Model model,
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
    
    public static final void writeConfig(final PsmConfig<Type> documentation, final Model model, final String fileName) {
        try {
            final OutputStream outstream = new BufferedOutputStream(new FileOutputStream(fileName));
            try {
                writeConfig(documentation, model, outstream);
            } finally {
                closeQuiet(outstream);
            }
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    private static final void writeClassType(final PsmClassType<Type> classType, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(PsmConfigElements.PSM_CLASS_TYPE.getLocalPart());
        try {
            final Type type = classType.getType();
            xsw.writeStartElement(PsmConfigElements.PIM_CLASS_REF.getLocalPart());
            try {
                final Identifier id = type.getId();
                xsw.writeAttribute(XmiAttributeName.IDREF.getLocalName(), id.toString());
            } finally {
                xsw.writeEndElement();
            }
            writeResource(classType.getResource(), xsw);
            writeCollection(classType.getCollection(), xsw);
        } finally {
            xsw.writeEndElement();
        }
    }
    
    private static final void writeRoot(final PsmConfig<Type> documentation, final Model model,
            final XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeStartElement(PsmConfigElements.PSM_CFG.getLocalPart());
        try {
            for (final PsmClassType<Type> domain : documentation.getClassTypes()) {
                writeClassType(domain, xsw);
            }
        } finally {
            xsw.writeEndElement();
        }
    }
}
