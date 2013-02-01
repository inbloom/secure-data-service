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


package org.slc.sli.modeling.wadl.reader;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.io.IOUtils;
import org.slc.sli.modeling.rest.Application;
import org.slc.sli.modeling.rest.Documentation;
import org.slc.sli.modeling.rest.Grammars;
import org.slc.sli.modeling.rest.Include;
import org.slc.sli.modeling.rest.Link;
import org.slc.sli.modeling.rest.Method;
import org.slc.sli.modeling.rest.Option;
import org.slc.sli.modeling.rest.Param;
import org.slc.sli.modeling.rest.ParamStyle;
import org.slc.sli.modeling.rest.Representation;
import org.slc.sli.modeling.rest.Request;
import org.slc.sli.modeling.rest.Resource;
import org.slc.sli.modeling.rest.ResourceType;
import org.slc.sli.modeling.rest.Resources;
import org.slc.sli.modeling.rest.Response;
import org.slc.sli.modeling.wadl.WadlAttributeName;
import org.slc.sli.modeling.wadl.WadlElementName;
import org.slc.sli.modeling.wadl.WadlRuntimeException;
import org.slc.sli.modeling.wadl.WadlSyntax;
import org.slc.sli.modeling.xdm.DmComment;
import org.slc.sli.modeling.xdm.DmElement;
import org.slc.sli.modeling.xdm.DmNode;
import org.slc.sli.modeling.xdm.DmProcessingInstruction;
import org.slc.sli.modeling.xdm.DmText;

/**
 * Reads from a file (by name) or {@link InputStream} to produce a UML {@link Model}.
 */
public final class WadlReader {
    private static final String GLOBAL_NAMESPACE = "";

    /**
     * A programmatic assertion that we have the reader positioned on the correct element.
     *
     * @param expectLocalName
     *            The local name that we expect.
     * @param reader
     *            The reader.
     */
    private static final void assertName(final WadlElementName name, final XMLStreamReader reader) {
        if (!match(name, reader)) {
            throw new AssertionError(reader.getLocalName());
        }
    }

    private static final <T> T assertNotNull(final T obj) {
        if (obj != null) {
            return obj;
        } else {
            throw new AssertionError();
        }
    }

    private static final void closeQuiet(final Closeable closeable) {
        IOUtils.closeQuietly(closeable);
    }

    private static final String getBase(final XMLStreamReader reader, final String defaultName) {
        final String name = reader.getAttributeValue(GLOBAL_NAMESPACE, WadlAttributeName.BASE.getLocalName());
        if (name != null) {
            return name;
        } else {
            return defaultName;
        }
    }

    private static final boolean getBooleanAttribute(final WadlAttributeName name, final boolean defaultValue,
            final XMLStreamReader reader) {
        final String value = reader.getAttributeValue(GLOBAL_NAMESPACE, name.getLocalName());
        if (value != null) {
            return Boolean.valueOf(value);
        } else {
            return defaultValue;
        }
    }

    private static final String getHRef(final XMLStreamReader reader) {
        final String value = reader.getAttributeValue(GLOBAL_NAMESPACE, WadlAttributeName.HREF.getLocalName());
        if (value != null) {
            return value;
        } else {
            throw new WadlRuntimeException(WadlAttributeName.HREF.getLocalName());
        }
    }

    private static final String getHRef(final XMLStreamReader reader, final String defaultName) {
        final String name = reader.getAttributeValue(GLOBAL_NAMESPACE, WadlAttributeName.HREF.getLocalName());
        if (name != null) {
            return name;
        } else {
            return defaultName;
        }
    }

    private static final String getId(final XMLStreamReader reader) {
        return reader.getAttributeValue(GLOBAL_NAMESPACE, WadlAttributeName.ID.getLocalName());
    }

    private static final String getLang(final XMLStreamReader reader, final String defaultName) {
        final String name = reader.getAttributeValue(GLOBAL_NAMESPACE, WadlAttributeName.LANG.getLocalName());
        if (name != null) {
            return name;
        } else {
            return defaultName;
        }
    }

    private static final String getName(final XMLStreamReader reader, final String defaultName) {
        final String name = reader.getAttributeValue(GLOBAL_NAMESPACE, WadlAttributeName.NAME.getLocalName());
        if (name != null) {
            return name;
        } else {
            return defaultName;
        }
    }

