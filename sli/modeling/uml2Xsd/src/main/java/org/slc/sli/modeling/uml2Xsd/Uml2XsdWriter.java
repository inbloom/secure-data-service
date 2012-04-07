package org.slc.sli.modeling.uml2Xsd;

import static org.slc.sli.modeling.xml.XmlTools.collapseWhitespace;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.slc.sli.modeling.uml.AbstractModelElement;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.DataType;
import org.slc.sli.modeling.uml.EnumLiteral;
import org.slc.sli.modeling.uml.EnumType;
import org.slc.sli.modeling.uml.Generalization;
import org.slc.sli.modeling.uml.HasName;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.Model;
import org.slc.sli.modeling.uml.Multiplicity;
import org.slc.sli.modeling.uml.Occurs;
import org.slc.sli.modeling.uml.Range;
import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.uml.TaggedValue;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.xml.IndentingXMLStreamWriter;
import org.slc.sli.modeling.xsd.XsdAttributeName;
import org.slc.sli.modeling.xsd.XsdElementName;
import org.slc.sli.modeling.xsd.XsdNamespace;

/**
 * Writes a UML {@link Model} to a file (by name) or {@link OutputStream}.
 */
public final class Uml2XsdWriter {
    
    private static final String NAMESPACE_XS = XsdNamespace.URI;
    private static final String PREFIX_XS = "xs";
    /**
     * xs:string is the default type that we use for a data type that has no generalizations.
     */
    private static final QName STRING = new QName(NAMESPACE_XS, "string");
    /**
     * xs:token is the default type that we use for a data type that has no generalizations.
     */
    private static final QName TOKEN = new QName(NAMESPACE_XS, "token");
    
    private static final void attributeFormDefault(final boolean qualified, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeAttribute(XsdAttributeName.ATTRIBUTE_FORM_DEFAULT.getLocalName(), qualified ? "qualified"
                : "unqualified");
    }
    
    private static final void base(final QName name, final XMLStreamWriter xsw) throws XMLStreamException {
        if (name.getNamespaceURI().equals(NAMESPACE_XS)) {
            xsw.writeAttribute(XsdAttributeName.BASE.getLocalName(), PREFIX_XS.concat(":").concat(name.getLocalPart()));
        } else {
            xsw.writeAttribute(XsdAttributeName.BASE.getLocalName(), name.getLocalPart());
        }
    }
    
    private static final void base(final Type type, final XMLStreamWriter xsw) throws XMLStreamException {
        base(type.getName(), xsw);
    }
    
    private static final void closeQuiet(final Closeable closeable) {
        try {
            closeable.close();
        } catch (final IOException e) {
            // Ignore.
        }
    }
    
    private static final void complexType(final ClassType complexType, final XMLStreamWriter xsw)
            throws XMLStreamException {
        writeStartElement(XsdElementName.COMPLEX_TYPE, xsw);
        try {
            name(complexType, xsw);
            final Type base = getBase(complexType);
            if (base == null) {
                writeSequence(complexType, xsw);
            } else {
                writeStartElement(XsdElementName.COMPLEX_CONTENT, xsw);
                try {
                    writeStartElement(XsdElementName.EXTENSION, xsw);
                    base(base, xsw);
                    try {
                        writeSequence(complexType, xsw);
                    } finally {
                        writeEndElement(xsw);
                    }
                } finally {
                    writeEndElement(xsw);
                }
            }
        } finally {
            writeEndElement(xsw);
        }
    }
    
    private static final void elementFormDefault(final boolean qualified, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeAttribute(XsdAttributeName.ELEMENT_FORM_DEFAULT.getLocalName(), qualified ? "qualified"
                : "unqualified");
    }
    
    private static final Type getBase(final Type type) {
        final List<Generalization> generalizationBase = type.getGeneralizationBase();
        if (generalizationBase.isEmpty()) {
            return null;
        } else {
            if (generalizationBase.size() == 1) {
                return generalizationBase.get(0).getParent();
            } else {
                throw new AssertionError(type.getName());
            }
        }
    }
    
