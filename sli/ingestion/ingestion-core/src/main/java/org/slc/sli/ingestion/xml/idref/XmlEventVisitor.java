package org.slc.sli.ingestion.xml.idref;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

/**
 * XMLEvent visitor interface.
 *
 * @author okrook
 *
 */
public interface XmlEventVisitor {

    /**
     * Whether visitor can accept more visits.
     *
     * @return Boolean
     */
    boolean canAcceptMore();

    /**
     * Whether the XMLEvent is supported by the visitor.
     *
     * @param xmlEvent XMLEvent to check
     * @return Boolean
     */
    boolean isSupported(XMLEvent xmlEvent);

    /**
     * Visitor's implementation.
     *
     * @param xmlEvent XMLEvent that visits the visitor
     * @param eventReader XMLEventReader
     * @throws XMLStreamException on error
     */
    void visit(XMLEvent xmlEvent, XMLEventReader eventReader) throws XMLStreamException;
}
