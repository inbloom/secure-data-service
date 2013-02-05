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

package org.slc.sli.test.utils;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Iterator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter;

import org.slc.sli.test.edfi.entities.meta.relations.MetaRelations;
import org.slc.sli.test.xmlgen.StateEdFiXmlGenerator;

/**
 * Interchange writer
 *
 * @author bsuzuki
 *
 */
public class InterchangeWriter<T> {

    private static final int XML_WRITER_BUFFER_SIZE = 104857600; // 100 MB
    private static final boolean FORMAT_INTERCHANGE_XML = true;
    private static final boolean SINGLE_LINE_MARSHALLING = true;

    private String interchangeName = null;
    private String xmlFilePath = null;

    private XMLStreamWriter writer = null;
    private XMLStreamWriter defaultWriter = null;
    private IndentingXMLStreamWriter indentingWriter = null;

    private Marshaller streamMarshaller = null;

    private long interchangeStartTime;

    public InterchangeWriter(Class<T> interchange) {

        interchangeStartTime = System.currentTimeMillis();
        interchangeName = interchange.getSimpleName();
        xmlFilePath = StateEdFiXmlGenerator.rootOutputPath + "/" + interchangeName + ".xml";

        System.out.println("Creating interchange " + interchangeName);
        try {
            JAXBContext context = JAXBContext.newInstance(interchange);
            //JAXBContext context = JAXBContext.newInstance(org.slc.sli.test.edfi.entitiesR1.Section.class);
            streamMarshaller = context.createMarshaller();
            // Doesn't work for XMLStreamWriter
//            streamMarshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
            streamMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
        } catch (JAXBException e1) {
            e1.printStackTrace();
            System.exit(1);  // fail fast for now
        }

        try {
            FileOutputStream xmlFos = new FileOutputStream(xmlFilePath);
            OutputStream xmlBos = new BufferedOutputStream(xmlFos, XML_WRITER_BUFFER_SIZE);
            defaultWriter = XMLOutputFactory.newFactory().createXMLStreamWriter(xmlBos, "UTF-8");
            if (FORMAT_INTERCHANGE_XML) {
                indentingWriter = new IndentingXMLStreamWriter(defaultWriter);
                indentingWriter.setIndentStep("    ");
                writer = indentingWriter;
            } else {
                writer = defaultWriter;
            }
            writer.writeStartDocument("UTF-8", "1.0");

            // remove unwanted population of namespace attributes
            writer.setNamespaceContext(new NamespaceContext() {
                public Iterator<String> getPrefixes(String namespaceURI) {
                    return null;
                }

                public String getPrefix(String namespaceURI) {
                    return "";
                }

                public String getNamespaceURI(String prefix) {
                    return null;
                }
            });

              writer.writeStartElement(interchangeName);

            if ("sliXsd-R1".equalsIgnoreCase(org.slc.sli.test.xmlgen.StateEdFiXmlGenerator.XSDVersionPath)) {
                writer.writeNamespace(null, "http://slc-sli/ed-org/0.1");
            } else {

                 writer.writeNamespace(null, "http://ed-fi.org/0100");
           }

        } catch (XMLStreamException e) {
            e.printStackTrace();
            System.exit(1);  // fail fast for now
        } catch (FactoryConfigurationError e) {
            e.printStackTrace();
            System.exit(1);  // fail fast for now
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);  // fail fast for now
        }
    }

    public void close() {

      System.out.println("generated and marshaled in: "
      + (System.currentTimeMillis() - interchangeStartTime));

        try {
            if (FORMAT_INTERCHANGE_XML && SINGLE_LINE_MARSHALLING) {
                defaultWriter.writeCharacters("\n");
            }
//            writer.writeEndElement();
            writer.writeEndDocument();
            writer.close();
        } catch (XMLStreamException e) {
            e.printStackTrace();
            System.exit(1);  // fail fast for now
        }

        streamMarshaller = null;
    }

    /**
     * Marshal the provided object using the XMLStreamWriter specified
     *
     * @param objectToMarshal
     * @param outputStream
     * @throws JAXBException
     */
    @SuppressWarnings("unused")
    public void marshal(Object objectToMarshal) {

        if (objectToMarshal != null) {

            try {
                if (FORMAT_INTERCHANGE_XML && SINGLE_LINE_MARSHALLING) {
                    defaultWriter.writeCharacters("\n");
                    streamMarshaller.marshal(objectToMarshal, defaultWriter);
                } else {
                    streamMarshaller.marshal(objectToMarshal, writer);
                }
            } catch (JAXBException e) {
                e.printStackTrace();
                System.exit(1);  // fail fast for now
            } catch (XMLStreamException e) {
                e.printStackTrace();
                System.exit(1);  // fail fast for now
            }

        } else {
            throw new IllegalArgumentException("Cannot marshal null object");
        }
    }

    public String getXmlFilePath() {
        return xmlFilePath;
    }

}
