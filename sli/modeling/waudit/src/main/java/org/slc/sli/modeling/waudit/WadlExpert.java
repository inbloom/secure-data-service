package org.slc.sli.modeling.waudit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.namespace.QName;

import org.slc.sli.modeling.psm.PsmDocument;
import org.slc.sli.modeling.rest.Method;
import org.slc.sli.modeling.rest.Representation;
import org.slc.sli.modeling.rest.Request;
import org.slc.sli.modeling.rest.Resource;
import org.slc.sli.modeling.rest.Response;
import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.HasName;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.index.ModelIndex;

/**
 * Provides analysis of the WADL.
 */
public final class WadlExpert {

    /**
     * Compute the element name for the request by walking the URI in the UML graph until it
     * terminates on a type. Then return the schema element for that type. This lookup is defined
     * externally to the UML graph.
     *
     * Note: A naive way to think about this is that we are returning the rightmost step in the URI.
     * However, this isn't precise because we are really walking the arcs in the UML graph and the
     * element names do not coincide exactly with the rightmost step.
     */
    public static final QName computeElementNameForRequest(final Representation representation, final Request request,
            final Method method, final Resource resource, final Stack<Resource> ancestors,
            final WadlAuditConfig config, final Map<String, PsmDocument<Type>> topLevel) {
        final String verb = method.getVerb();
        if (verb.equals(Method.NAME_HTTP_GET) || verb.equals(Method.NAME_HTTP_DELETE)) {
            // There is no request.
            return null;
        } else {
            final List<String> forwardSteps = toSteps(resource, ancestors);
            final QName typeName = computeType(forwardSteps, config, topLevel);
            final Map<String, QName> elementNames = config.getElementNameMap();
            if (elementNames.containsKey(typeName.getLocalPart())) {
                return elementNames.get(typeName.getLocalPart());
            } else {
                throw new RuntimeException("" + typeName);
            }
        }
    }

    public static final QName computeElementNameForResponse(final Representation representation,
            final Response response, final Method method, final Resource resource, final Stack<Resource> ancestors,
            final WadlAuditConfig config, final Map<String, PsmDocument<Type>> topLevel) {
        final String verb = method.getVerb();
        if (verb.equals(Method.NAME_HTTP_POST) || verb.equals(Method.NAME_HTTP_PUT)
                || verb.equals(Method.NAME_HTTP_PATCH) || verb.equals(Method.NAME_HTTP_DELETE)) {
            // There is no response.
            return null;
        } else {
            final List<String> forwardSteps = toSteps(resource, ancestors);
            final QName typeName = computeType(forwardSteps, config, topLevel);
            final Map<String, QName> elementNames = config.getElementNameMap();
            if (elementNames.containsKey(typeName.getLocalPart())) {
                final QName elementName = elementNames.get(typeName.getLocalPart());
                if (elementName != null) {
                    if (elementName.getLocalPart().equals("custom")) {
                        return elementName;
                    } else {
                        return new QName(elementName.getNamespaceURI(), elementName.getLocalPart().concat("List"));
                    }
                } else {
                    return null;
                }
            } else {
                throw new RuntimeException("" + typeName);
            }
        }
    }

    public static final QName computeType(final List<String> steps, final WadlAuditConfig config,
            final Map<String, PsmDocument<Type>> topLevel) {
        final ModelIndex model = config.getModel();
        final Stack<Type> types = new Stack<Type>();
        int stepIndex = 0;
        for (final String step : steps) {
            stepIndex = stepIndex + 1;
            if (stepIndex == 1) {
                if ("v1".equals(step)) {
                    // Expected
                } else {
                    System.err.println("1st step is not the version specifier.");
                }
            } else if (stepIndex == 2) {
                if ("home".equals(step)) {
                    return new QName(config.getNamespaceURI(), "Home", config.getPrefix());
                } else if (topLevel.containsKey(step)) {
                    final PsmDocument<Type> document = topLevel.get(step);
                    final Type type = document.getType();
                    types.push(type);
                } else {
                    System.err.println(step + " is not a valid top-level step.");
                }
            } else {
                final Type type = types.peek();
                if (!isTemplateParam(step)) {
                    final List<AssociationEnd> ends = model.getAssociationEnds(type.getId());
                    if ("custom".equals(step)) {
                        // Short-circuit because we don't have a UML type for custom.
                        return new QName(config.getNamespaceURI(), "Custom", config.getPrefix());
                    } else if ("createCheck".equals(step)) {
                        return new QName(config.getNamespaceURI(), "Unknown", config.getPrefix());
                    } else if ("createWaitingListUser".equals(step)) {
                        return new QName(config.getNamespaceURI(), "Unknown", config.getPrefix());
                    } else if ("studentWithGrade".equals(step)) {
                        return new QName(config.getNamespaceURI(), "Unknown", config.getPrefix());
                    } else {
                        boolean found = false;
                        for (final AssociationEnd end : ends) {
                            if (end.getName().equals(step)) {
                                final Identifier endTypeId = end.getType();
                                final Type endType = model.getType(endTypeId);
                                types.push(endType);
                                found = true;
                            } else {
                                // Try the next association end.
                            }
                        }
                        if (found) {
                            // Keep on going.
                        } else {
                            System.err.println("---------------------------------------");
                            System.err.println("step      : \"" + step + "\"");
                            System.err.println("type      : \"" + type.getName() + "\"");
                            System.err.println(step + " is not a valid association end name in steps " + steps
                                    + " for type " + type.getName());
                            System.err.println("ends  : " + getNames(ends));
                            System.err.println("types : " + getNames(types));
                        }
                    }
                } else {
                }
            }
        }
        final Type type = types.peek();
        return new QName(config.getNamespaceURI(), type.getName(), config.getPrefix());
    }

    private static final List<String> getNames(final List<? extends HasName> namedElements) {
        if (namedElements == null) {
            throw new NullPointerException("namedElements");
        }
        final List<String> names = new ArrayList<String>(namedElements.size());
        for (final HasName namedElement : namedElements) {
            final String name = namedElement.getName();
            names.add(name);
        }
        return Collections.unmodifiableList(names);

    }

    private static final boolean isTemplateParam(final String step) {
        return step.startsWith("{") && step.endsWith("}");
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

    private WadlExpert() {
        throw new RuntimeException();
    }

}
