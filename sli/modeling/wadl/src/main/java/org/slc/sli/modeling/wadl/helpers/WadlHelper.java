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

package org.slc.sli.modeling.wadl.helpers;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.slc.sli.modeling.rest.Application;
import org.slc.sli.modeling.rest.Method;
import org.slc.sli.modeling.rest.Param;
import org.slc.sli.modeling.rest.Request;
import org.slc.sli.modeling.rest.Resource;
import org.slc.sli.modeling.rest.Resources;

/**
 * Wadl Helper methods.
 * 
 * @author dholmes
 *
 */
public final class WadlHelper {

    private WadlHelper() {
        // Prevent normal initialization.
    }

    /**
     * Returns a list of request parameters for the specified method.
     * <p>
     * This method is safe in the case that the method has no request child.
     * </p>
     */
    public static List<Param> getRequestParams(final Method method) {
        if (method == null) {
            throw new IllegalArgumentException();
        }
        final Request request = method.getRequest();
        if (request != null) {
            return request.getParams();
        } else {
            return Collections.emptyList();
        }
    }

    public static final String computeId(final Method method, final Resource resource, final Resources resources,
            final Application app, final Stack<Resource> ancestors) {

        final List<String> steps = reverse(toSteps(resource, ancestors));

        final StringBuilder sb = new StringBuilder();
        sb.append(method.getVerb().toLowerCase());
        boolean first = true;
        boolean seenParam = false;
        String paramName = null;
        for (final String step : steps) {
            if (isTemplateParam(step)) {
                seenParam = true;
                paramName = parseTemplateParam(step);
            } else if (!isVersion(step)) {
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

    public static final String titleCase(final String text) {
        return text.substring(0, 1).toUpperCase().concat(text.substring(1));
    }

    public static final <T> List<T> reverse(final List<T> strings) {
        final LinkedList<T> result = new LinkedList<T>();
        for (final T s : strings) {
            result.addFirst(s);
        }
        return Collections.unmodifiableList(result);
    }

    public static final boolean isVersion(final String step) {
        return step.toLowerCase().equals("v1");
    }

    public static final String parseTemplateParam(final String step) {
        if (isTemplateParam(step)) {
            return step.substring(1, step.length() - 1);
        } else {
            throw new AssertionError(step);
        }
    }

    public static final List<String> toSteps(final Resource resource, final Stack<Resource> ancestors) {
        final List<String> result = new LinkedList<String>();
        for (final Resource ancestor : ancestors) {
            result.addAll(splitBasedOnFwdSlash(ancestor.getPath()));
        }
        result.addAll(splitBasedOnFwdSlash(resource.getPath()));
        return Collections.unmodifiableList(result);
    }

    private static final List<String> splitBasedOnFwdSlash(final String path) {
        final List<String> result = new LinkedList<String>();
        for (final String s : path.split("/")) {
            result.add(s);
        }
        return result;
    }

    public static final boolean isTemplateParam(final String step) {
        return step.trim().startsWith("{") && step.endsWith("}");
    }
}