    private static final boolean isW3cXmlSchemaType(final QName name) {
        final String namespace = name.getNamespaceURI();
        return namespace.equals(NAMESPACE_XS);
    }
    
    private static final String lexicalName(final QName name) {
        if (isW3cXmlSchemaType(name)) {
            return PREFIX_XS.concat(":").concat(name.getLocalPart());
        } else {
            return name.getLocalPart();
        }
    }
    
    private static final void maxOccurs(final Occurs value, final XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeAttribute(XsdAttributeName.MAX_OCCURS.getLocalName(), toString(value));
    }
    
    private static final void minOccurs(final Occurs value, final XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeAttribute(XsdAttributeName.MIN_OCCURS.getLocalName(), toString(value));
    }
    
    private static final void name(final HasName hasName, final XMLStreamWriter xsw) throws XMLStreamException {
        final QName name = hasName.getName();
        xsw.writeAttribute(XsdAttributeName.NAME.getLocalName(), name.toString());
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
            xsw.writeNamespace(PREFIX_XS, NAMESPACE_XS);
            attributeFormDefault(false, xsw);
            elementFormDefault(true, xsw);
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
        if (!isW3cXmlSchemaType(simpleType.getName())) {
            writeStartElement(XsdElementName.SIMPLE_TYPE, xsw);
            try {
                name(simpleType, xsw);
                writeAnnotation(simpleType, xsw);
                writeStartElement(XsdElementName.RESTRICTION, xsw);
                try {
                    final Type base = getBase(simpleType);
                    if (base != null) {
                        base(base, xsw);
                    } else {
                        base(STRING, xsw);
                    }
                    for (final TaggedValue taggedValue : simpleType.getTaggedValues()) {
                        final TagDefinition tagDefinition = taggedValue.getTagDefinition();
                        final QName name = tagDefinition.getName();
                        if (TagDefinition.NAME_MAX_LENGTH.equals(name)) {
                            writeFacet(XsdElementName.MAX_LENGTH, taggedValue, xsw);
                        } else if (TagDefinition.NAME_MIN_LENGTH.equals(name)) {
                            writeFacet(XsdElementName.MIN_LENGTH, taggedValue, xsw);
                        } else if (TagDefinition.NAME_MAX_INCLUSIVE.equals(name)) {
                            writeFacet(XsdElementName.MAX_INCLUSIVE, taggedValue, xsw);
                        } else if (TagDefinition.NAME_MIN_INCLUSIVE.equals(name)) {
                            writeFacet(XsdElementName.MIN_INCLUSIVE, taggedValue, xsw);
                        } else if (TagDefinition.NAME_MAX_EXCLUSIVE.equals(name)) {
                            writeFacet(XsdElementName.MAX_EXCLUSIVE, taggedValue, xsw);
                        } else if (TagDefinition.NAME_MIN_EXCLUSIVE.equals(name)) {
                            writeFacet(XsdElementName.MIN_EXCLUSIVE, taggedValue, xsw);
                        } else if (TagDefinition.NAME_TOTAL_DIGITS.equals(name)) {
                            writeFacet(XsdElementName.TOTAL_DIGITS, taggedValue, xsw);
                        } else if (TagDefinition.NAME_FRACTION_DIGITS.equals(name)) {
                            writeFacet(XsdElementName.FRACTION_DIGITS, taggedValue, xsw);
                        } else if (TagDefinition.NAME_LENGTH.equals(name)) {
                            writeFacet(XsdElementName.LENGTH, taggedValue, xsw);
                        } else if (TagDefinition.NAME_PATTERN.equals(name)) {
                            writeFacet(XsdElementName.PATTERN, taggedValue, xsw);
                        }
                    }
                } finally {
                    writeEndElement(xsw);
                }
            } finally {
                writeEndElement(xsw);
            }
        }
    }
    
