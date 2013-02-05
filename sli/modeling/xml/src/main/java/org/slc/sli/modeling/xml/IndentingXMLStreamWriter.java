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

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * An {@link XMLStreamWriter}filter that adds whitespace to pretty print.
 */
public final class IndentingXMLStreamWriter implements XMLStreamWriter {
    private static final String NEWLINE = "\n";

    /**
     * {@link State} is used to keep track of where we are in the document.
     */
    enum State {
        /**
         *
         */
        SEEN_NOTHING,
        /**
         *
         */
        SEEN_ELEMENT,
        /**
         *
         */
        SEEN_DATA;
    }

    private State state = State.SEEN_NOTHING;

    private static final String INDENT_STEP = "  ";
    private int depth = 0;

    private final XMLStreamWriter next;

    public IndentingXMLStreamWriter(final XMLStreamWriter next) {
        if (next == null) {
            throw new IllegalArgumentException("next");
        }
        this.next = next;
    }

    private void onStartElement() throws XMLStreamException {
        // All start elements get put on a new line except for the first
        if (depth > 0) {
            next.writeCharacters(NEWLINE);
        }
        state = State.SEEN_NOTHING;
        // Every start element is indented according to the depth.
        doIndent();
        // Increment the depth for the next start element.
        depth++;
    }

    private void onEndElement() throws XMLStreamException {
        // Decrement the depth for the end element.
        depth--;
        // We only indent when we have not seen text.
        if (state == State.SEEN_ELEMENT) {
            next.writeCharacters(NEWLINE);
            doIndent();
        } else {
            state = State.SEEN_ELEMENT;
        }
    }

    private void onEmptyElement() throws XMLStreamException {
        state = State.SEEN_ELEMENT;
        if (depth > 0) {
            next.writeCharacters(NEWLINE);
        }
        doIndent();
    }

    /**
     * Create indentation for the current level.
     */
    private void doIndent() throws XMLStreamException {
        if (depth > 0) {
            for (int i = 0; i < depth; i++) {
                next.writeCharacters(INDENT_STEP);
            }
        }
    }

    @Override
    public void close() throws XMLStreamException {
        next.close();
    }

    @Override
    public void flush() throws XMLStreamException {
        next.flush();
    }

    @Override
    public NamespaceContext getNamespaceContext() {
        return next.getNamespaceContext();
    }

    @Override
    public String getPrefix(final String uri) throws XMLStreamException {
        return next.getPrefix(uri);
    }

    @Override
    public Object getProperty(final String name) throws IllegalArgumentException {
        return next.getProperty(name);
    }

    @Override
    public void setDefaultNamespace(final String uri) throws XMLStreamException {
        next.setDefaultNamespace(uri);
    }

    @Override
    public void setNamespaceContext(final NamespaceContext context) throws XMLStreamException {
        next.setNamespaceContext(context);
    }

    @Override
    public void setPrefix(final String prefix, final String uri) throws XMLStreamException {
        next.setPrefix(prefix, uri);
    }

    @Override
    public void writeAttribute(final String localName, final String value) throws XMLStreamException {
        if (localName == null) {
            throw new IllegalArgumentException("localName");
        }
        if (value == null) {
            throw new IllegalArgumentException("value");
        }
        next.writeAttribute(localName, value);
    }

    @Override
    public void writeAttribute(final String namespaceURI, final String localName, final String value)
            throws XMLStreamException {
        next.writeAttribute(namespaceURI, localName, value);
    }

    @Override
    public void writeAttribute(final String prefix, final String namespaceURI, final String localName,
            final String value) throws XMLStreamException {
        if (prefix == null) {
            throw new IllegalArgumentException("prefix");
        }
        if (namespaceURI == null) {
            throw new IllegalArgumentException("namespaceURI");
        }
        if (localName == null) {
            throw new IllegalArgumentException("localName");
        }
        if (value == null) {
            throw new IllegalArgumentException("value");
        }
        next.writeAttribute(prefix, namespaceURI, localName, value);
    }

    @Override
    public void writeCData(final String data) throws XMLStreamException {
        state = State.SEEN_DATA;
        next.writeCData(data);
    }

    @Override
    public void writeCharacters(final String text) throws XMLStreamException {
        state = State.SEEN_DATA;
        next.writeCharacters(text);
    }

    @Override
    public void writeCharacters(final char[] text, final int start, final int len) throws XMLStreamException {
        state = State.SEEN_DATA;
        next.writeCharacters(text, start, len);
    }

    @Override
    public void writeComment(final String data) throws XMLStreamException {
        next.writeComment(data);
    }

    @Override
    public void writeDTD(final String dtd) throws XMLStreamException {
        next.writeDTD(dtd);
    }

    @Override
    public void writeDefaultNamespace(final String namespaceURI) throws XMLStreamException {
        next.writeDefaultNamespace(namespaceURI);
    }

    @Override
    public void writeEmptyElement(final String localName) throws XMLStreamException {
        onEmptyElement();
        next.writeEmptyElement(localName);
    }

    @Override
    public void writeEmptyElement(final String namespaceURI, final String localName) throws XMLStreamException {
        onEmptyElement();
        next.writeEmptyElement(namespaceURI, localName);
    }

    @Override
    public void writeEmptyElement(final String prefix, final String localName, final String namespaceURI)
            throws XMLStreamException {
        onEmptyElement();
        next.writeEmptyElement(prefix, localName, namespaceURI);
    }

    @Override
    public void writeEndDocument() throws XMLStreamException {
        next.writeEndDocument();
    }

    @Override
    public void writeEndElement() throws XMLStreamException {
        onEndElement();
        next.writeEndElement();
    }

    @Override
    public void writeEntityRef(final String name) throws XMLStreamException {
        next.writeEntityRef(name);
    }

    @Override
    public void writeNamespace(final String prefix, final String namespaceURI) throws XMLStreamException {
        next.writeNamespace(prefix, namespaceURI);
    }

    @Override
    public void writeProcessingInstruction(final String target) throws XMLStreamException {
        next.writeProcessingInstruction(target);
    }

    @Override
    public void writeProcessingInstruction(final String target, final String data) throws XMLStreamException {
        next.writeProcessingInstruction(target, data);
    }

    @Override
    public void writeStartDocument() throws XMLStreamException {
        next.writeStartDocument();
    }

    @Override
    public void writeStartDocument(final String version) throws XMLStreamException {
        next.writeStartDocument(version);
        next.writeCharacters(NEWLINE);
    }

    @Override
    public void writeStartDocument(final String encoding, final String version) throws XMLStreamException {
        next.writeStartDocument(encoding, version);
        next.writeCharacters(NEWLINE);
    }

    @Override
    public void writeStartElement(final String localName) throws XMLStreamException {
        onStartElement();
        next.writeStartElement(localName);
    }

    @Override
    public void writeStartElement(final String namespaceURI, final String localName) throws XMLStreamException {
        onStartElement();
        next.writeStartElement(namespaceURI, localName);
    }

    @Override
    public void writeStartElement(final String prefix, final String localName, final String namespaceURI)
            throws XMLStreamException {
        onStartElement();
        next.writeStartElement(prefix, localName, namespaceURI);
    }
}
