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


package org.slc.sli.modeling.wadl.writer;

import static org.slc.sli.modeling.wadl.WadlSyntax.encodeParamStyle;
import static org.slc.sli.modeling.wadl.WadlSyntax.encodeStringList;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.io.IOUtils;
import org.slc.sli.modeling.rest.Application;
import org.slc.sli.modeling.rest.Documentation;
import org.slc.sli.modeling.rest.Grammars;
import org.slc.sli.modeling.rest.Include;
import org.slc.sli.modeling.rest.Method;
import org.slc.sli.modeling.rest.Option;
import org.slc.sli.modeling.rest.Param;
import org.slc.sli.modeling.rest.Representation;
import org.slc.sli.modeling.rest.Request;
import org.slc.sli.modeling.rest.Resource;
import org.slc.sli.modeling.rest.ResourceType;
import org.slc.sli.modeling.rest.Resources;
import org.slc.sli.modeling.rest.Response;
import org.slc.sli.modeling.rest.WadlElement;
import org.slc.sli.modeling.wadl.WadlAttributeName;
import org.slc.sli.modeling.wadl.WadlElementName;
import org.slc.sli.modeling.wadl.WadlRuntimeException;
import org.slc.sli.modeling.wadl.WadlSyntax;
import org.slc.sli.modeling.xdm.DmComment;
import org.slc.sli.modeling.xdm.DmElement;
import org.slc.sli.modeling.xdm.DmNode;
import org.slc.sli.modeling.xdm.DmProcessingInstruction;
import org.slc.sli.modeling.xdm.DmText;
import org.slc.sli.modeling.xml.IndentingXMLStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Writes a WADL {@link Application} to a file (by name) or {@link OutputStream}.
 */
public final class WadlWriter {

    private static final Logger LOG = LoggerFactory.getLogger(WadlWriter.class);

    private static final String SCHEMA_INSTANCE_NS = "http://www.w3.org/2001/XMLSchema-instance";

    private static final String SCHEMA_LOCATION_VALUE = "http://wadl.dev.java.net/2009/02 http://www.w3.org/Submission/wadl/wadl.xsd";
    private static final String SCHEMA_NS = "http://www.w3.org/2001/XMLSchema";
    private static final String WADL_PREFIX = "wadl";

    // private static final String ELEMENT_NS = "http://www.slcedu.org/api/v1";
    // private static final String ELEMENT_PREFIX = "sli";

    private static final void closeQuiet(final Closeable closeable) {
        IOUtils.closeQuietly(closeable);
    }

    private static final String toLexicalForm(final QName name, final XMLStreamWriter xsw) throws XMLStreamException {
        final String namespaceURI = name.getNamespaceURI();
        if (namespaceURI.length() != 0) {
            final String prefix = xsw.getPrefix(namespaceURI);
            if (prefix == null) {
                throw new IllegalArgumentException(name.toString());
            }
            if (prefix.length() != 0) {
                return prefix.concat(":").concat(name.getLocalPart());
            } else {
                return name.getLocalPart();
            }
        } else {
            return name.getLocalPart();
        }
    }

    private static final void writeApplication(final Application app, final Map<String, String> prefixMappings,
            final XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeStartElement(WADL_PREFIX, WadlElementName.APPLICATION.getLocalName(), WadlSyntax.NAMESPACE);
        try {
            xsw.writeNamespace(WADL_PREFIX, WadlSyntax.NAMESPACE);
            xsw.writeNamespace("xsi", SCHEMA_INSTANCE_NS);
            xsw.writeAttribute("xsi", SCHEMA_INSTANCE_NS, "schemaLocation", SCHEMA_LOCATION_VALUE);
            xsw.writeNamespace("xs", SCHEMA_NS);
            for (final Map.Entry<String, String> prefix : prefixMappings.entrySet()) {
                xsw.writeNamespace(prefix.getKey(), prefix.getValue());
            }
            writeDocumentation(app, xsw);
            writeGrammars(app.getGrammars(), xsw);
            writeResources(app.getResources(), xsw);
            for (final ResourceType resourceType : app.getResourceTypes()) {
                writeResourceType(resourceType, xsw);
            }
            for (final Method method : app.getMethods()) {
                writeMethod(method, xsw);
            }
            for (final Representation representation : app.getRepresentations()) {
                writeRepresentation(representation, xsw);
            }
            for (final Representation representation : app.getFaults()) {
                writeFault(representation, xsw);
            }
        } finally {
            xsw.writeEndElement();
        }
    }

