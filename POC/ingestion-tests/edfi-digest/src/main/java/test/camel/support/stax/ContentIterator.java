package test.camel.support.stax;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public interface ContentIterator {

    void visit(XMLEvent xmlEvent) throws XMLStreamException;

}
