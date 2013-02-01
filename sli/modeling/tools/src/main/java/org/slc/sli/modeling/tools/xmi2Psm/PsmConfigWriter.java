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


package org.slc.sli.modeling.tools.xmi2Psm;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.slc.sli.modeling.psm.*;
import org.slc.sli.modeling.uml.Model;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.xmi.XmiAttributeName;
import org.slc.sli.modeling.xml.IndentingXMLStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Write PSM.
 */
public final class PsmConfigWriter {

    private static final Logger LOG = LoggerFactory.getLogger(PsmConfigWriter.class);

    private static final void closeQuiet(final Closeable closeable) {
        try {
            closeable.close();
        } catch (final IOException e) {
            LOG.warn(e.getMessage());
        }
    }

    private static final void writeSingularResourceName(final PsmCollection collection, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(PsmConfigElements.SINGULAR_RESOURCE_NAME.getLocalPart());
        try {
            xsw.writeCharacters(collection.getName());
        } finally {
            xsw.writeEndElement();
        }
    }

    private static final void writePluralResourceName(final PsmResource resource, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(PsmConfigElements.GRAPH_RESOURCE_NAME.getLocalPart());
        try {
            xsw.writeCharacters(resource.getName());
        } finally {
            xsw.writeEndElement();
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
            throw new PsmConfigRuntimeException(e);
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
            LOG.warn(e.getMessage());
        }
    }

    private static final void writeDocument(final PsmDocument<Type> document, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(PsmConfigElements.DOCUMENT.getLocalPart());
        try {
            final Type type = document.getType();
            // The type name is a reasonable default for the document name.
            xsw.writeAttribute(XmiAttributeName.NAME.getLocalName(), type.getName());
            xsw.writeStartElement(PsmConfigElements.CLASS_TYPE.getLocalPart());
            try {
                xsw.writeAttribute(XmiAttributeName.NAME.getLocalName(), type.getName());
            } finally {
                xsw.writeEndElement();
            }
            writePluralResourceName(document.getGraphAssociationEndName(), xsw);
            writeSingularResourceName(document.getSingularResourceName(), xsw);
        } finally {
            xsw.writeEndElement();
        }
    }

    private static final void writeRoot(final PsmConfig<Type> documentation, final Model model,
            final XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeStartElement(PsmConfigElements.DOCUMENTS.getLocalPart());
        try {
            for (final PsmDocument<Type> document : documentation.getDocuments()) {
                writeDocument(document, xsw);
            }
        } finally {
            xsw.writeEndElement();
        }
    }
}