    private static final void writeComment(final DmComment comment, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeComment(comment.getStringValue());
    }

    private static final void writeContents(final List<DmNode> nodes, final XMLStreamWriter xsw)
            throws XMLStreamException {
        for (final DmNode node : nodes) {
            if (node instanceof DmElement) {
                writeElement((DmElement) node, xsw);
            } else if (node instanceof DmText) {
                writeText((DmText) node, xsw);
            } else if (node instanceof DmComment) {
                writeComment((DmComment) node, xsw);
            } else if (node instanceof DmProcessingInstruction) {
                writeProcessingInstruction((DmProcessingInstruction) node, xsw);
            } else {
                throw new AssertionError(node);
            }
        }
    }

    public static final void writeDocument(final Application app, final Map<String, String> prefixMappings,
            final OutputStream outstream) {
        final XMLOutputFactory xof = XMLOutputFactory.newInstance();
        try {
            final XMLStreamWriter xsw = new IndentingXMLStreamWriter(xof.createXMLStreamWriter(outstream, "UTF-8"));
            xsw.writeStartDocument("UTF-8", "1.0");
            try {
                writeApplication(app, prefixMappings, xsw);
            } finally {
                xsw.writeEndDocument();
            }
            xsw.flush();
        } catch (final XMLStreamException e) {
            throw new WadlRuntimeException(e);
        }
    }

    public static final void writeDocument(final Application app, final Map<String, String> prefixMappings,
            final String fileName) {
        try {
            final OutputStream outstream = new BufferedOutputStream(new FileOutputStream(fileName));
            try {
                writeDocument(app, prefixMappings, outstream);
            } finally {
                closeQuiet(outstream);
            }
        } catch (final FileNotFoundException e) {
            LOG.warn(e.getMessage());
        }
    }

    public static final void writeDocument(final Application app, final Map<String, String> prefixMappings,
            final File file) {
        if (file == null) {
            throw new IllegalArgumentException("file");
        }
        try {
            final OutputStream outstream = new BufferedOutputStream(new FileOutputStream(file));
            try {
                writeDocument(app, prefixMappings, outstream);
            } finally {
                closeQuiet(outstream);
            }
        } catch (final FileNotFoundException e) {
            LOG.warn(e.getMessage());
        }
    }

    private static final void writeDocumentation(final Documentation doc, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(WADL_PREFIX, WadlElementName.DOCUMENTATION.getLocalName(), WadlSyntax.NAMESPACE);
        try {
            if (doc.getLanguage() != null) {
                xsw.writeAttribute("xml", "http://www.w3.org/XML/1998/namespace", "lang", doc.getLanguage());
            }
            if (doc.getTitle() != null) {
                xsw.writeAttribute(WadlAttributeName.TITLE.getLocalName(), doc.getTitle());
            }
            writeContents(doc.getContents(), xsw);
        } finally {
            xsw.writeEndElement();
        }
    }

    private static final void writeDocumentation(final List<Documentation> documentList, final XMLStreamWriter xsw)
            throws XMLStreamException {
        for (final Documentation doc : documentList) {
            writeDocumentation(doc, xsw);
        }
    }

    private static final void writeDocumentation(final WadlElement element, final XMLStreamWriter xsw)
            throws XMLStreamException {
        if (element == null) {
            throw new IllegalArgumentException("element");
        }
        writeDocumentation(element.getDocumentation(), xsw);
    }

    private static final void writeElement(final DmElement element, final XMLStreamWriter xsw)
            throws XMLStreamException {
        final QName name = element.getName();
        xsw.writeStartElement(name.getPrefix(), name.getLocalPart(), name.getNamespaceURI());
        try {
            writeContents(element.getChildAxis(), xsw);
        } finally {
            xsw.writeEndElement();
        }
    }

