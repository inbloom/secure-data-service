package org.slc.sli.api.resources.generic;

import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.representation.EntityResponse;
import org.slc.sli.api.resources.generic.representation.HateoasLink;
import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.resources.generic.service.ResourceService;
import org.slc.sli.api.resources.generic.util.ResourceHelper;
import org.slc.sli.api.resources.generic.util.ResourceMethod;
import org.slc.sli.api.resources.generic.util.ResourceTemplate;
import org.slc.sli.api.service.query.ApiQuery;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;

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
public abstract class GenericResource {

    @Autowired
    protected ResourceService resourceService;

    @Autowired
    protected ResourceHelper resourceHelper;

    @Autowired
    private HateoasLink hateoasLink;

    @javax.annotation.Resource(name = "resourceSupportedMethods")
    private Map<String, Set<String>> resourceSupportedMethods;

    protected static interface ResourceLogic {
        public Response run(Resource resource);
    }

    protected static interface GetResourceLogic {
        public List<EntityBody> run(Resource resource);
    }

    protected Response handleGet(final UriInfo uriInfo, final ResourceTemplate template, final ResourceMethod method,
                              final GetResourceLogic logic) {

        final String resourcePath = resourceHelper.getResourcePath(uriInfo, template);
        Resource resource = new Resource(resourcePath);

        Set<String> values = resourceSupportedMethods.get(resourcePath);
        if (!values.contains(method.getMethod())) {
            throw new UnsupportedOperationException("Not supported");
        }

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

        final String resourcePath = resourceHelper.getResourcePath(uriInfo, template);
        Resource resource = new Resource(resourcePath);

        Set<String> values = resourceSupportedMethods.get(resourcePath);
        if (!values.contains(method.getMethod())) {
            throw new UnsupportedOperationException("Not supported");
        }

        final String resourceName = resourceHelper.getResourceName(uriInfo, template);
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
