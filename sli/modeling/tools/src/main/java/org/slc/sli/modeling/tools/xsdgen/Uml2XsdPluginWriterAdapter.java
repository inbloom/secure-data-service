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


package org.slc.sli.modeling.tools.xsdgen;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.slc.sli.modeling.uml.Occurs;
import org.slc.sli.modeling.xsd.WxsNamespace;
import org.slc.sli.modeling.xsd.XsdAttributeName;

/**
 * Implementation of XSD writer (from UML).
 * 
 * @author kmyers
 *
 */
final class Uml2XsdPluginWriterAdapter implements Uml2XsdPluginWriter {

    private static final String toString(final Occurs value) {
        if (value == null) {
            throw new IllegalArgumentException("value");
        }
        switch (value) {
        case ZERO: {
            return "0";
        }
        case ONE: {
            return "1";
        }
        case UNBOUNDED: {
            return "unbounded";
        }
        default: {
            throw new AssertionError(value);
        }
        }
    }

    private static final String typeLexicalName(final QName name, final XMLStreamWriter context) {
        final NamespaceContext namespaceContext = context.getNamespaceContext();
        final String namespace = name.getNamespaceURI();
        if (namespace.length() > 0) {
            final String prefix = namespaceContext.getPrefix(namespace);
            if (prefix.length() > 0) {
                return prefix.concat(":").concat(name.getLocalPart());
            } else {
                return name.getLocalPart();
            }
        } else {
            return name.getLocalPart();
        }
    }

    private final String prefix;

    private final XMLStreamWriter xsw;

    public Uml2XsdPluginWriterAdapter(final XMLStreamWriter xsw, final String prefix) {
        this.xsw = xsw;
        this.prefix = prefix;
    }

    @Override
    public void annotation() {
        try {
            xsw.writeStartElement(prefix, "annotation", WxsNamespace.URI);
        } catch (final XMLStreamException e) {
            throw new XsdGenRuntimeException(e);
        }
    }

    @Override
    public void appinfo() {
        try {
            xsw.writeStartElement(prefix, "appinfo", WxsNamespace.URI);
        } catch (final XMLStreamException e) {
            throw new XsdGenRuntimeException(e);
        }
    }

    @Override
    public void begin(final String prefix, final String localName, final String namespace) {
        try {
            xsw.writeStartElement(prefix, localName, namespace);
        } catch (final XMLStreamException e) {
            throw new XsdGenRuntimeException(e);
        }
    }

    @Override
    public void characters(final String text) {
        try {
            xsw.writeCharacters(text);
        } catch (final XMLStreamException e) {
            throw new XsdGenRuntimeException(e);
        }
    }

    @Override
    public void choice() {
        try {
            xsw.writeStartElement(prefix, "choice", WxsNamespace.URI);
        } catch (final XMLStreamException e) {
            throw new XsdGenRuntimeException(e);
        }
    }

    @Override
    public void comment(final String data) {
        try {
            xsw.writeComment(data);
        } catch (final XMLStreamException e) {
            throw new XsdGenRuntimeException(e);
        }
    }

    @Override
    public void complexType() {
        try {
            xsw.writeStartElement(prefix, "complexType", WxsNamespace.URI);
        } catch (final XMLStreamException e) {
            throw new XsdGenRuntimeException(e);
        }
    }

    @Override
    public void documentation() {
        try {
            xsw.writeStartElement(prefix, "documentation", WxsNamespace.URI);
        } catch (final XMLStreamException e) {
            throw new XsdGenRuntimeException(e);
        }
    }

    @Override
    public void element() {
        try {
            xsw.writeStartElement(prefix, "element", WxsNamespace.URI);
        } catch (final XMLStreamException e) {
            throw new XsdGenRuntimeException(e);
        }
    }

    @Override
    public void elementName(final QName name) {
        try {
            xsw.writeAttribute("name", name.getLocalPart());
        } catch (final XMLStreamException e) {
            throw new XsdGenRuntimeException(e);
        }
    }

    @Override
    public void end() {
        try {
            xsw.writeEndElement();
        } catch (final XMLStreamException e) {
            throw new XsdGenRuntimeException(e);
        }
    }

    @Override
    public void maxOccurs(final Occurs value) {
        if (!value.equals(Occurs.ONE)) {
            try {
                xsw.writeAttribute(XsdAttributeName.MAX_OCCURS.getLocalName(), toString(value));
            } catch (final XMLStreamException e) {
                throw new XsdGenRuntimeException(e);
            }
        }
    }

    @Override
    public void minOccurs(final Occurs value) {
        if (!value.equals(Occurs.ONE)) {
            try {
                xsw.writeAttribute(XsdAttributeName.MIN_OCCURS.getLocalName(), toString(value));
            } catch (final XMLStreamException e) {
                throw new XsdGenRuntimeException(e);
            }
        }
    }

    @Override
    public void ref(final QName name) {
        try {
            if (name.getNamespaceURI().equals(WxsNamespace.URI)) {
                xsw.writeAttribute("ref", prefix.concat(":").concat(name.getLocalPart()));
            } else {
                xsw.writeAttribute("ref", typeLexicalName(name, xsw));
            }
        } catch (final XMLStreamException e) {
            throw new XsdGenRuntimeException(e);
        }
    }

    @Override
    public void sequence() {
        try {
            xsw.writeStartElement(prefix, "sequence", WxsNamespace.URI);
        } catch (final XMLStreamException e) {
            throw new XsdGenRuntimeException(e);
        }
    }

    @Override
    public void type(final QName name) {
        try {
            if (name.getNamespaceURI().equals(WxsNamespace.URI)) {
                xsw.writeAttribute("type", prefix.concat(":").concat(name.getLocalPart()));
            } else {
                xsw.writeAttribute("type", typeLexicalName(name, xsw));
            }
        } catch (final XMLStreamException e) {
            throw new XsdGenRuntimeException(e);
        }
    }
}
