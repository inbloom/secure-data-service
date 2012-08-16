package org.slc.sli.api.resources.generic;

import org.slc.sli.api.resources.generic.util.ResourceHelper;
import org.slc.sli.api.resources.generic.util.ResourceMethod;
import org.slc.sli.api.resources.generic.util.ResourceTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import java.util.Map;
import java.util.Set;

/**
 * Base resource class for all dynamic end points
 *
 * @author srupasinghe
 */
public abstract class GenericResource {

    @Autowired
    private ResourceHelper resourceHelper;

    @javax.annotation.Resource(name = "resourceSupportedMethods")
    private Map<String, Set<String>> resourceSupportedMethods;

    protected static interface ResourceLogic {
        public Response run(String resourceName);
    }

    protected Response handle(final UriInfo uriInfo, ResourceTemplate template, ResourceMethod method,
                            final ResourceLogic logic) {

        final String resourceName = getResourceName(uriInfo, ResourceTemplate.ONE_PART_FULL);

        Set<String> values = resourceSupportedMethods.get(resourceName);
        if (!values.contains(method.getMethod())) {
            throw new UnsupportedOperationException("Not supported");
        }

        return logic.run(resourceName);
    }

    protected String getResourceName(UriInfo uriInfo, ResourceTemplate template) {
        return resourceHelper.grabResource(uriInfo.getRequestUri().toString(), template);
    }
}
