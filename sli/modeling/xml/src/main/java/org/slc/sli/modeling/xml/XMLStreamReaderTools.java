package org.slc.sli.modeling.xml;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class XMLStreamReaderTools {
    
    /**
     * Skips (recursively) over the element in question. Also useful during development.
     * 
     * @param reader
     *            The StAX {@link XMLStreamReader}.
     */
    public static final void skipElement(final XMLStreamReader reader) throws XMLStreamException {
        final String localName = reader.getLocalName();
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    skipElement(reader);
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (localName.equals(reader.getLocalName())) {
                        return;
                    } else {
                        throw new AssertionError(reader.getLocalName());
                    }
                }
                case XMLStreamConstants.CHARACTERS: {
                    // Ignore.
                    break;
                }
                default: {
                    throw new AssertionError(reader.getEventType());
                }
            }
        }
        throw new AssertionError();
    }
    
}
