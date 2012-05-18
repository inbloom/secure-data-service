package org.slc.sli.ingestion.util;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

/**
 *
 *
 *
 */
public interface XmlEventBrowser {
    boolean isSupported(XMLEvent xmlEvent);

    void visit(XMLEvent xmlEvent, XMLEventReader eventReader) throws XMLStreamException;

    boolean canAcceptMore();

}
