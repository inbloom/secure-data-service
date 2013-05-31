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


package org.slc.sli.ingestion.handler;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Splits files
 *
 * @author dkornishev
 *
 */
public class FileSplitHandler {
    public static final Logger LOG = LoggerFactory.getLogger(FileSplitHandler.class);
    private static final int SPLIT_AT = 10000;

    private XMLEventFactory eventFactory = XMLEventFactory.newInstance();

    public void split(File file, String outPath) throws XMLStreamException, IOException {
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLEventReader reader = inputFactory.createXMLEventReader(new FileReader(file));
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        XMLEventWriter writer = null;

        int counter = 0;
        int startEventCounter = 0;

        XMLEvent event;
        StartElement parent = null;
        try {

            event = reader.nextEvent();
            while (!event.isEndDocument()) {
                if (event.isStartElement()) {
                    startEventCounter++;
                    if (startEventCounter == 1) {
                        parent = event.asStartElement();
                        writer = outputFactory.createXMLEventWriter(new FileWriter(outPath
                                + parent.getName().getLocalPart() + counter / SPLIT_AT + ".xml"));
                        writer.add(parent);
                        continue;
                    }

                    writeToFile(reader, event, writer);
                    counter++;

                    if (counter % SPLIT_AT == 0) {
                        writer.add(eventFactory.createEndElement(parent.asStartElement().getName(), null));
                        writer.close();
                        writer = outputFactory.createXMLEventWriter(new FileWriter(outPath
                                + parent.getName().getLocalPart() + counter / SPLIT_AT + ".xml"));
                        writer.add(parent);
                    }
                }
                event = reader.nextEvent();
            }
        } finally {
            if (parent != null && writer != null) {
                writer.add(eventFactory.createEndElement(parent.asStartElement().getName(), null));
            }
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
        }

    }

    private void writeToFile(XMLEventReader reader, XMLEvent startEvent, XMLEventWriter writer)
            throws XMLStreamException, IOException {
        StartElement element = startEvent.asStartElement();
        QName name = element.getName();
        writer.add(element);
        while (true) {
            XMLEvent event = reader.nextEvent();
            writer.add(event);
            if (event.isEndElement()) {
                EndElement end = event.asEndElement();
                if (end.getName().equals(name)) {
                    break;
                }
            }
        }
    }
}
