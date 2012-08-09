/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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
