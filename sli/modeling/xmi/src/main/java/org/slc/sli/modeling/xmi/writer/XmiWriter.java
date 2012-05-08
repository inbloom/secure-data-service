package org.slc.sli.modeling.xmi.writer;

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
import org.slc.sli.modeling.uml.Association;
import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.DataType;
import org.slc.sli.modeling.uml.EnumLiteral;
import org.slc.sli.modeling.uml.EnumType;
import org.slc.sli.modeling.uml.HasIdentity;
import org.slc.sli.modeling.uml.HasName;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.Model;
import org.slc.sli.modeling.uml.Multiplicity;
import org.slc.sli.modeling.uml.Occurs;
import org.slc.sli.modeling.uml.Range;
import org.slc.sli.modeling.uml.Reference;
import org.slc.sli.modeling.uml.ReferenceType;
import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.uml.TaggedValue;
import org.slc.sli.modeling.xmi.XmiAttributeName;
import org.slc.sli.modeling.xmi.XmiElementName;
import org.slc.sli.modeling.xml.IndentingXMLStreamWriter;

/**
 * Writes a UML {@link Model} to a file (by name) or {@link OutputStream}.
 */
public final class XmiWriter {
    
    private static final String NAMESPACE_UML = "org.omg.xmi.namespace.UML";
    private static final String PREFIX_UML = "UML";
    
