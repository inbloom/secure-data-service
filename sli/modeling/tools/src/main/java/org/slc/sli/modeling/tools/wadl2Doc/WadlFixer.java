package org.slc.sli.modeling.tools.wadl2Doc;

import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import javax.xml.namespace.QName;

import org.slc.sli.modeling.rest.Application;
import org.slc.sli.modeling.rest.Method;
import org.slc.sli.modeling.rest.Representation;
import org.slc.sli.modeling.rest.Request;
import org.slc.sli.modeling.rest.Resource;
import org.slc.sli.modeling.rest.Resources;
import org.slc.sli.modeling.rest.Response;
import org.slc.sli.modeling.wadl.reader.WadlReader;
import org.slc.sli.modeling.wadl.writer.WadlWriter;

public final class WadlFixer {

    private static final String computeId(final Method method, final Resource resource, final Resources resources,
            final Application app, final Stack<Resource> ancestors) {

        final List<String> steps = reverse(toSteps(resource, ancestors));

        final StringBuilder sb = new StringBuilder();
        sb.append(method.getName().toLowerCase());
        boolean first = true;
        boolean seenParam = false;
        String paramName = null;
        for (final String step : steps) {
            if (isTemplateParam(step)) {
                seenParam = true;
                paramName = parseTemplateParam(step);
            } else if (isVersion(step)) {
                // Ignore.
            } else {
                if (first) {
                    first = false;
                } else {
                    sb.append("For");
                }
                sb.append(titleCase(step));
                if (seenParam) {
                    sb.append("By" + titleCase(paramName));
                    seenParam = false;
                }
            }
        }
        return sb.toString();
    }

    private static final Application fixApplication(final Application app) {
        final Resources resources = fixResources(app.getResources(), app);
        return new Application(app.getDocumentation(), app.getGrammars(), resources, app.getResourceTypes(),
                app.getMethods(), app.getRepresentations(), app.getFaults());
    }

    private static final Method fixMethod(final Method method, final Resource resource, final Resources resources,
            final Application app, final Stack<Resource> ancestors) {

        final String id = computeId(method, resource, resources, app, ancestors);
        final Request request = fixRequest(method.getRequest(), method, resource, resources, app, ancestors);
        final List<Response> responseList = fixResponseList(method.getResponses(), method, resource, resources, app,
                ancestors);
        return new Method(id, method.getName(), method.getDocumentation(), request, responseList);
    }

    private static final List<Method> fixMethodList(final List<Method> methods, final Resource resource,
            final Resources resources, final Application app, final Stack<Resource> ancestors) {
        final List<Method> result = new LinkedList<Method>();
        for (final Method method : methods) {
            result.add(fixMethod(method, resource, resources, app, ancestors));
        }
        return Collections.unmodifiableList(result);
    }

    private static final Representation fixRepresentation(final Representation representation, final Request request,
            final Method method, final Resource resource, final Resources resources, final Application app,
            final Stack<Resource> ancestors) {

        final QName element = getRepresentationElement(representation, request, method, resource, ancestors);
        return new Representation(representation.getId(), element, representation.getMediaType(),
                representation.getProfiles(), representation.getDocumentation(), representation.getParams());
    }

    private static final Representation fixRepresentation(final Representation representation, final Response response,
            final Method method, final Resource resource, final Resources resources, final Application app,
            final Stack<Resource> ancestors) {

        final QName element = getRepresentationElement(representation, response, method, resource, ancestors);
        return new Representation(representation.getId(), element, representation.getMediaType(),
                representation.getProfiles(), representation.getDocumentation(), representation.getParams());
    }

    private static final List<Representation> fixRepresentationList(final List<Representation> representations,
            final Request request, final Method method, final Resource resource, final Resources resources,
            final Application app, final Stack<Resource> ancestors) {
        final List<Representation> result = new LinkedList<Representation>();
        for (final Representation representation : representations) {
            result.add(fixRepresentation(representation, request, method, resource, resources, app, ancestors));
        }
        return Collections.unmodifiableList(result);
    }

