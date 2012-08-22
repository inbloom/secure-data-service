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
package org.slc.sli.api.resources.generic.config;

import com.sun.jersey.api.core.DefaultResourceConfig;
import org.slc.sli.modeling.rest.Application;
import org.slc.sli.modeling.rest.Method;
import org.slc.sli.modeling.rest.Resource;
import org.slc.sli.modeling.wadl.helpers.WadlWalker;
import org.slc.sli.modeling.wadl.reader.WadlReader;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Custom resource config for registering resources at runtime
 *
 * @author srupasinghe
 */

@Component
public class ResourceRegisterConfig extends DefaultResourceConfig {

    private static final String WADL = "/wadl/base_wadl.wadl";

    @javax.annotation.Resource(name = "resourceSupportedMethods")
    private Map<String, Set<String>> resourceSupprtedMethods;

    public ResourceRegisterConfig() {
        super();
    }

    @PostConstruct
    public void init() {
        Map<String, Resource> resources = getResources();

        addResources(resources);
    }

    protected void addResources(Map<String, Resource> resources) {
        try {
            for (Map.Entry<String, Resource> resource : resources.entrySet()) {
                Resource resourceTemplate = resource.getValue();

                List<Method> methods = resourceTemplate.getMethods();
                Set<String> methodList = new HashSet<String>();
                for (Method method : methods) {
                    methodList.add(method.getVerb());
                }
                resourceSupprtedMethods.put(resource.getKey(), methodList);

                if (resourceTemplate.getResourceClass() != null && !resourceTemplate.getResourceClass().isEmpty()) {
                    Class resourceClass = Class.forName(resourceTemplate.getResourceClass());

                    getExplicitRootResources().put(resource.getKey(), resourceClass);
                }
            }

        } catch (ClassNotFoundException e) {
            error("Class load error", e);
        }
    }

    protected Map<String, Resource> getResources() {
        ResourceWadlHandler handler = new ResourceWadlHandler();
        Application app = WadlReader.readApplication(getClass().getResourceAsStream(WADL));
        WadlWalker walker = new WadlWalker(handler);

        walker.walk(app);

        return handler.getResourceEnds();
    }
}