    private static final ParamStyle getParamStyle(final XMLStreamReader reader, final WadlAttributeName name) {
        return WadlSyntax.decodeParamStyle(reader.getAttributeValue(GLOBAL_NAMESPACE, name.getLocalName()));
    }

    private static final QName getQNameAttribute(final WadlAttributeName name, final QName defaultValue,
            final XMLStreamReader reader) {
        final String value = reader.getAttributeValue(GLOBAL_NAMESPACE, name.getLocalName());
        if (value != null) {
            return toQName(value, reader);
        } else {
            return defaultValue;
        }
    }

    private static final QName toQName(final String lexicalName, final XMLStreamReader reader) {
        final int colonIndex = lexicalName.indexOf(":");
        if (colonIndex >= 0) {
            final String localName = lexicalName.substring(colonIndex + 1);
            final String prefix = lexicalName.substring(0, colonIndex);
            final String namespaceURI = reader.getNamespaceURI(prefix);
            return new QName(namespaceURI, localName, prefix);
        } else {
            final String namespaceURI = reader.getNamespaceURI("");
            return new QName(namespaceURI, lexicalName);
        }
    }

    private static final String getQueryType(final XMLStreamReader reader, final String defaultName) {
        final String name = reader.getAttributeValue(GLOBAL_NAMESPACE, WadlAttributeName.QUERY_TYPE.getLocalName());
        if (name != null) {
            return name;
        } else {
            return defaultName;
        }
    }

    private static final String getStringAttribute(final WadlAttributeName name, final String defaultValue,
            final XMLStreamReader reader) {
        final String value = reader.getAttributeValue(GLOBAL_NAMESPACE, name.getLocalName());
        if (value != null) {
            return value;
        } else {
            return defaultValue;
        }
    }

    private static final List<String> getStringListType(final WadlAttributeName name, final XMLStreamReader reader) {
        final String value = reader.getAttributeValue(GLOBAL_NAMESPACE, name.getLocalName());
        if (value != null) {
            final String delims = "[ ]+";
            final String[] tokens = value.split(delims);
            final List<String> strings = new ArrayList<String>(tokens.length);
            for (int i = 0; i < tokens.length; i++) {
                strings.add(tokens[i]);
            }
            return strings;
        } else {
            return Collections.emptyList();
        }
    }

    private static final String getTitle(final XMLStreamReader reader, final String defaultName) {
        final String name = reader.getAttributeValue(GLOBAL_NAMESPACE, WadlAttributeName.TITLE.getLocalName());
        if (name != null) {
            return name;
        } else {
            return defaultName;
        }
    }

    private static final boolean match(final WadlElementName name, final XMLStreamReader reader) {
        return name.getLocalName().equals(reader.getLocalName());
    }

    /**
     * Reads XMI from an {@link InputStream}.
     *
     * @param stream
     *            The {@link InputStream}.
     * @return The parsed {@link Model}.
     */
    public static final Application readApplication(final InputStream stream) {
        if (stream == null) {
            throw new IllegalArgumentException("stream");
        }
        final XMLInputFactory factory = XMLInputFactory.newInstance();
        try {
            final XMLStreamReader reader = factory.createXMLStreamReader(stream);
            try {
                return readDocument(reader);
            } finally {
                reader.close();
            }
        } catch (final XMLStreamException e) {
            throw new WadlRuntimeException(e);
        }
    }

    public static final Application readApplication(final File file) throws FileNotFoundException {
        if (file == null) {
            throw new IllegalArgumentException("file");
        }
        final InputStream istream = new BufferedInputStream(new FileInputStream(file));
        try {
            return readApplication(istream);
        } finally {
            closeQuiet(istream);
        }
    }

    public static final Application readApplication(final String fileName) throws FileNotFoundException {
        if (fileName == null) {
            throw new IllegalArgumentException("fileName");
        }
        final InputStream istream = new BufferedInputStream(new FileInputStream(fileName));
        try {
            return readApplication(istream);
        } finally {
            closeQuiet(istream);
        }
    }

