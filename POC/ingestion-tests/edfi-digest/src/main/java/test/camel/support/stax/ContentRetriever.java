package test.camel.support.stax;

import javax.xml.stream.XMLStreamException;

public interface ContentRetriever {
	void retrieve() throws XMLStreamException;
}
