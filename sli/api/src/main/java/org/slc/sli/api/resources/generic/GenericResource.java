package org.slc.sli.api.resources.generic;

import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.resources.generic.representation.ServiceResponse;
import org.slc.sli.api.resources.generic.response.DefaultResponseBuilder;
import org.slc.sli.api.resources.generic.service.ResourceService;
import org.slc.sli.api.resources.generic.response.GetAllResponseBuilder;
import org.slc.sli.api.resources.generic.response.GetResponseBuilder;
import org.slc.sli.api.resources.generic.util.ResourceHelper;
import org.slc.sli.api.resources.generic.util.ResourceMethod;
import org.slc.sli.api.resources.generic.util.ResourceTemplate;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import java.util.Map;
import java.util.Set;

/**
 * Base resource class for all dynamic end points
 *
 * @author srupasinghe
 */

@Scope("request")
@Consumes({ MediaType.APPLICATION_JSON + ";charset=utf-8", HypermediaType.VENDOR_SLC_JSON + ";charset=utf-8",
        MediaType.APPLICATION_XML + ";charset=utf-8", MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON,
        MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON + ";charset=utf-8", HypermediaType.VENDOR_SLC_JSON + ";charset=utf-8",
        MediaType.APPLICATION_XML + ";charset=utf-8", HypermediaType.VENDOR_SLC_XML + ";charset=utf-8" })
public abstract class GenericResource {

    @Autowired
    protected ResourceService resourceService;

    @Autowired
    protected ResourceHelper resourceHelper;

    @javax.annotation.Resource(name = "resourceSupportedMethods")
    private Map<String, Set<String>> resourceSupportedMethods;

    @Autowired
    protected GetResponseBuilder getResponseBuilder;

    @Autowired
    protected GetAllResponseBuilder getAllResponseBuilder;

    @Autowired
    protected DefaultResponseBuilder defaultResponseBuilder;

    public static interface ResourceLogic {
        public Response run(Resource resource);
    }

    public static interface GetResourceLogic {
        public ServiceResponse run(Resource resource);
    }

    protected Response handleGet(final UriInfo uriInfo, final ResourceTemplate template, final ResourceMethod method,
                                 final GetResourceLogic logic) {

        return getResponseBuilder.build(uriInfo, template, method, logic);
    }

    protected Response handleGetAll(final UriInfo uriInfo, final ResourceTemplate template, final ResourceMethod method,
                                    final GetResourceLogic logic) {

        return getAllResponseBuilder.build(uriInfo, template, method, logic);

    }

    protected Response handle(final UriInfo uriInfo, final ResourceTemplate template, final ResourceMethod method,
                            final ResourceLogic logic) {

        return defaultResponseBuilder.build(uriInfo, template, method, logic);
    }




}
