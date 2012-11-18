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

package org.slc.sli.modeling.waudit;

import static java.util.Arrays.asList;
import static org.slc.sli.modeling.wadl.helpers.WadlHelper.computeId;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.namespace.QName;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import org.slc.sli.modeling.psm.PsmConfig;
import org.slc.sli.modeling.psm.PsmConfigReader;
import org.slc.sli.modeling.psm.PsmDocument;
import org.slc.sli.modeling.rest.Application;
import org.slc.sli.modeling.rest.Method;
import org.slc.sli.modeling.rest.Representation;
import org.slc.sli.modeling.rest.Request;
import org.slc.sli.modeling.rest.Resource;
import org.slc.sli.modeling.rest.Resources;
import org.slc.sli.modeling.rest.Response;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.index.DefaultModelIndex;
import org.slc.sli.modeling.uml.index.ModelIndex;
import org.slc.sli.modeling.wadl.reader.WadlReader;
import org.slc.sli.modeling.wadl.writer.WadlWriter;
import org.slc.sli.modeling.xmi.reader.XmiReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class WadlAudit {

    private static final Logger LOG = LoggerFactory.getLogger(WadlAudit.class);

    private static final String NAMESPACE_WWW_SLCEDU_ORG_API_V1 = "http://www.slcedu.org/api/v1";
    private static final List<String> ARGUMENT_HELP = asList("h", "?");
    private static final String ARGUMENT_DOCUMENT_FILE = "documentFile";
    private static final String ARGUMENT_WADL = "wadlFile";
    private static final String ARGUMENT_XMI = "xmiFile";
    private static final String ARGUMENT_OUT_FILE = "outFile";
    private static final String ARGUMENT_OUT_FOLDER = "outFolder";

    // private static final String ELEMENT_PREFIX = "sli";

    private static final Application doApplication(final Application app, final WadlAuditConfig config,
            final Map<String, PsmDocument<Type>> topLevel, final boolean fixup) {
        final Resources resources = doResources(app.getResources(), app, config, topLevel, fixup);
        return new Application(app.getDocumentation(), app.getGrammars(), resources, app.getResourceTypes(),
                app.getMethods(), app.getRepresentations(), app.getFaults());
    }

    private static final Method doMethod(final Method method, final Resource resource, final Resources resources,
            final Application app, final Stack<Resource> ancestors, final WadlAuditConfig config,
            final Map<String, PsmDocument<Type>> topLevel, final boolean fixup) {

        final String id = computeId(method, resource, resources, app, ancestors);
        final Request request = doRequest(method.getRequest(), method, resource, resources, app, ancestors, config,
                topLevel, fixup);
        final List<Response> responseList = doResponseList(method.getResponses(), method, resource, resources, app,
                ancestors, config, topLevel, fixup);
        return new Method(id, method.getVerb(), method.getDocumentation(), request, responseList);
    }

    private static final List<Method> doMethodList(final List<Method> methods, final Resource resource,
            final Resources resources, final Application app, final Stack<Resource> ancestors,
            final WadlAuditConfig config, final Map<String, PsmDocument<Type>> topLevel, final boolean fixup) {
        final List<Method> result = new LinkedList<Method>();
        for (final Method method : methods) {
            result.add(doMethod(method, resource, resources, app, ancestors, config, topLevel, fixup));
        }
        return Collections.unmodifiableList(result);
    }

    private static final List<Representation> doRepresentationList(final List<Representation> representations,
            final Request request, final Method method, final Resource resource, final Resources resources,
            final Application app, final Stack<Resource> ancestors, final WadlAuditConfig config,
            final Map<String, PsmDocument<Type>> topLevel, final boolean fixup) {
        final List<Representation> result = new LinkedList<Representation>();
        for (final Representation representation : representations) {
            result.add(transformRequestRepresentation(representation, request, method, resource, resources, app,
                    ancestors, config, topLevel, fixup));
        }
        return Collections.unmodifiableList(result);
    }

    private static final List<Representation> doRepresentationList(final List<Representation> representations,
            final Response response, final Method method, final Resource resource, final Resources resources,
            final Application app, final Stack<Resource> ancestors, final WadlAuditConfig config,
            final Map<String, PsmDocument<Type>> topLevel, final boolean fixup) {
        final List<Representation> result = new LinkedList<Representation>();
        for (final Representation representation : representations) {
            result.add(transformResponseRepresentation(representation, response, method, resource, resources, app,
                    ancestors, config, topLevel, fixup));
        }
        return Collections.unmodifiableList(result);
    }

    private static final Request doRequest(final Request request, final Method method, final Resource resource,
            final Resources resources, final Application app, final Stack<Resource> ancestors,
            final WadlAuditConfig config, final Map<String, PsmDocument<Type>> topLevel, final boolean fixup) {
        if (request == null) {
            return null;
        }
        final List<Representation> representationList = doRepresentationList(request.getRepresentations(), request,
                method, resource, resources, app, ancestors, config, topLevel, fixup);
        return new Request(request.getDocumentation(), request.getParams(), representationList);
    }

    private static final Resource doResource(final Resource resource, final Resources resources, final Application app,
            final Stack<Resource> ancestors, final WadlAuditConfig config,
            final Map<String, PsmDocument<Type>> topLevel, final boolean fixup) {

        final List<Method> methods = doMethodList(resource.getMethods(), resource, resources, app, ancestors, config,
                topLevel, fixup);

        ancestors.push(resource);
        try {
            final List<Resource> childResourceList = doResourceList(resource.getResources(), resources, app, ancestors,
                    config, topLevel, fixup);
            return new Resource(resource.getId(), resource.getType(), resource.getQueryType(), resource.getPath(),
                    resource.getDocumentation(), resource.getParams(), methods, childResourceList, resource.getResourceClass());
        } finally {
            ancestors.pop();
        }
    }

    private static final List<Resource> doResourceList(final List<Resource> resourceList, final Resources resources,
            final Application app, final Stack<Resource> ancestors, final WadlAuditConfig config,
            final Map<String, PsmDocument<Type>> topLevel, final boolean fixup) {
        final List<Resource> result = new LinkedList<Resource>();
        for (final Resource resource : resourceList) {
            result.add(doResource(resource, resources, app, ancestors, config, topLevel, fixup));
        }
        return Collections.unmodifiableList(result);
    }

    private static final Resources doResources(final Resources resources, final Application app,
            final WadlAuditConfig config, final Map<String, PsmDocument<Type>> topLevel, final boolean fixup) {
        // Keep track of the ancestor resources.
        final Stack<Resource> ancestors = new Stack<Resource>();
        final List<Resource> resourceList = doResourceList(resources.getResources(), resources, app, ancestors, config,
                topLevel, fixup);
        return new Resources(resources.getBase(), resources.getDocumentation(), resourceList);
    }

    private static final Response doResponse(final Response response, final Method method, final Resource resource,
            final Resources resources, final Application app, final Stack<Resource> ancestors,
            final WadlAuditConfig config, final Map<String, PsmDocument<Type>> topLevel, final boolean fixup) {
        final List<Representation> representationList = doRepresentationList(response.getRepresentations(), response,
                method, resource, resources, app, ancestors, config, topLevel, fixup);
        return new Response(response.getStatusCodes(), response.getDocumentation(), response.getParams(),
                representationList);
    }

    private static final List<Response> doResponseList(final List<Response> responses, final Method method,
            final Resource resource, final Resources resources, final Application app, final Stack<Resource> ancestors,
            final WadlAuditConfig config, final Map<String, PsmDocument<Type>> topLevel, final boolean fixup) {
        final List<Response> result = new LinkedList<Response>();
        for (final Response response : responses) {
            result.add(doResponse(response, method, resource, resources, app, ancestors, config, topLevel, fixup));
        }
        return Collections.unmodifiableList(result);
    }

    public static void main(final String[] args) {
        
    	final OptionParser parser = new OptionParser();
        final OptionSpec<?> helpSpec = parser.acceptsAll(ARGUMENT_HELP, "Show help");
        final OptionSpec<File> documentFileSpec = optionSpec(parser, ARGUMENT_DOCUMENT_FILE, "Domain file", File.class);
        final OptionSpec<File> wadlFileSpec = optionSpec(parser, ARGUMENT_WADL, "WADL file", File.class);
        final OptionSpec<File> xmiFileSpec = optionSpec(parser, ARGUMENT_XMI, "XMI file", File.class);
        final OptionSpec<String> outFileSpec = optionSpec(parser, ARGUMENT_OUT_FILE, "Output file", String.class);
        final OptionSpec<File> outFolderSpec = optionSpec(parser, ARGUMENT_OUT_FOLDER, "Output folder", File.class);
        try {
            final OptionSet options = parser.parse(args);
            if (options.hasArgument(helpSpec)) {
                try {
                    parser.printHelpOn(System.out);
                } catch (final IOException e) {
                    throw new WadlAuditRuntimeException(e);
                }
            } else {
                try {
                    // This could be made more precise and configurable.
                    final boolean fixup = true;

                    final File xmiFile = options.valueOf(xmiFileSpec);
                    // The UML model provides the types and enables us to walk the URIs.
                    final ModelIndex model = new DefaultModelIndex(XmiReader.readModel(xmiFile));

                    // The document file provides the top-level elements.
                    final File documentFile = options.valueOf(documentFileSpec);
                    final PsmConfig<Type> psmConfig = PsmConfigReader.readConfig(documentFile, model);
                    final Map<String, PsmDocument<Type>> topLevel = new HashMap<String, PsmDocument<Type>>();
                    for (final PsmDocument<Type> document : psmConfig.getDocuments()) {
                        // System.out.println(document.getPluralResourceName());
                        topLevel.put(document.getGraphAssociationEndName().getName(), document);
                    }

                    final Map<String, QName> elementNames = computeElementNames(psmConfig,
                            NAMESPACE_WWW_SLCEDU_ORG_API_V1);

                    final WadlAuditConfig config = new WadlAuditConfig("", NAMESPACE_WWW_SLCEDU_ORG_API_V1, model,
                            elementNames);
                    final Map<String, String> prefixMappings = new HashMap<String, String>();
                    if (config.getNamespaceURI().trim().length() > 0) {
                        prefixMappings.put(config.getPrefix(), config.getNamespaceURI());
                    }

                    final File wadlFile = options.valueOf(wadlFileSpec);
                    final Application app = doApplication(WadlReader.readApplication(wadlFile), config, topLevel, fixup);

                    final File outFolder = options.valueOf(outFolderSpec);
                    final String outFile = options.valueOf(outFileSpec);
                    final File outLocation = new File(outFolder, outFile);
                    WadlWriter.writeDocument(app, prefixMappings, outLocation);

                } catch (final FileNotFoundException e) {
                    LOG.warn(e.getMessage());
                }
            }
        } catch (final OptionException e) {
            // Caused by illegal arguments.
            LOG.warn(e.getMessage());
        }

        try {
            @SuppressWarnings("unused")
            final ModelIndex model = new DefaultModelIndex(XmiReader.readModel("SLI.xmi"));
        } catch (final FileNotFoundException e) {
            LOG.warn(e.getMessage());
        }
    }

    /**
     * Compute the mapping from types to element names using the platform specific model.
     */
    private static final Map<String, QName> computeElementNames(final PsmConfig<Type> psm, final String namespaceUri) {
        if (namespaceUri == null) {
            throw new IllegalArgumentException("namespaceUri");
        }
        final Map<String, QName> elementNames = new HashMap<String, QName>();
        elementNames.put("Custom", new QName(namespaceUri, "custom"));
        elementNames.put("Aggregations", new QName(namespaceUri, "aggregations"));
        elementNames.put("CalculatedValues", new QName(namespaceUri, "calculatedValues"));
        elementNames.put("Home", null);
        elementNames.put("Unknown", null);
        for (final PsmDocument<Type> document : psm.getDocuments()) {
            // System.out.println(document.getPluralResourceName());
            final String typeName = document.getType().getName();
            elementNames.put(typeName, new QName(namespaceUri, document.getSingularResourceName().getName()));
        }
        return Collections.unmodifiableMap(elementNames);
    }

    private static final <T> OptionSpec<T> optionSpec(final OptionParser parser, final String option,
            final String description, final Class<T> argumentType) {
        return parser.accepts(option, description).withRequiredArg().ofType(argumentType).required();
    }

    private static final Representation transformRequestRepresentation(final Representation representation,
            final Request request, final Method method, final Resource resource, final Resources resources,
            final Application app, final Stack<Resource> ancestors, final WadlAuditConfig config,
            final Map<String, PsmDocument<Type>> topLevel, final boolean fixup) {

        // The element name for the request can be computed and changed.
        final QName elementName = fixup ? WadlExpert.computeElementNameForRequest(representation, request, method,
                resource, ancestors, config, topLevel) : representation.getElementName();

        return new Representation(representation.getId(), elementName, representation.getMediaType(),
                representation.getProfiles(), representation.getDocumentation(), representation.getParams());
    }

    private static final Representation transformResponseRepresentation(final Representation representation,
            final Response response, final Method method, final Resource resource, final Resources resources,
            final Application app, final Stack<Resource> ancestors, final WadlAuditConfig config,
            final Map<String, PsmDocument<Type>> topLevel, final boolean fixup) {

        // The element name for the response can be computed and changed.
        final QName elementName = fixup ? WadlExpert.computeElementNameForResponse(representation, response, method,
                resource, ancestors, config, topLevel) : representation.getElementName();

        return new Representation(representation.getId(), elementName, representation.getMediaType(),
                representation.getProfiles(), representation.getDocumentation(), representation.getParams());
    }
}