    private static final Application readApplication(final XMLStreamReader reader) throws XMLStreamException {
        assertName(WadlElementName.APPLICATION, reader);
        final List<Documentation> doc = new LinkedList<Documentation>();
        Grammars grammars = null;
        Resources resources = null;
        final List<ResourceType> resourceTypes = new LinkedList<ResourceType>();
        final List<Method> methods = new LinkedList<Method>();
        final List<Representation> representations = new LinkedList<Representation>();
        final List<Representation> faults = new LinkedList<Representation>();
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    if (match(WadlElementName.DOCUMENTATION, reader)) {
                        doc.add(readDocumentation(reader));
                        break;
                    } else if (match(WadlElementName.GRAMMARS, reader)) {
                        grammars = assertNotNull(readGrammars(reader));
                        break;
                    } else if (match(WadlElementName.RESOURCES, reader)) {
                        resources = assertNotNull(readResources(reader));
                        break;
                    } else {
                        throw new AssertionError(reader.getLocalName());
                    }
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(WadlElementName.APPLICATION, reader);
                    return new Application(doc, grammars, resources, resourceTypes, methods, representations, faults);
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

    private static final Application readDocument(final XMLStreamReader reader) {
        try {
            if (XMLStreamConstants.START_DOCUMENT == reader.getEventType()) {
                Application app = null;
                while (reader.hasNext()) {
                    reader.next();
                    switch (reader.getEventType()) {
                        case XMLStreamConstants.START_ELEMENT: {
                            if (match(WadlElementName.APPLICATION, reader)) {
                                app = readApplication(reader);
                                if (app == null) {
                                    throw new IllegalStateException();
                                }
                                break;
                            } else {
                                throw new AssertionError(reader.getLocalName());
                            }
                        }
                        case XMLStreamConstants.END_DOCUMENT: {
                            if (app == null) {
                                throw new IllegalStateException();
                            }
                            return app;
                        }
                        default: {
                            throw new AssertionError(reader.getEventType());
                        }
                    }
                }
                throw new AssertionError();
            } else {
                throw new AssertionError(reader.getLocalName());
            }
        } catch (final XMLStreamException e) {
            throw new WadlRuntimeException(e);
        }
    }

    private static final Documentation readDocumentation(final XMLStreamReader reader) throws XMLStreamException {
        assertName(WadlElementName.DOCUMENTATION, reader);
        final String title = getTitle(reader, null);
        final String lang = getLang(reader, null);
        final List<DmNode> content = new LinkedList<DmNode>();
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    content.add(readMixedElement(reader));
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(WadlElementName.DOCUMENTATION, reader);
                    return new Documentation(title, lang, content);
                }
                case XMLStreamConstants.CHARACTERS: {
                    content.add(new DmText(reader.getText()));
                    break;
                }
                case XMLStreamConstants.COMMENT: {
                    content.add(new DmComment(reader.getText()));
                    break;
                }
                case XMLStreamConstants.PROCESSING_INSTRUCTION: {
                    content.add(new DmProcessingInstruction(reader.getPITarget(), reader.getPIData()));
                    break;
                }
                default: {
                    throw new AssertionError(reader.getEventType());
                }
            }
        }
        throw new AssertionError();
    }

    private static final Option readOption(final XMLStreamReader reader) throws XMLStreamException {
        assertName(WadlElementName.OPTION, reader);
        final String value = getStringAttribute(WadlAttributeName.VALUE, null, reader);
        final List<Documentation> doc = new LinkedList<Documentation>();
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    if (match(WadlElementName.DOCUMENTATION, reader)) {
                        doc.add(readDocumentation(reader));
                    } else {
                        skipForeignNamespaceElement(reader);
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(WadlElementName.OPTION, reader);
                    return new Option(value, doc);
                }
                case XMLStreamConstants.CHARACTERS: {
                    break;
                }
                default: {
                    throw new AssertionError(reader.getEventType());
                }
            }
        }
        throw new AssertionError();
    }

    private static final Grammars readGrammars(final XMLStreamReader reader) throws XMLStreamException {
        assertName(WadlElementName.GRAMMARS, reader);
        final List<Documentation> documentation = new LinkedList<Documentation>();
        final List<Include> includes = new LinkedList<Include>();
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    if (match(WadlElementName.INCLUDE, reader)) {
                        includes.add(readInclude(reader));
                    } else {
                        skipForeignNamespaceElement(reader);
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(WadlElementName.GRAMMARS, reader);
                    return new Grammars(documentation, includes);
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

    private static final Include readInclude(final XMLStreamReader reader) throws XMLStreamException {
        assertName(WadlElementName.INCLUDE, reader);
        final String href = getHRef(reader);
        final List<Documentation> doc = new LinkedList<Documentation>();
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    if (match(WadlElementName.DOCUMENTATION, reader)) {
                        doc.add(readDocumentation(reader));
                    } else {
                        skipForeignNamespaceElement(reader);
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(WadlElementName.INCLUDE, reader);
                    return new Include(href, doc);
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

    private static final Method readMethod(final XMLStreamReader reader) throws XMLStreamException {
        assertName(WadlElementName.METHOD, reader);
        final String id = getId(reader);
        final String name = getName(reader, null);
        @SuppressWarnings("unused")
        final String href = getHRef(reader, null);
        final List<Documentation> doc = new LinkedList<Documentation>();
        Request request = null;
        final List<Response> responses = new LinkedList<Response>();
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    if (match(WadlElementName.REQUEST, reader)) {
                        request = readRequest(reader);
                    } else if (match(WadlElementName.RESPONSE, reader)) {
                        responses.add(readResponse(reader));
                    } else if (match(WadlElementName.DOCUMENTATION, reader)) {
                        doc.add(readDocumentation(reader));
                    } else {
                        skipForeignNamespaceElement(reader);
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(WadlElementName.METHOD, reader);
                    return new Method(id, name, doc, request, responses);
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

    private static final DmElement readMixedElement(final XMLStreamReader reader) throws XMLStreamException {
        final QName name = reader.getName();
        final List<DmNode> children = new LinkedList<DmNode>();
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    children.add(readMixedElement(reader));
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (name.equals(reader.getName())) {
                        return new DmElement(name, children);
                    } else {
                        throw new AssertionError(reader.getLocalName());
                    }
                }
                case XMLStreamConstants.CHARACTERS: {
                    // Assume that the element will coalesce text nodes.
                    children.add(new DmText(reader.getText()));
                    break;
                }
                case XMLStreamConstants.COMMENT: {
                    // Ignore for now.
                    break;
                }
                default: {
                    throw new AssertionError(reader.getEventType());
                }
            }
        }
        throw new AssertionError();
    }

    private static final Param readParam(final XMLStreamReader reader) throws XMLStreamException {
        assertName(WadlElementName.PARAM, reader);
        final String name = getName(reader, null);
        final ParamStyle style = getParamStyle(reader, WadlAttributeName.STYLE);
        final String id = getId(reader);
        final QName type = getQNameAttribute(WadlAttributeName.TYPE, new QName("http://www.w3.org/2001/XMLSchema",
                "string"), reader);
        final String defaultValue = getStringAttribute(WadlAttributeName.DEFAULT_VALUE, null, reader);
        final boolean required = getBooleanAttribute(WadlAttributeName.REQUIRED, false, reader);
        final boolean repeating = getBooleanAttribute(WadlAttributeName.REPEATING, false, reader);
        final String fixed = getStringAttribute(WadlAttributeName.FIXED, null, reader);
        final String path = getStringAttribute(WadlAttributeName.PATH, null, reader);
        final List<Documentation> doc = new LinkedList<Documentation>();
        final List<Option> options = new LinkedList<Option>();
        final Link link = null;
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    if (match(WadlElementName.DOCUMENTATION, reader)) {
                        doc.add(readDocumentation(reader));
                    } else if (match(WadlElementName.OPTION, reader)) {
                        options.add(readOption(reader));
                    } else {
                        skipForeignNamespaceElement(reader);
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(WadlElementName.PARAM, reader);
                    return new Param(name, style, id, type, defaultValue, required, repeating, fixed, path, doc,
                            options, link);
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

    private static final Representation readRepresentation(final XMLStreamReader reader) throws XMLStreamException {
        assertName(WadlElementName.REPRESENTATION, reader);
        final String id = getId(reader);
        final QName element = getQNameAttribute(WadlAttributeName.ELEMENT, null, reader);
        final String mediaType = getStringAttribute(WadlAttributeName.MEDIA_TYPE, null, reader);
        @SuppressWarnings("unused")
        final String href = getHRef(reader, null);
        final List<String> profiles = getStringListType(WadlAttributeName.PROFILE, reader);
        final List<Param> params = new LinkedList<Param>();
        final List<Documentation> documentation = new LinkedList<Documentation>();
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    skipForeignNamespaceElement(reader);
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(WadlElementName.REPRESENTATION, reader);
                    return new Representation(id, element, mediaType, profiles, documentation, params);
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

    private static final Request readRequest(final XMLStreamReader reader) throws XMLStreamException {
        assertName(WadlElementName.REQUEST, reader);
        final List<Param> params = new LinkedList<Param>();
        final List<Representation> representations = new LinkedList<Representation>();
        final List<Documentation> documentation = new LinkedList<Documentation>();
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    if (match(WadlElementName.REPRESENTATION, reader)) {
                        representations.add(readRepresentation(reader));
                    } else if (match(WadlElementName.PARAM, reader)) {
                        params.add(readParam(reader));
                    } else {
                        skipForeignNamespaceElement(reader);
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(WadlElementName.REQUEST, reader);
                    return new Request(documentation, params, representations);
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

    private static final Resource readResource(final XMLStreamReader reader) throws XMLStreamException {
        assertName(WadlElementName.RESOURCE, reader);
        final String id = getId(reader);
        final List<String> type = getStringListType(WadlAttributeName.TYPE, reader);
        final String queryType = getQueryType(reader, WadlSyntax.QUERY_TYPE_APPLICATION_X_WWW_FORM_URLENCODED);
        final String path = getStringAttribute(WadlAttributeName.PATH, null, reader);
        final String resourceClass = getStringAttribute(WadlAttributeName.RESOURCE_CLASS, null, reader);
        final List<Documentation> documentation = new LinkedList<Documentation>();
        final List<Param> params = new LinkedList<Param>();
        final List<Method> methods = new LinkedList<Method>();
        final List<Resource> resources = new LinkedList<Resource>();
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    if (match(WadlElementName.RESOURCE, reader)) {
                        resources.add(readResource(reader));
                    } else if (match(WadlElementName.DOCUMENTATION, reader)) {
                        documentation.add(readDocumentation(reader));
                    } else if (match(WadlElementName.PARAM, reader)) {
                        params.add(readParam(reader));
                    } else if (match(WadlElementName.METHOD, reader)) {
                        methods.add(readMethod(reader));
                    } else {
                        skipForeignNamespaceElement(reader);
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(WadlElementName.RESOURCE, reader);
                    return new Resource(id, type, queryType, path, documentation, params, methods, resources, resourceClass);
                }
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.COMMENT: {
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

    private static final Resources readResources(final XMLStreamReader reader) throws XMLStreamException {
        assertName(WadlElementName.RESOURCES, reader);
        final String base = getBase(reader, null);
        final List<Documentation> documentation = new LinkedList<Documentation>();
        final List<Resource> resources = new LinkedList<Resource>();
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    if (match(WadlElementName.RESOURCE, reader)) {
                        resources.add(readResource(reader));
                    } else if (match(WadlElementName.DOCUMENTATION, reader)) {
                        documentation.add(readDocumentation(reader));
                    } else {
                        skipForeignNamespaceElement(reader);
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(WadlElementName.RESOURCES, reader);
                    return new Resources(base, documentation, resources);
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

    private static final Response readResponse(final XMLStreamReader reader) throws XMLStreamException {
        assertName(WadlElementName.RESPONSE, reader);
        final List<String> statusCodes = new LinkedList<String>();
        final List<Param> params = new LinkedList<Param>();
        final List<Representation> representations = new LinkedList<Representation>();
        final List<Documentation> documentation = new LinkedList<Documentation>();
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    if (match(WadlElementName.REPRESENTATION, reader)) {
                        representations.add(readRepresentation(reader));
                    } else if (match(WadlElementName.PARAM, reader)) {
                        params.add(readParam(reader));
                    } else if (match(WadlElementName.DOCUMENTATION, reader)) {
                        documentation.add(readDocumentation(reader));
                    } else {
                        skipForeignNamespaceElement(reader);
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    assertName(WadlElementName.RESPONSE, reader);
                    return new Response(statusCodes, documentation, params, representations);
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

    /**
     * Skips (recursively) over the element in question. Also useful during development.
     *
     * @param reader
     *            The StAX {@link XMLStreamReader}.
     */
    private static final void skipElementReally(final XMLStreamReader reader) throws XMLStreamException {
        final String localName = reader.getLocalName();
        while (reader.hasNext()) {
            reader.next();
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    skipElementReally(reader);
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (localName.equals(reader.getLocalName())) {
                        return;
                    } else {
                        throw new AssertionError(reader.getLocalName());
                    }
                }
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.COMMENT: {
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

    private static final void skipForeignNamespaceElement(final XMLStreamReader reader) throws XMLStreamException {
        final String namespaceURI = reader.getNamespaceURI();
        if (!WadlSyntax.NAMESPACE.equals(namespaceURI)) {
            skipElementReally(reader);
        } else {
            throw new AssertionError(reader.getName());
        }
    }
}
