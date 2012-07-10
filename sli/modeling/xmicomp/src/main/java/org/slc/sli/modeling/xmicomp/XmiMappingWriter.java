package org.slc.sli.modeling.xmicomp;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.slc.sli.modeling.xml.IndentingXMLStreamWriter;

public class XmiMappingWriter {
    
    private static final void closeQuiet(final Closeable closeable) {
        try {
            closeable.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
    
    private static final void writeStatus(final XmiMappingStatus status, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(XmiMappingConstants.STATUS.getNamespaceURI(), XmiMappingConstants.STATUS.getLocalPart());
        try {
            switch (status) {
                case MATCH: {
                    xsw.writeCharacters(XmiMappingValues.STATUS_MATCH);
                    break;
                }
                case IGNORABLE: {
                    xsw.writeCharacters(XmiMappingValues.STATUS_IGNORABLE);
                    break;
                }
                case UNKNOWN: {
                    xsw.writeCharacters(XmiMappingValues.STATUS_UNKNOWN);
                    break;
                }
                case TRANSIENT: {
                    xsw.writeCharacters(XmiMappingValues.STATUS_TRANSIENT);
                    break;
                }
                case ALIGN: {
                    xsw.writeCharacters(XmiMappingValues.STATUS_ALIGN);
                    break;
                }
                case BUG: {
                    xsw.writeCharacters(XmiMappingValues.STATUS_BUG);
                    break;
                }
                default: {
                    throw new AssertionError(status);
                }
            }
        } finally {
            xsw.writeEndElement();
        }
    }
    
    private static final void writeComment(final String comment, final XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeStartElement(XmiMappingConstants.COMMENT.getNamespaceURI(), XmiMappingConstants.COMMENT.getLocalPart());
        try {
            xsw.writeCharacters(comment);
        } finally {
            xsw.writeEndElement();
        }
    }
    
    private static final void writeReference(final XmiDefinition ref, final QName elementName, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(elementName.getNamespaceURI(), elementName.getLocalPart());
        try {
            xsw.writeStartElement(XmiMappingConstants.NAME.getNamespaceURI(), XmiMappingConstants.NAME.getLocalPart());
            try {
                xsw.writeCharacters(ref.getName());
            } finally {
                xsw.writeEndElement();
            }
            xsw.writeStartElement(XmiMappingConstants.VERSION.getNamespaceURI(),
                    XmiMappingConstants.VERSION.getLocalPart());
            try {
                xsw.writeCharacters(ref.getVersion());
            } finally {
                xsw.writeEndElement();
            }
            xsw.writeStartElement(XmiMappingConstants.FILE.getNamespaceURI(), XmiMappingConstants.FILE.getLocalPart());
            try {
                xsw.writeCharacters(ref.getFile());
            } finally {
                xsw.writeEndElement();
            }
        } finally {
            xsw.writeEndElement();
        }
    }
    
    private static final void writeFeature(final XmiFeature feature, final QName elementName, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(elementName.getNamespaceURI(), elementName.getLocalPart());
        try {
            xsw.writeStartElement(XmiMappingConstants.TYPE.getNamespaceURI(), XmiMappingConstants.TYPE.getLocalPart());
            try {
                xsw.writeCharacters(feature.getType());
            } finally {
                xsw.writeEndElement();
            }
            xsw.writeStartElement(XmiMappingConstants.NAME.getNamespaceURI(), XmiMappingConstants.NAME.getLocalPart());
            try {
                xsw.writeCharacters(feature.getName());
            } finally {
                xsw.writeEndElement();
            }
            xsw.writeStartElement(XmiMappingConstants.DEFN.getNamespaceURI(), XmiMappingConstants.DEFN.getLocalPart());
            try {
                final Boolean defined = feature.isDefined();
                xsw.writeCharacters((defined != null) ? Boolean.toString(defined) : "");
            } finally {
                xsw.writeEndElement();
            }
        } finally {
            xsw.writeEndElement();
        }
    }
    
    private static final void writeMapping(final XmiMapping mapping, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(XmiMappingConstants.MAPPING.getNamespaceURI(), XmiMappingConstants.MAPPING.getLocalPart());
        try {
            final XmiFeature lhsFeature = mapping.getLhsFeature();
            if (lhsFeature != null) {
                writeFeature(lhsFeature, XmiMappingConstants.LHS_FEATURE, xsw);
            } else {
                xsw.writeEmptyElement(XmiMappingConstants.LHS_MISSING.getLocalPart());
            }
            final XmiFeature rhsFeature = mapping.getRhsFeature();
            if (rhsFeature != null) {
                writeFeature(rhsFeature, XmiMappingConstants.RHS_FEATURE, xsw);
            } else {
                xsw.writeEmptyElement(XmiMappingConstants.RHS_MISSING.getLocalPart());
            }
            writeStatus(mapping.getStatus(), xsw);
            writeComment(mapping.getComment(), xsw);
        } finally {
            xsw.writeEndElement();
        }
    }
    
    public static final void writeMappingDocument(final XmiComparison document, final OutputStream outstream) {
        final XMLOutputFactory xof = XMLOutputFactory.newInstance();
        try {
            final XMLStreamWriter xsw = new IndentingXMLStreamWriter(xof.createXMLStreamWriter(outstream, "UTF-8"));
            xsw.writeStartDocument("UTF-8", "1.0");
            try {
                writeMappingDocument(document, xsw);
            } finally {
                xsw.writeEndDocument();
            }
            xsw.flush();
            xsw.close();
        } catch (final XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static final void writeMappingDocument(final XmiComparison document, final File file) {
        try {
            final OutputStream outstream = new BufferedOutputStream(new FileOutputStream(file));
            try {
                writeMappingDocument(document, outstream);
            } finally {
                closeQuiet(outstream);
            }
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public static final void writeMappingDocument(final XmiComparison document, final String fileName) {
        writeMappingDocument(document, new File(fileName));
    }
    
    private static final void writeMappingDocument(final XmiComparison documentElement, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeProcessingInstruction("xml-stylesheet", "type='text/xsl' href='xmi-mapping.xsl'");
        xsw.setPrefix("", XmiMappingConstants.NAMESPACE_MAPPING);
        xsw.writeStartElement(XmiMappingConstants.DOCUMENT_ELEMENT.getLocalPart());
        xsw.writeNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        xsw.writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "noNamespaceSchemaLocation",
                "xmi-mapping.xsd");
        try {
            writeReference(documentElement.getLhsDef(), XmiMappingConstants.LHS_MODEL, xsw);
            writeReference(documentElement.getRhsDef(), XmiMappingConstants.RHS_MODEL, xsw);
            for (final XmiMapping mapping : documentElement.getMappings()) {
                writeMapping(mapping, xsw);
            }
        } finally {
            xsw.writeEndElement();
        }
    }
}
