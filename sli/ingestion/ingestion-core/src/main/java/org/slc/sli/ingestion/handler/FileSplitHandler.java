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
    
    public void split(File file) throws XMLStreamException, IOException {
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLEventReader reader = inputFactory.createXMLEventReader(new FileReader(file));
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        XMLEventWriter writer = outputFactory.createXMLEventWriter(new FileWriter("/var/files/out" + ".xml"));
        
        int counter = 0;
        int startEventCounter = 0;
        
        XMLEvent event;
        XMLEvent parent = null;
        try {
            
            while (!(event = reader.nextEvent()).isEndDocument()) {
                if (event.isStartElement()) {
                    startEventCounter++;
                    if (startEventCounter == 1) {
                        parent = event;
                        writer.add(parent);
                        continue;
                    }
                    
                    StartElement element = event.asStartElement();
//                    System.out.println(element.getName());
                    writeToFile(reader, event, writer);
                    counter++;
                    
                    if (counter % SPLIT_AT == 0) {
                        writer.add(eventFactory.createEndElement(parent.asStartElement().getName(), null));
                        writer.close();
                        writer = outputFactory.createXMLEventWriter(new FileWriter("/var/files/out/_"+parent.asStartElement().getName().getLocalPart() + (counter / SPLIT_AT) + ".xml"));
                        writer.add(parent);
                    }
                }
            }
        } catch (XMLStreamException e) {
            throw e;
        } finally {
            writer.add(eventFactory.createEndElement(parent.asStartElement().getName(), null));
            reader.close();
            writer.close();
        }
        
        //System.out.println("Total: "+counter);
    }
    
    private void writeToFile(XMLEventReader reader, XMLEvent startEvent, XMLEventWriter writer) throws XMLStreamException, IOException {
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