    private static final void writeFault(final Representation representation, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(WADL_PREFIX, WadlElementName.FAULT.getLocalName(), WadlSyntax.NAMESPACE);
        try {
            xsw.writeAttribute(WadlAttributeName.MEDIA_TYPE.getLocalName(), representation.getMediaType());
            if (representation.getId() != null) {
                xsw.writeAttribute(WadlAttributeName.ID.getLocalName(), representation.getId());
            }
            final QName element = representation.getElementName();
            xsw.writeAttribute(WadlAttributeName.ELEMENT.getLocalName(), toLexicalForm(element, xsw));
            writeDocumentation(representation, xsw);
        } finally {
            xsw.writeEndElement();
        }
    }

    private static final void writeGrammars(final Grammars resources, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(WADL_PREFIX, WadlElementName.GRAMMARS.getLocalName(), WadlSyntax.NAMESPACE);
        try {
            writeDocumentation(resources, xsw);
            for (final Include resource : resources.getIncludes()) {
                writeInclude(resource, xsw);
            }
        } finally {
            xsw.writeEndElement();
        }
    }

    private static final void writeInclude(final Include include, final XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeStartElement(WADL_PREFIX, WadlElementName.INCLUDE.getLocalName(), WadlSyntax.NAMESPACE);
        try {
            xsw.writeAttribute(WadlAttributeName.HREF.getLocalName(), include.getHref());
            writeDocumentation(include, xsw);
        } finally {
            xsw.writeEndElement();
        }
    }

    private static final void writeMethod(final Method method, final XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeStartElement(WADL_PREFIX, WadlElementName.METHOD.getLocalName(), WadlSyntax.NAMESPACE);
        try {
            xsw.writeAttribute(WadlAttributeName.NAME.getLocalName(), method.getVerb());
            xsw.writeAttribute(WadlAttributeName.ID.getLocalName(), method.getId());
            writeDocumentation(method, xsw);
            if (method.getRequest() != null) {
                writeRequest(method.getRequest(), xsw);
            }
            for (final Response response : method.getResponses()) {
                writeResponse(response, xsw);
            }
        } finally {
            xsw.writeEndElement();
        }
    }

    private static final void writeOption(final Option option, final XMLStreamWriter xsw) throws XMLStreamException {
        if (option == null) {
            throw new IllegalArgumentException("option");
        }
        xsw.writeStartElement(WADL_PREFIX, WadlElementName.OPTION.getLocalName(), WadlSyntax.NAMESPACE);
        try {
            xsw.writeAttribute(WadlAttributeName.VALUE.getLocalName(), option.getValue());
            writeDocumentation(option, xsw);
        } finally {
            xsw.writeEndElement();
        }
    }

    private static final void writeParam(final Param param, final XMLStreamWriter xsw) throws XMLStreamException {
        if (param == null) {
            throw new IllegalArgumentException("param");
        }
        xsw.writeStartElement(WADL_PREFIX, WadlElementName.PARAM.getLocalName(), WadlSyntax.NAMESPACE);
        try {
            xsw.writeAttribute(WadlAttributeName.NAME.getLocalName(), param.getName());
            xsw.writeAttribute(WadlAttributeName.STYLE.getLocalName(), encodeParamStyle(param.getStyle()));
            if (param.getId() != null) {
                xsw.writeAttribute(WadlAttributeName.ID.getLocalName(), param.getId());
            }
            final QName type = param.getType();
            xsw.writeAttribute(WadlAttributeName.TYPE.getLocalName(), toLexicalForm(type, xsw));
            if (param.getDefault() != null) {
                xsw.writeAttribute(WadlAttributeName.DEFAULT_VALUE.getLocalName(), param.getDefault());
            }
            if (param.getRequired()) {
                xsw.writeAttribute(WadlAttributeName.REQUIRED.getLocalName(), Boolean.toString(param.getRequired()));
            }
            if (param.getRepeating()) {
                xsw.writeAttribute(WadlAttributeName.REPEATING.getLocalName(), Boolean.toString(param.getRepeating()));
            }
            if (param.getFixed() != null) {
                xsw.writeAttribute(WadlAttributeName.FIXED.getLocalName(), param.getFixed());
            }
            if (param.getPath() != null) {
                xsw.writeAttribute(WadlAttributeName.PATH.getLocalName(), param.getPath());
            }
            writeDocumentation(param, xsw);
            for (final Option option : param.getOptions()) {
                writeOption(option, xsw);
            }
        } finally {
            xsw.writeEndElement();
        }
    }