    private static final void simpleType(final EnumType simpleType, final XMLStreamWriter xsw)
            throws XMLStreamException {
        writeStartElement(XsdElementName.SIMPLE_TYPE, xsw);
        try {
            name(simpleType, xsw);
            writeAnnotation(simpleType, xsw);
            writeStartElement(XsdElementName.RESTRICTION, xsw);
            try {
                final Type base = getBase(simpleType);
                if (base != null) {
                    base(base, xsw);
                } else {
                    base(TOKEN, xsw);
                }
                for (final EnumLiteral literal : simpleType.getLiterals()) {
                    writeStartElement(XsdElementName.ENUMERATION, xsw);
                    // FIXME: Enumerations should have values, not names!
                    value(collapseWhitespace(literal.getName().getLocalPart()), xsw);
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
                return "unbounded";
            }
            default: {
                throw new AssertionError(value);
            }
        }
    }
    
    private static final void type(final Attribute element, final XMLStreamWriter xsw) throws XMLStreamException {
        final QName name = element.getType().getName();
        xsw.writeAttribute(XsdAttributeName.TYPE.getLocalName(), lexicalName(name));
    }
    
    private static final void value(final String value, final XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeAttribute(XsdAttributeName.VALUE.getLocalName(), value);
    }
    
    private static final void writeAnnotation(final AbstractModelElement element, final XMLStreamWriter xsw)
            throws XMLStreamException {
        writeStartElement(XsdElementName.ANNOTATION, xsw);
        try {
            for (final TaggedValue taggedValue : element.getTaggedValues()) {
                final TagDefinition tagDefinition = taggedValue.getTagDefinition();
                final QName name = tagDefinition.getName();
                if (TagDefinition.NAME_DOCUMENTATION.equals(name)) {
                    writeDocumentation(taggedValue, xsw);
                }
            }
        } finally {
            writeEndElement(xsw);
        }
    }
    
    public static final void writeDocument(final Model model, final OutputStream outstream) {
        final XMLOutputFactory xof = XMLOutputFactory.newInstance();
        try {
            final XMLStreamWriter xsw = new IndentingXMLStreamWriter(xof.createXMLStreamWriter(outstream, "UTF-8"));
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
    
    private static final void writeDocumentation(final TaggedValue taggedValue, final XMLStreamWriter xsw)
            throws XMLStreamException {
        writeStartElement(XsdElementName.DOCUMENTATION, xsw);
        try {
            xsw.writeCharacters(collapseWhitespace(taggedValue.getValue()));
        } finally {
            writeEndElement(xsw);
        }
    }
    
    private static final void writeFacet(final XsdElementName name, TaggedValue taggedValue, final XMLStreamWriter xsw)
            throws XMLStreamException {
        writeStartElement(name, xsw);
        try {
            value(collapseWhitespace(taggedValue.getValue()), xsw);
        } finally {
            writeEndElement(xsw);
        }
    }
    
    private static final void writeElement(final Attribute element, final XMLStreamWriter xsw)
            throws XMLStreamException {
        writeStartElement(XsdElementName.ELEMENT, xsw);
        try {
            name(element, xsw);
            type(element, xsw);
            occurrences(element.getMultiplicity(), xsw);
            writeAnnotation(element, xsw);
        } finally {
            writeEndElement(xsw);
        }
    }
    
    private static final void writeEndElement(final XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeEndElement();
    }
    
    private static final void writeSequence(final ClassType complexType, final XMLStreamWriter xsw)
            throws XMLStreamException {
        writeStartElement(XsdElementName.SEQUENCE, xsw);
        try {
            for (final Attribute element : complexType.getAttributes()) {
                writeElement(element, xsw);
            }
        } catch (final RuntimeException e) {
            throw new RuntimeException(complexType.getName().getLocalPart(), e);
        } finally {
            writeEndElement(xsw);
        }
    }
    
    private static final void writeStartElement(final XsdElementName name, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(PREFIX_XS, name.getLocalName(), NAMESPACE_XS);
    }
}
