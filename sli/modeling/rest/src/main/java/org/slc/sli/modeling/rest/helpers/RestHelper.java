package org.slc.sli.modeling.rest.helpers;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.slc.sli.modeling.rest.Param;
import org.slc.sli.modeling.rest.ParamStyle;
import org.slc.sli.modeling.rest.Resource;

public final class RestHelper {
    public static final List<Param> computeRequestTemplateParams(final Resource resource,
            final Stack<Resource> ancestors) {
        final List<Param> params = new LinkedList<Param>();

        final List<Resource> rightToLeftResources = new LinkedList<Resource>();
        rightToLeftResources.addAll(reverse(ancestors));
        rightToLeftResources.add(resource);

        for (final Resource rightToLeftResource : rightToLeftResources) {
            for (final Param param : rightToLeftResource.getParams()) {
                if (param.getStyle() == ParamStyle.TEMPLATE) {
                    params.add(param);
                }
            }
        }
        return Collections.unmodifiableList(params);
    }

    public static final <T> List<T> reverse(final List<T> strings) {
        final LinkedList<T> result = new LinkedList<T>();
        for (final T s : strings) {
            result.addFirst(s);
        }
        return Collections.unmodifiableList(result);
    }
}