    private static final void writeProcessingInstruction(final DmProcessingInstruction pi, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeProcessingInstruction(pi.getName().getLocalPart(), pi.getStringValue());
    }

    private static final void writeRepresentation(final Representation representation, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(WADL_PREFIX, WadlElementName.REPRESENTATION.getLocalName(), WadlSyntax.NAMESPACE);
        try {
            xsw.writeAttribute(WadlAttributeName.MEDIA_TYPE.getLocalName(), representation.getMediaType());
            if (representation.getId() != null) {
                xsw.writeAttribute(WadlAttributeName.ID.getLocalName(), representation.getId());
            }
            final QName element = representation.getElementName();
            if (element != null) {
                xsw.writeAttribute(WadlAttributeName.ELEMENT.getLocalName(), toLexicalForm(element, xsw));
            }
            writeDocumentation(representation, xsw);
        } finally {
            xsw.writeEndElement();
        }
    }

    private static final void writeRequest(final Request request, final XMLStreamWriter xsw) throws XMLStreamException {
        if (request == null) {
            throw new IllegalArgumentException("request");
        }
        xsw.writeStartElement(WADL_PREFIX, WadlElementName.REQUEST.getLocalName(), WadlSyntax.NAMESPACE);
        try {
            writeDocumentation(request, xsw);
            for (final Param param : request.getParams()) {
                writeParam(param, xsw);
            }
            for (final Representation representation : request.getRepresentations()) {
                writeRepresentation(representation, xsw);
            }
        } finally {
            xsw.writeEndElement();
        }
    }

    private static final void writeResource(final Resource resource, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(WADL_PREFIX, WadlElementName.RESOURCE.getLocalName(), WadlSyntax.NAMESPACE);
        try {
            if (resource.getId() != null) {
                xsw.writeAttribute(WadlAttributeName.ID.getLocalName(), resource.getId());
            }
            if (!resource.getType().isEmpty()) {
                xsw.writeAttribute(WadlAttributeName.TYPE.getLocalName(), encodeStringList(resource.getType()));
            }
            final String queryType = resource.getQueryType();
            if (!WadlSyntax.QUERY_TYPE_APPLICATION_X_WWW_FORM_URLENCODED.equals(queryType)) {
                xsw.writeAttribute(WadlAttributeName.QUERY_TYPE.getLocalName(), queryType);
            }
            xsw.writeAttribute(WadlAttributeName.PATH.getLocalName(), resource.getPath());
            writeDocumentation(resource, xsw);
            for (final Param param : resource.getParams()) {
                writeParam(param, xsw);
            }
            for (final Method method : resource.getMethods()) {
                writeMethod(method, xsw);
            }
            for (final Resource nestedResource : resource.getResources()) {
                writeResource(nestedResource, xsw);
            }
        } finally {
            xsw.writeEndElement();
        }
    }

    private static final void writeResources(final Resources resources, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(WADL_PREFIX, WadlElementName.RESOURCES.getLocalName(), WadlSyntax.NAMESPACE);
        try {
            xsw.writeAttribute(WadlAttributeName.BASE.getLocalName(), resources.getBase());
            writeDocumentation(resources, xsw);
            for (final Resource resource : resources.getResources()) {
                writeResource(resource, xsw);
            }
        } finally {
            xsw.writeEndElement();
        }
    }

    private static final void writeResourceType(final ResourceType resourceType, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(WADL_PREFIX, WadlElementName.RESOURCE_TYPE.getLocalName(), WadlSyntax.NAMESPACE);
        try {
            xsw.writeAttribute(WadlAttributeName.ID.getLocalName(), resourceType.getId());
            writeDocumentation(resourceType, xsw);
        } finally {
            xsw.writeEndElement();
        }
    }

    private static final void writeResponse(final Response response, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(WADL_PREFIX, WadlElementName.RESPONSE.getLocalName(), WadlSyntax.NAMESPACE);
        try {
            writeDocumentation(response, xsw);
            for (final Representation representation : response.getRepresentations()) {
                writeRepresentation(representation, xsw);
            }
        } finally {
            xsw.writeEndElement();
        }
    }

    private static final void writeText(final DmText text, final XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeCharacters(text.getStringValue());
    }
}