    private static final void closeQuiet(final Closeable closeable) {
        try {
            closeable.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
    
    private static final XmiElementName getXmiElementName(final ReferenceType type) {
        if (type == null) {
            throw new NullPointerException("type");
        }
        switch (type) {
            case CLASS_TYPE: {
                return XmiElementName.CLASS;
            }
            case DATA_TYPE: {
                return XmiElementName.DATA_TYPE;
            }
            case ENUM_TYPE: {
                return XmiElementName.ENUMERATION;
            }
            case TAG_DEFINITION: {
                return XmiElementName.TAG_DEFINITION;
            }
            default: {
                throw new AssertionError(type);
            }
        }
    }
    
    private static final int range(final Occurs value) {
        if (value == null) {
            throw new NullPointerException("value");
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
    
    @SuppressWarnings("unused")
    private static final void writeAssociation(final Association association, final XMLStreamWriter xsw)
            throws XMLStreamException {
        
        xsw.writeStartElement(PREFIX_UML, "Association", NAMESPACE_UML);
        writeId(association, xsw);
        xsw.writeAttribute("name", "");
        xsw.writeStartElement(PREFIX_UML, "Association.connection", NAMESPACE_UML);
        writeAssociationEnd(association.getLHS(), xsw);
        writeAssociationEnd(association.getRHS(), xsw);
        xsw.writeEndElement();
        xsw.writeEndElement();
    }
    
    private static final void writeAssociationEnd(final AssociationEnd end, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(PREFIX_UML, "AssociationEnd", NAMESPACE_UML);
        writeId(end, xsw);
        xsw.writeAttribute("name", "");
        xsw.writeStartElement(PREFIX_UML, "AssociationEnd.multiplicity", NAMESPACE_UML);
        writeMultiplicity(end.getMultiplicity(), xsw);
        xsw.writeEndElement();
        writeAssociationEndParticipant(end.getType().getReference(), xsw);
        xsw.writeEndElement();
    }
    
    private static final void writeAssociationEndParticipant(final Reference participant, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(PREFIX_UML, "AssociationEnd.participant", NAMESPACE_UML);
        switch (participant.getKind()) {
            case CLASS_TYPE: {
                xsw.writeStartElement(PREFIX_UML, "Class", NAMESPACE_UML);
                break;
            }
            default: {
                throw new AssertionError(participant.getKind());
            }
        }
        xsw.writeAttribute("xmi.idref", participant.toString());
        xsw.writeEndElement();
        xsw.writeEndElement();
    }
    
    private static final void writeAttribute(final Attribute attribute, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(PREFIX_UML, "Attribute", NAMESPACE_UML);
        try {
            writeId(attribute, xsw);
            xsw.writeAttribute("name", attribute.getName());
            writeModelElementTaggedValues(attribute, xsw);
            xsw.writeStartElement(PREFIX_UML, "StructuralFeature.multiplicity", NAMESPACE_UML);
            try {
                writeMultiplicity(attribute.getMultiplicity(), xsw);
            } finally {
                xsw.writeEndElement();
            }
            xsw.writeStartElement(PREFIX_UML, "StructuralFeature.type", NAMESPACE_UML);
            try {
                writeReference(attribute.getType().getReference(), xsw);
            } finally {
                xsw.writeEndElement();
            }
        } finally {
            xsw.writeEndElement();
        }
    }
    
    private static final void writeClassType(final ClassType classType, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(PREFIX_UML, "Class", NAMESPACE_UML);
        try {
            writeId(classType, xsw);
            xsw.writeAttribute("name", classType.getName());
            writeModelElementTaggedValues(classType, xsw);
            xsw.writeStartElement(PREFIX_UML, "Classifier.feature", NAMESPACE_UML);
            try {
                for (final Attribute attribute : classType.getAttributes()) {
                    writeAttribute(attribute, xsw);
                }
            } finally {
                xsw.writeEndElement();
            }
        } finally {
            xsw.writeEndElement();
        }
    }
    
    private static final void writeContent(final Model model, final XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeStartElement("XMI.content");
        try {
            writeModel(model, xsw);
        } finally {
            xsw.writeEndElement();
        }
    }
    
    private static final void writeDataType(final DataType dataType, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(PREFIX_UML, "DataType", NAMESPACE_UML);
        try {
            writeId(dataType, xsw);
            xsw.writeAttribute("name", dataType.getName());
            xsw.writeAttribute("isAbstract", Boolean.toString(dataType.isAbstract()));
            writeModelElementTaggedValues(dataType, xsw);
        } finally {
            xsw.writeEndElement();
        }
    }
    
    public static final void writeDocument(final Model model, final OutputStream outstream) {
        final XMLOutputFactory xof = XMLOutputFactory.newInstance();
        try {
            final XMLStreamWriter xsw = new IndentingXMLStreamWriter(xof.createXMLStreamWriter(outstream, "UTF-8"));
            xsw.writeStartDocument("UTF-8", "1.0");
            try {
                XmiWriter.writeXMI(model, xsw);
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
    
    private static final void writeStartElement(final XmiElementName name, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(PREFIX_UML, name.getLocalName(), NAMESPACE_UML);
    }
    
    private static final void writeEnumType(final EnumType enumType, final XMLStreamWriter xsw)
            throws XMLStreamException {
        writeStartElement(XmiElementName.ENUMERATION, xsw);
        try {
            writeId(enumType, xsw);
            writeName(enumType, xsw);
            writeModelElementTaggedValues(enumType, xsw);
            writeStartElement(XmiElementName.ENUMERATION_LITERAL_GROUP, xsw);
            try {
                for (final EnumLiteral literal : enumType.getLiterals()) {
                    writeStartElement(XmiElementName.ENUMERATION_LITERAL, xsw);
                    writeId(literal, xsw);
                    writeName(literal, xsw);
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
    
    private static final void writeName(final HasName hasIdentity, final XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeAttribute(XmiAttributeName.NAME.getLocalName(), hasIdentity.getName());
    }
    
    private static final Identifier writeModel(final Model model, final XMLStreamWriter xsw) throws XMLStreamException {
        final Identifier idModel = Identifier.random();
        
        xsw.writeStartElement(PREFIX_UML, "Model", NAMESPACE_UML);
        try {
            xsw.writeAttribute(XmiAttributeName.ID.getLocalName(), idModel.toString());
            xsw.writeAttribute(XmiAttributeName.NAME.getLocalName(), "SLI");
            xsw.writeStartElement(PREFIX_UML, "Namespace.ownedElement", NAMESPACE_UML);
            try {
                final Map<Identifier, DataType> dataTypeMap = model.getDataTypeMap();
                for (final Identifier key : dataTypeMap.keySet()) {
                    final DataType type = dataTypeMap.get(key);
                    writeDataType(type, xsw);
                }
                final Map<Identifier, EnumType> enumTypeMap = model.getEnumTypeMap();
                for (final Identifier key : enumTypeMap.keySet()) {
                    final EnumType type = enumTypeMap.get(key);
                    writeEnumType(type, xsw);
                }
                final Map<Identifier, ClassType> classTypeMap = model.getClassTypeMap();
                for (final Identifier key : classTypeMap.keySet()) {
                    final ClassType type = classTypeMap.get(key);
                    writeClassType(type, xsw);
                }
                final Map<Identifier, TagDefinition> tagDefinitionMap = model.getTagDefinitionMap();
                for (final Identifier key : tagDefinitionMap.keySet()) {
                    final TagDefinition tagDefinition = tagDefinitionMap.get(key);
                    writeTagDefinition(tagDefinition, xsw);
                }
            } finally {
                xsw.writeEndElement();
            }
        } finally {
            xsw.writeEndElement();
        }
        return idModel;
    }
    
    private static final void writeModelElementTaggedValues(final AbstractModelElement element,
            final XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeStartElement(PREFIX_UML, "ModelElement.taggedValue", NAMESPACE_UML);
        try {
            for (final TaggedValue taggedValue : element.getTaggedValues()) {
                writeTaggedValue(taggedValue, xsw);
            }
        } finally {
            xsw.writeEndElement();
        }
    }
    
    private static final void writeMultiplicity(final Multiplicity multiplicity, final XMLStreamWriter xsw)
            throws XMLStreamException {
        
        xsw.writeStartElement(PREFIX_UML, "Multiplicity", NAMESPACE_UML);
        writeId(multiplicity, xsw);
        writeMultiplicityRange(multiplicity.getRange(), xsw);
        xsw.writeEndElement();
    }
    
    private static final void writeMultiplicityRange(final Range range, final XMLStreamWriter xsw)
            throws XMLStreamException {
        
        xsw.writeStartElement(PREFIX_UML, "Multiplicity.range", NAMESPACE_UML);
        xsw.writeStartElement(PREFIX_UML, "MultiplicityRange", NAMESPACE_UML);
        writeId(range, xsw);
        xsw.writeAttribute("lower", Integer.toString(range(range.getLower())));
        xsw.writeAttribute("upper", Integer.toString(range(range.getUpper())));
        xsw.writeEndElement();
        xsw.writeEndElement();
    }
    
    private static final void writeReference(final Reference reference, final XMLStreamWriter xsw)
            throws XMLStreamException {
        if (reference == null) {
            throw new NullPointerException("reference");
        }
        writeStartElement(getXmiElementName(reference.getKind()), xsw);
        try {
            xsw.writeAttribute(XmiAttributeName.IDREF.getLocalName(), reference.getIdRef().toString());
        } finally {
            xsw.writeEndElement();
        }
    }
    
    private static final void writeTagDefinition(final TagDefinition tagdef, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(PREFIX_UML, "TagDefinition", NAMESPACE_UML);
        try {
            writeId(tagdef, xsw);
            writeName(tagdef, xsw);
            xsw.writeStartElement(PREFIX_UML, "TagDefinition.multiplicity", NAMESPACE_UML);
            try {
                writeMultiplicity(tagdef.getMultiplicity(), xsw);
            } finally {
                xsw.writeEndElement();
            }
        } finally {
            xsw.writeEndElement();
        }
    }
    
    private static final void writeTaggedValue(final TaggedValue taggedValue, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(PREFIX_UML, "TaggedValue", NAMESPACE_UML);
        try {
            writeId(taggedValue, xsw);
            xsw.writeStartElement(PREFIX_UML, "TaggedValue.dataValue", NAMESPACE_UML);
            try {
                xsw.writeCharacters(taggedValue.getValue());
            } finally {
                xsw.writeEndElement();
            }
            xsw.writeStartElement(PREFIX_UML, "TaggedValue.type", NAMESPACE_UML);
            try {
                writeReference(taggedValue.getTagDefinition().getReference(), xsw);
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
     * @param model
     *            The UML model.
     * @param xsw
     *            The XML stream.
     * @throws XMLStreamException
     *             if anything bad happens.
     */
    private static final void writeXMI(final Model model, final XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeStartElement("XMI");
        try {
            xsw.writeNamespace("UML", NAMESPACE_UML);
            xsw.writeAttribute("xmi.version", "1.2");
            writeContent(model, xsw);
        } finally {
            xsw.writeEndElement();
        }
    }
}
