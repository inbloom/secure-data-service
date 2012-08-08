package org.slc.sli.api.resources.config;

import com.sun.jersey.api.core.DefaultResourceConfig;
import org.apache.commons.lang3.tuple.Pair;
import org.slc.sli.api.resources.generic.DefaultResource;
import org.slc.sli.api.resources.generic.GenericResource;
import org.slc.sli.api.resources.generic.RestSuite;
import org.slc.sli.modeling.rest.Application;
import org.slc.sli.modeling.rest.Method;
import org.slc.sli.modeling.rest.Resource;
import org.slc.sli.modeling.wadl.helpers.WadlWalker;
import org.slc.sli.modeling.wadl.reader.WadlReader;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.MediaType;
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

    private static final String WADL = "/wadl/base_wadl2.wadl";
    private static final String PREFIX = "generic/";
    private static final Integer ONE_PART = 1;

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
            Class resourceClass = GenericResource.class;

            for (Map.Entry<String, Resource> resource : resources.entrySet()) {
                Resource resourceTemplate = resource.getValue();

                List<Method> methods = resourceTemplate.getMethods();
                Set<String> methodList = new HashSet<String>();
                for (Method method : methods) {
                    methodList.add(method.getVerb());
                }
                resourceSupprtedMethods.put(resource.getKey(), methodList);

                if (resourceTemplate.getResourceClass() != null && !resourceTemplate.getResourceClass().isEmpty()) {
                    resourceClass = Class.forName(resourceTemplate.getResourceClass());
                }

                getExplicitRootResources().put(PREFIX + resource.getKey(), resourceClass);
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {

        }
    }

    protected void addOnePartResources(List<Pair<String, String>> resources) {
        for (Pair<String, String> resourceTemplate : resources) {
            try {
                Class resourceClass = GenericResource.class;
                if (resourceTemplate.getRight() != null && !resourceTemplate.getRight().isEmpty()) {
                    resourceClass = Class.forName(resourceTemplate.getRight());
                }
                getExplicitRootResources().put(PREFIX + resourceTemplate.getLeft(), DefaultResource.class);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        getExplicitRootResources().put(PREFIX + "schools/{id}", DefaultResource.class);
    }

    protected Map<String, Resource> getResources() {
        ResourceWadlHandler handler = new ResourceWadlHandler();
        Application app = WadlReader.readApplication(getClass().getResourceAsStream(WADL));
        WadlWalker walker = new WadlWalker(handler);

        walker.walk(app);

        return handler.getResourceEnds();
    }
}
