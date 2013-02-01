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

import java.util.Stack;

import org.slc.sli.modeling.rest.Application;
import org.slc.sli.modeling.rest.Method;
import org.slc.sli.modeling.rest.Resource;
import org.slc.sli.modeling.rest.Resources;

/**
 * Interface for WADL handler
 * 
 * @author dholmes
 *
 */
public interface WadlHandler {

    void beginApplication(Application application);

    void endApplication(Application application);

    void beginResource(Resource resource, Resources resources, Application app, Stack<Resource> ancestors);

    void endResource(Resource resource, Resources resources, Application app, Stack<Resource> ancestors);

    void method(Method method, Resource resource, Resources resources, Application app, Stack<Resource> ancestors);
}
