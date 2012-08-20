package org.slc.sli.api.resources.generic;

import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.representation.EntityResponse;
import org.slc.sli.api.resources.generic.representation.HateoasLink;
import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.resources.generic.service.ResourceAccessLog;
import org.slc.sli.api.resources.generic.service.ResourceService;
import org.slc.sli.api.resources.generic.util.ResourceHelper;
import org.slc.sli.api.resources.generic.util.ResourceMethod;
import org.slc.sli.api.resources.generic.util.ResourceTemplate;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.api.resources.v1.view.View;
import org.slc.sli.api.service.query.ApiQuery;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import java.util.List;
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

    @Autowired
    private HateoasLink hateoasLink;

    @Autowired
    private ResourceAccessLog resourceAccessLog;

    @javax.annotation.Resource(name = "resourceSupportedMethods")
    private Map<String, Set<String>> resourceSupportedMethods;

    protected static interface ResourceLogic {
        public Response run(Resource resource);
    }

    protected static interface GetResourceLogic {
        public List<EntityBody> run(Resource resource);
    }

    protected Resource constructAndCheckResource(final UriInfo uriInfo, final ResourceTemplate template,
                                                 final ResourceMethod method) {
        final String resourcePath = resourceHelper.getResourcePath(uriInfo, template);
        Resource resource = new Resource(resourcePath);

        Set<String> values = resourceSupportedMethods.get(resourcePath);
        if (!values.contains(method.getMethod())) {
            throw new MethodNotAllowedException(values);
        }

        //log security events
        resourceAccessLog.logAccessToRestrictedEntity(uriInfo, resource, GenericResource.class.toString());

        return resource;
    }

    protected Response handleGet(final UriInfo uriInfo, final ResourceTemplate template, final ResourceMethod method,
                              final GetResourceLogic logic) {

        //get the resource container
        Resource resource = constructAndCheckResource(uriInfo, template, method);

        //run the resource logic
        List<EntityBody> entities = logic.run(resource);

        //add the links
        entities = hateoasLink.add(resource.getResourceType(), entities, uriInfo);

        //get the page count
        long pagingHeaderTotalCount = resourceService.getEntityCount(resource, uriInfo.getRequestUri(), uriInfo.getQueryParameters());

        //add the paging headers and return the data
        return addPagingHeaders(Response.ok(new EntityResponse(resourceService.getEntityType(resource), entities)),
                pagingHeaderTotalCount, uriInfo).build();

    }

    protected Response handle(final UriInfo uriInfo, final ResourceTemplate template, final ResourceMethod method,
                            final ResourceLogic logic) {

        //get the resource container
        Resource resource = constructAndCheckResource(uriInfo, template, method);

        return logic.run(resource);
    }

    protected Response.ResponseBuilder addPagingHeaders(Response.ResponseBuilder resp, long total, UriInfo info) {
        if (info != null && resp != null) {
            NeutralQuery neutralQuery = new ApiQuery(info);
            int offset = neutralQuery.getOffset();
            int limit = neutralQuery.getLimit();

            int nextStart = offset + limit;
            if (nextStart < total) {
                neutralQuery.setOffset(nextStart);

                String nextLink = info.getRequestUriBuilder().replaceQuery(neutralQuery.toString()).build().toString();
                resp.header(ParameterConstants.HEADER_LINK, "<" + nextLink + ">; rel=next");
            }

            if (offset > 0) {
                int prevStart = Math.max(offset - limit, 0);
                neutralQuery.setOffset(prevStart);

                String prevLink = info.getRequestUriBuilder().replaceQuery(neutralQuery.toString()).build().toString();
                resp.header(ParameterConstants.HEADER_LINK, "<" + prevLink + ">; rel=prev");
            }

            resp.header(ParameterConstants.HEADER_TOTAL_COUNT, total);
        }

        return resp;
    }


}
