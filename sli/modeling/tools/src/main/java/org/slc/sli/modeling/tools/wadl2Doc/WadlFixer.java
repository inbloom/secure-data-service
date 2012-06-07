package org.slc.sli.modeling.tools.wadl2Doc;

import static org.slc.sli.modeling.wadl.helpers.WadlHelper.computeId;

import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.namespace.QName;

import org.slc.sli.modeling.rest.Application;
import org.slc.sli.modeling.rest.Method;
import org.slc.sli.modeling.rest.Representation;
import org.slc.sli.modeling.rest.Request;
import org.slc.sli.modeling.rest.Resource;
import org.slc.sli.modeling.rest.Resources;
import org.slc.sli.modeling.rest.Response;
import org.slc.sli.modeling.uml.index.DefaultModelIndex;
import org.slc.sli.modeling.uml.index.ModelIndex;
import org.slc.sli.modeling.wadl.reader.WadlReader;
import org.slc.sli.modeling.wadl.writer.WadlWriter;
import org.slc.sli.modeling.xmi.reader.XmiReader;

public final class WadlFixer {

    // private static final String ELEMENT_PREFIX = "sli";

    private static final Application fixApplication(final Application app, final WadlFixConfig config) {
        final Resources resources = fixResources(app.getResources(), app, config);
        return new Application(app.getDocumentation(), app.getGrammars(), resources, app.getResourceTypes(),
                app.getMethods(), app.getRepresentations(), app.getFaults());
    }

    private static final Method fixMethod(final Method method, final Resource resource, final Resources resources,
            final Application app, final Stack<Resource> ancestors, final WadlFixConfig config) {

        final String id = computeId(method, resource, resources, app, ancestors);
        final Request request = fixRequest(method.getRequest(), method, resource, resources, app, ancestors, config);
        final List<Response> responseList = fixResponseList(method.getResponses(), method, resource, resources, app,
                ancestors, config);
        return new Method(id, method.getName(), method.getDocumentation(), request, responseList);
    }

    private static final List<Method> fixMethodList(final List<Method> methods, final Resource resource,
            final Resources resources, final Application app, final Stack<Resource> ancestors,
            final WadlFixConfig config) {
        final List<Method> result = new LinkedList<Method>();
        for (final Method method : methods) {
            result.add(fixMethod(method, resource, resources, app, ancestors, config));
        }
        return Collections.unmodifiableList(result);
    }

    private static final Representation fixRepresentation(final Representation representation, final Request request,
            final Method method, final Resource resource, final Resources resources, final Application app,
            final Stack<Resource> ancestors, final WadlFixConfig config) {

        final QName element = getRepresentationElement(representation, request, method, resource, ancestors, config);
        return new Representation(representation.getId(), element, representation.getMediaType(),
                representation.getProfiles(), representation.getDocumentation(), representation.getParams());
    }

    private static final Representation fixRepresentation(final Representation representation, final Response response,
            final Method method, final Resource resource, final Resources resources, final Application app,
            final Stack<Resource> ancestors, final WadlFixConfig config) {

        final QName element = getRepresentationElement(representation, response, method, resource, ancestors, config);
        return new Representation(representation.getId(), element, representation.getMediaType(),
                representation.getProfiles(), representation.getDocumentation(), representation.getParams());
    }

    private static final List<Representation> fixRepresentationList(final List<Representation> representations,
            final Request request, final Method method, final Resource resource, final Resources resources,
            final Application app, final Stack<Resource> ancestors, final WadlFixConfig config) {
        final List<Representation> result = new LinkedList<Representation>();
        for (final Representation representation : representations) {
            result.add(fixRepresentation(representation, request, method, resource, resources, app, ancestors, config));
        }
        return Collections.unmodifiableList(result);
    }

    private static final List<Representation> fixRepresentationList(final List<Representation> representations,
            final Response response, final Method method, final Resource resource, final Resources resources,
            final Application app, final Stack<Resource> ancestors, final WadlFixConfig config) {
        final List<Representation> result = new LinkedList<Representation>();
        for (final Representation representation : representations) {
            result.add(fixRepresentation(representation, response, method, resource, resources, app, ancestors, config));
        }
        return Collections.unmodifiableList(result);
    }

    private static final Request fixRequest(final Request request, final Method method, final Resource resource,
            final Resources resources, final Application app, final Stack<Resource> ancestors,
            final WadlFixConfig config) {
        if (request == null) {
            return null;
        }
        final List<Representation> representationList = fixRepresentationList(request.getRepresentations(), request,
                method, resource, resources, app, ancestors, config);
        return new Request(request.getDocumentation(), request.getParams(), representationList);
    }

    private static final Resource fixResource(final Resource resource, final Resources resources,
            final Application app, final Stack<Resource> ancestors, final WadlFixConfig config) {
        final List<Method> methods = fixMethodList(resource.getMethods(), resource, resources, app, ancestors, config);

        ancestors.push(resource);
        try {
            final List<Resource> childResourceList = fixResourceList(resource.getResources(), resources, app,
                    ancestors, config);
            return new Resource(resource.getId(), resource.getType(), resource.getQueryType(), resource.getPath(),
                    resource.getDocumentation(), resource.getParams(), methods, childResourceList);
        } finally {
            ancestors.pop();
        }
    }

