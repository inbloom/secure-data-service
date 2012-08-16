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
    protected ResourceHelper resourceHelper;

    @javax.annotation.Resource(name = "resourceSupportedMethods")
    private Map<String, Set<String>> resourceSupportedMethods;

    protected static interface ResourceLogic {
        public Response run(String resourceName);
    }

    protected Response handle(final UriInfo uriInfo, final ResourceTemplate template, final ResourceMethod method,
                            final ResourceLogic logic) {

        final String resourceName = resourceHelper.getResourceName(uriInfo, template);

        Set<String> values = resourceSupportedMethods.get(resourceName);
        if (!values.contains(method.getMethod())) {
            throw new UnsupportedOperationException("Not supported");
        }

        return logic.run(resourceName);
    }
}
