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


package org.slc.sli.modeling.xml;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Utility methods for use when parsing an XML stream.
 * 
 * 
 * @author kmyers
 *
 */
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