    private static final List<Representation> fixRepresentationList(final List<Representation> representations,
            final Response response, final Method method, final Resource resource, final Resources resources,
            final Application app, final Stack<Resource> ancestors) {
        final List<Representation> result = new LinkedList<Representation>();
        for (final Representation representation : representations) {
            result.add(fixRepresentation(representation, response, method, resource, resources, app, ancestors));
        }
        return Collections.unmodifiableList(result);
    }

    private static final Request fixRequest(final Request request, final Method method, final Resource resource,
            final Resources resources, final Application app, final Stack<Resource> ancestors) {
        if (request == null) {
            return null;
        }
        final List<Representation> representationList = fixRepresentationList(request.getRepresentations(), request,
                method, resource, resources, app, ancestors);
        return new Request(request.getDocumentation(), request.getParams(), representationList);
    }

    private static final Resource fixResource(final Resource resource, final Resources resources,
            final Application app, final Stack<Resource> ancestors) {
        final List<Method> methods = fixMethodList(resource.getMethods(), resource, resources, app, ancestors);

        ancestors.push(resource);
        try {
            final List<Resource> childResourceList = fixResourceList(resource.getResources(), resources, app, ancestors);
            return new Resource(resource.getId(), resource.getType(), resource.getQueryType(), resource.getPath(),
                    resource.getDocumentation(), resource.getParams(), methods, childResourceList);
        } finally {
            ancestors.pop();
        }
    }

    private static final List<Resource> fixResourceList(final List<Resource> resourceList, final Resources resources,
            final Application app, final Stack<Resource> ancestors) {
        final List<Resource> result = new LinkedList<Resource>();
        for (final Resource resource : resourceList) {
            result.add(fixResource(resource, resources, app, ancestors));
        }
        return Collections.unmodifiableList(result);
    }

    private static final Resources fixResources(final Resources resources, final Application app) {
        // Keep track of the ancestor resources.
        final Stack<Resource> ancestors = new Stack<Resource>();
        final List<Resource> resourceList = fixResourceList(resources.getResources(), resources, app, ancestors);
        return new Resources(resources.getBase(), resources.getDocumentation(), resourceList);
    }

    private static final Response fixResponse(final Response response, final Method method, final Resource resource,
            final Resources resources, final Application app, final Stack<Resource> ancestors) {
        final List<Representation> representationList = fixRepresentationList(response.getRepresentations(), response,
                method, resource, resources, app, ancestors);
        return new Response(response.getStatusCodes(), response.getDocumentation(), response.getParams(),
                representationList);
    }

    private static final List<Response> fixResponseList(final List<Response> responses, final Method method,
            final Resource resource, final Resources resources, final Application app, final Stack<Resource> ancestors) {
        final List<Response> result = new LinkedList<Response>();
        for (final Response response : responses) {
            result.add(fixResponse(response, method, resource, resources, app, ancestors));
        }
        return Collections.unmodifiableList(result);
    }

    private static final QName getRepresentationElement(final Representation representation, final Request request,
            final Method method, final Resource resource, final Stack<Resource> ancestors) {
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
                        return new QName(step);
                    }
                }
            }
        }
        throw new AssertionError();
    }

    private static final QName getRepresentationElement(final Representation representation, final Response response,
            final Method method, final Resource resource, final Stack<Resource> ancestors) {
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
                        return new QName(step);
                    }
                }
            }
        }
        throw new AssertionError();
    }

    private static final boolean isTemplateParam(final String step) {
        return step.startsWith("{") && step.endsWith("}");
    }

    private static final boolean isVersion(final String step) {
        return step.toLowerCase().equals("v1");
    }

    public static void main(final String[] args) {

        try {
            final Application app = fixApplication(WadlReader.readApplication("wadl-original.xml"));
            WadlWriter.writeDocument(app, "wadl-clean.xml");
        } catch (final FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    private static final String parseTemplateParam(final String step) {
        if (isTemplateParam(step)) {
            return step.substring(1, step.length() - 1);
        } else {
            throw new AssertionError(step);
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

    private static final String titleCase(final String text) {
        return text.substring(0, 1).toUpperCase().concat(text.substring(1));
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
