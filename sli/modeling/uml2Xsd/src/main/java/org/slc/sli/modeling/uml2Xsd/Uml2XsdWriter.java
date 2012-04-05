package org.slc.sli.modeling.uml2Xsd;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.slc.sli.modeling.uml.AbstractModelElement;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.DataType;
import org.slc.sli.modeling.uml.EnumLiteral;
import org.slc.sli.modeling.uml.EnumType;
import org.slc.sli.modeling.uml.HasName;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.Model;
import org.slc.sli.modeling.uml.Multiplicity;
import org.slc.sli.modeling.uml.Occurs;
import org.slc.sli.modeling.uml.Range;
import org.slc.sli.modeling.uml.TaggedValue;

/**
 * Writes a UML {@link Model} to a file (by name) or {@link OutputStream}.
 */
public final class Uml2XsdWriter {
    
    private static final String NAMESPACE_XS = "http://www.w3.org/2001/XMLSchema";
    private static final String PREFIX_XS = "xs";
    
    private static final void annotation(final AbstractModelElement element, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(PREFIX_XS, "annotation", NAMESPACE_XS);
        try {
            for (final TaggedValue taggedValue : element.getTaggedValues()) {
                documentation(taggedValue, xsw);
            }
        } finally {
            xsw.writeEndElement();
        }
    }
    
    private static final void base(final String value, final XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeAttribute(XsdAttributeName.BASE.getLocalName(), value);
    }
    
    private static final void closeQuiet(final Closeable closeable) {
        try {
            closeable.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
    
    private static final void complexType(final ClassType complexType, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(PREFIX_XS, "complexType", NAMESPACE_XS);
        try {
            xsw.writeAttribute("name", complexType.getName());
            xsw.writeStartElement(PREFIX_XS, "sequence", NAMESPACE_XS);
            try {
                for (final Attribute attribute : complexType.getAttributes()) {
                    element(attribute, xsw);
                }
            } finally {
                xsw.writeEndElement();
            }
        } finally {
            xsw.writeEndElement();
        }
    }
    
    private static final void element(final Attribute element, final XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeStartElement(PREFIX_XS, "element", NAMESPACE_XS);
        try {
            name(element, xsw);
            type(element, xsw);
            occurrences(element.getMultiplicity(), xsw);
            annotation(element, xsw);
        } finally {
            xsw.writeEndElement();
        }
    }
    
    private static final void name(final HasName hasIdentity, final XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeAttribute(XsdAttributeName.NAME.getLocalName(), hasIdentity.getName());
    }
    
    private static final void type(final Attribute element, final XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeAttribute(XsdAttributeName.TYPE.getLocalName(), element.getType().getName());
    }
    
    private static final void occurrences(final Multiplicity multiplicity, final XMLStreamWriter xsw)
            throws XMLStreamException {
        final Range range = multiplicity.getRange();
        if (!range.getLower().equals(Occurs.ONE)) {
            xsw.writeAttribute("minOccurs", toString(range.getLower()));
        }
        if (!range.getUpper().equals(Occurs.ONE)) {
            xsw.writeAttribute("maxOccurs", toString(range.getUpper()));
        }
    }
    
    private static final void startElement(final XsdElementName name, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(PREFIX_XS, name.getLocalName(), NAMESPACE_XS);
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
                return "unbounded";
            }
            default: {
                throw new AssertionError(value);
            }
        }
    }
    
    private static final void value(final String value, final XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeAttribute(XsdAttributeName.VALUE.getLocalName(), value);
    }
    
    public static final void writeDocument(final Model model, final OutputStream outstream) {
        final XMLOutputFactory xof = XMLOutputFactory.newInstance();
        try {
            final XMLStreamWriter xsw = xof.createXMLStreamWriter(outstream, "UTF-8");
            xsw.writeStartDocument("UTF-8", "1.0");
            try {
                schema(model, xsw);
            } finally {
                xsw.writeEndDocument();
            }
            xsw.flush();
            xsw.close();
        } catch (final XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static final void writeDocument(final Model model, final String fileName) {
        try {
            final OutputStream outstream = new BufferedOutputStream(new FileOutputStream(fileName));
            try {
                writeDocument(model, outstream);
            } finally {
                closeQuiet(outstream);
            }
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    private static final void documentation(final TaggedValue taggedValue, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(PREFIX_XS, "documentation", NAMESPACE_XS);
        try {
            xsw.writeCharacters(taggedValue.getValue());
        } finally {
            xsw.writeEndElement();
        }
    }
    
    /**
     * Writes the UML model to the XML stream in XMI format.
     * 
     * @param model
     *            The UML model.
     * @param xsw
     *            The XML stream.
     * @throws XMLStreamException
     *             if anything bad happens.
     */
    private static final void schema(final Model model, final XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeStartElement(PREFIX_XS, "schema", NAMESPACE_XS);
        try {
            xsw.writeNamespace("xs", NAMESPACE_XS);
            final Map<Identifier, DataType> dataTypeMap = model.getDataTypeMap();
            for (final Identifier key : dataTypeMap.keySet()) {
                final DataType type = dataTypeMap.get(key);
                simpleType(type, xsw);
            }
            final Map<Identifier, EnumType> enumTypeMap = model.getEnumTypeMap();
            for (final Identifier key : enumTypeMap.keySet()) {
                final EnumType type = enumTypeMap.get(key);
                simpleType(type, xsw);
            }
            final Map<Identifier, ClassType> classTypeMap = model.getClassTypeMap();
            for (final Identifier key : classTypeMap.keySet()) {
                final ClassType type = classTypeMap.get(key);
                complexType(type, xsw);
            }
        } finally {
            xsw.writeEndElement();
        }
    }
    
    private static final void simpleType(final DataType simpleType, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(PREFIX_XS, "simpleType", NAMESPACE_XS);
        try {
            xsw.writeAttribute("name", simpleType.getName());
            annotation(simpleType, xsw);
            startElement(XsdElementName.RESTRICTION, xsw);
            try {
                // TODO: This needs to be injected?
                base("xs:string", xsw);
            } finally {
                xsw.writeEndElement();
            }
        } finally {
            xsw.writeEndElement();
        }
    }
    
    private static final void simpleType(final EnumType enumType, final XMLStreamWriter xsw) throws XMLStreamException {
        startElement(XsdElementName.SIMPLE_TYPE, xsw);
        try {
            name(enumType, xsw);
            annotation(enumType, xsw);
            startElement(XsdElementName.RESTRICTION, xsw);
            try {
                // TODO: This needs to be injected?
                base("xs:token", xsw);
                for (final EnumLiteral literal : enumType.getLiterals()) {
                    startElement(XsdElementName.ENUMERATION, xsw);
                    value(literal.getName(), xsw);
                    xsw.writeEndElement();
                }
            } finally {
                xsw.writeEndElement();
            }
        } finally {
            xsw.writeEndElement();
        }
    }
}