    private static final List<Resource> fixResourceList(final List<Resource> resourceList, final Resources resources,
            final Application app, final Stack<Resource> ancestors, final WadlFixConfig config) {
        final List<Resource> result = new LinkedList<Resource>();
        for (final Resource resource : resourceList) {
            result.add(fixResource(resource, resources, app, ancestors, config));
        }
        return Collections.unmodifiableList(result);
    }

    private static final Resources fixResources(final Resources resources, final Application app,
            final WadlFixConfig config) {
        // Keep track of the ancestor resources.
        final Stack<Resource> ancestors = new Stack<Resource>();
        final List<Resource> resourceList = fixResourceList(resources.getResources(), resources, app, ancestors, config);
        return new Resources(resources.getBase(), resources.getDocumentation(), resourceList);
    }

    private static final Response fixResponse(final Response response, final Method method, final Resource resource,
            final Resources resources, final Application app, final Stack<Resource> ancestors,
            final WadlFixConfig config) {
        final List<Representation> representationList = fixRepresentationList(response.getRepresentations(), response,
                method, resource, resources, app, ancestors, config);
        return new Response(response.getStatusCodes(), response.getDocumentation(), response.getParams(),
                representationList);
    }

    private static final List<Response> fixResponseList(final List<Response> responses, final Method method,
            final Resource resource, final Resources resources, final Application app, final Stack<Resource> ancestors,
            final WadlFixConfig config) {
        final List<Response> result = new LinkedList<Response>();
        for (final Response response : responses) {
            result.add(fixResponse(response, method, resource, resources, app, ancestors, config));
        }
        return Collections.unmodifiableList(result);
    }

    private static final QName getRepresentationElement(final Representation representation, final Request request,
            final Method method, final Resource resource, final Stack<Resource> ancestors, final WadlFixConfig config) {
        if (representation.getElement() != null) {
            // If the representation already has an element defined then use it.
            return representation.getElement();
        } else {
            final String name = method.getName();
            if (name.equals("DELETE") || name.equals("GET")) {
                return null;
            } else {
                // Otherwise, the element is the rightmost part of the resource (excluding template
                // parameters).
                final List<String> steps = reverse(toSteps(resource, ancestors));
                for (final String step : steps) {
                    if (isTemplateParam(step)) {
                        // Ignore, skip over it to get the type.
                    } else {
                        return new QName(config.getNamespaceURI(), step, config.getPrefix());
                    }
                }
            }
        }
        throw new AssertionError();
    }

    private static final QName getRepresentationElement(final Representation representation, final Response response,
            final Method method, final Resource resource, final Stack<Resource> ancestors, final WadlFixConfig config) {
        if (representation.getElement() != null) {
            // If the representation already has an element defined then use it.
            return representation.getElement();
        } else {
            final String name = method.getName();
            if (name.equals("DELETE") || name.equals("PUT")) {
                return null;
            } else {
                // Otherwise, the element is the rightmost part of the resource (excluding template
                // parameters).
                final List<String> steps = reverse(toSteps(resource, ancestors));
                for (final String step : steps) {
                    if (isTemplateParam(step)) {
                        // Ignore, skip over it to get the type.
                    } else {
                        return new QName(config.getNamespaceURI(), step, config.getPrefix());
                    }
                }
            }
        }
        throw new AssertionError();
    }

    private static final boolean isTemplateParam(final String step) {
        return step.startsWith("{") && step.endsWith("}");
    }

    public static void main(final String[] args) {
        final WadlFixConfig config = new WadlFixConfig("", "http://www.slcedu.org/api/v1");
        final Map<String, String> prefixMappings = new HashMap<String, String>();
        if (config.getNamespaceURI().trim().length() > 0) {
            prefixMappings.put(config.getPrefix(), config.getNamespaceURI());
        }
        try {
            @SuppressWarnings("unused")
            final ModelIndex model = new DefaultModelIndex(XmiReader.readModel("SLI.xmi"));
            final Application app = fixApplication(WadlReader.readApplication("wadl-original.xml"), config);
            WadlWriter.writeDocument(app, prefixMappings, "SLI.wadl");
        } catch (final FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    private static final List<String> reverse(final List<String> strings) {
        final LinkedList<String> result = new LinkedList<String>();
        for (final String s : strings) {
            result.addFirst(s);
        }
        return Collections.unmodifiableList(result);
    }

    private static final List<String> splitBasedOnFwdSlash(final String path) {
        final List<String> result = new LinkedList<String>();
        for (final String s : path.split("/")) {
            result.add(s);
        }
        return result;
    }

    private static final List<String> toSteps(final Resource resource, final Stack<Resource> ancestors) {
        final List<String> result = new LinkedList<String>();
        for (final Resource ancestor : ancestors) {
            result.addAll(splitBasedOnFwdSlash(ancestor.getPath()));
        }
        result.addAll(splitBasedOnFwdSlash(resource.getPath()));
        return result;
    }

}
