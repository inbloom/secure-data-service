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

package org.slc.sli.modeling.rest.helpers;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.slc.sli.modeling.rest.Param;
import org.slc.sli.modeling.rest.ParamStyle;
import org.slc.sli.modeling.rest.Resource;

/**
 * @author dholmes
 */
public class RestHelper {
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
